package streamprocessor.filterstream;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import streamprocessor.filterstream.maps.CountCharacters;
import streamprocessor.filterstream.source.GeneratedData;
import streamprocessor.filterstream.maps.TransformStrings;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FilterStream {
    private static final Logger LOG = Logger.getLogger(FilterStream.class.getName());
    public static void main(String[] args){

        final ParameterTool params = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> generateData = env.addSource(new GeneratedData());

        DataStream<String> transformedStrings = generateData
                .map(new TransformStrings());

        DataStream<Integer> countChars = transformedStrings
                .map(new CountCharacters());

        countChars.print();
        try {
            env.execute("Appending Strings Job");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Something went very wrong....", e);
            e.printStackTrace();
        }
    }
}
