package com.nhlstreams;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;


import org.apache.flink.streaming.connectors.pulsar.FlinkPulsarProducer;
import org.apache.flink.streaming.connectors.pulsar.PulsarSourceBuilder;

import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.impl.auth.AuthenticationDisabled;


import java.util.logging.Level;
import java.util.logging.Logger;

public class BarDown {
    private static final Logger LOG = Logger.getLogger(BarDown.class.getName());
    public static void main(String[] args){

        //TODO add execute parameters to set the team name and date.
        final ParameterTool params = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);

        try {

            String pulsarURL = "http://192.168.1.39:30002";
            String pulsarSourceTopic = "persistent://nifi/NHLStreams/game-logs";
            String pulsarSinkTopic = "persistent://flink/NHLStreams/game-results";
            String pulsarSubName = "BarDown";
            Authentication sinkAuth = new AuthenticationDisabled();


            PulsarSourceBuilder<String> pulsarSourceBuilder = PulsarSourceBuilder
                    .builder(new SimpleStringSchema())
                    .serviceUrl(pulsarURL)
                    .topic(pulsarSourceTopic)
                    .subscriptionName(pulsarSubName);

            FlinkPulsarProducer<String> pulsarSinkBuilder = new FlinkPulsarProducer<String>(
                    pulsarURL,
                    pulsarSinkTopic,
                    sinkAuth,
                    new SimpleStringSchema(),
                    null);

            SourceFunction<String> pulsarSource = pulsarSourceBuilder.build();
            SinkFunction<String> pulsarSink = pulsarSinkBuilder;

            DataStream<String> pulsarStream = env.addSource(pulsarSource);


            DataStream<String> passThroughStream = pulsarStream.forward();

            passThroughStream.addSink(pulsarSink);



            env.execute("NHL Data Scraping");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Something went very wrong....", e);
            e.printStackTrace();
        }
    }
}
