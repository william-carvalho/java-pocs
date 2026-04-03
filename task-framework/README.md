# Task Framework

POC simples em Java 8 para submissão e execução de tarefas em um pool próprio de threads, com status, resultado, falha, cancelamento e shutdown gracioso.

## Ideia

O framework encapsula um `ExecutorService` e adiciona uma camada simples de gerenciamento de tarefas com:

- `taskId` único
- status da execução
- timestamps
- resultado opcional
- erro opcional
- listagem e cancelamento

## Estrutura

```text
src/main/java/com/example/taskframework
├── core
├── demo
├── exception
├── executor
├── model
├── registry
├── task
└── util
```

## Componentes principais

- `TaskFramework`: fachada principal
- `DefaultTaskFramework`: implementação padrão
- `TaskExecutor`: abstração do executor interno
- `DefaultTaskExecutor`: implementação com `ExecutorService`
- `TaskRegistry`: armazena `TaskInfo` e `Future`
- `TaskWrapper`: atualiza status durante a execução
- `TaskInfo`: metadados da tarefa
- `TaskStatus`: estados da execução

## Status suportados

- `PENDING`
- `RUNNING`
- `COMPLETED`
- `FAILED`
- `CANCELLED`

## API principal

```java
TaskFramework framework = new DefaultTaskFramework(
    new DefaultTaskExecutor(4),
    new TaskRegistry(),
    new TaskIdGenerator()
);

String taskId1 = framework.submit(() -> {
    System.out.println("Executing runnable task");
}).getTaskId();

String taskId2 = framework.submit("sum-task", () -> 10 + 20).getTaskId();

TaskInfo info = framework.getTask(taskId2);
Object result = framework.getResult(taskId2);
```

## Comportamento da POC

- `Runnable` retorna `"Runnable task completed"` como resultado simples.
- `Callable<T>` guarda o valor retornado em `TaskInfo.result`.
- Falha em tarefa vira `FAILED` com `errorMessage`.
- Cancelamento usa o `Future.cancel(true)`.
- Após `shutdown`, novas submissões são rejeitadas.

## Como rodar a demo

```bash
mvn exec:java -Dexec.mainClass=com.example.taskframework.demo.TaskFrameworkDemo
```

Fluxo demonstrado:

1. inicializa framework com 4 threads
2. submete tarefa `Runnable`
3. submete tarefa `Callable` de soma
4. submete tarefa demorada com `sleep`
5. submete tarefa com falha proposital
6. imprime status periodicamente
7. imprime resultados e erro
8. faz shutdown

Exemplo de saída:

```text
Submitted task: task-1
Submitted task: task-2
Submitted task: task-3
Submitted task: task-4

Task task-1 status: COMPLETED
Task task-2 status: COMPLETED
Task task-3 status: RUNNING
Task task-4 status: FAILED
```

## Como rodar os testes

```bash
mvn test
```
