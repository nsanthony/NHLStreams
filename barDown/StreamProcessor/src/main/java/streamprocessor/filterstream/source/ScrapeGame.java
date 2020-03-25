package streamprocessor.filterstream.source;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import streamprocessor.filterstream.util.NHLScraper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ScrapeGame extends RichSourceFunction<String> {
    private static final Logger LOG = Logger.getLogger(ScrapeGame.class.getName());

    private boolean running = true;
    private String gameTag;


    public ScrapeGame(String gameParameters){
        this.gameTag = gameParameters;
    }


    @Override
    public void run(SourceContext<String> context) throws Exception {


        while(running){
            //Put scraping logic here.
            try {
                String nhlJsonAsString = NHLScraper.getJson(this.gameTag);
                context.collect(nhlJsonAsString);
                wait(10000);
            }catch(Exception e){
                LOG.log(Level.WARNING, "Got this exception: ", e.getMessage());
                e.printStackTrace();
            }
        }

    }

    @Override
    public void cancel() {
        this.running = false;
    }
}
