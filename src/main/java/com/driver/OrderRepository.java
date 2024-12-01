package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository(){
        this.orderMap = new HashMap<String, Order>();
        this.partnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerToOrderMap = new HashMap<String, HashSet<String>>();
        this.orderToPartnerMap = new HashMap<String, String>();
    }

    public void saveOrder(Order order){
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId){
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        partnerMap.put(partnerId, partner);
        partnerToOrderMap.put(partnerId, new HashSet<>());
    }

    public void saveOrderPartnerMap(String orderId, String partnerId){
        if(orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)){
            orderToPartnerMap.put(orderId, partnerId);
            partnerToOrderMap.get(partnerId).add(orderId);
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
        }
    }

    public Order findOrderById(String orderId){
        return orderMap.getOrDefault(orderId,null);
    }

    public DeliveryPartner findPartnerById(String partnerId){
        return partnerMap.getOrDefault(partnerId,null);
    }

    public Integer findOrderCountByPartnerId(String partnerId){
        if (partnerToOrderMap.containsKey(partnerId)) {
            return partnerToOrderMap.get(partnerId).size();
        }
        return 0;
    }

    public List<String> findOrdersByPartnerId(String partnerId){
        if (partnerToOrderMap.containsKey(partnerId)) {
            return new ArrayList<>(partnerToOrderMap.get(partnerId));
        }
        return new ArrayList<>();
    }

    public List<String> findAllOrders(){
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId){
        if (partnerToOrderMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                orderToPartnerMap.remove(orderId);
            }
            partnerToOrderMap.remove(partnerId);
        }
        partnerMap.remove(partnerId);
    }

    public void deleteOrder(String orderId){
        if (orderMap.containsKey(orderId)) {
            if (orderToPartnerMap.containsKey(orderId)) {
                String partnerId = orderToPartnerMap.get(orderId);
                partnerToOrderMap.get(partnerId).remove(orderId);
                DeliveryPartner partner = partnerMap.get(partnerId);
                partner.setNumberOfOrders(partner.getNumberOfOrders() - 1);
                orderToPartnerMap.remove(orderId);
            }
            orderMap.remove(orderId);
        }
    }

    public Integer findCountOfUnassignedOrders(){
        int unassignedOrders = 0;
        for (String orderId : orderMap.keySet()) {
            if (!orderToPartnerMap.containsKey(orderId)) {
                unassignedOrders++;
            }
        }
        return unassignedOrders;

    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId){
        int givenTime = convertTimeToMinutes(timeString);
        int count = 0;

        if (partnerToOrderMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                Order order = orderMap.get(orderId);
                if (order.getDeliveryTime() > givenTime) {
                    count++;
                }
            }
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId){
        int latestTime = 0;

        if (partnerToOrderMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.get(partnerId)) {
                Order order = orderMap.get(orderId);
                if (order.getDeliveryTime() > latestTime) {
                    latestTime = order.getDeliveryTime();
                }
            }
        }

        return convertMinutesToTime(latestTime);
    }

    private String convertMinutesToTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    private int convertTimeToMinutes(String timeString) {
        String[] timeParts = timeString.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return hours * 60 + minutes;
    }
}