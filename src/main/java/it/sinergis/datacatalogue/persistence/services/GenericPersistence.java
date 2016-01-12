package it.sinergis.datacatalogue.persistence.services;

import java.util.List;

public interface GenericPersistence {
	public List<Object> loadByNativeQueryGenericObject(final String query);
}
