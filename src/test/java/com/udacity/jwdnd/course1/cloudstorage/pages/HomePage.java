package com.udacity.jwdnd.course1.cloudstorage.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class HomePage extends AbstractPage {
    @FindBy(id = "logout-button")
    private WebElement logoutButton;

    @FindBy(id = "nav-notes-tab")
    private WebElement notesTab;

    @FindBy(id = "nav-credentials-tab")
    private WebElement credentialsTab;

    @FindBy(id = "nav-files-tab")
    private WebElement filesTab;

    @FindBy(id = "note-new-button")
    private WebElement newNoteButton;

    @FindBy(id = "credential-new-button")
    private WebElement newCredentialButton;

    @FindBy(id = "file-upload-button")
    private WebElement uploadFileButton;

    @FindBy(id = "file-upload")
    private WebElement uploadFileDialog;

    @FindBy(id = "note-title")
    private WebElement noteTitle;

    @FindBy(id = "note-description")
    private WebElement noteDescription;

    @FindBy(id = "credential-url")
    private WebElement credentialUrl;

    @FindBy(id = "credential-username")
    private WebElement credentialUsername;

    @FindBy(id = "credential-password")
    private WebElement credentialPassword;

    @FindBy(id = "note-save-button")
    private WebElement saveNoteButton;

    @FindBy(id = "credential-save-button")
    private WebElement saveCredentialButton;

    @FindBy(id = "user-table")
    private WebElement notesTable;

    @FindBy(id = "credentials-table")
    private WebElement credentialsTable;

    @FindBy(id = "files-table")
    private WebElement filesTable;

    @FindBy(id = "error-alert")
    private WebElement errorAlert;

    @FindBy(id = "success-alert")
    private WebElement successAlert;

    private final String URL;

    private final WebDriver driver;

    private final WebDriverWait wait;

    public HomePage(WebDriver driver, int port) {
        URL = BASE_URL + ":" + port + "/home";
        if (!driver.getCurrentUrl().equals(URL)) {
            driver.get(URL);
        }

        PageFactory.initElements(driver, this);

        this.driver = driver;
        this.wait = new WebDriverWait(driver, 2);
    }

    public void logoutUser() { logoutButton.click(); }

    public WebElement notesTab() { return notesTab; }

    public void createNewNote(String title, String description) {
        notesTab.click();

        wait.until(ExpectedConditions.visibilityOf(newNoteButton));
        newNoteButton.click();

        wait.until(ExpectedConditions.visibilityOf(noteTitle));
        noteTitle.clear();
        noteTitle.sendKeys(title);
        noteDescription.clear();
        noteDescription.sendKeys(description);
        saveNoteButton.click();
    }

    public WebElement notesTable() { return notesTable; }

    public int getNoteEntryCount() {
        notesTab.click();
        wait.until(ExpectedConditions.visibilityOf(newNoteButton));

        WebElement body = notesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));

        return rows.size();
    }

    public String getNoteTitle(int index) {
        notesTab.click();
        wait.until(ExpectedConditions.visibilityOf(newNoteButton));

        WebElement body = notesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        WebElement title = rows.get(index).findElement(By.tagName("th"));

        return title.getText();
    }

    public String getNoteDescription(int index) {
        notesTab.click();
        wait.until(ExpectedConditions.visibilityOf(newNoteButton));

        // TODO DRY
        WebElement body = notesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        List<WebElement> cols = rows.get(index).findElements(By.tagName("td"));
        WebElement description = cols.get(1);
        return description.getText();
    }

    public void editNote(int index, String newTitle, String newDescription) {
        notesTab.click();
        wait.until(ExpectedConditions.visibilityOf(newNoteButton));

        // TODO test simple search by id
        WebElement body = notesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        List<WebElement> cols = rows.get(index).findElements(By.tagName("td"));
        WebElement editButton = cols.get(0).findElement(By.id("note-edit-button"));

        editButton.click();
        wait.until(ExpectedConditions.visibilityOf(noteTitle));
        noteTitle.clear();
        noteTitle.sendKeys(newTitle);
        noteDescription.clear();
        noteDescription.sendKeys(newDescription);
        saveNoteButton.click();
    }

    public void deleteNote(int index) {
        notesTab.click();
        wait.until(ExpectedConditions.visibilityOf(newNoteButton));

        // TODO test simple search by id
        WebElement body = notesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        List<WebElement> cols = rows.get(index).findElements(By.tagName("td"));
        WebElement deleteButton = cols.get(0).findElement(By.id("note-delete-link"));

        deleteButton.click();
    }

    public WebElement errorMessage() {
        return errorAlert;
    }

    public WebElement successMessage() {
        return successAlert;
    }

    public WebElement credentialsTable() { return credentialsTable; }

    public int getCredentialEntryCount() {
        credentialsTab.click();
        wait.until(ExpectedConditions.visibilityOf(newCredentialButton));

        WebElement body = credentialsTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));

        return rows.size();
    }

    public String getCredentialUrl(int index) {
        credentialsTab.click();
        wait.until(ExpectedConditions.visibilityOf(newCredentialButton));

        WebElement body = credentialsTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        WebElement url = rows.get(index).findElement(By.tagName("th"));

        return url.getText();
    }

    public String getCredentialUsername(int index) {
        credentialsTab.click();
        wait.until(ExpectedConditions.visibilityOf(newCredentialButton));

        WebElement body = credentialsTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        WebElement username = rows.get(index).findElements(By.tagName("td")).get(1);

        return username.getText();
    }

    public String getCredentialPassword(int index) {
        credentialsTab.click();
        wait.until(ExpectedConditions.visibilityOf(newCredentialButton));

        WebElement body = credentialsTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        WebElement username = rows.get(index).findElements(By.tagName("td")).get(2);

        return username.getText();
    }

    public void createCredential(String url, String username, String password) {
        credentialsTab.click();

        wait.until(ExpectedConditions.visibilityOf(newCredentialButton));
        newCredentialButton.click();

        wait.until(ExpectedConditions.visibilityOf(credentialUrl));
        credentialUrl.clear();
        credentialUrl.sendKeys(url);
        credentialUsername.clear();
        credentialUsername.sendKeys(username);
        credentialPassword.clear();
        credentialPassword.sendKeys(password);
        saveCredentialButton.click();
    }

    public void editCredential(int index, String newUrl, String newUsername, String newPassword) {
        credentialsTab.click();
        wait.until(ExpectedConditions.visibilityOf(newCredentialButton));

        editCredentialButton(index).click();
        wait.until(ExpectedConditions.visibilityOf(credentialUrl));
        credentialUrl.clear();
        credentialUrl.sendKeys(newUrl);
        credentialUsername.clear();
        credentialUsername.sendKeys(newUsername);
        credentialPassword.clear();
        credentialPassword.sendKeys(newPassword);
        saveCredentialButton.click();
    }

    public WebElement editCredentialButton(int index) {
        credentialsTab.click();
        wait.until(ExpectedConditions.visibilityOf(newCredentialButton));

        WebElement body = credentialsTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        List<WebElement> cols = rows.get(index).findElements(By.tagName("td"));
        WebElement editButton = cols.get(0).findElement(By.id("credential-edit-button"));

        return editButton;
    }

    public WebElement credentialPassword() {
        return credentialPassword;
    }

    public void deleteCredential(int index) {
        credentialsTab.click();
        wait.until(ExpectedConditions.visibilityOf(newCredentialButton));

        // TODO test simple search by id
        WebElement body = credentialsTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        List<WebElement> cols = rows.get(index).findElements(By.tagName("td"));
        WebElement deleteButton = cols.get(0).findElement(By.id("credential-delete-button"));

        deleteButton.click();
    }

    public int getFileEntryCount() {
        filesTab.click();
        wait.until(ExpectedConditions.visibilityOf(uploadFileButton));

        WebElement body = filesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));

        return rows.size();
    }

    public void uploadFile(String filename) {
        filesTab.click();
        wait.until(ExpectedConditions.visibilityOf(uploadFileButton));

        uploadFileDialog.clear();
        uploadFileDialog.sendKeys(filename);
        uploadFileButton.click();
    }

    public void uploadEmptyFile() {
        filesTab.click();
        wait.until(ExpectedConditions.visibilityOf(uploadFileButton));

        uploadFileDialog.clear();
        uploadFileButton.click();
    }

    public String getFilename(int index) {
        filesTab.click();
        wait.until(ExpectedConditions.visibilityOf(uploadFileButton));

        WebElement body = filesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        WebElement fileName = rows.get(index).findElement(By.tagName("th"));

        return fileName.getText();
    }

    public void deleteFile(int index) {
        filesTab.click();
        wait.until(ExpectedConditions.visibilityOf(uploadFileButton));

        WebElement body = filesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        WebElement deleteButton = rows.get(index).findElement(By.id("file-delete-button"));

        deleteButton.click();
    }

    public void deleteAllFiles() {
        for (int i = 0; i < getFileEntryCount(); i++) {
            deleteFile(i);
        }
    }
}
