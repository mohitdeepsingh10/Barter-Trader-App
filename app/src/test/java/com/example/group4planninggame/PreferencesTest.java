package com.example.group4planninggame;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.example.group4planninggame.models.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PreferencesTest {

    private Preferences preferences;

    @Before
    public void setUp() {
        preferences = new Preferences(null);
    }

    @Test
    public void testSetCategories() {
        // Test setting categories
        List<String> categories = new ArrayList<>();
        categories.add("Clothing");
        categories.add("Books");
        preferences.setCategories(categories);

        assertEquals(categories, preferences.getCategories());

        // Test overwriting categories
        List<String> categories2 = new ArrayList<>();
        categories2.add("Furniture");
        categories2.add("Electronics");
        preferences.setCategories(categories2);

        assertEquals(categories2, preferences.getCategories());
    }

    @Test
    public void testAddCategoryNewCategory() {
        // Test adding a new category
        List<String> categories = new ArrayList<>();
        categories.add("Clothing");
        categories.add("Books");
        preferences.setCategories(categories);

        preferences.addCategory("Furniture");
        assertTrue(preferences.getCategories().contains("Furniture"));
        assertEquals(3, preferences.getCategories().size());
    }

    @Test
    public void testAddCategoryDuplicate() {
        // Test adding a duplicate category
        List<String> categories = new ArrayList<>();
        categories.add("Electronics");
        categories.add("Books");
        categories.add("Clothing");
        preferences.setCategories(categories);

        preferences.addCategory("Books");
        assertEquals(3, preferences.getCategories().size());
    }

    @Test
    public void testSetDistance() {
        // Assuming a setter for distance (you might add it later)
        preferences.setDistance(10.0);
        assertEquals(10.0, preferences.getDistance(),0.0);
    }
}