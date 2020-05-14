package com.nhlstreams;

import com.nhlstreams.source.WordData;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;


import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarProducer;
import org.apache.flink.streaming.connectors.pulsar.PulsarSourceBuilder;

import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.auth.AuthenticationBasic;
import org.apache.pulsar.client.impl.auth.AuthenticationDisabled;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class BarDown {
    private static final Logger LOG = Logger.getLogger(BarDown.class.getName());
    public static void main(String[] args){

        //TODO add execute parameters to set the team name and date.
        final ParameterTool params = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.getConfig().setGlobalJobParameters(params);

        String pulsarURL = "pulsar://192.168.1.39:30329";
        String pulsarSourceTopic = "persistent://NHLStreams/games/game-logs";
        String pulsarSinkTopic = "persistent://NHLStreams/games/game-results";
        String pulsarSubName = "BarDown";
        Authentication pulsarAuth = new AuthenticationDisabled();


        PulsarSourceBuilder<String> pulsarSourceBuilder = PulsarSourceBuilder
                .builder(new SimpleStringSchema())
                .authentication(pulsarAuth)
                .serviceUrl(pulsarURL)
                .topic(pulsarSourceTopic)
                .subscriptionName(pulsarSubName);


        FlinkPulsarProducer<String> pulsarSinkBuilder = new FlinkPulsarProducer<>(
                pulsarURL,
                pulsarSinkTopic,
                pulsarAuth,
                new SimpleStringSchema(),
                null);



//        DataStream<String> pulsarStream = env.fromElements(WordData.WORDS);

        System.out.println("Executing WordCount example with default input data set.");
        System.out.println("Use --input to specify file input.");
        // get default test text data


        SourceFunction<String> pulsarSource = null;
        try {
             pulsarSource = pulsarSourceBuilder.build();
        }catch (PulsarClientException e){
            LOG.log(Level.SEVERE, "Could not build the Pulsar Source!!!!", e);
            e.printStackTrace();
        }
//
        DataStream<String> pulsarStream = env.addSource(pulsarSource);


        DataStream<String> passThroughStream = pulsarStream.shuffle();

        DataStreamSink<String> sinkPoint = passThroughStream.addSink(pulsarSinkBuilder);


        try {
            env.execute("NHL Data Scraping");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Something went very wrong....", e);
            e.printStackTrace();
        }
    }
}
