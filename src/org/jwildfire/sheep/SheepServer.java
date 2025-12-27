package org.jwildfire.sheep;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SheepServer {
    private static final String REDIRECT_URL = "https://community.sheepserver.net/query.php";
    private static final String CLIENT_VERSION = "JWildfire_9.03";
    private static final String DEFAULT_NICKNAME = "jwildfire_user";
    
    private final HttpClient client;
    private String hostServer;
    private String renderServer;
    private String voteServer;

    public SheepServer() {
        this.client = HttpClient.newHttpClient();
    }

    public void authenticate() throws Exception {
        // Generate a random ID if we don't have one (mock for now)
        String uniqueId = "0000000000000000"; 
        String nickname = DEFAULT_NICKNAME;
        
        StringBuilder query = new StringBuilder();
        query.append("q=redir");
        query.append("&u=").append(URLEncoder.encode(nickname, StandardCharsets.UTF_8));
        query.append("&v=").append(URLEncoder.encode(CLIENT_VERSION, StandardCharsets.UTF_8));
        query.append("&i=").append(uniqueId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(REDIRECT_URL + "?" + query.toString()))
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
            
            System.out.println("Sheep Servers Discovered:");
            System.out.println("Host: " + hostServer);
            System.out.println("Render: " + renderServer);
            System.out.println("Vote: " + voteServer);
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

        // The list is gzipped
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
            // We'll store ID -> Gen for now, or maybe ID -> URL if available
            // The summary said 'url' attribute exists
            String url = sheep.getAttribute("url"); 
            
            sheepMap.put(id, "Gen: " + gen + " | " + url);
        }
        return sheepMap;
    }
    
    // Placeholder for fetching a specific genome
    // Since we don't have a direct "get genome by ID" endpoint confirmed, 
    // we might need to rely on the 'url' from the list or the /cgi/get endpoint.
    public String getGenome(String id) {
        // TODO: Research how to get genome by ID.
        // For now, return a dummy or try to construct a URL if possible.
        return null;
    }
}
