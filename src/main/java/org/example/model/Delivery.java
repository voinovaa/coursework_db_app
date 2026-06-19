package org.example.model;

import java.time.LocalDate;

public class Delivery {

    private int deliveryId;
    private int userId;
    private int supplierId;
    private LocalDate date;
    private int quantity;

    public Delivery(int deliveryId, int userId, int supplierId, LocalDate date, int quantity) {
        this.deliveryId = deliveryId;
        this.userId = userId;
        this.supplierId = supplierId;
        this.date = date;
        this.quantity = quantity;
    }

    public int getDeliveryId() { return deliveryId; }
    public int getUserId() { return userId; }
    public int getSupplierId() { return supplierId; }
    public LocalDate getDate() { return date; }
    public int getQuantity() { return quantity; }

    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}