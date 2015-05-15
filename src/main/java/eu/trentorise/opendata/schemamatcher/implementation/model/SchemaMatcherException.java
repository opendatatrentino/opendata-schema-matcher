package eu.trentorise.opendata.schemamatcher.implementation.model;

public class SchemaMatcherException extends Exception {


    public SchemaMatcherException(String s){
        super(s);
    }

    public SchemaMatcherException(String s, Exception ex) {
        super(s, ex);
    }

    public SchemaMatcherException(Exception ex) {
        super(ex);
    }

}
