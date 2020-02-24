package heron.topology.spout;

import com.twitter.heron.api.generated.TopologyAPI;
import com.twitter.heron.api.spout.IRichSpout;
import com.twitter.heron.api.spout.ISpoutOutputCollector;
import com.twitter.heron.api.spout.SpoutOutputCollector;
import com.twitter.heron.api.topology.TopologyContext;
import com.twitter.heron.api.tuple.Tuple;
import org.apache.pulsar.PulsarStandalone;
import org.apache.pulsar.PulsarStandaloneBuilder;
import org.apache.pulsar.broker.ServiceConfiguration;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.proxy.socket.client.SimpleTestProducerSocket;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.pulsar.PulsarStandalone.*;

import org.mockito.MockitoAnnotations;
import org.mockito.mock.*;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.constraints.AssertTrue;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class StandaloneTesting {
    private static Logger LOG = Logger.getLogger(StandaloneTesting.class.getName());

    private PulsarSpout spout;
    private Map<String, Object> testConf;
    private PulsarClient testClient;
    private Producer<String> newProducer;

    @Before
    public void setup(){
        try {
            testConf = setCongifs();


            testClient = PulsarClient.builder()
                    .serviceUrl((String) testConf.get("url"))
                    .build();

            newProducer = testClient.newProducer(Schema.STRING)
                    .topic((String) testConf.get("topic"))
                    .create();

            newProducer.send("testMessage");

            spout = new PulsarSpout((String) testConf.get("url"), (String) testConf.get("topic"), (String) testConf.get("subName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @After
    public void cleanup(){
        try {
            newProducer.close();
            testClient.close();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testConnection(){


        try {

            spout.nextTuple();

            Field consumerField = spout.getClass().getField("consumer");

            consumerField.setAccessible(true);

            Assert.assertNotNull(consumerField);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


    }


    private Map setCongifs(){
        Map<String, Object> configs = new HashMap<>();
        configs.put("url", "pulsar://localhost:6650");
        configs.put("topic", "my-test-topic");
        configs.put("subName", "testSubname");

        return configs;
    }


    private class TestSpoutCollector implements ISpoutOutputCollector{

        @Override
        public List<Integer> emit(String streamId, List<Object> tuple, Object messageId) {
            return emit(streamId, Collections.singletonList(tuple), tuple);
        }

        @Override
        public void emitDirect(int taskId, String streamId, List<Object> tuple, Object messageId) {

        }

        @Override
        public void reportError(Throwable error) {

        }
    }


}
