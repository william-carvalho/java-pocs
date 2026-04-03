package com.example.customstring.util;

public final class JsonEscapeUtil {

    private JsonEscapeUtil() {
    }

    public static String escape(char[] value) {
        StringBuilder builder = new StringBuilder();
        builder.append('"');
        for (char current : value) {
            switch (current) {
                case '"':
                    builder.append("\\\"");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    builder.append(current);
            }
        }
        builder.append('"');
        return builder.toString();
    }
}

