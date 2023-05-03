package com.firstspringmvcapp.dao;

import com.firstspringmvcapp.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PersonDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> index(IndexState state, int id) {
        if (state == IndexState.WITH) {
            return jdbcTemplate.query("select * from person", new BeanPropertyRowMapper<>(Person.class));
        }
        return jdbcTemplate.query("select * from person " +
                        "where id not in (select friend_id from person_friend where person_id = ?) and id != ? " +
                        "and id not in (select person_id from person_friend where friend_id = ?)",
                new Object[]{id, id, id},
                new BeanPropertyRowMapper<>(Person.class));
    }

    public Optional<Person> show(String name) {
        return jdbcTemplate.query("select * from person where name = ?",
                        new Object[]{name}, new BeanPropertyRowMapper<>(Person.class))
                .stream()
                .findAny();
    }

    public Person show(int id) {
        return jdbcTemplate.query("select * from person where id = ?",
                        new Object[]{id}, new BeanPropertyRowMapper<>(Person.class))
                .stream()
                .findAny()
                .orElse(null);
    }

    public void save(Person person) {
        jdbcTemplate.update("insert into person (name, age, email) values(?, ?, ?)",
                person.getName(), person.getAge(), person.getEmail());

        person.setId(jdbcTemplate.query("select id from person where name = ? and age = ? and email = ?",
                        new Object[]{person.getName(), person.getAge(), person.getEmail()},
                        new SingleColumnRowMapper<>(Integer.class))
                .stream()
                .findAny()
                .orElse(0));

        System.out.println(person + " was created");
    }

    public void update(int id, Person newPerson) {
        final Person toBeUpdated = show(id);

        jdbcTemplate.update("update person set name = ?, age = ?, email = ? where id = ?",
                newPerson.getName(), newPerson.getAge(), newPerson.getEmail(), id);

        System.out.println(toBeUpdated + " was changed to " + newPerson);
    }

    public void delete(int id) {
        final Person person = show(id);

        jdbcTemplate.update("delete from person where id = ?", id);

        System.out.println(person + " was deleted from database");
    }

    public void addFriend(int personId, int friendId) {
        jdbcTemplate.update("insert into person_friend (person_id, friend_id) values (?, ?);", personId, friendId);
    }

    public List<Person> getFriends(int personId) {
        return jdbcTemplate.query("select p1.id, p1.name, p1.age, " +
                        "p2.id as \"p2.id\", p2.name as \"p2.name\", p2.age as \"p2.age\" " +
                        "from person as p1 " +
                        "join person_friend on p1.id = person_friend.person_id " +
                        "join person as p2 on p2.id = person_friend.friend_id " +
                        "where p1.id = ? " +
                        "union " +
                        "select p3.id, p3.name, p3.age, p4.id, p4.name, p4.age " +
                        "from person as p3 " +
                        "join person_friend on p3.id = person_friend.friend_id " +
                        "join person as p4 on p4.id = person_friend.person_id " +
                        "where p3.id = ?",
                new Object[]{personId, personId}, new FriendMapper());
    }

    public void removeFriend(int personId, int friendId) {
        jdbcTemplate.update("delete from person_friend where person_id = ? and friend_id = ?;" +
                        "delete from person_friend where friend_id = ? and person_id = ?",
                personId, friendId, personId, friendId);
    }
}
