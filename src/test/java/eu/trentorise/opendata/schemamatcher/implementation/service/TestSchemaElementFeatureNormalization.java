package eu.trentorise.opendata.schemamatcher.implementation.service;

import static org.junit.Assert.*;

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
	private static final double DIVERGENCE = 4.0861425;
	private EntityType etype;
	ISchema sourceSchema;
	ISchema targetSchema;
	private static final double DELTA = 1e-6;

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
				for (ISchemaElement tel: targetSchemaElements){
					if((tel.getElementContext().getElementDataType().equalsIgnoreCase("xsd:float"))&&(tel.getElementContent().getContentSize()>0)){
						if(tel.getElementContext().getElementName().equalsIgnoreCase("Latitude")&&sel.getElementContext().getElementName().equalsIgnoreCase("latitudine"))
							assertEquals(sefe.getStatisticalDistance(sel.getElementContent(), tel.getElementContent()), DIVERGENCE, DELTA);
					}
				}
			}
		}



	}

}
