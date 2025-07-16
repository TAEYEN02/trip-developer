package com.korea.trip.service;

import com.korea.trip.models.Photo;
import com.korea.trip.models.Schedule;
import com.korea.trip.repositories.PhotoRepository;
import com.korea.trip.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final ScheduleRepository scheduleRepository;
    private final String uploadDir = "./uploads"; // 사진 저장 경로

    @Autowired
    public PhotoService(PhotoRepository photoRepository, ScheduleRepository scheduleRepository) {
        this.photoRepository = photoRepository;
        this.scheduleRepository = scheduleRepository;
        // 업로드 디렉토리가 없으면 생성
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public Photo uploadPhoto(MultipartFile file, Long scheduleId) throws IOException {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found with id " + scheduleId));

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);

        Photo photo = new Photo();
        photo.setImageUrl("/uploads/" + fileName); // 웹에서 접근할 수 있는 URL
        photo.setSchedule(schedule);

        return photoRepository.save(photo);
    }

    public List<Photo> getPhotosByScheduleId(Long scheduleId) {
        return photoRepository.findByScheduleId(scheduleId);
    }
}
