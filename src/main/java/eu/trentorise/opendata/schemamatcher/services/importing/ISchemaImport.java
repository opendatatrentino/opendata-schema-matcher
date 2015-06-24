package eu.trentorise.opendata.schemamatcher.services.importing;

import java.io.File;
import java.util.Locale;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaMatcherException;
import eu.trentorise.opendata.schemamatcher.model.ISchema;

/**
 * Import schema from different data formats
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 22 Jul 2014
 *
 */
public interface ISchemaImport {

    /**
     * Method extracts schema from a given file
     *
     * @param input file
     * @return schema
     * @throws SchemaMatcherException
     */
    public ISchema extractSchema(File file) throws SchemaMatcherException;

    /**
     * Method extracts schema from a given object
     *
     * @param input schema
     * @param locale language in which schema is described
     * @return schema
     * @throws SchemaMatcherException
     */
    public ISchema extractSchema(Object schema, Locale locale) throws SchemaMatcherException;

}
