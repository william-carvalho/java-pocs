package com.example.terminalmurdermystery.core;

import com.example.terminalmurdermystery.exception.GameException;
import com.example.terminalmurdermystery.generator.CaseGenerator;
import com.example.terminalmurdermystery.model.CaseSolution;
import com.example.terminalmurdermystery.model.MysteryCase;
import com.example.terminalmurdermystery.writer.FileStructureWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class GameSetupService {

    private final CaseGenerator caseGenerator;
    private final FileStructureWriter fileStructureWriter;
    private final Path gameRoot;

    public GameSetupService() {
        this(new CaseGenerator(), new FileStructureWriter(), Paths.get("game"));
    }

    public GameSetupService(CaseGenerator caseGenerator, FileStructureWriter fileStructureWriter) {
        this(caseGenerator, fileStructureWriter, Paths.get("game"));
    }

    public GameSetupService(CaseGenerator caseGenerator, FileStructureWriter fileStructureWriter, Path gameRoot) {
        this.caseGenerator = caseGenerator;
        this.fileStructureWriter = fileStructureWriter;
        this.gameRoot = gameRoot;
    }

    public Path init() {
        MysteryCase mysteryCase = caseGenerator.generateFixedCase();
        return fileStructureWriter.writeCase(gameRoot, mysteryCase);
    }

    public Path reset() {
        deleteRecursively(gameRoot);
        return init();
    }

    public CaseSolution revealSolution() {
        return caseGenerator.generateFixedCase().getSolution();
    }

    public MysteryCase getCase() {
        return caseGenerator.generateFixedCase();
    }

    private void deleteRecursively(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .forEach(current -> {
                        try {
                            Files.deleteIfExists(current);
                        } catch (IOException exception) {
                            throw new GameException("Could not delete " + current, exception);
                        }
                    });
        } catch (IOException exception) {
            throw new GameException("Could not reset game directory: " + path, exception);
        }
    }
}
