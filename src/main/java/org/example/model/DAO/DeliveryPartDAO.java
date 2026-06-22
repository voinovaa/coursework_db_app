package org.example.model.DAO;

import org.example.database.DatabaseConnection;
import org.example.model.DeliveryPart;
import org.example.model.Part;
import org.example.model.interfaces.IDeliveryPartDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DeliveryPartDAO implements IDeliveryPartDAO {

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
    public List<DeliveryPart> getDeliveryParts(int deliveryId) {

        List<DeliveryPart> deliveryParts = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM delivery_parts WHERE delivery_id = ?");
            stmt.setInt(1, deliveryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                deliveryParts.add(new DeliveryPart(
                        rs.getInt("delivery_id"),
                        rs.getInt("part_id"),
                        rs.getInt("quantity_parts")
                ));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deliveryParts;
    }

    @Override
    public boolean deliveryContainsPart(int deliveryId, int partId) {

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM delivery_parts WHERE delivery_id = ? AND part_id = ?");

            stmt.setInt(1, deliveryId);
            stmt.setInt(2, partId);

            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();
            rs.close();
            stmt.close();
            return exists;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void addDeliveryPart(int deliveryId, int partId, int quantity) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO delivery_parts (delivery_id, part_id, quantity_parts) VALUES (?, ?, ?)");

        stmt.setInt(1, deliveryId);
        stmt.setInt(2, partId);
        stmt.setInt(3, quantity);

        stmt.executeUpdate();

        stmt.close();
    }

    @Override
    public void deleteDeliveryPart(int deliveryId, int partId) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM delivery_parts WHERE delivery_id = ? AND part_id = ?");

        stmt.setInt(1, deliveryId);
        stmt.setInt(2, partId);

        stmt.executeUpdate();

        stmt.close();
    }

    @Override
    public void updateDeliveryQuantity(int deliveryId, List<DeliveryPart> parts) throws Exception {

        int totalQuantity = 0;
        for (DeliveryPart part : parts) {totalQuantity += part.getQuantity();}

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE deliveries SET quantity = ? WHERE delivery_id = ?");

        stmt.setInt(1, totalQuantity);
        stmt.setInt(2, deliveryId);

        stmt.executeUpdate();

        stmt.close();
    }
}