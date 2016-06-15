package it.sinergis.datacatalogue.persistence.services.util;

import org.apache.log4j.Logger;

import it.sauronsoftware.cron4j.Scheduler;
import it.sinergis.datacatalogue.bean.jpa.Gsc007DatasetEntity;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.common.PropertyReader;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.services.UpdateShapeService;

/**
 * Handles the cron scheduler.
 * 
 * @author A2LL0148
 *
 */
public final class CronUtils {
	
	/** Logger. */
	private static Logger logger = Logger.getLogger(CronUtils.class);
	
	///** Logger. */
	//private static PropertyReader propertyReader;
	
	/** The class. */
	private static CronUtils instance = null;
	
	/**The scheduler. */
	private static Scheduler scheduler;
	
	/** the prop reader. */
	private static PropertyReader propertyReader;
	
	/**
	 * private constructor.
	 */
	private CronUtils() {	
	}
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return
	 */
	public static CronUtils getInstance() {
		if(instance == null) {
	         instance = new CronUtils();
	         
	         propertyReader = new PropertyReader("configuration.properties");
	      }
	      return instance;
	}
	
	/**
	 * Get the scheduler.
	 * 
	 * @return
	 */
	public static Scheduler getScheduler() {
		if(scheduler == null) {
			scheduler = new Scheduler();
		}
		return scheduler;
	}
	
	/**
	 * Start the scheduler.
	 * 
	 * @return
	 */
	public void startScheduler() {
		getScheduler().start();
	}
	
	/**
	 * Stop the scheduler.
	 * 
	 * @return
	 */
	public void stopScheduler() {
		getScheduler().stop();
	}
	
	public void scheduleTask() {
		//testing run (every min)
		//getScheduler().schedule("* * * * *", new Runnable() {
		
		//the scheduler starts at :00 each hour ("0 * * * *")
		String cronPattern = propertyReader.getValue(Constants.CRON_PATTERN);
		getScheduler().schedule(cronPattern, new Runnable() {
			public void run() {

				long startExecutionTime = System.currentTimeMillis();
				logger.debug("Scheduled task start: check for updates.");
				//logger.debug("Current time:"+startExecutionTime);
				
				
				//find all the dataset records that need update checks...
				UpdateShapeService updateShapeService = new UpdateShapeService();
				
				//updated file number count
				int updatedFiles = 0;
				
				for(Gsc007DatasetEntity updatableDataset : updateShapeService.getUpdatableDatasets()) {
					
					try {
					
						//update the file. Increment count if it was effectively updated.
						if(updateShapeService.updateFile(updatableDataset) != null) {
							updatedFiles++;
						}
						
					} catch(DCException e) {
						logger.error("Exception occurred during the update process for dataset "+ updatableDataset.getId());
						logger.error(e);
						
						continue;
					} catch(Exception e) {
						logger.error("Exception occurred during the update process for dataset "+ updatableDataset.getId());
						logger.error(e);
						
						continue;
					}
				}
				
				long endExecutionTime = System.currentTimeMillis();
				logger.debug("Scheduled task end: check for updates.");
				logger.debug(updatedFiles+" files have been updated");	
				//logger.debug("Current time:"+endExecutionTime);			
				logger.debug("Runtime ="+ ((double)(endExecutionTime-startExecutionTime))/1000 +" seconds");
			}
		});
		
		startScheduler();
		
		
	}
	
}