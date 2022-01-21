package com.nfo.core.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

@Service
public class FirebaseFileService implements IFirebaseFileService {

    @Autowired
    Properties properties;

    @EventListener
    public void init(ApplicationReadyEvent event) {

        // initialize Firebase

        try {

            ClassPathResource serviceAccount = new ClassPathResource("firebase.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                    .setStorageBucket(properties.bucketName)
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (Exception ex) {

            ex.printStackTrace();

        }
    }

    @Override
    public String getFileUrl(String name) {
        return String.format(properties.fileUrl, name);
    }

    @Override
    public String getDownloadUrl(String name, String ref) {
        return "https://firebasestorage.googleapis.com/v0/b/englishcenter-2021.appspot.com/o/" + ref + "%2F" + name + "?alt=media";
    }

    @Override
    public String save(MultipartFile file) throws IOException {

        Bucket bucket = StorageClient.getInstance().bucket();

        String name = generateFileName(file.getOriginalFilename());

        bucket.create(name, file.getBytes(), file.getContentType());

        return name;
    }

    @Override
    public String saveFromUrl(String imagePath, String name) throws IOException {

        Bucket bucket = StorageClient.getInstance().bucket();
        URL url = new URL(imagePath);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", "Firefox");

        try (InputStream inputStream = conn.getInputStream()) {
            int n = 0;
            byte[] buffer = new byte[1024];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        }
        byte[] img = output.toByteArray();

        bucket.create(name, img);

        return name;
    }

    @Override
    public String save(BufferedImage bufferedImage, String originalFileName) throws IOException {

        byte[] bytes = getByteArrays(bufferedImage, getExtension(originalFileName));

        Bucket bucket = StorageClient.getInstance().bucket();

        String name = generateFileName(originalFileName);

        bucket.create(name, bytes);

        return name;
    }

    @Override
    public String save(File file, String originalFileName) throws IOException {

        InputStream inputStream = new FileInputStream(file);

        Bucket bucket = StorageClient.getInstance().bucket();

        String name = "exports/" + originalFileName;

        bucket.create(name, inputStream);

        return name;
    }

    @Override
    public void delete(String name) throws IOException {

        Bucket bucket = StorageClient.getInstance().bucket();

        if (StringUtils.isEmpty(name)) {
            throw new IOException("invalid file name");
        }

        Blob blob = bucket.get(name);

        if (blob == null) {
            throw new IOException("file not found");
        }

        blob.delete();
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "firebase")
    public class Properties {

        private String bucketName;

        private String fileUrl;
    }

}