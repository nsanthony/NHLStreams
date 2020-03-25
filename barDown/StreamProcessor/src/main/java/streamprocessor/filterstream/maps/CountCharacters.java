package streamprocessor.filterstream.maps;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.util.Collector;
import scala.Tuple1;
import scala.Tuple2;

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
