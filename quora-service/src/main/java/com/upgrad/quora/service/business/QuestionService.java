package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ErrorMessage;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    public QuestionEntity getQuestionByUuid(String questionUuid) throws InvalidQuestionException {
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }
        return questionEntity;
    }

    /** Authorize the delete operation on a question
     * Only an owner or admin can delete the question
     * @param questionEntity  question entity, containing owner details
     * @param userEntity    user performing delete operation
     * @return  true if user can delete the answer
     * @throws AuthorizationFailedException exception is thrown if user is not allowed to delete the question
     */
    public boolean authorize(QuestionEntity questionEntity, UserEntity userEntity) throws AuthorizationFailedException {
        boolean isOwner = questionEntity.getUser().getUuid().equals(userEntity.getUuid());
        boolean isAdmin = userEntity.getRole().equals("admin");
        if( isOwner || isAdmin){
            return true;
        }else{
            throw new AuthorizationFailedException("ATHR-003", ErrorMessage.OWNER_OR_ADMIN_CAN_DELETE_QUESTION.toString());
        }
    }

    @Transactional
    public void delete(QuestionEntity questionEntity) {
        questionDao.delete(questionEntity);
    }



     /**
   * Creates question in the DB if the accessToken is valid.
   *
   * @param accessToken accessToken of the user for valid authentication.
   * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if
   *     the user has already signed out.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public QuestionEntity createQuestion(QuestionEntity questionEntity, final String accessToken)
      throws AuthorizationFailedException {
    UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    } else if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException(
          "ATHR-002", "User is signed out.Sign in first to post a question");
    }
    questionEntity.setDate(ZonedDateTime.now());
    questionEntity.setUuid(UUID.randomUUID().toString());
    questionEntity.setUserEntity(userAuthEntity.getUserEntity());
    return questionDao.createQuestion(questionEntity);
  }

    /**
     * Gets all the questions in the DB.
     *
     * @param accessToken accessToken of the user for valid authentication.
     * @return List of QuestionEntity
     * @throws AuthorizationFailedException ATHR-001 - if user token is not present in DB. ATHR-002 if
     *     the user has already signed out.
     */
    public List<QuestionEntity> getAllQuestions(final String accessToken)
            throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException(
                    "ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        return questionDao.getAllQuestions();
    }

}
