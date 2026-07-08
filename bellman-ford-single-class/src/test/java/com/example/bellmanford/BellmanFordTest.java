package com.example.bellmanford;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BellmanFordTest {
    @Test
    void calculatesShortestPathsWithPositiveWeights() {
        List<BellmanFord.Edge> edges = Arrays.asList(
                BellmanFord.edge(0, 1, 4),
                BellmanFord.edge(0, 2, 2),
                BellmanFord.edge(2, 1, 1),
                BellmanFord.edge(1, 3, 1),
                BellmanFord.edge(2, 3, 5)
        );

        BellmanFord.Result result = BellmanFord.shortestPaths(4, edges, 0);

        assertEquals(0, result.distanceTo(0));
        assertEquals(3, result.distanceTo(1));
        assertEquals(2, result.distanceTo(2));
        assertEquals(4, result.distanceTo(3));
        assertEquals(Arrays.asList(0, 2, 1, 3), result.pathTo(3));
    }

    @Test
    void supportsNegativeEdgesWithoutNegativeCycle() {
        List<BellmanFord.Edge> edges = Arrays.asList(
                BellmanFord.edge(0, 1, 6),
                BellmanFord.edge(0, 2, 7),
                BellmanFord.edge(1, 2, 8),
                BellmanFord.edge(1, 3, 5),
                BellmanFord.edge(1, 4, -4),
                BellmanFord.edge(2, 3, -3),
                BellmanFord.edge(2, 4, 9),
                BellmanFord.edge(3, 1, -2),
                BellmanFord.edge(4, 0, 2),
                BellmanFord.edge(4, 3, 7)
        );

        BellmanFord.Result result = BellmanFord.shortestPaths(5, edges, 0);

        assertArrayEquals(new int[]{0, 2, 7, 4, -2}, result.distances());
        assertEquals(Arrays.asList(0, 2, 3, 1, 4), result.pathTo(4));
    }

    @Test
    void marksUnreachableVertices() {
        List<BellmanFord.Edge> edges = Collections.singletonList(BellmanFord.edge(0, 1, 3));

        BellmanFord.Result result = BellmanFord.shortestPaths(4, edges, 0);

        assertTrue(result.isReachable(1));
        assertFalse(result.isReachable(2));
        assertEquals(BellmanFord.INF, result.distanceTo(2));
        assertEquals(Collections.emptyList(), result.pathTo(2));
    }

    @Test
    void detectsNegativeCycleReachableFromSource() {
        List<BellmanFord.Edge> edges = Arrays.asList(
                BellmanFord.edge(0, 1, 1),
                BellmanFord.edge(1, 2, -1),
                BellmanFord.edge(2, 1, -1)
        );

        assertThrows(BellmanFord.NegativeCycleException.class, () -> BellmanFord.shortestPaths(3, edges, 0));
    }

    @Test
    void ignoresNegativeCycleNotReachableFromSource() {
        List<BellmanFord.Edge> edges = Arrays.asList(
                BellmanFord.edge(0, 1, 1),
                BellmanFord.edge(2, 3, -1),
                BellmanFord.edge(3, 2, -1)
        );

        BellmanFord.Result result = BellmanFord.shortestPaths(4, edges, 0);

        assertEquals(1, result.distanceTo(1));
        assertFalse(result.isReachable(2));
        assertFalse(result.isReachable(3));
    }

    @Test
    void handlesSingleVertexGraph() {
        BellmanFord.Result result = BellmanFord.shortestPaths(1, Collections.<BellmanFord.Edge>emptyList(), 0);

        assertEquals(0, result.distanceTo(0));
        assertEquals(Collections.singletonList(0), result.pathTo(0));
        assertEquals(-1, result.predecessorOf(0));
    }

    @Test
    void exposesDefensiveCopies() {
        BellmanFord.Result result = BellmanFord.shortestPaths(
                2,
                Collections.singletonList(BellmanFord.edge(0, 1, 5)),
                0
        );

        int[] distances = result.distances();
        int[] predecessors = result.predecessors();
        distances[1] = 99;
        predecessors[1] = 99;

        assertEquals(5, result.distanceTo(1));
        assertEquals(0, result.predecessorOf(1));
    }

    @Test
    void exposesEdgeData() {
        BellmanFord.Edge edge = BellmanFord.edge(1, 2, -3);

        assertEquals(1, edge.from());
        assertEquals(2, edge.to());
        assertEquals(-3, edge.weight());
    }

    @Test
    void validatesGraphInputs() {
        assertThrows(IllegalArgumentException.class, () -> BellmanFord.shortestPaths(0, Collections.<BellmanFord.Edge>emptyList(), 0));
        assertThrows(IllegalArgumentException.class, () -> BellmanFord.shortestPaths(2, null, 0));
        assertThrows(IllegalArgumentException.class, () -> BellmanFord.shortestPaths(2, Collections.<BellmanFord.Edge>emptyList(), -1));
        assertThrows(IllegalArgumentException.class, () -> BellmanFord.shortestPaths(2, Collections.<BellmanFord.Edge>emptyList(), 2));
        assertThrows(IllegalArgumentException.class, () -> BellmanFord.shortestPaths(2, Collections.singletonList(null), 0));
        assertThrows(IllegalArgumentException.class, () -> BellmanFord.shortestPaths(2, Collections.singletonList(BellmanFord.edge(0, 2, 1)), 0));
    }

    @Test
    void validatesResultVertexAccess() {
        BellmanFord.Result result = BellmanFord.shortestPaths(1, Collections.<BellmanFord.Edge>emptyList(), 0);

        assertThrows(IllegalArgumentException.class, () -> result.distanceTo(-1));
        assertThrows(IllegalArgumentException.class, () -> result.distanceTo(1));
        assertThrows(IllegalArgumentException.class, () -> result.pathTo(1));
        assertThrows(IllegalArgumentException.class, () -> result.predecessorOf(1));
    }
}
