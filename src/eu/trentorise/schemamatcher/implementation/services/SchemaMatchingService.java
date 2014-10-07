package eu.trentorise.schemamatcher.implementation.services;

import java.util.ArrayList;
import java.util.List;

import eu.trentorise.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.schemamatcher.services.matching.ISchemaMatchingService;

public class SchemaMatchingService implements ISchemaMatchingService {

	

	public List<String> getSchemaMatchingAlgorithms() {

		List<String> matcherTypes = new ArrayList<String>();
		matcherTypes.add("Simple");
		return matcherTypes;
	}

	public ISchemaMatcher getSchemaMatcher(String scheamMatcherType) {
		
		ISchemaMatcher sm=	SchemaMatcherFactory.create(scheamMatcherType);
		return sm;
	}




}
