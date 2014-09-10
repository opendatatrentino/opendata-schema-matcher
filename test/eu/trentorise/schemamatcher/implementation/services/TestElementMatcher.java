package eu.trentorise.schemamatcher.implementation.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.schemamatcher.implementation.model.SchemaElementCorrespondence;
import eu.trentorise.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.schemamatcher.model.ISchema;
import eu.trentorise.schemamatcher.model.ISchemaElement;
import eu.trentorise.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.schemamatcher.model.ISchemaElementMatcher;

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
		File file = new File("/home/ivan/Downloads/impianti-risalita-vivifiemme.csv");
		
		ISchema schemaCSV= si.parseCSV(file);
		ISchema schemaEtype=si.extractSchema(etype, Locale.ENGLISH);
		
		ElementMatcherFactory emf = new ElementMatcherFactory();
		ISchemaElementMatcher elementMatcher = emf.create("ConceptDistanceBased");
		
		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
		List<ISchemaElement> elementsConcept = sefe.runColumnRecognizer(schemaCSV.getSchemaElements());
		
		
		List<ISchemaElementCorrespondence> correspondences = elementMatcher.matchSchemaElements(elementsConcept, schemaEtype.getSchemaElements());
		for (ISchemaElementCorrespondence cor: correspondences){
			SchemaElementCorrespondence c = (SchemaElementCorrespondence)cor;
			c.computeHighestCorrespondencePair();
		LOGGER.info("SourceName: "+cor.getSourceElement().getElementContext().getElementName());
		LOGGER.info("Score: "+c.getScore());
		LOGGER.info("TargetName: "+c.getTargetElement());

		
		}
		
	}

}
