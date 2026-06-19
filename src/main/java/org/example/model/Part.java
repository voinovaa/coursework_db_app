package org.example.model;

public class Part {

    private int partId;
    private String name;
    private String article;

    public Part(int partId, String name, String article) {
        this.partId = partId;
        this.name = name;
        this.article = article;
    }

    public int getPartId() { return partId; }
    public String getName() { return name; }
    public String getArticle() { return article; }

    public void setPartId(int partId) { this.partId = partId; }
    public void setName(String name) { this.name = name; }
    public void setArticle(String article) { this.article = article; }
}