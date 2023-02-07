package com.example.Recipes.DB.presentation;

import com.example.Recipes.DB.business.AppUser;
import com.example.Recipes.DB.business.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import java.util.logging.Logger;

@RestController
@Validated
@RequiredArgsConstructor
public class AuthenticationController {
    private final AppUserService appUserService;
    private final Logger LOGGER = Logger.getLogger(RecipeController.class.getName());

    @Operation(summary = "Register as a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully created", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid credentials provided", content = @Content)})
    @PostMapping("/api/register")
    public ResponseEntity<AppUser> registerUser(@Valid @RequestBody AppUser appUser) {
        LOGGER.info("New user registration request received for '" + appUser.getEmail() + "'.");
        if (appUserService.add(appUser)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
