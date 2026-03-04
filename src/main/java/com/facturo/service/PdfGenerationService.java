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

        SettingsService settingsService = new SettingsService();
        String companyName = settingsService.getSetting("company_name",
                headerText != null && !headerText.isEmpty() ? headerText : "FACTURO - FACTURE");
        String companyAddress = settingsService.getSetting("company_address", "");
        String companyPhone = settingsService.getSetting("company_phone", "");
        String companyEmail = settingsService.getSetting("company_email", "");
        String companyLogo = settingsService.getSetting("company_logo", "");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Top Left: Company Info
                int companyY = 750;
                int leftMargin = 50;

                // Simple check if logo path might exist (we could add actual image loading if
                // needed later,
                // but for now we focus on the text as per requirements context)
                if (!companyLogo.isEmpty()) {
                    try {
                        org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject pdImage = org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
                                .createFromFile(companyLogo, document);
                        // Scale down to max 100x100
                        float scale = Math.min(100f / pdImage.getWidth(), 100f / pdImage.getHeight());
                        contentStream.drawImage(pdImage, leftMargin, companyY - (pdImage.getHeight() * scale) + 15,
                                pdImage.getWidth() * scale, pdImage.getHeight() * scale);
                        companyY -= (pdImage.getHeight() * scale) + 20; // adjust y for text
                    } catch (Exception e) {
                        System.err.println("Could not load logo: " + e.getMessage());
                    }
                }

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.beginText();
                contentStream.newLineAtOffset(leftMargin, companyY);
                contentStream.showText(companyName);
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(0, -15);
                if (!companyAddress.isEmpty()) {
                    contentStream.showText(companyAddress);
                    contentStream.newLineAtOffset(0, -15);
                }
                if (!companyPhone.isEmpty()) {
                    contentStream.showText("Tél: " + companyPhone);
                    contentStream.newLineAtOffset(0, -15);
                }
                if (!companyEmail.isEmpty()) {
                    contentStream.showText("Email: " + companyEmail);
                }
                contentStream.endText();

                // Top Right: Client Info
                int rightMargin = 380;
                int clientY = 750;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(rightMargin, clientY);
                contentStream.showText("Client: " + invoice.getCustomerName());
                contentStream.setFont(PDType1Font.HELVETICA, 11);

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

                // Middle: Invoice No & Date
                int middleY = 650;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                // Simple centering approximation, starting near middle
                contentStream.newLineAtOffset(200, middleY);
                contentStream.showText("Facture N° " + invoice.getInvoiceNumber() + " du " + invoice.getDate());
                contentStream.endText();

                // Table Header
                int tableYPos = 600;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, tableYPos);
                contentStream.showText("N°");
                contentStream.newLineAtOffset(40, 0); // gap after N°
                contentStream.showText("Description");
                contentStream.newLineAtOffset(210, 0); // gap after description
                contentStream.showText("Qté");
                contentStream.newLineAtOffset(50, 0); // gap after qty
                contentStream.showText("Prix Uni.");
                contentStream.newLineAtOffset(80, 0); // gap after prix
                contentStream.showText("Total");
                contentStream.endText();

                // Separator Line
                contentStream.moveTo(50, tableYPos - 5);
                contentStream.lineTo(550, tableYPos - 5);
                contentStream.stroke();

                // Items
                int yPosition = tableYPos - 25;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                int articleCount = 1;
                for (InvoiceItem item : invoice.getItems()) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, yPosition);
                    contentStream.showText(String.valueOf(articleCount));
                    contentStream.newLineAtOffset(40, 0);
                    contentStream.showText(item.getDescription());
                    contentStream.newLineAtOffset(210, 0);
                    contentStream.showText(String.valueOf(item.getQuantity()));
                    contentStream.newLineAtOffset(50, 0);
                    contentStream.showText(String.format("%.2f", item.getUnitPrice()));
                    contentStream.newLineAtOffset(80, 0);
                    contentStream.showText(String.format("%.2f", item.getTotal()));
                    contentStream.endText();
                    yPosition -= 20;
                    articleCount++;
                }

                // Total Separator
                yPosition -= 10;
                contentStream.moveTo(50, yPosition);
                contentStream.lineTo(550, yPosition);
                contentStream.stroke();

                // Total text
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(350, yPosition - 20);
                contentStream.showText("Total TTC: " + String.format("%.2f", invoice.getTotalAmount()));
                contentStream.endText();

                // Bottom Signatures
                int signatureY = yPosition - 100;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, signatureY);
                contentStream.showText("Client (e)");
                contentStream.newLineAtOffset(400, 0); // move to right side
                contentStream.showText("Gérant");
                contentStream.endText();

            }

            document.save(outputPath);
            System.out.println("PDF generated at: " + outputPath);

        } catch (IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
        }
    }
}
