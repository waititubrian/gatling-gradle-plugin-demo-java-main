package videogamedb;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class VideoGameDbSimulation extends Simulation {

    // Http configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json");

    // Scenario
    private ScenarioBuilder scn = scenario("Video Game DB Stress Test")
            .exec(http("Get All Video Games")
            .get("/videogame")
                    .check(status().is(200)));

    // Simulation
    {
        setUp(scn.injectOpen(
                atOnceUsers(10))
                .protocols(httpProtocol));
    }

}
