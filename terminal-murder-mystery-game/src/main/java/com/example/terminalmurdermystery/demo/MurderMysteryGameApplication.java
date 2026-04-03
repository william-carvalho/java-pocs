package com.example.terminalmurdermystery.demo;

import com.example.terminalmurdermystery.core.GameSetupService;
import com.example.terminalmurdermystery.model.CaseSolution;
import com.example.terminalmurdermystery.validator.AnswerValidator;

import java.nio.file.Path;

public class MurderMysteryGameApplication {

    public static void main(String[] args) {
        GameSetupService gameSetupService = new GameSetupService();
        AnswerValidator answerValidator = new AnswerValidator();

        if (args == null || args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];
        switch (command) {
            case "init":
                Path gameRoot = gameSetupService.init();
                System.out.println("Game created successfully at: " + gameRoot.toAbsolutePath());
                System.out.println("Start by reading:");
                System.out.println("cat " + gameRoot.resolve("instructions.txt"));
                break;
            case "reset":
                Path resetRoot = gameSetupService.reset();
                System.out.println("Game reset successfully at: " + resetRoot.toAbsolutePath());
                System.out.println("Start by reading:");
                System.out.println("cat " + resetRoot.resolve("instructions.txt"));
                break;
            case "solve":
                solve(args, gameSetupService, answerValidator);
                break;
            case "reveal":
                reveal(gameSetupService);
                break;
            default:
                printUsage();
        }
    }

    private static void solve(String[] args, GameSetupService gameSetupService, AnswerValidator answerValidator) {
        String killer = null;
        String weapon = null;
        String location = null;

        for (int index = 1; index < args.length - 1; index += 2) {
            String flag = args[index];
            String value = args[index + 1];
            if ("--killer".equals(flag)) {
                killer = value;
            } else if ("--weapon".equals(flag)) {
                weapon = value;
            } else if ("--location".equals(flag)) {
                location = value;
            }
        }

        if (killer == null || weapon == null || location == null) {
            System.out.println("Missing required arguments for solve.");
            printUsage();
            return;
        }

        CaseSolution solution = gameSetupService.revealSolution();
        if (answerValidator.isCorrect(solution, killer, weapon, location)) {
            System.out.println("Case solved. Your investigation is correct.");
        } else {
            System.out.println("That answer is not correct yet. Revisit the evidence and try again.");
        }
    }

    private static void reveal(GameSetupService gameSetupService) {
        CaseSolution solution = gameSetupService.revealSolution();
        System.out.println("Official solution");
        System.out.println("-----------------");
        System.out.println("Killer: " + solution.getKillerName());
        System.out.println("Weapon: " + solution.getWeapon());
        System.out.println("Location: " + solution.getLocation());
    }

    private static void printUsage() {
        System.out.println("Commands:");
        System.out.println("  init");
        System.out.println("  reset");
        System.out.println("  solve --killer \"NAME\" --weapon \"WEAPON\" --location \"LOCATION\"");
        System.out.println("  reveal");
    }
}

