package eu.trentorise.opendata.schemamatcher.implementation.model;

import java.util.List;

import com.google.common.base.Preconditions;

import eu.trentorise.opendata.schemamatcher.model.IElementContent;

/**
 * Class implements IElementContent interface
 *
 * @author Ivan Tankoyeu
 *
 */
public class ElementContent implements IElementContent {

    private List<Object> instances;
    private Integer contentSize;

    public ElementContent(List<Object> instances, Integer contentSize) {
        super();
        this.instances = instances;
        this.contentSize = contentSize;
    }

    public ElementContent() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<Object> getContent() {
        return this.instances;
    }

    @Override
    public int getContentSize() {
        if (contentSize == null) {
            this.contentSize = instances.size();
        }
        return contentSize;
    }

    @Override
    public void setContent(List<Object> instances) {
        Preconditions.checkNotNull(instances);
        this.instances = instances;

    }

    @Override
    public void setContentSize(int n) {
        Preconditions.checkNotNull(n);
        this.contentSize = n;

    }

}
