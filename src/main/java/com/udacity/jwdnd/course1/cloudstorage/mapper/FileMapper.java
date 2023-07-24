package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {
    @Insert("INSERT INTO FILES (filename, contenttype, filesize, userid, filedata) " +
            "VALUES(#{fileName}, #{contentType}, #{fileSize}, #{userId}, #{fileData})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insertFile(File file);

    @Select("SELECT * FROM FILES WHERE (userid = #{userId})")
    List<File> getFilesFor(int userId);

    @Select("SELECT * FROM FILES WHERE (fileid = #{fileId})")
    File getFile(int fileId);

    @Select("SELECT * FROM FILES WHERE (filename = #{fileName})")
    File getFileByName(String fileName);

    @Delete("DELETE FROM FILES WHERE (fileid = #{fileId})")
    int deleteFile(int fileId);
}
