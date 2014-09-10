package eu.trentorise.schemamatcher.services.matching;

import java.util.List;

import eu.trentorise.schemamatcher.model.ISchemaElementMatcher;

public interface ISElementMatchingService {

	/** Method allows to get available schema element matching algorithms
	 * @return names of available element matching algorithms.
	 */
	public List<String> getElementMatchingAlgorithms();

	/** Method returns schema element matcher of a required algorithm type. 
	 * @param elementMatcherType the name of the schema element matcher algorithm
	 * @return schema element matcher
	 */
	public ISchemaElementMatcher getElementMatcher(String elementMatcherType);

}
