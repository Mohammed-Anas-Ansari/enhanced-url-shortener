package com.eus.dto;

import com.eus.enums.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.eus.constant.Status.ERROR;
import static com.eus.constant.Status.SUCCESS;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomResponse<T> {

    private String status;
    private String message;
    private ErrorType errorType = ErrorType.getDefault();
    private T data;

    public static <T> CustomResponse<T> success(T data) {
        return new CustomResponse<>(SUCCESS, SUCCESS, ErrorType.getDefault(), data);
    }

    public static <T> CustomResponse<T> error(ErrorType errorType, T data) {
        return new CustomResponse<>(ERROR, ERROR, errorType, data);
    }

    public static <T> CustomResponse<T> error(ErrorType errorType, String message) {
        return new CustomResponse<>(ERROR, message, errorType, null);
    }
}
