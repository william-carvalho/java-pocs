package com.example.hibernateslowquerydetector.detector;

import java.util.ArrayList;
import java.util.List;

public final class QueryCaptureContext {

    private static final ThreadLocal<List<String>> SQLS = new ThreadLocal<List<String>>();

    private QueryCaptureContext() {
    }

    public static void start() {
        SQLS.set(new ArrayList<String>());
    }

    public static void add(String sql) {
        List<String> statements = SQLS.get();
        if (statements != null && sql != null) {
            statements.add(sql);
        }
    }

    public static List<String> getStatements() {
        List<String> statements = SQLS.get();
        if (statements == null) {
            return new ArrayList<String>();
        }
        return new ArrayList<String>(statements);
    }

    public static void clear() {
        SQLS.remove();
    }
}
