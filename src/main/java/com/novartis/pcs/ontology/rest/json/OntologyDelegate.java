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
package com.novartis.pcs.ontology.rest.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.novartis.pcs.ontology.entity.ControlledVocabularyTerm;
import com.novartis.pcs.ontology.entity.Curator;
import com.novartis.pcs.ontology.entity.CuratorAction;
import com.novartis.pcs.ontology.entity.Datasource;
import com.novartis.pcs.ontology.entity.Ontology;
import com.novartis.pcs.ontology.entity.Synonym;
import com.novartis.pcs.ontology.entity.Term;
import com.novartis.pcs.ontology.entity.Version;
import com.novartis.pcs.ontology.entity.VersionedEntity;
import com.novartis.pcs.ontology.service.util.TermReferenceIdComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OntologyDelegate extends Ontology {

    private final Ontology ontology;
    private Collection<Term> terms;

    public OntologyDelegate(Ontology ontology, Collection<Term> terms) {
        this.ontology = ontology;
        this.terms = filter(terms);
    }

    private static Collection<Term> filter(Collection<Term> terms) {
        List<Term> valid = new ArrayList<>();
        for (Term term : terms) {
            if (term.getStatus().equals(Status.APPROVED) || term.getStatus().equals(Status.OBSOLETE)) {
                filter(term.getSynonyms());
                filter(term.getRelationships());

                for (Synonym synonym : term.getSynonyms()) {
                    if (synonym.getControlledVocabularyTerm() != null) {
                        ControlledVocabularyTerm ctrldVocabTerm = synonym.getControlledVocabularyTerm();
                        Datasource datasource = ctrldVocabTerm.getControlledVocabulary().getDatasource();
                        String refId = ctrldVocabTerm.getReferenceId();

                        synonym.setControlledVocabularyTerm(null);
                        synonym.setDatasource(datasource);
                        synonym.setReferenceId(refId);
                    }
                }
                valid.add(term);
            }
        }
        valid.sort(new TermReferenceIdComparator());
        return valid;
    }

    private static <T extends VersionedEntity> void filter(Set<T> entities) {
        Set<T> invalid = new HashSet<>();
        for (T entity : entities) {
            if (!entity.getStatus().equals(Status.APPROVED)) {
                invalid.add(entity);
            }
        }
        entities.removeAll(invalid);
    }

    @JsonProperty("approved_version")
    public Version getApprovedVersion() {
        return ontology.getApprovedVersion();
    }

    public void setApprovedVersion(Version approvedVersion) {
        ontology.setApprovedVersion(approvedVersion);
    }

    @JsonProperty("created_by")
    public Curator getCreatedBy() {
        return ontology.getCreatedBy();
    }

    public void setCreatedBy(Curator createdBy) {
        ontology.setCreatedBy(createdBy);
    }

    @JsonProperty("created_date")
    public Date getCreatedDate() {
        return ontology.getCreatedDate();
    }

    public void setCreatedDate(Date createdDate) {
        ontology.setCreatedDate(createdDate);
    }

    @JsonProperty("created_version")
    public Version getCreatedVersion() {
        return ontology.getCreatedVersion();
    }

    public void setCreatedVersion(Version createdVersion) {
        ontology.setCreatedVersion(createdVersion);
    }

    @JsonProperty("curator_actions")
    public List<CuratorAction> getCuratorActions() {
        return ontology.getCuratorActions();
    }

    public void setCuratorActions(List<CuratorAction> curatorActions) {
        ontology.setCuratorActions(curatorActions);
    }

    public String getDescription() {
        return ontology.getDescription();
    }

    public void setDescription(String description) {
        ontology.setDescription(description);
    }

    public long getId() {
        return ontology.getId();
    }

    public String getName() {
        return ontology.getName();
    }

    public void setName(String name) {
        ontology.setName(name);
    }

    @JsonProperty("obsolete_version")
    public Version getObsoleteVersion() {
        return ontology.getObsoleteVersion();
    }

    public void setObsoleteVersion(Version obsoleteVersion) {
        ontology.setObsoleteVersion(obsoleteVersion);
    }

    @JsonProperty("reference_id_prefix")
    public String getReferenceIdPrefix() {
        return ontology.getReferenceIdPrefix();
    }

    public void setReferenceIdPrefix(String referenceIdPrefix) {
        ontology.setReferenceIdPrefix(referenceIdPrefix);
    }

    @JsonProperty("reference_id_value")
    public int getReferenceIdValue() {
        return ontology.getReferenceIdValue();
    }

    @JsonProperty("replaced_by")
    public Ontology getReplacedBy() {
        return ontology.getReplacedBy();
    }

    public void setReplacedBy(Ontology replacedBy) {
        ontology.setReplacedBy(replacedBy);
    }

    @JsonProperty("source_date")
    public Date getSourceDate() {
        return ontology.getSourceDate();
    }

    public void setSourceDate(Date sourceDate) {
        ontology.setSourceDate(sourceDate);
    }

    @JsonProperty("source_format")
    public String getSourceFormat() {
        return ontology.getSourceFormat();
    }

    public void setSourceFormat(String sourceFormat) {
        ontology.setSourceFormat(sourceFormat);
    }

    @JsonProperty("source_namespace")
    public String getSourceNamespace() {
        return ontology.getSourceNamespace();
    }

    public void setSourceNamespace(String sourceNamespace) {
        ontology.setSourceNamespace(sourceNamespace);
    }

    @JsonProperty("source_release")
    public String getSourceRelease() {
        return ontology.getSourceRelease();
    }

    public void setSourceRelease(String sourceRelease) {
        ontology.setSourceRelease(sourceRelease);
    }

    @JsonProperty("source_uri")
    public String getSourceUri() {
        return ontology.getSourceUri();
    }

    public void setSourceUri(String sourceUri) {
        ontology.setSourceUri(sourceUri);
    }

    public boolean isCodelist() {
        return ontology.isCodelist();
    }

    public Status getStatus() {
        return ontology.getStatus();
    }

    public void setStatus(Status status) {
        ontology.setStatus(status);
    }

    @JsonProperty("is_internal")
    public boolean isInternal() {
        return ontology.isInternal();
    }

    public void setInternal(boolean internal) {
        ontology.setInternal(internal);
    }

    public void setCreatedDate() {
        ontology.setCreatedDate();
    }

    public void setStatus() {
        ontology.setStatus();
    }

    public Collection<Term> getTerms() {
        return terms;
    }

    public void setTerms(Collection<Term> terms) {
        this.terms = filter(terms);
    }

    public int hashCode() {
        return ontology.hashCode();
    }

    public boolean equals(Object obj) {
        return ontology.equals(obj);
    }

    public String toString() {
        return ontology.toString();
    }
}
