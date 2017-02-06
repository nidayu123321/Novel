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
 * Created by nidayu on 17/1/5.
 */
public class BiQuGe extends HttpClientFactory {

    private CloseableHttpClient httpClient;

    private final int pageSize = 50;

    public BiQuGe(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }

    private void clawChapter(String url, String chapter){
        String response = getUrl(url);
        String href = Jsoup.parse(response).select("a").stream().filter(a -> a.text().contains(chapter)).findFirst()
                .map(a -> a
                        .attr("href")).get();
        String nextUrl = "http://www.biquge.tw" + href;
        response = getUrl(nextUrl);
        parseText(url, response, 1);
    }


    private void parseText(String url, String response, int index){
        if (index%5==0){
            try {
                BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("请输入回车继续。。。" + strin.readLine());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Document doc = Jsoup.parse(response);
        if (doc.select("#content")!=null){
            String bookText = doc.select("#content").text();
            String[] listInfo = bookText.split(" ");
            for (String i:listInfo){
                String text = i.replace(" ", "");
                int temp = text.length() / pageSize;
                for (int j = 0; j<= temp; j++){
                    if (j == temp) {
                        print(text.substring(pageSize * j, text.length()));
                    }else{
                        print(text.substring(pageSize * j, pageSize * (j+1)));
                    }
                }

            }
            if (doc.select("#pager_next")!=null){
                String nextUrl = doc.select("#pager_next").attr("href");
                response = getUrl(url+nextUrl);
                parseText(url, response, index+1);
            }
        }
    }

    private void getList(String url){
        String response = getUrl(url);
        Elements as = Jsoup.parse(response).select("dd a");
        for (int i = 0; i < as.size(); i ++){
            print(as.get(i).text());
        }
    }


    public static void main(String[] args){
        CloseableHttpClient httpClient= getInstance();
        BiQuGe biQuGe = new BiQuGe(httpClient);
//        String url = "http://www.biquge.tw/0_498/"; //君临
//        String chapter = "第一百九十五";

        String url = "http://www.biquge.tw/64_64451/";
        String chapter = "第七百八十八章";

//        biQuGe.searchBook("重生完美时代");
//        biQuGe.getList(url);
        biQuGe.clawChapter(url, chapter);
//        biQuGe.getPaiHangBang();

    }

    // 搜索小说链接
    private void searchBook(String bookName){
        try {
            bookName = URLEncoder.encode(bookName, "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        String source = "http://zhannei.baidu.com/cse/search?s=16829369641378287696&q=" +bookName+
                "&isNeedCheckDomain=1&jump=1";
        print(getUrl(source));
    }

    // 排行榜信息
    private void getPaiHangBang(){
        String url = "http://www.biquge.tw/nweph.html";
        print(getUrl(url));
    }

}
