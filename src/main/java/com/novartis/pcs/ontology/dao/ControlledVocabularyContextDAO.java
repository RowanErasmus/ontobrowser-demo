package com.novartis.pcs.ontology.dao;

import com.novartis.pcs.ontology.entity.ControlledVocabularyContext;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

import static com.novartis.pcs.ontology.entity.ControlledVocabularyContext.QUERY_BY_NAME;

@Stateless
@Local(ControlledVocabularyContextDAOLocal.class)
@Remote(ControlledVocabularyContextDAORemote.class)
public class ControlledVocabularyContextDAO extends ModifiableEntityDAO<ControlledVocabularyContext>
        implements ControlledVocabularyContextDAORemote, ControlledVocabularyContextDAOLocal {


    @Override
    @SuppressWarnings("unchecked")
    public ControlledVocabularyContext loadByName(String name) {
        Query query = entityManager.createNamedQuery(QUERY_BY_NAME, ControlledVocabularyContext.class);
        query.setParameter("name", name);
        List<ControlledVocabularyContext> contexts = query.getResultList();
        if (contexts == null || contexts.isEmpty()) {
            return null;
        }
        return contexts.get(0);
    }
}