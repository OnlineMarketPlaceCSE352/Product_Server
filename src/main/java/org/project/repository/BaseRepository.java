package org.project.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.project.util.HibernateUtil;
import org.jinq.orm.stream.JinqStream;
import java.util.List;


public abstract class BaseRepository<T> {

    private final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected List<T> query(JinqStream.Where<T, Exception> filter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            EntityManager em = session.unwrap(EntityManager.class);
            return HibernateUtil.getJinqProvider()
                    .streamAll(em, entityClass)
                    .where(filter)
                    .toList();
        }
    }

    protected List<T> queryPaginated(JinqStream.Where<T, Exception> filter, int pageIndex, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            EntityManager em = session.unwrap(EntityManager.class);
            return HibernateUtil.getJinqProvider()
                    .streamAll(em, entityClass)
                    .where(filter)
                    .skip((long) pageIndex * pageSize)
                    .limit(pageSize)
                    .toList();
        }
    }

    protected long count(JinqStream.Where<T, Exception> filter) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            EntityManager em = session.unwrap(EntityManager.class);
            return HibernateUtil.getJinqProvider()
                    .streamAll(em, entityClass)
                    .where(filter)
                    .count();
        }
    }

    public void save(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to save", e);
        }
    }

    public void delete(String id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T entity = session.get(entityClass, id);
            if (entity != null) {
                session.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to delete", e);
        }
    }

    public void update(T entity) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Failed to update", e);
        }
    }
}