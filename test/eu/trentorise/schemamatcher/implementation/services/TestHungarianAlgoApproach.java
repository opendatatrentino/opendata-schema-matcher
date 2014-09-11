package eu.trentorise.schemamatcher.implementation.services;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.schemamatcher.model.ISchema;
import eu.trentorise.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.schemamatcher.model.ISchemaMatcher;

public class TestHungarianAlgoApproach {

	HungarianAlgorithm ha = new HungarianAlgorithm();
	private final static Logger LOGGER = Logger.getLogger(TestSchemaImport.class.getName());
	private EntityType etype;

	@Before
	public void readEtype(){
		EntityTypeService ets = new EntityTypeService();
		String etypeUrl = WebServiceURLs.etypeIDToURL(12L);
		etype= (EntityType) ets.readEntityType(etypeUrl);
	}

	//@Test
	public void testHungarianMethod(){
		float[][] inputMatrix = new float[5][5];
		float minX = 0.0f;
		float maxX = 1.0f;
		Random rand = new Random();

		for (int i =0; i<5; i++){
			for (int j=0; j<5; j++){
				float finalX = rand.nextFloat() * (maxX - minX) + minX;
				inputMatrix[i][j]=finalX;
				System.out.print(finalX+"    ");
			}
			System.out.println();
		}
		int[][] assignedAttrs= ha.computeAssignments(inputMatrix);

		for (int i =0; i<5; i++){
			for (int j=0; j<2; j++){
				System.out.print(assignedAttrs[i][j]+" ");
			}
			System.out.println();
		}
	}

	@Test
	public void testSchemaElementMatcher() throws IOException, SchemaMatcherException{
		SchemaImport si = new SchemaImport();
		File file = new File("/home/ivan/Downloads/impianti-risalita-vivifiemme.csv");

		ISchema schemaCSV= si.parseCSV(file);
		ISchema schemaEtype=si.extractSchema(etype, Locale.ENGLISH);

		ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("HungarianAllocationAndEditDistance");
		ISchemaCorrespondence  schemaCor =schemaMatcher.matchSchemas(schemaCSV, schemaEtype, "ConceptDistanceBased");
	}
}
