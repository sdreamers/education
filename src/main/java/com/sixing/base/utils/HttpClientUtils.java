package com.sixing.base.utils;

import com.sixing.base.utils.exception.ServiceException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.ResourceBundle;

public class HttpClientUtils {

    // utf-8字符编码
    private static final String CHARSET_UTF_8 = "utf-8";

    // HTTP内容类型。xml格式
    private static final String CONTENT_TYPE_TEXT_HTML = "text/xml";

    // HTTP内容类型。form格式
    private static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";

    // HTTP内容类型。json格式
    private static final String CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8";

    public static CloseableHttpClient acceptsUntrustedCertsHttpClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder b = HttpClientBuilder.create();

        // setup a Trust Strategy that allows all certificates.
        //
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        b.setSSLContext(sslContext);

        // don't check Hostnames, either.
        // -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if
        // you don't want to weaken
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

        // here's the special part:
        // -- need to create an SSL Socket Factory, to use our weakened "trust
        // strategy";
        // -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslSocketFactory).build();

        // now, we create connection-manager using our Registry.
        // -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connMgr.setMaxTotal(200);
        connMgr.setDefaultMaxPerRoute(100);
        b.setConnectionManager(connMgr);

        // finally, build the HttpClient;
        // -- done!
        CloseableHttpClient client = b.build();
        return client;
    }

    /**
     * 获得当前用户请求的IP
     * 
     * @return IP
     */
    public static String getUserIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String unknown = "unknown";
        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && !ip.equals(unknown)) {
            return ip;
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.equals(unknown)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip != null && !ip.equals(unknown)) {
            return ip;
        }
        ip = request.getRemoteAddr();
        if (ip != null && !ip.equals(unknown)) {
            return ip;
        }
        return null;
    }

    /**
     * 获取服务器IP
     * @return
     */
    public static String getServerIpAddress(){
        String serviceIpAddress = null;
        try {
            InetAddress address = InetAddress.getLocalHost();
            serviceIpAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (Exception e2){
            e2.printStackTrace();
        }
        return serviceIpAddress;
    }

    /**
     * 获得当前请求的浏览器信息
     *
     * @return 浏览器信息
     */
    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getHeader("User-Agent");
    }

    /**
     * 获得当前请求的全球资源定位器
     *
     * @param request 当前请求实例
     * @return 全球资源定位器
     */
    public static String getRequestUrl(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getRequestURL().toString();
    }

    /**
     * 获得当前请求的传参
     * 
     * @param request 当前请求实例
     * @return &=式传参
     */
    public static String getRequestParams(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Map<String, String[]> params = request.getParameterMap();
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            int i = 0;
            int countValues = values.length;
            for (; i < countValues; i++) {
                buffer.append(key);
                buffer.append("=");
                buffer.append(StringUtils.trimToEmpty(values[i]));
                buffer.append("&");
            }
        }
        String queryString = buffer.toString();
        if (queryString.equals("")) {
            return queryString;
        }
        else {
            return queryString.substring(0, queryString.length() - 1);
        }
    }

    /**
     * 获得RequestBody模式的请求传参
     * 
     * @param request 当前请求实例
     * @return {"productList":[{"productBasicInfoId":"821","productBasicSpecificationId":"1305","num":1,"cost":"1.01","price":"0.00","comment":"1","purchasePrice":"1"},{"productBasicInfoId":"821","productBasicSpecificationId":"1307","num":2,"cost":"2.02","price":"0.00","comment":"2","purchasePrice":"2"}],"warehouseId":"1","status":1,"shippingPrice":"1","supplierId":"1002","purchasePaymentMethod":"0"}
     */
    public static String getRequestBody(HttpServletRequest request) {
        String requestBody = null;
        if (request != null) {
            try {
                Reader in = new InputStreamReader(request.getInputStream());
                BufferedReader reader = new BufferedReader(in);
                
                requestBody = reader.readLine();
                String line = "";
                while ((line = reader.readLine()) != null) {  
                    requestBody += line;
                }
                in.close();
                reader.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return requestBody;
    }

    /**
     * 发送Http请求
     *
     * @param httpUriRequest
     * @return
     */
    private static String sendHttpRequest(HttpUriRequest httpUriRequest) throws ServiceException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        // 响应内容
        String responseContent = null;
        try {
            httpClient = acceptsUntrustedCertsHttpClient();
            // 执行请求
            response = httpClient.execute(httpUriRequest);
            // 得到响应实例
            HttpEntity entity = response.getEntity();

            // 判断响应状态
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                responseContent = EntityUtils.toString(entity, CHARSET_UTF_8);
                EntityUtils.consume(entity);
            } else {
                throw new ServiceException("HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
            }

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException(e);
        } finally {
            // 释放资源
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseContent;
    }

    /**
     * 发送Get请求
     *
     * @param httpUrl 地址
     */
    public static String sendHttpGet(String httpUrl) throws ServiceException {
        // 创建httpGet请求
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpRequest(httpGet);
    }

    /**
     * 发送Get请求
     *
     * @param maps 参数
     */
    public static String sendHttpGet(String httpUrl, Map<String, String> maps) throws ServiceException {
        String param = StringUtils.convertStringParameter(maps);
        return sendHttpGet(httpUrl + "?" + param);
    }

    /**
     * 发送Get请求
     *
     * @param httpUrl 地址
     */
    public static String sendHttpGetWithHeaders(String httpUrl,Map<String, String> headers) throws ServiceException {
        // 创建httpGet请求
        HttpGet httpGet = new HttpGet(httpUrl);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        return sendHttpRequest(httpGet);
    }

    /**
     * 发送Post请求
     *
     * @param httpUrl 地址
     */
    public static String sendHttpPost(String httpUrl) throws ServiceException {
        // 创建httpPost请求
        HttpPost httpPost = new HttpPost(httpUrl);
        return sendHttpRequest(httpPost);
    }

    /**
     * 发送Post请求
     *
     * @param maps 参数
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> maps) throws ServiceException {
        String param = StringUtils.convertStringParameter(maps);
        return sendHttpPost(httpUrl, param, null);
    }

    /**
     * 发送Post请求
     *
     * @param httpUrl 地址
     * @param params  参数(格式:key1=value1&key2=value2)
     */
    public static String sendHttpPost(String httpUrl, String params, Integer connectTimeOut) throws ServiceException {
        // 创建httpPost请求
        HttpPost httpPost = new HttpPost(httpUrl);
        // 设置超时时间
        if (connectTimeOut != null && connectTimeOut > 0) {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeOut).build();
            httpPost.setConfig(config);
        }
        // 设置参数
        if (StringUtils.isNotBlank(params)) {
            StringEntity stringEntity = new StringEntity(params, CHARSET_UTF_8);
            stringEntity.setContentType(CONTENT_TYPE_FORM_URL);
            httpPost.setEntity(stringEntity);
        }
        return sendHttpRequest(httpPost);
    }

    /**
     * 发送Post请求 Json数据
     *
     * @param httpUrl    地址
     * @param paramsJson 参数(格式 Json)
     */
    public static String sendHttpPostJson(String httpUrl, String paramsJson) throws ServiceException {
        // 创建httpPost请求
        HttpPost httpPost = new HttpPost(httpUrl);
        // 设置参数
        if (StringUtils.isNotBlank(paramsJson)) {
            StringEntity stringEntity = new StringEntity(paramsJson, CHARSET_UTF_8);
            stringEntity.setContentType(CONTENT_TYPE_JSON_URL);
            httpPost.setEntity(stringEntity);
        }
        return sendHttpRequest(httpPost);
    }

    /**
     * 发送Post请求 Xml数据
     *
     * @param httpUrl   地址
     * @param paramsXml 参数(格式 Xml)
     */
    public static String sendHttpPostXml(String httpUrl, String paramsXml) throws ServiceException {
        // 创建httpPost请求
        HttpPost httpPost = new HttpPost(httpUrl);
        // 设置参数
        if (StringUtils.isNotBlank(paramsXml)) {
            StringEntity stringEntity = new StringEntity(paramsXml, CHARSET_UTF_8);
            stringEntity.setContentType(CONTENT_TYPE_TEXT_HTML);
            httpPost.setEntity(stringEntity);
        }
        return sendHttpRequest(httpPost);
    }
    
    /**
     * 获取当前运行实例的域名
     * 
     * @return http://xxxx/
     */
    public static String getCurrentDomain() throws ServiceException {
        String currentDomain = "";
        try {
            currentDomain = ResourceBundle.getBundle("common").getString("http.domain");
        }
        catch (Exception ex) {

        }
        return currentDomain;
    }

    /**
     * 发送Post请求
     *
     * @param maps 参数
     */
    public static String sendHttpPost(String httpUrl, Map maps, Integer connectTimeOut) throws ServiceException {
        String param = StringUtils.convertStringParameter(maps);
        return sendHttpPost(httpUrl, param, connectTimeOut);
    }
}