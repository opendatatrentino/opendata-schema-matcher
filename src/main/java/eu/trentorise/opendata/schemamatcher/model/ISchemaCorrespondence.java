package eu.trentorise.opendata.schemamatcher.model;

import java.util.List;





/**
 * The class stands for description of correspondence between two schemas
 *
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 22 Jul 2014
 *
 */
public interface ISchemaCorrespondence {

    /**
     * Returna correspondences between two schema elements.
     *
     * @return correspondences between source and target schema elements
     */
    List<ISchemaElementCorrespondence> getSchemaElementCorrespondence();

    /**
     * Method returns a source schema from which we trying to find mapping (in
     * ODR it is an schema of input data resource )
     *
     * @return source schema
     */
    ISchema getSourceSchema();

    /**
     * Method returns a target schema to which we trying to find mapping (in ODR
     * it is a target EType)
     *
     * @return target schema
     */
    ISchema getTargetSchema();

    /**
     * Return score [0,1] that shows the closeness between two given
     * schemas. The higher the score is, the more similar two schemas are.
     *
     * @return score that represents correspondences between target and source
     * schemas
     */
    float getSchemaCorrespondenceScore();

    /**
     * Sets a source schema from which we trying to find mapping
     * (in ODR it is an schema of input data resource )
     *
     * @param sourceSchema
     */
    public void setSourceSchema(ISchema sourceSchema);

    /**
     * Sets a target schema to which we trying to find mapping
     * (in ODR it is a target EType)
     *
     * @param targetSchema
     */
    public void setTargetSchema(ISchema targetSchema);

    /**
     * Sets a score [0,1] that shows the closeness between two
     * given schemas. The higher the score is, the more similar two schemas are.
     *
     * @param score
     */
    public void setScore(float score);

    /**
     * Sets correspondences between schema elements.
     *
     * @param elementCorrespondences
     */
    public void setElementCorrespondences(List<ISchemaElementCorrespondence> elementCorrespondences);
}
