package com.ou.oulib.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadService {

    private final Cloudinary cloudinary;

    public String uploadThumbnail(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "folder", "oulib/thumbnails"
                    ));
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload thumbnail to Cloudinary", e);
        }
    }

    public void delete(String thumbnailKey) {
        if (thumbnailKey == null || thumbnailKey.isBlank()) {
            return;
        }
        try {
            String publicId = extractPublicId(thumbnailKey);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete thumbnail from Cloudinary", e);
        }
    }

    private String extractPublicId(String key) {
        String normalized = key;
        int queryIdx = normalized.indexOf('?');
        if (queryIdx > -1) {
            normalized = normalized.substring(0, queryIdx);
        }
        int uploadIdx = normalized.indexOf("/upload/");
        if (uploadIdx > -1) {
            normalized = normalized.substring(uploadIdx + 8);
        }
        int lastDot = normalized.lastIndexOf('.');
        if (lastDot > -1) {
            normalized = normalized.substring(0, lastDot);
        }
        return normalized;
    }
}
