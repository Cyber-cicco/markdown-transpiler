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
    @Test
    public void testIsOOB() {
        assertTrue(isOOB("      ", -1));
        assertTrue(isOOB("1234", 4));
        assertTrue(isOOB("", 0));
        assertFalse(isOOB("1234", 0));
        assertFalse(isOOB("1234", 3));
    }

    @Test
    public void testCountTilda(){
        assertEquals(countTildas("```", 0), 3);
        assertEquals(countTildas("```\ntest", 0), 3);
        assertEquals(countTildas("```test", 0), 3);
        assertEquals(countTildas("t`est", 0), 0);
    }

    @Test
    public void testNeighbourExists() {
        assertTrue(neighbourExists(" u ", 1));
        assertTrue(neighbourExists("\nu\n", 1));
        assertFalse(neighbourExists("\nu", 1));
        assertFalse(neighbourExists("u\n", 0));
        assertFalse(neighbourExists(" u ", 0));
    }

    @Test
    public void testIfStartOfOL() {
        assertTrue(isStartOfOrderedListItem("1) ", 0));
        assertTrue(isStartOfOrderedListItem("1. ", 0));
        assertTrue(isStartOfOrderedListItem("10. ", 0));
        assertTrue(isStartOfOrderedListItem("100) ", 0));
        assertFalse(isStartOfOrderedListItem("1- ", 0));
        assertFalse(isStartOfOrderedListItem("1- ", 0));
    }
    @Test
    public void testIfStartOfUL() {
        assertTrue(isStartOfUnorderedListItem("- ", 0));
        assertTrue(isStartOfUnorderedListItem("\n- ", 1));
        assertFalse(isStartOfUnorderedListItem("-", 0));
    }

    @Test
    public void testIsEndOfCodeBlock() {
        assertTrue((isEndOfCodeBlock(3, 3, "\n", 0)));
        assertTrue((isEndOfCodeBlock(3, 3, "\t   \t\r", 0)));
        assertTrue((isEndOfCodeBlock(3, 7, "\t   \t\r", 0)));
        assertFalse((isEndOfCodeBlock(3, 2, "\n", 0)));
    }

    @Test
    public void testPreviousIsBlank() {
        assertTrue(previousIsBlankAndUnderAmount("   |", 2));
        assertTrue(previousIsBlankAndUnderAmount("\n   |", 3));
        assertFalse(previousIsBlankAndUnderAmount("\n    |", 4));
    }
}
