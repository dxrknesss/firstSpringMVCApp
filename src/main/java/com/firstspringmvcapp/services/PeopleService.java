package com.firstspringmvcapp.services;

import com.firstspringmvcapp.models.Person;
import com.firstspringmvcapp.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    public Person findByName(String name) {
        return peopleRepository.findByName(name).orElse(null);
    }

    //@Transactional
    public List<Person> findFriends(int id) {
        Person person = peopleRepository.getOne(id);

        Hibernate.initialize(person.getFriends());
        Hibernate.initialize(person.getFriendOf());

        List<Person> friendList = person.getFriends();
        List<Person> friendsOf = person.getFriendOf();

        friendList.addAll(friendsOf);
        return friendList;
    }

    public List<Person> findAvailableFriends(int id) {
        List<Integer> availableFriendsIds = findFriends(id)
                .stream()
                .map(Person::getId)
                .collect(Collectors.toList());
        availableFriendsIds.add(id);

        return peopleRepository.findByIdNotIn(availableFriendsIds);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person newPerson) {
        newPerson.setId(id);
        peopleRepository.save(newPerson);
    }

    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
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
}
