package streamprocessor.filterstream.source;

import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

public class ScrapeGame extends RichSourceFunction<String> {
    @Override
    public void run(SourceContext<String> ctx) throws Exception {

    }

    @Override
    public void cancel() {

    }
}
