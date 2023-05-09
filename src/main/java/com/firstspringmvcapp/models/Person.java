package com.firstspringmvcapp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.File;
import java.util.List;

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
    private List<Person> friends;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "person_friend",
            joinColumns = @JoinColumn(name = "friend_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<Person> friendOf;

    @Column(name = "pfp_path")
    private String profilePicturePath;

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

    public boolean profilePictureExists() {
        return new File("C:\\Programs\\java_projects\\FirstSpringMVCApp\\src\\main\\resources\\images\\pfp\\" + this.getId() + ".png").exists();
    }
}
