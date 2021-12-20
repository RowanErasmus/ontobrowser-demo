package com.novartis.pcs.ontology.dao;

import com.novartis.pcs.ontology.entity.ControlledVocabularyDomain;

import javax.ejb.Local;

@Local
public interface ControlledVocabularyDomainDAOLocal extends DAO<ControlledVocabularyDomain> {

    ControlledVocabularyDomain loadByName(String name);
}
