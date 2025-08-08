package org.feiyue.exception;

/**
 * MinIO操作异常类
 * 用于处理MinIO文件操作过程中的业务异常
 * 
 * @author feiyue
 * @since 1.0.0
 */
public class MinioException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final String errorCode;
    
    /**
     * 详细描述
     */
    private final String detail;
    
    /**
     * 默认错误码
     */
    public static final String DEFAULT_ERROR_CODE = "MINIO_ERROR";
    
    /**
     * 文件上传失败错误码
     */
    public static final String UPLOAD_ERROR_CODE = "MINIO_UPLOAD_ERROR";
    
    /**
     * 文件下载失败错误码
     */
    public static final String DOWNLOAD_ERROR_CODE = "MINIO_DOWNLOAD_ERROR";
    
    /**
     * 文件不存在错误码
     */
    public static final String FILE_NOT_FOUND_ERROR_CODE = "MINIO_FILE_NOT_FOUND";
    
    /**
     * Bucket不存在错误码
     */
    public static final String BUCKET_NOT_FOUND_ERROR_CODE = "MINIO_BUCKET_NOT_FOUND";
    
    /**
     * 连接失败错误码
     */
    public static final String CONNECTION_ERROR_CODE = "MINIO_CONNECTION_ERROR";
    
    /**
     * 权限不足错误码
     */
    public static final String PERMISSION_ERROR_CODE = "MINIO_PERMISSION_ERROR";
    
    /**
     * 文件格式不支持错误码
     */
    public static final String UNSUPPORTED_FORMAT_ERROR_CODE = "MINIO_UNSUPPORTED_FORMAT";
    
    /**
     * 文件大小超限错误码
     */
    public static final String FILE_SIZE_EXCEEDED_ERROR_CODE = "MINIO_FILE_SIZE_EXCEEDED";

    /**
     * 构造函数 - 使用默认错误码
     * 
     * @param message 错误消息
     */
    public MinioException(String message) {
        super(message);
        this.errorCode = DEFAULT_ERROR_CODE;
        this.detail = message;
    }

    /**
     * 构造函数 - 指定错误码和消息
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     */
    public MinioException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.detail = message;
    }

    /**
     * 构造函数 - 指定错误码、消息和详细描述
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     * @param detail 详细描述
     */
    public MinioException(String errorCode, String message, String detail) {
        super(message);
        this.errorCode = errorCode;
        this.detail = detail;
    }

    /**
     * 构造函数 - 包含原始异常
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     * @param cause 原始异常
     */
    public MinioException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.detail = message;
    }

    /**
     * 构造函数 - 包含原始异常和详细描述
     * 
     * @param errorCode 错误码
     * @param message 错误消息
     * @param detail 详细描述
     * @param cause 原始异常
     */
    public MinioException(String errorCode, String message, String detail, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.detail = detail;
    }

    /**
     * 获取错误码
     * 
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 获取详细描述
     * 
     * @return 详细描述
     */
    public String getDetail() {
        return detail;
    }

    /**
     * 创建文件上传失败异常
     * 
     * @param fileName 文件名
     * @param cause 原始异常
     * @return MinioException
     */
    public static MinioException uploadFailed(String fileName, Throwable cause) {
        return new MinioException(
            UPLOAD_ERROR_CODE,
            "文件上传失败：" + fileName,
            "文件 " + fileName + " 上传到MinIO服务器时发生错误",
            cause
        );
    }

    /**
     * 创建文件下载失败异常
     * 
     * @param fileName 文件名
     * @param cause 原始异常
     * @return MinioException
     */
    public static MinioException downloadFailed(String fileName, Throwable cause) {
        return new MinioException(
            DOWNLOAD_ERROR_CODE,
            "文件下载失败：" + fileName,
            "从MinIO服务器下载文件 " + fileName + " 时发生错误",
            cause
        );
    }

    /**
     * 创建文件不存在异常
     * 
     * @param fileName 文件名
     * @return MinioException
     */
    public static MinioException fileNotFound(String fileName) {
        return new MinioException(
            FILE_NOT_FOUND_ERROR_CODE,
            "文件不存在：" + fileName,
            "在MinIO服务器中未找到文件 " + fileName
        );
    }

    /**
     * 创建Bucket不存在异常
     * 
     * @param bucketName Bucket名称
     * @return MinioException
     */
    public static MinioException bucketNotFound(String bucketName) {
        return new MinioException(
            BUCKET_NOT_FOUND_ERROR_CODE,
            "Bucket不存在：" + bucketName,
            "在MinIO服务器中未找到Bucket " + bucketName
        );
    }

    /**
     * 创建连接失败异常
     * 
     * @param endpoint 端点地址
     * @param cause 原始异常
     * @return MinioException
     */
    public static MinioException connectionFailed(String endpoint, Throwable cause) {
        return new MinioException(
            CONNECTION_ERROR_CODE,
            "连接MinIO服务器失败：" + endpoint,
            "无法连接到MinIO服务器 " + endpoint + "，请检查网络连接和服务器状态",
            cause
        );
    }

    /**
     * 创建权限不足异常
     * 
     * @param operation 操作类型
     * @return MinioException
     */
    public static MinioException permissionDenied(String operation) {
        return new MinioException(
            PERMISSION_ERROR_CODE,
            "权限不足：" + operation,
            "执行操作 " + operation + " 时权限不足，请检查访问凭证"
        );
    }

    /**
     * 创建文件格式不支持异常
     * 
     * @param fileName 文件名
     * @param supportedFormats 支持的文件格式
     * @return MinioException
     */
    public static MinioException unsupportedFormat(String fileName, String supportedFormats) {
        return new MinioException(
            UNSUPPORTED_FORMAT_ERROR_CODE,
            "不支持的文件格式：" + fileName,
            "文件 " + fileName + " 的格式不被支持，支持的文件格式：" + supportedFormats
        );
    }

    /**
     * 创建文件大小超限异常
     * 
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param maxSize 最大允许大小
     * @return MinioException
     */
    public static MinioException fileSizeExceeded(String fileName, long fileSize, long maxSize) {
        return new MinioException(
            FILE_SIZE_EXCEEDED_ERROR_CODE,
            "文件大小超限：" + fileName,
            "文件 " + fileName + " 大小 " + fileSize + " 字节超过了最大允许大小 " + maxSize + " 字节"
        );
    }

    @Override
    public String toString() {
        return "MinioException{" +
                "errorCode='" + errorCode + '\'' +
                ", message='" + getMessage() + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
