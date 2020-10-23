package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthDao {

    @PersistenceContext
    private EntityManager entityManager;


    public UserAuthEntity createAuthToken(UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public UserAuthEntity getUserByAccessToken(String accessToken) {
        try{
            return  entityManager.createNamedQuery("UserAuthEntity.userByAccessToken", UserAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
        }catch(NoResultException nre){
            return null;
        }
    }

    public UserAuthEntity updateUserAuth(UserAuthEntity userAuthEntity){
        entityManager.merge(userAuthEntity);
        return userAuthEntity;
    }



}
