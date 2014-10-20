package eu.trentorise.opendata.schemamatcher.implementation.service;

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
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

public class TestSimpleSchemaMatcher {


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
		ISchema schemaEtype=si.extractSchema(etype, Locale.ENGLISH);

		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("Simple");
		ISchemaCorrespondence  schemaCor =schemaMatcher.matchSchemas(schemaCSV, schemaEtype, "ConceptDistanceBased");
		LOGGER.info("Schema Cor Score: "+schemaCor.getSchemaCorrespondenceScore());
	}

	@Test
	public void testSchemaElementMatcherAllEtypes() throws IOException, SchemaMatcherException{
		SchemaImport si = new SchemaImport();
		File file = new File("impianti risalita.csv");

		ISchema schemaCSV= si.parseCSV(file);
		List<ISchema> sourceSchemas = new ArrayList<ISchema>();
		sourceSchemas.add(schemaCSV);
		
		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypeList = etypeService.getAllEntityTypes();
		List<ISchema> targetSchemas = new ArrayList<ISchema>();

		for (IEntityType etype:etypeList){

			ISchema schemaEtype=si.extractSchema(etype, Locale.ENGLISH);
			targetSchemas.add(schemaEtype);

		}

		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("Simple");
		List<ISchemaCorrespondence>  schemaCor =schemaMatcher.matchSchemas(sourceSchemas, targetSchemas, "ConceptDistanceBased");
		
		for (ISchemaCorrespondence c : schemaCor){
		LOGGER.info("Etype name: "+c.getTargetSchema().getSchemaName()+ " "+c.getSchemaCorrespondenceScore()+" "+c.getSchemaElementCorrespondence() );
		List<ISchemaElementCorrespondence> sElCors = c.getSchemaElementCorrespondence();
		
		for (ISchemaElementCorrespondence sel: sElCors){
		System.out.print("Source-Target Element: "+sel.getSourceElement().getElementContext().getElementName()+"-");
		System.out.println(sel.getTargetElement().getElementContext().getElementName()+" Score: "+sel.getElementCorrespondenceScore());

		}

		}

	}

}
