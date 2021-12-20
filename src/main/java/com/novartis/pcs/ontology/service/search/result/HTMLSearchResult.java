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
package com.novartis.pcs.ontology.service.search.result;


@SuppressWarnings("serial")
public class HTMLSearchResult extends SearchResult {
    protected String html;

    protected HTMLSearchResult() {

    }

    public HTMLSearchResult(String ontology, String referenceId, String term, String html, float score) {
        this(ontology, referenceId, term, html, score, false);
    }

    public HTMLSearchResult(String ontology, String referenceId, String term, String html, float score, boolean synonym) {
        super(ontology, referenceId, term, score, synonym);
        this.html = html;
    }

    public String getHtml() {
        return html;
    }
}