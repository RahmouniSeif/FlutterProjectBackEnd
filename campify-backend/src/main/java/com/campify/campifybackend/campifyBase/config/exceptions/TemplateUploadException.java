package com.campify.campifybackend.campifyBase.config.exceptions;

public class TemplateUploadException extends RuntimeException {
    public TemplateUploadException(String message) {
        super(message);
    }

    public TemplateUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}