package eu.trentorise.opendata.schemamatcher.model;

import java.util.List;

/** Schema matching services allow operations on schemas
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 22 Jul 2014
 * 
 */
public interface ISchemaMatcher {


	/** Match two schemas and returns matching score and  mapping among elements of given schemas 
	 * @param sourceSchema schema FROM which matching is required 
	 * @param targetSchema schema TO which matching is required 
	 * @param schemaMatchingAlgorithm algorithm that is used for schema matching
	 * @return coresspondence between two schemas and mapping among their elements.
	 */
	ISchemaCorrespondence matchSchemas(ISchema sourceSchema, ISchema targetSchema, String schemaMatchingAlgorithm);

	/** Returns the name of an algorithm that is employed for matching schemas.
	 * @return an algorithms that is implemented in matcher.
	 */
	String getSchemaMatchingAlgorithm();

	/**Match the list of schemas and returns matching score and  mapping among elements of given schemas 
	 * @param sourceSchemas the list of schema FROM which mapping is required
	 * @param targetSchemas the list of schema TO which mapping is required
	 * @param elementMatchingAlgorithm algorithm that is used for schema matching
	 * @return coresspondence between all schemas and mapping among their elements.
	 */
	List<ISchemaCorrespondence> matchSchemas(List<ISchema> sourceSchemas,
			List<ISchema> targetSchemas, String elementMatchingAlgorithm);

}
