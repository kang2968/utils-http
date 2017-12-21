package com.ie.common.utilities;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Common http utilities
 * the field status of IEHttpEntity will be -1 when there is a error in sending request or reading response content
 * @author bradly
 * @version 1.0
 */
public class IEHttpUtils {

    /**
     * common content type
     */
    public static class ContentType {

        public static final String json = "application/json;charset=utf-8";

        public static final String form_no_file = "application/x-www-form-urlencoded;charset=utf-8";

        public static final String form_file = "multipart/form-data";

    }

    /**
     * http default charset -> UTF-8
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    private static String BOUNDARY = "----WebKitFormBoundaryob9B3Xqo1e4xup8m";

    private IEHttpUtils() {
    }

    /**
     * get request
     * @param url
     * @param headers add authorization
     * @param params
     * @param connectTimeoutSecond
     * @param readTimeoutSecond
     * @param isSkipReadEntity
     * @return
     */
    public static IEHttpEntity syncGet(final String url, final Map<String, String> headers, final Map<String, String> params, final int connectTimeoutSecond,
                                       final int readTimeoutSecond, final boolean isSkipReadEntity) {
        String tUrl = url;
        if (params != null && params.size() > 0) {
            StringBuilder urlSb = new StringBuilder(url);
            if (url.indexOf("?") == -1) {
                urlSb.append("?");
            }
            params.forEach((k, v) -> {
                try {
                    urlSb.append("&").append(URLEncoder.encode(k, DEFAULT_CHARSET)).append("=")
                            .append(URLEncoder.encode(v, DEFAULT_CHARSET));
                } catch (UnsupportedEncodingException e) {}
            });
            tUrl = urlSb.toString();
        }
        return syncGet(tUrl, headers, connectTimeoutSecond, readTimeoutSecond, isSkipReadEntity);
    }

    /**
     * get request
     * @param url
     * @param headers add authorization
     * @param connectTimeoutSecond
     * @param readTimeoutSecond
     * @param isSkipReadEntity
     * @return
     */
    public static IEHttpEntity syncGet(final String url, final Map<String,String> headers, final int connectTimeoutSecond, final int readTimeoutSecond,
                                       final boolean isSkipReadEntity) {


        try (CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(buildRequestConfig(connectTimeoutSecond, readTimeoutSecond)).build()) {

            HttpGet httpGet = configRequestHeader(new HttpGet(url), null, headers);

            try (CloseableHttpResponse response = client.execute(httpGet)) {
                return handleResponse(response, isSkipReadEntity);
            }
        } catch (Exception e){
            return new IEHttpEntity(e);
        }
    }

    /**
     * post form
     * @param url
     * @param headers
     * @param postParams
     * @param connectTimeoutSecond
     * @param readTimeoutSecond
     * @return
     */
    public static IEHttpEntity syncPostForm(final String url, final Map<String, String> headers, final Map<String, String> postParams, final int connectTimeoutSecond, final int readTimeoutSecond) {
        List<NameValuePair> pairs = new LinkedList<>();
        if (postParams != null && postParams.size() > 0) {
            postParams.forEach((k, v) -> {
                if (isNotBlank(k) && isNotBlank(v)) {
                    pairs.add(new BasicNameValuePair(k, v));
                }
            });
        }
        return syncPostForm(url, headers, pairs, connectTimeoutSecond, readTimeoutSecond);
    }

    /**
     * post form
     * @param url
     * @param headers
     * @param postParams
     * @param connectTimeoutSecond
     * @param readTimeoutSecond
     * @return
     */
    public static IEHttpEntity syncPostForm(final String url, final Map<String,String> headers,
                                            final List<NameValuePair> postParams, final int connectTimeoutSecond, final int readTimeoutSecond) {

        try (CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(buildRequestConfig(connectTimeoutSecond, readTimeoutSecond)).build()) {
            HttpPost httpPost = configRequestHeader(new HttpPost(url), ContentType.form_no_file, headers);
            if (postParams != null && postParams.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(postParams, DEFAULT_CHARSET));
            }

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                return handleResponse(response, false);
            }
        } catch (Exception e) {
            return new IEHttpEntity(e);
        }
    }

    /**
     * post for json
     * @param url
     * @param headers
     * @param jsonString
     * @param connectTimeoutSecond
     * @param readTimeoutSecond
     * @return
     */
    public static IEHttpEntity syncPostJson(final String url, final Map<String,String> headers, final String jsonString,
                                            final int connectTimeoutSecond, final int readTimeoutSecond) {

        try (CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(buildRequestConfig(connectTimeoutSecond, readTimeoutSecond)).build()) {
            HttpPost httpPost = configRequestHeader(new HttpPost(url), ContentType.json, headers);
            httpPost.setEntity(new StringEntity(jsonString, DEFAULT_CHARSET));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                return handleResponse(response, false);
            }
        } catch (Exception e) {
            return new IEHttpEntity(e);
        }
    }

    /**
     * post for file form
     * @param url
     * @param headers
     * @param postParams
     * @param fileParams
     * @param connectTimeoutSecond
     * @param readTimeoutSecond
     * @return
     */
    public static IEHttpEntity syncPostFileForm(final String url, final Map<String,String> headers,
                                                      final List<NameValuePair> postParams, final List<IEIFileValuePair> fileParams, final int connectTimeoutSecond, final int readTimeoutSecond) {
        HttpURLConnection conn = null;
        OutputStream os = null;
        InputStream is = null;
        BufferedReader br = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "close");
            conn.setRequestProperty("Charset", DEFAULT_CHARSET);
            if (headers != null && headers.size() > 0) {
                for (Map.Entry<String, String> entry : headers.entrySet()){
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.setRequestProperty("Content-Type", ContentType.form_file + "; boundary=" + BOUNDARY);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setConnectTimeout(connectTimeoutSecond * 1000);
            conn.setReadTimeout(readTimeoutSecond * 1000);
            os = conn.getOutputStream();

            StringBuilder content;

            if (postParams != null && postParams.size() > 0) {
                content = new StringBuilder();
                for (NameValuePair field : postParams) {
                    content.append("\r\n")
                            .append("--")
                            .append(BOUNDARY)
                            .append("\r\n")
                            .append("Content-Disposition: form-data; name=\"")
                            .append(field.getName() + "\"")
                            .append("\r\n")
                            .append("\r\n")
                            .append(field.getValue());
                }
                os.write(content.toString().getBytes(DEFAULT_CHARSET));
            }

            if (fileParams != null && fileParams.size() > 0) {
                for (IEIFileValuePair file : fileParams) {
                    if (file.getName() == null || file.getStream() == null) {
                        continue;
                    }
                    content = new StringBuilder()
                            .append("\r\n")
                            .append("--")
                            .append(BOUNDARY)
                            .append("\r\n")
                            .append("Content-Disposition:form-data; name=\"")
                            .append(file.getName() + "\";")
                            .append("filename=\"")
                            .append(file.getFileName() + "\"")
                            .append("\r\n")
                            .append("Content-Type:" + file.getContentType())
                            .append("\r\n\r\n");
                    os.write(content.toString().getBytes(DEFAULT_CHARSET));

                    // real write stream to server
                    byte[] b = new byte[1024 * 1024];
                    int len;
                    while ((len = file.getStream().read(b)) != -1) {
                        os.write(b, 0, len);
                    }
                }
            }
            os.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes(DEFAULT_CHARSET));
            os.flush();

            is = conn.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            return new IEHttpEntity(conn.getResponseCode(), sb.toString());
        } catch (Exception e) {
            return new IEHttpEntity(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                }
            }
            if (fileParams != null && fileParams.size() > 0) {
                fileParams.forEach(IEIFileValuePair::destroyStream);
            }
        }
    }

    private static RequestConfig buildRequestConfig(final int connectTimeoutSecond, final int readTimeoutSecond) {
        return RequestConfig.custom().setSocketTimeout(readTimeoutSecond * 1000).setConnectTimeout(connectTimeoutSecond * 1000).build();
    }

    private static <T extends HttpRequestBase> T configRequestHeader(final T request, final String contentType, final Map<String,String> headers) {
        request.setHeader("Connection", "close");
        if (contentType != null && contentType.trim().length() > 0) {
            request.setHeader("Content-Type", contentType);
        }
        if (headers != null && headers.size() > 0) {
            headers.forEach((k, v) -> request.setHeader(k, v));
        }
        return request;
    }

    private static IEHttpEntity handleResponse(final CloseableHttpResponse response, final boolean isSkipEntity) {
        if (isSkipEntity) {
            return new IEHttpEntity(response.getStatusLine().getStatusCode(), null);
        }else{
            return readContentFromResponse(response);
        }
    }

    private static IEHttpEntity readContentFromResponse(final CloseableHttpResponse response) {
        IEHttpEntity result = new IEHttpEntity(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        if (response.getEntity() != null ) {
            try (BufferedReader r1 = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), DEFAULT_CHARSET))) {
                StringBuilder sb = new StringBuilder();
                String line ;
                while ((line = r1.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    result.setResponse(sb.toString());
                }
            } catch (IOException e) {
                return new IEHttpEntity(e);
            } finally {
                try {
                    response.getEntity().getContent().close();
                } catch (Exception e) { }
            }
        }

        return result;
    }

    private static boolean isNotBlank(final CharSequence value) {
        if (value != null && value.length() == 0) {
            for (int i = 0; i < value.length(); i++) {
                if (Character.isWhitespace(value.charAt(i)) == false) {
                    return true;
                }
            }
        }
        return false;
    }
}
