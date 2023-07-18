package com.firstspringmvcapp.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.firstspringmvcapp.models.Person;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    List<Person> findByIdNotIn(List<Integer> ids);

    Optional<Person> findByName(String name);

    Optional<Person> findByEmail(String email);
}