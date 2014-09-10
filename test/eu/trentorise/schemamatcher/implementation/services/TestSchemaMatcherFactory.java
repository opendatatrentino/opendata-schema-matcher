package eu.trentorise.schemamatcher.implementation.services;

import org.junit.Test;

import eu.trentorise.schemamatcher.model.ISchemaMatcher;

public class TestSchemaMatcherFactory {

	@Test
	public void testSchemaMatcherFactoryCreate(){
		SchemaMatchingService sms = new SchemaMatchingService();
		System.out.println(sms.getSchemaMatchingAlgorithms().get(0));
		
		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("Simple");
		System.out.println(schemaMatcher.getSchemaMatchingAlgorithm());
		
	}
	
	
}
