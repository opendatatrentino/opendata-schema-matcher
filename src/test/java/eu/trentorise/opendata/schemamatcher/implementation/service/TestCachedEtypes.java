package eu.trentorise.opendata.schemamatcher.implementation.service;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.schemamatcher.util.EtypeCache;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;



public class TestCachedEtypes {

	private final static Logger LOGGER = Logger.getLogger(TestSchemaImport.class.getName());
	private EntityType etype;

//	@Test  TODO COMMENTED TEST!
	public void cacheTest(){
		EtypeCache ec = new EtypeCache();
		
	//	ec.createSchemas();
		List<IEntityType> etypes =ec.readSchemas();
		for(IEntityType etype : etypes){
			System.out.println(etype.getName().strings(Locale.ENGLISH));
			System.out.println(etype.getAttributeDefs().get(0).getConceptURL());

		}
		
	}
}
