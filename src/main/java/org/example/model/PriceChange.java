package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceChange {

    private int changeId;
    private int partId;
    private int supplierId;
    private LocalDate date;
    private BigDecimal value;

    public PriceChange(int changeId, int partId, int supplierId, LocalDate date, BigDecimal value) {
        this.changeId = changeId;
        this.partId = partId;
        this.supplierId = supplierId;
        this.date = date;
        this.value = value;
    }

    public int getChangeId() { return changeId; }
    public int getPartId() { return partId; }
    public int getSupplierId() { return supplierId; }
    public LocalDate getDate() { return date; }
    public BigDecimal getValue() { return value; }

    public void setChangeId(int changeId) { this.changeId = changeId; }
    public void setPartId(int partId) { this.partId = partId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setValue(BigDecimal value) { this.value = value; }
}