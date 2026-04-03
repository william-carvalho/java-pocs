package com.example.taxsystem.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PercentileCalculator {

    private PercentileCalculator() {
    }

    public static long percentile(List<Long> values, double percentile) {
        if (values == null || values.isEmpty()) {
            return 0L;
        }
        List<Long> sorted = new ArrayList<Long>(values);
        Collections.sort(sorted);
        int index = (int) Math.ceil(percentile / 100.0d * sorted.size()) - 1;
        index = Math.min(Math.max(index, 0), sorted.size() - 1);
        return sorted.get(index);
    }
}
