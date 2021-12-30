package cn.stylefeng.guns.modular.vsshop.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class IPHelper {


    private static final String QUERY_ADDRESS = "http://www.icanhazip.com";


    /**
     * 获取外网ip
     */
    public static String getOuterNetIp() {
        String result = "";
        URLConnection connection;
        BufferedReader in = null;
        try {
            URL url = new URL(QUERY_ADDRESS);
            connection = url.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "KeepAlive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
        return result;
    }
}