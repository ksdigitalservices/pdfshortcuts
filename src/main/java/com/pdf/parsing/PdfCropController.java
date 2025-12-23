package com.pdf.parsing;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class PdfCropController {

    private static final Logger log = LoggerFactory.getLogger(PdfCropController.class);
    
    private final PdfCropService pdfCropService;
    
    public PdfCropController(PdfCropService pdfCropService) {
        this.pdfCropService = pdfCropService;
    }
    
    @GetMapping("/pvc")
    public String index(Model model) {
        return "pdfcrop";
    }
    
    @PostMapping("/crop-document")
    @ResponseBody
    public Map<String, Object> cropDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") String documentTypeStr) {
        
        log.info("Processing: {} as {}", file.getOriginalFilename(), documentTypeStr);
        
        try {
            DocumentType documentType = DocumentType.valueOf(documentTypeStr.toUpperCase());
            
            // Use default file names
            String baseName = file.getOriginalFilename().replace(".pdf", "").replace(".PDF", "");
            String frontFileName = baseName + "_FRONT.jpg";
            String backFileName = baseName + "_BACK.jpg";
            
            return pdfCropService.cropDocumentWithTwoAreas(file, documentType, frontFileName, backFileName);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }
    
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadImage(
            @RequestParam("filename") String filename,
            @RequestParam("originalName") String originalName) throws IOException {
        
        try {
            Path imagePath = Paths.get("./temp/" + filename);
            
            if (!Files.exists(imagePath)) {
                throw new IOException("File not found: " + filename);
            }
            
            byte[] imageBytes = Files.readAllBytes(imagePath);
            ByteArrayResource resource = new ByteArrayResource(imageBytes);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + originalName + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .contentLength(imageBytes.length)
                    .body(resource);
                    
        } catch (Exception e) {
            throw new IOException("Failed to download image: " + e.getMessage());
        }
    }
}