package com.example.filesharesystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class StorageProperties {

    private final Storage storage = new Storage();
    private final Encryption encryption = new Encryption();

    public Storage getStorage() {
        return storage;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public static class Storage {
        private String path = "./storage";

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class Encryption {
        private String key = "1234567890123456";

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}

