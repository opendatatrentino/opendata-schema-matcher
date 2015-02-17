package eu.trentorise.opendata.schemamatcher.implementation.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.ElementMatcherFactory;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementMatcher;

public class TestElementMatcher {
	
	private final static Logger LOGGER = Logger.getLogger(TestSchemaImport.class.getName());
	private EntityType etype;
	
	@Before
	public void readEtype(){
		EntityTypeService ets = new EntityTypeService();
		String etypeUrl = WebServiceURLs.etypeIDToURL(12L);
		 etype= (EntityType) ets.readEntityType(etypeUrl);
	}
	
	
	@Test
	public void testSchemaElementMatcher() throws IOException, SchemaMatcherException{
		SchemaImport si = new SchemaImport();
		File file = new File("impianti risalita.csv");
		
		ISchema schemaCSV= si.parseCSV(file);
		ISchema schemaEtype=si.extractSchema(etype, Locale.ITALIAN);
		
		ElementMatcherFactory emf = new ElementMatcherFactory();
		ISchemaElementMatcher elementMatcher = emf.create("EditDistanceBased");
		
		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
		//List<ISchemaElement> elementsConcept = sefe.runColumnRecognizer(schemaCSV.getSchemaElements());
		
		
		List<ISchemaElementCorrespondence> correspondences = elementMatcher.matchSchemaElements(schemaCSV.getSchemaElements(), schemaEtype.getSchemaElements());
		for (ISchemaElementCorrespondence cor: correspondences){
			//SchemaElementCorrespondence c = (SchemaElementCorrespondence)cor;
		//	cor.computeHighestCorrespondencePair();
		LOGGER.info("SourceName: "+cor.getSourceElement().getElementContext().getElementName());
		LOGGER.info("TargetName: "+cor.getTargetElement().getElementContext().getElementName());
		LOGGER.info("Score: "+cor.getElementCorrespondenceScore());

		
		}
		
	}

}
