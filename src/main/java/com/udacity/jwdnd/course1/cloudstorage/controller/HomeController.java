package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.Credential;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.*;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/home")
public class HomeController {
    private NoteService noteService;
    private UserService userService;
    private CredentialService credentialService;
    private EncryptionService encryptionService;
    private FileService fileService;

    public HomeController(NoteService noteService, UserService userService, CredentialService credentialService, EncryptionService encryptionService, FileService fileService) {
        this.noteService = noteService;
        this.userService = userService;
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
        this.fileService = fileService;
    }

    @GetMapping
    public String viewPage(Model model, Authentication authentication) {
        // TODO add everything to model
        setupNotesModel(model, authentication);
        setupCredentialsModel(model, authentication);
        return "home";
    }

    @PostMapping("/notes")
    public String addAndUpdateNotes(Note note, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        if (note.getNoteId() > 0) {
            // note already exists in DB -> edit
            noteService.updateNote(note, user);
        } else {
            // note doesn't exist in DB -> create
            int noteId = noteService.createNote(note, user);

            if (noteId < 0) {
                model.addAttribute("alertMessage", "Note creation failed!");
                model.addAttribute("alertError", true);
            } else {
                model.addAttribute("alertMessage", "Note successfully created!");
                model.addAttribute("alertSuccess", true);
            }
        }
        setupNotesModel(model, authentication);

        return "home";
    }

    @GetMapping("/notes/delete/{noteid}")
    public String deleteNote(@PathVariable("noteid") String noteId, Model model, Authentication authentication) {
        if (noteService.deleteNote(Integer.parseInt(noteId)) < 0) {
            model.addAttribute("alertMessage", "Note deletion failed!");
            model.addAttribute("alertError", true);
        } else {
            model.addAttribute("alertMessage", "Note successfully deleted!");
            model.addAttribute("alertSuccess", true);
        }
        setupNotesModel(model, authentication);

        return "home";
    }

    @PostMapping("/credentials")
    public String addAndUpdateCredentials(Credential credential, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        if (credential.getCredentialId() > 0) {
            // credential already exists in DB -> edit/view
            credentialService.updateCredential(credential, user);
        } else {
            // credential doesn't exist in Db -> create
            int credentialId = credentialService.createCredential(credential, user);

            if (credentialId < 0) {
                model.addAttribute("alertMessage", "Credential creation failed!");
                model.addAttribute("alertError", true);
            } else {
                model.addAttribute("alertMessage", "Credential successfully created!");
                model.addAttribute("alertSuccess", true);
            }
        }
        setupCredentialsModel(model, authentication);

        return "home";
    }

    @GetMapping("/credentials/delete/{credentialid}")
    public String deleteCredential(@PathVariable("credentialid") String credentialId, Model model, Authentication authentication) {
        if (credentialService.deleteNote(Integer.parseInt(credentialId)) < 0) {
            model.addAttribute("alertMessage", "Credential deletion failed!");
            model.addAttribute("alertError", true);
        } else {
            model.addAttribute("alertMessage", "Credential successfully deleted!");
            model.addAttribute("alertSuccess", true);
        }
        setupCredentialsModel(model, authentication);

        return "home";
    }

    @PostMapping("/files")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile fileUpload, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        byte[] fileData;
        String fileContentType;
        String fileName;

        try {
            fileName = fileUpload.getOriginalFilename();
            if (fileService.fileExists(fileName)) {
                throw new FileAlreadyExistsException(fileName);
            }

            InputStream fis = fileUpload.getInputStream();
            fileData = fis.readAllBytes();
            fileContentType = "application/octet-stream";
            if (fileName != null) {
                fileContentType = Files.probeContentType(Paths.get(fileName));
            }

            int fileId = fileService.createFile(fileName, fileContentType, fileData, user);
            if (fileId < 0) {
                throw new RuntimeException();
            }

            model.addAttribute("alertMessage", "File successfully uploaded!");
            model.addAttribute("alertSuccess", true);
        } catch (FileAlreadyExistsException e) {
            model.addAttribute("alertMessage", "File already exists!");
            model.addAttribute("alertError", true);
        } catch (IOException | RuntimeException e) {
            model.addAttribute("alertMessage", "File upload failed!");
            model.addAttribute("alertError", true);
        }

        model.addAttribute("files", fileService.getFilesFor(user));
        return "home";
    }

    @GetMapping("/files/view/{fileid}")
    public ResponseEntity<ByteArrayResource> viewFile(@PathVariable("fileid") String fileId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);
        File file = fileService.getFile(Integer.parseInt(fileId));
        model.addAttribute("files", fileService.getFilesFor(user));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(new ByteArrayResource(file.getFileData()));
    }

    @GetMapping("/files/delete/{fileid}")
    public String deleteFile(@PathVariable("fileid") String fileId, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);

        if (fileService.deleteFile(Integer.parseInt(fileId)) < 0) {
            model.addAttribute("alertMessage", "File deletion failed!");
            model.addAttribute("alertError", true);
        } else {
            model.addAttribute("alertMessage", "File successfully deleted!");
            model.addAttribute("alertSuccess", true);
        }

        model.addAttribute("files", fileService.getFilesFor(user));
        return "home";
    }

    private void setupNotesModel(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);
        model.addAttribute("notes",  noteService.getNotesFor(user));
    }

    private void setupCredentialsModel(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.getUser(username);
        model.addAttribute("credentials", credentialService.getCredentialsFor(user));
        model.addAttribute("encryptionService", encryptionService);
    }
}
