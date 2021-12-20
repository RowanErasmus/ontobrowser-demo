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

import com.novartis.pcs.ontology.entity.RelationshipType;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

/**
 * Stateless session bean DAO for RelationshipType entity
 */
@Stateless
@Local({RelationshipTypeDAOLocal.class})
@Remote({RelationshipTypeDAORemote.class})
public class RelationshipTypeDAO extends VersionedEntityDAO<RelationshipType>
        implements RelationshipTypeDAOLocal, RelationshipTypeDAORemote {

    public RelationshipTypeDAO() {
        super();
    }

    @Override
    public RelationshipType loadByRelationship(String relationship) {
        try {
            Query query = entityManager.createNamedQuery(RelationshipType.QUERY_BY_RELSHIP);
            query.setParameter("relationship", relationship);
            return (RelationshipType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}