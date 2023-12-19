package spider.webSite;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Webdriver {

    public static void main(String[] args) throws Exception {
//        Path pd = Paths.get("src","drivers", "chromedriver.exe"); // 对应chrome114版本
//        System.setProperty("webdriver.chrome.driver", pd.toAbsolutePath().toString());

        Path pd = Paths.get("src","drivers", "geckodriver.exe"); // 对应firefox60版本
        System.setProperty("webdriver.gecko.driver", pd.toAbsolutePath().toString());

//        WebDriver driver = new ChromeDriver();
        WebDriver driver = new FirefoxDriver();
        // 最大化浏览器
//        driver.manage().window().maximize();
        //打开百度首页
        driver.navigate().to ("https://www.amazon.com/sp?seller=A2VT4H683M5NNA");
        //通过name属性找到搜索输入框
//        WebElement search_input	= driver.findElement(By.name("wd"));
//        //在搜索输入框中输入搜索关键字"耗子尾汁"
//        search_input.sendKeys("耗子尾汁");
//        //递交搜索请求
//        search_input.submit();
//        //等待5秒后自动关闭浏览器
//        Thread.sleep(5000);
//        //关闭浏览器窗口
//        driver.quit();

        // https://www.amazon.com/sp?seller=A2VT4H683M5NNA
    }


}