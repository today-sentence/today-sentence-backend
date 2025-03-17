package today.todaysentence.global.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import today.todaysentence.global.exception.exception.BaseException;
import today.todaysentence.global.exception.exception.ExceptionCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static today.todaysentence.global.exception.exception.ExceptionCode.FAILED_CONVERT_FILE;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String upload(MultipartFile multipartFile) {
        File file = convertToFile(multipartFile)
                .orElseThrow(() -> new BaseException(FAILED_CONVERT_FILE));

        return uploadFile(file);
    }

    private Optional<File> convertToFile(MultipartFile multipartFile) {
        try {
            File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));

            if (!file.createNewFile()) {
                throw new IOException("파일 변환 실패");
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            }

            return Optional.of(file);
        } catch (IOException e) {
            throw new BaseException(FAILED_CONVERT_FILE);
        }
    }

    private String uploadFile(File file) {
        String fileName = makeFileName(file);

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileName)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        file.delete();
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    private String makeFileName(File file) {
        return UUID.randomUUID() + file.getName();
    }

    public void remove(String profileUrl) {
        if (!s3Client.doesObjectExist(bucketName, profileUrl)) {
            return;
        }

        s3Client.deleteObject(bucketName, profileUrl);
    }
}
