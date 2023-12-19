package spider.webSite;

import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import spider.HttpClientFactory;
import util.FileUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 * Created by nidayu on 17/1/5.
 */
public class BiQuGe extends HttpClientFactory {

    private CloseableHttpClient httpClient;

    private final int pageSize = 50;

    private final String firstPre = "http://www.xinbqg.la/";

    public BiQuGe(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }

    private void clawChapter(String url, String chapter){
        String response = getUrl(url, null, "utf-8");
        String href = Jsoup.parse(response).select("a").stream().filter(a -> a.text().contains(chapter)).findFirst()
                .map(a -> a
                        .attr("href")).get();
        String nextUrl = firstPre + href;
        response = getUrl(nextUrl, null, "utf-8");
//        print(response);
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
//        print(response);
        Document doc = Jsoup.parse(response);
        if (doc.select("#content")!=null){
            String bookText = doc.select("#content").text();
            String[] listInfo = bookText.split(" ");
            for (String i:listInfo){
                String text = i.replace(" ", "");
                int temp = text.length() / pageSize;
                for (int j = 0; j<= temp; j++){
                    if (j == temp) {
                        String info = text.substring(pageSize * j, text.length());
                        if (!"　　".equals(info.trim())){
                            print(info);
                        }
                    }else{
                        String info = text.substring(pageSize * j, pageSize * (j+1));
                        if (!"　　".equals(info.trim())){
                            print(info);
                        }
                    }
                }
            }
            if (response != null && response.contains("下一章")){
                String href = Jsoup.parse(response).select("a").stream().filter(a -> a.text().contains("下一章")).findFirst()
                        .map(a -> a.attr("href")).get();
                String nextUrl = firstPre + href;
                response = getUrl(nextUrl, null, "utf-8");
                parseText(url, response, index+1);
            }
        }
    }

    private void getList(String url){
        String response = getUrl(url, null, "utf-8");
        print(response);
        Elements as = Jsoup.parse(response).select("#list a");
        for (int i = 0; i < as.size(); i ++){
            print(as.get(i).text());
        }
    }


    public static void main(String[] args){
        CloseableHttpClient httpClient = getInstance();
        BiQuGe biQuGe = new BiQuGe(httpClient);

        String url = "http://www.xinbqg.la/62/62042/"; // 苟在。。。
//        url = "http://www.qu-la.com/booktxt/24447310116/"; //似水流年
//        url = "http://www.xinbqg.la/112/112806/"; // 神话纪元
        url = "http://www.xinbqg.la/114/114397/"; // 都重生了
//        url = "http://www.xinbqg.la/2/2302/"; // 四万年
        url = "http://www.xinbqg.la/118/118662/";
        String chapter = "第213";
//        chapter = "第一百八十八章";
//        biQuGe.searchBook("重生之似水流年");
//        biQuGe.getList(url);
        biQuGe.clawChapter(url, chapter);
//        biQuGe.getPaiHangBang();

//        String text = FileUtil.readFile("/Users/nidayu/Documents/workspace/自己测试文本/b.html");
//        biQuGe.printText(text);
    }

    private void printText(String text){
        int temp = text.length() / pageSize;
        for (int j = 0; j<= temp; j++){
            if (j == temp) {
                String info = text.substring(pageSize * j, text.length());
                print(info);
            }else{
                String info = text.substring(pageSize * j, pageSize * (j+1));
                print(info);
            }
        }
    }

    private void searchBook(String bookName){
        try {
            bookName = URLEncoder.encode(bookName, "gbk");
        }catch (Exception e){
            e.printStackTrace();
        }
        String source = "http://zhannei.baidu.com/cse/search?ie=gbk&s=2758772450457967865&q=" + bookName;
//        String source = "http://zhannei.baidu.com/cse/search?s=16829369641378287696&q=" +bookName+
//                "&isNeedCheckDomain=1&jump=1";
        print(getUrl(source));
    }

    // 排行榜信息
    private void getPaiHangBang(){
        String url = "http://www.biquge.tw/nweph.html";
        print(getUrl(url));
    }

}
