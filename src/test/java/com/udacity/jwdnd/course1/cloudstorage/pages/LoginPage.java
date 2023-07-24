package com.udacity.jwdnd.course1.cloudstorage.pages;

import com.udacity.jwdnd.course1.cloudstorage.data.TestUser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends AbstractPage {
    @FindBy(id = "input-username")
    private WebElement inputUsername;

    @FindBy(id = "input-password")
    private WebElement inputPassword;

    @FindBy(id = "error-alert")
    private WebElement errorAlert;

    @FindBy(id = "logout-alert")
    private WebElement logoutAlert;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(id = "success-alert")
    private WebElement successAlert;

    private WebDriver driver;
    private int port;
    private final String URL;

    public LoginPage(WebDriver driver, int port) {
        URL = BASE_URL + ":" + port + "/login";
        if (!driver.getCurrentUrl().equals(URL)) {
            driver.get(URL);
        }

        PageFactory.initElements(driver, this);
        this.driver = driver;
        this.port = port;
    }

    public void loginUser(TestUser user) {
        loginUser(user.username(), user.password());
    }

    public void loginUser(String username, String password) {
        inputUsername.clear();
        inputUsername.sendKeys(username);
        inputPassword.clear();
        inputPassword.sendKeys(password);
        loginButton.click();
    }

    public WebElement errorMessage() { return errorAlert; }

    public WebElement logoutMessage() { return logoutAlert; }

    public WebElement signUpSuccessMessage() { return successAlert; }
}
