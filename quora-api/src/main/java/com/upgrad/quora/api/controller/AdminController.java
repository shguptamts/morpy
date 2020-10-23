package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserBusinessService userBusinessService;

    /** Deletes user
     * @param authorization  access token
     * @param userUuid User uuid
     * @return response for delete operation
     * @throws UserNotFoundException  If uuid is invalid
     * @throws AuthorizationFailedException If access token is invalid
     */
    @DeleteMapping(path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@RequestHeader("authorization") final String authorization,
                                                                      @PathVariable("userId") final String userUuid) throws UserNotFoundException, AuthorizationFailedException {

        UserEntity userEntityLoggedIn = authenticationService.validateTokenForDeleteUserEndpoint(authorization);
        UserEntity userEntityToDelete = userBusinessService.getUserByUuid(userUuid);

        userBusinessService.authorize(userEntityLoggedIn);
        userBusinessService.delete(userEntityToDelete);


        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id( userEntityToDelete.getUuid()).status("USER DELETED");
        return new ResponseEntity<UserDeleteResponse>( userDeleteResponse, HttpStatus.ACCEPTED);

    }

}
