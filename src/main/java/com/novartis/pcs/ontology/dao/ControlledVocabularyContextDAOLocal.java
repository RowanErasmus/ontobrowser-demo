package com.novartis.pcs.ontology.dao;

import com.novartis.pcs.ontology.entity.ControlledVocabularyContext;

import javax.ejb.Local;

@Local
public interface ControlledVocabularyContextDAOLocal extends DAO<ControlledVocabularyContext> {

    ControlledVocabularyContext loadByName(String name);
}
