package com.wannago.wannago.models;

/**
 * Created by Rafael Valle on 4/20/2017.
 */

public interface ImageUploadResponse
{
    void OnSuccess(String message);
    void OnError(String message);
}
