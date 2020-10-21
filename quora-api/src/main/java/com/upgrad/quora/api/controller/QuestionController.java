package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/")
public class QuestionController {
  
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserBusinessService userService;

  /**
   * Create a question
   *
   * @param questionRequest This object has the content i.e the question.
   * @param accessToken access token to authenticate user.
   * @return UUID of the question created in DB.
   * @throws AuthorizationFailedException In case the access token is invalid.
   */
  @PostMapping(path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String accessToken,
                                                         QuestionRequest questionRequest) throws AuthorizationFailedException {

      UserEntity userEntity = authenticationService.validateTokenForCreateQuestionEndpoint(accessToken);

      QuestionEntity questionEntity = new QuestionEntity();
      questionEntity.setContent(questionRequest.getContent());
      questionEntity.setUser(userEntity);

      questionEntity = questionService.createQuestion(questionEntity);

      QuestionResponse questionResponse = new QuestionResponse();
      questionResponse.setId(questionEntity.getUuid());
      questionResponse.setStatus("QUESTION CREATED");

    return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
  }

  /**
   * Get all questions posted by any user.
   *
   * @param accessToken access token to authenticate user.
   * @return List of QuestionDetailsResponse
   * @throws AuthorizationFailedException In case the access token is invalid.
   */
  @GetMapping(path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String accessToken)throws AuthorizationFailedException {

      UserEntity userEntity = authenticationService.validateTokenForGetAllQuestionsEndpoint(accessToken);


      List<QuestionDetailsResponse> questionDetailList = userEntity.getQuestions()
              .stream()
              .map( x-> new QuestionDetailsResponse()
                      .id(x.getUuid())
                      .content(x.getContent()))
              .collect(Collectors.toList());

    return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailList, HttpStatus.OK);
  }

  /**
   * Edit a question
   *
   * @param accessToken access token to authenticate user.
   * @param questionId id of the question to be edited.
   * @param questionEditRequest new content for the question.
   * @return Id and status of the question edited.
   * @throws AuthorizationFailedException In case the access token is invalid.
   * @throws InvalidQuestionException if question with questionId doesn't exist.
   */
  @PutMapping(path = "/question/edit/{questionId}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionEditResponse> editQuestion(@RequestHeader("authorization") final String accessToken,
                                                           @PathVariable("questionId") final String questionId,
                                                           QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {

      UserEntity  userEntity = authenticationService.validateTokenForEditAnswerEndpoint(accessToken);
      QuestionEntity questionEntity = questionService.getQuestionByUuidForQuestionEndpoints(questionId);

      questionService.authorizeEditOp(questionEntity, userEntity);

      questionEntity.setContent(questionEditRequest.getContent());
      questionEntity = questionService.editQuestion(questionEntity);

      QuestionEditResponse questionEditResponse = new QuestionEditResponse();
      questionEditResponse.setId(questionEntity.getUuid());
      questionEditResponse.setStatus("QUESTION EDITED");
      return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
  }


    @DeleteMapping(path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> delete(@RequestHeader("authorization") final String authorization,
                                                         @PathVariable("questionId") final String questionUuid) throws AuthorizationFailedException, InvalidQuestionException {

        UserEntity userEntity =  authenticationService.validateTokenForDeleteQuestionEndpoint(authorization);
        QuestionEntity questionEntity = questionService.getQuestionByUuidForQuestionEndpoints(questionUuid);

        questionService.authorize(questionEntity, userEntity);
        questionService.delete(questionEntity);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id( questionEntity.getUuid()).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>( questionDeleteResponse, HttpStatus.ACCEPTED);

    }

    /**
     * Get all questions posted by a user with given userId.
     *
     * @param userId of the user for whom we want to see the questions asked by him
     * @param authorization access token to authenticate user.
     * @return List of QuestionDetailsResponse
     * @throws AuthorizationFailedException In case the access token is invalid.
     */
    @GetMapping( path = "question/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser( @RequestHeader("authorization") final String authorization,
                                                                                @PathVariable("userId") String userId) throws AuthorizationFailedException, UserNotFoundException {

        UserEntity  currentUser = authenticationService.validateTokenForGetAllQuestionsPostedBySpecificUserEndpoint(authorization);
        UserEntity  requestedUser = userService.getUserByUuid(userId);

        //convert and add question Entities of a user to question details response list
        List<QuestionDetailsResponse> questionDetailResponseList = requestedUser.getQuestions()
                .stream()
                .map( x-> new QuestionDetailsResponse()
                        .id(x.getUuid())
                        .content(x.getContent()))
                .collect(Collectors.toList());
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailResponseList, HttpStatus.OK);
    }
}
