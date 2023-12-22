package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.ExpressionEvalutor.*;

class TableauTester {

    private final String TAB_1 = """
| Header 1 | Header 2 | Header 3 |
|:-----:|----------|----------|
| Row 1, Col 1 | Row 1, Col 2 | Row 1, Col 3 |
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 |
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
            """;
    private final String TAB_2 = """
| Header 1 | Header 2 | Header 3 |
|:-----:|----------|----------|
| Row 1, Col 1 
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 | test
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
            """;
    private final String TAB_3 = """
 Header 1 | Header 2 | Header 3 
|:-----|:----------:|----------:|
| Row 1, Col 1 
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 | test
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
            """;
    private final String TAB_4 = """
 Header 1 | Header 2  Header 3 
|:-----|:----------:|----------:|
| Row 1, Col 1 
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 | test
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
            """;
    private final String TAB_5 = """
 Header 1 | Header 2  | Header 3 
|:-----|:----------:|---:-------:|
| Row 1, Col 1 
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 | test
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
            """;
    private final String TAB_6 = """ 
Header 1 | Header 2 | Header 3 |
    :-----:|----------|----------|
| Row 1, Col 1 
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 | test
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
            """;
    private final String TAB_7 = """ 
Header 1 | Header 2 | Header 3 |
 :-----:|----------|----------|
| Row 1, Col 1 
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 | test
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
            """;
    private final String TAB_8 = """ 
Header 1 | Header 2 | Header 3 |
 :----- :|----------|----------|
| Row 1, Col 1 
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 | test
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
            """;
    private final String TAB_9 = """ 
 |||
 |||
            """;
    private final String TAB_10 = """ 
 |||
 |-|-|
            """;
    @Test
    public void testIsTabBreaker(){
        assertTrue(isTabBreaker("\n# test", 0));
        assertTrue(isTabBreaker("\n\ntest", 0));
        assertTrue(isTabBreaker("\n     \ntest", 0));
        assertTrue(isTabBreaker("\n", 0));
        assertTrue(isTabBreaker("\n- ", 0));
        assertTrue(isTabBreaker("\n   - ", 0));
        assertTrue(isTabBreaker("\n     test", 0));
        assertTrue(isTabBreaker("\n     2) ", 0));
        assertFalse(isTabBreaker("\n   test", 0));
        assertFalse(isTabBreaker("\n################# test", 0));
        assertFalse(isTabBreaker("\ntest", 0));
    }

    @Test
    public void testIsTableau(){
        assertTrue(isTableau(TAB_1, 0));
        assertTrue(isTableau(TAB_2, 0));
        assertTrue(isTableau(TAB_3, 0));
        assertTrue(isTableau(TAB_7, 0));
        assertTrue(isTableau(TAB_10, 0));
        assertFalse(isTableau(TAB_4, 0));
        assertFalse(isTableau(TAB_5, 0));
        assertFalse(isTableau(TAB_6, 0));
        assertFalse(isTableau(TAB_8, 0));
        assertFalse(isTableau(TAB_9, 0));
    }

    @Test
    public void testIsValidForDeuxPoints(){
        assertTrue(neighbourIsValidForDeuxPoints("\n:-", 1));
        assertTrue(neighbourIsValidForDeuxPoints("-:", 1));
        assertTrue(neighbourIsValidForDeuxPoints(" :-", 1));
        assertTrue(neighbourIsValidForDeuxPoints("|:-", 1));
        assertTrue(neighbourIsValidForDeuxPoints("-:|", 1));
        assertFalse(neighbourIsValidForDeuxPoints(" :|", 1));
        assertFalse(neighbourIsValidForDeuxPoints("-:-", 1));
        assertFalse(neighbourIsValidForDeuxPoints("c:-", 1));
    }
}