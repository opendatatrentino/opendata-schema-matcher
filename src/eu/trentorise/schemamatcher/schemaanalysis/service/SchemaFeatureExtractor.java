package eu.trentorise.schemamatcher.schemaanalysis.service;

import eu.trentorise.schemamatcher.model.ISchema;
import eu.trentorise.schemamatcher.schemaanalysis.model.ISchemaFeature;

/** COntains feature extractors 
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 22 Jul 2014
 * 
 */
public interface SchemaFeatureExtractor {

	ISchemaFeature extractSchemaFeature(ISchema schema);
	
	
	
}
