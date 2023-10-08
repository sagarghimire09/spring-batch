package com.example.springbatch.util;

import com.example.springbatch.model.User;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserItemPreparedStatementSetter implements ItemPreparedStatementSetter<User> {
    @Override
    public void setValues(User item, PreparedStatement ps) throws SQLException {
        ps.setString(1, item.getFirstName());
        ps.setString(2, item.getLastName());
        ps.setString(3, item.getEmail());
        ps.setString(4, item.getPhone());
        ps.setBoolean(5, item.getActive());
    }
}
