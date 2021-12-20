/* 

Copyright 2015 Novartis Institutes for Biomedical Research

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.novartis.pcs.ontology.rest.servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.novartis.pcs.ontology.dao.DatasourceDAOLocal;
import com.novartis.pcs.ontology.dao.SynonymDAOLocal;
import com.novartis.pcs.ontology.entity.Datasource;
import com.novartis.pcs.ontology.entity.Synonym;
import com.novartis.pcs.ontology.entity.VersionedEntity.Status;
import com.novartis.pcs.ontology.rest.json.ErrorResponse;
import com.novartis.pcs.ontology.rest.json.SynonymDTO;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * A simple REST service for synonyms. Produces JSON only.
 * <p>
 * Note: Could use JAX-RS (e.g. RESTEasy or Jersey) but this RESTful
 * service is so trivial that it does not warrant a framework.
 *
 * @author Carlo Ravagli
 */

@WebServlet("/synonyms/*")
@SuppressWarnings("serial")
public class SynonymsServlet extends HttpServlet {

    @EJB
    protected DatasourceDAOLocal datasourceDAO;

    @EJB
    protected SynonymDAOLocal synonymDAO;


    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String mediaType = getExpectedMediaType(request);
        log("Received request to get synonyms ");
        if (!APPLICATION_JSON.equals(mediaType)) {
            response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            response.setContentLength(0);
        } else {
            String datasourceAcronym = request.getParameter("data_source_acronym");
            String vocabRefId = request.getParameter("cntrldvocab");
            try {
                String modifiedAfter = request.getParameter("modified_after");
                Date modifiedAfterDate = new SimpleDateFormat("yyyy-MM-dd").parse("2000-01-01");
                if (modifiedAfter != null && !modifiedAfter.isEmpty()) {
                    modifiedAfterDate = new SimpleDateFormat("yyyy-MM-dd").parse(modifiedAfter);
                }

                boolean pending = Boolean.parseBoolean(
                        request.getParameter("pending"));
                serialize(datasourceAcronym, vocabRefId, pending, modifiedAfterDate, response);
            } catch (ParseException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String errorJson = mapper.writeValueAsString(new ErrorResponse("BAD REQUEST", "Could not parse date, please use yyyy-MM-dd format"));
                response.getWriter().write(errorJson);
                response.getWriter().close();
                response.getWriter().flush();
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        resp.setContentLength(0);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        resp.setContentLength(0);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        resp.setContentLength(0);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        String mediaType = getExpectedMediaType(request);

        if (APPLICATION_JSON.equals(mediaType)) {
            // Preflight CORS support
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET");
            response.setIntHeader("Access-Control-Max-Age", 60 * 60 * 24);
            response.setContentType(mediaType + ";charset=utf-8");
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

    private void serialize(String datasourceAcronym, String vocabRefId, boolean pending, Date modifiedAfter,
                           HttpServletResponse response) {
        try {
            Collection<Synonym> synonyms;
            Datasource datasource = datasourceDAO.loadByAcronym(datasourceAcronym);
            if (datasource == null && vocabRefId == null) {
                log("Loading all synonyms");
                synonyms = synonymDAO.loadAll();
            } else if (datasource != null && vocabRefId != null) {
                log("Loading synonyms by datasource and vocabulary");
                synonyms = synonymDAO.loadByCtrldVocabRefIdAndDataSource(datasource, vocabRefId);
            } else if (datasource != null) {
                log("Loading synonyms by datasource");
                synonyms = synonymDAO.loadByDatasource(datasource);
            } else {
                log("Loading synonyms by vocabulary");
                synonyms = synonymDAO.loadByCtrldVocabRefId(vocabRefId);
            }

            EnumSet<Status> statusSet = pending ?
                    EnumSet.of(Status.PENDING, Status.APPROVED) :
                    EnumSet.of(Status.APPROVED);


            List<SynonymDTO> dtos = synonyms.stream()
                    .filter(s -> statusSet.contains(s.getStatus()))
                    .map(SynonymDTO::new)
                    .filter(s -> s.getLastModified().toInstant().isAfter(modifiedAfter.toInstant()))
                    .collect(Collectors.toList());

            log("Returning " + dtos.size() + " synonyms");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType(APPLICATION_JSON + ";charset=utf-8");
            response.setHeader("Cache-Control", "public, max-age=0");

            // As per jackson javadocs - Encoding will be UTF-8
            mapper.writeValue(response.getOutputStream(), dtos);

        } catch (Exception e) {
            log("Failed to serialize synonyms to JSON", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentLength(0);
        }
    }
}
