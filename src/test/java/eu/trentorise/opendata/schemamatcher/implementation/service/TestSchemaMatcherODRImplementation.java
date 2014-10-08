package eu.trentorise.opendata.schemamatcher.implementation.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.odr.impl.MatchingService;

public class TestSchemaMatcherODRImplementation {

	private final static Logger LOGGER = Logger.getLogger(TestSchemaImport.class.getName());


	  String resourceName = "IMPIANTI RISALITA";

	    String col1 = "nr";
	    String col2 = "Comune";
	    String col3 = "Insegna";
	    String col4 = "Tipo";
	    String col5 = "Frazione";
	    String col6 = "Indirizio";
	    String col7 = "Civico";

	    List<String> cols = new ArrayList<String>() {
	        {
	            add("nr");
	            add("Comune");
	            add("Insegna");
	            add("Tipo");
	            add("Frazione");
	            add("Indirizio");
	            add("Civico");
	        }
	    };

	    List<List<String>> bodies = new ArrayList<List<String>>() {
	        {
	            add(new ArrayList<String>() {
	                {
	                    add("1");
	                    add("2");
	                    add("3");
	                }
	            });
	            add(new ArrayList<String>() {
	                {
	                    add("ANDALO");
	                    add("ARCO");
	                    add("BASELGA DI PINE");
	                }
	            });
	            add(new ArrayList<String>() {
	                {
	                    add("AL FAGGIO");
	                    add("OSTERIA IL RITRATTO");
	                    add("AI DUE CAMI");
	                }
	            });
	            add(new ArrayList<String>() {
	                {
	                    add("Ristorante");
	                    add("Ristorante-Bar");
	                    add("Albergo-Ristorante-Bar");
	                }
	            });
	            add(new ArrayList<String>() {
	                {
	                    add("ANDALO");
	                    add("ARCO");
	                    add("BASELGA DI PINE");
	                }
	            });
	            add(new ArrayList<String>() {
	                {
	                    add("Via Fovo");
	                    add("Via Ferrera");
	                    add("Via Pontara");
	                }
	            });
	            add(new ArrayList<String>() {
	                {
	                    add("11");
	                    add("30");
	                    add("352");
	                }
	            });
	        }
	    };


	@Test
	public void testSchemaElementMatcherAllEtypes() throws IOException, SchemaMatcherException{

		File file = new File("impianti risalita.csv");
		MatchingService ms = new MatchingService();
		List<eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence> sc = ms.matchSchemasFile(file);
		for (eu.trentorise.opendata.semantics.services.model.ISchemaCorrespondence c : sc){
			LOGGER.info("Etype name: "+c.getEtype().getName().getString(Locale.ENGLISH)+" "+c.getScore());
//			for(IAttributeCorrespondence ac: c.getAttributeCorrespondences()){
//				LOGGER.info("Attribute: "+ac.getAttrDef().getName().getString(Locale.ENGLISH)+" score: "+ac.getScore()+ " index: "+ac.getColumnIndex());
//			}
		}
	}
	

	 
}