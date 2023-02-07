package com.example.Recipes.DB.presentation;

import com.example.Recipes.DB.business.AppUser;
import com.example.Recipes.DB.business.AppUserService;
import com.example.Recipes.DB.business.RecipeService;
import com.example.Recipes.DB.business.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@Import(SecurityConfig.class)
@Tag("unit")
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AppUserService appUserService;

    @MockBean
    private RecipeService recipeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void whenValidUserRegistration_thenSucceed() throws Exception {
        // Given
        AppUser appUser = new AppUser("test@gmail.com", "password1", null);
        when(appUserService.add(any())).thenReturn(true);

        // When
        ResultActions response = mvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("utf-8")
                .content("{\"email\":\"" + appUser.getEmail() + "\",\"password\":\"" + appUser.getPassword() + "\"}"));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        verify(appUserService).add(appUser);
    }

    @Test
    void whenInvalidUserAuthenticating_thenFail() throws Exception {
        // Given
        AppUser appUser = new AppUser("anotheremail@gmail.com", "1", null);
        String json = "{\"email\":\"" + appUser.getEmail() + "\",\"password\":\"" + appUser.getPassword() + "\"}";

        // When
        ResultActions response = mvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding("utf-8")
                .content(json));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        verify(appUserService, never()).add(any());
    }
}
