package com.alejandro.game.server;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for the http server and the whole application flow.
 *
 * @author afernandez
 */
public class CustomHttpServerIT {
    private static final int TEST_PORT = 9090;

    private CustomHttpServer customHttpServer;

    @BeforeClass
    public static void setup() {
        RestAssured.port = Integer.valueOf(TEST_PORT);
        RestAssured.baseURI = "http://localhost";
    }

    @Before
    public void init() {
        customHttpServer = new CustomHttpServer(TEST_PORT);
        customHttpServer.start();
    }

    @After
    public void tearDown() {
        customHttpServer.stop();
    }

    @Test
    public void testWrongEndpoints() {
        given().when().get("/10/loginError").then().statusCode(404);
        given().when().get("/hi/score").then().statusCode(404);
        given().when().get("/test").then().statusCode(404);
    }

    @Test
    public void testInvalidHttpMethods() {
        String invalidMethod = "Incorrect HTTP Method";

        given().when().post("/15/login")
                .then()
                .statusCode(400)
                .body(containsString(invalidMethod));

        given().when().get("/15/score")
                .then()
                .statusCode(400)
                .body(containsString(invalidMethod));

        given().when().post("/15/highscorelist")
                .then()
                .statusCode(400)
                .body(containsString(invalidMethod));
    }

    @Test
    public void testLogin() throws Exception {
        Response sessionKey = expect().statusCode(200).given().when().get("/15/login");

        assertNotNull(sessionKey.body().asString());

        // Reusing key, second login with same user
        given().when().get("/15/login")
                .then()
                .statusCode(200)
                .body(equalTo(sessionKey.body().asString()));
    }

    @Test
    public void testAddScoreInvalidSessionKey() throws Exception {
        final String sessionNotValid = "Session key not valid.";

        given().body("150").when().post("/1/score")
                .then()
                .statusCode(401)
                .body(equalTo(sessionNotValid));

        given().body("150").when().post("/1/score?sessionkey=f372412971ef49b88526d251f258cf9f")
                .then()
                .statusCode(401)
                .body(equalTo(sessionNotValid));
    }

    @Test
    public void testAddScoreInvalidBodyContent() throws Exception {
        final String invalidBody = "The the score value to add was empty.";

        Response sessionKey = expect().statusCode(200).given().when().get("/20/login");

        given().body("").when().post("/1/score?sessionkey=" + sessionKey.body().asString())
                .then()
                .statusCode(400)
                .body(equalTo(invalidBody));
    }

    @Test
    public void testAddScoreValidSession() throws Exception {
        Response sessionKey = expect().statusCode(200).given().when().get("/20/login");

        given().body("500").when().post("/1/score?sessionkey=" + sessionKey.body().asString())
                .then()
                .statusCode(200);

        given().body("").when().post("/1/score?sessionkey=" + sessionKey.body().asString())
                .then()
                .statusCode(400);
    }

    @Test
    public void testHighScoreList() {
        Response sessionKeyUser1 = expect().statusCode(200).given().when().get("/1/login");
        Response sessionKeyUser2 = expect().statusCode(200).given().when().get("/2/login");
        Response sessionKeyUser3 = expect().statusCode(200).given().when().get("/3/login");

        assertNotNull(sessionKeyUser1.body().asString());
        assertNotNull(sessionKeyUser2.body().asString());
        assertNotNull(sessionKeyUser3.body().asString());

        given().body("500").when().post("/1/score?sessionkey=" + sessionKeyUser1.body().asString())
                .then()
                .statusCode(200);

        given().body("750").when().post("/1/score?sessionkey=" + sessionKeyUser2.body().asString())
                .then()
                .statusCode(200);

        given().body("25").when().post("/1/score?sessionkey=" + sessionKeyUser3.body().asString())
                .then()
                .statusCode(200);

        Response scoreList = expect().statusCode(200).given().when().get("/1/highscorelist");
        assertEquals("2=750,1=500,3=25", scoreList.body().asString());
    }

    @Test
    public void testEmptyHighScoreList() {
        Response scoreList = expect().statusCode(200).given().when().get("/5/highscorelist");
        assertEquals("", scoreList.body().asString());
    }
}