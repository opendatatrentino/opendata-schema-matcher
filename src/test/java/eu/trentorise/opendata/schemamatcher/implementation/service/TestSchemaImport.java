package eu.trentorise.opendata.schemamatcher.implementation.service;

import static org.junit.Assert.*;
import it.unitn.disi.sweb.webapi.model.eb.Instance;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.disiclient.services.EntityTypeService;
import eu.trentorise.opendata.disiclient.services.WebServiceURLs;
import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.implementation.services.SchemaImport;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElement;

/**
 * Test class for SchemaImport services
 *
 * @author Ivan Tankoyeu
 *
 */
public class TestSchemaImport {

    private static final int INSTANCES = 100;
    private static final String FACILITY = "Facility";
    private final static Logger LOGGER = Logger.getLogger(TestSchemaImport.class.getName());
    private EntityType etype;

    @Before
    public void readEtype() {
        EntityTypeService ets = new EntityTypeService();
        String etypeUrl = WebServiceURLs.etypeIDToURL(12L);
        etype = (EntityType) ets.readEntityType(etypeUrl);
    }

    @Test
    public void testSchemaImportEtype() throws SchemaMatcherException {

        SchemaImport si = new SchemaImport();
        ISchema schema = si.extractSchema(etype, Locale.ENGLISH);
        LOGGER.info(schema.toString());
        assertEquals(schema.getSchemaName(), FACILITY);
    }

    @Test
    public void testSchemaImportCSV() throws SchemaMatcherException, IOException {
        SchemaImport si = new SchemaImport();
        File file = new File("impianti risalita.csv");
        ISchema schemaOut = si.parseCSV(file);
        assertNotNull(schemaOut);
    }

    @Test
    public void testSchemaImportEntities() throws SchemaMatcherException {

        SchemaImport si = new SchemaImport();
        List<Instance> instances = si.getEntities(etype);
        assertEquals(instances.size(), INSTANCES);
    }

}
