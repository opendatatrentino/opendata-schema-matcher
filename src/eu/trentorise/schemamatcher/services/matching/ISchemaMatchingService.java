package eu.trentorise.schemamatcher.services.matching;

import java.util.List;

import eu.trentorise.schemamatcher.model.ISchemaMatcher;

/** Schema matching services allow operations on schemas
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 22 Jul 2014
 * 
 */
public interface ISchemaMatchingService {

	/** Returns schema matcher of needed type
	 * @param scheamMatcherType the type of schema matching 
	 * @return schema matcher
	 */
	ISchemaMatcher getSchemaMatcher(String scheamMatcherType);

	/** Returns availalbe algorithm that can be employed for matching schemas.
	 * @return a list of algorithms that can be used for schema matching
	 */
	List<String> getSchemaMatchingAlgorithms();
}