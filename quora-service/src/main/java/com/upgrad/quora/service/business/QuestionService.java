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
     * @param questionUuid    question's uuid, to be deleted
     * @return  true if user can delete the answer
     * @throws AuthorizationFailedException exception is thrown if user is not allowed to delete the answer
     */
    public boolean authorize(QuestionEntity questionEntity, UserEntity userEntity, String questionUuid) throws AuthorizationFailedException {
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


}
