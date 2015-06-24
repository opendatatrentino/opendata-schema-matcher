package eu.trentorise.opendata.schemamatcher.implementation.service;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.columnrecognizers.ColumnConceptCandidate;
import eu.trentorise.opendata.columnrecognizers.ColumnRecognizer;
import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.NLPService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaMatcherFactory;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaMatcher;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;

public class TestSimpleSchemaMatcher {

    private static final int ORARI_CONCEPT_ID = 80505;
    private static final int NAME_CONCEPT_ID = 2;
    private static final double DELTA = 1e-6;
    private static final int CONCEPT_ID = 14557;
    private EntityType etype;

    @Before
    public void readEtype() {
        EntityTypeService ets = new EntityTypeService();
        String etypeUrl = WebServiceURLs.etypeIDToURL(12L);
        etype = (EntityType) ets.readEntityType(etypeUrl);
    }

    @Test
    public void testSchemaElementMatcher() throws IOException, SchemaMatcherException {
        SchemaImport si = new SchemaImport();

        File file = new File("impianti risalita.csv");

        ISchema schemaCSV = si.parseCSV(file);
        List<String> nlpInput = new ArrayList<String>();
        nlpInput.add(schemaCSV.getSchemaElements().get(0).getElementContext().getElementName());
        nlpInput.add(schemaCSV.getSchemaElements().get(1).getElementContext().getElementName());

        Locale locale = NLPService.detectLanguage(nlpInput);
        ISchema schemaEtype = si.extractSchema(etype, locale);

        ISchemaMatcher schemaMatcher = SchemaMatcherFactory.create("Simple");
        ISchemaCorrespondence schemaCor = schemaMatcher.matchSchemas(schemaCSV, schemaEtype, "EditDistanceBased");
        assertEquals(schemaCor.getTargetSchema().getSchemaName(), "Infrastruttura");
        assertEquals(schemaCor.getSchemaCorrespondenceScore(), 0.6204213, DELTA);
    }

    @Test
    public void testConceptFromText() {
        String resourceName = "IMPIANTI RISALITA";
        Long conceptID = ColumnRecognizer.conceptFromText(resourceName);
        assertEquals(CONCEPT_ID, conceptID.intValue());
    }

    @Test
    public void testConsistanceOfColConcRecognizer() throws IOException {
        SchemaImport si = new SchemaImport();

        File file = new File("impianti risalita.csv");

        ISchema schemaCSV = si.parseCSV(file);
        List<ISchema> sourceSchemas = new ArrayList<ISchema>();
        sourceSchemas.add(schemaCSV);

        List<ISchemaElement> schemaElements = schemaCSV.getSchemaElements();
        List<String> elementNames = new ArrayList<String>();
        List<List<String>> elementContent = new ArrayList<List<String>>();

        HashMap<Integer, String> map = new HashMap<Integer, String>();
        int z = 0;
        for (ISchemaElement element : schemaElements) {
            z++;
            map.put(z, element.getElementContext().getElementName());
            elementNames.add(element.getElementContext().getElementName());
            List<Object> content = element.getElementContent().getContent();
            List<String> contStr = new ArrayList<String>();
            for (Object o : content) {
                contStr.add(o.toString());
            }
            elementContent.add(contStr);
        }
        List<ColumnConceptCandidate> extractedConcepts = new ArrayList<ColumnConceptCandidate>();
        extractedConcepts
                = ColumnRecognizer.computeScoredCandidates(elementNames, elementContent);

        for (ColumnConceptCandidate ccc : extractedConcepts) {

            if (map.get(ccc.getColumnNumber()).equalsIgnoreCase("nome")) {
                assertEquals(ccc.getConceptID(), NAME_CONCEPT_ID);
            }
            if (map.get(ccc.getColumnNumber()).equalsIgnoreCase("orari")) {
                assertEquals(ccc.getConceptID(), ORARI_CONCEPT_ID);
            }
        }
    }

}
