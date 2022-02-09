package fftl.usedtradingapi.commons.dto;

import lombok.Getter;

@Getter
public class Response<T> {
    private boolean success;
    private String message;
    private T data;

    public Response(boolean success, String message){
        this.success = success;
        this.message = message;
        this.data = null;
    }

    public Response(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Response<T> response(boolean success, String message){
        return new Response(success, message);
    }


    public Response<T> response(boolean success, String message, T data){
        return new Response(success, message, data);
    }

}
