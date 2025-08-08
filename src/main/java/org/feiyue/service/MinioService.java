package org.feiyue.service;

import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.feiyue.exception.MinioException;

@Service
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);
    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    // 检查并创建 Bucket
    public void checkAndCreateBucket(String bucketName) throws MinioException {
        logger.info("检查Bucket是否存在: {}", bucketName);
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                logger.info("Bucket不存在，正在创建: {}", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("Bucket创建成功: {}", bucketName);
            } else {
                logger.info("Bucket已存在: {}", bucketName);
            }
        } catch (Exception e) {
            logger.error("Bucket操作失败: {}", bucketName, e);
            throw MinioException.bucketNotFound(bucketName);
        }
    }

    // 上传文件
    public String uploadFile(String bucketName, String objectName, InputStream inputStream) throws MinioException {
        try {
            checkAndCreateBucket(bucketName);
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
            return "上传成功";
        } catch (Exception e) {
            throw MinioException.uploadFailed(objectName, e);
        }
    }

    // 获取文件信息
    public StatObjectResponse getFileInfo(String bucketName, String objectName) throws MinioException {
        try {
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw MinioException.fileNotFound(objectName);
        }
    }

    // 下载文件到本地
    public String downloadFile(String bucketName, String objectName, String localFilePath) throws MinioException {
        logger.info("开始下载文件: bucket={}, object={}, localPath={}", bucketName, objectName, localFilePath);
        
        // 1. 检查文件是否存在
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            logger.info("文件存在，大小: {} bytes", stat.size());
        } catch (Exception e) {
            logger.error("文件不存在或无法访问: bucket={}, object={}", bucketName, objectName, e);
            throw MinioException.fileNotFound(objectName);
        }
        
        // 2. 处理本地路径
        Path path = Paths.get(localFilePath);
        
        // 如果路径是目录，则使用原始文件名
        if (Files.isDirectory(path) || localFilePath.endsWith("/") || localFilePath.endsWith("\\")) {
            String fileName = objectName;
            // 如果objectName包含路径，只取文件名部分
            if (objectName.contains("/")) {
                fileName = objectName.substring(objectName.lastIndexOf("/") + 1);
            }
            path = path.resolve(fileName);
            logger.info("检测到目录路径，使用文件名: {}", fileName);
        }
        
        // 3. 创建本地目录
        try {
            Path parentDir = path.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                logger.info("创建目录: {}", parentDir);
            }
        } catch (Exception e) {
            logger.error("无法创建目录: {}", path.getParent(), e);
            throw MinioException.downloadFailed(objectName, new IOException("无法创建目录: " + path.getParent(), e));
        }
        
        // 4. 检查文件是否已存在，如果存在则重命名
        Path finalPath = path;
        int counter = 1;
        while (Files.exists(finalPath)) {
            String fileName = path.getFileName().toString();
            String nameWithoutExt = fileName;
            String extension = "";
            
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0) {
                nameWithoutExt = fileName.substring(0, lastDotIndex);
                extension = fileName.substring(lastDotIndex);
            }
            
            finalPath = path.getParent().resolve(nameWithoutExt + "_" + counter + extension);
            counter++;
            logger.info("文件已存在，重命名为: {}", finalPath.getFileName());
        }
        
        // 5. 下载文件是
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
             OutputStream outputStream = Files.newOutputStream(finalPath)) {

            byte[] buf = new byte[8192]; // 增加缓冲区大小
            int bytesRead;
            long totalBytes = 0;
            
            while ((bytesRead = stream.read(buf)) != -1) {
                outputStream.write(buf, 0, bytesRead);
                totalBytes += bytesRead;
            }
            
            logger.info("文件下载完成: {}, 总字节数: {}", finalPath, totalBytes);
            return "下载成功，文件大小: " + totalBytes + " bytes，保存路径: " + finalPath;
        } catch (Exception e) {
            logger.error("文件下载失败: bucket={}, object={}, localPath={}", bucketName, objectName, finalPath, e);
            throw MinioException.downloadFailed(objectName, e);
        }
    }
}