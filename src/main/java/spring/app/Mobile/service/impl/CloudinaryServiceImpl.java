package spring.app.Mobile.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import spring.app.Mobile.service.interfaces.CloudinaryService;

import java.io.IOException;
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

        files.forEach(file -> {
            System.out.println("Received file: " + file.getOriginalFilename() + " with size " + file.getSize());
            try {
                byte[] fileBytes = file.getBytes();
                System.out.println("File byte length: " + fileBytes.length);  // Log byte length to debug
            } catch (IOException e) {
                System.err.println("Error reading file bytes: " + e.getMessage());
            }
        });
        return files.parallelStream().map(file -> {
            if (file.isEmpty()) throw new RuntimeException("Empty file received for upload");

            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                return (String) uploadResult.get("secure_url");
            } catch (IOException e) {
                throw new RuntimeException("Error uploading image to cloudinary", e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image from cloudinary", e);
        }
    }

    @Override
    public String extractPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/v\\d+/");
        if (parts.length > 1) {
            String publicIdWithoutExtenstion = parts[1].split("\\.")[0];
            return publicIdWithoutExtenstion;
        }
        return null;
    }
}
