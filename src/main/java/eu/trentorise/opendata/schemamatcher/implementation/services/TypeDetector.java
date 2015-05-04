package eu.trentorise.opendata.schemamatcher.implementation.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import eu.trentorise.opendata.columnrecognizers.Column;
import eu.trentorise.opendata.nlprise.DataTypeGuess.Datatype;
import eu.trentorise.opendata.nlprise.typecheckers.EmptyTypeChecker;
import eu.trentorise.opendata.nlprise.typecheckers.FloatTypeChecker;
import eu.trentorise.opendata.nlprise.typecheckers.IntTypeChecker;
import eu.trentorise.opendata.nlprise.typecheckers.JsonTypeChecker;
import eu.trentorise.opendata.nlprise.typecheckers.ListTypeChecker;
import eu.trentorise.opendata.nlprise.typecheckers.XmlTypeChecker;

/**
 * @author Simon
 *
 */
public class TypeDetector {
	/** 
	 * Minimal fraction of cells that need to match a datatype
	 */
	private static final double CONFIDENCE_THRESHOLD = 0.9;

	/**
	 * Guesses the type of an individual cell.
	 * 
	 * @param cell	The contents of the cell
	 * @return		The datatype
	 */
	public static Datatype guessType(String cell) {
		
		if (EmptyTypeChecker.check(cell)) {
			return Datatype.EMPTY;
		}
		if (IntTypeChecker.check(cell)) {
			return Datatype.INT;
		}
		if (FloatTypeChecker.check(cell)) {
			return Datatype.FLOAT;
		}
        if (XmlTypeChecker.check(cell)) {
            return Datatype.XML;
        }
        if (JsonTypeChecker.check(cell)) {
        return Datatype.JSON;
        }
        if (ListTypeChecker.check(cell)) {
            return Datatype.LIST;
        }
        if (cell.length() > 20 && cell.contains(" ")) {
            return Datatype.NL_STRING;
        }
		return Datatype.STRING;
	}
	
	/**
	 * Guesses the type of a column.
	 * 
	 * @param column	The column
	 * @return			The datatype
	 */
	public static Datatype guessType(Column column) {
		int rowCount = column.size();
		int requiredMatchCount = (int)Math.ceil(CONFIDENCE_THRESHOLD * rowCount);
		Map<Datatype, Integer> matchCounts = new HashMap<Datatype, Integer>();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			Datatype cellType = guessType(column.getFieldAt(rowIndex));
			if (matchCounts.containsKey(cellType)) {
				matchCounts.put(cellType, matchCounts.get(cellType) + 1);
			} else {
				matchCounts.put(cellType, 1);
			}
		}
		Datatype lastType = Datatype.STRING;
		boolean foundType = false;
		Iterator<Datatype> it = matchCounts.keySet().iterator();
		while (!foundType && it.hasNext()) {
			lastType = it.next();
			foundType = matchCounts.get(lastType) >= requiredMatchCount;
		}
		return foundType ? lastType : Datatype.STRING;		
	}
}
