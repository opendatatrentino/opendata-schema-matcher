package eu.trentorise.opendata.schemamatcher.services.experiment;

import java.io.File;
import java.util.List;

import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;

public interface IMatchingExperiment {

    /**
     * Methods run experiments on effectivness of a choosen approach and returns
     * it results
     *
     * @param file folder that contains source schemas
     * @param methodology is the parameter that defines how the experimental
     * result will be counted
     * @param approach schema matching approach
     * @param groundTruth ground truth for the schemas
     * @return experimental results
     */
    IExperimentResult runExperiment(File file, String methodology, String approach, List<ISchemaCorrespondence> groundTruth);

    /**
     * Method provides the list of possible methodologies for running
     * experiments i.e. how experimental result should be computed
     *
     * @return possible methodologies that can be used for running experiments
     */
    List<String> experimentMethodologies();

    /**
     * Method provide list of approach that can be used for schema matching
     *
     * @return list of schema matching approaches
     */
    List<String> matchingApproach();

}
