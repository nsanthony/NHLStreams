package heron.topology.spout;


import com.twitter.heron.api.spout.BaseRichSpout;
import com.twitter.heron.api.spout.SpoutOutputCollector;
import com.twitter.heron.api.topology.OutputFieldsDeclarer;
import com.twitter.heron.api.topology.TopologyContext;
import org.apache.pulsar.client.api.*;

import javax.validation.constraints.Null;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PulsarSpout extends BaseRichSpout {

    private static Logger LOG = Logger.getLogger(PulsarSpout.class.getName());

    private String url;
    private String topic;
    private String subName;
    private PulsarClient spoutClient;
    private Consumer<String> consumer;
    private SpoutOutputCollector outputCollector;



    public PulsarSpout(String url, String topic, String subName){
        this.url = url;
        this.topic = topic;
        this.subName = subName;
    }

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.outputCollector = collector;

        try {
            spoutClient = PulsarClient.builder()
                    .serviceUrl(url)
                    .build();

            this.consumer = getConsumer();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextTuple() {
        Message message;
        try{
            if(consumer == null) {
                consumer = getConsumer();
            }

            message = consumer.receive();
            if (message != null){
                outputCollector.emit(Collections.singletonList(message));
                consumer.acknowledge(message.getMessageId());
            }


        } catch (PulsarClientException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }


    protected Consumer<String> getConsumer() throws PulsarClientException {
        LOG.log(Level.INFO, "Topic: " + topic + " URL: " + url + " Subname: " + subName);
        Consumer<String> consumer = spoutClient.newConsumer(Schema.STRING)
                .topic(topic)
//                    .subscriptionName(subName)
                .subscriptionType(SubscriptionType.Shared)
                .subscribe();
        return consumer;
    }
}
