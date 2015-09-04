package eu.trentorise.opendata.schemamatcher.services.experiment;

import static com.google.common.base.Preconditions.checkNotNull;
import static eu.trentorise.opendata.commons.validation.Preconditions.checkNotEmpty;
import eu.trentorise.opendata.semantics.services.IEkb;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Evaluate {

    private final static Logger LOG = LoggerFactory.getLogger(Evaluate.class.getName());
    
    public void run(IEkb ekb, String folderPath) {
        checkNotNull(ekb);
        checkNotEmpty(folderPath, "Invalid folder path!");
        
        File file = new File(folderPath);
        MatchingExperiment me = new MatchingExperiment(ekb);

        GroundTruthGeneration gtg = new GroundTruthGeneration(ekb);
        gtg.generateGT();

        IExperimentResult er1 = me.runExperiment(file, "Simple", "ConceptDistanceBased", gtg.getSchemaCorrespondenceGT());
        LOG.info("Experiment 1 - schema matcher: Simple; Element Matcher: concept distance based ");
        LOG.info("Correct detection: " + er1.getPrecision() + "Correct detection within first 5: " + er1.getPrecisionFive());
    }

}
