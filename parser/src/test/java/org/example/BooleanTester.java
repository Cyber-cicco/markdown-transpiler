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
        assertFalse(isBlankLine("      \tcaca", 0));
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
        assertTrue(isBasicParagraphSeparator("\n\t\rcaca", 0));
        assertFalse(isBasicParagraphSeparator("\n\tprout", 0));
        assertFalse(isBasicParagraphSeparator("caca", 0));
    }

    @Test
    public void testIsHeading() {
        assertTrue(isHeading(countHeadingNumber("### caca", 0)));
        assertTrue(isHeading(countHeadingNumber("AZ### caca", 2)));
        assertTrue(isHeading(countHeadingNumber("#", 0)));
        assertFalse(isHeading(countHeadingNumber("######## caca", 0)));
        assertFalse(isHeading(countHeadingNumber("caca", 0)));
        assertFalse(isHeading(countHeadingNumber("caca", 0)));
        assertFalse(isHeading(countHeadingNumber("#caca", 0)));
    }
}
