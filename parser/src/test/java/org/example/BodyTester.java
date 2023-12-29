package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.ExpressionEvalutor.*;

public class BodyTester {

    private final String CODE_1 = "bonjour j`aime` le boulgour";
    private final String CODE_2 = "bonjour j`aime` le boulgour et les `petits` fours";
    private final String CODE_3 = "bonjour j`aime` le boulgour et les petits `fours`";
    private final String CODE_4 = "`bonjour` j`aime` le boulgour et les petits `fours`";
    @Test
    public void testCodeInBody(){
        BodyParser bodyParser = new BodyParser();
        bodyParser.reinit(0);
        bodyParser.parseTagBodyStructure(new HTMLTag("main"), CODE_1, ExpressionEvalutor::isBasicParagraphSeparator);
        bodyParser.reinit(0);
        bodyParser.parseTagBodyStructure(new HTMLTag("main"), CODE_2, ExpressionEvalutor::isBasicParagraphSeparator);
        bodyParser.reinit(0);
        bodyParser.parseTagBodyStructure(new HTMLTag("main"), CODE_3, ExpressionEvalutor::isBasicParagraphSeparator);
        bodyParser.reinit(0);
        bodyParser.parseTagBodyStructure(new HTMLTag("main"), CODE_4, ExpressionEvalutor::isBasicParagraphSeparator);
        assert true;
    }
}
