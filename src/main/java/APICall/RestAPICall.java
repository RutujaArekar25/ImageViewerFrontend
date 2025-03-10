package APICall;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestAPICall {

    //GetAll--

    public static void getmethod() {
        try {
            URL url = new URL("http://localhost:8888/getAll");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                        + connection.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null){
                System.out.println(output);
//                String json = output;
//                JSONObject jsonObject = new JSONObject(json);
                //jsonObject.getJSONObject()
            }
            connection.disconnect();

        }catch (Exception e){
            System.out.println("Exception in method" + e);

        }
    }


    //GetuserById--

    public static void getmethodById() {
        try {
            URL url = new URL("http://localhost:8888/getUserById/70e386de-772c-11ef-b6b1-a08cfda3dc09");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

//            if (connection.getResponseCode() != 200) {
//                throw new RuntimeException("Failed : HTTP Error code : "
//                        + connection.getResponseCode());
//            }

            InputStreamReader in = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null){
                System.out.println(output);

                String json = output;

               JSONObject jsonObject = new JSONObject(json);
               String image = jsonObject.getString("image");
                System.out.println("Image: " + image);

            }
            connection.disconnect();

        }catch (Exception e){
            System.out.println("Exception in method" + e);

        }
    }
}