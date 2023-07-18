package com.firstspringmvcapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Min(value = 1, message = "Age should be greater than 0")
    @Max(value = 110, message = "Aren't you a bit too old to use the internet?")
    @Column(name = "age")
    private int age;

    @NotEmpty(message = "Name should not be empty!")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 letters!")
    @Column(name = "name")
    private String name;

    @NotEmpty(message = "Email should not be empty!")
    @Email(message = "You're not even trying... Enter valid email!")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
    @Column(name = "email")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_friend",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<Person> friends;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_friend",
            joinColumns = @JoinColumn(name = "friend_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Set<Person> friendOf;

    @Column(name = "pfp_path")
    private String profilePicturePath;

    @Column(name = "role")
    private String role;

    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Person(int age, String name, String email) {
        this.age = age;
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
