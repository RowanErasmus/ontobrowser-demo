package com.novartis.pcs.ontology.service.importer;

import com.novartis.pcs.ontology.dao.ControlledVocabularyContextDAOLocal;
import com.novartis.pcs.ontology.dao.ControlledVocabularyDAOLocal;
import com.novartis.pcs.ontology.dao.ControlledVocabularyDomainDAOLocal;
import com.novartis.pcs.ontology.dao.ControlledVocabularyTermDAOLocal;
import com.novartis.pcs.ontology.dao.DatasourceDAOLocal;
import com.novartis.pcs.ontology.dao.OntologyDAOLocal;
import com.novartis.pcs.ontology.entity.ControlledVocabulary;
import com.novartis.pcs.ontology.entity.ControlledVocabularyContext;
import com.novartis.pcs.ontology.entity.ControlledVocabularyDomain;
import com.novartis.pcs.ontology.entity.ControlledVocabularyTerm;
import com.novartis.pcs.ontology.entity.Curator;
import com.novartis.pcs.ontology.entity.Datasource;
import com.novartis.pcs.ontology.entity.InvalidEntityException;
import com.novartis.pcs.ontology.entity.Ontology;
import com.novartis.pcs.ontology.rest.json.controlledvocabulary.ControlledVocabularyDTO;
import com.novartis.pcs.ontology.rest.json.controlledvocabulary.TermDTO;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Stateless
@Local(ControlledVocabularyServiceLocal.class)
@Remote(ControlledVocabularyServiceRemote.class)
public class ControlledVocabularyServiceImpl implements ControlledVocabularyServiceLocal, ControlledVocabularyServiceRemote {
    private static final String UNKNOWN_DOMAIN = "UNKNOWN DOMAIN";
    protected Logger logger = Logger.getLogger(getClass().getName());

    @EJB
    protected DatasourceDAOLocal dataSourceDAO;

    @EJB
    protected ControlledVocabularyDomainDAOLocal domainDAO;

    @EJB
    protected ControlledVocabularyTermDAOLocal termDAO;

    @EJB
    protected OntologyDAOLocal ontologyDAO;

    @EJB
    protected ControlledVocabularyContextDAOLocal contextDAO;

    @EJB
    protected ControlledVocabularyDAOLocal vocabularyDAO;


    @Override
    public List<String> importControlledVocabulary(ControlledVocabularyDTO dto, Curator creator)
            throws InvalidEntityException {

        ControlledVocabulary vocabulary = getOrCreateVocabulary(dto, creator);
        List<String> failedTerms = new ArrayList<>();
        Set<String> existingTerms = termDAO.loadAll().stream()
                .map(ControlledVocabularyTerm::getName)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        Set<ControlledVocabularyTerm> terms = new HashSet<>(dto.getTerms().size());
        for (TermDTO termDTO : dto.getTerms()) {
            if (existingTerms.contains(termDTO.getName().toLowerCase())) {
                logger.info("Caught a duplicate term " + termDTO.getName());
                failedTerms.add(termDTO.getName());
            } else {
                existingTerms.add(termDTO.getName());
                //TODO: What is the idea behind the usage count?
                ControlledVocabularyTerm term =
                        new ControlledVocabularyTerm(vocabulary, termDTO.getName(), 1, creator);
                terms.add(term);
            }
        }
        terms.forEach(t -> {
            try {
                termDAO.save(t);
            } catch (Exception ex) {
                termDAO.flush();
                logger.info(ex.getMessage());
                logger.info("Caught a duplicate term " + t.getName());
                failedTerms.add(t.getName());
            }
        });
        return failedTerms;
    }

    @Override
    public List<ControlledVocabulary> loadAll() {
        return vocabularyDAO.loadAll();
    }

    @Override
    public List<ControlledVocabulary> loadByDatasource(String datasourceAcronym) {
        Datasource datasource = dataSourceDAO.loadByAcronym(datasourceAcronym);
        if (datasource == null) {
            return Collections.emptyList();
        }
        return vocabularyDAO.loadByDatasource(datasource);
    }

    private ControlledVocabulary getOrCreateVocabulary(ControlledVocabularyDTO dto, Curator creator)
            throws InvalidEntityException {
        ControlledVocabulary vocabulary;
        vocabulary = vocabularyDAO.loadByName(dto.getName());

        if (vocabulary != null) {
            if (!vocabulary.getDomain().getName().equals(dto.getDomain().getName())) {
                throw new InvalidEntityException(String.format("Vocabulary %s is already linked to domain %s, " +
                                "cannot link it to the provided domain %s",
                        vocabulary.getName(), vocabulary.getDomain().getName(), dto.getDomain().getName()));
            }

            return vocabulary;
        } else {
            Datasource dataSource = getDatasource(dto);
            ControlledVocabularyDomain domain = getOrCreateDomain(dto, creator);
            ControlledVocabularyContext context = getOrCreateContext(dto, creator);
            vocabulary = new ControlledVocabulary(dataSource, domain, context, dto.getReferenceId(), dto.getName(), creator);
            vocabularyDAO.save(vocabulary);
            return vocabulary;
        }
    }

    private Datasource getDatasource(ControlledVocabularyDTO dto) throws InvalidEntityException {
        Datasource dataSource = dataSourceDAO.loadByAcronym(dto.getDataSourceAcronym());

        if (dataSource == null) {
            String knownSources = dataSourceDAO.loadAll().stream()
                    .map(Datasource::getAcronym)
                    .collect(joining(", "));
            throw new InvalidEntityException(String.format(
                    "Data source %s could not be found, known data source acronyms are %s.",
                    dto.getDataSourceAcronym(), knownSources));
        }
        return dataSource;
    }

    private ControlledVocabularyDomain getOrCreateDomain(ControlledVocabularyDTO dto, Curator creator)
            throws InvalidEntityException {

        Set<Ontology> ontologies = dto.getDomain().getOntologies().stream()
                .map(o -> ontologyDAO.loadByName(o))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ontologies.isEmpty()) {
            throw new InvalidEntityException("Please supply valid ontologies to map synonyms to");
        }

        String domainName = dto.getDomain().getName() != null ? dto.getDomain().getName() : UNKNOWN_DOMAIN;

        ControlledVocabularyDomain domain = domainDAO.loadByName(domainName);

        if (domain == null) {
            logger.info("Creating new domain: " + domainName);
            domain = new ControlledVocabularyDomain(domainName, creator);
            domain.setOntologies(ontologies);
            domainDAO.save(domain);
        }
        return domain;
    }

    private ControlledVocabularyContext getOrCreateContext(ControlledVocabularyDTO dto, Curator creator)
            throws InvalidEntityException {
        ControlledVocabularyContext context = contextDAO.loadByName(dto.getContext());

        if (context == null) {
            logger.info("Creating new context: " + dto.getContext());
            context = new ControlledVocabularyContext(dto.getContext(), creator);
            contextDAO.save(context);
        }
        return context;
    }
}
