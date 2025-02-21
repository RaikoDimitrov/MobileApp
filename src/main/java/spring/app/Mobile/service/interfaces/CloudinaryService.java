package spring.app.Mobile.service.interfaces;

import jakarta.mail.Multipart;

import java.util.List;
import java.util.Map;

public interface CloudinaryService {

    List<Map> uploadImages(List<String> imagePaths);

    void deleteImage(String publicId);
}
