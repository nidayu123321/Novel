package spider.webSite;

import java.sql.*;
import  java.util.Date;

public class JDBCTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {


        String url = "jdbc:mysql://localhost:3306/nefu";
        //此处有可能出现错误，错误的原因，MySQL版本太低，出现utf8等编码不兼容的情况，
        //在url后面可以按照以下格式添加内容：
        //String url = "jdbc:mysql://localhost:3306/nefu?
        //useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String uname = "root";
        String pwd  = "root";
        String sql = "select id, username ,password ,createtime from user ";

        Class.forName("com.mysql.jdbc.Driver");

        Connection conn = DriverManager.getConnection(url, uname, pwd);


        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()){
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String password = rs.getString("password");
            Date createtime = rs.getTimestamp("createtime");

            System.out.println("id = " + id);
            System.out.println("username = " + username);
            System.out.println("password = " + password);
            System.out.println("createtime = " + createtime);

        }

        rs.close();
        stmt.close();
        conn.close();

    }
}