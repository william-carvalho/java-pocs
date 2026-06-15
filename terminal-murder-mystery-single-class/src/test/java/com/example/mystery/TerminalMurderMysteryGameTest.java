package com.example.mystery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerminalMurderMysteryGameTest {
    @TempDir
    Path tempDir;

    @Test
    void initCreatesTerminalGameFilesAndDirectories() throws Exception {
        Path gameRoot = tempDir.resolve("game");

        new TerminalMurderMysteryGame().init(gameRoot);

        assertTrue(Files.exists(gameRoot.resolve("instructions.txt")));
        assertTrue(Files.exists(gameRoot.resolve("cheatsheet.txt")));
        assertTrue(Files.exists(gameRoot.resolve("case/suspects/alice_moreau.txt")));
        assertTrue(Files.exists(gameRoot.resolve("case/witnesses/maria_torres.txt")));
        assertTrue(Files.exists(gameRoot.resolve("case/locations/library.txt")));
        assertTrue(Files.exists(gameRoot.resolve("case/evidence/display_cabinet.txt")));
        assertTrue(Files.exists(gameRoot.resolve("case/logs/security_log.txt")));
        assertTrue(Files.exists(gameRoot.resolve("case/reports/autopsy.txt")));
        assertTrue(Files.exists(gameRoot.resolve("solution/solution.txt")));
    }

    @Test
    void instructionsTeachLinuxInvestigationCommands() throws Exception {
        Path gameRoot = tempDir.resolve("game");
        new TerminalMurderMysteryGame().init(gameRoot);

        String instructions = read(gameRoot.resolve("instructions.txt"));

        assertTrue(instructions.contains("ls case"));
        assertTrue(instructions.contains("find . -type f"));
        assertTrue(instructions.contains("grep -R"));
        assertTrue(instructions.contains("cat case/reports/autopsy.txt"));
    }

    @Test
    void generatedCaseContainsConsistentCluesForSingleSolution() throws Exception {
        Path gameRoot = tempDir.resolve("game");
        new TerminalMurderMysteryGame().init(gameRoot);

        String autopsy = read(gameRoot.resolve("case/reports/autopsy.txt"));
        String library = read(gameRoot.resolve("case/locations/library.txt"));
        String securityLog = read(gameRoot.resolve("case/logs/security_log.txt"));
        String witness = read(gameRoot.resolve("case/witnesses/maria_torres.txt"));

        assertTrue(autopsy.contains("20:55 and 21:00"));
        assertTrue(autopsy.contains("letter opener"));
        assertTrue(library.contains("missing one decorative letter opener"));
        assertTrue(securityLog.contains("20:58 Alice Moreau exits library corridor"));
        assertTrue(witness.contains("Alice leaving the library side door"));
    }

    @Test
    void initFailsWhenGameAlreadyExists() {
        Path gameRoot = tempDir.resolve("game");
        TerminalMurderMysteryGame game = new TerminalMurderMysteryGame();
        game.init(gameRoot);

        assertThrows(TerminalMurderMysteryGame.GameException.class, () -> game.init(gameRoot));
    }

    @Test
    void resetRecreatesGameAndRemovesExtraFiles() throws Exception {
        Path gameRoot = tempDir.resolve("game");
        TerminalMurderMysteryGame game = new TerminalMurderMysteryGame();
        game.init(gameRoot);
        Files.write(gameRoot.resolve("extra.txt"), "extra".getBytes(StandardCharsets.UTF_8));

        game.reset(gameRoot);

        assertFalse(Files.exists(gameRoot.resolve("extra.txt")));
        assertTrue(Files.exists(gameRoot.resolve("instructions.txt")));
    }

    @Test
    void solvesCorrectAnswerCaseInsensitively() {
        TerminalMurderMysteryGame game = new TerminalMurderMysteryGame();

        TerminalMurderMysteryGame.SolveResult result = game.solve(
                new TerminalMurderMysteryGame.Solution("alice moreau", "letter opener", "library"));

        assertTrue(result.isCorrect());
        assertTrue(result.getMessage().contains("Correct"));
    }

    @Test
    void rejectsWrongSolution() {
        TerminalMurderMysteryGame game = new TerminalMurderMysteryGame();

        TerminalMurderMysteryGame.SolveResult result = game.solve(
                new TerminalMurderMysteryGame.Solution("Ben Carter", "Wrench", "Garage"));

        assertFalse(result.isCorrect());
        assertTrue(result.getMessage().contains("Incorrect"));
    }

    @Test
    void revealContainsOfficialSolution() {
        String reveal = new TerminalMurderMysteryGame().reveal();

        assertTrue(reveal.contains("Killer: Alice Moreau"));
        assertTrue(reveal.contains("Weapon: Letter Opener"));
        assertTrue(reveal.contains("Location: Library"));
    }

    @Test
    void caseDataExposesFilesAndSolutionForTestsOrCli() {
        TerminalMurderMysteryGame.MysteryCase mysteryCase = new TerminalMurderMysteryGame().caseData();

        assertTrue(mysteryCase.getFiles().size() >= 10);
        assertEquals("Alice Moreau", mysteryCase.getSolution().getKiller());
        assertEquals("Letter Opener", mysteryCase.getSolution().getWeapon());
        assertEquals("Library", mysteryCase.getSolution().getLocation());
    }

    private static String read(Path path) throws Exception {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
