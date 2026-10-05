# Big O — exemplos simples em Java

Projeto Java 8 para quem esta comecando a estudar complexidade de algoritmos.

## O que e Big O?

Big O descreve **como a quantidade de trabalho cresce** quando a entrada aumenta.
Ele nao mede segundos: computadores diferentes levam tempos diferentes, mas a forma
de crescimento do algoritmo continua a mesma.

| Big O | Exemplo do projeto | Para 8 itens (pior caso) | Ideia |
|---|---|---:|---|
| `O(1)` | Pegar o primeiro item | 1 | O trabalho nao cresce |
| `O(log n)` | Busca binaria | 4 comparacoes | Descarta metade a cada passo |
| `O(n)` | Busca linear | 8 comparacoes | Visita cada item uma vez |
| `O(n log n)` | Merge sort | Depende da ordem | Divide e combina os itens |
| `O(n²)` | Comparar todos os pares | 28 comparacoes | Cada item encontra os demais |

Se a entrada dobrar de 8 para 16 itens, `O(n)` tende a dobrar. Ja `O(n²)` passa de
aproximadamente 64 para 256 unidades de trabalho. Em Big O ignoramos constantes e
detalhes menores para enxergar essa tendencia.

## Executar

Requisitos: JDK 8 ou mais recente e Maven.

```bash
mvn test
mvn compile exec:java
```

Se preferir executar sem o plugin do Maven:

```bash
mvn compile
java -cp target/classes com.example.bigo.BigOExamples
```

O programa imprime o resultado e a contagem das operacoes importantes. Abra
`BigOExamples.java` e altere os vetores para experimentar tamanhos diferentes.

## Roteiro sugerido para estudar

1. Execute o programa e compare as contagens.
2. Leia primeiro `first`, depois `linearSearch` e `countEqualPairs`.
3. Observe que dois lacos aninhados fazem o trabalho crescer muito mais depressa.
4. Leia `binarySearch` e acompanhe os limites `left`, `middle` e `right` no papel.
5. Por ultimo, explore `mergeSort`, que combina divisao e repeticao.

> Regra pratica: Big O normalmente descreve o pior caso. Um algoritmo `O(n)` ainda
> pode encontrar o primeiro elemento imediatamente, mas talvez precise visitar todos.
