@echo off
chcp 65001
echo 启动 MinIO 文件管理服务...
echo 设置字符编码为 UTF-8...

mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dfile.encoding=UTF-8 -Duser.language=zh -Duser.region=CN"

pause 