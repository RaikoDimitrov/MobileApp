package spring.app.Mobile.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.app.Mobile.service.interfaces.CloudinaryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public List<Map> uploadImages(List<String> imagePaths) {
        List<Map> uploadedImages = new ArrayList<>();
        try {
            for (Map path : uploadedImages) {
                Map uploadResult = cloudinary.uploader().upload(path, ObjectUtils.emptyMap());
                uploadResult.get(uploadResult);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to cloudinary", e);
        }
        return uploadedImages;
    }

    @Override
    public void deleteImage(String publicId) {
        try {
            Map destroyResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Error deleting image from cloudinary", e);
        }
    }
}
