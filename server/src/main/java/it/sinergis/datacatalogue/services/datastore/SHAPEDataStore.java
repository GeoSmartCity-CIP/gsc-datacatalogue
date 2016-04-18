package it.sinergis.datacatalogue.services.datastore;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSShapefileDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.services.util.ServiceUtil;

/**
 * Class to create datastores and publish layers on them.
 * @author A2MD0149
 *
 */
public class SHAPEDataStore {

	/** Logger. */
	private static Logger logger = null;

	/**
	 * Costruttore di default.
	 */
	public SHAPEDataStore() {
		logger = Logger.getLogger(this.getClass());
	}

	/**
	 */
	public String createDatastore(String nameStore, String workspace, String workspace_uri, String fnDatabase,
			GeoServerRESTStoreManager publisher, GeoServerRESTReader reader, String tablephysicalname,
			String tablePhysicalPath) throws IOException, DCException {

		if (fnDatabase != null) {
			// se il db e' di tipo shape
			if (StringUtils.isNotEmpty(tablePhysicalPath)) {
				nameStore = nameStore + "_"
						+ ServiceUtil.normalizedLayerName(tablePhysicalPath.replaceAll("\\\\", "_"));
			}
			GSShapefileDatastoreEncoder sds;

			if (tablePhysicalPath != null) {
				URL url = new URL("file:" + fnDatabase + "\\" + tablePhysicalPath);
				sds = new GSShapefileDatastoreEncoder(nameStore, url);

			} else {
				URL url = new URL("file:" + fnDatabase);
				sds = new GSShapefileDatastoreEncoder(nameStore, url);
			}
			sds.setEnabled(true);
			// sds.setNamespace(workspace_uri);

			RESTDataStore ds = reader.getDatastore(workspace, nameStore);
			if (ds == null) {
				boolean created = publisher.create(workspace, sds);
				if (created == false) {
					throw new DCException(Constants.ER_GEO02);
				}

				logger.debug("Datastore created successfully on geoserver");
			}
		}
		return nameStore;
	}

	/**
	 * publish a layer on the datastore.
	 * 
	 *
	 * @param sotrenome
	 *            name store
	 * @param workspace
	 *            name workspace
	 * @param layername
	 *            nome logico layer
	 * @param nativename
	 *            nome fisico layer
	 * @param title
	 *            title layer
	 * @param srs
	 *            srs layer
	 * @param default
	 *            style style
	 * 
	 * @throws Exception
	 *             eccezione
	 */
	public void publishDBLayer(String workspace, String storename, PropertyLayer pl, GeoServerRESTPublisher publisher)
			throws DCException {
		GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
		fte.setName(pl.getLogicalName());
		fte.setNativeName(ServiceUtil.normalizedLayerName(pl.getPhysicalName()));
		fte.setSRS(pl.getSrs());
		fte.setTitle(pl.getTitle());

		if (pl.getBounds() != null) {
			fte.setNativeBoundingBox(pl.getBounds()[0], pl.getBounds()[2], pl.getBounds()[1], pl.getBounds()[3],
					pl.getSrs());
		}

		GSLayerEncoder le = new GSLayerEncoder();
		le.setDefaultStyle(pl.getDefaultStyle());

		boolean created = publisher.publishDBLayer(workspace, storename, fte, le);
		if (created == false) {
			throw new DCException(Constants.ER_GEO01);
		}
	}

}
