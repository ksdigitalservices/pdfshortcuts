package com.pdf.parsing;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfCropService {

    private static final Logger log = LoggerFactory.getLogger(PdfCropService.class);
    
    private static final String TEMP_DIR = "./temp/";
    private final DocumentTypeService documentTypeService;
    
    public PdfCropService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
    
    // Main method to crop document with exactly 2 areas in ULTRA HIGH QUALITY JPG
    public Map<String, Object> cropDocumentWithTwoAreas(MultipartFile file, DocumentType documentType, 
                                                       String frontFileName, String backFileName) throws IOException {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> croppedImages = new ArrayList<>();
        
        log.info("Cropping {} as {} with ULTRA HIGH QUALITY 800 DPI JPG", file.getOriginalFilename(), documentType);
        
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            List<CropArea> cropAreas = documentTypeService.getCropAreas(documentType);
            
            if (cropAreas == null || cropAreas.size() != 2) {
                throw new IllegalArgumentException("Expected exactly 2 crop areas for " + documentType);
            }
            
            PDFRenderer renderer = new PDFRenderer(document);
            int ultraHighDPI = documentTypeService.getRecommendedDPI(documentType); // 800 DPI
            
            log.info("Using ULTRA HIGH DPI: {} for document processing", ultraHighDPI);
            
            // Process only 2 areas
            for (int i = 0; i < 2; i++) {
                CropArea area = cropAreas.get(i);
                
                try {
                    // Use page 0 with 800 DPI ULTRA HIGH QUALITY
                    BufferedImage pageImage = renderer.renderImageWithDPI(0, ultraHighDPI);
                    
                    // Adjust crop area if needed
                    int actualWidth = Math.min(area.getWidth(), pageImage.getWidth() - area.getX());
                    int actualHeight = Math.min(area.getHeight(), pageImage.getHeight() - area.getY());
                    
                    if (actualWidth > 0 && actualHeight > 0) {
                        BufferedImage croppedImage = pageImage.getSubimage(
                            area.getX(), area.getY(), actualWidth, actualHeight
                        );
                        
                        // Convert to ULTRA HIGH QUALITY JPG format
                        byte[] imageBytes = convertImageToUltraHighQualityJPG(croppedImage);
                        
                        // Generate filename
                        String defaultFileName = documentTypeService.getDefaultFileName(
                            documentType, file.getOriginalFilename(), area.getName()
                        );
                        
                        String finalFileName;
                        if (i == 0) { // First area
                            finalFileName = (frontFileName != null && !frontFileName.trim().isEmpty()) 
                                ? frontFileName 
                                : defaultFileName;
                        } else { // Second area
                            finalFileName = (backFileName != null && !backFileName.trim().isEmpty()) 
                                ? backFileName 
                                : defaultFileName;
                        }
                        
                        // Ensure .jpg extension
                        if (!finalFileName.toLowerCase().endsWith(".jpg") && 
                            !finalFileName.toLowerCase().endsWith(".jpeg")) {
                            finalFileName = finalFileName.replace(".png", ".jpg");
                            if (!finalFileName.toLowerCase().endsWith(".jpg")) {
                                finalFileName += ".jpg";
                            }
                        }
                        
                        // Save temp file
                        String tempFilename = "cropped_800dpi_" + UUID.randomUUID() + ".jpg";
                        Path filePath = Paths.get(TEMP_DIR + tempFilename);
                        Files.write(filePath, imageBytes);
                        
                        // Calculate file size
                        long fileSizeKB = imageBytes.length / 1024;
                        
                        // Add to response
                        Map<String, Object> imageInfo = new HashMap<>();
                        imageInfo.put("imageUrl", "/temp/" + tempFilename);
                        imageInfo.put("fileName", finalFileName);
                        imageInfo.put("downloadUrl", "/download?filename=" + tempFilename + "&originalName=" + finalFileName);
                        imageInfo.put("displayName", area.getName());
                        imageInfo.put("width", croppedImage.getWidth());
                        imageInfo.put("height", croppedImage.getHeight());
                        imageInfo.put("format", "JPG");
                        imageInfo.put("dpi", ultraHighDPI);
                        imageInfo.put("quality", "ULTRA HIGH");
                        imageInfo.put("fileSizeKB", fileSizeKB);
                        
                        croppedImages.add(imageInfo);
                        
                        log.info("Cropped ULTRA HIGH QUALITY {}: {} ({}x{}), DPI: {}, Size: {}KB", 
                                area.getName(), finalFileName, 
                                croppedImage.getWidth(), croppedImage.getHeight(), 
                                ultraHighDPI, fileSizeKB);
                    }
                } catch (Exception e) {
                    log.error("Error cropping area {}: {}", area.getName(), e.getMessage());
                    // Try with lower DPI as fallback
                    tryLowerDPICropping(document, area, i, documentType, file.getOriginalFilename(), 
                                       frontFileName, backFileName, croppedImages);
                }
            }
            
            if (croppedImages.isEmpty()) {
                response.put("success", false);
                response.put("message", "No areas were successfully cropped. Try with a different PDF.");
            } else {
                response.put("success", true);
                response.put("message", String.format("Successfully cropped %d areas in ULTRA HIGH QUALITY 800 DPI JPG", croppedImages.size()));
                response.put("images", croppedImages);
            }
            
        } catch (Exception e) {
            log.error("Error cropping document", e);
            response.put("success", false);
            response.put("message", "Failed to crop document: " + e.getMessage());
        }
        
        return response;
    }
    
    // Fallback method for lower DPI cropping
    private void tryLowerDPICropping(PDDocument document, CropArea area, int index, 
                                    DocumentType documentType, String originalFilename,
                                    String frontFileName, String backFileName,
                                    List<Map<String, Object>> croppedImages) {
        try {
            PDFRenderer renderer = new PDFRenderer(document);
            int lowerDPI = 300; // Fallback to 300 DPI
            
            BufferedImage pageImage = renderer.renderImageWithDPI(0, lowerDPI);
            
            int actualWidth = Math.min(area.getWidth(), pageImage.getWidth() - area.getX());
            int actualHeight = Math.min(area.getHeight(), pageImage.getHeight() - area.getY());
            
            if (actualWidth > 0 && actualHeight > 0) {
                BufferedImage croppedImage = pageImage.getSubimage(
                    area.getX(), area.getY(), actualWidth, actualHeight
                );
                
                byte[] imageBytes = convertImageToUltraHighQualityJPG(croppedImage);
                
                String defaultFileName = documentTypeService.getDefaultFileName(
                    documentType, originalFilename, area.getName()
                );
                
                String finalFileName;
                if (index == 0) {
                    finalFileName = (frontFileName != null && !frontFileName.trim().isEmpty()) 
                        ? frontFileName 
                        : defaultFileName;
                } else {
                    finalFileName = (backFileName != null && !backFileName.trim().isEmpty()) 
                        ? backFileName 
                        : defaultFileName;
                }
                
                if (!finalFileName.toLowerCase().endsWith(".jpg") && 
                    !finalFileName.toLowerCase().endsWith(".jpeg")) {
                    finalFileName = finalFileName.replace(".png", ".jpg");
                    if (!finalFileName.toLowerCase().endsWith(".jpg")) {
                        finalFileName += ".jpg";
                    }
                }
                
                String tempFilename = "cropped_" + UUID.randomUUID() + ".jpg";
                Path filePath = Paths.get(TEMP_DIR + tempFilename);
                Files.write(filePath, imageBytes);
                
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("imageUrl", "/temp/" + tempFilename);
                imageInfo.put("fileName", finalFileName);
                imageInfo.put("downloadUrl", "/download?filename=" + tempFilename + "&originalName=" + finalFileName);
                imageInfo.put("displayName", area.getName() + " (300 DPI)");
                imageInfo.put("width", actualWidth);
                imageInfo.put("height", actualHeight);
                imageInfo.put("format", "JPG");
                imageInfo.put("dpi", lowerDPI);
                imageInfo.put("quality", "HIGH");
                imageInfo.put("fileSizeKB", imageBytes.length / 1024);
                
                croppedImages.add(imageInfo);
                log.info("Used fallback cropping for {} at {} DPI", area.getName(), lowerDPI);
            }
        } catch (Exception e) {
            log.error("Fallback cropping also failed for {}: {}", area.getName(), e.getMessage());
        }
    }
    
    // Convert image to ULTRA HIGH QUALITY JPG
    private byte[] convertImageToUltraHighQualityJPG(BufferedImage image) throws IOException {
        // Create RGB image (JPG doesn't support transparency)
        BufferedImage rgbImage = new BufferedImage(
            image.getWidth(), 
            image.getHeight(), 
            BufferedImage.TYPE_INT_RGB
        );
        
        // Draw original image onto RGB image with maximum quality settings
        Graphics2D g = rgbImage.createGraphics();
        
        // ULTRA HIGH QUALITY RENDERING HINTS
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        
        g.drawImage(image, 0, 0, null);
        g.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Use ImageIO with JPG writer
        javax.imageio.ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        javax.imageio.ImageWriteParam param = writer.getDefaultWriteParam();
        
        // Set ULTRA HIGH compression quality
        param.setCompressionMode(javax.imageio.ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(1.0f); // Maximum quality (1.0 = best)
        
        // Optional: Set compression type
        if (param.canWriteCompressed()) {
            param.setCompressionType("JPEG");
        }
        
        javax.imageio.stream.ImageOutputStream ios = javax.imageio.ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);
        writer.write(null, new javax.imageio.IIOImage(rgbImage, null, null), param);
        
        writer.dispose();
        ios.close();
        
        return baos.toByteArray();
    }
    
    // Get page info (for preview)
    public PageInfo getPageInfo(MultipartFile file, int page, float dpi) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PageInfo info = new PageInfo();
            info.setTotalPages(document.getNumberOfPages());
            
            if (page >= document.getNumberOfPages()) {
                page = document.getNumberOfPages() - 1;
            }
            info.setCurrentPage(page);
            
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(page, dpi);
            
            info.setPageWidth(image.getWidth());
            info.setPageHeight(image.getHeight());
            info.setDpi(dpi);
            
            return info;
        }
    }
    
    // Preview full page in JPG
    public String previewFullPage(MultipartFile file, int page, float dpi) throws IOException {
        createTempDirectory();
        
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            if (page >= document.getNumberOfPages()) {
                page = document.getNumberOfPages() - 1;
            }
            
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(page, dpi);
            
            byte[] imageBytes = convertImageToUltraHighQualityJPG(image);
            
            String filename = "preview_" + UUID.randomUUID() + ".jpg";
            Path filePath = Paths.get(TEMP_DIR + filename);
            Files.write(filePath, imageBytes);
            
            return filename;
        }
    }
    
    private void createTempDirectory() throws IOException {
        Path tempDir = Paths.get(TEMP_DIR);
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
    }
}