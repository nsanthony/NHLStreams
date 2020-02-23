package heron.topology.spout;


import com.twitter.heron.api.spout.BaseRichSpout;
import com.twitter.heron.api.spout.SpoutOutputCollector;
import com.twitter.heron.api.topology.OutputFieldsDeclarer;
import com.twitter.heron.api.topology.TopologyContext;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.Collections;
import java.util.Map;

public class PulsarSpout extends BaseRichSpout {

    private String url;
    private String topic;
    private String subName;
    private PulsarClient spoutClient;
    private Consumer consumer;
    private SpoutOutputCollector outputCollector;

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.url = (String) conf.get("url");
        this.topic = (String) conf.get("topic");
        this.subName = (String) conf.get("subName");
        this.outputCollector = collector;

        try {
            spoutClient = PulsarClient.builder()
                    .serviceUrl(url)
                    .build();

            this.consumer = getConsumer(spoutClient, url, subName);
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextTuple() {
        Message message;
        try{
            if(consumer == null) {
                consumer = getConsumer(spoutClient, url, subName);
            }

            message = consumer.receive();
            if (message != null){
                outputCollector.emit(Collections.singletonList(message));
            }


        } catch (PulsarClientException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }


    protected Consumer getConsumer(PulsarClient spoutClient, String topic, String subName) throws PulsarClientException {
        Consumer consumer = spoutClient.newConsumer()
                .topic(topic)
                .subscriptionName(subName)
                .subscribe();
        return consumer;
    }
}
