package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ErrorMessage;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    private  QuestionEntity getQuestionByUuid(String questionUuid, ErrorMessage errorMessage) throws InvalidQuestionException {
        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);
        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001", errorMessage.toString());
        }
        return questionEntity;
    }

    public QuestionEntity getQuestionByUuidForGetAllAnswerEnpoint(String questionUuid) throws InvalidQuestionException {
       return getQuestionByUuid(questionUuid,ErrorMessage.QUESTION_UUID_DOES_NOT_EXIST_FOR_GET_ALL_ANSWER );
    }

    public QuestionEntity getQuestionByUuidForCreateAnswerEnpoint(String questionUuid) throws InvalidQuestionException {
        return getQuestionByUuid(questionUuid,ErrorMessage.QUESTION_UUID_DOES_NOT_EXIST_FOR_CREATE_ANSWER);
    }

    public QuestionEntity getQuestionByUuidForQuestionEndpoints(String questionUuid) throws InvalidQuestionException {
        return getQuestionByUuid(questionUuid, ErrorMessage.QUESTION_UUID_DOES_NOT_EXIST);
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

    public boolean authorizeEditOp(QuestionEntity questionEntity, UserEntity userEntity) throws AuthorizationFailedException {
        boolean isOwner = questionEntity.getUser().getUuid().equals(userEntity.getUuid());
        if( !isOwner) {
            throw new AuthorizationFailedException("ATHR-003", ErrorMessage.ONLY_OWNER_CAN_EDIT_ANSWER.toString());
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(QuestionEntity questionEntity) {
        questionDao.delete(questionEntity);
    }



    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {

        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUuid(UUID.randomUUID().toString());
        return questionDao.createQuestion(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(QuestionEntity questionEntity) {
        return questionDao.editQuestion(questionEntity);
    }


}
