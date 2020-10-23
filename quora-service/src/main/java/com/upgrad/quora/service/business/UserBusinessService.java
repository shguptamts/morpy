package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ErrorMessage;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;


    /*
        checks if a user exists in db with username passed
     */
    public boolean checkUserNameExists(String username)throws  SignUpRestrictedException{
        UserEntity userEntity = userDao.getUserByUserName(username);
        if(userEntity != null){
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }
        return false;
    }

    /*
        checks if a user exists in db with email passed
     */
    public boolean checkEmailExists(String email)throws  SignUpRestrictedException{
        UserEntity userEntity = userDao.getUserByEmail(email);
        if(userEntity != null){
            throw  new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }
        return false;
    }

    @Transactional
    public UserEntity signUp( UserEntity userEntity) {
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }

    @Transactional
    public void delete(UserEntity userEntity) {
        userDao.delete(userEntity);
    }

    /** Authorize the delete operation on a user
     * Only an admin can delete a User
     * @param userEntityLoggedIn  logged in user
     * @return  true if user can delete the answer
     * @throws AuthorizationFailedException exception is thrown if user is not allowed to delete the question
     */
    public boolean authorize( UserEntity userEntityLoggedIn) throws AuthorizationFailedException {
        boolean isAdmin = userEntityLoggedIn.getRole().equals("admin");
        if(isAdmin){
            return true;
        }else{
            throw new AuthorizationFailedException("ATHR-003", ErrorMessage.ONLY_ADMIN_CAN_DELETE_USER.toString());
        }
    }


    /** Get user by uuid
     * @param userId  uuid
     * @return user entity
     * @throws UserNotFoundException if user does not exist by uuid
     */
    public UserEntity getUserByUuid(String userId) throws UserNotFoundException {
        UserEntity userEntity = userDao.getUserByUuid(userId);
        if(userEntity == null ){
            throw new UserNotFoundException("USR-001", ErrorMessage.USER_UUID_DOES_NOT_EXIST.toString());
        }
        return userEntity;
    }
}
