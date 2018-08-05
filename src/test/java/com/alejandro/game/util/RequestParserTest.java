package com.alejandro.game.util;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for RequestParser.
 *
 * @author afernandez
 */
public class RequestParserTest {

    private RequestParser requestParser;

    @Before
    public void init() {
        requestParser = new RequestParser();
    }

    @Test
    public void getUserOrLevelId() throws Exception {
        assertEquals(5, requestParser.getUserOrLevelId("/5/login"));
        assertEquals(17, requestParser.getUserOrLevelId("/17/score"));
        assertEquals(57892, requestParser.getUserOrLevelId("/57892/highscorelist"));

        // Limit case 2^31 - 1
        assertEquals(2147483647, requestParser.getUserOrLevelId("/2147483647/highscorelist"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUserOrLevelIdBigInteger() throws Exception {
        // Big integer, case 2^31
        requestParser.getUserOrLevelId("/2147483648/highscorelist");
    }

    @Test
    public void getQueryValue() throws Exception {
        assertEquals("f372412971ef49b88526d251f258cf9f", requestParser.getQueryValue("sessionkey=f372412971ef49b88526d251f258cf9f"));
        assertEquals("DifferentTestExample", requestParser.getQueryValue("sessionkey=DifferentTestExample"));
        assertNull(requestParser.getQueryValue(null));
    }

    @Test
    public void getScore() throws Exception {
        InputStream in = new ByteArrayInputStream("1575".getBytes());
        assertEquals(1575, requestParser.getScore(in));
    }
}