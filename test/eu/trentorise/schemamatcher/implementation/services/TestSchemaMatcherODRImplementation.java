package eu.trentorise.schemamatcher.implementation.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;
import eu.trentorise.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.schemamatcher.odr.impl.MatchingService;

public class TestSchemaMatcherODRImplementation {

	private final static Logger LOGGER = Logger.getLogger(TestSchemaImport.class.getName());



	@Test
	public void testSchemaElementMatcherAllEtypes() throws IOException, SchemaMatcherException{

		File file = new File("/home/ivan/Downloads/impianti-risalita-vivifiemme.csv");
		MatchingService ms = new MatchingService();
		List<eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence> sc = ms.matchSchemasFile(file);
		for (eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence c : sc){
			LOGGER.info("Etype name: "+c.getEtype().getName().getString(Locale.ENGLISH)+" "+c.getScore());
			for(IAttributeCorrespondence ac: c.getAttributeCorrespondences()){
				LOGGER.info("Attribute: "+ac.getAttrDef().getName().getString(Locale.ENGLISH)+" score: "+ac.getScore()+ " index: "+ac.getColumnIndex());
			}
		}
	}
}