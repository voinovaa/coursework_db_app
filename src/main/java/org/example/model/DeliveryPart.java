package org.example.model;

public class DeliveryPart {

    private int deliveryId;
    private int partId;
    private int quantity_parts;

    public DeliveryPart(int deliveryId, int partId, int quantity_parts) {
        this.deliveryId = deliveryId;
        this.partId = partId;
        this.quantity_parts = quantity_parts;
    }

    public int getDeliveryId() { return deliveryId; }
    public int getPartId() { return partId; }
    public int getQuantity() { return quantity_parts; }

    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
    public void setPartId(int partId) { this.partId = partId; }
    public void setQuantity(int quantity_parts) { this.quantity_parts = quantity_parts; }
}