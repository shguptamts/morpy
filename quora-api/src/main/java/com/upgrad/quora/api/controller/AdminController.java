package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserBusinessService;
//import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
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

   // @Autowired
   // private UserService userService;

    @Autowired
    private UserBusinessService userBusinessService;

    @GetMapping(path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@RequestHeader("authorization") final String authorization,
                                                                      @PathVariable("userId") final String userUuid) throws Exception { // This will change

        UserEntity userEntityLoggedIn = authenticationService.validateTokenForDeleteUserEndpoint(authorization);
        UserEntity userEntityToDelete = userBusinessService.getUserByUuid(userUuid);
        //UserEntity userEntityToDelete = userService.getUserByUuid(userUuid);

        userBusinessService.authorize(userEntityToDelete,userEntityLoggedIn);
        userBusinessService.delete(userEntityToDelete);
        //userService.authorize(userEntityToDelete,userEntityLoggedIn);
        //userService.delete(userEntityToDelete);

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id( userEntityToDelete.getUuid()).status("USER DELETED");
        return new ResponseEntity<UserDeleteResponse>( userDeleteResponse, HttpStatus.ACCEPTED);

    }

}
