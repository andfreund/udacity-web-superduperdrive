package com.udacity.jwdnd.course1.cloudstorage.mapper;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CredentialMapper {
    @Insert("INSERT INTO CREDENTIALS (url, username, key, password, userid) " +
            "VALUES(#{url}, #{username}, #{key}, #{password}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    int insertCredential(Credential credential);

    @Select("SELECT * FROM CREDENTIALS WHERE (userid = #{userId})")
    List<Credential> getCredentialsFor(int userId);

    @Update("UPDATE CREDENTIALS SET url = #{url}, username = #{username}, key = #{key}, password = #{password}, userid = #{userId}" +
            "WHERE (credentialid = #{credentialId})")
    void updateCredential(Credential credential);

    @Delete("DELETE FROM CREDENTIALS WHERE (credentialid = #{credentialId})")
    int deleteCredential(int credentialId);
}
