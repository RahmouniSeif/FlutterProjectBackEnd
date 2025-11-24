package com.campify.campifybackend.campifyBase.apiDTOs;


import com.campify.campifybackend.campifyBase.dtos.BaseDto;
import com.campify.campifybackend.campifyBase.entities.BaseEntity;

public class ApiSingleResponse<E extends BaseEntity, OUTDTO extends BaseDto<E>> {

    private boolean success;
    private String message;
    private OUTDTO data;

    public ApiSingleResponse() {
    }

    public ApiSingleResponse(boolean success, String message, OUTDTO data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and setters


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

    public OUTDTO getData() {
        return data;
    }

    public void setData(OUTDTO data) {
        this.data = data;
    }
}