package com.firstspringmvcapp.repositories;

import com.firstspringmvcapp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeopleRepository extends JpaRepository<Person, Integer> {}