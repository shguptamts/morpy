package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.ErrorMessage;
import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {

    @Autowired
    private AnswerDao answerDao;

    public AnswerEntity getAnswerByUuid(String answerUuid) throws AnswerNotFoundException {

        AnswerEntity answerEntity = answerDao.getAnswerByUuid(answerUuid);
        if(answerEntity == null){
            throw new AnswerNotFoundException("ANS-001", ErrorMessage.ANSWER_UUID_DOES_NOT_EXIST.toString());
        }
        return  answerEntity;
    }

    @Transactional
    public void delete(AnswerEntity answerEntity) {
        answerDao.delete(answerEntity);

    }

    /** Authorize the delete operation on an answer
     * Only an owner or admin can delete the answer
     * @param answerEntity  answer entity, containing owner details
     * @param userEntity    user performing delete operation
     * @return  true if user can delete the answer
     * @throws AuthorizationFailedException exception is thrown if user is not allowed to delete the answer
     */
    public boolean authorizeDeleteOp(AnswerEntity answerEntity, UserEntity userEntity) throws AuthorizationFailedException {
        boolean isOwner = answerEntity.getUser().getUuid().equals(userEntity.getUuid());
        boolean isAdmin = userEntity.getRole().equals("admin");
        if( isOwner || isAdmin){
            return true;
        }else{
            throw new AuthorizationFailedException("ATHR-003", ErrorMessage.OWNER_OR_ADMIN_CAN_DELETE_ANSWER.toString());
        }
    }

    public boolean authorizeEditOp(AnswerEntity answerEntity, UserEntity userEntity) throws AuthorizationFailedException {
        boolean isOwner = answerEntity.getUser().getUuid().equals(userEntity.getUuid());
        if( !isOwner ){
           throw new AuthorizationFailedException("ATHR-003", ErrorMessage.OWNER_CAN_EDIT_ANSWER.toString());
        }
        return true;
    }

    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        return answerDao.createAnswer(answerEntity);
    }
    public AnswerEntity editAnswer(AnswerEntity answerEntity) {
        return answerDao.editAnswer(answerEntity);
    }


}
