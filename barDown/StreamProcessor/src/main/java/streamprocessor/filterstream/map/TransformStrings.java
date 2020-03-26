package streamprocessor.filterstream.map;

import org.apache.flink.api.common.functions.MapFunction;

import java.nio.charset.Charset;
import java.util.Random;

public class TransformStrings implements MapFunction<String, String> {

    @Override
    public String map(String value) throws Exception {
        int randomNumCharacters = new Random().nextInt(100);
        byte[] randomBytes = new byte[randomNumCharacters];
        new Random().nextBytes(randomBytes);
        String randomCharacters = new String(randomBytes, Charset.forName("UTF-8"));
        String appendedString = value + randomCharacters;
        return appendedString;
    }
}
