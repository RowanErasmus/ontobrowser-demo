package com.novartis.pcs.ontology.rest.json.controlledvocabulary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TermDTO {

    private String name;
    @JsonProperty("reference_id")
    private String referenceId;
}
