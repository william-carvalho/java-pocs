package com.example.terminalmurdermystery;

import com.example.terminalmurdermystery.core.GameSetupService;
import com.example.terminalmurdermystery.generator.CaseGenerator;
import com.example.terminalmurdermystery.model.CaseSolution;
import com.example.terminalmurdermystery.model.MysteryCase;
import com.example.terminalmurdermystery.validator.AnswerValidator;
import com.example.terminalmurdermystery.writer.FileStructureWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TerminalMurderMysteryGameTest {

    @Test
    void shouldGenerateCaseStructureCorrectly() throws Exception {
        MysteryCase mysteryCase = new CaseGenerator().generateFixedCase();
        Path root = Files.createTempDirectory("mystery-game");

        new FileStructureWriter().writeCase(root, mysteryCase);

        assertTrue(Files.exists(root.resolve("instructions.txt")));
        assertTrue(Files.exists(root.resolve("cheatsheet.txt")));
        assertTrue(Files.exists(root.resolve("hint1.txt")));
        assertTrue(Files.exists(root.resolve("case").resolve("suspects").resolve("alice_moreau.txt")));
        assertTrue(Files.exists(root.resolve("case").resolve("witnesses").resolve("maria_torres.txt")));
        assertTrue(Files.exists(root.resolve("case").resolve("evidence").resolve("fingerprint_report.txt")));
        assertTrue(Files.exists(root.resolve("case").resolve("reports").resolve("autopsy.txt")));
        assertTrue(Files.exists(root.resolve("solution").resolve("solution.txt")));
    }

    @Test
    void shouldGenerateEssentialFilesWithExpectedContent() throws Exception {
        MysteryCase mysteryCase = new CaseGenerator().generateFixedCase();
        Path root = Files.createTempDirectory("mystery-content");

        new FileStructureWriter().writeCase(root, mysteryCase);

        String instructions = new String(Files.readAllBytes(root.resolve("instructions.txt")), StandardCharsets.UTF_8);
        String autopsy = new String(Files.readAllBytes(root.resolve("case").resolve("reports").resolve("autopsy.txt")), StandardCharsets.UTF_8);

        assertTrue(instructions.contains("Your objective"));
        assertTrue(instructions.contains("solve --killer"));
        assertTrue(autopsy.contains("Probable weapon: decorative letter opener or similar object"));
    }

    @Test
    void shouldValidateCorrectSolution() {
        CaseSolution solution = new CaseGenerator().generateFixedCase().getSolution();
        AnswerValidator validator = new AnswerValidator();

        assertTrue(validator.isCorrect(solution, "Alice Moreau", "Letter Opener", "Library"));
        assertFalse(validator.isCorrect(solution, "Bruno Costa", "Knife", "Kitchen"));
    }

    @Test
    void shouldKeepCaseConsistent() {
        MysteryCase mysteryCase = new CaseGenerator().generateFixedCase();

        assertTrue(mysteryCase.getSummary().contains("Victor Hales"));
        assertTrue(mysteryCase.getEvidence().stream().anyMatch(evidence ->
                evidence.getClueText().contains("Alice Moreau")));
        assertTrue(mysteryCase.getReports().get("autopsy.txt").contains("letter opener"));
        assertTrue(mysteryCase.getLogs().get("security_log.txt").contains("Alice Moreau seen leaving the library alone"));
    }

    @Test
    void shouldInitAndResetGameInExpectedLocation(@TempDir Path tempDir) {
        GameSetupService gameSetupService = new GameSetupService(
                new CaseGenerator(),
                new FileStructureWriter(),
                tempDir.resolve("game")
        );

        Path initPath = gameSetupService.init();
        assertTrue(Files.exists(initPath.resolve("instructions.txt")));

        Path resetPath = gameSetupService.reset();
        assertTrue(Files.exists(resetPath.resolve("instructions.txt")));
    }
}
