package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
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
public class CommonController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserBusinessService userBusinessService;


    /** Gets the user profile
     * @param authorization access token
     * @param userId user uuid of the user
     * @return UserDetails Response
     * @throws AuthorizationFailedException if authentication or authorization fails
     * @throws UserNotFoundException if uuid not found
     */
    @GetMapping(path = "/userprofile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<UserDetailsResponse> getUserProfile(@RequestHeader("authorization") final String authorization,
                               @PathVariable("userId") final String userId) throws AuthorizationFailedException, UserNotFoundException {

        authenticationService.validateTokenForGetUserEndpoint(authorization);
        UserEntity userEntity = userBusinessService.getUserByUuid(userId);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUsername())
                .emailAddress(userEntity.getEmail())
                .contactNumber(userEntity.getContactNumber())
                .aboutMe(userEntity.getAboutMe())
                .country(userEntity.getCountry())
                .dob(userEntity.getDob());

        return new ResponseEntity<UserDetailsResponse>( userDetailsResponse, HttpStatus.OK);
    }
}
