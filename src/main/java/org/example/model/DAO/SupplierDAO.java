package org.example.model.DAO;

import org.example.database.DatabaseConnection;
import org.example.model.Supplier;
import org.example.model.interfaces.ISupplierDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO implements ISupplierDAO {

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
    public void addSupplier(String name, String address, String phone) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO suppliers (name, address, phone) VALUES (?, ?, ?)");

        stmt.setString(1, name);
        stmt.setString(2, address);
        stmt.setString(3, phone);

        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void updateSupplier(int supplierId, String name, String address, String phone) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("UPDATE suppliers SET name = ?, address = ?, phone = ? WHERE supplier_id = ?");

        stmt.setString(1, name);
        stmt.setString(2, address);
        stmt.setString(3, phone);
        stmt.setInt(4, supplierId);

        stmt.executeUpdate();
        stmt.close();
    }

    @Override
    public void deleteSupplier(int supplierId) throws Exception {

        Connection conn = DatabaseConnection.getInstance().getConnection();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM suppliers WHERE supplier_id = ?");
        stmt.setInt(1, supplierId);

        stmt.executeUpdate();
        stmt.close();
    }
}