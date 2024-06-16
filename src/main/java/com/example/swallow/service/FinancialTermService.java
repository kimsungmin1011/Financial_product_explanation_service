package com.example.swallow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
public class FinancialTermService {

    private static final Logger logger = Logger.getLogger(FinancialTermService.class.getName());

    @Value("${financial.api.key}")
    private String serviceKey;

    public String getFinancialTermMeaning(String term) throws Exception {
        StringBuilder urlBuilder = new StringBuilder("http://api.seibro.or.kr/openapi/service/FnTermSvc/getFinancialTermMeaning");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", StandardCharsets.UTF_8.toString()) + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("term", StandardCharsets.UTF_8.toString()) + "=" + URLEncoder.encode(term, StandardCharsets.UTF_8.toString()));
        urlBuilder.append("&" + URLEncoder.encode("pageNo", StandardCharsets.UTF_8.toString()) + "=1");
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", StandardCharsets.UTF_8.toString()) + "=1");

        logger.info("Request URL: " + urlBuilder.toString());

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);  // 리다이렉션을 따르도록 설정
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        int responseCode = conn.getResponseCode();
        logger.info("Response code: " + responseCode);

        BufferedReader rd;
        if (responseCode >= 200 && responseCode <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else if (responseCode == 302) {
            String location = conn.getHeaderField("Location");
            URL redirectUrl = new URL(location);
            conn = (HttpURLConnection) redirectUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
            }
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        logger.info("Response: " + sb.toString());

        if (responseCode >= 200 && responseCode <= 300) {
            return parseXmlResponse(sb.toString());
        } else {
            throw new Exception("Failed to fetch financial term meaning: " + sb.toString());
        }
    }

    private String parseXmlResponse(String xmlResponse) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlResponse)));

        NodeList fnceDictNmList = doc.getElementsByTagName("fnceDictNm");
        NodeList ksdFnceDictDescContentList = doc.getElementsByTagName("ksdFnceDictDescContent");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < fnceDictNmList.getLength(); i++) {
            String termName = fnceDictNmList.item(i).getTextContent();
            String termDescription = ksdFnceDictDescContentList.item(i).getTextContent();
            result.append(termName).append(": ").append(termDescription).append("\n");
        }

        return result.toString();
    }
}
