package spider.webSite;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import util.DateUtils;
import util.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class Webdriver {

    public static void main(String[] args) throws Exception {
//        Path pd = Paths.get("src","drivers", "chromedriver.exe"); // 对应chrome114版本
//        System.setProperty("webdriver.chrome.driver", pd.toAbsolutePath().toString());
//        WebDriver driver = new ChromeDriver();

        Path pd = Paths.get("src","drivers", "geckodriver.exe"); // 对应firefox60版本
        System.setProperty("webdriver.gecko.driver", pd.toAbsolutePath().toString());
        WebDriver driver = new FirefoxDriver();

        //打开首页
        driver.navigate().to("https://www.amazon.com/sp?seller=A2VT4H683M5NNA");
        if (driver.findElement(By.id("g")).isDisplayed()) {
            driver.navigate().refresh();
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String data_url = "jdbc:mysql://localhost:3306/test";
            String uname = "root";
            String pwd  = "123456";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(data_url, uname, pwd);

            String sql = "SELECT * from amazon_data where company_name is null and id <= 26000";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()){
                String url = rs.getString("url");
                driver.navigate().to(url);

                WebElement sellerInfo = driver.findElement(By.id("page-section-detail-seller-info"));
                List<WebElement> webElementList = sellerInfo.findElements(By.className("a-row"));
                int size = webElementList.size();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < webElementList.size(); i++){
                    sb.append(webElementList.get(i).getText());
                }
                String info = sb.toString();
                String company_name = webElementList.get(1).getText().replace("Business Name: ", "");
                // 不变
                String detail_address = webElementList.get(3).getText();
                String city = "";
                String area = "";
                String street = "";
                if (size == 8) {
                    city = webElementList.get(4).getText();
                }
                if (size == 9){
                    street = webElementList.get(4).getText();
                    city = webElementList.get(5).getText();
                }
                if (size == 10){
                    street = webElementList.get(4).getText();
                    city = webElementList.get(5).getText();
                    area = webElementList.get(6).getText();
                }
                // 不变
                String province = webElementList.get(size - 3).getText();
                String code = webElementList.get(size - 2).getText();
                String country = webElementList.get(size - 1).getText();

                save(conn, company_name, country, code, province, city, area, street, detail_address, info, url);
            }

        } finally {

            rs.close();
            stmt.close();
            conn.close();

            driver.quit();
        }
    }

    private static void save(Connection conn, String company_name, String country, String code, String province, String city,
                             String area, String street, String detail_address, String info, String url) throws Exception {

        String sql = "update amazon_data set company_name = ?, country = ?, code = ?, province = ?, city = ?, area = ?, street = ?, detail_address = ?, info = ? where url = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        try {
            // 设置参数的值
            preparedStatement.setString(1, company_name);
            preparedStatement.setString(2, country);
            preparedStatement.setString(3, code);
            preparedStatement.setString(4, province);
            preparedStatement.setString(5, city);
            preparedStatement.setString(6, area);
            preparedStatement.setString(7, street);
            preparedStatement.setString(8, detail_address);
            preparedStatement.setString(9, info);
            preparedStatement.setString(10, url);
            // 执行预编译的语句
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("insert success  " + DateUtils.format(DateUtils.FORMATTER_Y_M_D_HMS));
            } else {
                System.out.println("Insertion failed.");
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        // 关闭连接和PreparedStatement
        preparedStatement.close();



    }


}