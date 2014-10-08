package eu.trentorise.opendata.schemamatcher.model;

import java.util.HashMap;

import eu.trentorise.opendata.schemamatcher.implementation.model.SchemaElement;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 22 Jul 2014
 * 
 */
public interface ISchemaElementCorrespondence {

	/** Methods returns the correspondence score between target and source schema element  from min to max - [0,1], 
	 * @returncorrespondence score between target and source schema elements.
	 */
	public float getElementCorrespondenceScore();

	/** Returns element TO which correspondence has been computed
	 * @return schema element
	 */
	public SchemaElement getTargetElement();


	/** Method allows to set schema element TO which correspondence is provided
	 * @param targetElement
	 */
	public void setTargetElement(ISchemaElement targetElement);


	/** Returns element FROM which correspondence has been computed
	 * @return schema element
	 */
	public SchemaElement getSourceElement();


	/** Method allows to set schema element FROM which correspondence is provided
	 * @param sourceSchemaElement
	 */
	public void setSourceElement(ISchemaElement sourceSchemaElement);

	/** Method allows to set scores that represents similarity between source and target elements. 
	 * Score is in the interval [0,1], where maximum and minimum similarities are '1' and '0' accordingly.  
	 * @param score
	 */
	public void setElementCorrespondenceScore(Float score);
	
	/** Method allows to get a correspondence of a source element to all the target elements with scores.
	 * @return map where key is a target schema element and value is the score between target and source. 
	 */
	public HashMap<ISchemaElement, Float> getElementMapping();

	/** Method allows to set a correspondence of a source element to all the target elements with scores.
	 * @param elementMapping  map where key is a target schema element and value is the score between target and source.
	 */
	public void setElementMapping(HashMap<ISchemaElement, Float> elementMapping);
	
}
