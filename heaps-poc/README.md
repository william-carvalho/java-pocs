# Heaps POC

POC em Java 8 que implementa um heap binario generico e mostra problemas
classicos resolvidos com heaps.

## Conteudo

- `BinaryHeap<T>` configuravel por `Comparator` como min-heap ou max-heap;
- insercao, consulta, extracao, remocao e substituicao da raiz;
- construcao por heapify em O(n);
- validacao da propriedade do heap;
- heap sort;
- selecao dos K maiores elementos usando O(k) memoria;
- mediana dinamica de um fluxo com dois heaps.

## Complexidade

| Operacao | Complexidade |
|---|---:|
| `peek` | O(1) |
| `offer`, `poll`, `replaceRoot` | O(log n) |
| `remove` por valor | O(n) |
| construcao com heapify | O(n) |
| heap sort | O(n log n) |
| Top-K | O(n log k) |
| inclusao na mediana dinamica | O(log n) |

O array interno fica em ordem por niveis, e nao totalmente ordenado. Somente a
raiz e os relacionamentos entre pais e filhos obedecem a prioridade.

## Executar

Requisitos: JDK 8+ e Maven 3.8+.

```bash
cd heaps-poc
mvn clean test
mvn package
java -cp target/classes com.example.heaps.HeapsPocApplication
```

## Exemplo

```java
BinaryHeap<Integer> minHeap = BinaryHeap.minHeap();
minHeap.offer(8);
minHeap.offer(2);
minHeap.offer(5);

int smallest = minHeap.poll(); // 2

List<Integer> top3 = HeapAlgorithms.topK(values, 3);
```
