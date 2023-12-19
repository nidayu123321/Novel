package spider.webSite;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.impl.client.CloseableHttpClient;
import spider.HttpClientFactory;
import util.DateUtils;

import java.util.Date;

public class Stock extends HttpClientFactory {

    private CloseableHttpClient httpClient;

    private final int pageSize = 45;

    private final String firstPre = "https://xueqiu.com";

    public Stock(CloseableHttpClient httpClient){
        this.httpClient = httpClient;
    }

    private void clawPrice(String[] codeA, String[] codeHK){
        String[][] headers = {
                {"Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"},
                {"User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36"},
                {"Referer", firstPre},
                {"Upgrade-Insecure-Requests", "1"},
                {"Connection", "keep-alive"},
                {":authority", "stock.xueqiu.com"},
                {"cookie", "snbim_minify=true; Hm_lpvt_fe218c11eab60b6ab1b6f84fb38bcc4a=1663064411; device_id=ee22cd05cb485d0a33eb3498772ea0d1; s=c711seh69z; bid=9dca2b50974204ac4f111ca7b514b855_lgeenbyh; cookiesu=111692350007857; Hm_lvt_1db88642e346389874251b5a1eded6e3=1699837922; remember=1; xq_a_token=b365cda4d44eb87496d089e036334117b5d8c656; xqat=b365cda4d44eb87496d089e036334117b5d8c656; xq_id_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJ1aWQiOjYxMTM3NzAwMTMsImlzcyI6InVjIiwiZXhwIjoxNzA0NTQxODE4LCJjdG0iOjE3MDE5OTgwODA5NTcsImNpZCI6ImQ5ZDBuNEFadXAifQ.QBffiPliNc9BvDFL5iR48-gqq9oYxDcVwdmTXo17wx4k-T7yZqnFF-WMEH1BfE3elsCqAplPORxje1mZNKP4ouuMHSrWNSmRw7iAs13t7t1OPQMSBJdghsH36rC4cBKX4VS-Ui2YwuwUSS_ifcAKemDoM9vZUW6_tSZNGCsf4C_bKR7_F7gK9GWS6ltffti9vPAKqGP3K30p9TvJkxPAy9z5nzJVMJKyIsil2QRp6ZBdmP26SS9y4KsJCUEjXVhw4Kve4d25WJopGrt5LKqSo5yH1-GAg9CIQJlsseMu-6-uZUQWWuBwhrh2iuPcnlmDy1USgZfCgJy07QBLc5Vm9w; xq_r_token=93e0e0dacdb57a5dec5c35ae83e2424dad2640db; xq_is_login=1; u=6113770013; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1701999340"}
        };

        while (true) {

            for (int i = 0; i < codeA.length; i++) {
                String zsyh = "https://stock.xueqiu.com/v5/stock/realtime/quotec.json?symbol=" + codeA[i].split("-")[0] + "&_=" + System.currentTimeMillis();
                String zs = getUrl(zsyh, headers, "UTF-8");
                JSONObject zsres = JSONObject.parseObject(zs);
                JSONArray zsarr = zsres.getJSONArray("data");
                JSONObject zsob = zsarr.getJSONObject(0);

                String zslog = String.format("%s -- %s -- %s -- %s", DateUtils.format(new Date(), DateUtils.FORMATTER_Y_M_D_HMS), codeA[i].split("-")[1], zsob.getString("current"), zsob.getString("percent") + "%");
                System.out.println(zslog);

            }



            String hszs = "https://stock.xueqiu.com/v5/stock/realtime/quotec.json?symbol=HKHSI";
            String resp = getUrl(hszs, headers, "UTF-8");
            JSONObject res = JSONObject.parseObject(resp);
            JSONArray arr = res.getJSONArray("data");
            JSONObject ob = arr.getJSONObject(0);

            String log = String.format("%s -- %s -- %s -- %s", DateUtils.format(new Date(), DateUtils.FORMATTER_Y_M_D_HMS), "恒生指数", ob.getString("current"), ob.getString("percent") + "%");
            System.out.println(log);

            for (int i = 0; i < codeHK.length; i++) {
                String url = String.format("https://stock.xueqiu.com/v5/stock/quote.json?symbol=%s&extend=detail", codeHK[i]);
                String response = getUrl(url, headers, "UTF-8");
//                System.out.println(response);
                JSONObject jsonObj = JSONObject.parseObject(response);
                JSONObject data = jsonObj.getJSONObject("data");
                JSONObject quote = data.getJSONObject("quote");

                String info = String.format("%s -- %s -- %s -- %s", DateUtils.format(new Date(), DateUtils.FORMATTER_Y_M_D_HMS), quote.getString("name"), quote.getString("current"), quote.getString("percent") + "%");
                System.out.println(info);
            }
            try {
                Thread.sleep(20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args){

        String[] codeA = new String[]{"SH000001-上证指数", "SH600036-招行", "SH513050-中概", "SZ000333-美的", "SH601021-春秋", "SZ000651-格力", "SH600584-长电", "SZ002142-波行", "SZ300750-宁德", "SZ300760-迈瑞", "SH600887-伊利", "SH600009-上机"};

        String[] codeHK = new String[]{
                "00700", "03690", "00388", "00772", "01093", "01951", "09988", "01024"};
        CloseableHttpClient httpClient= getInstance();
        Stock stock = new Stock(httpClient);

        stock.clawPrice(codeA, codeHK);
    }

}
