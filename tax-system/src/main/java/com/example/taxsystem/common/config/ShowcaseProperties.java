package com.example.taxsystem.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "taxsystem")
public class ShowcaseProperties {

    private final Security security = new Security();
    private final Stress stress = new Stress();
    private final Samples samples = new Samples();

    public Security getSecurity() {
        return security;
    }

    public Stress getStress() {
        return stress;
    }

    public Samples getSamples() {
        return samples;
    }

    public static class Security {
        private String readerUser = "reader";
        private String readerPassword = "reader-secret";
        private String writerUser = "writer";
        private String writerPassword = "writer-secret";

        public String getReaderUser() {
            return readerUser;
        }

        public void setReaderUser(String readerUser) {
            this.readerUser = readerUser;
        }

        public String getReaderPassword() {
            return readerPassword;
        }

        public void setReaderPassword(String readerPassword) {
            this.readerPassword = readerPassword;
        }

        public String getWriterUser() {
            return writerUser;
        }

        public void setWriterUser(String writerUser) {
            this.writerUser = writerUser;
        }

        public String getWriterPassword() {
            return writerPassword;
        }

        public void setWriterPassword(String writerPassword) {
            this.writerPassword = writerPassword;
        }
    }

    public static class Stress {
        private int defaultTimeoutMillis = 2000;
        private int maxConcurrency = 32;

        public int getDefaultTimeoutMillis() {
            return defaultTimeoutMillis;
        }

        public void setDefaultTimeoutMillis(int defaultTimeoutMillis) {
            this.defaultTimeoutMillis = defaultTimeoutMillis;
        }

        public int getMaxConcurrency() {
            return maxConcurrency;
        }

        public void setMaxConcurrency(int maxConcurrency) {
            this.maxConcurrency = maxConcurrency;
        }
    }

    public static class Samples {
        private String propertiesLocation = "classpath:sample-taxsystem.properties";

        public String getPropertiesLocation() {
            return propertiesLocation;
        }

        public void setPropertiesLocation(String propertiesLocation) {
            this.propertiesLocation = propertiesLocation;
        }
    }
}
