package com.example.taxsystem.converter.tests;

import com.example.taxsystem.common.annotation.Mappable;
import com.example.taxsystem.common.validation.ValidationSupport;
import com.example.taxsystem.converter.application.ConverterEngine;
import com.example.taxsystem.converter.application.ConverterRegistry;
import com.example.taxsystem.converter.domain.Converter;
import com.example.taxsystem.converter.infrastructure.StringToLocalDateTimeConverter;
import com.example.taxsystem.stresstest.domain.StressExecutionMode;
import com.example.taxsystem.stresstest.domain.StressTestConfig;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ConverterEngineTest {

    private final ConverterRegistry registry = new ConverterRegistry(Arrays.<Converter<?, ?>>asList(
            new StringToLocalDateTimeConverter(),
            new UpperCaseLabelConverter()
    ));
    private final ConverterEngine converterEngine = new ConverterEngine(
            registry,
            new ValidationSupport(Validation.buildDefaultValidatorFactory().getValidator())
    );

    @Test
    void shouldConvertNestedMapToStressConfig() {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        Map<String, Object> target = new LinkedHashMap<String, Object>();
        target.put("url", "https://example.org/api");
        target.put("method", "POST");
        target.put("headers", Collections.singletonMap("X-Test", "yes"));
        target.put("payload", "{\"hello\":\"world\"}");

        Map<String, Object> execution = new LinkedHashMap<String, Object>();
        execution.put("totalRequests", 10);
        execution.put("concurrencyLevel", 2);
        execution.put("timeoutMillis", 500);
        execution.put("rampUpMillis", 50);
        execution.put("mode", "ASYNC");
        execution.put("scheduledAt", "2026-04-03T10:15:30");

        payload.put("target", target);
        payload.put("execution", execution);

        StressTestConfig config = converterEngine.convert(payload, StressTestConfig.class);

        assertThat(config.getTarget().getUrl()).isEqualTo("https://example.org/api");
        assertThat(config.getTarget().getMethod().name()).isEqualTo("POST");
        assertThat(config.getExecution().getMode()).isEqualTo(StressExecutionMode.ASYNC);
        assertThat(config.getExecution().getScheduledAt()).isEqualTo(LocalDateTime.parse("2026-04-03T10:15:30"));
    }

    @Test
    void shouldUseCustomRegisteredConverterForCollectionElements() {
        SourceContainer source = new SourceContainer();
        source.setLabels(Arrays.asList("fresh", "organic"));

        TargetContainer converted = converterEngine.convert(source, TargetContainer.class);

        assertThat(converted.getLabels())
                .extracting(ConvertedLabel::getValue)
                .containsExactly("FRESH", "ORGANIC");
    }

    @Mappable
    public static class SourceContainer {
        private List<String> labels;

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }
    }

    @Mappable
    public static class TargetContainer {
        private List<ConvertedLabel> labels;

        public List<ConvertedLabel> getLabels() {
            return labels;
        }

        public void setLabels(List<ConvertedLabel> labels) {
            this.labels = labels;
        }
    }

    @Mappable
    public static class ConvertedLabel {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static class UpperCaseLabelConverter implements Converter<String, ConvertedLabel> {

        @Override
        public Class<String> sourceType() {
            return String.class;
        }

        @Override
        public Class<ConvertedLabel> targetType() {
            return ConvertedLabel.class;
        }

        @Override
        public ConvertedLabel convert(String source, ConverterEngine converterEngine) {
            ConvertedLabel label = new ConvertedLabel();
            label.setValue(source.toUpperCase(Locale.ENGLISH));
            return label;
        }
    }
}
