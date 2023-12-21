package org.example;

import java.util.ArrayList;
import java.util.List;

/**
 * Markdown se divise en paragraphes
 * Un paragraphe peut se définir de deux façons:
 * une ligne vide (rien ou espace)
 * Un titre
 */
public class ParagraphParser {


    private int pos = 0;
    private int posPrevious = 0;
    private int startWhiteSpaces = 0;
    private int startTab = 0;
    List<Paragraph> paragraphs = new ArrayList<>();
    private boolean isEOF(String fileContent, int pos) {
        return fileContent.charAt(pos) != '\0';
    }

    /**
     * MUT startTab
     * MUT startWhiteSpaces
     * MUT pos
     * @param fileContent contenu du fichier
     */
    private void handleStartWhitespaces(String fileContent){
        while (isStartWhiteSpaceOrTab(fileContent)){
        }
    }

    /**
     * MUT startTab
     * MUT startWhiteSpaces
     * MUT pos
     * @param fileContent contenu du fichier
     * @return bool
     */
    private boolean isStartWhiteSpaceOrTab(String fileContent) {
        boolean isWhiteSpace = fileContent.charAt(pos) == ' ' ;
        boolean isTab = fileContent.charAt(pos) == '\t';
        if (isTab) startTab ++;
        if (isWhiteSpace) startWhiteSpaces++;
        return isWhiteSpace || isTab;
    }

    /**
     * PURE
     * @param fileContent contenu du fichier
     * @return bool
     */
    private int countHeadingNumber(String fileContent, int peek) {
        int headNumber = 0;
        while (fileContent.charAt(peek) == '#'){
            peek++;
            headNumber ++;
        }
        return  headNumber;
    }

    private boolean isHeading(int headNumber){
        return  headNumber > 0 && headNumber <= 6;
    }

    private boolean isCarriageReturn(char c){
        return c == '\n';
    }

    /**
     * MUT startTab
     * MUT startWhiteSpaces
     * MUT posPrevious
     */
    private void resetParagraph(){
        startTab = 0;
        startWhiteSpaces = 0;
        posPrevious = pos;
    }

    private boolean isBlankLine(String fileContent, int peek){
        while (!isEOF(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))){
            if (!isWhiteSpaceOrTab(fileContent, peek)){
                return false;
            }
        }
        return true;
    }

    private boolean isWhiteSpaceOrTab(String fileContent, int peek) {
        return fileContent.charAt(peek) == ' ' || fileContent.charAt(peek) == '\t';
    }

    /**
     * PURE
     * @param fileContent contenu
     * @return bool
     */
    private boolean isBasicParagraphSeparator(String fileContent){
        if(pos <= fileContent.length()) {
            return fileContent.charAt(pos) == '\n' && isBlankLine(fileContent, pos + 1);
        }
        return false;
    }

    /**
     * MUT pos
     * MUT posPrevious
     * MUT paragraphs
     * @param fileContent contenu
     */
    private void handleSideParagraph(String fileContent){
        posPrevious = pos;
        while (!isBasicParagraphSeparator(fileContent)){
            pos++;
        }
        pos++;
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.SIDE_PARAGRAPH;
        paragraph.prefixHTML = HTMLTagFactory.getAsidePrefix();
        paragraph.suffixHTML = HTMLTagFactory.getAsideSuffix();
        paragraphs.add(paragraph);
        resetParagraph();
    }

    public boolean isBlockQuote(char c){
        return c == '>';
    }

    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     * @param fileContent contenu
     */
    public void handleHeading(String fileContent, int headNumber){
        pos = headNumber;
        posPrevious = pos;
        while (!isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.HEADING;
        paragraph.prefixHTML = HTMLTagFactory.getHeadingPrefix(headNumber);
        paragraph.suffixHTML = HTMLTagFactory.getHeadingSuffix(headNumber);
        paragraphs.add(paragraph);
        resetParagraph();
    }

    public void handleBlockQuote(String fileContent) {
        posPrevious = pos;
        while (!isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
    }

    public List<Paragraph> parseParagraphs(String fileContent) {
        while(!isEOF(fileContent, pos)) {
            handleStartWhitespaces(fileContent);
            if (startWhiteSpaces >= 4) {
                pos += 4;
                handleSideParagraph(fileContent);
                continue;
            }
            if (startTab >= 1){
                pos += 1;
                handleSideParagraph(fileContent);
                continue;
            }
            int headNumber = countHeadingNumber(fileContent, pos);
            if(isHeading(headNumber)) {
                handleHeading(fileContent, headNumber);
                continue;
            }
            if(isBlockQuote(fileContent.charAt(pos))) {
                handleBlockQuote(fileContent);
                continue;
            }
        }
        return paragraphs;
    }
}

