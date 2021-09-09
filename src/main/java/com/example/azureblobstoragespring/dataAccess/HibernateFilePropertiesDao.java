package com.example.azureblobstoragespring.dataAccess;

import com.example.azureblobstoragespring.property.FileLoggerProperties;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;


@Repository
public class HibernateFilePropertiesDao implements FilePropertiesDao{

    private EntityManager entityManager;

    @Autowired
    public HibernateFilePropertiesDao(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void add(FileLoggerProperties fileLoggerProperties) {
        Session session = entityManager.unwrap(Session.class);
        session.saveOrUpdate(fileLoggerProperties);
    }

    @Override
    public FileLoggerProperties getFilePropertiesById(int fileID) {
        Session session = entityManager.unwrap(Session.class);
        return session.get(FileLoggerProperties.class, fileID);
    }
}
