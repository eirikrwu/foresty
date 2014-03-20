package com.foresty.repository.impl;

import com.foresty.model.Event;
import com.foresty.repository.EventRepository;
import com.foresty.repository.EventRepositoryCustom;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by EveningSun on 14-3-20.
 */
@Repository
public class EventRepositoryImpl implements EventRepositoryCustom {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public List<Event> getEventsByCriterion(EventRepository.EventCriteria criteria) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            List<Object> parameters = Lists.newArrayList();
            StringBuilder queryStringBuilder = new StringBuilder("SELECT e FROM Event e WHERE 1 = 1");
            if (criteria.getName() != null) {
                parameters.add(criteria.getName());
                queryStringBuilder.append(" AND e.name = ?").append(parameters.size());
            }
            if (criteria.getMinHighestLevel() != null) {
                parameters.add(criteria.getMinHighestLevel());
                queryStringBuilder.append(" AND e.highestLevel >= ?").append(parameters.size());
            }
            if (criteria.getOrderBy() != null) {
                queryStringBuilder.append(" ORDER BY e.").append(criteria.getOrderBy());
                if (criteria.isOrderDesc()) {
                    queryStringBuilder.append(" DESC");
                }
            }

            String queryString = queryStringBuilder.toString();
            Query query = entityManager.createQuery(queryString);
            for (int i = 0; i < parameters.size(); i++) {
                query.setParameter(i + 1, parameters.get(i));
            }

            return Lists.newArrayList(query.getResultList());
        } finally {
            entityManager.close();
        }
    }
}
