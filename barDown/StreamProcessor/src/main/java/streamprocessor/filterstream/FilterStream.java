package streamprocessor.filterstream;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilterStream {
    private static final Logger LOG = Logger.getLogger(FilterStream.class.getName());
    public static void main(String[] args){

        //TODO add execute parameters to set the team name and date.
        final ParameterTool params = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);


        if(params.has("url")) {
            String apiURL = params.get("url").toString();


            LOG.log(Level.INFO, "Got this url from the startup parameters: " + apiURL);

            JSONKeyValueDeserializationSchema jsonDeserialization = new JSONKeyValueDeserializationSchema(true);
            Properties props = new Properties();

            FlinkKafkaConsumer<ObjectNode> nhlKafkaConsumer = new FlinkKafkaConsumer<>("testTopic", jsonDeserialization, props);

            DataStream<ObjectNode> scrapeData = env.addSource(nhlKafkaConsumer);
            scrapeData.print();

            try {
                env.execute("NHL Data Scraping");
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Something went very wrong....", e);
                e.printStackTrace();
            }
        }else {
            LOG.log(Level.WARNING, "Did not get the runtime parameter needed to scrape data");
        }
    }
}
