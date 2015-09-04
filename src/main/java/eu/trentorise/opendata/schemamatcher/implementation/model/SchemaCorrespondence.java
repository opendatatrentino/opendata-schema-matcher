package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.List;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;


/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * 
 */
public class SchemaCorrespondence implements ISchemaCorrespondence {

    /**
     * Scores are considered equals up to this margin
     */
    static public final double SCORE_TOLERANCE = 0.01;

    private ISchema sourceSchema;    
    private ISchema targetSchema;
    private float score;
    private List<ISchemaElementCorrespondence> elementCorrespondences;  

    public SchemaCorrespondence() {

    }

    @Override
    public String toString() {
        return "SchemaCorrespondence [sourceSchema=" + sourceSchema
                + ", targetSchema=" + targetSchema + ", score=" + score
                + ", attributeCorrespondences=" + elementCorrespondences
                + "]";
    }

    @Override
    public void setScore(float score) {
        this.score = score;
    }

    public void setSchemaElementCorrespondence(
            List<ISchemaElementCorrespondence> elementCorrespondences) {
        Preconditions.checkNotNull(elementCorrespondences);

        this.elementCorrespondences = elementCorrespondences;
    }

    @Override
    public void setSourceSchema(ISchema sourceSchema) {
        Preconditions.checkNotNull(sourceSchema);
        this.sourceSchema = sourceSchema;
    }

    @Override
    public void setTargetSchema(ISchema targetSchema) {
        Preconditions.checkNotNull(targetSchema);
        this.targetSchema = targetSchema;
    }

    @Override
    public void setElementCorrespondences(
            List<ISchemaElementCorrespondence> elementCorrespondences) {
        Preconditions.checkNotNull(elementCorrespondences);
        this.elementCorrespondences = elementCorrespondences;
    }

    @Override
    public List<ISchemaElementCorrespondence> getSchemaElementCorrespondence() {
        return elementCorrespondences;
    }

    @Override
    public ISchema getSourceSchema() {
        return this.sourceSchema;
    }

    @Override
    public ISchema getTargetSchema() {
        return this.targetSchema;
    }

    @Override
    public float getSchemaCorrespondenceScore() {
        return score;
    }

    
}
