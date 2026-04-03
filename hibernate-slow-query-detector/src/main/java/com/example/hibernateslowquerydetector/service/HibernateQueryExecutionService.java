package com.example.hibernateslowquerydetector.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import com.example.hibernateslowquerydetector.detector.SlowQueryDetectorService;
import com.example.hibernateslowquerydetector.entity.Customer;
import com.example.hibernateslowquerydetector.repository.CustomerRepository;

@Service
public class HibernateQueryExecutionService {

    private static final int SLOW_QUERY_DELAY_MS = 300;

    private final CustomerRepository customerRepository;
    private final EntityManager entityManager;
    private final SlowQueryDetectorService slowQueryDetectorService;
    private final DemoDataLoaderService demoDataLoaderService;

    public HibernateQueryExecutionService(CustomerRepository customerRepository,
                                          EntityManager entityManager,
                                          SlowQueryDetectorService slowQueryDetectorService,
                                          DemoDataLoaderService demoDataLoaderService) {
        this.customerRepository = customerRepository;
        this.entityManager = entityManager;
        this.slowQueryDetectorService = slowQueryDetectorService;
        this.demoDataLoaderService = demoDataLoaderService;
    }

    public String runFastQuery() {
        List<Customer> customers = slowQueryDetectorService.track("demo.runFastQuery", () ->
                customerRepository.findTop20ByCity("Florianopolis"));
        return "Fast query executed. Customers found: " + customers.size();
    }

    public String runSlowQuery() {
        demoDataLoaderService.registerSleepAlias();
        Number result = slowQueryDetectorService.track("demo.runSlowQuery", () ->
                (Number) entityManager.createNativeQuery("SELECT SLEEP_MS(:delay)")
                        .setParameter("delay", SLOW_QUERY_DELAY_MS)
                        .getSingleResult());
        return "Slow query executed with delay " + result.longValue() + " ms";
    }
}
