package com.example.queue;

public class Main {
    public static void main(String[] args) {
        CustomerService service = new CustomerService();
        service.joinLine("Ana");
        service.joinLine("Bruno");
        service.joinLine("Carla");

        System.out.println("Next customer: " + service.nextCustomer());
        System.out.println("Serving: " + service.serveNext());
        System.out.println("Serving: " + service.serveNext());
        System.out.println("Customers waiting: " + service.customersWaiting());
    }
}
