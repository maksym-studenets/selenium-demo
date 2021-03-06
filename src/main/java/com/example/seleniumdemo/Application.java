package com.example.seleniumdemo;

import com.example.seleniumdemo.config.ChromeDriverConfig;
import com.example.seleniumdemo.config.SeleniumWebDriverConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.seleniumdemo.Utils.TABLEAU_LOGIN;
import static com.example.seleniumdemo.Utils.TABLEAU_PASS;
import static com.example.seleniumdemo.Utils.TPOT_LOGIN;
import static com.example.seleniumdemo.Utils.TPOT_PASS;
import static com.example.seleniumdemo.Utils.parseArguments;

public class Application {

    private static final Map<String, String> PARAMS = new HashMap<>();

    public static void main(String[] args) {
        parseArguments(args, PARAMS);

        SeleniumWebDriverConfig config = new ChromeDriverConfig();
        WebDriver webDriver = config.webDriver();

        webDriver.get("https://tpot-dev.stotle.io");
        try {
            authenticateNative(webDriver);
            singleWindowSample(webDriver);

            // Tableau charts are embedded in an iframe, and require separate authentication (pop-up)
            tableauSample(webDriver);

            bigTemplateExample(webDriver);

            testFullPageScreenshot(webDriver);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
    }

    private static void singleWindowSample(WebDriver webDriver) throws IOException {
        String searchQuery = "Big 6 Vs. Nat";

        webDriver.findElement(By.className("search-input")).sendKeys(searchQuery);
        webDriver.findElement(By.cssSelector("ul.result-list:first-child button")).click();

        webDriver.get("https://tpot-dev.stotle.io/dashboard/q?p=ODcyIyMwIyNMSU5FIyNRbWxuSURZZ1ZuTXVJRTVoZEdsdmJtRnNJRUZ3Y0hKdmRtRnNJRkpoZEdVPSMjVTBWQlVrTkkjIyQk");
        By chartContainerClassSelector = By.className("google-vector-body");
        WebElement googleVectorBodyElement = new WebDriverWait(webDriver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(chartContainerClassSelector));
        String base64ScreenshotGoogleVectorBody = googleVectorBodyElement.getScreenshotAs(OutputType.BASE64);

        WebElement parentElement = webDriver.findElement(By.className("vector-box"));
        String base64ParentElementScreenshot = parentElement.getScreenshotAs(OutputType.BASE64);

        byte[] googleVectorBodyScreenshotBytes = DatatypeConverter.parseBase64Binary(base64ScreenshotGoogleVectorBody);
        byte[] base64ParentElementBytes = DatatypeConverter.parseBase64Binary(base64ParentElementScreenshot);

        BufferedImage googleVectorBodyImage = ImageIO.read(new ByteArrayInputStream(googleVectorBodyScreenshotBytes));
        BufferedImage parentElementImage = ImageIO.read(new ByteArrayInputStream(base64ParentElementBytes));

        File googleVectorBodyImageFile = new File(getFileName("google-vector-body-image", webDriver));
        ImageIO.write(googleVectorBodyImage, "png", googleVectorBodyImageFile);
        File googleVectorBodyHeaderImageFile = new File(getFileName("google-vector-body-header-image", webDriver));
        ImageIO.write(parentElementImage, "png", googleVectorBodyHeaderImageFile);
    }

    private static void tableauSample(WebDriver webDriver) throws IOException {
        webDriver.get("https://tpot-dev.stotle.io/dashboard/q?p=ODY5IyMwIyNUQUJMRUFVX0VNQkVEREVEIyNVMkZzWlhNZ1lua2dVM1JoZEdVPSMjVTBWQlVrTkkjIyQkNjQjIyR5ZWFyIyNZZWFyIyMiMjAxOCIjIyIyMDE4IiMjZmFsc2UjIzAjI2ZhbHNlIyN0cnVlIyMxMjEjIyRzZWdtZW50IyNzZWdtZW50IyMiU1lTX0RFRkFVTFRfQUxMIiMjIkFsbCIjI2ZhbHNlIyMxIyNmYWxzZSMjdHJ1ZQ%3D%3D");

        String mainWindowHandle = webDriver.getWindowHandle(); // sort-of ID of the main window

        WebElement iframe = webDriver.findElement(By.tagName("iframe"));
        webDriver.switchTo().frame(iframe);

        new WebDriverWait(webDriver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("primary-auth")));
        webDriver.findElement(By.id("primary-auth")).click();
        Set<String> windowHandles = webDriver.getWindowHandles();
        windowHandles.remove(mainWindowHandle);

        String tableauAuthWindow = windowHandles.stream().findFirst().orElse("");
        webDriver.switchTo().window(tableauAuthWindow);
        new WebDriverWait(webDriver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='email']")));
        webDriver.findElement(By.cssSelector("input[type='email']")).sendKeys(PARAMS.get(TABLEAU_LOGIN));
        WebElement passWordElement = webDriver.findElement(By.cssSelector("input[type='password']"));
        passWordElement.sendKeys(PARAMS.get(TABLEAU_PASS));
        passWordElement.sendKeys(Keys.RETURN);

        webDriver.switchTo().window(mainWindowHandle);
        webDriver.switchTo().frame(iframe);
        new WebDriverWait(webDriver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("tabLegendPanel")));

        byte[] tableauScreenshotBytes = webDriver.findElements(By.id("tab-dashboard-region"))
                .stream()
                .findFirst()
                .map(webElement -> webElement.getScreenshotAs(OutputType.BYTES))
                .orElse(new byte[]{});
        BufferedImage tableauScreenshotImage = ImageIO.read(new ByteArrayInputStream(tableauScreenshotBytes));
        File tableauScreenshotFile = new File(getFileName("tableau-screenshot", webDriver));
        ImageIO.write(tableauScreenshotImage, "png", tableauScreenshotFile);
    }

    private static void bigTemplateExample(WebDriver webDriver) throws IOException {
        webDriver.get("https://tpot-dev.stotle.io/dashboard/q?p=MzQyIyMxIyNOT05FIyNUV0Y0SUVSbGJXOD0jI1UwVkJVa05JWDBSU1QxQkVUMWRPIyMkJDUxIyMkY2hhbm5lbCMjY2hhbm5lbCMjWyJTVEFURSBNRURJQ0FJRCIsIlRSSUNBUkUiLCJDT01NRVJDSUFMIiwiTUVESUNBUkUiXSMjWyJTVEFURSBNRURJQ0FJRCIsIlRSSUNBUkUiLCJDT01NRVJDSUFMIiwiTUVESUNBUkUiXSMjZmFsc2UjIzAjI3RydWUjI2ZhbHNlIyM2NiMjJGdlbyMjZ2VvIyNbIkFLLS1TdGF0ZSIsIkFMLS1TdGF0ZSIsIk5KLS1TdGF0ZSIsIk5NLS1TdGF0ZSIsIk5ILS1TdGF0ZSIsIk5ZLS1TdGF0ZSIsIkZMLS1TdGF0ZSIsIk1JLS1TdGF0ZSIsIlROLS1TdGF0ZSIsIlRYLS1TdGF0ZSIsIk9SLS1TdGF0ZSJdIyNbIkFsYXNrYSIsIkFsYWJhbWEiLCJOZXcgSmVyc2V5IiwiTmV3IE1leGljbyIsIk5ldyBIYW1wc2hpcmUiLCJOZXcgWW9yayIsIkZsb3JpZGEiLCJNaWNoaWdhbiIsIlRlbm5lc3NlZSIsIlRleGFzIiwiT3JlZ29uIl0jI2ZhbHNlIyMxIyN0cnVlIyNmYWxzZSMjOTUjIyRhY2NvdW50IyNhY2NvdW50IyMiU1lTX0RFRkFVTFRfQUxMIiMjIkFsbCIjI2ZhbHNlIyMyIyNmYWxzZSMjdHJ1ZQ%3D%3D");
        By templateContainer = By.className("dashboard-page");

        new WebDriverWait(webDriver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div[id^='ready-']")));
        WebElement templateContainerElement = webDriver.findElement(templateContainer);
        byte[] templateCutScreenshotBytes = templateContainerElement.getScreenshotAs(OutputType.BYTES);
        BufferedImage templateCutImage = ImageIO.read(new ByteArrayInputStream(templateCutScreenshotBytes));

        WebElement vectorDownBelowElement = webDriver.findElement(By.id("vector1212-vector"));
        byte[] vectorDownInTheTemplateBytes = vectorDownBelowElement.getScreenshotAs(OutputType.BYTES);
        ImageIO.read(new ByteArrayInputStream(vectorDownInTheTemplateBytes));

        // Trying to make a full page screenshot. Implementation will vary significantly depending on a specific driver.
        Screenshot fullPageScreenshot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .shootingStrategy(ShootingStrategies.viewportPasting(2_000))
                .takeScreenshot(webDriver, templateContainerElement);
        BufferedImage fullTemplateScreenshot = fullPageScreenshot.getImage();

        WebElement lastElement = webDriver.findElement(By.id("vector1217-vector"));
        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
        jsExecutor.executeScript("arguments[0].scrollIntoView(true)", lastElement);

        String screenshotAs = templateContainerElement.getScreenshotAs(OutputType.BASE64);
    }

    private static void testFullPageScreenshot(WebDriver webDriver) throws IOException {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
        jsExecutor.executeScript("window.open('about:blank', '_blank');");
        ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
        webDriver.switchTo().window(tabs.get(1));
        webDriver.get("https://google.com");

        webDriver.findElement(By.cssSelector("input[name=q]")).sendKeys("selenium webdriver");
        webDriver.findElement(By.cssSelector("input[type=submit]")).click();

        new WebDriverWait(webDriver, Duration.ofSeconds(3));
        WebElement bodyElement = webDriver.findElement(By.tagName("body"));
        Screenshot fullPageScreenshot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .shootingStrategy(ShootingStrategies.viewportPasting(2_000))
                .takeScreenshot(webDriver, bodyElement);
        BufferedImage fullPageScreenshotImage = fullPageScreenshot.getImage();
        File fullPageScreenshotFile = new File(getFileName("full-page-screenshot", webDriver));
        ImageIO.write(fullPageScreenshotImage, "png", fullPageScreenshotFile);
    }

    private static void authenticateNative(WebDriver webDriver) {
        webDriver.findElement(By.cssSelector("input[type='email']")).sendKeys(PARAMS.get(TPOT_LOGIN));
        webDriver.findElement(By.cssSelector("input[type='password']")).sendKeys(PARAMS.get(TPOT_PASS));
        webDriver.findElement(By.className("login-button")).click();
        new WebDriverWait(webDriver, Duration.ofSeconds(3))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("user-image")));
    }

    private static String getFileName(String title, WebDriver webDriver) {
        final String outputFileFormat = "src/main/resources/%s_%s.png";
        return switch (webDriver) {
            case ChromeDriver ignored -> outputFileFormat.formatted("chrome", title);
            case FirefoxDriver fd -> outputFileFormat.formatted("firefox", title);
            default -> outputFileFormat.formatted("UNKNOWN", title);
        };
    }
}
