package com.example.Recipes.DB;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RecipesDbApplicationTests {
	private final ApplicationContext applicationContext;

	@Autowired
	RecipesDbApplicationTests(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Test
	void whenStarting_contextLoads() {
		assertThat(applicationContext).isNotNull();
	}
}
