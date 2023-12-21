package spider.webSite;

import util.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import  java.util.Date;

public class JDBCTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        String url = "jdbc:mysql://localhost:3306/test";

        String uname = "root";
        String pwd  = "123456";
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url, uname, pwd);

        // 查询
//        String sql = "select url from amazon_data ";
//        Statement stmt = conn.createStatement();
//        ResultSet rs = stmt.executeQuery(sql);
//        while (rs.next()){
//            String username = rs.getString("url");
//            System.out.println("url = " + username);
//        }
//        rs.close();
//        stmt.close();





        Path pd = Paths.get("src","data", "spider.txt");
        String text = FileUtil.readFile(pd.toAbsolutePath().toString());
        String[] urls = text.split("\n");
        // 插入
//        String sql = "INSERT INTO amazon_data (url) VALUES (?)";
        String sql = "update amazon_data set company_name = ?, country = ? where url = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        for (int i = 0; i < 1; i++){
            try {
                // 设置参数的值
                preparedStatement.setString(1, "aa");
                preparedStatement.setString(2, "123");
                preparedStatement.setString(3, urls[i]);
                // 执行预编译的语句
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Insertion successful. Rows affected: " + rowsAffected);
                } else {
                    System.out.println("Insertion failed.");
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        // 关闭连接和PreparedStatement
        preparedStatement.close();


        conn.close();
    }
}