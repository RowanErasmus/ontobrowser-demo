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
package com.novartis.pcs.ontology.service;

import com.novartis.pcs.ontology.entity.Curator;
import com.novartis.pcs.ontology.entity.CuratorAction;
import com.novartis.pcs.ontology.entity.InvalidEntityException;
import com.novartis.pcs.ontology.entity.Relationship;
import com.novartis.pcs.ontology.entity.Synonym;
import com.novartis.pcs.ontology.entity.Term;
import com.novartis.pcs.ontology.entity.VersionedEntity;

import java.util.Collection;
import java.util.Set;

public interface OntologyCuratorService {

    public Curator loadByUsername(String username);

    public void save(Curator curator) throws InvalidEntityException;

    public Collection<Term> loadPendingTerms();

    public Collection<Synonym> loadPendingSynonyms();

    public Collection<Relationship> loadPendingRelationships();

    public Term approveTerm(long termId, String comments, Curator curator) throws InvalidEntityException;

    public Term rejectTerm(long termId, String comments, Curator curator) throws InvalidEntityException;

    public Synonym approveSynonym(long synonymId, String comments, Curator curator) throws InvalidEntityException;

    public Synonym rejectSynonym(long synonymId, String comments, Curator curator) throws InvalidEntityException;

    public Relationship approveRelationship(long relationshipId,
                                            String comments, Curator curator) throws InvalidEntityException;

    public Relationship rejectRelationship(long relationshipId,
                                           String comments, Curator curator) throws InvalidEntityException;

    public <T extends VersionedEntity> Set<T> approve(Set<T> pending, String comments, Curator curator) throws InvalidEntityException;

    public <T extends VersionedEntity> Set<T> reject(Set<T> pending, String comments, Curator curator) throws InvalidEntityException;

    public Relationship obsoleteRelationship(long relationshipId, long replacementRelationshipId,
                                             String comments, Curator curator) throws InvalidEntityException;

    public Synonym obsoleteSynonym(long synonymId, long replacementSynonymId, String comments,
                                   Curator curator) throws InvalidEntityException;

    public Term obsoleteTerm(long termId, long replacementTermId, String comments,
                             Curator curator) throws InvalidEntityException;

    public Collection<CuratorAction> loadCuratorActions();

    public void changePassword(Curator curator, String oldPassword, String newPassword) throws InvalidEntityException;

}
