package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@Repository
public class QuestionDao {

    @Autowired
    private EntityManager entityManager;

    public QuestionEntity getQuestionByUuid(final String uuid){
        try{
            return entityManager.createNamedQuery("QuestionEntity.questionByUuid",QuestionEntity.class)
                    .setParameter("uuid",uuid).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }

    }

    public void delete(QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }
}
