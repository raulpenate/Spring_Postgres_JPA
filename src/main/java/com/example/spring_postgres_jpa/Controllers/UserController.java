package com.example.spring_postgres_jpa.Controllers;

import com.example.spring_postgres_jpa.Models.UserModel;
import com.example.spring_postgres_jpa.Repositories.UserRepository;
import com.example.spring_postgres_jpa.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public ArrayList<UserModel> getUsers(){
        return this.userService.getUsers();
    }

    @GetMapping(path = "/{id}")
    public Optional<UserModel> getUserById(@PathVariable("id") Long id){
        return this.userService.getUserById(id);
    }

    @GetMapping("/query")
    public ArrayList<UserModel> getUserByPriority(@RequestParam("priority") Integer priority){
        return this.userService.getUsersByPriority(priority);
    }

    @PostMapping
    public UserModel postUser(@RequestBody UserModel user){
        return this.userService.postUser(user);
    }

    @DeleteMapping(path = "/{id}")
    public String deleteById(@PathVariable("id") Long id){
        boolean ok = this.userService.deleteUser(id);
        if(ok){
            return String.format("User by id: %s was deleted", id);
        }else{
            return String.format("User by id: %s request to delete failed", id);
        }
    }
}
