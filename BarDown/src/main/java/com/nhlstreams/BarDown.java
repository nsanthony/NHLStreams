package com.nhlstreams;

import com.google.gson.Gson;
import com.nhlstreams.source.NifiBarDownDataStream;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.AllWindowedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;
import org.apache.flink.streaming.connectors.nifi.NiFiSource;

import org.apache.flink.streaming.connectors.pulsar.PulsarSourceBuilder;
import org.apache.flink.util.OutputTag;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;

import com.nhlstreams.map.ParseNHLJson;
import org.apache.pulsar.client.impl.conf.ClientConfigurationData;
import org.apache.pulsar.shade.org.codehaus.jackson.schema.JsonSchema;

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

            PulsarSourceBuilder<String> pulsarSourceBuilder = PulsarSourceBuilder
                    .builder(new SimpleStringSchema())
                    .serviceUrl("http://192.168.1.39:30002")
                    .topic("persistent://nifi/NHLStreams/game-logs")
                    .subscriptionName("BarDown");

            SourceFunction<String> pulsarSource = pulsarSourceBuilder.build();

            DataStream<String> pulsarStream = env.addSource(pulsarSource);

            OutputTag<String> newOutputTag = new OutputTag<>("100032482342");

            AllWindowedStream<String, GlobalWindow> countWindowOfStream = pulsarStream.countWindowAll(Long.getLong("10000")).sideOutputLateData(newOutputTag);

            countWindowOfStream.sum(1).print();




//            SingleOutputStreamOperator<Gson> convertJsonToStream = pulsarStream.map(new ParseNHLJson());
//
//            convertJsonToStream.timeWindowAll(Time.seconds(1000));

            env.execute("NHL Data Scraping");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Something went very wrong....", e);
            e.printStackTrace();
        }
    }
}
