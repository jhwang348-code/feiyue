# 下载问题解决方案

## 问题分析

你遇到的错误是：
```
java.nio.file.AccessDeniedException: D:\minio_download
```

这是因为：
1. **路径问题**：`D:/minio_download` 被当作文件名，而不是目录
2. **权限问题**：应用没有权限在 D 盘根目录创建文件

## 解决方案

### 方案1：使用正确的路径格式

**错误的路径：**
```
D:/minio_download
```

**正确的路径：**
```
C:/temp/minio_download/tujpg2.png
```

### 方案2：使用目录路径（推荐）

现在代码已经改进，支持目录路径。你可以这样使用：

```
GET http://localhost:8080/api/minio/download?bucketName=test1&objectName=tujpg2.png&localFilePath=C:/temp/minio_download
```

代码会自动：
1. 检测这是一个目录路径
2. 使用原始文件名 `tujpg2.png`
3. 最终保存为 `C:/temp/minio_download/tujpg2.png`

### 方案3：使用浏览器直接下载

新增了流式下载接口，可以直接在浏览器中下载：

```
GET http://localhost:8080/api/minio/download/stream?bucketName=test1&objectName=tujpg2.png
```

## 测试步骤

### 1. 重新编译并启动服务

```bash
mvn clean compile
mvn spring-boot:run
```

### 2. 测试正确的下载路径

使用 Postman 测试：

```
GET http://localhost:8080/api/minio/download?bucketName=test1&objectName=tujpg2.png&localFilePath=C:/temp/minio_download
```

### 3. 测试浏览器下载

```
GET http://localhost:8080/api/minio/download/stream?bucketName=test1&objectName=tujpg2.png
```

### 4. 使用Web界面测试

访问：http://localhost:8080/api/

在下载部分：
- Bucket名称：`test1`
- 对象名称：`tujpg2.png`
- 本地保存路径：`C:/temp/minio_download`

## 预期结果

### 成功响应示例

```json
{
  "success": true,
  "message": "下载成功，文件大小: 512441 bytes，保存路径: C:\\temp\\minio_download\\tujpg2.png",
  "data": {
    "bucketName": "test1",
    "objectName": "tujpg2.png",
    "localFilePath": "C:/temp/minio_download"
  }
}
```

## 改进功能

### 1. 智能路径处理
- 自动检测目录路径
- 自动使用原始文件名
- 自动创建目录

### 2. 文件重命名
- 如果文件已存在，自动重命名
- 格式：`filename_1.ext`, `filename_2.ext`

### 3. 详细日志
- 显示下载进度
- 显示文件大小
- 显示最终保存路径

### 4. 多种下载方式
- 本地文件下载
- 浏览器流式下载

## 常见路径示例

| 输入路径 | 实际保存路径 | 说明 |
|---------|-------------|------|
| `C:/temp/minio_download` | `C:/temp/minio_download/tujpg2.png` | 目录路径 |
| `C:/temp/minio_download/` | `C:/temp/minio_download/tujpg2.png` | 目录路径（带斜杠） |
| `C:/temp/minio_download/test.png` | `C:/temp/minio_download/test.png` | 完整文件路径 |
| `C:/temp/minio_download/test.png` | `C:/temp/minio_download/test_1.png` | 文件已存在时重命名 |

## 注意事项

1. **权限问题**：确保应用有权限写入目标目录
2. **磁盘空间**：确保有足够的磁盘空间
3. **路径格式**：Windows 系统可以使用 `/` 或 `\`
4. **目录创建**：应用会自动创建不存在的目录

## 故障排除

如果仍然遇到问题：

1. **检查目录权限**
   ```bash
   # 确保 C:/temp 目录存在且有写权限
   mkdir C:\temp
   ```

2. **使用管理员权限**
   - 以管理员身份运行应用
   - 或者使用用户目录：`C:/Users/YourUsername/Downloads/`

3. **检查日志**
   - 查看应用启动时的详细日志
   - 日志会显示具体的错误原因

4. **测试简单路径**
   ```bash
   # 先测试简单的路径
   GET http://localhost:8080/api/minio/download?bucketName=test1&objectName=tujpg2.png&localFilePath=C:/temp/test.png
   ``` 