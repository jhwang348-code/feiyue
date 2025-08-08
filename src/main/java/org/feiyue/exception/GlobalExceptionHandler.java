package org.feiyue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 用于统一处理应用中的各种异常，返回标准化的错误响应
 * 
 * @author feiyue
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义MinioException异常
     * 
     * @param e MinioException异常
     * @return 标准化的错误响应
     */
    @ExceptionHandler(MinioException.class)
    public ResponseEntity<Map<String, Object>> handleMinioException(MinioException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", e.getErrorCode());
        errorResponse.put("message", e.getMessage());
        errorResponse.put("detail", e.getDetail());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        // 根据错误码设置不同的HTTP状态码
        HttpStatus status = getHttpStatusByErrorCode(e.getErrorCode());
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    /**
     * 处理MinIO官方异常
     * 
     * @param e io.minio.errors.MinioException异常
     * @return 标准化的错误响应
     */
    @ExceptionHandler(io.minio.errors.MinioException.class)
    public ResponseEntity<Map<String, Object>> handleOfficialMinioException(io.minio.errors.MinioException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", "MINIO_OFFICIAL_ERROR");
        errorResponse.put("message", "MinIO操作失败");
        errorResponse.put("detail", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理文件上传大小超限异常
     * 
     * @param e MaxUploadSizeExceededException异常
     * @return 标准化的错误响应
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", "FILE_SIZE_EXCEEDED");
        errorResponse.put("message", "文件大小超过限制");
        errorResponse.put("detail", "上传的文件大小超过了系统允许的最大值");
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }

    /**
     * 处理IO异常
     * 
     * @param e IOException异常
     * @return 标准化的错误响应
     */
    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(java.io.IOException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", "IO_ERROR");
        errorResponse.put("message", "文件操作失败");
        errorResponse.put("detail", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理安全相关异常
     * 
     * @param e 安全异常
     * @return 标准化的错误响应
     */
    @ExceptionHandler({java.security.InvalidKeyException.class, java.security.NoSuchAlgorithmException.class})
    public ResponseEntity<Map<String, Object>> handleSecurityException(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", "SECURITY_ERROR");
        errorResponse.put("message", "安全验证失败");
        errorResponse.put("detail", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * 处理XML解析异常
     * 
     * @param e XmlPullParserException异常
     * @return 标准化的错误响应
     */
    @ExceptionHandler(org.xmlpull.v1.XmlPullParserException.class)
    public ResponseEntity<Map<String, Object>> handleXmlPullParserException(org.xmlpull.v1.XmlPullParserException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", "XML_PARSE_ERROR");
        errorResponse.put("message", "XML解析失败");
        errorResponse.put("detail", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理其他未预期的异常
     * 
     * @param e Exception异常
     * @return 标准化的错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("errorCode", "UNKNOWN_ERROR");
        errorResponse.put("message", "系统内部错误");
        errorResponse.put("detail", e.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 根据错误码获取对应的HTTP状态码
     * 
     * @param errorCode 错误码
     * @return HTTP状态码
     */
    private HttpStatus getHttpStatusByErrorCode(String errorCode) {
        switch (errorCode) {
            case MinioException.FILE_NOT_FOUND_ERROR_CODE:
            case MinioException.BUCKET_NOT_FOUND_ERROR_CODE:
                return HttpStatus.NOT_FOUND;
            case MinioException.PERMISSION_ERROR_CODE:
                return HttpStatus.FORBIDDEN;
            case MinioException.CONNECTION_ERROR_CODE:
                return HttpStatus.SERVICE_UNAVAILABLE;
            case MinioException.FILE_SIZE_EXCEEDED_ERROR_CODE:
            case MinioException.UNSUPPORTED_FORMAT_ERROR_CODE:
                return HttpStatus.BAD_REQUEST;
            case MinioException.UPLOAD_ERROR_CODE:
            case MinioException.DOWNLOAD_ERROR_CODE:
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
} 