package com.example.dontpadclone.controller;

import com.example.dontpadclone.dto.PadResponse;
import com.example.dontpadclone.service.PadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PadWebController {

    private final PadService padService;

    public PadWebController(PadService padService) {
        this.padService = padService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("exampleSlug", "welcome");
        return "index";
    }

    @GetMapping("/pads/{slug}")
    public String openPad(@PathVariable String slug, Model model) {
        PadResponse pad = padService.getOrCreateBySlug(slug);
        model.addAttribute("pad", pad);
        return "pad";
    }
}

