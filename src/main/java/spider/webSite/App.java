package spider.webSite;
import com.microsoft.playwright.*;
import util.DateUtils;

import java.sql.*;
import java.util.List;


public class App {

    public static void main(String[] args) throws Exception{

        String data_url = "jdbc:mysql://localhost:3306/test";
        String uname = "root";
        String pwd  = "123456";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(data_url, uname, pwd);
        ResultSet rs = null;
        Statement stmt = null;
        Browser browser = null;

        try (Playwright playwright = Playwright.create()) {
            browser = playwright.chromium().launch();

            Page page = browser.newPage();
            page.navigate("https://www.baidu.com/");
            page.reload();

            String sql = "SELECT * from amazon_data where company_name is null and id > 26000 order by id desc";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()){
                String url = rs.getString("url");
                try {
                    page.navigate(url);
                    if (page.querySelector("div#page-section-detail-seller-info") == null) {
                        page.reload();
                    }
                } catch (PlaywrightException e){
                    e.printStackTrace();
                }
                List<ElementHandle> elementHandleList = page.querySelector("div#page-section-detail-seller-info").querySelectorAll("span");
                int size = elementHandleList.size();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < elementHandleList.size(); i++){
//                    System.out.println(elementHandleList.get(i).innerText());
                    sb.append(elementHandleList.get(i).innerText());
                }
                String info = sb.toString();

                String company_name = elementHandleList.get(1).innerText();
                // 不变
                String detail_address = elementHandleList.get(3).innerText();
                String city = "";
                String area = "";
                String street = "";
                if (size == 8) {
                    city = elementHandleList.get(4).innerText();
                }
                if (size == 9){
                    street = elementHandleList.get(4).innerText();
                    city = elementHandleList.get(5).innerText();
                }
                if (size == 10){
                    street = elementHandleList.get(4).innerText();
                    city = elementHandleList.get(5).innerText();
                    area = elementHandleList.get(6).innerText();
                }
                // 不变
                String province = elementHandleList.get(size - 3).innerText();
                String code = elementHandleList.get(size - 2).innerText();
                String country = elementHandleList.get(size - 1).innerText();
                save(conn, company_name, country, code, province, city, area, street, detail_address, info, url);
            }

        } finally {
            rs.close();
            stmt.close();
            conn.close();

            browser.close();
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
