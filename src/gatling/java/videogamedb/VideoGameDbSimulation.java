package videogamedb;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class VideoGameDbSimulation extends Simulation {

    // Http configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // HTTP CALLS
    private static ChainBuilder getAllGames =
            exec(http("Get All Video Games")
            .get("/videogame")
                    .check(status().is(200)));

    private static ChainBuilder authenticate =
            exec(http("Authenticate")
            .post("/authenticate")
            .body(StringBody("{\n" +
                    "  \"password\": \"admin\",\n" +
                    "  \"username\": \"admin\"\n" +
                    "}"))
                    .check(jmesPath("token").saveAs("jwtToken")));

    private static ChainBuilder createNewGame =
            exec(http("Create New Game")
            .post("/videogame")
            .header("Authorization", "Bearer ${jwtToken}")
            .body(ElFileBody("bodies/newGameTemplate.json")).asJson()
                    .check(status().is(200)));

    private static ChainBuilder getLastPostedGame =
            exec(http("Get Last Posted Game")
            .get("/videogame/1"));

      private static ChainBuilder deleteLastPostedGame =
            exec(http("Delete Last Posted Game")
            .delete("/videogame/1")
            .header("Authorization", "Bearer ${jwtToken}"));

    // Scenario
    // 1. Get all video games
    // 2. Authenticate
    // 3. Create a new video game
    // 4. Get details of newly created game
    // 5. Delete newly created game
    private ScenarioBuilder scn = scenario("Video Game DB Stress Test")
            .exec(getAllGames)
            .pause(2)
            .exec(authenticate)
            .pause(2)
            .exec(createNewGame)
            .pause(2)
            .exec(getLastPostedGame)
            .pause(2)
            .exec(deleteLastPostedGame);

    // Simulation
    {
        setUp(scn.injectOpen(
                atOnceUsers(1))
                .protocols(httpProtocol));
    }

}
