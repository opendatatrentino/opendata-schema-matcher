package eu.trentorise.opendata.schemamatcher.services.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

public class MatchingExperiment implements IMatchingExperiment {

    private final static Logger LOGGER = Logger.getLogger(MatchingExperiment.class.getName());

    public IExperimentResult runExperiment(File file, String methodology, String approach,
            List<ISchemaCorrespondence> groundTruth) {
        List<ISchema> sourceSchemas = null;
        try {
            sourceSchemas = getAllSourceSchemas(file);
        }
        catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
        List<ISchema> targetSchemas = null;
        try {
            targetSchemas = getAllTargetSchemas();
        }
        catch (SchemaMatcherException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create(methodology);
        ExperimentResult experimentResults = new ExperimentResult();
        int firstAppearance = 0;
        int withinFiveAppearance = 0;
        int numberOfMappedAttributes = 0;
        int correctlyMappedAttribute = 0;
        for (ISchema sourceSchema : sourceSchemas) {
            List<ISchema> source = new ArrayList<ISchema>();
            source.add(sourceSchema);
            List<ISchemaCorrespondence> schemaCorespondencesResult = new ArrayList<ISchemaCorrespondence>();
            schemaCorespondencesResult = schemaMatcher.matchSchemas(source, targetSchemas, approach);
            for (ISchemaCorrespondence gtSchema : groundTruth) {
                if (gtSchema.getSourceSchema().getSchemaName().equals(schemaCorespondencesResult.get(0).getSourceSchema().getSchemaName())) {
                    if (gtSchema.getTargetSchema().getSchemaName().equals(schemaCorespondencesResult.get(0).getTargetSchema().getSchemaName())) {
                        firstAppearance++;
                        withinFiveAppearance++;
                        continue;
                    }

                }

                for (int i = 1; i < 5; i++) {
                    if (gtSchema.getSourceSchema().getSchemaName().equals(schemaCorespondencesResult.get(i).getSourceSchema().getSchemaName())) {
                        if (gtSchema.getTargetSchema().getSchemaName().equals(schemaCorespondencesResult.get(i).getTargetSchema().getSchemaName())) {
                            withinFiveAppearance++;
                        }
                    }
                }
            }

            for (ISchemaCorrespondence gtSchema : groundTruth) {
                List<ISchemaElementCorrespondence> gtCorrs = gtSchema.getSchemaElementCorrespondence();
                if (gtSchema.getSourceSchema().getSchemaName().equals(schemaCorespondencesResult.get(0).getSourceSchema().getSchemaName())) {
                    for (ISchemaCorrespondence resultSchema : schemaCorespondencesResult) {
                        if (gtSchema.getTargetSchema().getSchemaName().equals(resultSchema.getTargetSchema().getSchemaName())) {
                            List<ISchemaElementCorrespondence> elCorGt = gtSchema.getSchemaElementCorrespondence();
                            List<ISchemaElementCorrespondence> elCorRes = resultSchema.getSchemaElementCorrespondence();
                            for (ISchemaElementCorrespondence elGt : elCorGt) {
                                if (elGt.getTargetElement() != null) {
                                    for (ISchemaElementCorrespondence elRes : elCorRes) {
                                        if (elGt.getSourceElement().getElementContext().getElementName().equals(elRes.getSourceElement().getElementContext().getElementName())) {
                                            LOGGER.info("elGtSource: " + elGt.getSourceElement().getElementContext().getElementName());
                                            LOGGER.info("elGtTarget: " + elGt.getTargetElement().getElementContext().getElementName());
                                            LOGGER.info("elResSource: " + elRes.getSourceElement().getElementContext().getElementName());
                                            LOGGER.info("elResTarget: " + elRes.getTargetElement().getElementContext().getElementName());
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
                    if (gtSchema.getTargetSchema().getSchemaName().equals(resultSchema.getSourceSchema().getSchemaName())) {
                        List<ISchemaElementCorrespondence> resCorrs = resultSchema.getSchemaElementCorrespondence();
                        for (ISchemaElementCorrespondence rsel : resCorrs) {
                            for (ISchemaElementCorrespondence sel : gtCorrs) {
                                if (sel.getTargetElement() != null) {
                                    LOGGER.info("source: " + sel.getSourceElement().getElementContext().getElementName()
                                            + " target: " + sel.getTargetElement().getElementContext().getElementName());
                                }
                            }
                        }
                    }
                }
            }
            LOGGER.info("Source name:" + schemaCorespondencesResult.get(0).getSourceSchema().getSchemaName());
            LOGGER.info("Target name:" + schemaCorespondencesResult.get(0).getTargetSchema().getSchemaName());
            LOGGER.info("Score: " + schemaCorespondencesResult.get(0).getSchemaCorrespondenceScore());

            LOGGER.info("Target name:" + schemaCorespondencesResult.get(1).getTargetSchema().getSchemaName());
            LOGGER.info("Score: " + schemaCorespondencesResult.get(1).getSchemaCorrespondenceScore());

            LOGGER.info("Target name:" + schemaCorespondencesResult.get(2).getTargetSchema().getSchemaName());
            LOGGER.info("Score: " + schemaCorespondencesResult.get(2).getSchemaCorrespondenceScore());

            LOGGER.info("Target name:" + schemaCorespondencesResult.get(3).getTargetSchema().getSchemaName());
            LOGGER.info("Score: " + schemaCorespondencesResult.get(3).getSchemaCorrespondenceScore());

            LOGGER.info("Target name:" + schemaCorespondencesResult.get(4).getTargetSchema().getSchemaName());
            LOGGER.info("Appearance: " + firstAppearance);

            LOGGER.info("___________");

        }
        int datasetSize = groundTruth.size();
        LOGGER.info("Appearance: " + firstAppearance);
        LOGGER.info("Within 5: " + withinFiveAppearance);
        LOGGER.info("Number of Mapped attribute in GT: " + numberOfMappedAttributes);
        LOGGER.info("Correctly mapped Attributes: " + correctlyMappedAttribute);
        float attrRate = (float) correctlyMappedAttribute / (float) numberOfMappedAttributes;
        LOGGER.info("Correctly mapped Attributes rates: " + attrRate);

        double precisionFive = (double) withinFiveAppearance / (double) datasetSize;
        double precision = (double) firstAppearance / (double) datasetSize;
        experimentResults.setPrecisionFive(precisionFive);
        experimentResults.setPrecision(precision);
        return experimentResults;
    }

    public List<String> experimentMethodologies() {
        return null;
    }

    public List<String> matchingApproach() {
        return null;
    }

    public static List<ISchema> getAllSourceSchemas(final File folder) throws IOException {
        List<ISchema> schemas = new ArrayList<ISchema>();
        SchemaImport si = new SchemaImport();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                getAllSourceSchemas(fileEntry);
            } else {
                schemas.add(si.parseCSV(fileEntry));
            }
        }
        return schemas;
    }

    public static List<ISchema> getAllTargetSchemas() throws SchemaMatcherException {
        EntityTypeService etypeService = new EntityTypeService();
        List<IEntityType> etypeList = etypeService.getAllEntityTypes();
        List<ISchema> targetSchemas = new ArrayList<ISchema>();
        SchemaImport si = new SchemaImport();

        for (IEntityType etype : etypeList) {
            ISchema schemaEtype = si.extractSchema(etype, Locale.ITALIAN);
            targetSchemas.add(schemaEtype);
        }
        return targetSchemas;
    }

}
