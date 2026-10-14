package vladimir.java_dz_15;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    private Long id;
    private String title;
    private String category;
    private String cuisine;
    private String description;
    private String videoUrl;
    private List<String> ingredients;
    private int views;
}
