package eu.trentorise.opendata.schemamatcher.odr.impl;

import eu.trentorise.opendata.schemamatcher.model.IResource;
import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;

public class Resource implements IResource {
	private IResourceContext rc;
	private ITableResource tr;
	
	public IResourceContext getResourceContext() {

		return this.rc;
	}

	public void setResourceContext(IResourceContext resourceContext) {
		this.rc=resourceContext;
	}

	public ITableResource getTableResource() {
		return this.tr;
	}

	public void setTableResource(ITableResource tableResource) {
	this.tr=tableResource;

	}
	

}
