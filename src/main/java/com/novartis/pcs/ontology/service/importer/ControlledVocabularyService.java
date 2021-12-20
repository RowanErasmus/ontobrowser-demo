package com.novartis.pcs.ontology.service.importer;

import com.novartis.pcs.ontology.entity.ControlledVocabulary;
import com.novartis.pcs.ontology.entity.Curator;
import com.novartis.pcs.ontology.entity.InvalidEntityException;
import com.novartis.pcs.ontology.rest.json.controlledvocabulary.ControlledVocabularyDTO;

import java.util.List;

public interface ControlledVocabularyService {
    List<String> importControlledVocabulary(ControlledVocabularyDTO dto, Curator creator) throws InvalidEntityException;

    List<ControlledVocabulary> loadAll();

    List<ControlledVocabulary> loadByDatasource(String datasourceAcronym);
}
