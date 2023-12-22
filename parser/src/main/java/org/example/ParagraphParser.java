package org.example;

import java.util.ArrayList;
import java.util.List;

import static org.example.ExpressionEvalutor.*;

public class ParagraphParser {


    public int pos = 0;
    private int posPrevious = 0;
    private int startWhiteSpaces = 0;
    private int startTab = 0;
    List<Paragraph> paragraphs = new ArrayList<>();


    /**
     * MUT startTab
     * MUT startWhiteSpaces
     * MUT pos
     */
    private void handleStartWhitespaces(String fileContent){
        while (isStartWhiteSpaceOrTab(fileContent)){
            pos++;
        }
    }

    /**
     * MUT startTab
     * MUT startWhiteSpaces
     * MUT pos
     */
    private boolean isStartWhiteSpaceOrTab(String fileContent) {
        boolean isWhiteSpace = fileContent.charAt(pos) == ' ' ;
        boolean isTab = fileContent.charAt(pos) == '\t';
        if (isTab) startTab ++;
        if (isWhiteSpace) startWhiteSpaces++;
        return isWhiteSpace || isTab;
    }


    /**
     * MUT startTab
     * MUT startWhiteSpaces
     * MUT posPrevious
     * MUT pos
     */
    private void toNextParagraph(String fileContent){
        resetStartWhiteSpaces();
        while (!isOOB(fileContent, pos) && isBlankLine(fileContent, pos)){
            pos++;
        }
        posPrevious = pos;
    }

    private void resetStartWhiteSpaces(){
        startTab = 0;
        startWhiteSpaces = 0;
    }
    /**
     * MUT pos
     */
    private void skipLine(String fileContent){
        while (!isOOB(fileContent, pos)) {
            pos++;
        }
        pos++;
    }

    /**
     * MUT pos
     * MUT posPrevious
     * MUT paragraphs
     * MUT startTab
     * MUT startWhiteSpaces
     */
    private void handleIndentCodeBlock(String fileContent){
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isBasicParagraphSeparator(fileContent, pos)){
            pos++;
        }
        pos++;
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.CODE_BLOCK;
        paragraph.prefixHTML = HTMLTagFactory.getAsidePrefix();
        paragraph.suffixHTML = HTMLTagFactory.getAsideSuffix();
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }

    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     * MUT startTab
     * MUT startWhiteSpaces
     */
    private void handleHeading(String fileContent, int headNumber){
        pos += headNumber;
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.HEADING;
        paragraph.prefixHTML = HTMLTagFactory.getHeadingPrefix(headNumber);
        paragraph.suffixHTML = HTMLTagFactory.getHeadingSuffix(headNumber);
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }

    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     * MUT startTab
     * MUT startWhiteSpaces
     */
    private void handleBlockQuote(String fileContent) {
        pos++;
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isBasicParagraphSeparator(fileContent, pos)){
            pos++;
        }
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.BLOCKQUOTE;
        paragraph.prefixHTML = HTMLTagFactory.getBlockQuotePrefix();
        paragraph.suffixHTML = HTMLTagFactory.getBlockQuoteSuffix();
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }


    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     * MUT startTab
     * MUT startWhiteSpaces
     */
    private void handleCodeBlock(String fileContent, int tildaNumber) {
        Paragraph paragraph = new Paragraph();
        paragraph.kind = ParagraphKind.CODE_BLOCK;
        paragraph.prefixHTML = HTMLTagFactory.getCodePrefix();
        paragraph.suffixHTML = HTMLTagFactory.getCodeSuffix();
        pos += tildaNumber;
        posPrevious = pos;
        boolean isEndOfCodeBlock = false;
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))) {
            pos++;
        }
        if(!isOOB(fileContent, pos)) paragraph.marker = fileContent.substring(posPrevious, pos);
        pos++;
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isEndOfCodeBlock) {
            if(isTidla(fileContent.charAt(pos))) {
                int endTildaNumber = countTildas(fileContent, pos);
                isEndOfCodeBlock = isEndOfCodeBlock(tildaNumber, endTildaNumber, fileContent, pos+endTildaNumber);
            }
            pos++;
        }
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }

    private static int getPipeCount() {
        return 0;
    }

    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     */
    private void handleBaseParagraphOrTableau(String fileContent) {
        posPrevious = pos;
        boolean isTableau = isTableau(fileContent, pos);
        if(isTableau){
            handleTableau(fileContent);
            return;
        }
        handleBaseParagraph(fileContent);
    }

    private void handleBaseParagraph(String fileContent) {
        while (!isOOB(fileContent, pos) && !isBasicParagraphSeparator(fileContent, pos)) {
            pos++;
        }
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.SIMPLE;
        paragraph.prefixHTML = HTMLTagFactory.getDivPrefix();
        paragraph.suffixHTML = HTMLTagFactory.getDivSuffix();
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }

    private void handleTableau(String fileContent) {
        while (!isOOB(fileContent, pos) && !isTabBreaker(fileContent, pos)) {
            pos++;
        }
        pos++;
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.TABLE;
        paragraph.prefixHTML = HTMLTagFactory.getTablePrefix();
        paragraph.suffixHTML = HTMLTagFactory.getTableSuffix();
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }

    private void handleNestedLists(String fileContent) {
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))) {
            pos++;
        }
        if(hasNestedList(fileContent)) {
            handleNestedLists(fileContent);
        }
    }
    private void handleOrderedList(String fileContent) {
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
        if (hasNestedList(fileContent)) {
            handleNestedLists(fileContent);
        }
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.ORDERED_LIST_ITEM;
        paragraph.prefixHTML = HTMLTagFactory.getUlPrefix();
        paragraph.suffixHTML = HTMLTagFactory.getUlSuffix();
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }
    private void handleUnorderedList(String fileContent) {
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
        if (hasNestedList(fileContent)) {
            handleNestedLists(fileContent);
        }
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.UNORDERED_LIST_ITEM;
        paragraph.prefixHTML = HTMLTagFactory.getOlPrefix();
        paragraph.suffixHTML = HTMLTagFactory.getOlSuffix();
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }

    /**
     * PURE
     */
    private boolean hasNestedList(String fileContent) {
        int numWhiteSpaces = 0;
        int numTabs = 0;
        int peek = pos;
        while (!isOOB(fileContent, peek) && (isWhiteSpaceOrTab(fileContent, peek) || isCarriageReturn(fileContent.charAt(peek)))){
            if (isWhiteSpace(fileContent.charAt(peek))) numWhiteSpaces++;
            if (isTab(fileContent.charAt(peek))) numTabs++;
            peek++;
        }
        boolean indentMatches = numWhiteSpaces >= startWhiteSpaces + 2 || numTabs >= startTab + 1;
        boolean starterMatches = isStartOfOrderedListItem(fileContent, peek) || isStartOfUnorderedListItem(fileContent, peek);
        return indentMatches && starterMatches;
    }

    private void handleBlankLine(String fileContent) {
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
    }

    public List<Paragraph> parseParagraphs(String fileContent) {
        while(!isOOB(fileContent, pos)) {
            handleStartWhitespaces(fileContent);
            if (isBlankLine(fileContent, pos)){
                handleBlankLine(fileContent);
            }
            if (hasEnoughWhiteSpaceForCodeBlock(startWhiteSpaces)) {
                pos += startWhiteSpaces;
                handleIndentCodeBlock(fileContent);
                continue;
            }
            if (hasEnoughTabForCodeBlock(startTab)){
                pos += startTab;
                handleIndentCodeBlock(fileContent);
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
            int tildaNumber = countTildas(fileContent, pos);
            if (isCodeBlock(tildaNumber)) {
                handleCodeBlock(fileContent, tildaNumber);
                continue;
            }
            if(isStartOfUnorderedListItem(fileContent, pos)) {
                handleUnorderedList(fileContent);
                continue;
            }
            if(isStartOfOrderedListItem(fileContent, pos)) {
                handleOrderedList(fileContent);
                continue;
            }
            handleBaseParagraphOrTableau(fileContent);
        }
        paragraphs.forEach(p -> {
            System.out.println(p);
            System.out.println();
        });
        return paragraphs;
    }

}

