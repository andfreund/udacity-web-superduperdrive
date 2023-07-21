package com.udacity.jwdnd.course1.cloudstorage.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends AbstractPage {
    @FindBy(id = "logoutButton")
    private WebElement logoutButton;
    private final String URL;

    public HomePage(WebDriver driver, int port) {
        URL = BASE_URL + ":" + port + "/home";
        if (!driver.getCurrentUrl().equals(URL)) {
            driver.get(URL);
        }

        PageFactory.initElements(driver, this);
    }

    public void logoutUser() { logoutButton.click(); }
}
