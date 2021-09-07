package com.example.seleniumdemo;

import com.example.seleniumdemo.config.ChromeDriverConfig;
import com.example.seleniumdemo.config.SeleniumWebDriverConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;

import static com.example.seleniumdemo.Credentials.TPOT_LOGIN;
import static com.example.seleniumdemo.Credentials.TPOT_PASS;

public class Application {

    public static void main(String[] args) throws Exception {
        SeleniumWebDriverConfig config = new ChromeDriverConfig();
        WebDriver webDriver = config.webDriver();

        webDriver.get("https://tpot-dev.stotle.io");
        authenticateNative(webDriver);

        singleWindowSample(webDriver);



        // findElement(By...) will throw an exception if no matching element is found
        //WebElement element = webDriver.findElement(By.className("google-vector-body"));



        webDriver.quit();
    }

    private static void singleWindowSample(WebDriver webDriver) throws IOException {
        String searchQuery = "Big 6 Vs. Nat";
        char[] chars = searchQuery.toCharArray();
        for (int i = 0; i < chars.length; i++) {

        }

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
    }

    private static void authenticateNative(WebDriver webDriver) {
        webDriver.findElement(By.cssSelector("input[type='email']")).sendKeys(TPOT_LOGIN);
        webDriver.findElement(By.cssSelector("input[type='password']")).sendKeys(TPOT_PASS);
        webDriver.findElement(By.className("login-button")).click();
    }
}
