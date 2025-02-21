package spring.app.Mobile.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudinaryService {

    List<String> uploadImages(List<MultipartFile> files);

    void deleteImage(String publicId);
}
