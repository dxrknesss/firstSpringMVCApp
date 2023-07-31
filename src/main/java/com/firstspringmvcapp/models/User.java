package com.firstspringmvcapp.models;

import com.firstspringmvcapp.util.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_friend",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private Set<User> friends;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_friend",
            joinColumns = @JoinColumn(name = "friend_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> friendOf;

    @Column(name = "pfp_path")
    private String profilePicturePath;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
