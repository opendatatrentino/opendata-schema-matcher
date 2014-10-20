package eu.trentorise.opendata.schemamatcher.implementation.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;

public class TestSchemaElementFeatureExtractor {
	
	@Test 
	public void testFeatureExtractor() throws IOException{
		SchemaImport si = new SchemaImport();
		File file = new File("impianti-risalita-vivifiemme.csv");
		ISchema schemaOut= si.parseCSV(file);
		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();

		List<ISchemaElement> elementsConcept = sefe.runColumnRecognizer(schemaOut.getSchemaElements());
		System.out.println(elementsConcept.get(0).getElementContext().getElementConcept());
		System.out.println(elementsConcept.get(1).getElementContext().getElementConcept());
		System.out.println(elementsConcept.get(2).getElementContext().getElementConcept());


	}
	
}
