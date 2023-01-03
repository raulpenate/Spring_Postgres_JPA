package com.example.spring_postgres_jpa.Repositories;

import com.example.spring_postgres_jpa.Models.UserModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserRepository extends CrudRepository<UserModel, Long> {

    public abstract UserModel findById(Integer id);
    public abstract ArrayList<UserModel> findByPriority(Integer priority);
}
