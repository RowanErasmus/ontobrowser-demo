package com.novartis.pcs.ontology.rest.json.controlledvocabulary;

import lombok.Data;

import java.util.List;

@Data
public class DomainDTO {

    private String name;
    private List<String> ontologies;

}


