package com.nhlstreams;

import com.google.gson.Gson;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;
import org.apache.flink.streaming.connectors.nifi.NiFiSource;

import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;
import org.apache.nifi.remote.client.socket.SocketClient;

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
                    .url("http://192.168.1.39:9090/nifi")
                    .portIdentifier("ce4c4909-7299-1c20-8730-0077daf72357")
                    .requestBatchCount(5)
                    .buildConfig();

            SourceFunction<NiFiDataPacket> nifiSource = new NiFiSource(nifiConfig);

            DataStreamSource<NiFiDataPacket> scrapeData = env.addSource(nifiSource);

            scrapeData.print();

            env.execute("NHL Data Scraping");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Something went very wrong....", e);
            e.printStackTrace();
        }
    }
}
