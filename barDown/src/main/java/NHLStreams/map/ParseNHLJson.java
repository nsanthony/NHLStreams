package NHLStreams.map;

import com.google.gson.Gson;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;

public class ParseNHLJson implements MapFunction<NiFiDataPacket, Gson> {
    @Override
    public Gson map(NiFiDataPacket value) throws Exception {
        Gson nhlJson = new Gson();
        nhlJson.toJson(value.getContent().toString());
        return nhlJson;
    }
}
