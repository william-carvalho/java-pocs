package com.example.terminalmurdermystery.validator;

import com.example.terminalmurdermystery.model.CaseSolution;

public class AnswerValidator {

    public boolean isCorrect(CaseSolution solution, String killer, String weapon, String location) {
        return equalsNormalized(solution.getKillerName(), killer)
                && equalsNormalized(solution.getWeapon(), weapon)
                && equalsNormalized(solution.getLocation(), location);
    }

    private boolean equalsNormalized(String expected, String provided) {
        if (expected == null || provided == null) {
            return false;
        }
        return expected.trim().equalsIgnoreCase(provided.trim());
    }
}

