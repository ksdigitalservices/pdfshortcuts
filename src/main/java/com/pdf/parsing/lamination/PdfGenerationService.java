package com.pdf.parsing.lamination;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.pdf.parsing.PdfCropController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private static final Logger log = LoggerFactory.getLogger(PdfCropController.class);
    
    @Autowired
    private  ImageConfig imageConfig;
    
    public byte[] generatePdfFromImages(List<MultipartFile> imageFiles) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4)) {
            
            document.setMargins(0, 0, 0, 0);
            
            for (MultipartFile imageFile : imageFiles) {
                // Resize image
                byte[] resizedImage = resizeImage(imageFile);
                
                // Add to PDF
                ImageData imageData = ImageDataFactory.create(resizedImage);
                Image pdfImage = new Image(imageData);
                
                // Scale image to fit A4 page
                pdfImage.setAutoScale(true);
                pdfImage.setWidth(PageSize.A4.getWidth());
                pdfImage.setHeight(PageSize.A4.getHeight());
                
                document.add(pdfImage);
                
                // Add new page for next image (if not last)
                if (imageFiles.indexOf(imageFile) < imageFiles.size() - 1) {
                    document.add(new com.itextpdf.layout.element.AreaBreak());
                }
            }
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generating PDF", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
    
    private byte[] resizeImage(MultipartFile imageFile) {
        try (InputStream inputStream = imageFile.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Resize image using Thumbnailator with high quality
            Thumbnails.of(inputStream)
                    .size(imageConfig.getTargetWidth(), imageConfig.getTargetHeight())
                    .outputFormat("JPEG")
                    .outputQuality(imageConfig.getCompressionQuality())
                    .toOutputStream(baos);
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error resizing image: {}", imageFile.getOriginalFilename(), e);
            throw new RuntimeException("Failed to resize image", e);
        }
    }
    
    // Alternative method using Java ImageIO (if you prefer not to use Thumbnailator)
    private byte[] resizeImageWithImageIO(MultipartFile imageFile) {
        try {
            BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
            
            // Create high-quality resized image
            java.awt.Image resizedImage = originalImage.getScaledInstance(
                    imageConfig.getTargetWidth(),
                    imageConfig.getTargetHeight(),
                    java.awt.Image.SCALE_SMOOTH
            );
            
            BufferedImage bufferedResizedImage = new BufferedImage(
                    imageConfig.getTargetWidth(),
                    imageConfig.getTargetHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            
            bufferedResizedImage.getGraphics().drawImage(resizedImage, 0, 0, null);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedResizedImage, "JPEG", baos);
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error resizing image with ImageIO", e);
            throw new RuntimeException("Failed to resize image", e);
        }
    }
}