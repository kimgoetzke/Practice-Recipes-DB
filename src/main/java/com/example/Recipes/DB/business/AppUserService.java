package com.example.Recipes.DB.business;

import com.example.Recipes.DB.persistence.UserRepository;
import com.example.Recipes.DB.presentation.RecipeController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Logger LOGGER = Logger.getLogger(RecipeController.class.getName());

    public boolean add(AppUser appUser) {
        if (userRepository.existsById(appUser.getEmail())) {
            LOGGER.warning("User not created, " + userRepository.findById(appUser.getEmail()) + " already exists.");
            return false;
        }
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        userRepository.save(appUser);
        LOGGER.info("Added: " + userRepository.findById(appUser.getEmail()) + " - total # users: " + userRepository.count() + ".");
        return true;
    }

    public AppUser get(String email) {
        return userRepository.findById(email).orElseThrow(() -> new RuntimeException("Couldn't find user '" + email + "'."));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findById(email).orElseThrow(() -> new UsernameNotFoundException(email));
        return new org.springframework.security.core.userdetails.User(appUser.getEmail(), appUser.getPassword(), List.of());
    }
}
