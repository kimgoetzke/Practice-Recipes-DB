package com.example.Recipes.DB.business;

import com.example.Recipes.DB.persistence.RecipeRepository;
import com.example.Recipes.DB.presentation.RecipeController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class RecipeService {
    public enum SearchCriterion {
        UNDEFINED(0), CATEGORY (1), NAME (2);

        private final int id;
        public int getId()
        {
            return this.id;
        }

        public SearchCriterion findById(int id) {
            switch (id) {
                case 1 -> { return CATEGORY; }
                case 2 -> { return NAME; }
                default ->  { return UNDEFINED; }
            }
        }

        SearchCriterion(int id)
        {
            this.id = id;
        }
    }

    private final RecipeRepository recipeRepository;
    private final Logger LOGGER = Logger.getLogger(RecipeController.class.getName());

    public Recipe add(Recipe recipe, boolean update) {
        recipe.setDate(LocalDateTime.now());
        if (update) {
            if (recipeRepository.findById(recipe.getId()).isEmpty()) {
                LOGGER.warning("Update failed because recipe (id=" + recipe.getId() + ") could not be found.");
                return null;
            }
            LOGGER.info("Updated: " + recipe);
            return recipeRepository.save(recipe);
        }
        LOGGER.info("Adding: " + recipe);
        return recipeRepository.save(recipe);
    }

    public Recipe get(long id) {
        if (recipeRepository.findById(id).isPresent()) {
            LOGGER.info("Recipe (id=" + id + ") found.");
            return recipeRepository.findById(id).get();
        }
        LOGGER.warning("Recipe (id=" + id + ") not found.");
        return null;
    }

    public boolean delete(long id) {
        if (recipeRepository.findById(id).isPresent()) {
            LOGGER.info("Recipe (id=" + id + ") to be deleted does exist.");
            recipeRepository.deleteById(id);
            return true;
        }
        LOGGER.warning("Recipe (id=" + id + ") to be deleted cannot be found.");
        return false;
    }

    public List<Recipe> search(String search, SearchCriterion searchCriterion) {
        return switch (searchCriterion) {
            case CATEGORY -> searchForCategory(search);
            case NAME -> searchByName(search);
            default -> null;
        };
    }

    private List<Recipe> searchByName(String search) {
        LOGGER.info("Searching by name containing '" + search + "'.");
        return recipeRepository.findByNameContainingIgnoreCaseOrderByDateDesc(search);
    }

    private List<Recipe> searchForCategory(String search) {
        LOGGER.info("Searching for category='" + search + "'.");
        return recipeRepository.findByCategoryIgnoreCaseOrderByDateDesc(search);
    }
}
