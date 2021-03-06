package com.upgrad.quora.service.common;


public enum ErrorMessage{

    /*
    Error messages If the user has signed out, but tries to perform operation on Answer's endpoint
     */
    USER_SIGNED_OUT_CAN_NOT_POST_AN_ANSWER ("User is signed out.Sign in first to post an answer"),
    USER_SIGNED_OUT_CAN_NOT_EDIT_AN_ANSWER ("User is signed out.Sign in first to edit an answer"),
    USER_SIGNED_OUT_CAN_NOT_DELETE_AN_ANSWER ("User is signed out.Sign in first to delete an answer"),
    USER_SIGNED_OUT_CAN_NOT_GET_ALL_ANSWER ("User is signed out.Sign in first to get the answers"),

    /*
    Error messages If the user has signed out, but tries to perform operation on Question's endpoint
     */
    USER_SIGNED_OUT_CAN_NOT_POST_A_QUESTION  ( "User is signed out.Sign in first to post a question"),
    USER_SIGNED_OUT_CAN_NOT_EDIT_A_QUESTION ( "User is signed out.Sign in first to edit the question"),
    USER_SIGNED_OUT_CAN_NOT_DELETE_A_QUESTION ( "User is signed out.Sign in first to delete a question"),
    USER_SIGNED_OUT_CAN_NOT_GET_ALL_QUESTION ( "User is signed out.Sign in first to get all questions"),
    USER_SIGNED_OUT_CAN_NOT_GET_ALL_QUESTIONS_POSTED_BY_A_USER("User is signed out.Sign in first to get all questions posted by a specific user"),

    /*
    Other error messages
     */
    USER_SIGNED_OUT_CAN_NOT_USER_DETAILS( "User is signed out.Sign in first to get user details"),
    USER_UUID_DOES_NOT_EXIST ("User with entered uuid does not exist"),
    ANSWER_UUID_DOES_NOT_EXIST ("Entered answer uuid does not exist"),
    QUESTION_UUID_DOES_NOT_EXIST ("Entered question uuid does not exist"),
    QUESTION_UUID_DOES_NOT_EXIST_FOR_GET_ALL_ANSWER ("The question with entered uuid whose details are to be seen does not exist"),
    QUESTION_UUID_DOES_NOT_EXIST_FOR_CREATE_ANSWER("The question entered is invalid"),
    OWNER_OR_ADMIN_CAN_DELETE_QUESTION("Only the question owner or admin can delete the question"),
    OWNER_CAN_EDIT_ANSWER("Only the answer owner can edit the answer"),
    ONLY_OWNER_CAN_EDIT_ANSWER ("Only the question owner can edit the question"),
    USER_SIGNED_OUT_CAN_NOT_DELETE_USER("User is signed out"),
    OWNER_OR_ADMIN_CAN_DELETE_ANSWER("Only the answer owner or admin can delete the answer"),
    ONLY_ADMIN_CAN_DELETE_USER("Unauthorized Access, Entered user is not an admin");


    private String value;

    ErrorMessage(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
