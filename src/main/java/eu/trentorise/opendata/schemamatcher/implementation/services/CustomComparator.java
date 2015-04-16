package eu.trentorise.opendata.schemamatcher.implementation.services;

import java.util.Comparator;

import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;

/** Compares two schema elements by their correspondence score
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * 
 * 
 */

	public class CustomComparator implements Comparator<ISchemaCorrespondence> {
	    public int compare(ISchemaCorrespondence sc1, ISchemaCorrespondence sc2) {
	    	Float score1 = sc1.getSchemaCorrespondenceScore();
	    	Float score2 = sc2.getSchemaCorrespondenceScore();
	        return score2.compareTo(score1);
	    }
	}