package hello;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.scheduling.annotation.Scheduled;


import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class HelloController {
    
    @RequestMapping("/")
    public String getData() throws Exception{
        StringBuilder sb = new StringBuilder("");
        sb.append(index("https://api-prod.ibyke.io/v2/boards?latitude=32.05281297232161&longitude=34.77219253778458", true));
        sb.append(index("https://api-prod.ibyke.io/v2/boards?latitude=32.095595&longitude=34.783595",true));
        sb.append(index("https://api-prod.ibyke.io/v2/boards?latitude=32.078153&longitude=34.774472",true));
        return sb.toString();
    }


    public String index(String urlString, Boolean writeToFile) throws Exception{
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestProperty("user-agent","ZenBike-New/3.21.0 (iPhone; iOS 12.3.1; Scale/3.00)");
        con.setRequestProperty("x-lang", "en");
        con.setRequestProperty("authentication","clientId=A34194B9-D430-4202-9544-D2460A920729;userId=ed2fe992-d851-4bdb-8721-2674991cc97a;ft=9e2b71c2d8d3520b8c29be0b069ad0c5");
        con.setRequestProperty("x-package-name","com.zen.zbike");
        con.setRequestProperty("x-app-version", "3.21.0");
        con.setRequestProperty("x-country", "IL");
        con.setRequestProperty("x-platform","iOS_12.3.1");
        con.setRequestProperty("accept-language","en-IL;q=1");
        con.setRequestProperty("accept","application/json");
        con.setRequestProperty("content-type","application/json; charset=utf-8");
        con.setRequestProperty("x-adv-id","3AFA6040-6B24-4018-BED6-4890EE01AF6C");
        con.setRequestProperty("x-req-id","A34194B9-D430-4202-9544-D2460A920729");

        con.setRequestMethod("GET");

        int status = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        if(writeToFile) {
            FileWriter writer = new FileWriter("data15_3points.txt", true);
            writer.append(content.toString());
            writer.append(",HERE");
            writer.close();
        }

        JSONObject obj = new JSONObject(content.toString());

        FileWriter writerCSV = new FileWriter("data15_3points.csv", true);

        JSONArray res = obj.getJSONArray("items");
        int length = res.length();
        for (int i = 0; i < length; i++) {
            System.out.println(res.getJSONObject(i).getString("boardId"));
            System.out.println("\t " + res.getJSONObject(i).getString("boardNo"));
            System.out.println("\t " + res.getJSONObject(i).getString("latitude"));
            System.out.println("\t " + res.getJSONObject(i).getString("longitude"));
            System.out.println("\t " + res.getJSONObject(i).getString("lastReportedTime"));
            String line = res.getJSONObject(i).getString("boardId") + "," + res.getJSONObject(i).getString("boardNo") +  "," +res.getJSONObject(i).getString("latitude") + "," + res.getJSONObject(i).getString("longitude") + "," + res.getJSONObject(i).getString("lastReportedTime") + "," + res.getJSONObject(i).getString("estimatedRange") + "\n";
            writerCSV.append(line);

        }
        writerCSV.close();
        return content.toString();
    }

    @Scheduled(cron= "0 0/15 * * * *")
    private void timed() throws Exception{
        System.out.println("here");
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.05281297232161&longitude=34.77219253778458", true);
        index("https://api-prod.ibyke.io/v2/boards?latitude32.095595=&longitude=34.783595",true);
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.078153&longitude=34.774472",true);
    }
    
}
