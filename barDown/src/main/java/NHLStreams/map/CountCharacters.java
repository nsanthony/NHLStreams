package NHLStreams.map;

import org.apache.flink.api.common.functions.MapFunction;

public class CountCharacters implements MapFunction<String, Integer> {

    @Override
    public Integer map(String value) throws Exception {
        int countOfCharacters = 0;

        for(String character: value.split("")){
            countOfCharacters++;
        }
        return countOfCharacters;
    }
}
