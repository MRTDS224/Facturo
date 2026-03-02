package com.facturo.service;

import com.facturo.model.Invoice;
import com.facturo.model.InvoiceItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

import com.facturo.model.Client;

public class PdfGenerationService {

    public void generateInvoicePdf(Invoice invoice, String outputPath, boolean showAddr, boolean showPhone,
            boolean showEmail, String headerText) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText(headerText != null && !headerText.isEmpty() ? headerText : "FACTURO - FACTURE");
                contentStream.endText();

                // Invoice Info
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                int currentY = 700;
                contentStream.newLineAtOffset(50, currentY);
                contentStream.showText("Facture N°: " + invoice.getInvoiceNumber());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Date: " + invoice.getDate());

                contentStream.newLineAtOffset(0, -30); // Gap before client info
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Client: " + invoice.getCustomerName());
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                Client c = invoice.getClient();
                if (c != null) {
                    if (showAddr && c.getAddress() != null && !c.getAddress().isEmpty()) {
                        contentStream.newLineAtOffset(0, -15);
                        contentStream.showText(c.getAddress());
                    }
                    if (showPhone && c.getPhone() != null && !c.getPhone().isEmpty()) {
                        contentStream.newLineAtOffset(0, -15);
                        contentStream.showText("Tél: " + c.getPhone());
                    }
                    if (showEmail && c.getEmail() != null && !c.getEmail().isEmpty()) {
                        contentStream.newLineAtOffset(0, -15);
                        contentStream.showText("Email: " + c.getEmail());
                    }
                }
                contentStream.endText();

                // Table Header
                int tableYPos = 560; // Moved down to accommodate longer client info
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, tableYPos);
                contentStream.showText("Description");
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("Qté");
                contentStream.newLineAtOffset(50, 0);
                contentStream.showText("Prix Uni.");
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText("Total");
                contentStream.endText();

                // Separator Line
                contentStream.moveTo(50, tableYPos - 5);
                contentStream.lineTo(550, tableYPos - 5);
                contentStream.stroke();

                // Items
                int yPosition = tableYPos - 25;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                for (InvoiceItem item : invoice.getItems()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText(item.getDescription());
                    contentStream.newLineAtOffset(250, 0);
                    contentStream.showText(String.valueOf(item.getQuantity()));
                    contentStream.newLineAtOffset(50, 0);
                    contentStream.showText(String.format("%.2f", item.getUnitPrice()));
                    contentStream.newLineAtOffset(80, 0);
                    contentStream.showText(String.format("%.2f", item.getTotal()));
                    contentStream.endText();
                    yPosition -= 20;
                }

                // Total
                yPosition -= 20;
                contentStream.moveTo(50, yPosition + 15);
                contentStream.lineTo(550, yPosition + 15);
                contentStream.stroke();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(350, yPosition);
                contentStream.showText("Total TTC: " + String.format("%.2f", invoice.getTotalAmount()));
                contentStream.endText();
            }

            document.save(outputPath);
            System.out.println("PDF generated at: " + outputPath);

        } catch (IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
        }
    }
}
