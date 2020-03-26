package com.nhlstreams;

import com.google.gson.Gson;
import com.nhlstreams.source.NifiBarDownDataStream;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;
import org.apache.flink.streaming.connectors.nifi.NiFiSource;

import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;

import com.nhlstreams.map.ParseNHLJson;

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
            SiteToSiteClientConfig nifiConfig = new SiteToSiteClient.Builder()
                    .url("http://192.168.1.39:8080/nifi")
                    .portIdentifier("7c6548e9-acc8-31c1-8201-5942e1cde3ac")
                    .requestBatchCount(5)
                    .buildConfig();

            SourceFunction<NiFiDataPacket> nifiSource = new NifiBarDownDataStream(nifiConfig);

            DataStreamSource<NiFiDataPacket> scrapeData = env.addSource(nifiSource);

            scrapeData.print();

            env.execute("NHL Data Scraping");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Something went very wrong....", e);
            e.printStackTrace();
        }
    }
}
