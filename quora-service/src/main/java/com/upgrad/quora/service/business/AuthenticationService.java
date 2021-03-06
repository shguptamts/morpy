package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ErrorMessage;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    /** Authenticate the user
     * @param username  username
     * @param password password
     * @return access-token
     * @throws AuthenticationFailedException if invalid username
     */
    @Transactional
    public UserAuthEntity authenticate(String username, String password) throws AuthenticationFailedException {

        UserEntity userEntity = userDao.getUserByUserName(username);
        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        String encryptedPassword =  PasswordCryptographyProvider.encrypt(password, userEntity.getSalt());
        if(userEntity.getPassword().equals(encryptedPassword)){

            JwtTokenProvider tokenProvider = new JwtTokenProvider(encryptedPassword);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            UserAuthEntity userAuthEntity = new UserAuthEntity();
            userAuthEntity.setUser(userEntity);
            userAuthEntity.setUuid(UUID.randomUUID().toString());
            userAuthEntity.setAccessToken(tokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));
            userAuthEntity.setLoginAt(now);
            userAuthEntity.setExpiresAt(expiresAt);

            return userAuthDao.createAuthToken(userAuthEntity);

        }else{
            throw new AuthenticationFailedException("ATH-002","Password failed");
        }
    }

    @Transactional
    public String signOut(String accessToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserByAccessToken(accessToken);
        if(userAuthEntity == null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }

        userAuthEntity.setLogoutAt(ZonedDateTime.now());
        userAuthDao.updateUserAuth(userAuthEntity);
        return userAuthEntity.getUser().getUuid();

    }

    /**
     * Method validates that access-token is not expired and user is signed in
     * @param authorization  access-token
     * @param errorMessage  customized error message if access-token is expired
     * @return User of corresponding access-token
     * @throws AuthorizationFailedException
     */
    private UserEntity validateToken(String authorization, final ErrorMessage errorMessage) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserByAccessToken(authorization);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        else if(userAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002", errorMessage.toString());
        }

        return userAuthEntity.getUser();
    }

    /** Method validates that access-token is not expired and user is signed in
     * @param authorization  access-token
     * @return User of corresponding access-token
     * @throws AuthorizationFailedException
     */
    public UserEntity validateTokenForGetAllAnswersEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_GET_ALL_ANSWER );
    }


    public UserEntity validateTokenForDeleteAnswerEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_DELETE_AN_ANSWER);
    }

    public UserEntity validateTokenForDeleteQuestionEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_DELETE_A_QUESTION);
    }

    public UserEntity validateTokenForCreateAnswerEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_POST_AN_ANSWER);
    }
    public UserEntity validateTokenForEditAnswerEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_EDIT_AN_ANSWER);
    }
    public UserEntity validateTokenForGetAllQuestionsPostedBySpecificUserEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_GET_ALL_QUESTIONS_POSTED_BY_A_USER);
    }

    public UserEntity validateTokenForDeleteUserEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_DELETE_USER);
    }

    public UserEntity validateTokenForGetUserEndpoint(String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_USER_DETAILS);
    }

    public UserEntity validateTokenForCreateQuestionEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_POST_A_QUESTION);
    }
    public UserEntity validateTokenForGetAllQuestionsEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_GET_ALL_QUESTION);
    }

    public UserEntity validateTokenForEditQuestionEndpoint(final String authorization) throws AuthorizationFailedException {
        return this.validateToken(authorization, ErrorMessage.USER_SIGNED_OUT_CAN_NOT_EDIT_A_QUESTION);
    }

}
