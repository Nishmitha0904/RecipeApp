package com.example.recipe_app.controller;

import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.exceptions.RecipeNotFoundException;
import com.example.recipe_app.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipe App", description = "API for managing favorite recipes")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @PostMapping
    @Operation(summary = "Create a new recipe", description = "Adds a new recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid recipe data")
    })
    public ResponseEntity<Recipe> createRecipe(@Valid @Parameter(description = "Recipe details to be added", required = true) @RequestBody Recipe recipe) {
        return ResponseEntity.ok(recipeService.save(recipe));
    }

    @PutMapping
    @Operation(summary = "Update an existing recipe", description = "Updates a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe updated successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<Recipe> updateRecipe(@Parameter(description = "Recipe details to be updated", required = true) @RequestBody Recipe recipe) {
        return ResponseEntity.ok(recipeService.update(recipe));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recipe", description = "Removes a recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<String> deleteRecipe(@Parameter(description = "ID of the recipe to be deleted", required = true) @PathVariable("id") Long recipeId) {
        return ResponseEntity.ok(recipeService.deleteById(recipeId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by ID", description = "Fetches a specific recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe found"),
            @ApiResponse(responseCode = "404", description = "Recipe not found")
    })
    public ResponseEntity<Recipe> getRecipeById(@Parameter(description = "ID of the recipe to fetch", required = true) @PathVariable("id") Long recipeId) {
        return recipeService.findById(recipeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all recipes", description = "Fetches all the recipes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipes fetched successfully")
    })
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    @GetMapping("/search")
    @Operation(summary = "Search recipes", description = "Search recipes based on the filter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<List<Recipe>> searchRecipes(
            @Parameter(description = "Filter by vegetarian status")
            @RequestParam(required = false) Boolean isVegetarian,
            @Parameter(description = "Filter by number of servings")
            @RequestParam(required = false) Integer servings,
            @Parameter(description = "Filter by ingredient included")
            @RequestParam(required = false) String includeIngredient,
            @Parameter(description = "Filter by ingredient excluded")
            @RequestParam(required = false) String excludeIngredient,
            @Parameter(description = "Search text within recipe instructions")
            @RequestParam(required = false) String instructionSearch) {
        return ResponseEntity.ok(recipeService.search(isVegetarian, servings,
                includeIngredient, excludeIngredient, instructionSearch));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        return ResponseEntity
                .badRequest()
                .body(
                        ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(fieldError -> fieldError.getDefaultMessage())
                                .collect(Collectors.toList())
                );
    }
    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<String> handleRecipeNotFoundException(RecipeNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
