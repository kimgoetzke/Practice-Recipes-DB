package com.example.Recipes.DB.business;

import com.example.Recipes.DB.persistence.UserRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Tag("unit")
class AppUserServiceTest {
    @MockBean
    UserRepository userRepository;

    @Autowired
    AppUserService appUserService;

    AppUser appUser = new AppUser("test@gmail.com", "password1", null);

    @Test
    void givenNewUser_whenAdd_thenSucceed() {
        when(userRepository.existsById(appUser.getEmail())).thenReturn(false);
        when(userRepository.save(appUser)).thenReturn(appUser);
        assertTrue(appUserService.add(appUser));
    }

    @Test
    void givenUserExists_whenAdd_thenFail() {
        when(userRepository.existsById(appUser.getEmail())).thenReturn(true);
        assertFalse(appUserService.add(appUser));
        verify(userRepository, never()).save(any());
    }

    @Test
    void givenUserExists_whenGet_thenSucceed() {
        when(userRepository.findById(appUser.getEmail())).thenReturn(Optional.ofNullable(appUser));
        assertEquals(appUser, appUserService.get(appUser.getEmail()));
    }

    @Test
    void givenUserNotFound_whenGet_thenThrowException() {
        when(userRepository.findById(appUser.getEmail())).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> appUserService.get(appUser.getEmail()));
    }

    @Test
    void whenLoadUser_thenThrowException() {
        when(userRepository.findById(appUser.getEmail())).thenThrow(UsernameNotFoundException.class);
        assertThrows(UsernameNotFoundException.class, () -> appUserService.loadUserByUsername(appUser.getEmail()));
    }

    @Test
    void whenLoadUser_thenSucceed() {
        User userDetails = new org.springframework.security.core.userdetails.User(appUser.getEmail(), appUser.getPassword(), List.of());
        when(userRepository.findById(appUser.getEmail())).thenReturn(Optional.ofNullable(appUser));
        assertEquals(userDetails, appUserService.loadUserByUsername(appUser.getEmail()));
    }
}