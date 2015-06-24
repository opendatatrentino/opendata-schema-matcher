package eu.trentorise.opendata.schemamatcher.schemaanalysis.service;

import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.schemaanalysis.model.ISchemaFeature;

/**
 * COntains feature extractors
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 22 Jul 2014
 *
 */
public interface SchemaFeatureExtractor {

    ISchemaFeature extractSchemaFeature(ISchema schema);

}
