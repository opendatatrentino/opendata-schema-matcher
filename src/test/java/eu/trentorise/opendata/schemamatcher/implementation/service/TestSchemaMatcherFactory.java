package eu.trentorise.opendata.schemamatcher.implementation.service;

import org.junit.Test;

import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatchingService;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;

public class TestSchemaMatcherFactory {

	@Test
	public void testSchemaMatcherFactoryCreate(){
		SchemaMatchingService sms = new SchemaMatchingService();
		System.out.println(sms.getSchemaMatchingAlgorithms().get(0));
		
		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("Simple");
		System.out.println(schemaMatcher.getSchemaMatchingAlgorithm());
		
	}
	
	
}
