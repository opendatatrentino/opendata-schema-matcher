package eu.trentorise.opendata.schemamatcher.services.experiment;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.IEkb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchingExperiment implements IMatchingExperiment {

    private final static Logger LOG = LoggerFactory.getLogger(MatchingExperiment.class.getName());

    private IEkb ekb;
    
    public MatchingExperiment(IEkb ekb) {
        Preconditions.checkNotNull(ekb);
        this.ekb = ekb;    
    }

    
    
    @Override
    public IExperimentResult runExperiment(File file, String methodology, String approach,
            List<ISchemaCorrespondence> groundTruth) {
        List<ISchema> sourceSchemas = null;
        try {
            sourceSchemas = getAllSourceSchemas(file);
        }
        catch (IOException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        List<ISchema> targetSchemas = null;
        try {
            targetSchemas = getAllTargetSchemas();
        }
        catch (SchemaMatcherException e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
        }

        ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create(methodology);
        ExperimentResult experimentResults = new ExperimentResult();
        int firstAppearance = 0;
        int withinFiveAppearance = 0;
        int numberOfMappedAttributes = 0;
        int correctlyMappedAttribute = 0;
        for (ISchema sourceSchema : sourceSchemas) {
            List<ISchema> source = new ArrayList();
            source.add(sourceSchema);
            List<ISchemaCorrespondence> schemaCorespondencesResult = new ArrayList();
            schemaCorespondencesResult = schemaMatcher.matchSchemas(source, targetSchemas, approach);
            for (ISchemaCorrespondence gtSchema : groundTruth) {
                if (gtSchema.getSourceSchema().getName().equals(schemaCorespondencesResult.get(0).getSourceSchema().getName())) {
                    if (gtSchema.getTargetSchema().getName().equals(schemaCorespondencesResult.get(0).getTargetSchema().getName())) {
                        firstAppearance++;
                        withinFiveAppearance++;
                        continue;
                    }

                }

                for (int i = 1; i < 5; i++) {
                    if (gtSchema.getSourceSchema().getName().equals(schemaCorespondencesResult.get(i).getSourceSchema().getName())) {
                        if (gtSchema.getTargetSchema().getName().equals(schemaCorespondencesResult.get(i).getTargetSchema().getName())) {
                            withinFiveAppearance++;
                        }
                    }
                }
            }

            for (ISchemaCorrespondence gtSchema : groundTruth) {
                List<ISchemaElementCorrespondence> gtCorrs = gtSchema.getSchemaElementCorrespondence();
                if (gtSchema.getSourceSchema().getName().equals(schemaCorespondencesResult.get(0).getSourceSchema().getName())) {
                    for (ISchemaCorrespondence resultSchema : schemaCorespondencesResult) {
                        if (gtSchema.getTargetSchema().getName().equals(resultSchema.getTargetSchema().getName())) {
                            List<ISchemaElementCorrespondence> elCorGt = gtSchema.getSchemaElementCorrespondence();
                            List<ISchemaElementCorrespondence> elCorRes = resultSchema.getSchemaElementCorrespondence();
                            for (ISchemaElementCorrespondence elGt : elCorGt) {
                                if (elGt.getTargetElement() != null) {
                                    for (ISchemaElementCorrespondence elRes : elCorRes) {
                                        if (elGt.getSourceElement().getElementContext().getElementName().equals(elRes.getSourceElement().getElementContext().getElementName())) {
                                            LOG.info("elGtSource: " + elGt.getSourceElement().getElementContext().getElementName());
                                            LOG.info("elGtTarget: " + elGt.getTargetElement().getElementContext().getElementName());
                                            LOG.info("elResSource: " + elRes.getSourceElement().getElementContext().getElementName());
                                            LOG.info("elResTarget: " + elRes.getTargetElement().getElementContext().getElementName());
                                            if (elGt.getTargetElement().getElementContext().getElementName().equals(elRes.getTargetElement().getElementContext().getElementName())) {
                                                {
                                                    correctlyMappedAttribute++;
                                                }
                                            }
                                        }
                                    }
                                    numberOfMappedAttributes++;
                                }
                            }
                        }
                    }
                }
                for (ISchemaCorrespondence resultSchema : schemaCorespondencesResult) {
                    if (gtSchema.getTargetSchema().getName().equals(resultSchema.getSourceSchema().getName())) {
                        List<ISchemaElementCorrespondence> resCorrs = resultSchema.getSchemaElementCorrespondence();
                        for (ISchemaElementCorrespondence rsel : resCorrs) {
                            for (ISchemaElementCorrespondence sel : gtCorrs) {
                                if (sel.getTargetElement() != null) {
                                    LOG.info("source: " + sel.getSourceElement().getElementContext().getElementName()
                                            + " target: " + sel.getTargetElement().getElementContext().getElementName());
                                }
                            }
                        }
                    }
                }
            }
            LOG.info("Source name:" + schemaCorespondencesResult.get(0).getSourceSchema().getName());
            LOG.info("Target name:" + schemaCorespondencesResult.get(0).getTargetSchema().getName());
            LOG.info("Score: " + schemaCorespondencesResult.get(0).getSchemaCorrespondenceScore());

            LOG.info("Target name:" + schemaCorespondencesResult.get(1).getTargetSchema().getName());
            LOG.info("Score: " + schemaCorespondencesResult.get(1).getSchemaCorrespondenceScore());

            LOG.info("Target name:" + schemaCorespondencesResult.get(2).getTargetSchema().getName());
            LOG.info("Score: " + schemaCorespondencesResult.get(2).getSchemaCorrespondenceScore());

            LOG.info("Target name:" + schemaCorespondencesResult.get(3).getTargetSchema().getName());
            LOG.info("Score: " + schemaCorespondencesResult.get(3).getSchemaCorrespondenceScore());

            LOG.info("Target name:" + schemaCorespondencesResult.get(4).getTargetSchema().getName());
            LOG.info("Appearance: " + firstAppearance);

            LOG.info("___________");

        }
        int datasetSize = groundTruth.size();
        LOG.info("Appearance: " + firstAppearance);
        LOG.info("Within 5: " + withinFiveAppearance);
        LOG.info("Number of Mapped attribute in GT: " + numberOfMappedAttributes);
        LOG.info("Correctly mapped Attributes: " + correctlyMappedAttribute);
        float attrRate = (float) correctlyMappedAttribute / (float) numberOfMappedAttributes;
        LOG.info("Correctly mapped Attributes rates: " + attrRate);

        double precisionFive = (double) withinFiveAppearance / (double) datasetSize;
        double precision = (double) firstAppearance / (double) datasetSize;
        experimentResults.setPrecisionFive(precisionFive);
        experimentResults.setPrecision(precision);
        return experimentResults;
    }

    @Override
    public List<String> experimentMethodologies() {
        return null;
    }

    @Override
    public List<String> matchingApproach() {
        return null;
    }

    public List<ISchema> getAllSourceSchemas(final File folder) throws IOException {
        List<ISchema> schemas = new ArrayList();
        SchemaImport si = new SchemaImport(ekb);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                getAllSourceSchemas(fileEntry);
            } else {
                schemas.add(si.parseCSV(fileEntry));
            }
        }
        return schemas;
    }

    public List<ISchema> getAllTargetSchemas() throws SchemaMatcherException {
        
        List<IEntityType> etypeList = ekb.getEntityTypeService().readAllEntityTypes();
        List<ISchema> targetSchemas = new ArrayList();
        SchemaImport si = new SchemaImport(ekb);

        for (IEntityType etype : etypeList) {
            ISchema schemaEtype = si.extractSchema(etype, Locale.ITALIAN);
            targetSchemas.add(schemaEtype);
        }
        return targetSchemas;
    }

}
