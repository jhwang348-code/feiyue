package org.feiyue.controller;

import io.minio.StatObjectResponse;
import io.minio.GetObjectArgs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.feiyue.service.MinioService;
import org.feiyue.exception.MinioException;

@RestController
@RequestMapping("/minio")
public class MinioController {

    private final MinioService minioService;
    private final io.minio.MinioClient minioClient;

    public MinioController(MinioService minioService, io.minio.MinioClient minioClient) {
        this.minioService = minioService;
        this.minioClient = minioClient;
    }

    // 健康检查接口
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "MinIO服务正常运行");
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", "UP");
        return ResponseEntity.ok(response);
    }

    // 检查文件是否存在接口
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Object>> checkFileExists(@RequestParam("bucketName") String bucketName,
                                                               @RequestParam("objectName") String objectName) {
        try {
            StatObjectResponse fileInfo = minioService.getFileInfo(bucketName, objectName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "文件存在");
            response.put("data", Map.of(
                "bucketName", fileInfo.bucket(),
                "objectName", fileInfo.object(),
                "size", fileInfo.size(),
                "lastModified", fileInfo.lastModified(),
                "etag", fileInfo.etag(),
                "contentType", fileInfo.contentType()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "文件不存在或无法访问");
            response.put("error", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }

    // 上传接口，接收 MultipartFile
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("bucketName") String bucketName,
                                                      @RequestParam("objectName") String objectName) {
        try (InputStream inputStream = file.getInputStream()) {
            String result = minioService.uploadFile(bucketName, objectName, inputStream);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("data", Map.of(
                "bucketName", bucketName,
                "objectName", objectName,
                "fileName", file.getOriginalFilename(),
                "fileSize", file.getSize()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 异常会被全局异常处理器捕获并处理
            throw MinioException.uploadFailed(file.getOriginalFilename(), e);
        }
    }

    // 获取文件信息接口
    @GetMapping("/fileInfo")
    public ResponseEntity<Map<String, Object>> getFileInfo(@RequestParam("bucketName") String bucketName,
                                                           @RequestParam("objectName") String objectName) {
        try {
            StatObjectResponse fileInfo = minioService.getFileInfo(bucketName, objectName);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "获取文件信息成功");
            response.put("data", Map.of(
                "bucketName", fileInfo.bucket(),
                "objectName", fileInfo.object(),
                "size", fileInfo.size(),
                "lastModified", fileInfo.lastModified(),
                "etag", fileInfo.etag(),
                "contentType", fileInfo.contentType()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 异常会被全局异常处理器捕获并处理
            throw MinioException.fileNotFound(objectName);
        }
    }

    // 下载文件接口（这里示例是下载到本地指定路径，实际可根据需求调整返回，比如返回文件流给前端直接下载）
    @GetMapping("/download")
    public ResponseEntity<Map<String, Object>> downloadFile(@RequestParam("bucketName") String bucketName,
                                                            @RequestParam("objectName") String objectName,
                                                            @RequestParam("localFilePath") String localFilePath) {
        try {
            String result = minioService.downloadFile(bucketName, objectName, localFilePath);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", result);
            response.put("data", Map.of(
                "bucketName", bucketName,
                "objectName", objectName,
                "localFilePath", localFilePath
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 异常会被全局异常处理器捕获并处理
            throw MinioException.downloadFailed(objectName, e);
        }
    }

    // 直接下载文件流接口（用于浏览器直接下载）
    @GetMapping("/download/stream")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFileStream(
            @RequestParam("bucketName") String bucketName,
            @RequestParam("objectName") String objectName) {
        try {
            // 获取文件信息
            StatObjectResponse fileInfo = minioService.getFileInfo(bucketName, objectName);
            
            // 获取文件流杀杀杀
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            
            // 创建Resource
            org.springframework.core.io.Resource resource = new org.springframework.core.io.InputStreamResource(inputStream) {
                @Override
                public long contentLength() {
                    return fileInfo.size();
                }
            };
            
            // 设置文件名
            String fileName = objectName;
            if (objectName.contains("/")) {
                fileName = objectName.substring(objectName.lastIndexOf("/") + 1);
            }
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Content-Type", fileInfo.contentType())
                    .header("Content-Length", String.valueOf(fileInfo.size()))
                    .body(resource);
                    
        } catch (Exception e) {
            throw MinioException.downloadFailed(objectName, e);
        }
    }
}