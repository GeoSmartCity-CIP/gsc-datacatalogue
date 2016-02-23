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
		map.put("url", file.toURI().toURL());

		DataStore dataStore = DataStoreFinder.getDataStore(map);
		String typeName = dataStore.getTypeNames()[0];

		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(typeName);
		Filter filter = Filter.INCLUDE;

		return buildJsonColumns(source.getFeatures(filter));
	}

	public static String createJSONColumnsFromPostGisDB(String dbType, String host, String port, String schema,
			String database, String user, String password, String table) throws IOException {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dbtype", dbType);
		map.put("host", host);
		map.put("port", Integer.parseInt(port));
		map.put("schema", schema);
		map.put("database", database);
		map.put("user", user);
		map.put("passwd", password);
		
		DataStore dataStore = DataStoreFinder.getDataStore(map);
		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(table);
		Filter filter = Filter.INCLUDE;

		return buildJsonColumns(source.getFeatures(filter));
	}
	
	public static String createJSONColumnsFromOracleDB(String dbType, String host, String port, String schema,
			String database, String user, String password, String table) throws IOException {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dbtype", dbType);
		map.put("host", host);
		map.put("port", Integer.parseInt(port));
		map.put("schema", schema);
		map.put("database", database);
		map.put("user", user);
		map.put("passwd", password);
		
		DataStore dataStore = DataStoreFinder.getDataStore(map);
		FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(table);
		Filter filter = Filter.INCLUDE;

		return buildJsonColumns(source.getFeatures(filter));
	}
	
	private static String buildJsonColumns(FeatureCollection<SimpleFeatureType, SimpleFeature> collection) throws IOException {
		
		try (FeatureIterator<SimpleFeature> features = collection.features()) {
			ObjectMapper mapper = new ObjectMapper();
			List<Map<String, Object>> columnsList = new ArrayList<>();

			while (features.hasNext()) {
				SimpleFeature feature = features.next();
				feature.getAttributes();
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
