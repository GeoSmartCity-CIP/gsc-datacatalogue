package it.sinergis.datacatalogue.services;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;

import it.sinergis.datacatalogue.bean.jpa.Gsc006DatasourceEntity;
import it.sinergis.datacatalogue.bean.jpa.Gsc007DatasetEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.PersistenceServiceProvider;
import it.sinergis.datacatalogue.persistence.services.Gsc006DatasourcePersistence;
import it.sinergis.datacatalogue.persistence.services.Gsc007DatasetPersistence;
import it.sinergis.datacatalogue.persistence.services.util.ServiceUtil;

/**
 * @author A2LL0148
 *
 */
/**
 * @author A2LL0148
 *
 */
public class UpdateShapeService extends ServiceCommons {

	/** Logger. */
	private static Logger logger;

	/** Gsc006Datasource DAO. */
	private Gsc006DatasourcePersistence gsc006Dao;
	
	/** Gsc007Dataset DAO. */
	private Gsc007DatasetPersistence gsc007Dao;

	public UpdateShapeService() {
		logger = Logger.getLogger(this.getClass());
		gsc007Dao = PersistenceServiceProvider.getService(Gsc007DatasetPersistence.class);
		gsc006Dao = PersistenceServiceProvider.getService(Gsc006DatasourcePersistence.class);
	}
	
	
	/**
	 * Return the list of datasets that have to be checked for updates
	 * 
	 * @return
	 */
	public List<Gsc007DatasetEntity> getUpdatableDatasets() {
		String updatableDatasetsQuery = createGetUpdatableDatasetsQuery();
		return gsc007Dao.getDatasets(updatableDatasetsQuery);
	}
	
	/**
	 * Query that retrieves all datasets that have 
	 * 1) flag 'tobeupdated' = true 
	 * 2) refreshinterval + last update time > current time
	 * 
	 * e.g.
	 * 
	 * SELECT * FROM gscdatacatalogue.gsc_007_dataset dstT
	 * WHERE dstT.json->>'tobeingested' = 'true' AND (
	 *	to_timestamp(dstT.json->>'lastUpdated','dd/MM/yyyy hh24:mi:ss') + 
	 *  (CAST((dstT.json->>'refreshinterval') AS INTEGER) * INTERVAL '1 hour')) 
	 *  < clock_timestamp();
	 * 
	 * @return
	 */
	private String createGetUpdatableDatasetsQuery() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT * FROM ").append(Constants.DATASETS_TABLE_NAME).append(" dstT ");
		sb.append("WHERE dstT.json->>'tobeingested' = 'true' AND (");
		sb.append("to_timestamp(dstT.json->>'lastupdated','dd/MM/yyyy hh24:mi:ss') + ");
		sb.append("(CAST((dstT.json->>'refreshinterval') AS INTEGER) * INTERVAL '1 hour'))");
		sb.append("< clock_timestamp()");
		
		return sb.toString();
	}
	
	
	/**
	 * Update the shapefile.
	 * 
	 * @param dataset
	 * @return
	 * @throws DCException
	 */
	public Gsc007DatasetEntity updateFile(Gsc007DatasetEntity dataset) throws DCException {
		
		try {
			Long datasetId = dataset.getId();
			String lastUpdateTimestamp = getFieldValueFromJsonText(dataset.getJson(),Constants.LAST_UPDATE_TS);
			String sourceURL = getFieldValueFromJsonText(dataset.getJson(),Constants.URL);
			
			//destination url is the file on our local machine. 
			//concat the path on the datasource + the filename on the dataset.
			Gsc006DatasourceEntity datasource = gsc006Dao.load(Long.parseLong(getFieldValueFromJsonText(dataset.getJson(),Constants.DATASOURCE_ID_FIELD)));
			
			String destinationDirectory = getFieldValueFromJsonText(datasource.getJson(),Constants.PATH);
			String fileName = getFieldValueFromJsonText(dataset.getJson(),Constants.DSET_REALNAME_FIELD);
			String fileNameNoExtension = fileName.substring(0, fileName.lastIndexOf("."));
			
			boolean updated  = updateFile(datasetId,sourceURL,fileNameNoExtension,destinationDirectory,lastUpdateTimestamp);
			
			//set a new Last Update timestamp if the file has been updated.
			if(updated) {
				ObjectNode datasetNode = ((ObjectNode) om.readTree(dataset.getJson()));
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				String dateFormatted = formatter.format(System.currentTimeMillis());
				datasetNode.put(Constants.LAST_UPDATE_TS, dateFormatted);
				dataset.setJson(om.writeValueAsString(datasetNode));
				return gsc007Dao.save(dataset);
			}
			
			return null;
		} catch(Exception e) {
			logger.error("error trying to copy file" );
			logger.error(e);
			throw new DCException(Constants.ER21);
		}
		
	}
	
	/**
	 * Update the shapefile.
	 * 
	 * @param datasetId
	 * @param sourceURL
	 * @param destinationFilePath
	 * @param lastUpdateTimestamp
	 * @return
	 * @throws DCException
	 */
	private boolean updateFile(Long datasetId,String sourceURL,String fileNameNoExtension,String destinationDirectory,String lastUpdateTimestamp) throws DCException {

		try {
			
			File file = new File(destinationDirectory + fileNameNoExtension+".zip");
			
			//check if the file has changed since last update
			if(isFileModified(sourceURL,file)) {
				
				//create a temp directory
				String tempDestinationDirectory = destinationDirectory + "temp";
				File tempDir = new File(tempDestinationDirectory);
				if(!tempDir.exists()) {
					tempDir.mkdir();
				}
				
				//if == null it means we're uploading the file while creating the dataset
				if(datasetId != null) {
					logger.info("Found new file for datasetId="+datasetId+"... update in progress.");
				}
				//DOWNLOAD
				FileUtils.copyURLToFile(new URL(sourceURL), file);
				
				//unzip
				ZipInputStream zipIn = new ZipInputStream(new FileInputStream(destinationDirectory + fileNameNoExtension+".zip"));
				getZipFiles(zipIn,tempDestinationDirectory);

				//check if the metadata match
				//if they do change move the files to parent directory
				File shpFile = new File(destinationDirectory + fileNameNoExtension+".shp");
				if(shpFile.exists()) {
					
					String tempFilePath = tempDestinationDirectory +  File.separator + fileNameNoExtension + ".shp";
					String oldFilePath = destinationDirectory + fileNameNoExtension + ".shp";
					
					
					logger.info("Check if metadata from "+ tempFilePath + " file are equals to "+ oldFilePath + " file");
					if(!metadataMatch(tempFilePath,oldFilePath)) {
						logger.error("The retrieved file cannot be updated because its metadata do not match with the ones of the file that needs to be ovverridden");
						throw new DCException(Constants.ER22);
					} else {
						//copy
						FileUtils.copyDirectory(tempDir,new File(destinationDirectory));
					}
				} else {
					//copy
					FileUtils.copyDirectory(tempDir,new File(destinationDirectory));
				}
				
				FileUtils.deleteDirectory(tempDir);
				
				return true;
			}
			
			return false;
			
		} catch(DCException e) {
			logger.error("error trying to copy file" );
			logger.error(e);
			throw new DCException(e.getErrorCode());
		} catch(Exception e) {
			logger.error("error trying to copy file" );
			logger.error(e);
			throw new DCException(Constants.ER21);
		}
	}
	
	/**
	 * Unzips files into the specified directory.
	 * 
	 * @param zipIn
	 * @param destDirectory
	 * @throws DCException
	 */
	private void getZipFiles(ZipInputStream zipIn, String destDirectory) throws DCException {
		try {
			ZipEntry entry = zipIn.getNextEntry();
	        // iterates over entries in the zip file
	        while (entry != null) {
	            String filePath = destDirectory + File.separator + entry.getName();
	            if (!entry.isDirectory()) {
	                // if the entry is a file, extracts it
	                extractFile(zipIn, filePath);
	            } else {
	                // if the entry is a directory, make the directory
	                File dir = new File(filePath);
	                dir.mkdir();
	            }
	            zipIn.closeEntry();
	            entry = zipIn.getNextEntry();
	        }
	        zipIn.close();
		} catch(Exception e) {
			logger.error("error trying to copy file" );
			logger.error(e);
			throw new DCException(Constants.ER21);
		}
	}
	
	/**
	 * Exctracts the zip files.
	 * 
	 * @param zipIn
	 * @param filePath
	 * @throws IOException
	 */
	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
	
	/**
	 * Check if 2 shapes have the same metadata associated.
	 * 
	 * @param tempFilePath
	 * @param localFilePath
	 * @return
	 * @throws DCException
	 */
	private boolean metadataMatch(String tempFilePath,String localFilePath) throws DCException {
		
		try {
			
			String tempFileMetadata = ServiceUtil.createJSONColumnsFromShapeFile(tempFilePath);
			//logger.info("Temp file metadata:" +tempFileMetadata );
			
			String localFileMetadata = ServiceUtil.createJSONColumnsFromShapeFile(localFilePath);
			//logger.info("Local file metadata:" +localFileMetadata );
			
			if(localFileMetadata.equalsIgnoreCase(tempFileMetadata)) {
				return true;
			}
			return false;
			
		} catch(Exception e) {
			logger.error("error trying to copy file" );
			logger.error(e);
			throw new DCException(Constants.ER21);
		}
	}
	
	/**
	 * Returns true if the remote file last modified time is more recent than the local file one.
	 * 
	 * @param sourceURL
	 * @param file
	 * @return
	 * @throws DCException
	 */
	private boolean isFileModified(String sourceURL,File file) throws DCException {
		return getRemoteLastModified(sourceURL) > getLocalLastModified(file);
	}
	
	/**
	 * Returns the remote file last modified time.
	 * 
	 * @param sourceURL
	 * @return
	 * @throws DCException
	 */
	private long getRemoteLastModified(String sourceURL) throws DCException {
		
		try {
			
			URL url = new URL(sourceURL);
		    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		    //logger.info("REMOTE FILE LAST MODIFIED:"+httpCon.getLastModified());
		    return httpCon.getLastModified();
		
		} catch(Exception e) {
			logger.error("error trying to copy file" );
			logger.error(e);
			throw new DCException(Constants.ER21);
		}

	}
	
	/**
	 * 
	 * Returns the local file last modified time.
	 *  
	 * @param file
	 * @return
	 * @throws DCException
	 */
	private long getLocalLastModified(File file) throws DCException {
			//logger.info("FILE LAST MODIFIED:"+file.lastModified());
			return file.lastModified();
	}
}
