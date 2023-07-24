package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.mapper.CredentialMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class CredentialService {
    private CredentialMapper credentialMapper;
    private EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper, EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    public int createCredential(Credential credential, User user) {
        credential.setUserId(user.getUserId());

        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), encodedKey);

        credential.setKey(encodedKey);
        credential.setPassword(encryptedPassword);

        return credentialMapper.insertCredential(credential);
    }

    public List<Credential> getCredentialsFor(User user) {
        int userId = user.getUserId();
        return credentialMapper.getCredentialsFor(userId);
    }

    public void updateCredential(Credential credential, User user) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        String encryptedPassword = encryptionService.encryptValue(credential.getPassword(), encodedKey);

        credential.setKey(encodedKey);
        credential.setPassword(encryptedPassword);
        credential.setUserId(user.getUserId());

        credentialMapper.updateCredential(credential);
    }

    public int deleteNote(int credentialId) {
        return credentialMapper.deleteCredential(credentialId);
    }
}
