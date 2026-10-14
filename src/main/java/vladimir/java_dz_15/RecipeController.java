package vladimir.java_dz_15;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final List<Recipe> recipes = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public RecipeController() {
        recipes.add(new Recipe(idGenerator.getAndIncrement(), "Pizza Margherita", "Breakfast", "Italian", "Classic pizza with tomatoes and mozzarella", "https://video.com", List.of("tomato", "cheese", "flour"), 150));
        recipes.add(new Recipe(idGenerator.getAndIncrement(), "Pasta Carbonara", "Lunch", "Italian", "Pasta with guanciale, egg yolks and pecorino", "https://video.com", List.of("pasta", "bacon", "cheese", "egg"), 200));
        recipes.add(new Recipe(idGenerator.getAndIncrement(), "Beef Tacos", "Dinner", "Mexican", "Spicy corn tortillas with minced beef", "https://video.com", List.of("meat", "tortilla", "pepper"), 90));
        recipes.add(new Recipe(idGenerator.getAndIncrement(), "Borscht", "Lunch", "Russian", "Traditional beet soup", "https://video.com", List.of("meat", "beet", "cabbage"), 300));
        recipes.add(new Recipe(idGenerator.getAndIncrement(), "Omlette", "Breakfast", "French", "Fluffy egg omelette with milk", "https://video.com", List.of("egg", "milk"), 50));
        recipes.add(new Recipe(idGenerator.getAndIncrement(), "Pancakes", "Breakfast", "Russian", "Thin traditional pancakes", "https://video.com", List.of("flour", "milk", "egg"), 180));
    }

    @GetMapping("/{id}/short")
    public Map<String, String> getShortInfo(@PathVariable Long id) {
        return recipes.stream().filter(r -> r.getId().equals(id)).findFirst()
                .map(r -> Map.of("title", r.getTitle(), "category", r.getCategory()))
                .orElse(Map.of("error", "Recipe not found"));
    }

    @GetMapping("/{id}/full")
    public Map<String, String> getFullInfo(@PathVariable Long id) {
        return recipes.stream().filter(r -> r.getId().equals(id)).findFirst()
                .map(r -> Map.of("title", r.getTitle(), "description", r.getDescription(), "video", r.getVideoUrl()))
                .orElse(Map.of("error", "Recipe not found"));
    }

    @GetMapping("/by-ingredients")
    public List<Recipe> getByIngredients(@RequestParam List<String> items) {
        return recipes.stream()
                .filter(r -> r.getIngredients().containsAll(items.stream().map(String::toLowerCase).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @GetMapping("/poems")
    public List<String> getPoems(@RequestParam String poet) {
        if ("Pushkin".equalsIgnoreCase(poet)) {
            return List.of(
                    "I remember a wonderful moment...",
                    "Frost and sun; a wonderful day!",
                    "An oak tree greening by the ocean..."
            );
        }
        return List.of("Poems by " + poet + " are not available in the cookbook.");
    }

    @GetMapping("/random")
    public Recipe getRandom() {
        return recipes.get(new Random().nextInt(recipes.size()));
    }

    @GetMapping("/categories")
    public Set<String> getCategories() {
        return recipes.stream().map(Recipe::getCategory).collect(Collectors.toSet());
    }

    @GetMapping("/top-by-category")
    public List<Recipe> getTopByCategory(@RequestParam String category) {
        return recipes.stream()
                .filter(r -> r.getCategory().equalsIgnoreCase(category))
                .sorted((r1, r2) -> Integer.compare(r2.getViews(), r1.getViews()))
                .limit(3).collect(Collectors.toList());
    }

    @GetMapping("/random-dinner")
    public List<Recipe> getRandomDinner() {
        Collections.shuffle(recipes);
        return recipes.stream().limit(3).collect(Collectors.toList());
    }

    @GetMapping("/random-breakfast")
    public Recipe getRandomBreakfast() {
        List<Recipe> breakfasts = recipes.stream().filter(r -> "Breakfast".equalsIgnoreCase(r.getCategory())).collect(Collectors.toList());
        return breakfasts.get(new Random().nextInt(breakfasts.size()));
    }

    @GetMapping("/exclude-ingredients")
    public List<Recipe> excludeIngredients(@RequestParam List<String> exclude) {
        return recipes.stream()
                .filter(r -> r.getIngredients().stream().noneMatch(i -> exclude.contains(i.toLowerCase())))
                .limit(10).collect(Collectors.toList());
    }

    @GetMapping("/top-by-cuisine")
    public List<Recipe> getTopByCuisine(@RequestParam String cuisine) {
        return recipes.stream()
                .filter(r -> r.getCuisine().equalsIgnoreCase(cuisine))
                .sorted((r1, r2) -> Integer.compare(r2.getViews(), r1.getViews()))
                .limit(3).collect(Collectors.toList());
    }

    @GetMapping("/top-cuisines")
    public List<String> getTopCuisines() {
        Map<String, Integer> cuisineViews = new HashMap<>();
        recipes.forEach(r -> cuisineViews.put(r.getCuisine(), cuisineViews.getOrDefault(r.getCuisine(), 0) + r.getViews()));
        return cuisineViews.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @GetMapping("/top-categories-by-count")
    public List<String> getTopCategoriesByCount() {
        Map<String, Long> countMap = recipes.stream().collect(Collectors.groupingBy(Recipe::getCategory, Collectors.counting()));
        return countMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        recipe.setId(idGenerator.getAndIncrement());
        recipes.add(recipe);
        return recipe;
    }

    @PutMapping("/{id}")
    public Recipe updateRecipe(@PathVariable Long id, @RequestBody Recipe updated) {
        for (Recipe r : recipes) {
            if (r.getId().equals(id)) {
                r.setTitle(updated.getTitle());
                r.setCategory(updated.getCategory());
                r.setCuisine(updated.getCuisine());
                r.setDescription(updated.getDescription());
                r.setVideoUrl(updated.getVideoUrl());
                r.setIngredients(updated.getIngredients());
                return r;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public String deleteRecipe(@PathVariable Long id) {
        boolean removed = recipes.removeIf(r -> r.getId().equals(id));
        return removed ? "Recipe successfully deleted." : "Recipe not found.";
    }
}
