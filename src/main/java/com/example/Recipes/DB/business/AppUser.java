package com.example.Recipes.DB.business;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AppUser {
    @Id
    @NotNull
    @Email(regexp = ".+@.+\\..+", message = "A valid email address must be entered.")
    private String email;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval=true, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Schema(hidden = true)
    private Set<Recipe> recipes;
}
