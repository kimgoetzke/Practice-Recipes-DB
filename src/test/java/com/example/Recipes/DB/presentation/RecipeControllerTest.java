package com.example.Recipes.DB.presentation;

import com.example.Recipes.DB.business.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RecipeController.class)
@Import(SecurityConfig.class)
@Tag("unit")
public class RecipeControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    RecipeController recipeController;

    @MockBean
    RecipeService recipeService;

    @MockBean
    AppUserService appUserService;

    @Autowired
    ObjectMapper objectMapper;

    List<Recipe> listOfRecipes = Arrays.asList(
            new Recipe(0, "Mint Julep", "Beverage", LocalDateTime.now(),
                    "Light, aromatic and refreshing beverage, ...",
                    new ArrayList<>() {{ add("bourbon"); add("fresh mint leaves"); }},
                    new ArrayList<>() {{ add("Do everything that has to be done"); }},
                    new AppUser("test@hotmail.com", "password1", null)),
            new Recipe(1, "Peppermint Tea", "Beverage", LocalDateTime.now(), "Yummy",
                    new ArrayList<>() {{ add("Everything you need"); }},
                    new ArrayList<>() {{ add("Get it done"); }},
                    new AppUser("test@google.com", "password1", null))
    );

    static HttpHeaders getDefaultHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        return responseHeaders;
    }

    @Test
    void givenUnauthorisedUser_whenGet_thenReturnForbidden() throws Exception {
        when(recipeService.get(anyLong())).thenReturn(listOfRecipes.get(1));
        mvc.perform(get("/api/recipe/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        verify(recipeService, never()).get(anyLong());
    }

    @Test
    @WithMockUser(value = "test@gmail.com", password = "password1")
    public void givenRecipeExists_whenGet_thenReturnRecipe() throws Exception {
        when(recipeService.get(anyLong())).thenReturn(listOfRecipes.get(1));
        mvc.perform(get("/api/recipe/1")).andExpect(status().isOk());
        assertThat(recipeController.getRecipe(1).getBody()).isSameAs(listOfRecipes.get(1));
    }

    @Test
    @WithMockUser(value = "test@gmail.com", password = "password1")
    void givenRecipeDoesNotExist_whenGet_thenFail() throws Exception {
        when(recipeService.get(anyLong())).thenReturn(null);
        mvc.perform(get("/api/recipe/1")).andExpect(status().isNotFound());
        assertNull(recipeController.getRecipe(1).getBody());
    }

    @Test
    void givenUnauthorisedUser_whenAddRecipe_thenReturnForbidden() throws Exception {
        // Given
        when(recipeService.add(any(), anyBoolean())).thenReturn(listOfRecipes.get(1));
        String json = objectMapper.writeValueAsString(listOfRecipes.get(1));

        // When
        ResultActions result = mvc.perform(post("/api/recipe/new")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json));

        // Then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
        verify(recipeService, never()).get(anyLong());
    }

    @Test
    @WithMockUser(value = "test@gmail.com", password = "password1")
    public void givenValidRecipe_whenAddRecipe_thenSucceed() throws Exception {
        // Given
        when(recipeService.add(any(), anyBoolean())).thenReturn(listOfRecipes.get(1));
        UsernamePasswordAuthenticationToken authenticationToken =
                UsernamePasswordAuthenticationToken.authenticated("test@gmail.com", "password1", null);
        String json = objectMapper.writeValueAsString(listOfRecipes.get(1));

        // When
        ResultActions result = mvc.perform(post("/api/recipe/new")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json));

        // Then
        result.andExpect(status().isOk());
        assertThat(recipeController.addRecipe(listOfRecipes.get(1), authenticationToken))
                .isEqualTo(ResponseEntity.ok(Collections.singletonMap("id", recipeService.add(listOfRecipes.get(1), false).getId())));
    }

    @Test
    @WithMockUser(value = "test@gmail.com", password = "password1")
    public void givenNotOwner_whenUpdate_thenReturnForbidden() throws Exception {
        // Given
        AppUser appUser = new AppUser("test@gmail.com", "password1", null);
        when(recipeService.get(anyLong())).thenReturn(listOfRecipes.get(1));
        when(appUserService.get(anyString())).thenReturn(appUser);
        String json = objectMapper.writeValueAsString(listOfRecipes.get(1));

        // When
        ResultActions result = mvc.perform(put("/api/recipe/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json));

        // Then
        result.andExpect(status().isForbidden());
        verify(recipeService, never()).add(any(), anyBoolean());
    }

    @Test
    @WithMockUser(value = "test@google.com", password = "password1")
    public void givenOwner_whenUpdate_thenSucceed() throws Exception {
        // Given
        AppUser appUser = new AppUser("test@google.com", "password1", null);
        when(recipeService.get(anyLong())).thenReturn(listOfRecipes.get(1));
        when(recipeService.add(any(), anyBoolean())).thenReturn(listOfRecipes.get(1));
        when(appUserService.get(anyString())).thenReturn(appUser);
        String json = objectMapper.writeValueAsString(listOfRecipes.get(1));

        // When
        ResultActions result = mvc.perform(put("/api/recipe/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json));

        // Then
        result.andExpect(status().isNoContent());
        verify(recipeService).add(any(), anyBoolean());
    }

    @Test
    @WithMockUser(value = "test@gmail.com", password = "password1")
    public void givenRecipeNotFound_whenUpdate_thenReturnNotFound() throws Exception {
        // Given
        AppUser appUser = new AppUser("test@gmail.com", "password1", null);
        when(recipeService.get(anyLong())).thenReturn(null);
        when(appUserService.get(anyString())).thenReturn(appUser);
        String json = objectMapper.writeValueAsString(listOfRecipes.get(1));

        // When
        ResultActions result = mvc.perform(put("/api/recipe/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json));

        // Then
        result.andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthorisedUser_whenDelete_thenReturnForbidden() throws Exception {
        // Given
        when(recipeService.delete(anyLong())).thenReturn(true);

        // When
        ResultActions result = mvc.perform(delete("/api/recipe/1"));

        // Then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
        verify(recipeService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(value = "test@hotmail.com", password = "password1")
    void givenNotOwner_whenDelete_thenReturnForbidden() throws Exception {
        // Given
        when(recipeService.delete(anyLong())).thenReturn(true);
        when(recipeService.get(anyLong())).thenReturn(listOfRecipes.get(1));

        // When
        ResultActions result = mvc.perform(delete("/api/recipe/1"));

        // Then
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
        verify(recipeService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(value = "test@hotmail.com", password = "password1")
    void givenRecipeNotFound_whenDelete_thenReturnNotFound() throws Exception {
        // Given
        when(recipeService.get(anyLong())).thenReturn(null);

        // When
        ResultActions result = mvc.perform(delete("/api/recipe/1"));

        // Then
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        verify(recipeService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(value = "test@google.com", password = "password1")
    public void givenOwner_whenDelete_thenSucceed() throws Exception {
        // Given
        when(recipeService.get(anyLong())).thenReturn(listOfRecipes.get(1));
        when(recipeService.delete(anyLong())).thenReturn(anyBoolean());

        // When
        ResultActions result = mvc.perform(delete("/api/recipe/1"));

        // Then
        result.andExpect(status().isNoContent());
        verify(recipeService).delete(anyLong());
    }

    @Test
    void whenSearchByName_thenSucceed() {
        when(recipeService.search("mint", RecipeService.SearchCriterion.NAME)).thenReturn(listOfRecipes);
        assertEquals(recipeController.searchRecipes(null, "mint"),
                new ResponseEntity<>(listOfRecipes, getDefaultHeaders(), HttpStatus.OK));
    }

    @Test
    void givenNoSearchParameters_whenSearch_thenReturnBadRequest() {
        when(recipeService.search("mint", RecipeService.SearchCriterion.NAME)).thenReturn(null);
        assertEquals(recipeController.searchRecipes(null, null),
                new ResponseEntity<>(null, getDefaultHeaders(), HttpStatus.BAD_REQUEST));
        verify(recipeService, never()).search(anyString(), any());
    }

    @Test
    void givenNotFound_whenSearchByName_thenReturnNotFound() {
        when(recipeService.search("mint", RecipeService.SearchCriterion.NAME)).thenReturn(null);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        assertEquals(recipeController.searchRecipes(null, "mint"),
                new ResponseEntity<>(null, responseHeaders, HttpStatus.NOT_FOUND));
    }

    @Test
    @WithMockUser(value = "test@gmail.com", password = "password1")
    public void searchRecipes_ValidParams_ReturnRecipes() throws Exception {
        when(recipeService.get(anyLong())).thenReturn(listOfRecipes.get(1));
        mvc.perform(get("/api/recipe/1")).andExpect(status().isOk());
        verify(recipeService).get(anyLong());
    }
}
