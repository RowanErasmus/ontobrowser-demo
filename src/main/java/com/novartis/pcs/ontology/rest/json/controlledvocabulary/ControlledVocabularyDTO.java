package com.novartis.pcs.ontology.rest.json.controlledvocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.novartis.pcs.ontology.entity.ControlledVocabulary;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ControlledVocabularyDTO {

    private String name;
    @JsonProperty("reference_id")
    private String referenceId;
    @JsonProperty("data_source_acronym")
    private String dataSourceAcronym;
    private String context;
    private DomainDTO domain;
    private List<TermDTO> terms;

    private ControlledVocabularyDTO(String name, String referenceId) {
        this.name = name;
        this.referenceId = referenceId;
    }

    public static ControlledVocabularyDTO mapNameAndReferenceFromEntity(ControlledVocabulary entity) {
        return new ControlledVocabularyDTO(entity.getName(), entity.getReferenceId());
    }
}
