package eu.trentorise.schemamatcher.odr.impl;

import eu.trentorise.opendata.semantics.model.knowledge.IResourceContext;
import eu.trentorise.opendata.semantics.model.knowledge.ITableResource;
import eu.trentorise.schemamatcher.model.IResource;

public class Resource implements IResource {
	IResourceContext rc;
	ITableResource tr;
	
	@Override
	public IResourceContext getResourceContext() {

		return this.rc;
	}

	@Override
	public void setResourceContext(IResourceContext resourceContext) {
		this.rc=resourceContext;
	}

	@Override
	public ITableResource getTableResource() {
		return this.tr;
	}

	@Override
	public void setTableResource(ITableResource tableResource) {
	this.tr=tableResource;

	}
	

}
