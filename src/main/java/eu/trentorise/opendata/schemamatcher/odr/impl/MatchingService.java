package eu.trentorise.opendata.schemamatcher.odr.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.NLPService;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semantics.services.ISemanticMatchingService;
import eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence;

public  class MatchingService implements ISemanticMatchingService {
	private static final String SCHEMA_MATCHER_ALGORITHM = "Simple";
	private final static Logger LOGGER = Logger.getLogger(MatchingService.class.getName());
	private Locale locale;

	public List<ISchemaCorrespondence> matchSchemas(IResourceContext resourceContext, ITableResource tableResource) {
		Resource rc = new Resource();
		List<String> strings = tableResource.getHeaders();
		List<String> nlpInput = new ArrayList<String>();
		nlpInput.add(strings.toString());
		locale = NLPService.detectLanguage(nlpInput);
		rc.setResourceContext(resourceContext);
		rc.setTableResource(tableResource);
		SchemaImport si = new SchemaImport();
		ISchema schemaSource = null;
		try {
			schemaSource = si.extractSchema(rc, locale);
		} catch (SchemaMatcherException e) {
			LOGGER.error(e.getMessage());
		}
		List<ISchema> sourceSchemas = new ArrayList<ISchema>();
		sourceSchemas.add(schemaSource);

		return match(sourceSchemas);
	}

	public List<ISchemaCorrespondence> matchSchemasFile(File file) {
		SchemaImport si = new SchemaImport();
		ISchema schemaCSV = null;
		try {
			schemaCSV = si.parseCSV(file);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		List<ISchema> sourceSchemas = new ArrayList<ISchema>();
		sourceSchemas.add(schemaCSV);
		return	match(sourceSchemas);
	}


	private List<ISchemaCorrespondence> match(List<ISchema> sourceSchemas){
		SchemaImport si = new SchemaImport();
		EntityTypeService etypeService = new EntityTypeService();
		List<IEntityType> etypeList = etypeService.getAllEntityTypes();
		List<ISchema> targetSchemas = new ArrayList<ISchema>();

		for (IEntityType etype:etypeList){

			ISchema schemaEtype = null;
			try {
				schemaEtype = si.extractSchema(etype, locale);
			} catch (SchemaMatcherException e) {
				LOGGER.error(e.getMessage());
			}
			targetSchemas.add(schemaEtype);

		}
		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create(SCHEMA_MATCHER_ALGORITHM);
		List<eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence>  schemaCor =schemaMatcher.matchSchemas(sourceSchemas, targetSchemas, "ConceptDistanceBased");
		List<ISchemaCorrespondence> schemaCorODR = new ArrayList<ISchemaCorrespondence>();
		for(eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence sc: schemaCor){
			SchemaCorrespondence scor = (SchemaCorrespondence) sc;
			ScheCorrespondence scheC = scor.convertToScheCorrespondence();
			schemaCorODR.add(scheC);
		}

		return schemaCorODR;
	}
}
