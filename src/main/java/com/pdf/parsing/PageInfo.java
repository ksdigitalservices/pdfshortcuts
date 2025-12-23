package com.pdf.parsing;

import lombok.Data;

@Data
public class PageInfo {
    private int totalPages;
    private int currentPage;
    private int pageWidth;
    private int pageHeight;
    private float dpi;
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPageWidth() {
		return pageWidth;
	}
	public void setPageWidth(int pageWidth) {
		this.pageWidth = pageWidth;
	}
	public int getPageHeight() {
		return pageHeight;
	}
	public void setPageHeight(int pageHeight) {
		this.pageHeight = pageHeight;
	}
	public float getDpi() {
		return dpi;
	}
	public void setDpi(float dpi) {
		this.dpi = dpi;
	}
    
    
}
