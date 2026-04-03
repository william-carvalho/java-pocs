package com.example.hibernateslowquerydetector.detector;

import org.hibernate.resource.jdbc.spi.StatementInspector;

public class QueryStatementInspector implements StatementInspector {

    private static final long serialVersionUID = 1L;

    @Override
    public String inspect(String sql) {
        QueryCaptureContext.add(sql);
        return sql;
    }
}
