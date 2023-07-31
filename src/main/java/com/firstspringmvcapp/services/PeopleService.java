package com.firstspringmvcapp.services;

import com.firstspringmvcapp.dto.UserDto;
import com.firstspringmvcapp.models.User;
import com.firstspringmvcapp.repositories.PeopleRepository;
import com.firstspringmvcapp.util.Role;
import com.firstspringmvcapp.util.UserNotFoundException;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final MinioFileService minioFileService;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper,
                         MinioFileService minioFileService) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.minioFileService = minioFileService;
    }

    public List<User> findAll() {
        return peopleRepository.findAll().stream()
                .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                .toList();
    }

    public User findOne(int id) {
        return peopleRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User wasn't found by id"));
    }

    public Optional<User> findByEmail(String email) {
        return peopleRepository.findByEmail(email);
    }

    public Set<User> findFriends(int id) {
        User user = peopleRepository.getOne(id);

        Hibernate.initialize(user.getFriends());
        Hibernate.initialize(user.getFriendOf());

        Set<User> friendList = user.getFriends();
        Set<User> friendsOf = user.getFriendOf();
        friendList.addAll(friendsOf);

        return friendList;
    }

    public List<User> findAvailableFriends(int id) {
        List<Integer> availableFriendsIds = findFriends(id)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
        availableFriendsIds.add(id);

        return peopleRepository.findAll()
                .stream()
                .filter(user -> !availableFriendsIds.contains(user.getId()))
                .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                .toList();
    }

    @Transactional
    public void save(User user) {
        enrichPerson(user);
        peopleRepository.save(user);

        System.out.println("user " + user + "was successfully created");
    }

    @Transactional
    public void update(int id, User newUser) {
        final User user = peopleRepository.getOne(id);
        newUser.setId(id);
        newUser.setProfilePicturePath(user.getProfilePicturePath());
        newUser.setFriends(user.getFriends());
        newUser.setFriendOf(user.getFriendOf());
        peopleRepository.save(newUser);

        System.out.println("user " + user + "was successfully changed to " + newUser);
    }

    @Transactional
    public void delete(int id) {
        final User toBeDeleted = peopleRepository.getOne(id);
        peopleRepository.deleteById(id);
        minioFileService.remove("picture-bucket", id + ".png");

        System.out.println("person " + toBeDeleted + "was successfully deleted");
    }

    @Transactional
    public void addFriendById(int userId, int friendId) {
        User user = peopleRepository.findById(userId).get();
        User friend = peopleRepository.findById(friendId).get();

        user.getFriends().add(friend);
    }

    @Transactional
    public void removeFriendById(int userId, int friendId) {
        User user = findOne(userId);
        User friend = findOne(friendId);

        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
    }

    @Transactional
    public void uploadProfilePicture(MultipartFile file, int personId) {
        String pictureName = personId + ".png";
        minioFileService.upload("picture-bucket", pictureName, file);

        User updatedUser = peopleRepository.getOne(personId);
        if (!(pictureName).equals(updatedUser.getProfilePicturePath())) {
            updatedUser.setProfilePicturePath(pictureName);
        }
    }

    private void enrichPerson(User user) {
        user.setProfilePicturePath("0.png");
        user.setRole(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
    }

    public User userDtoToUser(UserDto userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public UserDto userToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }
}
