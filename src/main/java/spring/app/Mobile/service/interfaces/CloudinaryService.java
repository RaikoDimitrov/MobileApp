package spring.app.Mobile.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudinaryService {

    List<String> uploadImages(List<MultipartFile> files);

    String uploadImage(MultipartFile file);

    void deleteImage(String publicId);

    String extractPublicIdFromUrl(String imageUrl);
}
