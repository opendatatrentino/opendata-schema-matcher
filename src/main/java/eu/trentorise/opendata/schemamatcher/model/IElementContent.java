/**
 * 
 */
package eu.trentorise.opendata.schemamatcher.model;

import java.util.List;

/** The class stands for instances/content of a schema element
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 14 Jul 2014
 * 
 */
public interface IElementContent {

	/**	 For example in case of table returns all cells( instances) for a given column (element) 
	 * @return the list of element instances. 
	 */
	public	List<Object> getContent();

	/** Returns the number of objects that will be returned for a given object
	 * @return the number of instances for a given element
	 */
	public	int getContentSize();

	/** Assign the list of instances to the element content
	 * @param instances
	 */
	public	void  setContent(List<Object> instances);
	
	/** Assign the number of the instances for the element
	 * @param n
	 */
	public void setContentSize(int n);

}
 