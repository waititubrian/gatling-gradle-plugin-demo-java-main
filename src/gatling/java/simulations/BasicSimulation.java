package simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static simulations.utils.Constants.AUTH_URI;
import static simulations.utils.Constants.BASE_URI;

public class BasicSimulation extends Simulation {

    // HTTP configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URI)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

//    // Feeder for test data (Optional, if you have any JSON or CSV data files)
//    private static FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/gameJsonFile.json").random();

    // HTTP CALLS
    // 1. Authenticate to get the access token
    private static ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post(AUTH_URI)
                    .header("Content-Type", "application/x-www-form-urlencoded") // Correct content type
                    .formParam("grant_type", "password")
                    .formParam("client_id", "web-client")
                    .formParam("client_secret", "62f8c3fb-0fba-4358-a8da-80b52dd1e32b")
                    .formParam("username", "eva.mutuku@agencify.insure")
                    .formParam("password", "123456789")
                    .formParam("scope", "email profile")
                    .check(jsonPath("$.access_token").saveAs("accessToken")));

    // 2. Make the API call for "/gis/setups/product-groups/58/cover-types"
    private static ChainBuilder getCoverTypes =
            exec(http("Get Cover Types")
                    .get("/gis/setups/product-groups/58/cover-types")
                    .header("Authorization", "Bearer ${accessToken}") // Use the token from the previous step
                    .check(status().is(200))); // Check for a 200 OK response

    // Scenario
    // 1. Authenticate
    // 2. Get Cover Types API
    private ScenarioBuilder scn = scenario("Test API with Authentication")
            .exec(authenticate)
            .pause(5)
            .exec(getCoverTypes)
            .pause(5);

    // Simulation Setup
    {
        setUp(scn.injectOpen(atOnceUsers(1)) // Simulate a single user at once
                .protocols(httpProtocol));    // Use the HTTP protocol for the requests
    }
}
