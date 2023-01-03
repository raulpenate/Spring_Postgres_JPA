## 🚀 What's Spring Boot?
Spring Boot is a framework that helps you build stand-alone, production-grade Java apps 💪. It's built on top of the popular Spring Framework and comes with a bunch of useful tools and libraries to simplify development.

Some cool features include:

 - Embedded web server (Tomcat, Jetty, Undertow) to run your app without deploying to a separate server
 - Externalized configuration for easy changes without modifying code
 - Automatic configuration of Spring beans based on classpath and properties
 - "Starter" dependencies to enable specific features (e.g. web development, JPA support)
 - Spring Boot is often used for building microservices, web apps, and other Java-based projects. It's easy to use and has a low learning curve, making it a great choice for developers of all skill levels 🙌.

---

## 👣 Steps

In this tutorial( based in [this video](https://www.youtube.com/watch?v=vTu2HQrXtyw)) I'm gonna create a simple SpringBoot project using `Java`, `JDK 17 Corretto` and `Gradle-Groovy`, but it's your choice to use the Language, JDK or dependency tool that you prefer.

1. Start the project, add JPA and the SQL driver of your choice, in this tutorial I'm gonna use postgres.
2. Create the  ```Controller-Service-Repository``` pattern structure in your ```com.example.projectpackage```, that is inside ```src/main/java```, just add the following packages: **Models, Controllers, Repositories, Services**.  
   ![img](https://miro.medium.com/max/720/1*neBcAZJyLGpE7KHc3sH8bw.webp)
3. In ```applications.propreties``` add the connection to you DB, e.g:
    ```spring
        # IP + Port + DB name
        spring.datasource.url=jdbc:postgresql://localhost:5432/db_demo
        # Username
        spring.datasource.username=postgres
        # Password
        spring.datasource.password=password
        # If it's code first use -> update
        # IF it's database first use -> none
        spring.jpa.hibernate.ddl-auto=update
    ```
    * In **Database first**, db and tables are created first. Then you create entity Data Model using the created database.
    * In **Code first**, we create first the entity classes & properties, and the framework will create the db & tables based on the entities defined. We are gonna use this in this "tutorial".
4. Create the model of the class `User` inside your `Models` package:
   ```java
   import jakarta.persistence.*;
   
   @Entity // Mark that this is a entity of the DB
   @Table(name = "users") // Specify the table name in the DB
   public class UserModel { 
      @Id // Mark this attribute as the PK
      @GeneratedValue(strategy = GenerationType.IDENTITY) // Declared as autoincrement / SERIAL
      @Column(nullable = false, unique = true) // NOT NULL & UNIQUE
      private long id;
      private String name;
      private String email;
      private Integer priority;
      
      /* Here are all the setters & getters*/
    }
   ```
   And create all the `setters` & `getters`.
   ##### NOTE: DON'T NAME the table as `user`, because is a [reserved key-word](https://www.postgresql.org/docs/current/sql-keywords-appendix.html#:~:text=USER-,reserved,-reserved) in postgres, click [here](https://www.postgresql.org/docs/current/sql-keywords-appendix.html) for further info.  
5. Run the project with `./gradlew bootRun`, `gradle bootRun` or using the IDE and check if the table was created in the DB.
6. Create `UserReporitory` inside the `Repositories` package.
```java
import com.example.springprojectjpapostgres.Models.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository // Mark that is a repository
// CrudRepository is an interface in the Spring Data project that provides 
// CRUD (create, read, update, delete) operations for a specific type of entity. 
// It is a part of the Spring Data JPA module and extends the Repository interface.
public interface UserRepository extends CrudRepository<UserModel, Long> {
    // In this example, the UserRepository interface extends CrudRepository and 
   // specifies that it will be used to manage UserModel entities with a Long id type.

   // The findById method returns a UserModel with the specified id.
    public abstract UserModel findById(Integer id);
   // The findByPriority method returns an ArrayList of UserModel objects with the specified priority.
   public abstract ArrayList<UserModel> findByPriority(Integer priority);
}
```
7. Create `UserService` inside the `Services` package.
```java
// @Service annotation indicates that this is a service class
@Service
public class UserService {
   // @Autowired annotation injects the UserRepository bean
   @Autowired
   UserRepository userRepository;

   // This method returns a list of all users in the database
   public ArrayList<UserModel> getUsers(){
      // The findAll() method of the UserRepository is called to retrieve all users
      // The result is casted to an ArrayList and returned
      return (ArrayList<UserModel>) userRepository.findAll();
   }

   // This method returns a specific user by id
   public Optional<UserModel> getUserById(Long id){
      // The findById() method of the UserRepository is called with the provided id
      // The result is returned as an Optional
      return userRepository.findById(id);
   }

   // This method returns a list of users with a specific priority
   public ArrayList<UserModel> getUsersByPriority(Integer priority){
      // The findByPriority() method of the UserRepository is called with the provided priority
      // The result is returned as an ArrayList
      return userRepository.findByPriority(priority);
   }

   // This method adds a new user to the database
   public UserModel postUser(UserModel user){
      // The save() method of the UserRepository is called with the provided user
      // The result is returned
      return userRepository.save(user);
   }

   // This method deletes a user by id
   public boolean deleteUser(Long id){
      try{
         // The deleteById() method of the UserRepository is called with the provided id
         userRepository.deleteById(id);
         return true;
      }catch (Exception e){
         // If an exception occurs, it is caught and the method returns false
         return false;
      }
   }
}

```
* A bean in Spring is a Java object that is managed by the Spring container. It is an object that is instantiated, assembled, and otherwise managed by the Spring IoC container. These beans are created from configuration metadata, which can be specified in various ways, such as Java annotations, XML, or Java code.
* he [Inversion of Control (IoC)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-introduction) container is a component in a Spring application that manages the lifecycle and dependencies of beans. It reads configuration metadata, creates and wires beans, and manages their lifecycle events.
8. Create `UserController` inside the `Controllers` package.
```java
@RestController // This annotation indicates that this class will handle HTTP requests
@RequestMapping("/user") // Indicates the base URL for all the methods in this class
public class UserController {
    @Autowired // This annotation injects the UserService bean into this class
    UserService userService;

    @GetMapping // Indicates that this method will handle GET requests
    public ArrayList<UserModel> getUsers(){
        // This method returns a list of all the users in the database by calling the getUsers method in the UserService
        return this.userService.getUsers();
    }

    @GetMapping(path = "/{id}") // This annotation indicates that this method will handle GET requests and expects an id in the URL path
    public Optional<UserModel> getUserById(@PathVariable("id") Long id){
        // This method returns a user with the specified id by calling the getUserById method in the UserService
        return this.userService.getUserById(id);
    }

    @GetMapping("/query") // This annotation indicates that this method will handle GET requests with a query parameter
    public ArrayList<UserModel> getUserByPriority(@RequestParam("priority") Integer priority){
        // This method returns a list of users with the specified priority by calling the getUsersByPriority method in the UserService
        return this.userService.getUsersByPriority(priority);
    }

    @PostMapping // This annotation indicates that this methods  will handle POST requests
    public UserModel postUser(@RequestBody UserModel user){
        // This method saves a new user to the database by calling the postUser method in the UserService
        return this.userService.postUser(user);
    }

    @DeleteMapping(path = "/{id}") // Indicates that this method will handle DELETE requests and expects an id in the URL path
    public String deleteById(@PathVariable("id") Long id){
        // This method deletes a user with the specified id by calling the deleteUser method in the UserService
        boolean ok = this.userService.deleteUser(id);
        if(ok){
            return String.format("User by id: %s was deleted", id);
        }else{
            return String.format("User by id: %s request to delete failed", id);
        }
    }
}
```
9. Now run the project and test it:
Run in the API tool of your preference a `GET` request with the url and path created in your controller.
In this case `http://localhost:8080/user`. As you may see here, we get nothing in return so need to add data.
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleOne.png)
10. Send a `POST` request with`http://localhost:8080/user`. 
```json
{
   "name": "Randal Kolo Muani",
   "email": "randal.kolo@gmail.com",
   "priority": 1
}
```
And after that one send another with:
```json
{
  "name": "Julián Álvarez",
  "email": "julyalva@gmail.com",
  "priority": 2
}
```
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleTwo.png)
11. Now send the `GET` request to `http://localhost:8080/user` and see the magic!!. We get both of the users we added.
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleThree.png)
12. We can consult by `id` seding a  `GET` request to `http://localhost:8080/user/7` for e.g. And we get the user with `id` 7 in this case:
```json
{
  "name": "Julián Álvarez",
  "email": "julyalva@gmail.com",
  "priority": 2
}
```
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleFour.png)
13. And we also can consult by `id` seding a  `GET` request to `http://localhost:8080/user/query?priority=1` for e.g. And we get the user where `priority` == `1` in this case:
```json
{
   "name": "Randal Kolo Muani",
   "email": "randal.kolo@gmail.com",
   "priority": 1
}
```
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleFive.png)
14. To delete the user or our DB with a `DELETE` request to `http://localhost:8080/user/6`. If it was successful we receive the message that `User by id: 6 was deleted`.
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleSix.png)
We can check that in our database `Randa Kolo Muani` no longer exist.
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleSeven.png)
15. And finally the last cool thing we can do with `Spring` is that we don't need to code the `UPDATE` method, we can just send a `POST` with the `id` of the data that we want to update and that's it, for e.g:
```json
{
  "id": 7,
  "name": "Julián Baltasar Mariano José Luis de la Santísima Trinidad Álvarez",
  "email": "julyalva@gmail.com",
  "priority": 1
}
```
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleEight.png)
And we appreciate in our DB that the change was made.
![testingApi](https://raw.githubusercontent.com/raulpenate/Spring_Postgres_JPA/Main/screenshots/exampleNine.png)

And that's it!!, I hope you enjoyed this journey learning Spring with me 🥳🙌.
---

## 🧑‍💼 Spring MVC Components
Spring MVC is a design pattern that separates concerns into different components. Here's a quick breakdown of the main components:

### 🧑‍💼 Controllers
- Controllers handle incoming HTTP requests and return responses. They're annotated with @Controller and contain handler methods annotated with ```@RequestMapping```.

### 🧰 Services
- Services handle business logic and interact with the model. They're annotated with ```@Service``` and contain methods that perform specific tasks.

### 🗄 Repositories
- Repositories interact with the data store and perform CRUD operations. They're typically implemented as interfaces annotated with ```@Repository``` and extend JpaRepository.

### 🧑‍💼 Models
- Models represent the data being used in the application. They're typically implemented as POJOs (plain old Java objects) with fields and ```getter/setter``` methods.

Overall, these components work together to implement the MVC design pattern in Spring. They handle incoming requests, perform business logic, access data, and represent the data being used in the application. 🚀

---

## 📈  Layered Architecture
A Layered Architecture is a software design pattern in which an application is divided into distinct layers, each with a specific purpose and responsibility.

In a Layered Architecture, the different layers are separated by abstractions, and each layer communicates with the layers above and below it through a well-defined interface. This separation of concerns makes it easier to change or modify one layer without affecting the others.

Here are the main layers that are typically found in a Layered Architecture:

### 💻 Presentation Layer
- The Presentation Layer is responsible for handling user interaction and rendering the user interface. This layer is usually the outermost layer of the application, and communicates with the Business Logic Layer to retrieve data and perform actions.

### 💾 Business Logic Layer
- The Business Logic Layer is responsible for implementing the business logic of the application. This layer communicates with the Data Access Layer to retrieve and update data, and with the Presentation Layer to provide data to the user interface.

### 🗄 Data Access Layer
- The Data Access Layer is responsible for interacting with the data store (such as a database) and providing data to the Business Logic Layer. This layer abstracts the underlying data store and provides a uniform interface for the rest of the application to use.

### 📂 File System Layer (optional)
- The File System Layer is responsible for interacting with the file system and providing data to the Business Logic Layer. This layer is optional and is only used if the application needs to access files on the file system.

A Layered Architecture can help to make an application more modular and easier to maintain, as it separates the different concerns of the application into distinct layers. It is often used in conjunction with other design patterns, such as Dependency Injection and Inversion of Control. 💡

---

## JPA (Java Persistence API)
Is a specification for persisting Java objects to a relational database. It provides a number of annotations that can be used to define how objects should be mapped to the database. Here are some of the main JPA annotations:

### 📝 @Entity
- The ```@Entity``` annotation is used to mark a class as a persistent entity. This tells the JPA implementation that the class should be persisted to the database and should have a corresponding table in the database.

### 📝 @Table
- The @Table annotation is used in the Java Persistence API (JPA) to specify the name of the database table that a Java class is mapped to.

### 🔑 @Id
- The ```@Id``` annotation is used to mark a field as the primary key for an entity. This field will be used as the unique identifier for each entity in the database.

### 📝 @Column
- The ```@Column``` annotation is used to map a field to a column in the database. It can be used to specify the name of the column, as well as other properties such as the column's data type and whether it's nullable or unique.

### 📝 @Repository
- The `@Repository` annotation is typically used in combination with the `@Autowired` annotation to allow dependency injection of the repository into other classes. It can also be used in combination with the `@Transactional` annotation to specify that the repository's methods should be executed within a transaction. The `@Repository` annotation is primarily used as a marker to indicate that a class is a repository, and does not provide any additional functionality. However, it can be helpful in distinguishing repository classes from other types of classes, and can also be used in combination with other annotations to specify additional behavior like `@Entity`.

### 📝 @OneToOne
- The ```@OneToOne``` annotation is used to define a one-to-one relationship between two entities. In this type of relationship, one entity is related to exactly one other entity.

### 📝 @OneToMany
- The ```@OneToMany``` annotation is used to define a one-to-many relationship between two entities. In this type of relationship, one entity is related to many other entities.

### 📝 @ManyToOne
- The ```@ManyToOne``` annotation is used to define a many-to-one relationship between two entities. In this type of relationship, many entities are related to one other entity.

### 📝 @JoinColumn
- The ```@JoinColumn``` annotation is used to specify the name of the column that should be used to join two entities in a relationship. This annotation is often used in conjunction with other annotations, such as ```@OneToOne```, ```@OneToMany```, and ```@ManyToOne```.

### 📝 @GeneratedValue
- The ```@GeneratedValue``` annotation is used to specify that the value of a field should be generated by the database. This can be useful for generating primary keys, for example.

### 📝 @Temporal
- The ```@Temporal``` annotation is used to specify that a field should be persisted as a temporal type (i.e. a date or time). This annotation can be used in conjunction with the TemporalType enum to specify the specific temporal type to use.

### 📝 @Transient
- The ```@Transient``` annotation is used to mark a field as transient, which means that it will not be persisted to the database. This can be useful for fields that are only used in the application and don't need to be persisted.

### 📝 @Enumerated
- The ```@Enumerated``` annotation is used to specify that a field should be persisted as an enumeration. This annotation can be used in conjunction with the EnumType enum to specify the specific enum type to use.

### 📝 @Embeddable
- The ```@Embeddable``` annotation is used to mark a class as an embeddable entity. This means that the class can be embedded as a value inside another entity. An embeddable entity does not have a separate table in the database, and its fields are persisted as part of the entity that contains it.

### 📝 @Embedded
- The ```@Embedded``` annotation is used to mark a field as an embedded entity. This means that the field will be persisted as part of the entity that contains it. The field should be of a type that is annotated with @Embeddable. 

### 📝 @ElementCollection
- The ```@ElementCollection``` annotation is used to map a collection of basic (non-entity) type values to the database. The collection can be a list, set, or map, and the elements of the collection will be persisted as a separate table in the database.

### 📝 @ManyToMany
- The ```@ManyToMany``` annotation is used to define a many-to-many relationship between two entities. In this type of relationship, many entities of one type can be related to many entities of another type.

These are just a few of the many JPA annotations that are available. You can find more information about JPA annotations in the JPA specification. 💻
Overall, these annotations are useful for defining how objects should be persisted to the database and for defining relationships between entities. 💾

---

# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.1/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.1/gradle-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.0.1/reference/htmlsingle/#data.sql.jpa-and-spring-data)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)



