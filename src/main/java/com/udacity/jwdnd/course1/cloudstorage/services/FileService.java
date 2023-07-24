package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    private FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public int createFile(String name, String contentType, byte[] data, User user) {
        return fileMapper.insertFile(new File(null, name, contentType, String.valueOf(data.length), user.getUserId(), data));
    }

    public List<File> getFilesFor(User user) {
        return fileMapper.getFilesFor(user.getUserId());
    }
}
