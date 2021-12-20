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

import com.novartis.pcs.ontology.entity.ControlledVocabularyTerm;
import com.novartis.pcs.ontology.entity.Curator;
import com.novartis.pcs.ontology.entity.DuplicateEntityException;
import com.novartis.pcs.ontology.entity.InvalidEntityException;
import com.novartis.pcs.ontology.entity.Relationship;
import com.novartis.pcs.ontology.entity.RelationshipType;
import com.novartis.pcs.ontology.entity.Synonym;
import com.novartis.pcs.ontology.entity.Term;

import java.util.Collection;

public interface OntologyTermService {

    public Collection<Term> loadRoots();

    public Collection<Term> loadAll(String ontologyName);

    public Collection<Term> loadLastCreated(int max);

    public Term loadByReferenceId(String referenceId);

    public Collection<RelationshipType> loadAllRelationshipTypes();

    public Term createTerm(String ontologyName, String term,
                           String definition, String url, String comments,
                           String relatedTermRefId, String relationshipType,
                           Curator curator) throws DuplicateEntityException, InvalidEntityException;

    public Term createTerm(String ontologyName, String term,
                           String definition, String url, String comments,
                           String relatedTermRefId, String relationshipType,
                           String datasourceAcronym, String referenceId,
                           Collection<ControlledVocabularyTerm> synonyms,
                           Synonym.Type synonymType,
                           Curator curator) throws DuplicateEntityException, InvalidEntityException;

    public Term addSynonym(String termRefId, String synonym, Synonym.Type type,
                           String datasourceAcronym, String referenceId, Curator curator)
            throws DuplicateEntityException, InvalidEntityException;

    public Term addSynonym(String termRefId,
                           ControlledVocabularyTerm vocabTerm,
                           Synonym.Type synonymType,
                           Curator curator)
            throws DuplicateEntityException, InvalidEntityException;

    public Term addSynonyms(String termRefId,
                            Collection<ControlledVocabularyTerm> terms,
                            Synonym.Type synonymType,
                            Curator curator)
            throws DuplicateEntityException, InvalidEntityException;

    public Term addRelationship(String termRefId, String relatedTermRefId,
                                String relationshipType, Curator curator)
            throws DuplicateEntityException, InvalidEntityException;

    public Term updateTerm(long termId, String definition, String url,
                           String comments, Curator curator) throws InvalidEntityException;

    public Synonym updateSynonym(long synonymId, Synonym.Type type,
                                 Curator curator) throws InvalidEntityException;

    public Relationship updateRelationship(long relationshipId,
                                           String relationship, Curator curator)
            throws InvalidEntityException, DuplicateEntityException;

    public void deleteTerm(long termId, Curator curator) throws InvalidEntityException;

    public void deleteSynonym(long synonymId, Curator curator) throws InvalidEntityException;

    public void deleteRelationship(long relationshipId, Curator curator)
            throws InvalidEntityException;
}
