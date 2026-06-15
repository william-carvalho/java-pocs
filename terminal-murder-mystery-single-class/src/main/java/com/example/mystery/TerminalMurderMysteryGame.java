package com.example.mystery;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TerminalMurderMysteryGame {
    private static final Solution SOLUTION = new Solution("Alice Moreau", "Letter Opener", "Library");

    public static void main(String[] args) {
        TerminalMurderMysteryGame game = new TerminalMurderMysteryGame();
        Path gameRoot = Paths.get("game");
        String command = args.length == 0 ? "init" : args[0];
        try {
            if ("init".equals(command)) {
                game.init(gameRoot);
                System.out.println("Game created successfully at: " + gameRoot.toAbsolutePath());
                System.out.println("Start by reading: cat game/instructions.txt");
            } else if ("reset".equals(command)) {
                game.reset(gameRoot);
                System.out.println("Game reset successfully at: " + gameRoot.toAbsolutePath());
            } else if ("reveal".equals(command)) {
                System.out.println(game.reveal());
            } else if ("solve".equals(command)) {
                Solution answer = parseSolution(args);
                System.out.println(game.solve(answer).getMessage());
            } else {
                System.out.println("Usage: init | reset | reveal | solve --killer NAME --weapon WEAPON --location LOCATION");
            }
        } catch (RuntimeException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void init(Path gameRoot) {
        if (Files.exists(gameRoot)) {
            throw new GameException("Game already exists: " + gameRoot);
        }
        writeGame(gameRoot);
    }

    public void reset(Path gameRoot) {
        deleteRecursively(gameRoot);
        writeGame(gameRoot);
    }

    public SolveResult solve(Solution answer) {
        if (answer == null) {
            throw new IllegalArgumentException("answer is required");
        }
        boolean correct = matches(answer.getKiller(), SOLUTION.getKiller())
                && matches(answer.getWeapon(), SOLUTION.getWeapon())
                && matches(answer.getLocation(), SOLUTION.getLocation());
        return new SolveResult(correct, correct
                ? "Correct. Alice Moreau killed Victor Hales with the Letter Opener in the Library."
                : "Incorrect. Keep investigating with grep, find, cat, and ls.");
    }

    public String reveal() {
        return "Killer: " + SOLUTION.getKiller()
                + "\nWeapon: " + SOLUTION.getWeapon()
                + "\nLocation: " + SOLUTION.getLocation()
                + "\nMotive: Victor discovered Alice was selling estate artifacts.";
    }

    public MysteryCase caseData() {
        List<FileEntry> files = new ArrayList<FileEntry>();
        files.add(new FileEntry("instructions.txt",
                "Terminal Murder Mystery\n"
                        + "Victim: Victor Hales\n"
                        + "Goal: identify killer, weapon, and location.\n\n"
                        + "Use Linux commands to investigate:\n"
                        + "ls case\n"
                        + "find . -type f\n"
                        + "grep -R \"letter opener\" .\n"
                        + "cat case/reports/autopsy.txt\n"));
        files.add(new FileEntry("cheatsheet.txt",
                "Useful Linux commands:\n"
                        + "ls -R\n"
                        + "find . -type f\n"
                        + "cat file.txt\n"
                        + "grep -R \"word\" .\n"
                        + "sort file.txt\n"));
        files.add(new FileEntry("hint1.txt", "Start with the autopsy and security log.\n"));
        files.add(new FileEntry("hint2.txt", "Compare alibis against the time of death.\n"));
        files.add(new FileEntry("hint3.txt", "The missing item in the library display matters.\n"));
        files.add(new FileEntry("case/suspects/alice_moreau.txt",
                "Name: Alice Moreau\n"
                        + "Role: Estate Manager\n"
                        + "Alibi: Claims she was checking inventory records in the library but left before 20:50.\n"
                        + "Notes: Victor accused her of selling estate artifacts.\n"));
        files.add(new FileEntry("case/suspects/ben_carter.txt",
                "Name: Ben Carter\n"
                        + "Role: Driver\n"
                        + "Alibi: Seen washing the car near the garage from 20:40 to 21:10.\n"
                        + "Notes: Had no access to the library cabinet keys.\n"));
        files.add(new FileEntry("case/suspects/clara_voss.txt",
                "Name: Clara Voss\n"
                        + "Role: Art Dealer\n"
                        + "Alibi: Argued with Victor at dinner, then called a client from the conservatory.\n"
                        + "Notes: Wanted to buy the letter opener collection legally.\n"));
        files.add(new FileEntry("case/witnesses/maria_torres.txt",
                "Witness: Maria Torres\n"
                        + "Statement: At 20:58 I saw Alice leaving the library side door. She looked pale and held a cloth bundle.\n"));
        files.add(new FileEntry("case/witnesses/owen_price.txt",
                "Witness: Owen Price\n"
                        + "Statement: The library lights flickered at 20:56. I heard a short cry seconds later.\n"));
        files.add(new FileEntry("case/locations/library.txt",
                "Location: Library\n"
                        + "Findings: Blood drops near the writing desk. Display cabinet missing one decorative letter opener.\n"));
        files.add(new FileEntry("case/locations/garage.txt",
                "Location: Garage\n"
                        + "Findings: Wet floor, clean tools, no blood traces.\n"));
        files.add(new FileEntry("case/evidence/display_cabinet.txt",
                "Evidence: Library display cabinet\n"
                        + "Inventory: Three antique letter openers listed. Only two remain.\n"));
        files.add(new FileEntry("case/evidence/cloth_bundle.txt",
                "Evidence: Cloth bundle\n"
                        + "Found: laundry chute behind Alice Moreau's office.\n"
                        + "Contents: polished handle matching the missing letter opener set.\n"));
        files.add(new FileEntry("case/logs/security_log.txt",
                "20:44 Ben Carter enters garage\n"
                        + "20:51 Clara Voss enters conservatory\n"
                        + "20:56 Library side door opened from inside\n"
                        + "20:58 Alice Moreau exits library corridor\n"
                        + "21:03 Maria Torres calls for help\n"));
        files.add(new FileEntry("case/reports/autopsy.txt",
                "Victim: Victor Hales\n"
                        + "Estimated time of death: between 20:55 and 21:00\n"
                        + "Cause: single stab wound from a narrow blade\n"
                        + "Probable weapon: decorative letter opener or similar object\n"));
        files.add(new FileEntry("solution/solution.txt",
                reveal() + "\n"
                        + "Reasoning: autopsy identifies a narrow blade, library evidence shows a missing letter opener, "
                        + "security logs and Maria's testimony place Alice at the library during the death window.\n"));
        return new MysteryCase(files, SOLUTION);
    }

    private void writeGame(Path gameRoot) {
        MysteryCase mysteryCase = caseData();
        for (FileEntry file : mysteryCase.getFiles()) {
            write(gameRoot.resolve(file.getRelativePath()), file.getContent());
        }
    }

    private static Solution parseSolution(String[] args) {
        String killer = null;
        String weapon = null;
        String location = null;
        for (int index = 1; index < args.length - 1; index++) {
            if ("--killer".equals(args[index])) {
                killer = args[++index];
            } else if ("--weapon".equals(args[index])) {
                weapon = args[++index];
            } else if ("--location".equals(args[index])) {
                location = args[++index];
            }
        }
        return new Solution(killer, weapon, location);
    }

    private static void write(Path path, String content) {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new GameException("Could not write " + path, ex);
        }
    }

    private static void deleteRecursively(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.walk(path)
                    .sorted((left, right) -> right.compareTo(left))
                    .forEach(item -> {
                        try {
                            Files.delete(item);
                        } catch (IOException ex) {
                            throw new GameException("Could not delete " + item, ex);
                        }
                    });
        } catch (IOException ex) {
            throw new GameException("Could not reset " + path, ex);
        }
    }

    private static boolean matches(String actual, String expected) {
        return actual != null && actual.trim().equalsIgnoreCase(expected);
    }

    public static final class MysteryCase {
        private final List<FileEntry> files;
        private final Solution solution;

        private MysteryCase(List<FileEntry> files, Solution solution) {
            this.files = new ArrayList<FileEntry>(files);
            this.solution = solution;
        }

        public List<FileEntry> getFiles() {
            return new ArrayList<FileEntry>(files);
        }

        public Solution getSolution() {
            return solution;
        }
    }

    public static final class FileEntry {
        private final String relativePath;
        private final String content;

        private FileEntry(String relativePath, String content) {
            this.relativePath = relativePath;
            this.content = content;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getContent() {
            return content;
        }
    }

    public static final class Solution {
        private final String killer;
        private final String weapon;
        private final String location;

        public Solution(String killer, String weapon, String location) {
            this.killer = killer;
            this.weapon = weapon;
            this.location = location;
        }

        public String getKiller() {
            return killer;
        }

        public String getWeapon() {
            return weapon;
        }

        public String getLocation() {
            return location;
        }
    }

    public static final class SolveResult {
        private final boolean correct;
        private final String message;

        private SolveResult(boolean correct, String message) {
            this.correct = correct;
            this.message = message;
        }

        public boolean isCorrect() {
            return correct;
        }

        public String getMessage() {
            return message;
        }
    }

    public static final class GameException extends RuntimeException {
        private GameException(String message) {
            super(message);
        }

        private GameException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
