/*
 * Copyright 2015 Trento Rise.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.trentorise.opendata.schemamatcher.util;

import eu.trentorise.opendata.columnrecognizers.SwebConfiguration;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.semantics.model.entity.IEntityType;
import it.unitn.disi.sweb.webapi.client.eb.InstanceClient;
import it.unitn.disi.sweb.webapi.client.kb.ConceptClient;
import it.unitn.disi.sweb.webapi.model.Pagination;
import it.unitn.disi.sweb.webapi.model.eb.Instance;
import it.unitn.disi.sweb.webapi.model.filters.InstanceFilter;
import it.unitn.disi.sweb.webapi.model.kb.concepts.Concept;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for stuff with obscure meaning
 *
 * @author David Leoni
 */
public class SwebClientCrap {

    private final static int PAGE_SIZE = 100;

    private static final Logger LOG = LoggerFactory.getLogger(SwebClientCrap.class);
 
 
 
    /**
     * methods takes random entities from Entitypedia
     *
     * @param etype
     * @return
     */
    public static List<Instance> getEntities(IEntityType etype) {

        InstanceClient insClient = new InstanceClient(SwebConfiguration.getClientProtocol());
        Long etypeId = SwebConfiguration.getUrlMapper().etypeUrlToId(etype.getURL());
        Pagination page = new Pagination();
        page.setPageSize(PAGE_SIZE);
        List<Instance> instances = insClient.readInstances(1L, etypeId, null, null, page); // TODO Make sure that they are taken randomly
        List<Long> instancesIds = new ArrayList();

        for (Instance in : instances) {
            instancesIds.add(in.getId());
        }
        InstanceFilter filter = new InstanceFilter();
        filter.setIncludeAttributes(true);
        if (!instancesIds.isEmpty()) {
            List<Instance> instancesFull = insClient.readInstancesById(instancesIds, filter);
            return instancesFull;
        } else {
            return null;
        }
    }
    
    /**
     * Ported from Disi client ConceptODR. 
     */
    public static Long readConceptGUID(long glId) {
        ConceptClient client = new ConceptClient(SwebConfiguration.getClientProtocol());
        LOG.warn("Entity Base is 1");
        List<Concept> concepts = client.readConcepts(1L, glId, null, null, null, null);
        LOG.warn("Only the first concept is returned. The number of returned concepts is: " + concepts.size());
        return concepts.get(0).getId();
    }    
}
