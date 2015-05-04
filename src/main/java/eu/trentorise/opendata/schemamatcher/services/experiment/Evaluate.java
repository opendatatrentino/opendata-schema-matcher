package eu.trentorise.opendata.schemamatcher.services.experiment;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;

public class Evaluate {
	private final static Logger LOGGER = Logger.getLogger(Evaluate.class.getName());
	
	public static void main(String [] args) throws IOException, SchemaMatcherException{

		String folderPath = args[0];
		File file = new File(folderPath);
		MatchingExperiment me = new MatchingExperiment();

		GroundTruthGeneration gtg = new GroundTruthGeneration();
		gtg.generateGT();

		IExperimentResult er1  =me.runExperiment(file, "Simple", "ConceptDistanceBased", gtg.schemaCorrespondenceGT);
		LOGGER.info("Experiment 1 - schema matcher: Simple; Element Matcher: concept distance based ");
		LOGGER.info("Correct detection: "+er1.getPrecision()+ "Correct detection within first 5: "+er1.getPrecisionFive());
	}
}
