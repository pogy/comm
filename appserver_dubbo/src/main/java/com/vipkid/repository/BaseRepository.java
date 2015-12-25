package com.vipkid.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

@Repository
public abstract class BaseRepository<T extends Serializable>{
    private static final Logger logger = LoggerFactory.getLogger(BaseRepository.class);
    private Class<T> clazz;

    @PersistenceContext
    EntityManager entityManager;

    public BaseRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    public BaseRepository() {

    }

    public T create(T t) {
        entityManager.persist(t);
        return t;
    }

    public T find(long id) {
        return entityManager.find(clazz, id);
    }

    public T update(T t) {
        try {
            entityManager.merge(t);
            //entityManager.flush();
        } catch (Exception e) {
            logger.error("BaseRepository update error,msg = ",e);
            throw e;
        }
        return t;
    }
    
    public T updateWithException(T t) {
        entityManager.merge(t);
        return t;
    }

    public void delete(T t) {
        entityManager.remove(t);
    }

    public void deleteById(long entityId) {
        T entity = find(entityId);
        delete(entity);
    }


}
