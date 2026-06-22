package org.example.model.interfaces;

import org.example.model.Part;
import org.example.model.PriceChange;
import org.example.model.Supplier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IPriceChangeDAO {

    List<PriceChange> getAllPriceChanges();

    List<Part> getAllParts();

    List<Supplier> getAllSuppliers();

    boolean priceChangeExists(int partId, int supplierId, LocalDate date);

    void addPriceChange(int partId, int supplierId, LocalDate date, BigDecimal value) throws Exception;

    void deletePriceChange(int changeId) throws Exception;
}