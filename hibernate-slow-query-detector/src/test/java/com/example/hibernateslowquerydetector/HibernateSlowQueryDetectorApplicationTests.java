package com.example.hibernateslowquerydetector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hibernateslowquerydetector.dto.SlowQueryStatsResponse;
import com.example.hibernateslowquerydetector.entity.SlowQueryRecord;
import com.example.hibernateslowquerydetector.repository.SlowQueryRecordRepository;
import com.example.hibernateslowquerydetector.service.DemoDataLoaderService;
import com.example.hibernateslowquerydetector.service.HibernateQueryExecutionService;
import com.example.hibernateslowquerydetector.service.SlowQueryService;

@SpringBootTest
@AutoConfigureMockMvc
class HibernateSlowQueryDetectorApplicationTests {

    @Autowired
    private DemoDataLoaderService demoDataLoaderService;

    @Autowired
    private HibernateQueryExecutionService hibernateQueryExecutionService;

    @Autowired
    private SlowQueryRecordRepository slowQueryRecordRepository;

    @Autowired
    private SlowQueryService slowQueryService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        slowQueryRecordRepository.deleteAll();
        demoDataLoaderService.loadSampleData();
    }

    @Test
    void shouldNotRegisterFastQuery() {
        hibernateQueryExecutionService.runFastQuery();

        assertThat(slowQueryRecordRepository.count()).isZero();
    }

    @Test
    void shouldRegisterSlowQueryAboveThreshold() {
        hibernateQueryExecutionService.runSlowQuery();

        assertThat(slowQueryRecordRepository.count()).isEqualTo(1);
        SlowQueryRecord record = slowQueryRecordRepository.findAll().get(0);
        assertThat(record.getExecutionTimeMs()).isGreaterThanOrEqualTo(200L);
        assertThat(record.getSqlText()).contains("SLEEP_MS");
    }

    @Test
    void shouldCalculateStatsCorrectly() {
        hibernateQueryExecutionService.runSlowQuery();
        hibernateQueryExecutionService.runSlowQuery();

        SlowQueryStatsResponse stats = slowQueryService.getStats();

        assertThat(stats.getTotalDetected()).isEqualTo(2L);
        assertThat(stats.getMaxExecutionTimeMs()).isGreaterThanOrEqualTo(stats.getMinExecutionTimeMs());
        assertThat(stats.getAvgExecutionTimeMs()).isGreaterThanOrEqualTo(200.0);
    }

    @Test
    void shouldClearHistory() throws Exception {
        hibernateQueryExecutionService.runSlowQuery();
        assertThat(slowQueryRecordRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/slow-queries"))
                .andExpect(status().isNoContent());

        assertThat(slowQueryRecordRepository.count()).isZero();
    }

    @Test
    void shouldExposeListEndpoint() throws Exception {
        hibernateQueryExecutionService.runSlowQuery();

        mockMvc.perform(get("/slow-queries"))
                .andExpect(status().isOk());
    }
}
