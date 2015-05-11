 package eu.trentorise.opendata.schemamatcher.services.experiment;

import java.io.File;
import java.io.IOException;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;

public class Evaluate {

	

	public static void main(String [] args) throws IOException, SchemaMatcherException{

		String folderPath = args[0];
		File file = new File(folderPath);
		MatchingExperiment me = new MatchingExperiment();

		GroundTruthGeneration gtg = new GroundTruthGeneration();
		gtg.generateGT();

		IExperimentResult er1  =me.runExperiment(file, "Simple", "ConceptDistanceBased", gtg.schemaCorrespondenceGT);
		System.out.println("Experiment 1 - schema matcher: Simple; Element Matcher: concept distance based ");
		System.out.println("Correct detection: "+er1.getPrecision()+ "Correct detection within first 5: "+er1.getPrecisionFive());

//		System.out.println("Correct detection: "+er2.getPrecision()+ "Correct detection within first 5: "+er2.getPrecisionFive());

//				IExperimentResult er3  =me.runExperiment(file, "HungarianAllocationAndEditDistance", "EditDistanceBased", gtg.schemaCorrespondenceGT);
//				System.out.println("Experiment 1 - schema matcher: Simple; Element Matcher: edit distance based ");
//				System.out.println("Correct detection: "+er3.getPrecision()+ "Correct detection within first 5: "+er3.getPrecisionFive());
//				
//				IExperimentResult  er4  =me.runExperiment(file, "HungarianAllocationAndEditDistance", "ConceptDistanceBased", gtg.schemaCorrespondenceGT);
//				System.out.println("Experiment 1 - schema matcher: HungarianAllocationAndEditDistance; Element Matcher: edit distance based ");
//				System.out.println("Correct detection: "+er4.getPrecision()+ "Correct detection within first 5: "+er4.getPrecisionFive());
		//
	}
}
