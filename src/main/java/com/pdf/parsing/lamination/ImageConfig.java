package com.pdf.parsing.lamination;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
public class ImageConfig {
    private int targetWidth = 200;
    private int targetHeight = 100;
    private int dpi = 800;
    private float compressionQuality = 1.0f;
	public int getTargetWidth() {
		return targetWidth;
	}
	public void setTargetWidth(int targetWidth) {
		this.targetWidth = targetWidth;
	}
	public int getTargetHeight() {
		return targetHeight;
	}
	public void setTargetHeight(int targetHeight) {
		this.targetHeight = targetHeight;
	}
	public int getDpi() {
		return dpi;
	}
	public void setDpi(int dpi) {
		this.dpi = dpi;
	}
	public float getCompressionQuality() {
		return compressionQuality;
	}
	public void setCompressionQuality(float compressionQuality) {
		this.compressionQuality = compressionQuality;
	}
	public ImageConfig(int targetWidth, int targetHeight, int dpi, float compressionQuality) {
		super();
		this.targetWidth = targetWidth;
		this.targetHeight = targetHeight;
		this.dpi = dpi;
		this.compressionQuality = compressionQuality;
	}
	public ImageConfig() {
		super();
	}
    
    
}