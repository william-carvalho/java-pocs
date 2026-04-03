package com.example.terminalmurdermystery.writer;

import com.example.terminalmurdermystery.exception.GameException;
import com.example.terminalmurdermystery.model.Evidence;
import com.example.terminalmurdermystery.model.Location;
import com.example.terminalmurdermystery.model.MysteryCase;
import com.example.terminalmurdermystery.model.Suspect;
import com.example.terminalmurdermystery.model.Witness;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class FileStructureWriter {

    public Path writeCase(Path gameRoot, MysteryCase mysteryCase) {
        try {
            Files.createDirectories(gameRoot);
            Files.createDirectories(gameRoot.resolve("case").resolve("suspects"));
            Files.createDirectories(gameRoot.resolve("case").resolve("witnesses"));
            Files.createDirectories(gameRoot.resolve("case").resolve("locations"));
            Files.createDirectories(gameRoot.resolve("case").resolve("evidence"));
            Files.createDirectories(gameRoot.resolve("case").resolve("logs"));
            Files.createDirectories(gameRoot.resolve("case").resolve("reports"));
            Files.createDirectories(gameRoot.resolve("solution"));

            write(gameRoot.resolve("instructions.txt"), buildInstructions(mysteryCase));
            write(gameRoot.resolve("cheatsheet.txt"), buildCheatsheet());
            write(gameRoot.resolve("hint1.txt"), mysteryCase.getHints().get(0));
            write(gameRoot.resolve("hint2.txt"), mysteryCase.getHints().get(1));
            write(gameRoot.resolve("hint3.txt"), mysteryCase.getHints().get(2));

            for (Suspect suspect : mysteryCase.getSuspects()) {
                write(gameRoot.resolve("case").resolve("suspects").resolve(fileName(suspect.getName())),
                        "Name: " + suspect.getName() + "\n" +
                                "Role: " + suspect.getRole() + "\n" +
                                "Alibi: " + suspect.getAlibi() + "\n" +
                                "Notes: " + suspect.getNotes() + "\n");
            }

            for (Witness witness : mysteryCase.getWitnesses()) {
                write(gameRoot.resolve("case").resolve("witnesses").resolve(fileName(witness.getName())),
                        "Witness: " + witness.getName() + "\n" +
                                "Statement: " + witness.getStatement() + "\n");
            }

            for (Location location : mysteryCase.getLocations()) {
                write(gameRoot.resolve("case").resolve("locations").resolve(fileName(location.getName())),
                        "Location: " + location.getName() + "\n" +
                                "Description: " + location.getDescription() + "\n");
            }

            for (Evidence evidence : mysteryCase.getEvidence()) {
                write(gameRoot.resolve("case").resolve("evidence").resolve(evidence.getFileName()),
                        "Evidence: " + evidence.getDescription() + "\n" +
                                "Clue: " + evidence.getClueText() + "\n");
            }

            for (Map.Entry<String, String> log : mysteryCase.getLogs().entrySet()) {
                write(gameRoot.resolve("case").resolve("logs").resolve(log.getKey()), log.getValue());
            }

            for (Map.Entry<String, String> report : mysteryCase.getReports().entrySet()) {
                write(gameRoot.resolve("case").resolve("reports").resolve(report.getKey()), report.getValue());
            }

            write(gameRoot.resolve("solution").resolve("solution.txt"),
                    "Killer: " + mysteryCase.getSolution().getKillerName() + "\n" +
                            "Weapon: " + mysteryCase.getSolution().getWeapon() + "\n" +
                            "Location: " + mysteryCase.getSolution().getLocation() + "\n");

            return gameRoot;
        } catch (IOException exception) {
            throw new GameException("Could not write game files to " + gameRoot, exception);
        }
    }

    private void write(Path path, String content) throws IOException {
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
    }

    private String fileName(String value) {
        return value.toLowerCase().replace(" ", "_") + ".txt";
    }

    private String buildInstructions(MysteryCase mysteryCase) {
        return mysteryCase.getTitle() + "\n" +
                "============================\n\n" +
                mysteryCase.getSummary() + "\n\n" +
                "Your objective:\n" +
                "- discover the killer\n" +
                "- discover the weapon\n" +
                "- discover the crime location\n\n" +
                "Start by exploring:\n" +
                "- case/suspects\n" +
                "- case/witnesses\n" +
                "- case/evidence\n" +
                "- case/logs\n" +
                "- case/reports\n\n" +
                "Use Linux commands such as:\n" +
                "- ls\n" +
                "- cat\n" +
                "- grep -R\n" +
                "- find\n" +
                "- less\n" +
                "- head\n" +
                "- tail\n\n" +
                "If you get stuck, open hint1.txt, hint2.txt or hint3.txt.\n\n" +
                "When you think you solved the case, run:\n" +
                "java -jar target/terminal-murder-mystery-game.jar solve --killer \"NAME\" --weapon \"WEAPON\" --location \"LOCATION\"\n\n" +
                "You can reveal the official answer with:\n" +
                "java -jar target/terminal-murder-mystery-game.jar reveal\n";
    }

    private String buildCheatsheet() {
        return "Useful Linux commands\n" +
                "---------------------\n" +
                "ls\n" +
                "pwd\n" +
                "cat FILE\n" +
                "less FILE\n" +
                "find . -type f\n" +
                "grep -R \"keyword\" .\n" +
                "head FILE\n" +
                "tail FILE\n" +
                "wc -l FILE\n";
    }
}

