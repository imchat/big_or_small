package botapi.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * **************************************************************
 * <p>
 * Desc:
 * User: jianguangluo
 * Date: 2018-12-27
 * Time: 14:39
 * <p>
 * **************************************************************
 */
public class HttpClient {
    private static final int TIMEOUT = 30000;

    /**
     * post json
     *
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String post(String url, String body) throws Exception {
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("req:" + body);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(body, "UTF-8"));

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();
        httpPost.setConfig(requestConfig);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String responseContent = EntityUtils.toString(entity, "UTF-8");
        response.close();
        httpClient.close();

        System.out.println("res:" + responseContent);
        return responseContent;
    }

    /**
     * post multipart
     *
     * @param url
     * @param map
     * @param file
     * @return
     * @throws Exception
     */
    public static String postMultipart(String url, Map<String, String> map, File file) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(TIMEOUT)
                .setSocketTimeout(TIMEOUT).build();
        httpPost.setConfig(requestConfig);

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", inputStream, ContentType.create("multipart/form-data"), file.getName());

            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    builder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.MULTIPART_FORM_DATA));
                }
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            entity = response.getEntity();
            String responseContent = EntityUtils.toString(entity, "UTF-8");
            response.close();
            httpClient.close();

            System.out.println("res:" + responseContent);
            return responseContent;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return "";
    }

    /**
     * post multipart
     *
     * @param url
     * @param map
     * @param filePath
     * @return
     * @throws Exception
     */
    public static String postMultipart(String url, Map<String, String> map, String filePath) throws Exception {
        return postMultipart(url, map, new File(filePath));
    }
}
