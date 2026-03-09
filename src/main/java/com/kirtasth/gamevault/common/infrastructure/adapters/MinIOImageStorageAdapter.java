package com.kirtasth.gamevault.common.infrastructure.adapters;

import com.kirtasth.gamevault.common.application.exception.ImageUploadException;
import com.kirtasth.gamevault.common.domain.ports.out.ImageStoragePort;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class MinIOImageStorageAdapter implements ImageStoragePort {

    @Value("${minio.url.external}")
    private String externalUrl;

    private final MinioClient client;

    @Value("minio.bucket-name")
    private String bucketName;

    @Override
    public String uploadAvatar(MultipartFile image, Long userId) {
        createBucketIfNotExists();
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object("users/" + userId + "/avatar")
                    .stream(new ByteArrayInputStream(image.getBytes()), image.getBytes().length, -1L)
                    .contentType("image/jpeg")
                    .build()
            );

            return externalUrl + "/" + bucketName + "/users/" + userId + "/avatar";
        } catch (Exception e) {
            throw new ImageUploadException(e.getMessage());
        }
    }

    @Override
    public String uploadGameMainImage(byte[] image, Long gameId) {
        createBucketIfNotExists();
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object("games/" + gameId + "/main")
                    .stream(new ByteArrayInputStream(image), image.length, -1L)
                    .contentType("image/jpeg")
                    .build()
            );

            return externalUrl + "/" + bucketName + "/games/" + gameId + "/main";
        } catch (Exception e) {
            throw new ImageUploadException(e.getMessage());
        }
    }

    private void createBucketIfNotExists() {
        try {
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                String policy = """
                        {
                          "Version": "2012-10-17",
                          "Statement": [
                            {
                              "Effect": "Allow",
                              "Principal": "*",
                              "Action": ["s3:GetObject"],
                              "Resource": ["arn:aws:s3:::%s/*"]
                            }
                          ]
                        }
                        """.formatted(bucketName);
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                client.setBucketPolicy(SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(policy)
                        .build());
            }
        } catch (Exception e) {
            throw new ImageUploadException(e.getMessage());
        }
    }
}
