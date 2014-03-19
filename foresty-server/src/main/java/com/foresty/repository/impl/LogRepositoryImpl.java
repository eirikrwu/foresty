package com.foresty.repository.impl;

import com.foresty.model.Log;
import com.foresty.repository.LogRepositoryCustomQuery;
import com.google.common.base.Preconditions;
import org.hibernate.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateQueryException;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created by EveningSun on 14-3-18.
 */
@Component
public class LogRepositoryImpl implements LogRepositoryCustomQuery {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public List<String> getChildNodes(int parentNodeDepth, String parentNodeName) {
        if (parentNodeDepth > 0) {
            Preconditions.checkNotNull(parentNodeName);
            Preconditions.checkArgument(!parentNodeName.trim().isEmpty());
        }
        Preconditions.checkArgument(parentNodeDepth >= 0 && parentNodeDepth <= Log.MAX_DEPTH);

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            String parentNodePropertyName = "node" + parentNodeDepth;
            String childNodePropertyName = "node" + (parentNodeDepth + 1);

            Query query;
            if (parentNodeDepth == 0) {
                query = entityManager.createQuery("SELECT DISTINCT (l.node1) FROM Log l");
            } else {
                String queryString = MessageFormat
                        .format("SELECT DISTINCT (l.{0}) FROM Log l WHERE l.{1} = ?1", childNodePropertyName,
                                parentNodePropertyName);
                query = entityManager.createQuery(queryString);
                query.setParameter(1, parentNodeName);
            }

            return query.getResultList();
        } catch (PersistenceException e) {
            throw new HibernateQueryException(new QueryException(e));
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Log> getLogs(int parentNodeDepth, String parentNodeName) {
        if (parentNodeDepth > 0) {
            Preconditions.checkNotNull(parentNodeName);
            Preconditions.checkArgument(!parentNodeName.trim().isEmpty());
        }
        Preconditions.checkArgument(parentNodeDepth >= 0 && parentNodeDepth <= Log.MAX_DEPTH);

        EntityManager entityManager = this.entityManagerFactory.createEntityManager();
        try {
            Query query;
            if (parentNodeDepth == 0) {
                query = entityManager.createQuery("SELECT l FROM Log l WHERE l.node1 is NULL ORDER BY l.timestamp");
            } else {
                String parentNodePropertyName = "node" + parentNodeDepth;
                String queryString;
                if (parentNodeDepth < Log.MAX_DEPTH) {
                    String childNodePropertyName = "node" + (parentNodeDepth + 1);
                    queryString = MessageFormat
                            .format("SELECT l FROM Log l WHERE l.{0} = ?1 AND l.{1} is NULL ORDER BY l.timestamp",
                                    parentNodePropertyName, childNodePropertyName);
                } else {
                    queryString = MessageFormat.format("SELECT l FROM Log l WHERE l.{0} = ?1 ORDER BY l.timestamp",
                            parentNodePropertyName);
                }
                query = entityManager.createQuery(queryString);
                query.setParameter(1, parentNodeName);
            }

            return query.getResultList();
        } catch (PersistenceException e) {
            throw new HibernateQueryException(new QueryException(e));
        } finally {
            entityManager.close();
        }
    }
}
