# Terminal Murder Mystery Single Class

Java 8 POC for a murder mystery game played in the terminal with Linux commands.

The production code is intentionally in one class:

```text
src/main/java/com/example/mystery/TerminalMurderMysteryGame.java
```

## Game Flow

1. Generate the case files.
2. Open the `game` directory.
3. Investigate with Linux commands.
4. Solve by naming the killer, weapon, and location.

Useful commands:

```bash
cd game
cat instructions.txt
ls case
find . -type f
grep -R "letter opener" .
cat case/reports/autopsy.txt
cat case/logs/security_log.txt
```

## CLI

```bash
mvn package
java -cp target/terminal-murder-mystery-single-class-0.0.1-SNAPSHOT.jar com.example.mystery.TerminalMurderMysteryGame init
java -cp target/terminal-murder-mystery-single-class-0.0.1-SNAPSHOT.jar com.example.mystery.TerminalMurderMysteryGame reset
java -cp target/terminal-murder-mystery-single-class-0.0.1-SNAPSHOT.jar com.example.mystery.TerminalMurderMysteryGame reveal
java -cp target/terminal-murder-mystery-single-class-0.0.1-SNAPSHOT.jar com.example.mystery.TerminalMurderMysteryGame solve --killer "Alice Moreau" --weapon "Letter Opener" --location "Library"
```

## Test

```bash
mvn test
```
