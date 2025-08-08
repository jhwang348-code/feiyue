# 中文日志乱码解决方案

## 问题描述

日志中出现中文乱码：
```
寮€濮嬩笅杞芥枃浠? bucket=test1, object=tujpg2.png, localPath=D:/minio_download
鏂囦欢瀛樺湪锛屽ぇ灏? 512441 bytes
妫€娴嬪埌鐩綍璺緞锛屼娇鐢ㄦ枃浠跺悕: tujpg2.png
鏂囦欢涓嬭浇瀹屾垚: D:\minio_download\tujpg2.png, 鎬诲瓧鑺傛暟: 512441
```

## 解决方案

### 方案1：使用批处理文件启动（推荐）

我已经创建了 `run.bat` 文件，使用以下命令启动：

```bash
# 双击运行 run.bat 文件
# 或者在命令行中运行：
run.bat
```

这个批处理文件会：
1. 设置控制台编码为 UTF-8 (`chcp 65001`)
2. 设置 JVM 参数为 UTF-8 编码

### 方案2：手动设置 JVM 参数

在命令行中运行：

```bash
# Windows CMD
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dfile.encoding=UTF-8 -Duser.language=zh -Duser.region=CN"

# Windows PowerShell
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Dfile.encoding=UTF-8 -Duser.language=zh -Duser.region=CN"
```

### 方案3：设置环境变量

在系统环境变量中添加：

```
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
```

### 方案4：修改 IDE 设置

如果你使用 IntelliJ IDEA：

1. 打开 `File` → `Settings` → `Editor` → `File Encodings`
2. 设置：
   - `Global Encoding`: UTF-8
   - `Project Encoding`: UTF-8
   - `Default encoding for properties files`: UTF-8
   - 勾选 `Transparent native-to-ascii conversion`

3. 在运行配置中添加 VM 选项：
   ```
   -Dfile.encoding=UTF-8
   -Duser.language=zh
   -Duser.region=CN
   ```

## 验证解决方案

启动服务后，日志应该正常显示中文：

```
2025-08-07 16:57:03 [http-nio-8080-exec-3] INFO  org.feiyue.service.MinioService - 开始下载文件: bucket=test1, object=tujpg2.png, localPath=D:/minio_download
2025-08-07 16:57:03 [http-nio-8080-exec-3] INFO  org.feiyue.service.MinioService - 文件存在，大小: 512441 bytes
2025-08-07 16:57:03 [http-nio-8080-exec-3] INFO  org.feiyue.service.MinioService - 检测到目录路径，使用文件名: tujpg2.png
2025-08-07 16:57:03 [http-nio-8080-exec-3] INFO  org.feiyue.service.MinioService - 文件下载完成: D:\minio_download\tujpg2.png, 总字节数: 512441
```

## 常见问题

### 1. 如果仍然显示乱码

检查控制台编码：
```bash
# 查看当前编码
chcp

# 应该显示：活动代码页: 65001
```

### 2. 如果使用 PowerShell

PowerShell 可能需要额外设置：
```powershell
# 设置 PowerShell 编码
$OutputEncoding = [console]::InputEncoding = [console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

### 3. 如果使用 Git Bash

在 Git Bash 中运行：
```bash
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
mvn spring-boot:run
```

## 最佳实践

1. **推荐使用 `run.bat`**：这是最简单的解决方案
2. **设置 IDE 编码**：确保开发环境统一使用 UTF-8
3. **检查文件编码**：确保源代码文件保存为 UTF-8 格式

## 注意事项

- 修改编码设置后需要重新启动应用
- 不同的终端可能需要不同的设置方式
- 建议在项目开始时就设置好编码，避免后期出现问题 