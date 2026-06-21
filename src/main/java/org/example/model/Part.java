package org.example.model;

import java.math.BigDecimal;

public class Part {

    private int partId;
    private String name;
    private String article;
    private BigDecimal price;

    public Part(int partId, String name, String article, BigDecimal price) {
        this.partId = partId;
        this.name = name;
        this.article = article;
        this.price = price;
    }

    public int getPartId() { return partId; }
    public String getName() { return name; }
    public String getArticle() { return article; }
    public BigDecimal getPrice() { return price; }

    public void setPartId(int partId) { this.partId = partId; }
    public void setName(String name) { this.name = name; }
    public void setArticle(String article) { this.article = article; }
    public void setPrice(BigDecimal price) { this.price = price; }
}