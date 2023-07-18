package com.firstspringmvcapp.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.firstspringmvcapp.models.Person;
import com.firstspringmvcapp.repositories.PeopleRepository;
import com.firstspringmvcapp.util.MinioFileHandler;
import com.firstspringmvcapp.util.PersonNotFoundException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> findAll() {
        List<Person> people = peopleRepository.findAll().stream().sorted(
                (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName())
        ).collect(Collectors.toList());
        return people;
    }

    public Person findOne(int id) {
        return peopleRepository.findById(id).orElseThrow(PersonNotFoundException::new);
    }

    public Person findByEmail(String email) {
        return peopleRepository.findByEmail(email).orElse(null);
    }

    public Set<Person> findFriends(int id) {
        Person person = peopleRepository.getOne(id);

        Hibernate.initialize(person.getFriends());
        Hibernate.initialize(person.getFriendOf());

        Set<Person> friendList = person.getFriends();
        Set<Person> friendsOf = person.getFriendOf();

        friendList.addAll(friendsOf);
        return friendList;
    }

    public List<Person> findAvailableFriends(int id) {
        List<Integer> availableFriendsIds = findFriends(id)
                .stream()
                .map(Person::getId)
                .collect(Collectors.toList());
        availableFriendsIds.add(id);

        return peopleRepository.findByIdNotIn(availableFriendsIds).stream().sorted(
                (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName())
        ).collect(Collectors.toList());
    }

    @Transactional
    public void save(Person person) {
        enrichPerson(person);
        peopleRepository.save(person);

        System.out.println("person " + person + "was successfully created");
    }

    @Transactional
    public void update(int id, Person newPerson) {
        final Person person = peopleRepository.getOne(id);
        newPerson.setId(id);
        newPerson.setProfilePicturePath(person.getProfilePicturePath());
        newPerson.setFriends(person.getFriends());
        newPerson.setFriendOf(person.getFriendOf());
        peopleRepository.save(newPerson);

        System.out.println("person " + person + "was successfully changed to " + newPerson);
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(int id) {
        final Person toBeDeleted = peopleRepository.getOne(id);
        peopleRepository.deleteById(id);
        MinioFileHandler.remove("picture-bucket", id + ".png");

        System.out.println("person " + toBeDeleted + "was successfully deleted");
    }

    @Transactional
    public void addFriendById(int personId, int friendId) {
        Person person = findOne(personId);
        Person friend = findOne(friendId);

        person.getFriends().add(friend);
    }

    @Transactional
    public void removeFriendById(int personId, int friendId) {
        Person person = findOne(personId);
        Person friend = findOne(friendId);

        person.getFriends().remove(friend);
        friend.getFriends().remove(person);
    }

    @Transactional
    public void uploadProfilePicture(MultipartFile file, int personId) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
            MinioFileHandler.upload("picture-bucket", personId + ".png", bais);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Person updatedPerson = peopleRepository.getOne(personId);
        if (!updatedPerson.getProfilePicturePath().equals(personId + ".png")) {
            updatedPerson.setProfilePicturePath(personId + ".png");
        }
    }

    private void enrichPerson(Person person) {
        person.setProfilePicturePath("0.png");
        person.setRole("ROLE_USER");
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setCreatedAt(LocalDateTime.now());
    }
}
