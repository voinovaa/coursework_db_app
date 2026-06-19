package org.example.model;

public class Supplier {

    private int supplierId;
    private String name;
    private String address;
    private String phone;

    public Supplier(int supplierId, String name, String address, String phone) {
        this.supplierId = supplierId;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public int getSupplierId() { return supplierId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }

    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
}