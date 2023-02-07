package com.example.Recipes.DB.business;

import com.example.Recipes.DB.persistence.RecipeRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.Recipes.DB.business.RecipeService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@Tag("unit")
@SpringBootTest
class RecipeServiceTest {
    @MockBean
    RecipeRepository recipeRepository;

    @Autowired
    RecipeService recipeService;

    long nonExistentRecipeId = 99L;

    Recipe recipe = new Recipe(0, "Fresh Mint Tea", "Beverage", LocalDateTime.now(),
            "Light, aromatic and refreshing beverage, ...",
            new ArrayList<>() {{
                add("boiled water");
                add("honey");
                add("fresh mint leaves");
            }},
            new ArrayList<>() {{
                add("Boil water");
                add("Pour boiling hot water into a mug");
                add("Add fresh mint leaves");
                add("Mix and let the mint leaves seep for 3-5 minutes");
                add("Add honey and mix again");
            }},
            new AppUser("test@test.com", "password1", null)
    );

    List<Recipe> listOfRecipes = new ArrayList<>() {{
        add(recipe);
    }};

    @Test
    void whenAdd_thenSucceed() {
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        assertEquals(recipe, recipeService.add(recipe, false));
    }

    @Test
    void givenRecipeExists_whenUpdate_thenSucceed() {
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.ofNullable(recipe));
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        assertEquals(recipe, recipeService.add(recipe, true));
    }

    @Test
    void givenRecipeNotFound_whenUpdate_thenReturnNull() {
        Recipe nonExistentRecipe = recipe;
        nonExistentRecipe.setId(nonExistentRecipeId);
        when(recipeRepository.findById(nonExistentRecipe.getId())).thenReturn(Optional.empty());
        assertNull(recipeService.add(nonExistentRecipe, true));
        verify(recipeRepository, never()).save(any());
    }

    @Test
    void givenRecipeExists_whenGet_thenSucceed() {
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.ofNullable(recipe));
        assertEquals(recipe, recipeService.get(recipe.getId()));
    }

    @Test
    void givenRecipeNotFound_whenGet_thenReturnNull() {
        Recipe nonExistentRecipe = recipe;
        nonExistentRecipe.setId(nonExistentRecipeId);
        when(recipeRepository.findById(nonExistentRecipe.getId())).thenReturn(Optional.empty());
        assertNull(recipeService.get(nonExistentRecipe.getId()));
    }

    @Test
    void givenRecipeExists_whenDelete_thenSucceed() {
        when(recipeRepository.findById(recipe.getId())).thenReturn(Optional.ofNullable(recipe));
        assertTrue(recipeService.delete(recipe.getId()));
    }

    @Test
    void givenRecipeNotFound_whenDelete_thenReturnFalse() {
        when(recipeRepository.findById(nonExistentRecipeId)).thenReturn(Optional.empty());
        assertFalse(recipeService.delete(nonExistentRecipeId));
        verify(recipeRepository, never()).delete(any());
    }

    @Test
    void whenSearchByName_thenReturnRecipeList() {
        when(recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc("mint")).thenReturn(listOfRecipes);
        assertThat(listOfRecipes).isEqualTo(recipeService.search("mint", SearchCriterion.NAME));
    }

    @Test
    void whenSearchByCategory_thenReturnRecipeList() {
        when(recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc("Beverage")).thenReturn(listOfRecipes);
        assertThat(listOfRecipes).isEqualTo(recipeService.search("Beverage", SearchCriterion.CATEGORY));
    }

    @Test
    void whenInvalidSearch_thenReturnNull() {
        assertNull(recipeService.search("", SearchCriterion.UNDEFINED));
    }

    @Test
    void whenUsingSearchCriteria_thenReturnOrFindId() {
        SearchCriterion searchCriterion = SearchCriterion.UNDEFINED;
        assertEquals(0, searchCriterion.getId());
        assertEquals(SearchCriterion.UNDEFINED, searchCriterion.findById(0));
        assertEquals(SearchCriterion.CATEGORY, searchCriterion.findById(1));
        assertEquals(SearchCriterion.NAME, searchCriterion.findById(2));
    }
}