package com.nhlstreams.source;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

public class GeneratedData extends RichSourceFunction<String> {

    private boolean running = true;

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {

        String testOutputString = "testOutput";

        while(running){
            sourceContext.collect(testOutputString);
        }

    }

    @Override
    public void cancel() {
        this.running = false;
    }


    /**
     * Create a new {@link DataStream} in the given execution environment with
     * partitioning set to forward by default.
     *
     * @param environment    The StreamExecutionEnvironment
     * @param transformation
     */



}
