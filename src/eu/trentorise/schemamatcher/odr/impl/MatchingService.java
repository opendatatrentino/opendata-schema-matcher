package eu.trentorise.schemamatcher.odr.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.opendata.semantics.services.ISemanticMatchingService;
import eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence;
import eu.trentorise.schemamatcher.implementation.model.SchemaCorrespondence;
import eu.trentorise.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.schemamatcher.model.ISchema;
import eu.trentorise.schemamatcher.model.ISchemaMatcher;

public  class MatchingService implements ISemanticMatchingService {

	public List<ISchemaCorrespondence> matchSchemas(IResourceContext resourceContext, ITableResource tableResource) {
		Resource rc = new Resource();
		rc.setResourceContext(resourceContext);
		rc.setTableResource(tableResource);
		SchemaImport si = new SchemaImport();
		ISchema schemaSource = null;
		try {
			schemaSource = si.extractSchema(rc, Locale.ITALIAN);
		} catch (SchemaMatcherException e) {
			e.printStackTrace();
		}
		List<ISchema> sourceSchemas = new ArrayList<ISchema>();
		sourceSchemas.add(schemaSource);

		return match(sourceSchemas);
	}

	public List<ISchemaCorrespondence> matchSchemasFile(File file) {
		SchemaImport si = new SchemaImport();
		//File file = new File("/home/ivan/Downloads/impianti-risalita-vivifiemme.csv");

		ISchema schemaCSV = null;
		try {
			schemaCSV = si.parseCSV(file);
		} catch (IOException e) {
			e.printStackTrace();
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
				schemaEtype = si.extractSchema(etype, Locale.ITALIAN);
			} catch (SchemaMatcherException e) {
				e.printStackTrace();
			}
			targetSchemas.add(schemaEtype);

		}
		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("Simple");
		List<eu.trentorise.schemamatcher.model.ISchemaCorrespondence>  schemaCor =schemaMatcher.matchSchemas(sourceSchemas, targetSchemas, "ConceptDistanceBased");

		List<ISchemaCorrespondence> schemaCorODR = new ArrayList<ISchemaCorrespondence>();

		for(eu.trentorise.schemamatcher.model.ISchemaCorrespondence sc: schemaCor){
			SchemaCorrespondence scor = (SchemaCorrespondence) sc;
			ScheCorrespondence scheC = scor.convertToScheCorrespondence();
			schemaCorODR.add(scheC);
		}

		return schemaCorODR;
	}
}
