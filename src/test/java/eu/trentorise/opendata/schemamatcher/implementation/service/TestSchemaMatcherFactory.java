package eu.trentorise.opendata.schemamatcher.implementation.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatchingService;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;

public class TestSchemaMatcherFactory {

	private static final String ALG_NAME = "Simple";

	@Test
	public void testSchemaMatcherFactoryCreate(){
		SchemaMatchingService sms = new SchemaMatchingService();
		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create(ALG_NAME);
		assertEquals(schemaMatcher.getSchemaMatchingAlgorithm(),ALG_NAME);
	}
	
	
}
