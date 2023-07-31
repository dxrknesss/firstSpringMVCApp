package com.firstspringmvcapp.repositories;


import com.firstspringmvcapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<User, Integer> {
    List<User> findByIdNotIn(List<Integer> ids);

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);
}