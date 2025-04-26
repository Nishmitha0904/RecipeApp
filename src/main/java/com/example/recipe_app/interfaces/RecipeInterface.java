package com.example.recipe_app.interfaces;

import com.example.recipe_app.entity.Recipe;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeInterface {
    Recipe save(Recipe recipe);
    Recipe update(Recipe recipe);
    String deleteById(Long recipeId);
    Optional<Recipe> findById(Long recipeId);
    List<Recipe> findAll();
    List<Recipe> search(Boolean isVegetarian, Integer servings, String includeIngredient, String excludeIngredient, String instructionSearch);
}
