package scooby;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class ScoobyController {
    
    @RequestMapping("/")
    public String getData() throws Exception{
        StringBuilder sb = new StringBuilder("");
        sb.append(index("https://api-prod.ibyke.io/v2/boards?latitude=32.05281297232161&longitude=34.77219253778458", true));
        sb.append(index("https://api-prod.ibyke.io/v2/boards?latitude=32.095595&longitude=34.783595",true));
        sb.append(index("https://api-prod.ibyke.io/v2/boards?latitude=32.078153&longitude=34.774472",true));
        return sb.toString();
    }

    @RequestMapping("/healthCheck")
    public String getHealth() throws Exception{

        //String json = "{\"result\":[{\"cluster\":{\"lat\":32,\"lon\":34},\"text\":\"text\"}, \"cluster\":{\"lat\":32,\"lon\":34},\"text\":\"text\"}]}";
        String json = "{\"result\":[{\"cluster\":{\"lat\":32,\"lon\":31},\"text\":\"text\"},{\"cluster\":{\"lat\":43,\"lon\":42},\"text\":\"text1\"}]}";

        JSONObject obj = new JSONObject(json);
        return json;
    }

    @RequestMapping("/getLocations")
    public String getLocations() throws Exception{
        URL url1 = new URL("http://127.0.0.1:5000/do_magic");
        HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
        int status1 = con1.getResponseCode();
        BufferedReader in1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
        String inline;
        StringBuffer cont = new StringBuffer();
        while((inline = in1.readLine()) != null) {
            cont.append(inline);
        }
        return cont.toString();
    }



    public String index(String urlString, Boolean writeToFile) throws Exception{
        URL url = new URL(urlString);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
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

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        if(writeToFile) {
            FileWriter writer = new FileWriter("data15_7points.txt", true);
            writer.append(content.toString());
            writer.append(",HERE");
            writer.close();
        }

        JSONObject obj = new JSONObject(content.toString());

        FileWriter writerCSV = new FileWriter("data15_7pointsVol.csv", true);

        JSONArray res = obj.getJSONArray("items");
        int length = res.length();
        for (int i = 0; i < length; i++) {
            String line = res.getJSONObject(i).getString("boardId") + "," + res.getJSONObject(i).getString("boardNo") +  "," +res.getJSONObject(i).getString("latitude") + "," + res.getJSONObject(i).getString("longitude") + "," + res.getJSONObject(i).getInt("lastReportedTime") + "," + res.getJSONObject(i).getString("estimatedRange") + ","+res.getJSONObject(i).getString("vol") + "\n";
            writerCSV.append(line);
        }
        writerCSV.close();
        return content.toString();
    }

    @Scheduled(cron= "0 0/15 * * * *")
    private void timed() throws Exception{
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.05281297232161&longitude=34.77219253778458", true);
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.095595&longitude=34.783595",true);
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.078153&longitude=34.774472",true);
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.060212&longitude=34.760442",true);
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.073225&longitude=34.789651",true);
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.091072&longitude=34.790440",true);
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.113158&longitude=34.804926",true);
        index("https://api-prod.ibyke.io/v2/boards?latitude=32.109189&longitude=34.794630",true);
    }
    
}
