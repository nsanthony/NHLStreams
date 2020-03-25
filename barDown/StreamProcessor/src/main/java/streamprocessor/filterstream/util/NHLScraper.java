package streamprocessor.filterstream.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.UnknownFormatConversionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NHLScraper {
    private static final Logger LOG = Logger.getLogger(NHLScraper.class.getName());


    private static InputStream getNHLData(String url) throws IOException {
        Process process = Runtime.getRuntime().exec("curl " + url);
        LOG.log(Level.INFO, "Got this json in response: " + process.getInputStream().toString() +
                "--------------------------------------------------------------------------------");
        return process.getInputStream();
    }

    public static String getJson(String url) throws IOException{

        InputStream dataStream = getNHLData(url);
        String nhlDataJson = null;

        if(dataStream != null){
            BufferedReader inBuffer = new BufferedReader(new InputStreamReader(dataStream));

            StringBuffer jsonString  = new StringBuffer();
            String jsonLine = null;

            while((jsonLine = inBuffer.readLine()) != null){
                jsonString.append(jsonLine);
            }
            nhlDataJson = jsonString.toString();
            LOG.log(Level.INFO, "Here is the response from the NHL: " + nhlDataJson +
                    " --------------------------------------------------------------------------------------------");
            return nhlDataJson;
        }else{
            throw new IOException();
        }

    }

}
