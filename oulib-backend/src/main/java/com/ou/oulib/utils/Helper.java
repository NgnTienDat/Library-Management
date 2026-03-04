package com.ou.oulib.utils;

import org.springframework.web.multipart.MultipartFile;

import com.ou.oulib.enums.ErrorCode;
import com.ou.oulib.exception.AppException;

public class Helper {
    public static void validatePdf(MultipartFile file) {
        if (!"application/pdf".equals(file.getContentType())) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    public static void validateImage(MultipartFile file) {
        if (!file.getContentType().startsWith("image/")) {
            throw new AppException(ErrorCode.INVALID_IMAGE_TYPE);
        }
    }
}
