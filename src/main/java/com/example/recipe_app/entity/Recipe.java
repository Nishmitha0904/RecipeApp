package com.example.recipe_app.entity;

public class Recipe {
    private Long recipeId;
    private String recipeName;
    private boolean isVegetarian;
    private int servings;
    private String ingredients;
    private String instructions;

    public Recipe() {
    }

    public Recipe(Long recipeId, String recipeName, boolean isVegetarian, int servings, String ingredients, String instructions) {
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.isVegetarian = isVegetarian;
        this.servings = servings;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        isVegetarian = vegetarian;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
