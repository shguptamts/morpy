package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ErrorMessage;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public UserEntity getUserByUuid(String userUuid) throws Exception {
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if(userEntity == null){
            throw new Exception("The user uuid whose details are to be seen does not exist");
        }
        return userEntity;
    }

    /** Authorize the delete operation on a user
     * Only an admin can delete a User
     * @param userEntityToDelete  user entity to be deleted
     * @param userEntityLoggedIn  logged in user
     * @return  true if user can delete the answer
     * @throws AuthorizationFailedException exception is thrown if user is not allowed to delete the question
     */
    public boolean authorize(UserEntity userEntityToDelete, UserEntity userEntityLoggedIn) throws AuthorizationFailedException {
        boolean isAdmin = userEntityLoggedIn.getRole().equals("admin");
        if(isAdmin){
            return true;
        }else{
            throw new AuthorizationFailedException("ATHR-003", ErrorMessage.USER_SIGNED_OUT_CAN_NOT_DELETE_USER.toString());
        }
    }

    @Transactional
    public void delete(UserEntity userEntity) {
        userDao.delete(userEntity);
    }

}
