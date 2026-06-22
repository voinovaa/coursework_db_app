package org.example.model.DAO;

import org.example.database.DatabaseConnection;
import org.example.model.Role;
import org.example.model.interfaces.IRoleDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO implements IRoleDAO {

    @Override
    public List<Role> getAllRoles() {

        List<Role> roles = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM roles");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roles.add(new Role(
                        rs.getInt("role_id"),
                        rs.getString("name")
                ));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roles;
    }
}