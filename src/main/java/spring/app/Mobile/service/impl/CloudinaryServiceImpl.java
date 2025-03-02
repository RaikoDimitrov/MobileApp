package spring.app.Mobile.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.app.Mobile.service.interfaces.CloudinaryService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(@Value("${cloudinary.cloud-name}") String cloudName,
                                 @Value("${cloudinary.api-key}") String apiKey,
                                 @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files) {

        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            return Collections.emptyList();
        }

        return files.parallelStream().filter(file -> !file.isEmpty())
                .map(file -> {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                return (String) uploadResult.get("secure_url");
            } catch (IOException e) {
                throw new RuntimeException("Error uploading image to cloudinary", e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No valid file to upload.");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to Cloudinary", e);
        }
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            System.out.println("Successful deleted from cloud");
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image from cloudinary", e);
        }
    }

    @Override
    public String extractPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        if (parts.length > 1) {
            String publicIdWithExtension = parts[parts.length - 1];
            String[] publicIdParts = publicIdWithExtension.split("\\.");
            return publicIdParts[0];
        }
        return null;
    }
}
