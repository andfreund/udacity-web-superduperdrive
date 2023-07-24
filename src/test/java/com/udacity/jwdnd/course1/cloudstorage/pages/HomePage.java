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
    @FindBy(id = "logoutButton")
    private WebElement logoutButton;

    @FindBy(id = "nav-notes-tab")
    private WebElement notesTab;

    @FindBy(id = "note-new-button")
    private WebElement newNoteButton;

    @FindBy(id = "note-title")
    private WebElement noteTitle;

    @FindBy(id = "note-description")
    private WebElement noteDescription;

    @FindBy(id = "note-save-button")
    private WebElement saveNoteButton;

    @FindBy(id = "userTable")
    private WebElement notesTable;

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

        WebElement body = notesTable.findElement(By.tagName("tbody"));
        List<WebElement> rows = body.findElements(By.tagName("tr"));
        List<WebElement> cols = rows.get(index).findElements(By.tagName("td"));
        WebElement description = cols.get(1);
        return description.getText();
    }
}
