package com.pdf.parsing;

import lombok.Data;

@Data
public class CropRequest {
    private int page = 0;
    private int x = 0;
    private int y = 0;
    private int width = 100;
    private int height = 100;
    private float dpi = 150f;
    private String format = "PNG";
    private DocumentType documentType; // Added for document-specific cropping
    private String fileName; // Added for custom file naming
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public float getDpi() {
		return dpi;
	}
	public void setDpi(float dpi) {
		this.dpi = dpi;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public DocumentType getDocumentType() {
		return documentType;
	}
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}