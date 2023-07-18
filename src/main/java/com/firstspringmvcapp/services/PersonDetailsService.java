package com.firstspringmvcapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.firstspringmvcapp.models.Person;
import com.firstspringmvcapp.repositories.PeopleRepository;
import com.firstspringmvcapp.security.PersonDetails;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByEmail(email);

        if (person.isEmpty()) {
            throw new UsernameNotFoundException("Invalid email!");
        }

        return new PersonDetails(person.get());
    }
}
