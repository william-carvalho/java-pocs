package com.example.rendertemplatesystem.controller;

import com.example.rendertemplatesystem.dto.RenderRequest;
import com.example.rendertemplatesystem.dto.RenderResponse;
import com.example.rendertemplatesystem.enums.RenderFormat;
import com.example.rendertemplatesystem.service.RenderService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RenderController {

    private final RenderService renderService;

    public RenderController(RenderService renderService) {
        this.renderService = renderService;
    }

    @PostMapping("/render")
    public ResponseEntity<byte[]> render(@Valid @RequestBody RenderRequest request) {
        RenderResponse response = renderService.render(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(response.getContentType()));
        headers.add("X-Template-Name", response.getTemplateName());
        headers.add("X-Render-Format", response.getFormat().name());

        if (response.getFormat() != RenderFormat.HTML) {
            headers.setContentDisposition(ContentDisposition.attachment()
                    .filename(response.getFileName(), StandardCharsets.UTF_8)
                    .build());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(response.getContent());
    }

    @GetMapping("/templates")
    public ResponseEntity<List<String>> templates() {
        return ResponseEntity.ok(renderService.getTemplates());
    }

    @GetMapping("/formats")
    public ResponseEntity<List<String>> formats() {
        return ResponseEntity.ok(renderService.getFormats());
    }
}
