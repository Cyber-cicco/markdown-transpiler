package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.example.ExpressionEvalutor.*;

public class BodyTester {

    private final String CODE_1 = "bonjour j`aime` le boulgour";
    private final String CODE_2 = "bonjour j`aime` le boulgour et les `petits` fours";
    private final String CODE_3 = "bonjour j`aime` le boulgour et les petits `fours`";
    private final String CODE_4 = "`bonjour` j`aime` le boulgour et les petits `fours`";
    private final String IMG_1 = "![Image **Alt** Text](https://placekitten.com/600/723)";
    private final String IMG_2 = "![Image **Alt** Text] (https://placekitten.com/600/723)";
    private final String IMG_3 = "Les chats c'est trop mignon : ![Image **Alt** Text](https://placekitten.com/600/723)";
    private final String IMG_4 = "Les chats c'est trop mignon : ![Image **Alt** Text](https://placekitten.com/600/723)(mais c'est aussi insupportable)";
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

    @Test
    public void testImgInBody(){
        BodyParser bodyParser = new BodyParser();
        bodyParser.reinit(0);
        HTMLTag body1 = bodyParser.parseTagBodyStructure(new HTMLTag("main"), IMG_1, ExpressionEvalutor::isBasicParagraphSeparator);
        assertEquals(body1.toString(), "<main > <img src=\"https://placekitten.com/600/72\" alt=\"Image **Alt** Text\"/> </main>");
        bodyParser.reinit(0);
        HTMLTag body2 = bodyParser.parseTagBodyStructure(new HTMLTag("main"), IMG_2, ExpressionEvalutor::isBasicParagraphSeparator);
        assertEquals(body2.toString(), "<main >![Image **Alt** Text] (https://placekitten.com/600/723)</main>");
        bodyParser.reinit(0);
        HTMLTag body3 = bodyParser.parseTagBodyStructure(new HTMLTag("main"), IMG_3, ExpressionEvalutor::isBasicParagraphSeparator);
        assertEquals(body3.toString(), "<main >Les chats c'est trop mignon :  <img src=\"https://placekitten.com/600/72\" alt=\"Image **Alt** Text\"/> </main>");
        bodyParser.reinit(0);
        HTMLTag body4 = bodyParser.parseTagBodyStructure(new HTMLTag("main"), IMG_4, ExpressionEvalutor::isBasicParagraphSeparator);
        assertEquals(body3.toString(), "<main >Les chats c'est trop mignon :  <img src=\"https://placekitten.com/600/72\" alt=\"Image **Alt** Text\"/> (mais c'est aussi insupportable)</main>");
        assert true;
    }
}
