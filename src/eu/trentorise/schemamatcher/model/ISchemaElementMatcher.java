package eu.trentorise.schemamatcher.model;

import java.util.List;

/** Interface contains methods for matching elements of the two schemas.
 * @author Ivan Tankoyeu
 *
 */

public interface ISchemaElementMatcher {

	/** Match two elements and returns matching score.
	 * @param sourceSchemaElement is a schema element FROM which matching is required 
	 * @param targetSchemaElement is a schema element TO which matching is required 
	 * @param elementMatchingAlgorithm algorithm that is used for schema matching
	 * @return coresspondence between two elements .
	 */
	ISchemaElementCorrespondence matchSchemaElements(ISchemaElement sourceSchemaElement, ISchemaElement targetSchemaElement);

	/**Match the list of elements and returns matching score for a matched pairs of elements 
	 * @param sourceElements the list of schema elements FROM which mapping is required
	 * @param targetElements the list of schema elements TO which mapping is required
	 * @return list of coresspondences between given lists of schemas and mapping among their elements.
	 */
	List<ISchemaElementCorrespondence> matchSchemaElements(List<ISchemaElement> sourceElements, List<ISchemaElement> targetElements); 

	/** Returns the name of an algorithm that is employed for matching elements.
	 * @return an algorithms that is implemented in matcher.
	 */
	String getElementMatchingAlgorithm();
}
