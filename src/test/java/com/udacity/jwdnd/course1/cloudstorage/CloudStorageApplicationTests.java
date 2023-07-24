package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.data.TestUser;
import com.udacity.jwdnd.course1.cloudstorage.pages.HomePage;
import com.udacity.jwdnd.course1.cloudstorage.pages.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.pages.SignUpPage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {
	private static final TestUser DEFAULT_USER = new TestUser();
	private static final String DUMMY_FILENAME_0 = "two-states.png";
	private static final String DUMMY_FILENAME_1 = "works-doesnt-work.jpg";
	private static final File DUMMY_FILE_0 = new File(CloudStorageApplicationTests.class.getClassLoader().getResource(DUMMY_FILENAME_0).getFile());
	private static final File DUMMY_FILE_1 = new File(CloudStorageApplicationTests.class.getClassLoader().getResource(DUMMY_FILENAME_1).getFile());
	private static boolean defaultUserExists;

	@LocalServerPort
	private int port;

	private WebDriver driver;
	private String baseUrl;

	private void createDefaultUser() {
		SignUpPage signUpPage = new SignUpPage(driver, port);
		signUpPage.signUpUser(DEFAULT_USER);
		defaultUserExists = true;
	}

	@BeforeAll
	static void beforeAll() {
		WebDriverManager.chromedriver().setup();
		defaultUserExists = false;
	}

	@BeforeEach
	public void beforeEach() {
		this.driver = new ChromeDriver();
		baseUrl = "http://localhost:" + port;

		// create a default user exactly once, simplifies the tests
		if (!defaultUserExists) {
			createDefaultUser();
		}
	}

	@AfterEach
	public void afterEach() {
		if (this.driver != null) {
			driver.quit();
		}
	}

	@Test
	public void getLoginPage() {
		driver.get(baseUrl + "/login");
		assertEquals("Login", driver.getTitle());
	}

	@Test
	public void loginRedirectsToHome() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		assertEquals(baseUrl + "/home", driver.getCurrentUrl());
	}

	@Test
	public void signUpUserAlert() {
		TestUser user = new TestUser("userone");
		SignUpPage signUpPage = new SignUpPage(driver, port);
		signUpPage.signUpUser(user);

		LoginPage loginPage = new LoginPage(driver, port);
		assertTrue(loginPage.signUpSuccessMessage().isDisplayed());
		assertEquals("You successfully signed up!", loginPage.signUpSuccessMessage().getText());
	}

	@Test
	public void signUpUserFailedAlert() {
		SignUpPage signUpPage = new SignUpPage(driver, port);
		signUpPage.signUpUser(DEFAULT_USER);

		assertTrue(signUpPage.errorMessage().isDisplayed());
		assertEquals("User with name " + DEFAULT_USER.username() + " already exists", signUpPage.errorMessage().getText());
	}

	@Test
	public void failedLoginAlert() {
		TestUser user = new TestUser("user");
		SignUpPage signUpPage = new SignUpPage(driver, port);
		signUpPage.signUpUser(user);

		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(user.username(), "wrongpassword");

		assertEquals("Invalid username or password", loginPage.errorMessage().getText());

		loginPage.loginUser("flandersn", user.password());

		assertEquals("Invalid username or password", loginPage.errorMessage().getText());
	}

	@Test
	public void logoutRedirectsToLogin() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		homePage.logoutUser();

		assertEquals(baseUrl + "/login", driver.getCurrentUrl());
	}

	@Test
	public void createSingleNote() {
		int existingNotes;
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		existingNotes = homePage.getNoteEntryCount();
		homePage.createNewNote("First Note", "Interesting content");

		assertEquals(existingNotes + 1, homePage.getNoteEntryCount());
		assertEquals("First Note", homePage.getNoteTitle(existingNotes));
		assertEquals("Interesting content", homePage.getNoteDescription(existingNotes));
	}

	@Test
	public void createMultipleNotes() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingNotes = homePage.getNoteEntryCount();
		homePage.createNewNote("First Note", "Interesting content");
		homePage.createNewNote("Second Note", "Also interesting content");

		assertEquals(existingNotes + 2, homePage.getNoteEntryCount());
		assertEquals("First Note", homePage.getNoteTitle(existingNotes));
		assertEquals("Interesting content", homePage.getNoteDescription(existingNotes));
		assertEquals("Second Note", homePage.getNoteTitle(existingNotes+1));
		assertEquals("Also interesting content", homePage.getNoteDescription(existingNotes+1));
	}

	@Test
	public void createNoteAlert() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		homePage.createNewNote("First Note", "Interesting content");

		assertEquals("Note successfully created!", homePage.successMessage().getText());
	}

	@Test
	public void editNote() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingNotes = homePage.getNoteEntryCount();
		homePage.createNewNote("First Note", "Interesting content");

		homePage.editNote(existingNotes, "Edited Note", "Edited content");
		assertEquals(existingNotes + 1, homePage.getNoteEntryCount());
		assertEquals("Edited Note", homePage.getNoteTitle(existingNotes));
		assertEquals("Edited content", homePage.getNoteDescription(existingNotes));
	}

	@Test
	public void deleteNote() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingNotes = homePage.getNoteEntryCount();
		homePage.createNewNote("First Note", "Interesting content");
		homePage.createNewNote("Second Note", "Also interesting content");

		homePage.deleteNote(existingNotes);
		assertEquals(existingNotes + 1, homePage.getNoteEntryCount());
		assertEquals("Second Note", homePage.getNoteTitle(existingNotes));
		assertEquals("Also interesting content", homePage.getNoteDescription(existingNotes));
	}

	@Test
	public void deleteNoteAlert() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingNotes = homePage.getNoteEntryCount();
		homePage.createNewNote("First Note", "Interesting content");
		homePage.deleteNote(existingNotes);

		assertEquals("Note successfully deleted!", homePage.successMessage().getText());
	}

	@Test
	public void createSingleCredential() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingCredentials = homePage.getCredentialEntryCount();
		homePage.createCredential("example.com", "root", "123456");

		assertEquals(existingCredentials + 1, homePage.getCredentialEntryCount());
		assertEquals("example.com", homePage.getCredentialUrl(existingCredentials));
		assertEquals("root", homePage.getCredentialUsername(existingCredentials));
		assertTrue(homePage.getCredentialPassword(existingCredentials).endsWith("=="));
	}

	@Test
	public void createMultipleCredentials() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingCredentials = homePage.getCredentialEntryCount();
		homePage.createCredential("example.com", "root", "123456");
		homePage.createCredential("duff.com", "duffman", "duffbeer");

		assertEquals(existingCredentials + 2, homePage.getCredentialEntryCount());
		assertEquals("example.com", homePage.getCredentialUrl(existingCredentials));
		assertEquals("root", homePage.getCredentialUsername(existingCredentials));
		assertTrue(homePage.getCredentialPassword(existingCredentials).endsWith("=="));
		assertEquals("duff.com", homePage.getCredentialUrl(existingCredentials+1));
		assertEquals("duffman", homePage.getCredentialUsername(existingCredentials+1));
		assertTrue(homePage.getCredentialPassword(existingCredentials+1).endsWith("=="));
	}

	@Test
	public void createCredentialAlert() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		homePage.createCredential("example.com", "root", "123456");

		assertEquals("Credential successfully created!", homePage.successMessage().getText());
	}

	@Test
	public void samePasswordHasDifferentEncryptedViews() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingCredentials = homePage.getCredentialEntryCount();
		homePage.createCredential("example.com", "root", "123456");
		homePage.createCredential("duff.com", "duffman", "123456");

		assertNotEquals(homePage.getCredentialPassword(existingCredentials), homePage.getCredentialPassword(existingCredentials+1));
	}

	@Test
	public void viewCredentialShowsPlainPassword() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingCredentials = homePage.getCredentialEntryCount();
		homePage.createCredential("example.com", "root", "123456");

		homePage.editCredentialButton(existingCredentials).click();
		assertEquals("123456", homePage.credentialPassword().getAttribute("value"));
	}

	@Test
	public void editCredential() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingCredentials = homePage.getCredentialEntryCount();
		homePage.createCredential("example.com", "root", "123456");
		String encryptedPassword = homePage.getCredentialPassword(existingCredentials);

		homePage.editCredential(existingCredentials, "anotherurl.com", "normaluser", "security1o1");
		assertEquals(existingCredentials + 1, homePage.getCredentialEntryCount());
		assertEquals("anotherurl.com", homePage.getCredentialUrl(existingCredentials));
		assertEquals("normaluser", homePage.getCredentialUsername(existingCredentials));
		assertNotEquals(encryptedPassword, homePage.getCredentialPassword(existingCredentials));
	}

	@Test
	public void deleteCredential() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingCredentials = homePage.getCredentialEntryCount();
		homePage.createCredential("example.com", "root", "123456");
		homePage.createCredential("duff.com", "duffman", "duffbeer");

		homePage.deleteCredential(existingCredentials);
		assertEquals(existingCredentials + 1, homePage.getCredentialEntryCount());
		assertEquals("duff.com", homePage.getCredentialUrl(existingCredentials));
		assertEquals("duffman", homePage.getCredentialUsername(existingCredentials));
		assertTrue(homePage.getCredentialPassword(existingCredentials).endsWith("=="));
	}

	@Test
	public void deleteCredentialAlert() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);

		HomePage homePage = new HomePage(driver, port);
		int existingCredentials = homePage.getCredentialEntryCount();
		homePage.createCredential("example.com", "root", "123456");
		homePage.deleteCredential(existingCredentials);

		assertEquals("Credential successfully deleted!", homePage.successMessage().getText());
	}

	@Test
	public void uploadSingleFile() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);
		HomePage homePage = new HomePage(driver, port);
		homePage.deleteAllFiles();

		homePage.uploadFile(DUMMY_FILE_0.getAbsolutePath());

		assertEquals(1, homePage.getFileEntryCount());
		assertEquals(DUMMY_FILE_0.getName(), homePage.getFilename(0));
	}

	@Test
	public void uploadSameFileAlert() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);
		HomePage homePage = new HomePage(driver, port);
		homePage.deleteAllFiles();

		homePage.uploadFile(DUMMY_FILE_1.getAbsolutePath());
		homePage.uploadFile(DUMMY_FILE_1.getAbsolutePath());

		assertEquals(1, homePage.getFileEntryCount());
		assertEquals("File already exists!", homePage.errorMessage().getText());
	}

	@Test
	public void uploadMultipleFiles() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);
		HomePage homePage = new HomePage(driver, port);
		homePage.deleteAllFiles();

		homePage.uploadFile(DUMMY_FILE_0.getAbsolutePath());
		homePage.uploadFile(DUMMY_FILE_1.getAbsolutePath());

		assertEquals(2, homePage.getFileEntryCount());
		assertEquals(DUMMY_FILE_0.getName(), homePage.getFilename(0));
		assertEquals(DUMMY_FILE_1.getName(), homePage.getFilename(1));
	}

	@Test
	public void deleteFile() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);
		HomePage homePage = new HomePage(driver, port);
		homePage.deleteAllFiles();

		homePage.uploadFile(DUMMY_FILE_0.getAbsolutePath());
		homePage.uploadFile(DUMMY_FILE_1.getAbsolutePath());
		homePage.deleteFile(1);

		assertEquals(1, homePage.getFileEntryCount());
		assertEquals(DUMMY_FILE_0.getName(), homePage.getFilename(0));
	}

	@Test
	public void deleteFileAlert() {
		LoginPage loginPage = new LoginPage(driver, port);
		loginPage.loginUser(DEFAULT_USER);
		HomePage homePage = new HomePage(driver, port);
		homePage.deleteAllFiles();

		homePage.uploadFile(DUMMY_FILE_0.getAbsolutePath());
		homePage.uploadFile(DUMMY_FILE_1.getAbsolutePath());
		homePage.deleteFile(1);

		assertEquals("File successfully deleted!", homePage.successMessage().getText());
	}

	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doMockSignUp(String firstName, String lastName, String userName, String password){
		// Create a dummy account for logging in later.

		// Visit the sign-up page.
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		driver.get("http://localhost:" + this.port + "/signup");
		webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));

		// Fill out credentials
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
		WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
		inputFirstName.click();
		inputFirstName.sendKeys(firstName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
		WebElement inputLastName = driver.findElement(By.id("inputLastName"));
		inputLastName.click();
		inputLastName.sendKeys(lastName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
		WebElement inputUsername = driver.findElement(By.id("inputUsername"));
		inputUsername.click();
		inputUsername.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
		WebElement inputPassword = driver.findElement(By.id("inputPassword"));
		inputPassword.click();
		inputPassword.sendKeys(password);

		// Attempt to sign up.
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
		WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
		buttonSignUp.click();

		/* Check that the sign up was successful.
		// You may have to modify the element "success-msg" and the sign-up
		// success message below depening on the rest of your code.
		*/
		Assertions.assertTrue(driver.findElement(By.id("success-alert")).getText().contains("You successfully signed up!"));
	}



	/**
	 * PLEASE DO NOT DELETE THIS method.
	 * Helper method for Udacity-supplied sanity checks.
	 **/
	private void doLogIn(String userName, String password)
	{
		// Log in to our dummy account.
		driver.get("http://localhost:" + this.port + "/login");
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-username")));
		WebElement loginUserName = driver.findElement(By.id("input-username"));
		loginUserName.click();
		loginUserName.sendKeys(userName);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-password")));
		WebElement loginPassword = driver.findElement(By.id("input-password"));
		loginPassword.click();
		loginPassword.sendKeys(password);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
		WebElement loginButton = driver.findElement(By.id("login-button"));
		loginButton.click();

		webDriverWait.until(ExpectedConditions.titleContains("Home"));

	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
	 * rest of your code.
	 * This test is provided by Udacity to perform some basic sanity testing of
	 * your code to ensure that it meets certain rubric criteria.
	 *
	 * If this test is failing, please ensure that you are handling redirecting users
	 * back to the login page after a succesful sign up.
	 * Read more about the requirement in the rubric:
	 * https://review.udacity.com/#!/rubrics/2724/view
	 */
	@Test
	public void testRedirection() {
		// Create a test account
		doMockSignUp("Redirection","Test","RT","123");

		// Check if we have been redirected to the log in page.
		assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
	}

	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
	 * rest of your code.
	 * This test is provided by Udacity to perform some basic sanity testing of
	 * your code to ensure that it meets certain rubric criteria.
	 *
	 * If this test is failing, please ensure that you are handling bad URLs
	 * gracefully, for example with a custom error page.
	 *
	 * Read more about custom error pages at:
	 * https://attacomsian.com/blog/spring-boot-custom-error-page#displaying-custom-error-page
	 */
	@Test
	public void testBadUrl() {
		// Create a test account
		doMockSignUp("URL","Test","UT","123");
		doLogIn("UT", "123");

		// Try to access a random made-up URL.
		driver.get("http://localhost:" + this.port + "/some-random-page");
		Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
	}


	/**
	 * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
	 * rest of your code.
	 * This test is provided by Udacity to perform some basic sanity testing of
	 * your code to ensure that it meets certain rubric criteria.
	 *
	 * If this test is failing, please ensure that you are handling uploading large files (>1MB),
	 * gracefully in your code.
	 *
	 * Read more about file size limits here:
	 * https://spring.io/guides/gs/uploading-files/ under the "Tuning File Upload Limits" section.
	 */
	@Test
	public void testLargeUpload() {
		// Create a test account
		doMockSignUp("Large File","Test","LFT","123");
		doLogIn("LFT", "123");

		// Try to upload an arbitrary large file
		WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
		String fileName = "upload5m.zip";

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("file-upload")));
		WebElement fileSelectButton = driver.findElement(By.id("file-upload"));
		fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

		WebElement uploadButton = driver.findElement(By.id("file-upload-button"));
		uploadButton.click();
		try {
			webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
		} catch (org.openqa.selenium.TimeoutException e) {
			System.out.println("Large File upload failed");
		}
		Assertions.assertFalse(driver.getPageSource().contains("HTTP Status 403 – Forbidden"));

	}



}
