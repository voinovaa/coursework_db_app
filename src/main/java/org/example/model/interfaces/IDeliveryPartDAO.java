package org.example.model.interfaces;

import org.example.model.DeliveryPart;
import org.example.model.Part;

import java.util.List;

public interface IDeliveryPartDAO {

    List<Part> getAllParts();

    List<DeliveryPart> getDeliveryParts(int deliveryId);

    boolean deliveryContainsPart(int deliveryId, int partId);

    void addDeliveryPart(int deliveryId, int partId, int quantity) throws Exception;

    void deleteDeliveryPart(int deliveryId, int partId) throws Exception;

    void updateDeliveryQuantity(int deliveryId, List<DeliveryPart> parts) throws Exception;
}