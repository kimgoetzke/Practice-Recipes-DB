# The Recipes Project

This repo contains a simple multi-users web service, written in Java and using the Spring framework. 

This app was initially created as a [graduate project](https://hyperskill.org/projects/180) for the '[Spring Security for Java Backend Developer](https://hyperskill.org/tracks/38)' track on Hyperskill (JetBrains Academy). However, after finishing the project, I decided to:
+ Migrate from using an H2 database to Postgres
+ Containerise the app/db with Docker Compose
+ Upgrade to the latest Spring Framework 6 (which required:)
+ Refactoring/upgrading the security configuration to use SecurityFilterChain

The project is a simple multi-users web service for storing recipes. Some key features include:
+ An API with a Spring Boot REST controller offering endpoints for creating users and adding/requesting/updating/deleting recipes
+ Sprint Crypto to implement BCrypt encoding for passwords
+ Sprint Security to allow only authorised users access (except for user registration)
+ Processing and returning data in JSON format
+ Using Project Lombok to reduce boilerplate code ()
+ Docker Compose to separate app from database
+ PostgreSQL database


## Configuration & available endpoints

+ Run `docker compose up` to create and run images and containers
+ Use local port `8881` for API requests

### Register user
POST `api/register`
+ Expects JSON with `email` (valid email) and `password` (8 character minimum)
+ Example:
  `{
  "email": "test@email.com",
  "password": "password"
  }`
+ Returns HTTP status code (`200` or `400`) based on success

### Create recipe
POST `api/recipe/new`
+ Expects JSON with `name`, `category`, `description`, `ingredients` (1+), `directions` (1+) and authorisation
+ Example:
  ```
  {
     "name": "Fresh Mint Tea",
     "category": "Beverage",
     "description": "Light, aromatic and refreshing beverage, ...",
     "ingredients": ["boiled water", "honey", "fresh mint leaves"],
     "directions": ["Boil water", "Pour boiling hot water into a mug", "Add fresh mint leaves", "Mix and let the mint leaves seep for 3-5 minutes", "Add honey and mix again"]
  }
  ```
+ If successful, returns JSON with the generated `id` and HTTP status `200`, or `400`/`401` if invalid/unauthorised

### Update recipe
PUT `api/recipe/{id}`
+ Expects JSON with `name`, `category`, `description`, `ingredients` (1+), `directions` (1+) and authorisation
+ Example:
  ```
  {
     "name": "Even Fresher Mint Tea",
     "category": "Beverage",
     "description": "Light, aromatic and refreshing beverage, ...",
     "ingredients": ["boiled water", "honey", "very fresh mint leaves"],
     "directions": ["Boil water", "Pour boiling hot water into a mug", "Add very fresh mint leaves", "Mix and let the mint leaves seep for 3-5 minutes", "Add honey and mix again"]
  }
  ```
+ Returns HTTP status `204` if successful - or `403`/`404` if not the owner/recipe not found

### Get recipe
GET `api/recipe/{id}`
+ Expects authorisation, no body required
+ Returns JSON with `name`, `category`, `description`, `ingredients`, `directions`  and HTTP status `200` if successful, or `401`/`404` if unauthorised/not found

### Search recipes
GET `api/recipes/search`
+ Expects authorisation, no body required
+ Accepts either `?name=` (string containing) or `?category=`
+ Returns sorted JSON with all recipes matching the search criteria and HTTP status `200` if successful, or `401` if unauthorised

### Delete recipe
DELETE `api/recipe/{id}`
+ Expects authorisation, no body required
+ Returns HTTP status `204` if successful, or `403`/`404` if not the owner/recipe not found
