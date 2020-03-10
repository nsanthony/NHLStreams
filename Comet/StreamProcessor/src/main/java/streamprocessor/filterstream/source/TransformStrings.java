package streamprocessor.filterstream.source;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.kafka.common.utils.Time;

public class TransformStrings <T, O> {

    private DataStream<T> dataStream;


    public TransformStrings(DataStream<T> sourceStream) {
        this.dataStream = sourceStream;
    }

    public DataStream<O> transformString(){
        return this.dataStream.process(new countingProcessor<T, O>());
    }




    private static class countingProcessor<S, S1> extends ProcessFunction<S, S1>{

        @Override
        public void processElement(S value, Context ctx, Collector<S1> out) throws Exception {
            String appendedString = value + "appendedString" + String.valueOf(Time.SYSTEM);
            out.collect((S1) appendedString);
        }
    }

}
