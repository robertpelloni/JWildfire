package org.jwildfire.sheep;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SheepServer {
    private static final String DEFAULT_REDIRECT_URL = "https://community.sheepserver.net/query.php";
    private static final String CLIENT_VERSION = "JWildfire_9.03";
    private static final String DEFAULT_NICKNAME = "jwildfire_user";
    
    private final HttpClient client;
    private String hostServer;
    private String renderServer;
    private String voteServer;

    private String redirectUrl = DEFAULT_REDIRECT_URL;
    private String nickname = DEFAULT_NICKNAME;
    private String uniqueId = "0000000000000000"; // Should be persistent

    public SheepServer() {
        this.client = HttpClient.newBuilder()
                .sslContext(getUnsafeSslContext()) // Use unsafe context to handle community server certs
                .build();
    }

    private SSLContext getUnsafeSslContext() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            return sc;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create unsafe SSL context", e);
        }
    }

    public void setConfig(String nickname, String redirectUrl) {
        if (nickname != null && !nickname.isEmpty()) this.nickname = nickname;
        if (redirectUrl != null && !redirectUrl.isEmpty()) this.redirectUrl = redirectUrl;
    }

    public String getNickname() { return nickname; }
    public String getRedirectUrl() { return redirectUrl; }

    public void authenticate() throws Exception {
        StringBuilder query = new StringBuilder();
        query.append("q=redir");
        query.append("&u=").append(URLEncoder.encode(nickname, StandardCharsets.UTF_8));
        query.append("&v=").append(URLEncoder.encode(CLIENT_VERSION, StandardCharsets.UTF_8));
        query.append("&i=").append(uniqueId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(redirectUrl + "?" + query.toString()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to contact redirect server: " + response.statusCode());
        }

        parseRedirectResponse(response.body());
    }

    private void parseRedirectResponse(String xml) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        
        NodeList nodeList = doc.getElementsByTagName("redir");
        if (nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            this.hostServer = element.getAttribute("host");
            this.renderServer = element.getAttribute("render");
            this.voteServer = element.getAttribute("vote");
        } else {
            throw new RuntimeException("Invalid response from redirect server");
        }
    }

    public Map<String, String> getFlockList() throws Exception {
        if (hostServer == null) authenticate();

        String url = "http://" + hostServer + "/cgi/list?v=" + URLEncoder.encode(CLIENT_VERSION, StandardCharsets.UTF_8);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to download flock list: " + response.statusCode());
        }

        try (GZIPInputStream gzipIn = new GZIPInputStream(response.body())) {
            return parseFlockList(gzipIn);
        }
    }

    private Map<String, String> parseFlockList(InputStream is) throws Exception {
        Map<String, String> sheepMap = new HashMap<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(is);

        NodeList sheepList = doc.getElementsByTagName("sheep");
        for (int i = 0; i < sheepList.getLength(); i++) {
            Element sheep = (Element) sheepList.item(i);
            String id = sheep.getAttribute("id");
            String gen = sheep.getAttribute("gen");
            String url = sheep.getAttribute("url"); 
            
            sheepMap.put(id, "Gen: " + gen + " | " + url);
        }
        return sheepMap;
    }
    
    public String fetchRenderingJob() throws Exception {
        if (renderServer == null) authenticate();

        StringBuilder query = new StringBuilder();
        query.append("n=").append(URLEncoder.encode(nickname, StandardCharsets.UTF_8));
        query.append("&u=").append(uniqueId);
        query.append("&v=").append(URLEncoder.encode(CLIENT_VERSION, StandardCharsets.UTF_8));
        
        String url = "http://" + renderServer + "/cgi/get?" + query.toString();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch rendering job: " + response.statusCode());
        }
        
        return response.body();
    }
}
