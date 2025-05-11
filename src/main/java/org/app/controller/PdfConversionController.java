package org.app.controller;

import org.app.model.requests.ConversionRequest;
import org.app.services.PdfConversionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/pdf")
public class PdfConversionController {

    private final PdfConversionService pdfConversionService;

    public PdfConversionController(PdfConversionService pdfConversionService) {
        this.pdfConversionService = pdfConversionService;
    }

    @PostMapping(value = "/convert", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> convertToPdf(@RequestBody ConversionRequest request) {
        byte[] pdfBytes = pdfConversionService.convertToPdf(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cv.pdf")
                .body(pdfBytes);
    }
}