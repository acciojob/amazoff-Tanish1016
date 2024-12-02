package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        this.id=id;
        int HH=Integer.parseInt(deliveryTime.substring(0,2));
        int MM=Integer.parseInt(deliveryTime.substring(3));
        this.deliveryTime=(HH*60)+MM;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
