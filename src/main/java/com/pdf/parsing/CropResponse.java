package com.pdf.parsing;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CropResponse {
    private boolean success;
    private String message;
    private List<CroppedImage> images;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<CroppedImage> getImages() {
		return images;
	}
	public void setImages(List<CroppedImage> images) {
		this.images = images;
	}
    
    
}