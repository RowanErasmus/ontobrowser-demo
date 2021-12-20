/* 

Copyright 2015 Novartis Institutes for Biomedical Research

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.novartis.pcs.ontology.dao;

import com.novartis.pcs.ontology.entity.CuratorAction;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Stateless session bean DAO for CuratorAaction entity
 */
@Stateless
@Local({CuratorActionDAOLocal.class})
@Remote({CuratorActionDAORemote.class})
public class CuratorActionDAO extends AbstractDAO<CuratorAction>
        implements CuratorActionDAOLocal, CuratorActionDAORemote {

    public CuratorActionDAO() {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<CuratorAction> loadByCuratorId(long curatorId) {
        Query query = entityManager.createNamedQuery(CuratorAction.QUERY_BY_CURATOR_ID);
        query.setParameter("curatorId", curatorId);
        return query.getResultList();
    }
}
