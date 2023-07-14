package com.udacity.jwdnd.course1.cloudstorage.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignUpPage {
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

    @FindBy(id = "success-msg")
    private WebElement successMessage;

    @FindBy(id = "error-msg")
    private WebElement errorMessage;

    public SignUpPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
    }

    public void clickSignUpButton() {
        signUpButton.click();
    }

    public void signUpUser(String firstName, String lastName, String username, String password) {
        inputFirstName.clear();
        inputFirstName.sendKeys(firstName);
        inputLastName.clear();
        inputLastName.sendKeys(lastName);
        inputUsername.clear();
        inputUsername.sendKeys(username);
        inputPassword.clear();
        inputPassword.sendKeys(password);
        clickSignUpButton();
    }

    public WebElement successMessage() { return successMessage; }

    public WebElement errorMessage() { return errorMessage; }
}
