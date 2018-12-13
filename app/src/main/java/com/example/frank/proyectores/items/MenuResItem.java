package com.example.frank.proyectores.items;

public class MenuResItem {

    private String property,name,description,foto,id;
    private Double price;

    public MenuResItem(String property, String name, String description, String foto, String id, Double price) {
        this.property = property;
        this.name = name;
        this.description = description;
        this.foto = foto;
        this.id = id;
        this.price = price;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
