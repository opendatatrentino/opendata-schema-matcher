package eu.trentorise.schemamatcher.services.experiment;

import java.util.List;

import eu.trentorise.schemamatcher.model.ISchemaCorrespondence;

public interface IMatchingExperiment {

	
	/**  Methods run experiments on effectivness of a choosen approach and returns it results
	 * @param methodology of the experiment
	 * @return
	 */
	IExperimentResult runExperiment(String methodology, String approach, ISchemaCorrespondence groundTruth);
	
	/** Method provides the list of possible methodologies for running experiments
	 *  i.e. how experimental result should be computed
	 * @return possible methodologies that can be used for running experiments
	 */
	List<String> experimentMethodologies();

	
	/** Method provide list of approach that can be used for schema matching
	 * @return list of schema matching approaches
	 */
	List<String> matchingApproach();

	
}
