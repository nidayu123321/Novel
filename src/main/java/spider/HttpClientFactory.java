
package spider;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import util.FileUtil;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author nidayu
 * @Description:4.3  还有设置超时时间，重试次数等
 * @date 2015/8/27
 */
public class HttpClientFactory {

    private static CloseableHttpClient httpClient;
    private static CloseableHttpResponse response;
    private static HttpContext context = null;
    private static PoolingHttpClientConnectionManager connectionManager;

    //遇到以下一些异常自动重试 ToDo 还没测试
    private static HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
        public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            if (executionCount >= 5) {
                // 如果已经重试了5次，就放弃
                return false;
            }
            if (exception instanceof InterruptedIOException) {
                // 超时
                return false;
            }
            if (exception instanceof UnknownHostException) {
                // 目标服务器不可达
                return false;
            }
            if (exception instanceof ConnectTimeoutException) {
                // 连接被拒绝
                return false;
            }
            if (exception instanceof SSLException) {
                // ssl握手异常
                return false;
            }
            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
            if (idempotent) {
                // 如果请求是幂等的，就再次尝试
                return true;
            }
            return false;
        }

    };

    public static CloseableHttpClient getInstance(){
        if (httpClient==null){
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            // ToDo 还没测试
//            httpClientBuilder.setRetryHandler(myRetryHandler);

            // ToDo 还没测试
            // LaxRedirectStrategy可以自动重定向所有的HEAD，GET，POST请求，解除了http规范对post请求重定向的限制。
//            LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
//            httpClientBuilder.setRedirectStrategy(redirectStrategy);

            // 证书信任问题
            try {
                SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
                SSLConnectionSocketFactory socketFactory = null;
                try {
                    sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                    socketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
                httpClientBuilder.setSSLSocketFactory(socketFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
            httpClientBuilder.setDefaultSocketConfig(socketConfig);
            // 保持Cookie
            CookieStore cookieStore = new BasicCookieStore();
            httpClientBuilder.setDefaultCookieStore(cookieStore);

            // 设置代理ip
//            httpClientBuilder.setProxy(new HttpHost("45.62.97.18", 29609));
            httpClient = httpClientBuilder.build();
            return httpClient;
        }
        return httpClient;
    }

    /**
     * 获取响应回来的请求头
     */
    public static void getResHeaders(){
        Arrays.asList(response.getAllHeaders()).stream().forEach(header -> System.out.println(header.getName()+" : "+header.getValue()));
    }

    /**
     * 获取当前cookie信息
     * @return
     */
    public static String getCookie(){
        StringBuffer sb = new StringBuffer();
        try {
            //获取cookie中的各种信息
            CookieStore cookieStore = (CookieStore) context.getAttribute(HttpClientContext.COOKIE_STORE);
            cookieStore.getCookies().stream().forEach(cookie -> sb.append(cookie.getName()).append("=").append(cookie.getValue()).append(";"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb == null ? null : sb.toString();
    }

    public String getUrl(String url){ return getUrl(url, null);}

    public String getUrl(String url, String[][] headers){ return getUrl(url, headers, null);}

    /**
     * get请求
     * @param url
     * @param headers
     * @param charset
     * @return
     */
    public String getUrl(String url, String[][] headers, String charset){
        if (url == null){
            return null;
        }
        String ret = null;
        HttpGet httpget = new HttpGet(url);
        // 设置请求头
        if (headers != null) {
            for (int i=0;i<headers.length;i++) {
                httpget.addHeader(headers[i][0], headers[i][1]);
            }
        }
        try {
            //设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(80000).setConnectTimeout(50000).build();
            httpget.setConfig(requestConfig);

//            System.out.println("executing request " + httpget.getURI());
            context = new BasicHttpContext();
            response = httpClient.execute(httpget);
            try {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    if (charset == null) {
                        ret = EntityUtils.toString(entity, "UTF-8");
                    }else {
                        // 返回编码的设置，默认为utf-8
                        ret = EntityUtils.toString(entity, charset);
                    }
                } else {
                    print(response.getStatusLine().getStatusCode());
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return ret;
    }

    public String postUrl(String url, String[][] params){ return postUrl(url, params, null);}

    public String postUrl(String url, String[][] params, String[][] headers){ return postUrl(url, params, null, headers);}

    /**
     * post请求
     * @param url
     * @param params
     * @param xmlParam
     * @param headers
     * @return
     */
    public String postUrl(String url, String[][] params, String xmlParam, String[][] headers){
        if (url == null){
            return null;
        }
        // 返回信息
        String ret = null;
        HttpPost httppost = new HttpPost(url);
        // 设置请求头
        if (headers != null) {
            for (int i=0;i<headers.length;i++) {
                httppost.addHeader(headers[i][0], headers[i][1]);
            }
        }
        // 设置参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (int i=0;i<params.length;i++){
            formparams.add(new BasicNameValuePair(params[i][0], params[i][1]));
        }
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            // 当参数是xml格式的时候
            if (xmlParam != null) {
                httppost.setEntity(new StringEntity(xmlParam, "text/xml"));
            }
            System.out.println("executing request " + httppost.getURI());
            context = new BasicHttpContext();
            response = httpClient.execute(httppost, context);
            try {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    HttpEntity entity = response.getEntity();
                    ret = EntityUtils.toString(entity, "UTF-8");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return ret;
    }

    public void downloadImgByGet(String url, String filePath){ downloadImgByGet(url, null, filePath);}

    public void downloadImgByGet(String url, String referer, String filePath){ downloadImgByGet(url, referer, null, filePath);}

    public void downloadImgByGet(String url, String referer, String host, String filePath){
        HttpGet httpget = new HttpGet(url);
        try {
            if (referer != null) {
                httpget.addHeader("Referer", referer);
            }
            if (host != null){
                httpget.addHeader("Host", host);
            }
            context = new BasicHttpContext();
            response = httpClient.execute(httpget, context);
            try {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    // 读取为 InputStream，在网页内容数据量大时候推荐使用
                    FileUtil.downloadImg(filePath, response.getEntity().getContent());
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            e.printStackTrace();
        }
    }

    /**
     * 关闭链接
     * @param httpClient
     */
    public static void closeHttpClient(CloseableHttpClient httpClient){
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void print(Object info){ System.out.println(info.toString());}

}
