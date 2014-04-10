package se.hrmsoftware.guess.leaderboard.internal;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import se.hrmsoftware.guess.model.events.GameEndedEvent;
import se.hrmsoftware.guess.model.events.GameStartedEvent;
import se.hrmsoftware.guess.model.events.GuessMadeEvent;

import java.util.HashMap;
import java.util.Map;

public class SnapshotAggregatorRouteTest extends CamelTestSupport {
    private static final String FIRST_MESSAGE =
            "+-----------------------------------------------+\n" +
            "| Current: 1                                    |\n" +
            "| Games played: 0                               |\n" +
            "+-----------------------------------------------+\n" +
            "| Player    | Wins      | Guesses    | Ratio    |\n" +
            "+-----------------------------------------------+\n" +
            "+-----------------------------------------------+\n";

    private static final String SECOND_MESSAGE =
            "+-----------------------------------------------+\n" +
            "| Current: 0                                    |\n" +
            "| Games played: 1                               |\n" +
            "+-----------------------------------------------+\n" +
            "| Player    | Wins      | Guesses    | Ratio    |\n" +
            "+-----------------------------------------------+\n" +
            "| p2        | 1         | 1          | 100,00   |\n" +
            "| p1        | 0         | 2          | 0,00     |\n" +
            "| p3        | 0         | 1          | 0,00     |\n" +
            "+-----------------------------------------------+\n";

    private SnapshotAggregatorRoute route;

    @EndpointInject(uri = "mock:leaderboard")
    private MockEndpoint leaderboard;

    @Produce(uri = "direct:start")
    private ProducerTemplate template;

    @Override
    protected void doPreSetup() throws Exception {
        route = new SnapshotAggregatorRoute();
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("eventsEndpoint", "direct:start");
        config.put("outputEndpoint", "mock:leaderboard");
        route.onActivate(config);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return route;
    }

    @Test
    public void testRoute() throws InterruptedException {
        leaderboard.expectedBodiesReceived(FIRST_MESSAGE, SECOND_MESSAGE);

        template.sendBody(new GameStartedEvent("1"));
        template.sendBody(new GuessMadeEvent("p1", "1"));
        template.sendBody(new GuessMadeEvent("p2", "1"));
        template.sendBody(new GuessMadeEvent("p3", "1"));
        template.sendBody(new GuessMadeEvent("p1", "1"));
        Thread.sleep(1000);
        template.sendBody(new GameEndedEvent("1", "p2"));

        leaderboard.assertIsSatisfied();
    }
}
