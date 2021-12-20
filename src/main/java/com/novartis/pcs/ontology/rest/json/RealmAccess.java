package com.novartis.pcs.ontology.rest.json;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RealmAccess {

    private List<String> roles;

}
