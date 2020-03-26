package com.nhlstreams.source;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.connectors.nifi.NiFiDataPacket;
import org.apache.flink.streaming.connectors.nifi.NiFiSource;
import org.apache.flink.streaming.connectors.nifi.StandardNiFiDataPacket;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.client.SiteToSiteClientConfig;
import org.apache.nifi.remote.protocol.DataPacket;
import org.apache.nifi.stream.io.StreamUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NifiBarDownDataStream extends RichSourceFunction<NiFiDataPacket> {
    private static final Logger LOG = Logger.getLogger(NifiBarDownDataStream.class.getName());
    private static final long serialVersionUID = 1L;

    private static final long DEFAULT_WAIT_TIME_MS = 1000;

    // ------------------------------------------------------------------------

    private final SiteToSiteClientConfig clientConfig;

    private final long waitTimeMs;

    private transient SiteToSiteClient client;

    private volatile boolean isRunning = true;

    public NifiBarDownDataStream(SiteToSiteClientConfig clientConfig) {
        this(clientConfig, DEFAULT_WAIT_TIME_MS);
    }

    public NifiBarDownDataStream(SiteToSiteClientConfig clientConfig, long waitTimeMs) {
        this.clientConfig = clientConfig;
        this.waitTimeMs = waitTimeMs;
    }


    @Override
    public void run(SourceContext<NiFiDataPacket> ctx) throws Exception {
        if(client == null){
            client = new SiteToSiteClient.Builder().fromConfig(clientConfig).build();
        }

        while (isRunning) {
            try {
                final Transaction transaction = client.createTransaction(TransferDirection.RECEIVE);
            if (transaction == null) {
                LOG.log(Level.WARNING, "A transaction could not be created, waiting and will try again...");
                try {
                    Thread.sleep(waitTimeMs);
                } catch (InterruptedException ignored) {

                }
                continue;
            }

            DataPacket dataPacket = transaction.receive();
            if (dataPacket == null) {
                transaction.confirm();
                transaction.complete();

                LOG.log(Level.WARNING, "No data available to pull, waiting and will try again...");
                try {
                    Thread.sleep(waitTimeMs);
                } catch (InterruptedException ignored) {

                }
                continue;
            }

            final List<NiFiDataPacket> niFiDataPackets = new ArrayList<>();
            do {
                // Read the data into a byte array and wrap it along with the attributes
                // into a NiFiDataPacket.
                final InputStream inStream = dataPacket.getData();
                final byte[] data = new byte[(int) dataPacket.getSize()];
                StreamUtils.fillBuffer(inStream, data);

                final Map<String, String> attributes = dataPacket.getAttributes();

                niFiDataPackets.add(new StandardNiFiDataPacket(data, attributes));
                dataPacket = transaction.receive();
            } while (dataPacket != null);

            // Confirm transaction to verify the data
            transaction.confirm();

            for (NiFiDataPacket dp : niFiDataPackets) {
                ctx.collect(dp);
            }

            transaction.complete();
            }catch (UnknownHostException e){
                LOG.log(Level.WARNING, "Got UnknownHostException Again..... Waiting.....");
                e.printStackTrace();
                Thread.sleep(waitTimeMs);
            }
        }

    }

    @Override
    public void cancel() {
        isRunning = false;
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
