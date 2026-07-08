package com.example.bellmanford;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class BellmanFord {
    public static final int INF = Integer.MAX_VALUE / 4;

    private BellmanFord() {
    }

    public static Result shortestPaths(int vertices, List<Edge> edges, int source) {
        validateGraph(vertices, edges, source);

        int[] distances = new int[vertices];
        int[] predecessors = new int[vertices];
        for (int vertex = 0; vertex < vertices; vertex++) {
            distances[vertex] = INF;
            predecessors[vertex] = -1;
        }
        distances[source] = 0;

        for (int pass = 1; pass < vertices; pass++) {
            boolean changed = false;
            for (Edge edge : edges) {
                if (distances[edge.from] == INF) {
                    continue;
                }
                int candidate = distances[edge.from] + edge.weight;
                if (candidate < distances[edge.to]) {
                    distances[edge.to] = candidate;
                    predecessors[edge.to] = edge.from;
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }

        for (Edge edge : edges) {
            if (distances[edge.from] != INF && distances[edge.from] + edge.weight < distances[edge.to]) {
                throw new NegativeCycleException("negative cycle detected");
            }
        }

        return new Result(source, distances, predecessors);
    }

    public static Edge edge(int from, int to, int weight) {
        return new Edge(from, to, weight);
    }

    private static void validateGraph(int vertices, List<Edge> edges, int source) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("vertices must be greater than zero");
        }
        if (source < 0 || source >= vertices) {
            throw new IllegalArgumentException("source is outside the graph");
        }
        if (edges == null) {
            throw new IllegalArgumentException("edges is required");
        }
        for (Edge edge : edges) {
            if (edge == null) {
                throw new IllegalArgumentException("edge must not be null");
            }
            if (edge.from < 0 || edge.from >= vertices || edge.to < 0 || edge.to >= vertices) {
                throw new IllegalArgumentException("edge endpoint is outside the graph");
            }
        }
    }

    public static final class Edge {
        private final int from;
        private final int to;
        private final int weight;

        private Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public int weight() {
            return weight;
        }
    }

    public static final class Result {
        private final int source;
        private final int[] distances;
        private final int[] predecessors;

        private Result(int source, int[] distances, int[] predecessors) {
            this.source = source;
            this.distances = copy(distances);
            this.predecessors = copy(predecessors);
        }

        public int source() {
            return source;
        }

        public int distanceTo(int vertex) {
            validateVertex(vertex);
            return distances[vertex];
        }

        public boolean isReachable(int vertex) {
            validateVertex(vertex);
            return distances[vertex] != INF;
        }

        public int predecessorOf(int vertex) {
            validateVertex(vertex);
            return predecessors[vertex];
        }

        public List<Integer> pathTo(int vertex) {
            validateVertex(vertex);
            if (!isReachable(vertex)) {
                return Collections.emptyList();
            }

            List<Integer> path = new ArrayList<Integer>();
            int current = vertex;
            while (current != -1) {
                path.add(current);
                current = predecessors[current];
            }
            Collections.reverse(path);
            return path;
        }

        public int[] distances() {
            return copy(distances);
        }

        public int[] predecessors() {
            return copy(predecessors);
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= distances.length) {
                throw new IllegalArgumentException("vertex is outside the graph");
            }
        }

        private static int[] copy(int[] source) {
            int[] copy = new int[source.length];
            for (int index = 0; index < source.length; index++) {
                copy[index] = source[index];
            }
            return copy;
        }
    }

    public static final class NegativeCycleException extends RuntimeException {
        public NegativeCycleException(String message) {
            super(message);
        }
    }
}
