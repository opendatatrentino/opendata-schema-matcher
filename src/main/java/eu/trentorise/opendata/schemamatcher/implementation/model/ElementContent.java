package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.List;

import eu.trentorise.opendata.schemamatcher.model.IElementContent;

public class ElementContent implements IElementContent{

	public List<Object> instances;
	public int contentSize;
	
	public List<Object> getContent() {
		return this.instances;
	}

	public int getContentSize() {
		return this.contentSize;
	}

	public void setContent(List<Object> instances) {
		this.instances=instances;
		
	}

	public void setContentSize(int n) {
		this.contentSize=n;

	}
	
	
	

}
