package com.novartis.pcs.ontology.dao;

import com.novartis.pcs.ontology.entity.ControlledVocabularyDomain;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

import static com.novartis.pcs.ontology.entity.ControlledVocabularyDomain.QUERY_BY_NAME;

@Stateless
@Local(ControlledVocabularyDomainDAOLocal.class)
@Remote(ControlledVocabularyDomainDAORemote.class)
public class ControlledVocabularyDomainDAO extends ModifiableEntityDAO<ControlledVocabularyDomain>
        implements ControlledVocabularyDomainDAOLocal, ControlledVocabularyDomainDAORemote {

    @Override
    @SuppressWarnings("unchecked")
    public ControlledVocabularyDomain loadByName(String name) {
        Query query = entityManager.createNamedQuery(QUERY_BY_NAME, ControlledVocabularyDomain.class);
        query.setParameter("name", name);
        List<ControlledVocabularyDomain> domains = query.getResultList();
        if (domains == null || domains.isEmpty()) {
            return null;
        }
        return domains.get(0);
    }
}
