package eu.trentorise.opendata.schemamatcher.model;

import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;

/**
 * Contains resource from ODR that describes schema.
 *
 * @author Ivan Tankoyeu
 *
 */
public interface IResource {

    public IResourceContext getResourceContext();

    public void setResourceContext(IResourceContext resourceContext);

    public ITableResource getTableResource();

    public void setTableResource(ITableResource tableResource);

}
