package com.novartis.pcs.ontology.rest.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.novartis.pcs.ontology.entity.Ontology;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OntologyDTO {

    private String name;
    private String description;
    @JsonProperty("is_internal")
    private boolean internal;

    @JsonProperty("source_namespace")
    private String sourceNamespace;

    @JsonProperty("source_uri")
    private String sourceUri;

    @JsonProperty("source_release")
    private String sourceRelease;

    @JsonProperty("source_date")
    private String sourceDate;

    @JsonProperty("source_format")
    private String sourceFormat;

    @JsonProperty("reference_id_prefix")
    private String referenceIdPrefix;

    @JsonProperty("reference_id_value")
    private int referenceIdValue;

    @JsonProperty("is_codelist")
    private boolean codelist;

    @JsonProperty("replaced_by")
    private Ontology replacedBy;


    public OntologyDTO fromEntity(Ontology entity) {
        return OntologyDTO.builder()
                .name(entity.getName())
                .codelist(entity.isCodelist())
                .sourceDate(entity.getSourceDate().toString())
                .description(entity.getDescription())
                .internal(entity.isInternal())
                .referenceIdPrefix(entity.getReferenceIdPrefix())
                .sourceUri(entity.getSourceUri())
                .build();
    }


}
