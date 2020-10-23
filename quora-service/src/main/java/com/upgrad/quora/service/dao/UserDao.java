package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity getUserByUserName(final String username){
        try{
            return entityManager.createNamedQuery("UserEntity.userByUserName", UserEntity.class).setParameter("username",username).getSingleResult();
        } catch(NoResultException nre)
        {
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email){
        try{
            return entityManager.createNamedQuery("UserEntity.userByEmail", UserEntity.class)
                    .setParameter("email",email).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByUuid(final String uuid){
        try{
            return entityManager.createNamedQuery("UserEntity.userByUuid", UserEntity.class)
                    .setParameter("uuid",uuid).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }


    public UserEntity createUser(final UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    public void delete(UserEntity userEntity) {
        entityManager.remove(userEntity);

    }
}
