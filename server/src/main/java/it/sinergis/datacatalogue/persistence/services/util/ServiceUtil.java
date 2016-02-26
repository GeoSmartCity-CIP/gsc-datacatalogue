package it.sinergis.datacatalogue.persistence.services.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.sinergis.datacatalogue.common.Constants;

public class ServiceUtil {

	public static String createJSONColumnsFromShapeFile(String path) throws IOException {
		File file = new File(path);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constants.URL, file.toURI().toURL());

		DataStore dataStore = DataStoreFinder.getDataStore(map);
		String typeName = dataStore.getTypeNames()[0];

		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
		Filter filter = Filter.INCLUDE;

		String json = buildJsonColumns(source.getFeatures(filter));
		dataStore.dispose();
		return json;
	}
	
	public static String[] getDatastoreTypeNames(String dbType, String host, String port, String schema,
			String database, String user, String password) throws IOException {
		DataStore dataStore = createDatastorePostgis(dbType, host, port, schema, database, user, password);
		
		String[] typeNames = dataStore.getTypeNames();
		dataStore.dispose();
		
		return typeNames;
	}

	public static String createJSONColumnsFromPostGisDB(String dbType, String host, String port, String schema,
			String database, String user, String password, String table) throws IOException {

		DataStore dataStore = createDatastorePostgis(dbType, host, port, schema, database, user, password);
		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(table);
		Filter filter = Filter.INCLUDE;

		String json = buildJsonColumns(source.getFeatures(filter));
		dataStore.dispose();
		return json;
	}

	private static DataStore createDatastorePostgis(String dbType, String host, String port, String schema,
			String database, String user, String password) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constants.DBTYPE, dbType);
		map.put(Constants.HOST, host);
		map.put(Constants.PORT, Integer.parseInt(port));
		map.put(Constants.SCHEMA_FIELD, schema);
		map.put(Constants.DATABASE, database);
		map.put(Constants.USER, user);
		map.put(Constants.PASSWD, password);

		DataStore dataStore = DataStoreFinder.getDataStore(map);

		return dataStore;
	}

	private static String buildJsonColumns(FeatureCollection<SimpleFeatureType, SimpleFeature> collection)
			throws IOException {

		try (FeatureIterator<SimpleFeature> features = collection.features()) {
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> columnsList = new ArrayList<>();

			while (features.hasNext()) {
				SimpleFeature feature = features.next();
				for (Property attribute : feature.getProperties()) {
					Map<String, Object> mapValues = new HashMap<>();
					mapValues.put(Constants.NAME, attribute.getName().toString());
					mapValues.put(Constants.TYPE, attribute.getType().getBinding().getSimpleName().toString());
					mapValues.put(Constants.ALIAS, attribute.getName().toString());
					mapValues.put(Constants.VISIBILITY, Constants.TRUE);
					columnsList.add(mapValues);
				}
				break;
			}

			return mapper.writeValueAsString(columnsList);
		}
	}
}
