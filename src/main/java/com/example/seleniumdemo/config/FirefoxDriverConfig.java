package com.example.seleniumdemo.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FirefoxDriverConfig implements SeleniumWebDriverConfig {

    static {
        System.setProperty("webdriver.gecko.driver", "drivers/geckodriver");
    }

    @Override
    public WebDriver webDriver() {
        return new FirefoxDriver();
    }
}
