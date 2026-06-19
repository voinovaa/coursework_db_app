package org.example.model;

public class Role {

    private int roleId;
    private String name;

    public Role(int roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    public int getRoleId() { return roleId; }
    public String getName() { return name; }

    public void setRoleId(int roleId) { this.roleId = roleId; }
    public void setName(String name) { this.name = name; }
}