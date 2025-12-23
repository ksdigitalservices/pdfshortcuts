package com.pdf.parsing;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class DocumentTypeService {
    private static final Map<DocumentType, List<CropArea>> DOCUMENT_CROP_AREAS = new HashMap<>();
    static {
        DOCUMENT_CROP_AREAS.put(DocumentType.AADHAR_CARD, Arrays.asList(
        		new CropArea("FRONT", 380, 6359, 2830, 1850),  // Front side: Name, Aadhar Number, Photo
                new CropArea("BACK", 3355, 6359, 2830, 1850)    // Back side:
        ));
        DOCUMENT_CROP_AREAS.put(DocumentType.VOTER_ID, Arrays.asList(
        		new CropArea("FRONT", 340, 920, 2830, 1850),  // Front side: Name, Aadhar Number, Photo
                new CropArea("BACK", 3630, 920, 2830, 1850)    // Back side:
        ));
     
        // PAN Card - 2 crop areas (800 DPI ULTRA HIGH QUALITY)
        DOCUMENT_CROP_AREAS.put(DocumentType.PAN_CARD, Arrays.asList(
        		new CropArea("FRONT", 780, 7300, 2530, 1550),  // Front side: Name, Aadhar Number, Photo
                new CropArea("BACK", 3430, 7300, 2530, 1550)    // Back side:
        ));
    }
    
    public List<CropArea> getCropAreas(DocumentType documentType) {
        return DOCUMENT_CROP_AREAS.get(documentType);
    }
    
    public String getDefaultFileName(DocumentType documentType, String originalFileName, String areaName) {
        String baseName = originalFileName.replace(".pdf", "").replace(".PDF", "");
        
        if (areaName.equals("FRONT") || areaName.equals("PAN DETAILS")) {
            return baseName + "_FRONT.jpg";
        } else if (areaName.equals("BACK") || areaName.equals("PHOTO/SIGNATURE")) {
            return baseName + "_BACK.jpg";
        } else {
            return baseName + "_" + areaName + ".jpg";
        }
    }
    
    // ULTRA HIGH DPI - 800 DPI FOR ALL DOCUMENTS
    public int getRecommendedDPI(DocumentType documentType) {
        return 800; // ULTRA HIGH DPI FOR ALL DOCUMENTS
    }
}