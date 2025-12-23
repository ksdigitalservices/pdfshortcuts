package com.pdf.parsing;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentCropRequest {
    private DocumentType documentType;
    private MultipartFile file;
    private String frontFileName;
    private String backFileName;
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public String getFrontFileName() {
		return frontFileName;
	}
	public void setFrontFileName(String frontFileName) {
		this.frontFileName = frontFileName;
	}
	public String getBackFileName() {
		return backFileName;
	}
	public void setBackFileName(String backFileName) {
		this.backFileName = backFileName;
	}
    
}