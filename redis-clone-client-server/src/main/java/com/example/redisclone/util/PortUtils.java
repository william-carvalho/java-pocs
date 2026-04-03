package com.example.redisclone.util;

import com.example.redisclone.protocol.ProtocolConstants;

public final class PortUtils {

    private PortUtils() {
    }

    public static int parsePort(String[] args, int index, int defaultPort) {
        if (args == null || args.length <= index) {
            return defaultPort;
        }

        try {
            int port = Integer.parseInt(args[index]);
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535");
            }
            return port;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid port: " + args[index], ex);
        }
    }

    public static String parseHost(String[] args, int index, String defaultHost) {
        if (args == null || args.length <= index || args[index].trim().isEmpty()) {
            return defaultHost;
        }
        return args[index].trim();
    }
}

