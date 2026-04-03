package com.example.unusedclassdetector.report;

import com.example.unusedclassdetector.model.AnalysisResult;

public final class UnusedClassReportPrinter {

    private UnusedClassReportPrinter() {
    }

    public static void print(AnalysisResult result) {
        System.out.println("Unused Class Detector Report");
        System.out.println("----------------------------");
        System.out.println("Source root: " + result.getSourceRoot());
        System.out.println("Total classes found: " + result.getTotalClasses());
        System.out.println("Referenced classes: " + result.getReferencedClasses());
        System.out.println("Ignored classes: " + result.getIgnoredClasses().size());
        System.out.println("Potentially unused classes: " + result.getPotentiallyUnusedClasses().size());
        System.out.println();

        if (!result.getPotentiallyUnusedClasses().isEmpty()) {
            System.out.println("Potentially unused:");
            for (String className : result.getPotentiallyUnusedClasses()) {
                System.out.println("- " + className);
            }
            System.out.println();
        }

        if (!result.getWarnings().isEmpty()) {
            System.out.println("Warnings:");
            for (String warning : result.getWarnings()) {
                System.out.println("- " + warning);
            }
        }
    }
}

