package pageObjects.nopCommerce.user;

import org.openqa.selenium.WebDriver;

import commons.BasePage;
import commons.PageGeneratorManage;
import pageUIs.nopCommerce.user.UserHomePageUI;

public class UserHomePageObject extends BasePage {
	private WebDriver driver;

	public UserHomePageObject(WebDriver driver) {
		super(driver);
		this.driver = driver;
	}

	public UserRegisterPageObject clickToRegisterLink() {
		waitForElementClickable(UserHomePageUI.REGISTER_LINK);
		clickToElement(UserHomePageUI.REGISTER_LINK);
		return PageGeneratorManage.getUserRegisterPage(driver);
	}
}
