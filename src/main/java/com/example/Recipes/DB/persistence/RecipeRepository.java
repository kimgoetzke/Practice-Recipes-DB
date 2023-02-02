package com.example.Recipes.DB.persistence;

import com.example.Recipes.DB.business.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByCategoryIgnoreCaseOrderByDateDesc(String name);
    List<Recipe> findByNameContainingIgnoreCaseOrderByDateDesc(String name);
}
