package comfenalco.api.filter.sms.utils;


import comfenalco.api.filter.sms.dto.ApiResponse;

import java.time.Instant;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(String message, T data, Object metaData, int statusCode) {
        return new ApiResponse<>(true, message, data, statusCode, Instant.now().toString(), metaData );
    }

    public static <T> ApiResponse<T> error(String message, T data, int statusCode) {
        return new ApiResponse<>(false, message, data, statusCode, Instant.now().toString(), null );
    }

}
