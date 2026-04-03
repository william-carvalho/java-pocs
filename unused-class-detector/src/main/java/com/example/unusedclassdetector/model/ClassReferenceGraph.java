package com.example.unusedclassdetector.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ClassReferenceGraph {

    private final Map<String, Set<String>> outboundReferences;
    private final Map<String, Integer> inboundReferenceCounts;

    public ClassReferenceGraph(Map<String, Set<String>> outboundReferences, Map<String, Integer> inboundReferenceCounts) {
        this.outboundReferences = outboundReferences;
        this.inboundReferenceCounts = inboundReferenceCounts;
    }

    public Map<String, Set<String>> getOutboundReferences() {
        return Collections.unmodifiableMap(outboundReferences);
    }

    public Map<String, Integer> getInboundReferenceCounts() {
        return Collections.unmodifiableMap(inboundReferenceCounts);
    }
}

