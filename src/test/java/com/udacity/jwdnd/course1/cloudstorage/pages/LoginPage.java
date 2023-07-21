package com.udacity.jwdnd.course1.cloudstorage.pages;

import com.udacity.jwdnd.course1.cloudstorage.data.TestUser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends AbstractPage {
    @FindBy(id = "inputUsername")
    private WebElement inputUsername;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "errorMsg")
    private WebElement errorMessage;

    @FindBy(id = "logoutMsg")
    private WebElement logoutMessage;

    @FindBy(id = "loginButton")
    private WebElement loginButton;

    @FindBy(id = "successMsg")
    private WebElement signUpSuccessmessage;

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

    public WebElement errorMessage() { return errorMessage; }

    public WebElement logoutMessage() { return logoutMessage; }

    public WebElement signUpSuccessMessage() { return signUpSuccessmessage; }
}
