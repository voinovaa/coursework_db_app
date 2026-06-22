package org.example.model.DAO;

import org.example.database.DatabaseConnection;
import org.example.model.Part;
import org.example.model.interfaces.IPartDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PartDAO implements IPartDAO {

    @Override
    public List<Part> getAllParts() {

        List<Part> parts = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM parts");

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                parts.add(new Part(
                        rs.getInt("part_id"),
                        rs.getString("name"),
                        rs.getString("article")
                ));
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parts;
    }

    @Override
    public void addPart(String name, String article) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO parts (name, article) VALUES (?, ?)");

        stmt.setString(1, name);
        stmt.setString(2, article);
        stmt.executeUpdate();

        stmt.close();
    }

    @Override
    public void updatePart(int partId, String name, String article) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE parts SET name = ?, article = ? WHERE part_id = ?");

        stmt.setString(1, name);
        stmt.setString(2, article);
        stmt.setInt(3, partId);

        stmt.executeUpdate();

        stmt.close();
    }

    @Override
    public void deletePart(int partId) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM parts WHERE part_id = ?");
        stmt.setInt(1, partId);
        stmt.executeUpdate();

        stmt.close();
    }
}