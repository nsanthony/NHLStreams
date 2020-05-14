package com.nhlstreams.map;

import com.google.gson.Gson;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;

public class ParseNHLJson implements MapFunction<String, Gson> {
    @Override
    public Gson map(String value) throws Exception {
        Gson nhlJson = new Gson();
        nhlJson.toJson(value);
        return nhlJson;
    }
}
