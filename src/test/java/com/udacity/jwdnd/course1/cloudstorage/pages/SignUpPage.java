package com.udacity.jwdnd.course1.cloudstorage.pages;

import com.udacity.jwdnd.course1.cloudstorage.data.TestUser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignUpPage extends AbstractPage {
    @FindBy(id = "inputFirstName")
    private WebElement inputFirstName;

    @FindBy(id = "inputLastName")
    private WebElement inputLastName;

    @FindBy(id = "inputUsername")
    private WebElement inputUsername;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "buttonSignUp")
    private WebElement signUpButton;

    @FindBy(id = "errorMsg")
    private WebElement errorMessage;

    private final String URL;

    public SignUpPage(WebDriver driver, int port) {
        URL = BASE_URL + ":" + port + "/signup";
        if (!driver.getCurrentUrl().equals(URL)) {
            driver.get(URL);
        }

        PageFactory.initElements(driver, this);
    }

    public void clickSignUpButton() {
        signUpButton.click();
    }

    public WebElement errorMessage() { return errorMessage; }

    public void signUpUser(TestUser user) {
        inputFirstName.clear();
        inputFirstName.sendKeys(user.firstName());
        inputLastName.clear();
        inputLastName.sendKeys(user.lastName());
        inputUsername.clear();
        inputUsername.sendKeys(user.username());
        inputPassword.clear();
        inputPassword.sendKeys(user.password());
        clickSignUpButton();
    }
}
