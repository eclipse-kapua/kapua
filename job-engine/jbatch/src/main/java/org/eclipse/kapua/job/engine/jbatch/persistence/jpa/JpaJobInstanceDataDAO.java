/*******************************************************************************
 * Copyright (c) 2020, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.job.engine.jbatch.persistence.jpa;

import com.ibm.jbatch.container.impl.PartitionedStepBuilder;
import org.eclipse.kapua.commons.jpa.EntityManager;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service DAO for {@link JpaJobInstanceData}.
 *
 * @since 1.2.0
 */
public class JpaJobInstanceDataDAO {

    private JpaJobInstanceDataDAO() {
    }

    public static JpaJobInstanceData create(EntityManager em, String name, String appTag, String jobXml) {

        JpaJobInstanceData jpaJobInstanceData = new JpaJobInstanceData();
        jpaJobInstanceData.setName(name);
        jpaJobInstanceData.setAppTag(appTag);
        jpaJobInstanceData.setJobXml(jobXml);

        em.persist(jpaJobInstanceData);
        em.flush();
        em.refresh(jpaJobInstanceData);

        return jpaJobInstanceData;
    }

    public static JpaJobInstanceData find(EntityManager em, long id) {
        return em.find(JpaJobInstanceData.class, id);
    }

    public static Integer getJobInstanceCount(EntityManager em, String jobName, String appTag) {

        TypedQuery<Long> countQuery = appTag != null ?
                em.createNamedQuery("JobInstanceData.countByNameTagApp", Long.class) :
                em.createNamedQuery("JobInstanceData.countByName", Long.class);

        countQuery.setParameter("name", jobName);

        if (appTag != null) {
            countQuery.setParameter("appTag", appTag);
        }

        return countQuery.getSingleResult().intValue();
    }

    public static List<Long> getJobInstanceIds(EntityManager em, String jobName, String appTag, Integer offset, Integer limit) {

        TypedQuery<Long> selectQuery = appTag != null ?
                em.createNamedQuery("JobInstanceData.selectIdsByNameTagApp", Long.class) :
                em.createNamedQuery("JobInstanceData.selectIdsByName", Long.class);

        selectQuery.setParameter("name", jobName);

        if (appTag != null) {
            selectQuery.setParameter("appTag", appTag);
        }

        if (offset != null) {
            selectQuery.setFirstResult(offset);
        }
        if (limit != null) {
            selectQuery.setMaxResults(limit);
        }

        return selectQuery.getResultList();
    }

    public static Map<Long, String> getExternalJobInstanceData(EntityManager em) {
        TypedQuery<JpaJobInstanceData> selectQuery = em.createNamedQuery("JobInstanceData.selectByName", JpaJobInstanceData.class);

        selectQuery.setParameter("name", PartitionedStepBuilder.JOB_ID_SEPARATOR + "%");

        List<JpaJobInstanceData> queryResult = selectQuery.getResultList();

        return queryResult.stream().collect(Collectors.toMap(JpaJobInstanceData::getId, JpaJobInstanceData::getName));
    }

    public static int deleteByName(EntityManager em, String jobName) {
        TypedQuery<Integer> deleteByNameQuery = em.createNamedQuery("JobInstanceData.deleteByName", Integer.class);
        deleteByNameQuery.setParameter("name", jobName);
        return deleteByNameQuery.executeUpdate();
    }

    public static int deleteById(EntityManager em, long jobInstanceId) {
        TypedQuery<Integer> deleteByNameQuery = em.createNamedQuery("JobInstanceData.deleteById", Integer.class);
        deleteByNameQuery.setParameter("id", jobInstanceId);
        return deleteByNameQuery.executeUpdate();
    }
}
