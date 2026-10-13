package com.example.queue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerServiceTest {
    @Test
    void servesCustomersInArrivalOrder() {
        CustomerService service = new CustomerService();
        service.joinLine("Ana");
        service.joinLine("Bruno");

        assertEquals("Ana", service.serveNext());
        assertEquals("Bruno", service.nextCustomer());
        assertEquals(1, service.customersWaiting());
    }

    @Test
    void rejectsBlankCustomerNames() {
        CustomerService service = new CustomerService();

        assertThrows(IllegalArgumentException.class, () -> service.joinLine("  "));
    }
}
