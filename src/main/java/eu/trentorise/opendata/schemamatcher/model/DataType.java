package eu.trentorise.opendata.schemamatcher.model;

public final class  DataType {

	public static final String STRING = "string";
	public static final String BOOLEAN = "boolean";
	public static final String DATE = "dateTime";
	public static final String INTEGER = "int";
	public static final String FLOAT = "float";
	public static final String NLSTRING = "NLString";
	public static final String DOUBLE = "double";
	public static final String LONG = "long";
	public static final String BIGINT = "bigint";


	public enum Datatype {
		INT, FLOAT, DATE, STRING, NL_STRING, BOOLEAN, UNRECOGNIZED ;
	}
	
	
	/** Method takes as input string and returns data type defined in the matcher.
	 *@see Datatype
	 * @param st
	 * @return
	 */
	public static Datatype getDataType(String st){

		if ((st.equalsIgnoreCase(NLSTRING))){
			return Datatype.NL_STRING;
		} else 
			if ((st.equalsIgnoreCase(STRING))){
				return Datatype.STRING;
			} else 
				if ((st.equalsIgnoreCase(INTEGER))||(st.equalsIgnoreCase(LONG))||(st.equalsIgnoreCase(BIGINT))){
					return Datatype.INT;
				}  else 
					if ((st.equalsIgnoreCase(DATE))){
						return Datatype.DATE;
					}else 
						if ((st.equalsIgnoreCase(FLOAT))||(st.equalsIgnoreCase(DOUBLE))){
							return Datatype.FLOAT;
						} else 
							if ((st.equalsIgnoreCase(BOOLEAN))){
								return Datatype.BOOLEAN;
							}
							else return Datatype.UNRECOGNIZED;
	}

	//public float getDataTypeSimilarityScore

}

