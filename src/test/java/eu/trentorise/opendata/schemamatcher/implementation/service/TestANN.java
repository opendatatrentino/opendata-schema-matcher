package eu.trentorise.opendata.schemamatcher.implementation.service;

import java.io.IOException;

import org.junit.Test;

import eu.trentorise.opendata.schemamatcher.implementation.services.ANNElementMatcher;

public class TestANN {

	@Test
	public void testConceptFromText() throws IOException{

		ANNElementMatcher ann = new ANNElementMatcher();
		ann.createANN();

	}
}