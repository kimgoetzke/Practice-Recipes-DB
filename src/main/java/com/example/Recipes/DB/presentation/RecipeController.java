package com.example.Recipes.DB.presentation;

import com.example.Recipes.DB.business.Recipe;
import com.example.Recipes.DB.business.RecipeService;
import com.example.Recipes.DB.business.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
public class RecipeController {
    private final RecipeService recipeService;
    private final AppUserService appUserService;
    private final Logger LOGGER = Logger.getLogger(RecipeController.class.getName());

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        return responseHeaders;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable long id) {
        LOGGER.info("GET request for recipe (id=" + id + ") received.");
        Recipe recipe = recipeService.get(id);
        if (recipe == null) {
            return new ResponseEntity<>(null, getDefaultHeaders(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(recipe, getDefaultHeaders(), HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Map<String, Long>> addRecipe(@Valid @RequestBody Recipe recipe,
                                                          Authentication authentication) {
        recipe.setAppUser(appUserService.get(authentication.getName()));
        LOGGER.info("POST request received for: " + recipe);
        return ResponseEntity.ok(Collections.singletonMap("id", recipeService.add(recipe, false).getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Long>> updateRecipe(@PathVariable long id,
                                                 @Valid @RequestBody Recipe recipe,
                                                 Authentication authentication) {
        recipe.setId(id);
        recipe.setAppUser(appUserService.get(authentication.getName()));
        LOGGER.info("PUT request received for: " + recipe);
        if (recipeService.get(id) == null) {
            LOGGER.warning("Recipe doesn't exist. Request declined.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else if (recipeService.get(id).getAppUser().getEmail().equals(authentication.getName())) {
            LOGGER.info("User is the owner of the recipe and is allowed to update it.");
            return new ResponseEntity<>(Collections.singletonMap("id", recipeService.add(recipe, false)
                    .getId()), getDefaultHeaders(), HttpStatus.NO_CONTENT);
        } else {
            LOGGER.warning("User is not the owner of the recipe or it doesn't exist. Request declined.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable long id,
                                          Authentication authentication){
        LOGGER.info("DELETE request for recipe " + id + " from user '" + authentication.getName() + "' received.");
        if (recipeService.get(id) == null) {
            LOGGER.warning("Recipe doesn't exist. Request declined.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else if (recipeService.get(id).getAppUser().getEmail().equals(authentication.getName())) {
            LOGGER.info("User is the owner of the recipe and is allowed to delete it.");
            recipeService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            LOGGER.warning("User is not the owner of the recipe or it doesn't exist. Request declined.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(@RequestParam(required = false) String category,
                                                      @RequestParam(required = false) String name) {
        LOGGER.info("GET request with search parameters received - category=" + category + ", name=" + name + ".");

        // Check which, if any, RequestParam's are not null and not empty
        RecipeService.SearchCriterion searchCriterion = RecipeService.SearchCriterion.UNDEFINED;
        String nonEmptyString = null;
        String[] strings = new String[]{category, name};
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            if (str != null && !str.isEmpty()) {
                if (nonEmptyString == null) {
                    nonEmptyString = str;
                    searchCriterion = searchCriterion.findById(i + 1);
                } else {
                    nonEmptyString = null;
                    break;
                }
            }
        }

        // Attempt search and return result
        if(nonEmptyString != null) {
            List<Recipe> result = recipeService.search(nonEmptyString.toLowerCase(), searchCriterion);
            if (result == null) {
                return new ResponseEntity<>(null, getDefaultHeaders(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(result, getDefaultHeaders(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, getDefaultHeaders(), HttpStatus.BAD_REQUEST);
    }
}
