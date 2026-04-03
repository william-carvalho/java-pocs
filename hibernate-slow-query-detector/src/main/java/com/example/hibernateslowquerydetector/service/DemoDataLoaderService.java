package com.example.hibernateslowquerydetector.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hibernateslowquerydetector.entity.Customer;
import com.example.hibernateslowquerydetector.entity.OrderEntity;
import com.example.hibernateslowquerydetector.repository.CustomerRepository;
import com.example.hibernateslowquerydetector.repository.OrderEntityRepository;

@Service
public class DemoDataLoaderService {

    private final CustomerRepository customerRepository;
    private final OrderEntityRepository orderEntityRepository;
    private final JdbcTemplate jdbcTemplate;

    public DemoDataLoaderService(CustomerRepository customerRepository,
                                 OrderEntityRepository orderEntityRepository,
                                 JdbcTemplate jdbcTemplate) {
        this.customerRepository = customerRepository;
        this.orderEntityRepository = orderEntityRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String loadSampleData() {
        registerSleepAlias();

        orderEntityRepository.deleteAllInBatch();
        customerRepository.deleteAllInBatch();

        List<Customer> customers = new ArrayList<Customer>();
        for (int index = 1; index <= 40; index++) {
            Customer customer = new Customer();
            customer.setName("Customer " + index);
            customer.setEmail("customer" + index + "@example.com");
            customer.setCity(index % 2 == 0 ? "Sao Paulo" : "Florianopolis");
            customers.add(customer);
        }

        List<Customer> savedCustomers = customerRepository.saveAll(customers);
        List<OrderEntity> orders = new ArrayList<OrderEntity>();

        for (int index = 0; index < savedCustomers.size(); index++) {
            Customer customer = savedCustomers.get(index);
            OrderEntity order = new OrderEntity();
            order.setCustomer(customer);
            order.setStatus(index % 2 == 0 ? "PAID" : "PENDING");
            order.setTotalAmount(BigDecimal.valueOf(100 + index).setScale(2, RoundingMode.HALF_UP));
            orders.add(order);
        }

        orderEntityRepository.saveAll(orders);
        return "Sample data loaded: " + savedCustomers.size() + " customers and " + orders.size() + " orders";
    }

    public void registerSleepAlias() {
        jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS SLEEP_MS FOR \"com.example.hibernateslowquerydetector.util.H2DatabaseFunctions.sleepMs\"");
    }
}
