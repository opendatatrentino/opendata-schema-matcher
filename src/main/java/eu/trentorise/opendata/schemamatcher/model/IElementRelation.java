package eu.trentorise.opendata.schemamatcher.model;


public interface IElementRelation {

	/** Method returns relation between schema elements
	 * @return relation between element
	 */
	public String getRelation();
	
	/** Method allows to assign relation between two schema elements
	 * @param relation between schema elements
	 */
	public void setRelation(String relation);
	
	/** Method returns target schema element towards which relation is assigned 
	 * @return schema element
	 */
	public ISchemaElement getRelatedElement();
	
	/** Method allows to assign target schema element
	 * @param relatedElement related schema element
	 */
	public void setRelatedElement(ISchemaElement relatedElement);

}
