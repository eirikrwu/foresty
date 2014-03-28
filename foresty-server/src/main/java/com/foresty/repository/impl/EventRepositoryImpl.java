package com.foresty.repository.impl;

import com.foresty.model.Event;
import com.foresty.repository.EventRepository;
import com.foresty.repository.EventRepositoryCustom;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    public Page<Event> getEventsByCriterion(EventRepository.EventCriteria criteria, Pageable pageable) {
        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            List<Object> parameters = Lists.newArrayList();
            StringBuilder queryStringBuilder = new StringBuilder();
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

            // count total number
            Query countQuery = createQuery(entityManager, "SELECT COUNT(e) FROM Event e WHERE 1 = 1",
                    queryStringBuilder.toString(),
                    parameters);
            Long total = (Long) countQuery.getSingleResult();

            // get records for this page
            Query query = createQuery(entityManager, "SELECT e FROM Event e WHERE 1 = 1", queryStringBuilder.toString(),
                    parameters);
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            List<Event> records = Lists.newArrayList(query.getResultList());

            // create page object
            return new PageImpl<Event>(records, pageable, total);
        } finally {
            entityManager.close();
        }
    }

    private Query createQuery(EntityManager entityManager, String select, String where, List<Object> parameters) {
        Query query = entityManager.createQuery(select + where);
        for (int i = 0; i < parameters.size(); i++) {
            query.setParameter(i + 1, parameters.get(i));
        }

        return query;
    }
}
