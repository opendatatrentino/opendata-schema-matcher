package eu.trentorise.opendata.schemamatcher.implementation.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElementFeatureExtractor;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;

public class TestSchemaElementFeatureNormalization {
	private EntityType etype;
	ISchema sourceSchema;
	ISchema targetSchema;
	@Before
	public void importSchemas() throws IOException, SchemaMatcherException{

		SchemaImport si = new SchemaImport();
		EntityTypeService ets = new EntityTypeService();
		String etypeUrl = WebServiceURLs.etypeIDToURL(12L);
		etype= (EntityType) ets.readEntityType(etypeUrl);

		File file = new File("impianti risalita.csv");
		sourceSchema= si.parseCSV(file);
		targetSchema=si.extractSchema(etype, Locale.ENGLISH);
	}

	@Test
	public  void testKLDivergenceDistance(){

		SchemaElementFeatureExtractor sefe = new SchemaElementFeatureExtractor();
		List<ISchemaElement> sourceSchemaElements = sourceSchema.getSchemaElements();
		List<ISchemaElement> targetSchemaElements = targetSchema.getSchemaElements();

		for(ISchemaElement sel: sourceSchemaElements){
			if(sel.getElementContext().getElementDataType().equalsIgnoreCase("FLOAT")){
				System.out.println("Source: "+sel.getElementContext().getElementName());
				for (ISchemaElement tel: targetSchemaElements){
					if((tel.getElementContext().getElementDataType().equalsIgnoreCase("xsd:float"))&&(tel.getElementContent().getContentSize()>0)){
						System.out.println("Target: "+tel.getElementContext().getElementName());
						System.out.println("Divergence: "+sefe.getStatisticalDistance(sel.getElementContent(), tel.getElementContent()));
					}
				}
			}
		}

	

	}

}
