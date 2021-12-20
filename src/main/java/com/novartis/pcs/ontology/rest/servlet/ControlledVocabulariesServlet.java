package com.novartis.pcs.ontology.rest.servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.novartis.pcs.ontology.entity.ControlledVocabulary;
import com.novartis.pcs.ontology.entity.Curator;
import com.novartis.pcs.ontology.entity.InvalidEntityException;
import com.novartis.pcs.ontology.rest.json.ErrorResponse;
import com.novartis.pcs.ontology.rest.json.controlledvocabulary.ControlledVocabularyDTO;
import com.novartis.pcs.ontology.service.OntologyCuratorServiceLocal;
import com.novartis.pcs.ontology.service.importer.ControlledVocabularyServiceLocal;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@WebServlet("/cntrldvocabs")
public class ControlledVocabulariesServlet extends HttpServlet {

    @EJB
    private ControlledVocabularyServiceLocal vocabService;

    @EJB
    private OntologyCuratorServiceLocal curatorService;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        resp.setContentLength(0);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        resp.setContentLength(0);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log("Received controlled vocabulary get request");
        String datasource = request.getParameter("data_source_acronym");
        List<ControlledVocabulary> vocabs;

        log("Loading controlled vocabularies");
        if (StringUtils.isEmpty(datasource)) {
            log("Loading all controlled vocabularies");
            vocabs = vocabService.loadAll();
        } else {
            log("Loading all controlled vocabularies for datasource " + datasource);
            vocabs = vocabService.loadByDatasource(datasource);
        }
        List<ControlledVocabularyDTO> dtos = vocabs.stream()
                .map(ControlledVocabularyDTO::mapNameAndReferenceFromEntity)
                .collect(Collectors.toList());
        returnJson(response, dtos);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log("Received controlled vocabulary import request");
        // TODO: See if the user of this endpoint needs a particular reference
        Curator curator = curatorService.loadByUsername("SYSTEM");

        try {
            String jsonString = IOUtils.toString(request.getReader());
            ControlledVocabularyDTO controlledVocab = mapper.readValue(jsonString, ControlledVocabularyDTO.class);
            log("Started import controlled vocabulary");
            List<String> failedTerms = vocabService.importControlledVocabulary(controlledVocab, curator);
            log("Finished import controlled vocabulary with " + failedTerms.size() + " skipped terms");
            returnJson(response, Collections.singletonMap("failed_terms", failedTerms));
        } catch (IOException e) {
            log("Something went wrong parsing the request body, most likely the request was malformed");
            log(Arrays.toString(e.getStackTrace()));
            returnError(response, HttpServletResponse.SC_BAD_REQUEST, "BAD REQUEST",
                    "Something went wrong parsing the request body, most likely request was malformed");
        } catch (InvalidEntityException e) {
            log("Import contained an invalid entity " + e.getMessage());
            returnError(response, HttpServletResponse.SC_BAD_REQUEST, "BAD REQUEST", e.getMessage());
        }
        response.setContentLength(0);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        String mediaType = getExpectedMediaType(request);

        if (APPLICATION_JSON.equals(mediaType)) {
            // Preflight CORS support
            setHeaders(response);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Methods", "PUT");
            response.setIntHeader("Access-Control-Max-Age", 60 * 60 * 24);
            response.setContentLength(0);
        } else {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            response.setContentLength(0);
        }
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        return System.currentTimeMillis();
    }

    private String getExpectedMediaType(HttpServletRequest request) {
        String mediaType = null;
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null) {
            mediaType = StringUtils.trimToNull(
                    MIMEParse.bestMatch(Collections.singleton(APPLICATION_JSON), acceptHeader));
        }
        return mediaType;
    }


    private void returnError(HttpServletResponse response, int responseCode, String error, String message)
            throws IOException {
        setHeaders(response);
        String errorJson = mapper.writeValueAsString(new ErrorResponse(error, message));
        response.setStatus(responseCode);
        response.getWriter().write(errorJson);
        response.getWriter().close();
        response.getWriter().flush();
    }

    private void returnJson(HttpServletResponse response, Object objectToReturn) throws IOException {
        try {
            setHeaders(response);
            String jsonString = mapper.writeValueAsString(objectToReturn);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonString);
            response.getWriter().close();
            response.getWriter().flush();
        } catch (IOException e) {
            returnError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR", "Something went wrong serializing the response");
        }
    }

    private void setHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType(APPLICATION_JSON + ";charset=utf-8");
        response.setHeader("Cache-Control", "public, max-age=0");
    }
}
