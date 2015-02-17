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
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;


/** Test class for SchemaImport services
 * @author Ivan Tankoyeu
 *
 */


public class TestSchemaImport {

	private final static Logger LOGGER = Logger.getLogger(TestSchemaImport.class.getName());
	private EntityType etype;

	@Before
	public void readEtype(){
		EntityTypeService ets = new EntityTypeService();
		String etypeUrl = WebServiceURLs.etypeIDToURL(12L);
		etype= (EntityType) ets.readEntityType(etypeUrl);
	}

	@Test
	public void testSchemaImportEtype() throws SchemaMatcherException{

		SchemaImport si = new SchemaImport();
		ISchema schema=si.extractSchema(etype, Locale.ENGLISH);

		List<ISchemaElement> elements = schema.getSchemaElements();
		for(ISchemaElement el: elements){
		//	System.out.println(el.getElementContext().getElementName()+" "+el.getElementContext().getElementDataType());
			if((el.getElementContext().getElementDataType().equalsIgnoreCase("xsd:float"))|| (el.getElementContext().getElementDataType().equalsIgnoreCase("xsd:integer")) 
					|| (el.getElementContext().getElementDataType().equalsIgnoreCase("xsd:long")) )
			{
				//System.out.println("Content size: "+el.getElementContent().getContentSize());
			//	if(el.getElementContent().getContentSize()!=0)
			//	System.out.println("Content example: "+el.getElementContent().getContent().iterator().next());
			}
		}
		LOGGER.info(schema.toString());
	}

	@Test 
	public void testSchemaImportCSV() throws SchemaMatcherException, IOException{
		SchemaImport si = new SchemaImport();
		File file = new File("impianti risalita.csv");
		ISchema schemaOut= si.parseCSV(file);

	}

	@Test
	public void testSchemaImportEntities() throws SchemaMatcherException{

		SchemaImport si = new SchemaImport();
		si.getEntities(etype);

	}

}
