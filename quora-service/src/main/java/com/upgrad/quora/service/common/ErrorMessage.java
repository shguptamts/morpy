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

    /*
    Other error messages
     */

    ANSWER_UUID_DOES_NOT_EXIST ("Entered answer uuid does not exist"),
    OWNER_OR_ADMIN_CAN_DELETE_ANSWER("Only the answer owner or admin can delete the answer"),
    OWNER_OR_ADMIN_CAN_DELETE_QUESTION("Only the question owner or admin can delete the question");
    OWNER_CAN_EDIT_ANSWER("Only the answer owner can edit the answer"),
    OWNER_OR_ADMIN_CAN_DELETE_ANSWER("Only the answer owner or admin can delete the answer");


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
