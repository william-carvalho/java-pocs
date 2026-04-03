# Terminal Murder Mystery Game

POC em Java 8 para um jogo investigativo jogado pelo terminal, inspirado por experiencias como The Command Line Murders. A aplicacao gera um caso fixo em diretorios e arquivos, e o jogador resolve o misterio explorando o filesystem com comandos Linux.

## O que o projeto entrega

- CLI simples com `init`, `reset`, `solve` e `reveal`
- geracao real de diretorios e arquivos do caso
- narrativa investigativa fixa e consistente
- suspeitos, testemunhas, locais, evidencias, logs e relatorios
- hints opcionais
- validador de resposta final

## Estrutura do projeto

```text
src/main/java/com/example/terminalmurdermystery
|-- core
|-- demo
|-- exception
|-- generator
|-- model
|-- validator
`-- writer
```

## Como compilar

```bash
mvn clean package
```

## Como iniciar o jogo

```bash
java -jar target/terminal-murder-mystery-game-1.0.0-SNAPSHOT.jar init
```

Saida esperada:

```text
Game created successfully at: .../game
Start by reading:
cat game/instructions.txt
```

## Fluxo de jogo

1. gerar o caso com `init`
2. entrar na pasta `game`
3. ler `instructions.txt`
4. investigar usando comandos Linux
5. abrir hints se travar
6. validar a solucao com `solve`

## Comandos disponiveis

### init

Cria a pasta `game` com toda a estrutura do caso.

```bash
java -jar target/terminal-murder-mystery-game-1.0.0-SNAPSHOT.jar init
```

### reset

Apaga e recria a pasta `game`.

```bash
java -jar target/terminal-murder-mystery-game-1.0.0-SNAPSHOT.jar reset
```

### solve

Valida a resposta final:

```bash
java -jar target/terminal-murder-mystery-game-1.0.0-SNAPSHOT.jar solve --killer "Alice Moreau" --weapon "Letter Opener" --location "Library"
```

### reveal

Mostra a solucao oficial:

```bash
java -jar target/terminal-murder-mystery-game-1.0.0-SNAPSHOT.jar reveal
```

## Estrutura gerada

```text
game/
|-- instructions.txt
|-- cheatsheet.txt
|-- hint1.txt
|-- hint2.txt
|-- hint3.txt
|-- case/
|   |-- suspects/
|   |-- witnesses/
|   |-- locations/
|   |-- evidence/
|   |-- logs/
|   `-- reports/
`-- solution/
    `-- solution.txt
```

## Exemplos reais de investigacao no Linux

```bash
cd game
cat instructions.txt
ls case
find . -type f
grep -R "library" .
cat case/suspects/alice_moreau.txt
cat case/witnesses/maria_torres.txt
cat case/reports/autopsy.txt
cat case/logs/security_log.txt
```

## Caso inicial

O misterio fixo gira em torno da morte de Victor Hales dentro de uma mansao privada.

Voce precisa descobrir:

- quem matou Victor Hales
- qual arma foi usada
- em qual local o crime aconteceu

O caso foi escrito para ter uma solucao unica e pistas suficientes, com algumas distracoes leves.

## Exemplo de arquivos gerados

`case/suspects/alice_moreau.txt`

```text
Name: Alice Moreau
Role: Estate Manager
Alibi: Claims she was checking inventory records in the library but left before 20:50.
Notes: During questioning she kept insisting Victor had no enemies inside the house.
```

`case/reports/autopsy.txt`

```text
Victim: Victor Hales
Estimated time of death: between 20:55 and 21:00
Cause: single stab wound from a narrow blade
Probable weapon: decorative letter opener or similar object
```

## Limitacoes da POC

- caso fixo, sem geracao procedural complexa
- sem parser de comandos naturais
- sem multiplayer
- sem UI grafica
- a investigacao depende do jogador usar comandos do terminal

## Testes

```bash
mvn test
```

Os testes cobrem:

- criacao da estrutura de diretorios e arquivos
- conteudo dos arquivos essenciais
- validacao da solucao
- consistencia narrativa
- `init` e `reset`
