package org.example.model.DAO;

import org.example.database.DatabaseConnection;
import org.example.model.Delivery;
import org.example.model.Supplier;
import org.example.model.interfaces.IDeliveryDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeliveryDAO implements IDeliveryDAO {

    @Override
    public List<Delivery> getAllDeliveries() {

        List<Delivery> deliveries = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM deliveries");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                deliveries.add(new Delivery(
                        rs.getInt("delivery_id"),
                        rs.getInt("user_id"),
                        rs.getInt("supplier_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getInt("quantity")));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deliveries;
    }

    @Override
    public List<Supplier> getAllSuppliers() {

        List<Supplier> suppliers = new ArrayList<>();

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM suppliers");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                suppliers.add(new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone")
                ));
            }
            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return suppliers;
    }

    @Override
    public void addDelivery(int userId, int supplierId, LocalDate date, int quantity) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO deliveries (user_id, supplier_id, date, quantity) VALUES (?, ?, ?, ?)");

        stmt.setInt(1, userId);
        stmt.setInt(2, supplierId);
        stmt.setDate(3, java.sql.Date.valueOf(date));
        stmt.setInt(4, quantity);

        stmt.executeUpdate();

        stmt.close();
    }

    @Override
    public void deleteDelivery(int deliveryId) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM deliveries WHERE delivery_id = ?");
        stmt.setInt(1, deliveryId);

        stmt.executeUpdate();

        stmt.close();
    }
}