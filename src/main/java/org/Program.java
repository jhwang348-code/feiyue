package org;

import io.minio.*;
import io.minio.errors.MinioException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Program {
    public static void main(String[] args) {
        System.out.println("hello java");
        try {
            test1();
        } catch (Exception e) {
            System.out.println("test1错误: " + e);
        }
    }

    public static void test1() throws NoSuchAlgorithmException, IOException,
            InvalidKeyException, XmlPullParserException {
        try {
            // 初始化客户端，xx修改测试
            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://127.0.0.1:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

            // 检查Bucket是否存在
            boolean isExist = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket("test1").build()
            );

            if (isExist) {
                System.out.println("test1文件夹已经存在了");
            } else {
                // 创建Bucket
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket("test1").build()
                );
                System.out.println("test1文件夹已创建");
            }

            // 上传文件（使用文件流方式，兼容8.5.2版本）
            try (FileInputStream fileInputStream = new FileInputStream("D:/test_WeChat.jpg")) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket("test1")
                                .object("tujpg2.png")
                                .stream(fileInputStream, fileInputStream.available(), -1)
                                .build()
                );
            }
            System.out.println("上传成功了");

            // 获取文件信息
            StatObjectResponse fileStat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket("test1")
                            .object("tujpg2.png")
                            .build()
            );
            System.out.println("文件名：" + fileStat.object() + ", 文件大小:" + fileStat.size());

            // 下载文件（通过流写入本地文件，兼容8.5.2版本）
            try (InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("test1")
                            .object("tujpg2.png")
                            .build());
                 OutputStream outputStream = Files.newOutputStream(Paths.get("D:/tujpg_minio.png"))) {

                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = stream.read(buf)) != -1) {
                    outputStream.write(buf, 0, bytesRead);
                }
            }
            System.out.println("下载成功了");

        } catch (MinioException e) {
            // 修正异常处理，移除不存在的errorResponse()方法
            System.out.println("MinIO错误信息: " + e.getMessage());
        }
    }
}
