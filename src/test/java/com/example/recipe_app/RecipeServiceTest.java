package com.example.recipe_app;

import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.exceptions.RecipeNotFoundException;
import com.example.recipe_app.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RecipeServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @InjectMocks
    private RecipeService recipeService;

    @Test
    public void testSave() {
        Recipe recipe = new Recipe();
        recipe.setRecipeName("Cheesy Pasta");
        recipe.setVegetarian(true);
        recipe.setServings(2);
        recipe.setIngredients("Pasta, Butter, Garlic, Cream, Parmesan Cheese, Salt, Pepper");
        recipe.setInstructions("Boil pasta. Saute garlic in butter. Add cream and parmesan. Mix with pasta and serve.");

        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);
        Recipe savedRecipe = recipeService.save(recipe);

        assertNotNull(savedRecipe);
        assertEquals("Cheesy Pasta", savedRecipe.getRecipeName());
    }
    @Test
    public void testSaveFailure() {
        Recipe recipe = new Recipe();
        recipe.setRecipeName("Cheesy Pasta");

        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(0);
        boolean saveResult = recipeService.save(recipe) == null;

        assertTrue(saveResult);
    }

    @Test
    public void testUpdate() {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(1L);
        recipe.setRecipeName("Pasta");
        recipe.setVegetarian(true);
        recipe.setServings(3);
        recipe.setIngredients("Whole wheat pasta, Vegetables");

        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);
        Recipe updatedRecipe = recipeService.update(recipe);

        assertNotNull(updatedRecipe);
        assertTrue(updatedRecipe.isVegetarian());
        assertEquals(3, updatedRecipe.getServings());
        assertTrue(updatedRecipe.getIngredients().contains("Whole wheat pasta"));
    }

    @Test
    public void testDelete() {
        Long recipeId = 1L;

        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);
        String result = recipeService.deleteById(recipeId);

        assertEquals("Recipe deleted successfully", result);
        verify(jdbcTemplate).update(anyString(), eq(recipeId));
    }

    @Test
    public void testDeleteIdNotFound() {
        Long recipeId = 9L;

        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(0);

        assertThrows(RecipeNotFoundException.class, () -> {
            recipeService.deleteById(recipeId);
        });
    }

    @Test
    public void testFindById() {
        Long recipeId = 1L;
        Recipe recipe = new Recipe();
        recipe.setRecipeId(recipeId);
        recipe.setRecipeName("Fried Rice");
        recipe.setServings(4);

        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RecipeService.RecipeMapper.class))).thenReturn(Collections.singletonList(recipe));
        Optional<Recipe> foundRecipe = recipeService.findById(recipeId);

        assertTrue(foundRecipe.isPresent());
        assertEquals(recipeId, foundRecipe.get().getRecipeId());
        assertEquals("Fried Rice", foundRecipe.get().getRecipeName());
    }

    @Test
    public void testFindByIdFailure() {
        Long nonExistentId = 9L;

        when(jdbcTemplate.query(anyString(), any(RecipeService.RecipeMapper.class), any(Object[].class))).
                thenReturn(Collections.emptyList());
        Optional<Recipe> foundRecipe = recipeService.findById(nonExistentId);

        assertFalse(foundRecipe.isEmpty());
    }

    @Test
    public void testFindAll() {
        List<Recipe> mockRecipes = new ArrayList<>();
        Recipe recipe1 = new Recipe(1L, "Cheesy Pasta", true, 2, "Pasta, Butter, Garlic, Cream, Parmesan Cheese, Salt, Pepper", "Boil pasta. Saute garlic in butter. Add cream and parmesan. Mix with pasta and serve.");
        Recipe recipe2 = new Recipe(2L, "Vegetable Fried Rice", true, 2, "Cooked Rice, Carrot, Beans, Capsicum, Soy Sauce, Oil, Salt", "Saute chopped vegetables in oil. Add cooked rice and soy sauce. Stir-fry on high heat. Season and serve hot.");
        mockRecipes = Stream.of(recipe1, recipe2).collect(Collectors.toList());

        when(jdbcTemplate.query(eq("select * from recipes"), eq(new Object[]{}), any(RecipeService.RecipeMapper.class)
        )).thenReturn(mockRecipes);

        List<Recipe> recipes = recipeService.findAll();

        assertNotNull(recipes);
        assertEquals(mockRecipes.get(0).getRecipeId(), recipes.get(0).getRecipeId());
        assertTrue(recipes.get(0).isVegetarian());
    }

    @Test
    void testSearchNoFilters() {
        List<Recipe> results = recipeService.search(null, null, null, null, null);
        assertNotNull(results);
    }

    @Test
    void testSearch() {
        List<Recipe> recipes = recipeService.search(true, 4, "chicken", "nuts", "grill");

        assertNotNull(recipes);
        assertTrue(recipes.stream().allMatch(r ->
                r.isVegetarian() &&
                        r.getServings() == 4 &&
                        r.getIngredients().toLowerCase().contains("chicken") &&
                        !r.getIngredients().toLowerCase().contains("nuts") &&
                        r.getInstructions().toLowerCase().contains("grill")
        ));
    }

    @Test
    void testSearchVegetarianFilter() {
        List<Recipe> vegetarianRecipes = recipeService.search(true, null, null, null, null);

        assertNotNull(vegetarianRecipes);
        assertTrue(vegetarianRecipes.stream().allMatch(Recipe::isVegetarian));
    }

}
