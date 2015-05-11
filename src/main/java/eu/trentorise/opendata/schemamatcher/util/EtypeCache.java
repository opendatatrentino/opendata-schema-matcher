package eu.trentorise.opendata.schemamatcher.util;

import it.unitn.disi.sweb.webapi.client.kb.ComplexTypeClient;
import it.unitn.disi.sweb.webapi.model.filters.ComplexTypeFilter;
import it.unitn.disi.sweb.webapi.model.kb.types.ComplexType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;


/**
 *
 * @author Ivan Tankoyeu
 */
public final class EtypeCache {

	private static final long DEFAULT_KB = 1;


	private static final EtypeCache singleton = new EtypeCache();

	private HashMap<Long,Long> etypesStatusMap;

	private ImmutableList<IEntityType> etypes;

	public EtypeCache() {
		etypes = ImmutableList.of();
	}

	/**
	 * Returns the cache singleton;
	 */
	public static EtypeCache of() {
		return singleton;
	}

	/**
	 * Returns an immutable list of all schemas. Schemas can be fetched lazily
	 * from the server if not present in cache or if stale
	 */
	public synchronized List<IEntityType> readSchemas() {

		if(etypes.size()==0){
			createSchemas();
		}

		ArrayList<Long> outdatedSchemaIds = getOutdatedSchemas();
		if (outdatedSchemaIds.size()!=0) {
			updateSchemas(outdatedSchemaIds);
			return etypes;
		} else {
			return etypes;
		}
	}

	public void createSchemas() {
		EntityTypeService ets = new EntityTypeService();
		List<IEntityType> etypeList =  ets.getAllEntityTypes();
		etypes = new ImmutableList.Builder<IEntityType>()
	           .addAll(etypeList)
	           .build();
	}

	private void updateSchemas(ArrayList<Long> outdatedSchemaIds) {
		EntityTypeService ets = new EntityTypeService();
		
		for(Long outdatedEtypeId : outdatedSchemaIds){
			IEntityType updtadetEtype = ets.getEntityType(outdatedEtypeId);
			//update immutable list;
		}
	}

	public ArrayList<Long> getOutdatedSchemas() {
		ArrayList<Long> outdatedTypes = new ArrayList<Long>();
		ComplexTypeClient ctc = new ComplexTypeClient(WebServiceURLs.getClientProtocol());
		ComplexTypeFilter ctFilter = new ComplexTypeFilter();
		ctFilter.setIncludeTimestamps(true);
		List<ComplexType> complexTypeList = ctc.readComplexTypes(DEFAULT_KB, null, null, ctFilter);
		int i=0;
		HashMap<Long,Long> freshEtypes = new HashMap<Long,Long>();
		for (ComplexType ct : complexTypeList){
			freshEtypes.put(ct.getConceptId(), ct.getModificationDate().getTime());
			//			System.out.println(++i+": Concept id: "+ct.getConceptId());
			//			System.out.println("Timestamp: "+ct.getModificationDate());
		}

		for(Map.Entry<Long, Long> entry : freshEtypes.entrySet() ){

			if(!etypesStatusMap.containsKey(entry.getKey())) {
				outdatedTypes.add(entry.getKey());
			} else {
				long newTime = entry.getValue();
				if(etypesStatusMap.get(entry.getKey())<newTime) {
					outdatedTypes.add(entry.getKey());
				}
			}
		}
		etypesStatusMap = freshEtypes;

		return outdatedTypes;
	}
}

class Usage {

	public void test() {
		List<IEntityType> schemas = EtypeCache.of().readSchemas();
	}
}
