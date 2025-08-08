# MinIO 下载问题诊断指南

## 问题分析

你遇到的下载错误可能有以下几个原因：

1. **文件不存在** - 文件 `tujpg2.png` 在 bucket `test1` 中不存在
2. **Bucket不存在** - bucket `test1` 不存在
3. **权限问题** - 没有访问该文件的权限
4. **路径问题** - 本地路径 `D:/minio_download` 无法创建或写入
5. **MinIO服务器连接问题** - 无法连接到MinIO服务器

## 诊断步骤

### 1. 检查MinIO服务连接

```bash
GET http://localhost:8080/api/minio/health
```

### 2. 检查Bucket是否存在

首先检查 bucket `test1` 是否存在。你可以通过以下方式：

- 访问 MinIO 控制台：http://localhost:9001
- 使用用户名：minioadmin，密码：minioadmin

### 3. 检查文件是否存在

使用新的检查接口：

```bash
GET http://localhost:8080/api/minio/exists?bucketName=test1&objectName=tujpg2.png
```

### 4. 获取文件信息

```bash
GET http://localhost:8080/api/minio/fileInfo?bucketName=test1&objectName=tujpg2.png
```

## 常见解决方案

### 1. 如果文件不存在

先上传一个测试文件：

```bash
POST http://localhost:8080/api/minio/upload
Content-Type: multipart/form-data

Body:
- file: [选择一个图片文件]
- bucketName: test1
- objectName: tujpg2.png
```

### 2. 如果Bucket不存在

Bucket会在上传时自动创建，或者你可以先上传一个文件来创建bucket。

### 3. 如果路径问题

尝试使用不同的本地路径：

```bash
GET http://localhost:8080/api/minio/download?bucketName=test1&objectName=tujpg2.png&localFilePath=C:/temp/test.png
```

### 4. 检查日志

查看应用日志，会显示详细的错误信息：

```bash
# 在应用启动时查看控制台输出
# 或者查看日志文件
```

## 测试用例

### 完整测试流程

1. **健康检查**
   ```bash
   GET http://localhost:8080/api/minio/health
   ```

2. **上传测试文件**
   ```bash
   POST http://localhost:8080/api/minio/upload
   Content-Type: multipart/form-data
   
   Body:
   - file: [选择一个小图片文件]
   - bucketName: test1
   - objectName: test.jpg
   ```

3. **检查文件是否存在**
   ```bash
   GET http://localhost:8080/api/minio/exists?bucketName=test1&objectName=test.jpg
   ```

4. **获取文件信息**
   ```bash
   GET http://localhost:8080/api/minio/fileInfo?bucketName=test1&objectName=test.jpg
   ```

5. **下载文件**
   ```bash
   GET http://localhost:8080/api/minio/download?bucketName=test1&objectName=test.jpg&localFilePath=C:/temp/downloaded_test.jpg
   ```

## 预期结果

### 成功响应示例

**健康检查：**
```json
{
  "success": true,
  "message": "MinIO服务正常运行",
  "timestamp": 1754552689052,
  "status": "UP"
}
```

**文件存在检查：**
```json
{
  "success": true,
  "message": "文件存在",
  "data": {
    "bucketName": "test1",
    "objectName": "tujpg2.png",
    "size": 1024,
    "lastModified": "2023-12-21T10:30:00Z",
    "etag": "abc123",
    "contentType": "image/png"
  }
}
```

**下载成功：**
```json
{
  "success": true,
  "message": "下载成功，文件大小: 1024 bytes",
  "data": {
    "bucketName": "test1",
    "objectName": "tujpg2.png",
    "localFilePath": "C:/temp/downloaded_file"
  }
}
```

## 故障排除

### 1. 检查MinIO服务器状态

确保MinIO服务器正在运行：
```bash
# 如果使用Docker
docker ps | grep minio

# 检查端口是否开放
netstat -an | grep 9000
```

### 2. 检查网络连接

```bash
# 测试MinIO连接
curl http://localhost:9000/minio/health/live
```

### 3. 检查文件权限

确保应用有权限写入本地目录。

### 4. 查看详细日志

重启应用并查看控制台输出，会显示详细的错误信息。

## 联系支持

如果问题仍然存在，请提供：

1. 完整的错误日志
2. MinIO服务器版本
3. 操作系统信息
4. 具体的测试步骤和结果 