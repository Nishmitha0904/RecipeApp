package com.example.recipe_app;

import com.example.recipe_app.controller.RecipeController;
import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class EndpointTesting {
    @MockitoBean
    private RecipeService recipeService;
    @InjectMocks
    private RecipeController recipeController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateRecipe() throws Exception {
        Recipe recipe = new Recipe(1L, "Cheesy Pasta", true, 2, "Pasta, Butter, Garlic, Cream, Parmesan Cheese, Salt, Pepper", "Boil pasta. Saute garlic in butter. Add cream and parmesan. Mix with pasta and serve.");

        String recipeJson = "{" +
                "\"id\": 1," +
                "\"name\": \"Cheesy Pasta\"," +
                "\"vegetarian\": true," +
                "\"servings\": 2," +
                "\"ingredients\": \"Pasta, Butter, Garlic, Cream, Parmesan Cheese, Salt, Pepper\"," +
                "\"instructions\": \"Boil pasta. Saute garlic in butter. Add cream and parmesan. Mix with pasta and serve.\"" +
                "}";

        when(recipeService.save(any(Recipe.class))).thenReturn(recipe);

        // Perform POST request and validate response
        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recipeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(1L))
                .andExpect(jsonPath("$.recipeName").value("Cheesy Pasta"))
                .andExpect(jsonPath("$.vegetarian").value(true));
    }

    @Test
    void testUpdateRecipe() throws Exception {
        Recipe recipe = new Recipe(1L, "Pasta", true, 3,
                "Pasta, Vegetables, Sauce",
                "Boil pasta, mix with vegetables and sauce");

        String recipeJson = "{" +
                "\"id\": 1," +
                "\"name\": \"Pasta\"," +
                "\"vegetarian\": true," +
                "\"servings\": 3," +
                "\"ingredients\": \"Pasta, Vegetables, Sauce\"," +
                "\"instructions\": \"Boil pasta, mix with vegetables and sauce\"" +
                "}";

        when(recipeService.update(any(Recipe.class))).thenReturn(recipe);

        mockMvc.perform(put("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recipeJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(1L))
                .andExpect(jsonPath("$.recipeName").value("Pasta"))
                .andExpect(jsonPath("$.vegetarian").value(true))
                .andExpect(jsonPath("$.servings").value(3));
    }

    @Test
    void testDeleteRecipe() throws Exception {
        when(recipeService.deleteById(1L)).thenReturn("Recipe deleted successfully");
        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Recipe deleted successfully"));
    }

    @Test
    void testGetRecipeById() throws Exception {
        Recipe recipe = new Recipe(1L, "Cheesy Pasta", true, 2, "Pasta, Butter, Garlic, Cream, Parmesan Cheese, Salt, Pepper", "Boil pasta. Saute garlic in butter. Add cream and parmesan. Mix with pasta and serve.");

        when(recipeService.findById(1L)).thenReturn(Optional.of(recipe));

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipeId").value(1L))
                .andExpect(jsonPath("$.recipeName").value("Cheesy Pasta"))
                .andExpect(jsonPath("$.vegetarian").value(true));
    }

    @Test
    void testGetAllRecipes() throws Exception {
        Recipe recipe1 = new Recipe(1L, "Cheesy Pasta", true, 2, "Pasta, Butter, Garlic, Cream, Parmesan Cheese, Salt, Pepper", "Boil pasta. Saute garlic in butter. Add cream and parmesan. Mix with pasta and serve.");
        Recipe recipe2 = new Recipe(2L, "Vegetable Fried Rice", true, 2, "Cooked Rice, Carrot, Beans, Capsicum, Soy Sauce, Oil, Salt", "Saute chopped vegetables in oil. Add cooked rice and soy sauce. Stir-fry on high heat. Season and serve hot.");

        List<Recipe> recipes = Arrays.asList(recipe1, recipe2);
        when(recipeService.findAll()).thenReturn(recipes);

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipeId").value(1L))
                .andExpect(jsonPath("$[0].recipeName").value("Cheesy Pasta"))
                .andExpect(jsonPath("$[1].recipeId").value(2L))
                .andExpect(jsonPath("$[1].recipeName").value("Vegetable Fried Rice"));
    }

    @Test
    void testSearch() throws Exception {
        Recipe recipe = new Recipe(1L, "Cheesy Pasta", true, 2,
                "Pasta, Butter, Garlic, Cream, Parmesan Cheese, Salt, Pepper",
                "Boil pasta. Saute garlic in butter. Add cream and parmesan. Mix with pasta and serve.");

        List<Recipe> combinedRecipes = Arrays.asList(recipe);

        when(recipeService.search(
                true,
                2,
                "Pasta",
                null,
                null
        )).thenReturn(combinedRecipes);

        mockMvc.perform(get("/api/recipes/search")
                        .param("isVegetarian", "true")
                        .param("servings", "2")
                        .param("includeIngredient", "Pasta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipeId").value(1L))
                .andExpect(jsonPath("$[0].recipeName").value("Cheesy Pasta"));
    }

}
