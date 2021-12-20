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
package com.novartis.pcs.ontology.service.parser.obo;

import org.coode.owlapi.obo.parser.OBOVocabulary;

public class BuiltInRelationshipTagHandler extends OBOTagHandler {

    public BuiltInRelationshipTagHandler(OBOVocabulary tagName,
                                         OBOParseContext context) {
        super(tagName, context);
    }

    @Override
    void handleTagValue(String tag, String value,
                        String qualifierBlock, String comment) {
        String refId = context.unescapeTagValue(value);
        context.addRelationship(getTagName().getName(), refId, false);
    }
}
