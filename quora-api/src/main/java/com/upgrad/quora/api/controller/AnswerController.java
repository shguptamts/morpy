package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 */
@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    /**
     * Get all answers speccific to question
     * @param authorization Access-token
     * @param questionUuid  question uuid
     * @return list of answers to the question
     * @throws AuthorizationFailedException invalid access token
     * @throws InvalidQuestionException invalid question uuid
     */
    @GetMapping(path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@RequestHeader("authorization") final String authorization,
                                                                               @PathVariable("questionId") final String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {

        UserEntity userEntity = authenticationService.validateTokenForGetAllAnswersEndpoint(authorization);
        QuestionEntity questionEntity =  questionService.getQuestionByUuidForGetAllAnswerEnpoint(questionUuid);

        List<AnswerDetailsResponse> answerDetailsResponseList  = new ArrayList<>();

        //convert and add Answer Entities of a question to answer details response list
        questionEntity.getAnswers()
                .stream()
                .forEach(x -> {
                    AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse()
                            .id(x.getUuid())
                            .questionContent(x.getQuestion().getContent())
                            .answerContent(x.getAns());
                    answerDetailsResponseList.add(answerDetailsResponse);
                });

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList, HttpStatus.OK);
    }

    /** Deletes an answer
     * @param authorization Access-token
     * @param answerUuid answer uuid
     * @return answer uuid and status
     * @throws AuthorizationFailedException invalid access-token
     * @throws AnswerNotFoundException invalid answer uuid
     */
    @DeleteMapping(path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> delete(@RequestHeader("authorization") final String authorization,
                                                       @PathVariable("answerId") final String answerUuid) throws AuthorizationFailedException, AnswerNotFoundException {

        UserEntity userEntity =  authenticationService.validateTokenForDeleteAnswerEndpoint(authorization);
        AnswerEntity  answerEntity = answerService.getAnswerByUuid(answerUuid);

        answerService.authorizeDeleteOp(answerEntity, userEntity);
        answerService.delete(answerEntity);

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id( answerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>( answerDeleteResponse, HttpStatus.ACCEPTED);

    }

    /**
     * Creates an anwer
     * @param authorization Access-token
     * @param questionUuid question uuid
     * @param answerRequest answer request body
     * @return Answer uuid and status
     * @throws AuthorizationFailedException invalid access-token
     * @throws InvalidQuestionException inavild question uuid
     */
    @PostMapping(path = "/question/{questionId}/answer/create",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization,
                                                       @PathVariable("questionId") final String questionUuid,
                                                       AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity =  questionService.getQuestionByUuidForCreateAnswerEnpoint(questionUuid);
        UserEntity userEntity = authenticationService.validateTokenForCreateAnswerEndpoint(authorization);

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setUser(userEntity);
        answerEntity = answerService.createAnswer(answerEntity);

        AnswerResponse answerResponse = new AnswerResponse().id(answerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }


    /**
     * Edit an answer
     *
     * @param authorization   Access-token
     * @param answeruuid    Answer uuid
     * @param answerEditRequest edit request
     * @return answer uuid and status
     * @throws AuthorizationFailedException Invalid access token
     * @throws AnswerNotFoundException Invalid answer uuid
     */
    @PutMapping(path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@RequestHeader("authorization") final String authorization,
                                                         @PathVariable("answerId") final String answeruuid,
                                                         AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, AnswerNotFoundException {

        UserEntity userEntity = authenticationService.validateTokenForEditAnswerEndpoint(authorization);
        AnswerEntity answerEntity = answerService.getAnswerByUuid(answeruuid);

        answerService.authorizeEditOp(answerEntity, userEntity);

        answerEntity.setAns(answerEditRequest.getContent());
        answerService.editAnswer(answerEntity);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        answerEditResponse.setId(answerEntity.getUuid());
        answerEditResponse.setStatus("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }


}
