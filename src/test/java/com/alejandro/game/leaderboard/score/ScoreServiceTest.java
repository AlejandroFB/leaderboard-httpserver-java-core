package com.alejandro.game.leaderboard.score;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ScoreService.
 *
 * @author afernandez
 */
public class ScoreServiceTest {
    private static final String RESULT_ONE   = "5=850,8=475,6=325,1=300,4=150,3=150,2=150,7=20";
    private static final String RESULT_TWO   = "14=980,5=850,11=650,8=475,9=400,6=325,1=300,4=150,3=150,2=150,15=135,12=85,13=50,7=20,10=15";
    private static final String RESULT_THREE = "14=980,5=850,11=650,8=475,9=400,6=325,1=300,4=150,3=150,2=150,15=135,12=85,13=50,7=20,30=16";
    private static final String RESULT_FORTH = "14=980,5=850,11=650,35=515,8=475,9=400,6=325,1=300,4=150,3=150,2=150,15=135,12=85,13=50,7=20";
    private static final String RESULT_FIFTH  = "29=1750,14=980,5=850,11=650,35=515,8=475,9=400,6=325,1=300,4=150,3=150,2=150,15=135,12=85,13=50";

    private ScoreService scoreService;
    private ConcurrentHashMap<Integer, ConcurrentSkipListSet<Score>> leaderboard;

    @Before
    public void init() throws Exception {
        scoreService = new ScoreService();
    }

    @Test
    public void addScore() throws Exception {
        initScores();

        assertEquals(RESULT_ONE, scoreService.convertToCsv(1));

        // Assert empty level
        assertEquals("", scoreService.convertToCsv(2));
    }

    @Test
    public void addScoreLeaderboardFull() throws Exception {
        initScores();
        initAdditionalScores();

        assertEquals(RESULT_TWO, scoreService.convertToCsv(1));

        scoreService.addScore(1, 50, 5);
        scoreService.addScore(1, 25, 15);
        assertEquals(RESULT_TWO, scoreService.convertToCsv(1));

        scoreService.addScore(1, 30, 16);
        assertEquals(RESULT_THREE, scoreService.convertToCsv(1));

        scoreService.addScore(1, 35, 515);
        assertEquals(RESULT_FORTH, scoreService.convertToCsv(1));

        scoreService.addScore(1, 29, 1750);
        assertEquals(RESULT_FIFTH, scoreService.convertToCsv(1));

        scoreService.addScore(5, 50, 5);
        scoreService.addScore(5, 25, 15);
        assertEquals("25=15,50=5", scoreService.convertToCsv(5));
    }

    private void initScores() {
        scoreService.addScore(1, 6, 325);
        scoreService.addScore(1, 7, 20);
        scoreService.addScore(1, 8, 475);
        scoreService.addScore(1, 1, 150);
        scoreService.addScore(1, 2, 150);
        scoreService.addScore(1, 3, 150);
        scoreService.addScore(1, 4, 150);
        scoreService.addScore(1, 2, 100);
        scoreService.addScore(1, 1, 300);
        scoreService.addScore(1, 5, 850);
    }

    private void initAdditionalScores() {
        scoreService.addScore(1, 9, 400);
        scoreService.addScore(1, 10, 15);
        scoreService.addScore(1, 11, 650);
        scoreService.addScore(1, 12, 85);
        scoreService.addScore(1, 13, 50);
        scoreService.addScore(1, 14, 980);
        scoreService.addScore(1, 15, 135);
    }
}