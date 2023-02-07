package com.example.Recipes.DB.business;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private long id;

    @NotBlank(message = "Name is a mandatory field")
    private String name;

    @NotBlank(message = "Category is a mandatory field")
    private String category;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime date;

    @NotBlank(message = "Description is a mandatory field")
    private String description;

    @NotEmpty
    @ElementCollection
    private List<@NotEmpty String> ingredients;

    @NotEmpty
    @ElementCollection
    private List<@NotEmpty String> directions;

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "added_by")
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Schema(hidden = true)
    private AppUser appUser;
}
