package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.ExpressionEvalutor.*;
public class BooleanTester {

    @Test
    public void testBlankLine(){
        assertTrue(isBlankLine(" ", 0));
        assertTrue(isBlankLine("\n ", 0));
        assertTrue(isBlankLine("\r ", 0));
        assertTrue(isBlankLine("\t\r ", 0));
        assertTrue(isBlankLine("", 0));

        assertFalse(isBlankLine("      \ttest", 0));
    }

    @Test
    public void testWhiteSpaceOrTab(){
        assertTrue(isWhiteSpaceOrTab(" ", 0));
        assertTrue(isWhiteSpaceOrTab("c ", 1));
        assertTrue(isWhiteSpaceOrTab("\t", 0));

        assertFalse(isWhiteSpaceOrTab("\0", 0));
        assertFalse(isWhiteSpaceOrTab("c", 0));
    }

    @Test
    public void testIsBasicParagraphSeparator() {
        assertTrue(isBasicParagraphSeparator("\n ", 0));
        assertTrue(isBasicParagraphSeparator("\n", 0));
        assertTrue(isBasicParagraphSeparator("\n\t\rtest", 0));

        assertFalse(isBasicParagraphSeparator("\n\tfoo", 0));
        assertFalse(isBasicParagraphSeparator("test", 0));
    }

    @Test
    public void testIsHeading() {
        assertTrue(isHeading(countHeadingNumber("### test", 0)));
        assertTrue(isHeading(countHeadingNumber("AZ### test", 2)));
        assertTrue(isHeading(countHeadingNumber("#", 0)));

        assertFalse(isHeading(countHeadingNumber("######## test", 0)));
        assertFalse(isHeading(countHeadingNumber("test", 0)));
        assertFalse(isHeading(countHeadingNumber("test", 0)));
        assertFalse(isHeading(countHeadingNumber("#test", 0)));
    }

    @Test
    public void testIsPipeAtLineStart() {
        assertTrue(isPipeAtStartLine("|", 0));
        assertTrue(isPipeAtStartLine("\n|", 1));
        assertFalse(isPipeAtStartLine("\n    |", 1));
    }

    @Test
    public void testIsTAtSL() {
        assertTrue(isTabAtStartLine("\t", 0));
        assertTrue(isTabAtStartLine("\n\t", 1));
    }
    @Test
    public void testIsWAtSL() {
        assertTrue(isWhiteSpaceAtStartLine(" ", 0));
        assertTrue(isWhiteSpaceAtStartLine("\n ", 1));
    }

    @Test
    public void isPipeAtEOL(){
        assertTrue(isPipeAtEndOfLine("|", 0));
        assertTrue(isPipeAtEndOfLine("|\rtest", 0));
        assertTrue(isPipeAtEndOfLine("|\t   ", 0));
        assertTrue(isPipeAtEndOfLine("|\t   \n", 0));
        assertTrue(isPipeAtEndOfLine("|\t   \r", 0));
        assertFalse(isPipeAtEndOfLine("|\t     c", 0));
        assertFalse(isPipeAtEndOfLine("|\t|", 0));
    }
}
