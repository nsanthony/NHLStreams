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
import org.apache.pulsar.client.api.*;
import org.apache.pulsar.proxy.socket.client.SimpleTestProducerSocket;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import org.apache.pulsar.PulsarStandalone.*;

import org.mockito.MockitoAnnotations;
import org.mockito.mock.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class StandaloneTesting {
    private static Logger LOG = Logger.getLogger(StandaloneTesting.class.getName());

    private PulsarSpout spout;
    private Map<String, Object> testConf;

    @Before
    public void setup(){
        try {
            testConf = setCongifs();
            spout = mock(PulsarSpout.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testConnection(){
        ISpoutOutputCollector spoutCollector = new TestSpoutCollector();
        SpoutOutputCollector outputCollector = new SpoutOutputCollector(spoutCollector);
//        spout.open(testConf, mock(TopologyContext.class), mock(SpoutOutputCollector.class));

        MockitoAnnotations.initMocks(this);
        String setClient = null;

        ReflectionTestUtils.setField(spout, "consumer", setClient);
        spout.nextTuple();



        try {
            PulsarClient testClient = PulsarClient.builder()
                    .serviceUrl((String) testConf.get("url"))
                    .build();


        verify(spout).getConsumer(testClient, (String) testConf.get("topic"), (String) testConf.get("subName"));

        } catch (PulsarClientException e) {
            e.printStackTrace();
        }


    }


    private Map setCongifs(){
        Map<String, Object> configs = new HashMap<>();
        configs.put("url", "pulsar://localhost:6650");
        configs.put("topic", "testTopic");
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
