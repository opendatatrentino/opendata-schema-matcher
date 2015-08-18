package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.disiclient.model.entity.EntityType;
import eu.trentorise.opendata.schemamatcher.model.ISchema;
import eu.trentorise.opendata.schemamatcher.model.ISchemaCorrespondence;
import eu.trentorise.opendata.schemamatcher.model.ISchemaElementCorrespondence;
import eu.trentorise.opendata.schemamatcher.odr.impl.AtrCorrespondence;
import eu.trentorise.opendata.schemamatcher.odr.impl.ScheCorrespondence;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import eu.trentorise.opendata.semantics.services.model.IAttributeCorrespondence;

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
    private IEntityType etype;
    private ISchema targetSchema;
    private float score;
    private List<ISchemaElementCorrespondence> elementCorrespondences;

    public SchemaCorrespondence(ISchema sourceSchema, IEntityType etype,
            ISchema targetSchema, float score,
            List<ISchemaElementCorrespondence> elementCorrespondences) {
        super();
        this.sourceSchema = sourceSchema;
        this.etype = etype;
        this.targetSchema = targetSchema;
        this.score = score;
        this.elementCorrespondences = elementCorrespondences;
    }

    public SchemaCorrespondence() {

    }

    @Override
    public String toString() {
        return "SchemaCorrespondence [sourceSchema=" + sourceSchema
                + ", targetSchema=" + targetSchema + ", score=" + score
                + ", attributeCorrespondences=" + elementCorrespondences
                + "]";
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public void setSchemaElementCorrespondence(
            List<ISchemaElementCorrespondence> elementCorrespondences) {
        Preconditions.checkNotNull(elementCorrespondences);

        this.elementCorrespondences = elementCorrespondences;
    }

    public void setSourceSchema(ISchema sourceSchema) {
        Preconditions.checkNotNull(sourceSchema);
        this.sourceSchema = sourceSchema;
    }

    public void setTargetSchema(ISchema targetSchema) {
        Preconditions.checkNotNull(targetSchema);

        this.targetSchema = targetSchema;
    }

    public void setScore(float score) {
        Preconditions.checkNotNull(score);

        this.score = score;
    }

    public void setElementCorrespondences(
            List<ISchemaElementCorrespondence> elementCorrespondences) {
        Preconditions.checkNotNull(elementCorrespondences);
        this.elementCorrespondences = elementCorrespondences;
    }

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

    public ScheCorrespondence convertToScheCorrespondence() {
        ScheCorrespondence sc = new ScheCorrespondence();
        sc.setScore(this.score);

        if (this.etype == null) {
            Schema s = (Schema) this.targetSchema;
            sc.setEtype((EntityType) s.getEtype());
0        } else {
            sc.setEtype((EntityType) this.etype);
        }
        List<IAttributeCorrespondence> atrCors = new ArrayList<IAttributeCorrespondence>();
        List<ISchemaElementCorrespondence> elCors = this.elementCorrespondences;
        for (ISchemaElementCorrespondence elCor : elCors) {
            SchemaElementCorrespondence el = (SchemaElementCorrespondence) elCor;
            AtrCorrespondence a = el.convertToACorrespondence();
            atrCors.add(a);
        }
        sc.setAttributeCorrespondence(atrCors);
        return sc;
    }
}
