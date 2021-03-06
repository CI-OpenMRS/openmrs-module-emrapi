/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.emrapi.concept;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class HibernateEmrConceptDAO implements EmrConceptDAO {

    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Concept> getConceptsMappedTo(Collection<ConceptMapType> mapTypes, ConceptReferenceTerm term) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(Concept.class);
        crit.createCriteria("conceptMappings")
                .add(Restrictions.in("conceptMapType", mapTypes))
                .add(Restrictions.eq("conceptReferenceTerm", term));
        return crit.list();
    }

}
