# Bellman-Ford Single Class

Java 8 POC for the Bellman-Ford shortest path algorithm.

The production code is intentionally in one class:

```text
src/main/java/com/example/bellmanford/BellmanFord.java
```

## Features

- Directed weighted edges.
- Shortest paths from one source.
- Supports negative edge weights.
- Detects reachable negative cycles.
- Tracks predecessors for path reconstruction.
- Reports unreachable vertices.
- Returns defensive copies for distances and predecessors.

## Example

```java
List<BellmanFord.Edge> edges = Arrays.asList(
        BellmanFord.edge(0, 1, 4),
        BellmanFord.edge(0, 2, 2),
        BellmanFord.edge(2, 1, 1)
);

BellmanFord.Result result = BellmanFord.shortestPaths(3, edges, 0);
int distance = result.distanceTo(1);
List<Integer> path = result.pathTo(1);
```

## Test

```bash
mvn test
```
