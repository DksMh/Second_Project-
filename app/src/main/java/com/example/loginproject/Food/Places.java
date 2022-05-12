package com.example.loginproject.Food;

public class Places {
    String name, address, image, idToken;

    public Places(){}

    public Places(String name, String address, String image, String idToken){
        this.name = name;
        this.address = address;
        this.idToken = idToken;
        this.image = image;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Places{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", image='" + image + '\'' +
                ", idToken='" + idToken + '\'' +
                '}';
    }
}
