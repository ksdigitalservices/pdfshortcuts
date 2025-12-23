package com.pdf.parsing.lamination;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.parsing.PdfCropController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class PdfController {
    
	private static final Logger log = LoggerFactory.getLogger(PdfCropController.class);
    
	@Autowired
    private  PdfGenerationService pdfGenerationService;
    
	@GetMapping("/temp")
	public String home() {
		return "lamination";
	}
    @PostMapping(value = "/generate-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void generatePdf(
            @RequestParam("images") List<MultipartFile> images,
            HttpServletResponse response) throws IOException {
        
        log.info("Received request to generate PDF with {} images", images.size());
        
        // Validate input
        if (images == null || images.size() != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Exactly 2 images are required");
            return;
        }
        
        for (MultipartFile image : images) {
            if (image.isEmpty() || !image.getContentType().startsWith("image/")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                        "Invalid image file: " + image.getOriginalFilename());
                return;
            }
        }
        
        try {
            // Generate PDF
            byte[] pdfBytes = pdfGenerationService.generatePdfFromImages(images);
            
            // Set response headers for file download
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                    "attachment; filename=\"generated-images.pdf\"");
            response.setContentLength(pdfBytes.length);
            
            // Write PDF to response
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
            
            log.info("PDF generated successfully, size: {} bytes", pdfBytes.length);
            
        } catch (Exception e) {
            log.error("Error processing PDF generation request", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Error generating PDF: " + e.getMessage());
        }
    }
    
    // Health check endpoint
    @GetMapping("/health")
    public String health() {
        return "Image to PDF Service is running";
    }
}