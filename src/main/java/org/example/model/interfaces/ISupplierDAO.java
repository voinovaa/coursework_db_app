package org.example.model.interfaces;

import org.example.model.Supplier;

import java.util.List;

public interface ISupplierDAO {

    List<Supplier> getAllSuppliers();

    void addSupplier(String name, String address, String phone) throws Exception;

    void updateSupplier(int supplierId, String name, String address, String phone) throws Exception;

    void deleteSupplier(int supplierId) throws Exception;
}