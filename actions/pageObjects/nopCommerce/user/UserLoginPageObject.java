package pageObjects.nopCommerce.user;

import org.openqa.selenium.WebDriver;

import commons.BasePage;
import pageUIs.nopCommerce.user.UserLoginPageUI;

public class UserLoginPageObject extends BasePage {
	private WebDriver driver;

	public UserLoginPageObject(WebDriver driver) {
		super(driver);
		this.driver = driver;
	}

	public void clickToLoginButton() {
		waitForElementClickable(UserLoginPageUI.LOGIN_BUTTON);
		clickToElement(UserLoginPageUI.LOGIN_BUTTON);
	}

	public boolean isEmailErrorMessageDisplay(String erorrMessage) {
		waitForElementVisible(UserLoginPageUI.EMAIL_EXISTED_MESSAGE_BY_TEXT, erorrMessage);
		return isElementDisPlayed(UserLoginPageUI.EMAIL_EXISTED_MESSAGE_BY_TEXT, erorrMessage);
	}

	public void inPutToEmailTextbox(String emailAddress) {
		waitForElementVisible(UserLoginPageUI.EMAIL_TEXTBOX);
		senkeyToElement(UserLoginPageUI.EMAIL_TEXTBOX, emailAddress);
	}

	public String getVaidationSumaryError() {
		waitForElementVisible(UserLoginPageUI.VALIDATION_SUMMARY_ERROR_MESSAGE);
		return getElementText(UserLoginPageUI.VALIDATION_SUMMARY_ERROR_MESSAGE);
	}

}
