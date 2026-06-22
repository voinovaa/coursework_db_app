package org.example.model.interfaces;

import org.example.model.Delivery;
import org.example.model.Supplier;

import java.time.LocalDate;
import java.util.List;

public interface IDeliveryDAO {

    List<Delivery> getAllDeliveries();

    List<Supplier> getAllSuppliers();

    void addDelivery(int userId, int supplierId, LocalDate date, int quantity) throws Exception;

    void deleteDelivery(int deliveryId) throws Exception;
}