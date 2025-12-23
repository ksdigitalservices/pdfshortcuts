package com.pdf.parsing;


public enum DocumentType {
    AADHAR_CARD("Aadhar Card"),
    VOTER_ID("Voter ID"),
    PAN_CARD("PAN Card");
    
    private final String displayName;
    
    DocumentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}