package com.example.recipe_app.service;

import com.example.recipe_app.entity.Recipe;
import com.example.recipe_app.exceptions.RecipeNotFoundException;
import com.example.recipe_app.interfaces.RecipeInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import oracle.jdbc.OracleTypes;

@Service
public class RecipeService implements RecipeInterface {

    @Autowired
    private  JdbcTemplate jdbcTemplate;

    @Override
    public Recipe save(Recipe recipe) {
        int ack = jdbcTemplate.update("insert into recipes(recipe_id, recipe_name, is_vegetarian, servings, ingredients, instructions) values(recipe_seq.nextval, ?, ?, ?, ?, ?)",
                new Object[]{
                        recipe.getRecipeName(),
                        recipe.isVegetarian() ? 1: 0,
                        recipe.getServings(),
                        recipe.getIngredients(),
                        recipe.getInstructions()
                });
        return recipe;
    }

    @Override
    public Recipe update(Recipe recipe) {
        int ack = jdbcTemplate.update("update recipes set recipe_name=?, is_vegetarian=?, servings=?, ingredients=?, instructions=? where recipe_id=?",
                new Object[]{
                        recipe.getRecipeName(),
                        recipe.isVegetarian() ? 1: 0,
                        recipe.getServings(),
                        recipe.getIngredients(),
                        recipe.getInstructions(),
                        recipe.getRecipeId()
                });
        if (ack != 0)
            return recipe;
        else
            throw new RecipeNotFoundException(recipe.getRecipeId());
    }

    @Override
    public String deleteById(Long recipeId) {
        int ack = jdbcTemplate.update("delete from recipes where recipe_id=?",
                new Object[]{recipeId});
        if (ack != 0)
            return "Recipe deleted successfully";
        else
//            return "Recipe not found with id: "+recipeId;
            throw new RecipeNotFoundException(recipeId);
    }

    @Override
    public Optional<Recipe> findById(Long recipeId) {
        List<Recipe> recipes = jdbcTemplate.query("select * from recipes where recipe_id=?",
                new Object[]{recipeId},
                new RecipeMapper());
        if (recipes.isEmpty())
            throw new RecipeNotFoundException(recipeId);
        return Optional.of(recipes.get(0));
    }

    @Override
    public List<Recipe> findAll() {
        return jdbcTemplate.query("select * from recipes",
                new Object[]{},
                new RecipeMapper());
    }

    @Override
    public List<Recipe> search(Boolean isVegetarian, Integer servings, String includeIngredient, String excludeIngredient, String instructionSearch) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM recipes WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (isVegetarian != null) {
            sqlBuilder.append(" AND is_vegetarian = ?");
            params.add(isVegetarian ? 1 : 0);
        }
        if (servings != null) {
            sqlBuilder.append(" AND servings = ?");
            params.add(servings);
        }
        if (includeIngredient != null && !includeIngredient.isEmpty()) {
            sqlBuilder.append(" AND LOWER(ingredients) LIKE ?");
            params.add("%" + includeIngredient.toLowerCase() + "%");
        }
        if (excludeIngredient != null && !excludeIngredient.isEmpty()) {
            sqlBuilder.append(" AND LOWER(ingredients) NOT LIKE ?");
            params.add("%" + excludeIngredient.toLowerCase() + "%");
        }
        if (instructionSearch != null && !instructionSearch.isEmpty()) {
            sqlBuilder.append(" AND LOWER(instructions) LIKE ?");
            params.add("%" + instructionSearch.toLowerCase() + "%");
        }
        return jdbcTemplate.query(
                sqlBuilder.toString(),
                new RecipeMapper(),
                params.toArray()
        );
    }

    public static class RecipeMapper implements RowMapper<Recipe> {

        @Override
        public Recipe mapRow(ResultSet rs, int rowNum) throws SQLException {
            Recipe recipe = new Recipe();
            recipe.setRecipeId(rs.getLong("recipe_id"));
            recipe.setRecipeName(rs.getString("recipe_name"));
            recipe.setVegetarian(rs.getBoolean("is_vegetarian"));
            recipe.setServings(rs.getInt("servings"));
            recipe.setIngredients(rs.getString("ingredients"));
            recipe.setInstructions(rs.getString("instructions"));
            return recipe;
        }
    }
}
