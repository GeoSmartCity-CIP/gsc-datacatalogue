package it.sinergis.datacatalogue.services.datastore;

import java.io.IOException;

import org.apache.log4j.Logger;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import it.sinergis.datacatalogue.common.Constants;
import it.sinergis.datacatalogue.exception.DCException;
import it.sinergis.datacatalogue.persistence.services.util.ServiceUtil;

/**
 * Class to create datastores and publish layers on them.
 * 
 * @author A2MD0149
 *
 */
public class PostgisDataStore {

	/** Logger. */
	private static Logger logger = null;

	/**
	 * Costruttore di default.
	 */
	public PostgisDataStore() {
		logger = Logger.getLogger(this.getClass());
	}

	/**
	 */
	public String createDatastore(String nameStore, String workspace, String workspace_uri,
			GeoServerRESTStoreManager publisher, GeoServerRESTReader reader, String host, int port, String user,
			String schema, String password, String database) throws IOException, DCException {

		GSPostGISDatastoreEncoder postgisEncoder = new GSPostGISDatastoreEncoder(nameStore);
		postgisEncoder.setHost(host);
		postgisEncoder.setPort(port);
		postgisEncoder.setPassword(password);
		postgisEncoder.setNamespace(workspace_uri);
		postgisEncoder.setSchema(schema);
		postgisEncoder.setUser(user);
		postgisEncoder.setDatabase(database);
		postgisEncoder.setEnabled(true);

		RESTDataStore ds = reader.getDatastore(workspace, nameStore);
		if (ds == null) {
			boolean created = publisher.create(workspace, postgisEncoder);
			if (created == false) {
				throw new DCException(Constants.ER_GEO02);
			}

			logger.debug("Datastore created successfully on geoserver");
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
