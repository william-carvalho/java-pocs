package com.example.validationframework;

import com.example.validationframework.annotation.Email;
import com.example.validationframework.annotation.Max;
import com.example.validationframework.annotation.Min;
import com.example.validationframework.annotation.NotBlank;
import com.example.validationframework.annotation.NotNull;
import com.example.validationframework.annotation.Pattern;
import com.example.validationframework.annotation.Size;
import com.example.validationframework.engine.DefaultValidatorEngine;
import com.example.validationframework.engine.ValidatorEngine;
import com.example.validationframework.engine.ValidatorRegistry;
import com.example.validationframework.model.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultValidatorEngineTest {

    private ValidatorEngine validatorEngine;

    @BeforeEach
    void setUp() {
        validatorEngine = new DefaultValidatorEngine(ValidatorRegistry.withDefaults());
    }

    @Test
    void shouldReturnInvalidWhenObjectIsNull() {
        ValidationResult result = validatorEngine.validate(null);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("$object", result.getErrors().get(0).getField());
    }

    @Test
    void shouldValidateNotNull() {
        ValidationResult result = validatorEngine.validate(new NotNullSample(null));

        assertFalse(result.isValid());
        assertEquals("value", result.getErrors().get(0).getField());
    }

    @Test
    void shouldValidateNotBlank() {
        ValidationResult result = validatorEngine.validate(new NotBlankSample("   "));

        assertFalse(result.isValid());
        assertEquals("text", result.getErrors().get(0).getField());
    }

    @Test
    void shouldValidateSize() {
        ValidationResult result = validatorEngine.validate(new SizeSample("abc"));

        assertFalse(result.isValid());
        assertEquals("password", result.getErrors().get(0).getField());
    }

    @Test
    void shouldValidateMinAndMax() {
        ValidationResult result = validatorEngine.validate(new RangeSample(15));

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("number", result.getErrors().get(0).getField());

        ValidationResult tooHigh = validatorEngine.validate(new RangeSample(200));

        assertFalse(tooHigh.isValid());
        assertEquals(1, tooHigh.getErrors().size());
        assertEquals("number", tooHigh.getErrors().get(0).getField());
    }

    @Test
    void shouldValidateEmail() {
        ValidationResult result = validatorEngine.validate(new EmailSample("invalid"));

        assertFalse(result.isValid());
        assertEquals("email", result.getErrors().get(0).getField());
    }

    @Test
    void shouldValidatePattern() {
        ValidationResult result = validatorEngine.validate(new PatternSample("abc123"));

        assertFalse(result.isValid());
        assertEquals("code", result.getErrors().get(0).getField());
    }

    @Test
    void shouldAccumulateMultipleErrorsForSameObject() {
        ValidationResult result = validatorEngine.validate(new MixedSample("", "bad", "123", 10));

        assertFalse(result.isValid());
        assertEquals(4, result.getErrors().size());
    }

    @Test
    void shouldReturnValidForCorrectObject() {
        ValidationResult result = validatorEngine.validate(new MixedSample("William", "william@email.com", "12345678", 30));

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }

    private static class NotNullSample {
        @NotNull(message = "Value is required")
        private final String value;

        private NotNullSample(String value) {
            this.value = value;
        }
    }

    private static class NotBlankSample {
        @NotBlank(message = "Text is required")
        private final String text;

        private NotBlankSample(String text) {
            this.text = text;
        }
    }

    private static class SizeSample {
        @Size(min = 8, max = 20, message = "Invalid password size")
        private final String password;

        private SizeSample(String password) {
            this.password = password;
        }
    }

    private static class RangeSample {
        @Min(value = 18, message = "Too low")
        @Max(value = 120, message = "Too high")
        private final Integer number;

        private RangeSample(Integer number) {
            this.number = number;
        }
    }

    private static class EmailSample {
        @Email(message = "Invalid email")
        private final String email;

        private EmailSample(String email) {
            this.email = email;
        }
    }

    private static class PatternSample {
        @Pattern(regex = "[A-Z]{3}-\\d{4}", message = "Invalid code")
        private final String code;

        private PatternSample(String code) {
            this.code = code;
        }
    }

    private static class MixedSample {
        @NotBlank(message = "Name is required")
        private final String name;

        @Email(message = "Invalid email")
        private final String email;

        @Size(min = 8, max = 20, message = "Invalid password size")
        private final String password;

        @Min(value = 18, message = "Age must be at least 18")
        @Max(value = 100, message = "Age must be at most 100")
        private final Integer age;

        private MixedSample(String name, String email, String password, Integer age) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.age = age;
        }
    }
}

