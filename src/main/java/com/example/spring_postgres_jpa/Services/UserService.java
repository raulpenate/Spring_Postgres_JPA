package com.example.spring_postgres_jpa.Services;

import com.example.spring_postgres_jpa.Models.UserModel;
import com.example.spring_postgres_jpa.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public ArrayList<UserModel> getUsers(){
        return (ArrayList<UserModel>) userRepository.findAll();
    }

    public Optional<UserModel> getUserById(Long id){
        return userRepository.findById(id);
    }

    public ArrayList<UserModel> getUsersByPriority(Integer priority){
        return userRepository.findByPriority(priority);
    }

    public UserModel postUser(UserModel user){
        return userRepository.save(user);
    }

    public boolean deleteUser(Long id){
        try{
            userRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }


}
