package com.example.terminalmurdermystery.generator;

import com.example.terminalmurdermystery.model.CaseSolution;
import com.example.terminalmurdermystery.model.Evidence;
import com.example.terminalmurdermystery.model.Location;
import com.example.terminalmurdermystery.model.MysteryCase;
import com.example.terminalmurdermystery.model.Suspect;
import com.example.terminalmurdermystery.model.Witness;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class CaseGenerator {

    public MysteryCase generateFixedCase() {
        CaseSolution solution = new CaseSolution("Alice Moreau", "Letter Opener", "Library");

        Map<String, String> logs = new LinkedHashMap<String, String>();
        logs.put("security_log.txt",
                "20:30 - Victor Hales enters the dining room\n" +
                "20:42 - Bruno Costa seen in the kitchen corridor\n" +
                "20:47 - Alice Moreau enters the library\n" +
                "20:52 - Eva Lin leaves the study\n" +
                "20:55 - Unidentified footsteps heard near the library\n" +
                "21:03 - Alice Moreau seen leaving the library alone\n");

        logs.put("phone_log.txt",
                "20:40 - Bruno Costa calls reception for more ice\n" +
                "20:48 - No outgoing call from Alice Moreau's room\n" +
                "20:56 - Victor Hales receives an unanswered call\n");

        Map<String, String> reports = new LinkedHashMap<String, String>();
        reports.put("autopsy.txt",
                "Victim: Victor Hales\n" +
                "Estimated time of death: between 20:55 and 21:00\n" +
                "Cause: single stab wound from a narrow blade\n" +
                "Probable weapon: decorative letter opener or similar object\n");

        reports.put("room_map.txt",
                "Ground floor:\n" +
                "- Dining room\n" +
                "- Kitchen\n" +
                "- Library\n" +
                "- Study\n" +
                "The library connects directly to the main hall.\n");

        return new MysteryCase(
                "The Hales Mansion Murder",
                "Industrialist Victor Hales was found dead in the mansion library during a private dinner. " +
                        "You must identify the killer, the murder weapon and the crime location by exploring the case files.",
                Arrays.asList(
                        new Suspect("Alice Moreau", "Estate Manager",
                                "Claims she was checking inventory records in the library but left before 20:50.",
                                "During questioning she kept insisting Victor had no enemies inside the house."),
                        new Suspect("Bruno Costa", "Chef",
                                "Says he stayed near the kitchen preparing dessert for the guests.",
                                "Kitchen staff confirm he requested more ice around 20:40 and stayed busy."),
                        new Suspect("Eva Lin", "Private Secretary",
                                "Says she was organizing contracts in the study until 21:05.",
                                "She admitted Victor planned to fire someone that night.")
                ),
                Arrays.asList(
                        new Witness("Maria Torres",
                                "I saw Alice leaving the library around 21:03. She was walking fast and hiding something in her sleeve."),
                        new Witness("Dario Nunes",
                                "I heard Victor arguing with a woman in the library shortly before the scream. I am sure it was not Eva."),
                        new Witness("Otavio Reis",
                                "The chef never left the kitchen corridor for long. I would have noticed if he went to the library.")
                ),
                Arrays.asList(
                        new Location("Library", "A quiet room with leather chairs, shelves and a heavy oak desk."),
                        new Location("Kitchen", "Busy, noisy and full of staff during dinner service."),
                        new Location("Study", "A private room used for contracts and business papers.")
                ),
                Arrays.asList(
                        new Evidence("fingerprint_report.txt",
                                "Partial prints from the library desk.",
                                "Fingerprints on the blood-stained letter opener partially match Alice Moreau."),
                        new Evidence("broken_watch.txt",
                                "The victim's watch stopped after the attack.",
                                "The stopped watch points to 20:58, matching the autopsy window."),
                        new Evidence("desk_note.txt",
                                "A torn note found in the library.",
                                "The note reads: 'If you tell Victor tonight, I am finished. Please let me explain. - A'"),
                        new Evidence("weapon_case.txt",
                                "A display case near the library desk.",
                                "One decorative letter opener is missing from the case. Dust marks show it was removed recently.")
                ),
                logs,
                reports,
                Arrays.asList(
                        "Cross-check the estimated time of death with movement in the security log.",
                        "One suspect's alibi conflicts directly with a witness statement.",
                        "The weapon is described in the autopsy and confirmed by the evidence near the desk."
                ),
                solution
        );
    }
}

