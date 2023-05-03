package com.firstspringmvcapp.dao;

import com.firstspringmvcapp.models.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person friend = new Person();

        friend.setId(resultSet.getInt("p2.id"));
        friend.setName(resultSet.getString("p2.name"));
        friend.setAge(resultSet.getInt("p2.age"));

        return friend;
    }
}
