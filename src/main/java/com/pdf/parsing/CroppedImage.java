package com.pdf.parsing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public  class CroppedImage {
    private String imageUrl;
    private String fileName;
    private String downloadUrl;
    private String displayName;
    private int width;
    private int height;

	public CroppedImage(String imageUrl, String fileName, String downloadUrl, String displayName, int width,
			int height) {
		super();
		this.imageUrl = imageUrl;
		this.fileName = fileName;
		this.downloadUrl = downloadUrl;
		this.displayName = displayName;
		this.width = width;
		this.height = height;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
    
    
}