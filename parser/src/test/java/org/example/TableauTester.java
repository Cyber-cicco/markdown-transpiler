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
    @Test
    public void testIsTabBreaker(){
        assertTrue(isTabBreaker("\n# test", 0));
        assertTrue(isTabBreaker("\n\ntest", 0));
        assertTrue(isTabBreaker("\n     \ntest", 0));
        assertTrue(isTabBreaker("\n", 0));
        assertTrue(!isTabBreaker("\n################# test", 0));
        assertTrue(!isTabBreaker("\ntest", 0));
    }

    @Test
    public void testIsTableau(){
        assertTrue(isTableau(TAB_1, 0));
        assertTrue(isTableau(TAB_2, 0));
        assertTrue(isTableau(TAB_3, 0));
        assertTrue(!isTableau(TAB_4, 0));
        assertTrue(!isTableau(TAB_5, 0));
    }
}