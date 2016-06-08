package com.showworld.live.main;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by alex on 16/6/1.
 */
public class HttpUtil {
    private static String TAG = "HttpUtil";
    public static final String SERVER_URL = "http://203.195.167.34/";
    public static final String enterRoomUrl = SERVER_URL + "enter_room.php";
    public static final String rootUrl = SERVER_URL + "image_get.php";


    public static final int SUCCESS = 200;
    public static final int FAIL = 500;

    public static String PostUrl(String Url, List<NameValuePair> pairlist) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Url);

        List<NameValuePair> params = pairlist;
        UrlEncodedFormEntity entity;
        int ret = 0;
        try {
            entity = new UrlEncodedFormEntity(params, "utf-8");
            httpPost.setEntity(entity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            ret = httpResponse.getStatusLine().getStatusCode();

            if (ret > 0) {
                HttpEntity getEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(getEntity, "utf-8");
                return response;
            } else {
                Log.e("error", "Httpresponse error");
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("error", e.toString());
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("error", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("error", e.toString());
            e.printStackTrace();
        }
        return "";
    }
}
