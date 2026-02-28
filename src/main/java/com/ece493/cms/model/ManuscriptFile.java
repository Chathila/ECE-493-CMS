package com.ece493.cms.model;

public class ManuscriptFile {
    private final String filename;
    private final String contentBase64;

    public ManuscriptFile(String filename, String contentBase64) {
        this.filename = filename;
        this.contentBase64 = contentBase64;
    }

    public String getFilename() {
        return filename;
    }

    public String getContentBase64() {
        return contentBase64;
    }
}
