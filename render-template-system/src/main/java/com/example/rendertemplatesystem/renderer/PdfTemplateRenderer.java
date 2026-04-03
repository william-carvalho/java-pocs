package com.example.rendertemplatesystem.renderer;

import com.example.rendertemplatesystem.dto.RenderResponse;
import com.example.rendertemplatesystem.enums.RenderFormat;
import com.example.rendertemplatesystem.exception.TemplateRenderException;
import com.example.rendertemplatesystem.template.TemplateDefinition;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PdfTemplateRenderer implements TemplateRenderer {

    @Override
    public RenderFormat getFormat() {
        return RenderFormat.PDF;
    }

    @Override
    public RenderResponse render(TemplateDefinition template, Map<String, Object> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph(template.getTitle(), titleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{2f, 4f});

            for (Map.Entry<String, String> entry : template.getFields().entrySet()) {
                table.addCell(new Paragraph(entry.getValue(), bodyFont));
                table.addCell(new Paragraph(asString(data.get(entry.getKey())), bodyFont));
            }

            document.add(table);
        } catch (DocumentException ex) {
            throw new TemplateRenderException("Failed to render PDF", ex);
        } finally {
            document.close();
        }

        return new RenderResponse(
                template.getName(),
                getFormat(),
                "application/pdf",
                template.getName() + ".pdf",
                outputStream.toByteArray()
        );
    }

    private String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
