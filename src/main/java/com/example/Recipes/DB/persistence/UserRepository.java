package com.example.Recipes.DB.persistence;

import com.example.Recipes.DB.business.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<AppUser, String> { }