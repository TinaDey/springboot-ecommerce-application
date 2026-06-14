package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile image) throws IOException {

        //File names of the original File
        String originalFileName= image.getOriginalFilename();

        //Generate a Unique File Name
        String randomId= UUID.randomUUID().toString();
        String fileName= randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        String filePath= path+ File.separator+fileName;

        //Check if path exists and Create
        File folder= new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }

        //Upload to server
        Files.copy(image.getInputStream(), Paths.get(filePath));

        //Return the file name
        return fileName;
    }

}
