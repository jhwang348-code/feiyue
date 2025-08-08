# MinIO 文件管理服务

基于 Spring Boot 框架的 MinIO 文件操作服务，提供文件上传、下载、信息查询等功能。

## 功能特性

- ✅ 文件上传到 MinIO
- ✅ 文件下载到本地
- ✅ 获取文件信息
- ✅ 自定义异常处理
- ✅ 全局异常处理器
- ✅ 健康检查接口
- ✅ Web 测试界面

## 技术栈

- Spring Boot 3.2.0
- MinIO Java SDK 8.5.2
- Java 21
- Maven

## 快速开始

### 1. 环境要求

- JDK 21+
- Maven 3.6+
- MinIO 服务器（可选，用于测试）

### 2. 启动 MinIO 服务器（可选）

如果你没有 MinIO 服务器，可以使用 Docker 快速启动一个：

```bash
docker run -p 9000:9000 -p 9001:9001 --name minio \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v minio_data:/data \
  quay.io/minio/minio server /data --console-address ":9001"
```

### 3. 配置

编辑 `src/main/resources/application.yml` 文件，修改 MinIO 连接配置：

```yaml
minio:
  endpoint: http://127.0.0.1:9000  # MinIO 服务器地址
  access-key: minioadmin           # 访问密钥
  secret-key: minioadmin           # 秘密密钥
  bucket-name: default-bucket      # 默认 bucket 名称
```

### 4. 启动服务

```bash
# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

服务启动后，访问：
- 测试页面：http://localhost:8080/api/
- 健康检查：http://localhost:8080/api/minio/health

## API 接口

### 1. 健康检查

```
GET /api/minio/health
```

**响应示例：**
```json
{
  "success": true,
  "message": "MinIO服务正常运行",
  "timestamp": 1703123456789,
  "status": "UP"
}
```

### 2. 文件上传

```
POST /api/minio/upload
```

**参数：**
- `file`: 要上传的文件（MultipartFile）
- `bucketName`: 存储桶名称
- `objectName`: 对象名称

**响应示例：**
```json
{
  "success": true,
  "message": "上传成功",
  "data": {
    "bucketName": "default-bucket",
    "objectName": "test.jpg",
    "fileName": "test.jpg",
    "fileSize": 1024
  }
}
```

### 3. 获取文件信息

```
GET /api/minio/fileInfo?bucketName={bucketName}&objectName={objectName}
```

**响应示例：**
```json
{
  "success": true,
  "message": "获取文件信息成功",
  "data": {
    "bucketName": "default-bucket",
    "objectName": "test.jpg",
    "size": 1024,
    "lastModified": "2023-12-21T10:30:00Z",
    "etag": "abc123",
    "contentType": "image/jpeg"
  }
}
```

### 4. 文件下载

```
GET /api/minio/download?bucketName={bucketName}&objectName={objectName}&localFilePath={localFilePath}
```

**响应示例：**
```json
{
  "success": true,
  "message": "下载成功",
  "data": {
    "bucketName": "default-bucket",
    "objectName": "test.jpg",
    "localFilePath": "C:/temp/downloaded_file"
  }
}
```

## 异常处理

服务使用自定义的 `MinioException` 类处理各种异常情况：

- `MINIO_UPLOAD_ERROR`: 文件上传失败
- `MINIO_DOWNLOAD_ERROR`: 文件下载失败
- `MINIO_FILE_NOT_FOUND`: 文件不存在
- `MINIO_BUCKET_NOT_FOUND`: Bucket 不存在
- `MINIO_CONNECTION_ERROR`: 连接失败
- `MINIO_PERMISSION_ERROR`: 权限不足
- `MINIO_UNSUPPORTED_FORMAT`: 文件格式不支持
- `MINIO_FILE_SIZE_EXCEEDED`: 文件大小超限

**错误响应示例：**
```json
{
  "success": false,
  "errorCode": "MINIO_FILE_NOT_FOUND",
  "message": "文件不存在：test.jpg",
  "detail": "在MinIO服务器中未找到文件 test.jpg",
  "timestamp": 1703123456789
}
```

## 使用 Postman 测试

### 1. 健康检查
```
GET http://localhost:8080/api/minio/health
```

### 2. 文件上传
```
POST http://localhost:8080/api/minio/upload
Content-Type: multipart/form-data

Body (form-data):
- file: [选择文件]
- bucketName: default-bucket
- objectName: test.jpg
```

### 3. 获取文件信息
```
GET http://localhost:8080/api/minio/fileInfo?bucketName=default-bucket&objectName=test.jpg
```

### 4. 文件下载
```
GET http://localhost:8080/api/minio/download?bucketName=default-bucket&objectName=test.jpg&localFilePath=C:/temp/downloaded_file
```

## 项目结构

```
src/main/java/org/feiyue/
├── Application.java              # 启动类
├── config/
│   └── MinioConfig.java         # MinIO 配置
├── controller/
│   └── MinioController.java     # 控制器
├── exception/
│   ├── MinioException.java      # 自定义异常
│   └── GlobalExceptionHandler.java # 全局异常处理器
└── service/
    └── MinioService.java        # 业务逻辑
```

## 注意事项

1. 确保 MinIO 服务器正在运行且可访问
2. 检查网络连接和防火墙设置
3. 确保有足够的磁盘空间用于文件存储
4. 文件上传大小限制为 100MB（可在配置中修改）

## 故障排除

### 常见问题

1. **连接失败**
   - 检查 MinIO 服务器是否启动
   - 验证 endpoint 配置是否正确
   - 检查网络连接

2. **权限错误**
   - 验证 access-key 和 secret-key 是否正确
   - 检查 MinIO 用户权限设置

3. **文件上传失败**
   - 检查文件大小是否超过限制
   - 验证 bucket 是否存在
   - 检查磁盘空间

## 许可证

MIT License 