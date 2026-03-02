package com.facturo.service;

import com.facturo.model.Invoice;
import com.facturo.model.InvoiceItem;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PdfTest {

    @Test
    public void testPdfGeneration() {
        Invoice invoice = new Invoice("INV-001", "Test Customer");
        invoice.setDate(LocalDate.now());
        invoice.addItem(new InvoiceItem("Item 1", 2, 50.0)); // Total 100
        invoice.addItem(new InvoiceItem("Item 2", 1, 75.0)); // Total 75
        // Ttl 175

        PdfGenerationService service = new PdfGenerationService();
        String outputPath = "test_invoice.pdf";
        service.generateInvoicePdf(invoice, outputPath, true, true, true, "FACTURO - FACTURE");

        File file = new File(outputPath);
        assertTrue(file.exists(), "PDF should be created");
        // file.delete(); // Keep it to inspect visually if needed
    }
}
