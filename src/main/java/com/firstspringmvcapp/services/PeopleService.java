package com.firstspringmvcapp.services;

import com.firstspringmvcapp.models.Person;
import com.firstspringmvcapp.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.MalformedURLException;
import java.util.Comparator;
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
        List<Person> people = peopleRepository.findAll().stream().sorted(
                (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName())
        ).collect(Collectors.toList());
        return people;
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


        return peopleRepository.findByIdNotIn(availableFriendsIds).stream().sorted(
                (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName())
        ).collect(Collectors.toList());
    }

    @Transactional
    public void save(Person person) {
        person.setProfilePicturePath("0.png");
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
    public void delete(int id) {
        final Person toBeDeleted = peopleRepository.getOne(id);
        peopleRepository.deleteById(id);
        File pfp = new File(
                "C:\\Programs\\java_projects\\FirstSpringMVCApp\\src\\main\\resources\\images\\pfp\\" + id + ".png");
        pfp.delete();
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
    public void uploadProfilePicture(HttpSession session, CommonsMultipartFile file, int personId) {
        ServletContext servletContext = session.getServletContext();
        String path = servletContext.getRealPath("/images/pfp");
        path = path.replace("FirstSpringMVCApp\\target\\FirstSpringMVCApp\\",
                "FirstSpringMVCApp\\src\\main\\resources\\");
        String filename = file.getOriginalFilename();

        System.out.println(path + " " + filename);
        System.out.println(path + "\\" + personId + ".png");
        if (file.isEmpty() || !(filename != null && (filename.endsWith(".png") || filename.endsWith(".jpg")))) {
            System.out.println("Your file should be valid .png image!");
            return;
        }

        try (BufferedOutputStream bout = new BufferedOutputStream(
                new FileOutputStream(path + "\\" + personId + ".png"))) {
            bout.write(file.getBytes());
            bout.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        peopleRepository.getOne(personId).setProfilePicturePath(personId + ".png");
    }
}
