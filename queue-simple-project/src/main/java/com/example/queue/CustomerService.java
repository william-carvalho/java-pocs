package com.example.queue;

/** A practical example that serves customers in arrival order. */
public class CustomerService {
    private final SimpleQueue<String> customers = new SimpleQueue<String>();

    public void joinLine(String customerName) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        customers.enqueue(customerName);
    }

    public String serveNext() {
        return customers.dequeue();
    }

    public String nextCustomer() {
        return customers.peek();
    }

    public int customersWaiting() {
        return customers.size();
    }
}
