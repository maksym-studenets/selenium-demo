package com.example.seleniumdemo.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class ChromeDriverConfig implements SeleniumWebDriverConfig {

    static {
        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
    }

    @Override
    public WebDriver webDriver() {
        WebDriver driver = getDriverOptions();
        //
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        return driver;
    }

    private WebDriver getDriverOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        //chromeOptions.addArguments("headless");
        chromeOptions.addArguments("disable-gpu");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("window-size=1280x800");
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.addArguments("--no-check-certificate");
        return new ChromeDriver(chromeOptions);
    }
}
