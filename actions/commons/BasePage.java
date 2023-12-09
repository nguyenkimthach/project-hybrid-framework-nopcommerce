package commons;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import pageObjects.nopCommerce.user.UserRegisterPageObject;
import pageUIs.nopCommerce.user.BasePageNopCommerceUI;

public class BasePage {
	private WebDriver driver;

	public BasePage(WebDriver driver) {
		this.driver = driver;
	}

	public void openPageUrl(String pageUrl) {
		driver.get(pageUrl);
	}

	public String getPageTitle() {
		return driver.getTitle();
	}

	public String getPageUrl() {
		return driver.getCurrentUrl();
	}

	public String getPageSourceCode() {
		return driver.getPageSource();
	}

	public void refreshCurrentPage() {
		driver.navigate().refresh();
	}

	public Set<Cookie> getAllCookies() {
		return driver.manage().getCookies();
	}

	public void setCookies(Set<Cookie> cookies) {
		for (Cookie cookie : cookies) {
			driver.manage().addCookie(cookie);
		}
		sleepInSecond(3);
	}

	public void backToPage() {
		driver.navigate().back();
	}

	public Alert waitForAlertPresence() {
		WebDriverWait explicitwait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		return explicitwait.until(ExpectedConditions.alertIsPresent());
	}

	public void acceptAlert() {
		waitForAlertPresence().accept();
	}

	public void cancelAlert() {
		waitForAlertPresence().dismiss();
	}

	public String getlAlertText() {
		return waitForAlertPresence().getText();
	}

	public void sendKeyTolAlert(String textValue) {
		waitForAlertPresence().sendKeys(textValue);
	}

	public void switchToWindowByID(String WindowsID) {
		Set<String> allWindowsIDs = driver.getWindowHandles();
		for (String id : allWindowsIDs) {
			if (!id.equals(WindowsID)) {
				driver.switchTo().window(id);
			}
		}
	}

	public void switchToWindowByPageTitle(String tabTitle) {
		Set<String> allWindowsIDs = driver.getWindowHandles();
		for (String id : allWindowsIDs) {
			driver.switchTo().window(id);
			String actuaPageTitle = driver.getTitle();
			if (actuaPageTitle.equals(tabTitle)) {
				break;
			}
		}
	}

	public void closeAllWindow_WithoutParent(String parentID) {
		Set<String> allWindowsIDs = driver.getWindowHandles();
		for (String id : allWindowsIDs) {
			if (!id.equals(parentID)) {
				driver.switchTo().window(id);
				driver.close();
			}
		}
		driver.switchTo().window(parentID);
	}

	private By getByLocator(String locatorType) {
		By by = null;
		String[] locatorParts = locatorType.split("=", 2);
		if (locatorParts.length != 2) {
			throw new IllegalArgumentException("Invalid locator format");
		}
		String typeLocator = locatorParts[0].trim().toLowerCase();
		String locator = locatorParts[1].trim();

		switch (typeLocator) {
		case "id":
			by = By.id(locator);
			break;
		case "class":
			by = By.className(locator);
			break;
		case "name":
			by = By.name(locator);
			break;
		case "xpath":
			by = By.xpath(locator);
			break;
		case "css":
			by = By.cssSelector(locator);
			break;
		default:
			throw new IllegalArgumentException("LocatorType is not supported");
		}
		return by;
	}

	private String getDynamicXpath(String locatorType, String... value) {
		if (locatorType.startsWith("xpath=") || locatorType.startsWith("XPATH=") || locatorType.startsWith("Xpath=") || locatorType.startsWith("XPath=")) {
			locatorType = String.format(locatorType, (Object[]) value);
		}
		return locatorType;
	}

	private WebElement getWebElement(String locatorType) {
		return driver.findElement(getByLocator(locatorType));
	}

	public List<WebElement> getListWebElement(String locatorType) {
		return driver.findElements(getByLocator(locatorType));
	}

	public void clickToElement(String locatorType) {
		getWebElement(locatorType).click();
	}

	public void clickToElement(String locatorType, String... dynamicValues) {
		getWebElement(getDynamicXpath(locatorType, dynamicValues)).click();
	}

	public void senkeyToElement(String locatorType, String textValue) {
		WebElement element = getWebElement(locatorType);
		element.clear();
		element.sendKeys(textValue);
	}

	public void clearValueInElementByDeleteKey(String locatorType) {
		WebElement element = getWebElement(locatorType);
		element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
	}

	public void senkeyToElement(String locatorType, String textValue, String... dynamicValues) {
		WebElement element = getWebElement(getDynamicXpath(locatorType, dynamicValues));
		element.clear();
		element.sendKeys(textValue);
	}

	public void selectItemInDefaultDropDown(String locatorType, String textItem) {
		Select select = new Select(getWebElement(locatorType));
		select.selectByVisibleText(textItem);
	}

	public void selectItemInDefaultDropDown(String locatorType, String textItem, String... dynamicValues) {
		Select select = new Select(getWebElement(getDynamicXpath(locatorType, dynamicValues)));
		select.selectByVisibleText(textItem);
	}

	public String getSelectedItemDefaultDropdown(String locatorType) {
		Select select = new Select(getWebElement(locatorType));
		return select.getFirstSelectedOption().getText();
	}

	public boolean isDropdownMultiple(String locatorType) {
		Select select = new Select(getWebElement(locatorType));
		return select.isMultiple();
	}

	public void SelectItemInCustomDropdown(String parentXpath, String childXpath, String expectedTextItem) {
		getWebElement(parentXpath).click();
		sleepInSecond(1);

		WebDriverWait explicitwait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		List<WebElement> speeDropdownItems = explicitwait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(childXpath)));
		;
		for (WebElement item : speeDropdownItems) {
			if (item.getText().trim().equals(expectedTextItem)) {
				JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
				jsExecutor.executeScript("arguments[0].scrollIntoView(true);", item);
				sleepInSecond(1);
				item.click();
				break;
			}
		}
	}

	public void sleepInSecond(long timeInSecond) {
		try {
			Thread.sleep(timeInSecond * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getElementAttribute(String locatorType, String attributeName) {
		return getWebElement(locatorType).getAttribute(attributeName);
	}

	public String getElementAttribute(String locatorType, String attributeName, String... dynamicValues) {
		return getWebElement(getDynamicXpath(locatorType, dynamicValues)).getAttribute(attributeName);
	}

	public String getElementText(String locatorType) {
		return getWebElement(locatorType).getText();
	}

	public String getElementText(String locatorType, String... dynamicValues) {
		return getWebElement(getDynamicXpath(locatorType, dynamicValues)).getText();
	}

	public String getElementCssValue(String locatorType, String propertyName) {
		return getWebElement(locatorType).getCssValue(propertyName);
	}

	public String getHexaColorFromRGBA(String rgbaValue) {
		return Color.fromString(rgbaValue).asHex();
	}

	public int getElementSize(String locatorType) {
		return getListWebElement(locatorType).size();
	}

	public int getElementSize(String locatorType, String... dynamicValues) {
		return getListWebElement(getDynamicXpath(locatorType, dynamicValues)).size();
	}

	public void checkToDefaultCheckboxOrRadio(String locatorType) {
		WebElement element = getWebElement(locatorType);
		if (!element.isSelected()) {
			element.click();
		}
	}

	public void checkToDefaultCheckboxOrRadio(String locatorType, String... dynamicValues) {
		WebElement element = getWebElement(getDynamicXpath(locatorType, dynamicValues));
		if (!element.isSelected()) {
			element.click();
		}
	}

	public void unCheckToDefaultCheckboxRadio(String locatorType) {
		WebElement element = getWebElement(locatorType);
		if (element.isSelected()) {
			element.click();
		}
	}

	public void unCheckToDefaultCheckboxRadio(String locatorType, String... dynamicValues) {
		WebElement element = getWebElement(getDynamicXpath(locatorType, dynamicValues));
		if (element.isSelected()) {
			element.click();
		}
	}

	public boolean isElementDisPlayed(String locatorType) {
		try {
			return getWebElement(locatorType).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isElementDisPlayed(String locatorType, String... dynamicValues) {
		return getWebElement(getDynamicXpath(locatorType, dynamicValues)).isDisplayed();
	}

	public boolean isElementUndisplayed(String locatorType) {
		overrideImplicitTimeout(GlobalConstants.SHORT_TIMEOUT);
		List<WebElement> elements = getListWebElement(locatorType);
		overrideImplicitTimeout(GlobalConstants.LONG_TIMEOUT);

		if (elements.size() == 0) {
			return true;
		} else if (elements.size() > 0 && !elements.get(0).isDisplayed()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isElementUndisplayed(String locatorType, String... dynamicValues) {
		overrideImplicitTimeout(GlobalConstants.SHORT_TIMEOUT);
		List<WebElement> elements = getListWebElement(getDynamicXpath(locatorType, dynamicValues));
		overrideImplicitTimeout(GlobalConstants.LONG_TIMEOUT);

		if (elements.size() == 0) {
			return true;
		} else if (elements.size() > 0 && !elements.get(0).isDisplayed()) {
			return true;
		} else {
			return false;
		}
	}

	public void overrideImplicitTimeout(long timeOut) {
		driver.manage().timeouts().implicitlyWait(timeOut, TimeUnit.SECONDS);
	}

	public boolean isElementEnable(String locatorType) {
		return getWebElement(locatorType).isEnabled();
	}

	public boolean isElementSelected(String locatorType) {
		return getWebElement(locatorType).isSelected();
	}

	public boolean isElementSelected(String locatorType, String... dynamicValues) {
		return getWebElement(getDynamicXpath(locatorType, dynamicValues)).isSelected();
	}

	public void switchToFrameIframe(String locatorType) {
		driver.switchTo().frame(getWebElement(locatorType));
	}

	public void switchToDefaultContent(String locatorType) {
		driver.switchTo().defaultContent();
	}

	public void hoverMouseToElement(String locatorType) {
		Actions action = new Actions(driver);
		action.moveToElement(getWebElement(locatorType)).perform();
	}

	public void pressKeyToElement(String locatorType, Keys key) {
		Actions action = new Actions(driver);
		action.sendKeys(getWebElement(locatorType), key).perform();
	}

	public void pressKeyToElement(String locatorType, Keys key, String... dynamicValues) {
		Actions action = new Actions(driver);
		action.sendKeys(getWebElement(getDynamicXpath(locatorType, dynamicValues)), key).perform();
	}

	public void scrollToBottomPage() {
		((JavascriptExecutor) driver).executeScript("window.scrollBy(0,document.body.scrollHeight)");
	}

	public void highlightElement(String locatorType) {
		WebElement element = getWebElement(locatorType);
		String originalStyle = element.getAttribute("style");
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", element, "style", "border: 2px solid red; border-style: dashed;");
		sleepInSecond(1);
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute(arguments[1], arguments[2])", element, "style", originalStyle);
	}

	public void clickToElementByJS(String locatorType) {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", getWebElement(locatorType));
	}

	public void scrollToElement(String locatorType) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", getWebElement(locatorType));
	}

	public String getElementValueByJSXpath(String xpathLocator) {
		JavascriptExecutor jsExcutor = (JavascriptExecutor) driver;
		if (xpathLocator.startsWith("xpath=")) {
			xpathLocator = xpathLocator.replace("xpath=", "");
		}
		return (String) jsExcutor.executeScript("return $(document.evaluate(\"" + xpathLocator + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue).val()");
	}

	public void removeAttributeInDOM(String locatorType, String attributeRemove) {
		((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('" + attributeRemove + "');", getWebElement(locatorType));
	}

	public boolean areJQueryAndJSLoadedSuccess() {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
				} catch (Exception e) {
					return true;
				}
			}
		};
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
			}
		};
		return explicitWait.until(jQueryLoad) && explicitWait.until(jsLoad);
	}

	public WebElement getShadowDOM(String locatorType) {
		return (WebElement) ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot;", getWebElement(locatorType));
	}

	public String getElementValidationMessage(String locatorType) {
		return (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].validationMessage;", getWebElement(locatorType));
	}

	public boolean isImageLoaded(String locatorType) {
		boolean status = (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", getWebElement(locatorType));
		if (status) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isImageLoaded(String locatorType, String... dynamicValues) {
		boolean status = (boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0",
				getWebElement(getDynamicXpath(locatorType, dynamicValues)));
		if (status) {
			return true;
		} else {
			return false;
		}
	}

	public void waitForElementVisible(String locatorType) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.visibilityOfElementLocated(getByLocator(locatorType)));
	}

	public void waitForElementVisible(String locatorType, String... dynamicValues) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.visibilityOfElementLocated(getByLocator(getDynamicXpath(locatorType, dynamicValues))));
	}

	public void waitForAllElementVisible(String locatorType) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(getByLocator(locatorType)));
	}

	public void waitForAllElementVisible(String locatorType, String... dynamicValues) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(getByLocator(getDynamicXpath(locatorType, dynamicValues))));
	}

	public void waitForElementInvisible(String locatorType) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.invisibilityOfElementLocated(getByLocator(locatorType)));
	}

	/**
	 * Wait for element undisplayed in DOM or not in DOM and override implicit timeout
	 * 
	 * @author ThachNk
	 * @param driver
	 * @param locatorType
	 */
	public void waitForElementUndisplay(String locatorType) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.SHORT_TIMEOUT);
		overrideImplicitTimeout(GlobalConstants.SHORT_TIMEOUT);
		explicitWait.until(ExpectedConditions.invisibilityOfElementLocated(getByLocator(locatorType)));
		overrideImplicitTimeout(GlobalConstants.LONG_TIMEOUT);
	}

	public void waitForElementInvisible(String locatorType, String... dynamicValues) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.invisibilityOfElementLocated(getByLocator(getDynamicXpath(locatorType, dynamicValues))));
	}

	public void waitForAllElementInvisible(String locatorType) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.invisibilityOfAllElements(getListWebElement(locatorType)));
	}

	public void waitForElementClickable(String locatorType) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.elementToBeClickable(getByLocator(locatorType)));
	}

	public void waitForElementClickable(String locatorType, String... dynamicValues) {
		WebDriverWait explicitWait = new WebDriverWait(driver, GlobalConstants.LONG_TIMEOUT);
		explicitWait.until(ExpectedConditions.elementToBeClickable(getByLocator(getDynamicXpath(locatorType, dynamicValues))));
	}

	public void uploadMultipleFiles(String... fileNames) {
		// Đường dẫn của thư muc Upload file : Windows\ Mac\ Linux
		String filePath = GlobalConstants.UPLOAD_FILE;

		// Đường dẩn của all File
		String fullFileName = "";
		for (String file : fileNames) {
			fullFileName = fullFileName + filePath + file + "\n";
		}
		fullFileName = fullFileName.trim();
		getWebElement(GlobalConstants.UPLOAD_FILE).sendKeys(fullFileName);
	}

	/**
	 * Enter to dynamic textbox by id
	 * 
	 * @author ThachNK
	 * @param driver
	 * @param textBoxID
	 * @param value
	 */
	public void inPutToTextboxByID(String textBoxID, String value) {
		waitForElementVisible(BasePageNopCommerceUI.DYNAMIC_TEXTBOX_BY_ID, textBoxID);
		senkeyToElement(BasePageNopCommerceUI.DYNAMIC_TEXTBOX_BY_ID, value, textBoxID);
	}

	/**
	 * Click to dynamic button by text
	 * 
	 * @author ThachNk
	 * @param driver
	 * @param buttonText
	 */
	public void clickTobuttonByText(String buttonText) {
		waitForElementClickable(BasePageNopCommerceUI.DYNAMIC_BUTTON_BY_TEXT, buttonText);
		clickToElement(BasePageNopCommerceUI.DYNAMIC_BUTTON_BY_TEXT, buttonText);
	}

	/**
	 * Select item in dropdown by name atribute
	 * 
	 * @author ThachNk
	 * @param driver
	 * @param dropdownAtributeName
	 * @param itemValue
	 */
	public void selectToDropdownByName(String dropdownAtributeName, String itemValue) {
		waitForElementClickable(BasePageNopCommerceUI.DYNAMIC_DROPDOWN_BY_NAME, dropdownAtributeName);
		selectItemInDefaultDropDown(BasePageNopCommerceUI.DYNAMIC_DROPDOWN_BY_NAME, itemValue, dropdownAtributeName);
	}

	/**
	 * Click To dynamic radio button by label name
	 * 
	 * @author ThachNk
	 * @param driver
	 * @param checkBoxLabelname
	 */
	public void clickToRadioButtonByLabel(String checkBoxLabelname) {
		waitForElementClickable(BasePageNopCommerceUI.DYNAMIC_RADIO_BUTTON_BY_LABEL, checkBoxLabelname);
		checkToDefaultCheckboxOrRadio(BasePageNopCommerceUI.DYNAMIC_RADIO_BUTTON_BY_LABEL, checkBoxLabelname);
	}

	/**
	 * Click To dynamic checkbox by label name
	 * 
	 * @author ThachNk
	 * @param driver
	 * @param checkBoxLabelname
	 */
	public void clickToCheckboxByLabel(String checkBoxLabelname) {
		waitForElementClickable(BasePageNopCommerceUI.DYNAMIC_CHECKBOX_BY_LABEL, checkBoxLabelname);
		checkToDefaultCheckboxOrRadio(BasePageNopCommerceUI.DYNAMIC_CHECKBOX_BY_LABEL, checkBoxLabelname);
	}

	/**
	 * Get value in textbox by textboxID
	 * 
	 * @author ThachNk
	 * @param driver
	 * @param textboxID
	 * @return
	 */
	public String getTextboxValueByID(String textboxID) {
		waitForElementVisible(BasePageNopCommerceUI.DYNAMIC_TEXTBOX_BY_ID, textboxID);
		return getElementAttribute(BasePageNopCommerceUI.DYNAMIC_TEXTBOX_BY_ID, "value", textboxID);
	}

	public UserRegisterPageObject openRegisterPage() {
		openPageUrl(BasePageNopCommerceUI.REGISTER_LINK);
		return PageGeneratorManage.getUserRegisterPage(driver);
	}

	public void openPageAtMyAccountByPageName(String pageName) {
		waitForElementClickable(BasePageNopCommerceUI.DYNAMIC_PAGE_AT_MY_ACCOUNT_AREA, pageName);
		clickToElement(BasePageNopCommerceUI.DYNAMIC_PAGE_AT_MY_ACCOUNT_AREA, pageName);
	}
}
