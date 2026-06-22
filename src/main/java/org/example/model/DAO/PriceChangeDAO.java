package org.example.model.DAO;

import org.example.database.DatabaseConnection;
import org.example.model.Part;
import org.example.model.PriceChange;
import org.example.model.Supplier;
import org.example.model.interfaces.IPriceChangeDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceChangeDAO implements IPriceChangeDAO {

    @Override
    public List<PriceChange> getAllPriceChanges() {

        List<PriceChange> changes = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM price_changes ORDER BY date DESC");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                changes.add(new PriceChange(
                        rs.getInt("change_id"),
                        rs.getInt("part_id"),
                        rs.getInt("supplier_id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBigDecimal("value")
                ));
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return changes;
    }

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
    public boolean priceChangeExists(int partId, int supplierId, LocalDate date) {

        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM price_changes WHERE part_id = ? AND supplier_id = ? AND date = ?");
            stmt.setInt(1, partId);
            stmt.setInt(2, supplierId);
            stmt.setDate(3, java.sql.Date.valueOf(date));

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
    public void addPriceChange(int partId, int supplierId, LocalDate date, BigDecimal value) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO price_changes (part_id, supplier_id, date, value) VALUES (?, ?, ?, ?)");

        stmt.setInt(1, partId);
        stmt.setInt(2, supplierId);
        stmt.setDate(3, java.sql.Date.valueOf(date));
        stmt.setBigDecimal(4, value);

        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void deletePriceChange(int changeId) throws Exception {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM price_changes WHERE change_id = ?");

        stmt.setInt(1, changeId);
        stmt.executeUpdate();
        stmt.close();
    }
}