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
package com.novartis.pcs.ontology.service.util;

import com.novartis.pcs.ontology.entity.ControlledVocabulary;
import com.novartis.pcs.ontology.entity.ControlledVocabularyTerm;
import com.novartis.pcs.ontology.entity.Datasource;

import java.util.Comparator;

public class ControlledVocabularyTermSourceComparator implements Comparator<ControlledVocabularyTerm> {

    @Override
    public int compare(ControlledVocabularyTerm term1,
                       ControlledVocabularyTerm term2) {
        ControlledVocabulary controlledVocabulary1 = term1.getControlledVocabulary();
        ControlledVocabulary controlledVocabulary2 = term2.getControlledVocabulary();

        Datasource datasource1 = controlledVocabulary1.getDatasource();
        Datasource datasource2 = controlledVocabulary2.getDatasource();

        int c = datasource1.getAcronym().compareTo(datasource2.getAcronym());
        if (c == 0) {
            c = controlledVocabulary1.getName().compareTo(controlledVocabulary2.getName());
        }

        if (c == 0) {
            c = term1.compareTo(term2);
        }

        return c;
    }
}
