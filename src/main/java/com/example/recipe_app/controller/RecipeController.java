package com.example.recipe_app.controller;

import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        return ResponseEntity.ok(recipeService.save(recipe));
    }

    @PutMapping
    public ResponseEntity<Recipe> updateRecipe(@RequestBody Recipe recipe) {
        return ResponseEntity.ok(recipeService.update(recipe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable("id") Long recipeId) {
        return ResponseEntity.ok(recipeService.deleteById(recipeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable("id") Long recipeId) {
        return recipeService.findById(recipeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam(required = false) Boolean isVegetarian,
            @RequestParam(required = false) Integer servings,
            @RequestParam(required = false) String includeIngredient,
            @RequestParam(required = false) String excludeIngredient,
            @RequestParam(required = false) String instructionSearch) {
        return ResponseEntity.ok(recipeService.search(isVegetarian, servings,
                includeIngredient, excludeIngredient, instructionSearch));
    }
}
