package spider.webSite;

import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import spider.HttpClientFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 * @author nidayu
 * @Description:
 * @date 2016/5/9
 */
public class DingDian extends HttpClientFactory {
    private CloseableHttpClient httpClient;

    private final int pageSize = 45;

    public DingDian(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }

    private void clawChapter(String url, String chapter){
        String[][] headers = {
                {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"},
                {"User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36"},
                {"Referer", url},
                {"Upgrade-Insecure-Requests", "1"},
                {"Connection", "keep-alive"}
        };
        String response = getUrl(url, headers, "GBK");
        if (response == null){
            return;
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String href = Jsoup.parse(response).select("a").stream().filter(a -> a.text().contains(chapter)).findFirst()
                .map(a -> a.attr("href")).get();
        String nextUrl = url + href;
        response = getUrl(nextUrl, headers, "GBK");
        if (response == null){
            return;
        }
        parseText(url, response, 1);
    }

    private void parseText(String url, String response, int index){
        if (index % 5 == 0){
            try {
                BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
                System.out.print(strin.readLine());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Document doc = Jsoup.parse(response);
        if (doc.select("h1") != null){
            print(doc.select("h1").first().text());
        }
        if (doc.select("#contents")!=null){
            String bookText = doc.select("#contents").text();
            String[] listInfo = bookText.split(" ");
            for (String i : listInfo){
                String text = i.replace(" ", "").replace("&nbsp;", "");
                int temp = text.length() / pageSize;
                for (int j = 0; j<= temp; j++){
                    if (j == temp) {
                        print(text.substring(pageSize * j, text.length()));
                    } else {
                        print(text.substring(pageSize * j, pageSize * (j+1)));
                    }
                }
            }
            if (response.contains("下一")){
                String href = Jsoup.parse(response).select("a").stream().filter(a -> a.text().contains("下一")).findFirst()
                        .map(a -> a.attr("href")).get();
                String[][] headers = {
                        {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"},
                        {"User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36"},
                        {"Referer", url},
                        {"Upgrade-Insecure-Requests", "1"},
                        {"Connection", "keep-alive"}
                };
                href = href.substring(href.lastIndexOf("/") + 1);
                response = getUrl(url+href, headers, "gbk");
                if (response == null){
                    return;
                }
                parseText(url, response, index+1);
            }
        }
    }

    private void getList(String url){
        String[][] headers = {
                {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"},
                {"User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36"},
                {"Referer", url},
                {"Upgrade-Insecure-Requests", "1"}
        };
        String response = getUrl(url, headers, "GBK");
        if (response == null){
            return;
        }
        Elements eles = Jsoup.parse(response).select("a");
        eles.stream().forEach(a -> print(a.text()));
    }

    private void searchBook(String bookName){
        try {
            bookName = URLEncoder.encode(bookName, "gbk");
        }catch (Exception e){
            e.printStackTrace();
        }
        String source = "http://so.23wx.com/cse/search?s=15772447660171623812&entry=1&q=" + bookName;
        print(getUrl(source, null, "utf-8"));
    }

    private void getPaiHangBang(){
        String url = "http://www.biquge.tw/nweph.html";
        print(getUrl(url));
    }

    public static void main(String[] args){
        String url = "http://www.23wx.com/html/54/54775/";
//        String url = "http://www.23wx.com/html/15/15069/"; //飞天
//        String url = "http://www.23wx.com/html/58/58671/"; //龙王传说
        String chapter = "第四百二十一章";
//        String url = "http://www.23wx.com/html/55/55519/"; //雪鹰领主
//        String chapter = "第23篇 第8章";
        CloseableHttpClient httpClient= getInstance();
        DingDian dingDian = new DingDian(httpClient);
        dingDian.searchBook("重生完美时代");
//        dingDian.getList(url);
//        dingDian.clawChapter(url, chapter);
//        dingDian.getPaiHangBang();
    }


}
