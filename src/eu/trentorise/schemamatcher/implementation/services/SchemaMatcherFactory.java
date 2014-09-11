package eu.trentorise.schemamatcher.implementation.services;


import eu.trentorise.schemamatcher.model.ISchemaMatcher;

public  class SchemaMatcherFactory {


	public static ISchemaMatcher create(String schemaMatcherType) {
		ISchemaMatcher schemaMatcher = null;
		if (schemaMatcherType.equalsIgnoreCase ("Simple")){
			return (ISchemaMatcher) new SimpleSchemaMatcher();
		} else if 
		(schemaMatcherType.equalsIgnoreCase ("HungarianAllocationAndEditDistance")){
			return (ISchemaMatcher) new HungarianAlgSchemaMatcher();
		}
		return schemaMatcher;
	}

}

