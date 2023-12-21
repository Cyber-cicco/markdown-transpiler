package org.example;

import java.util.ArrayList;
import java.util.List;

public class ParagraphParser {


    private int pos = 0;
    private int posPrevious = 0;
    private int startWhiteSpaces = 0;
    private int startTab = 0;
    List<Paragraph> paragraphs = new ArrayList<>();

    /**
     * PURE
     */
    private boolean isEOF(String fileContent, int pos) {
        return pos >= fileContent.length();
    }

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
     * PURE
     */
    private int countHeadingNumber(String fileContent, int peek) {
        int headNumber = 0;
        while (fileContent.charAt(peek) == '#'){
            peek++;
            headNumber ++;
        }
        return  headNumber;
    }

    /**
     * PURE
     */
    private boolean isHeading(int headNumber){
        return  headNumber > 0 && headNumber <= 6;
    }

    /**
     * PURE
     */
    private boolean isCarriageReturn(char c){
        return c == '\n' || c == '\r';
    }

    /**
     * MUT startTab
     * MUT startWhiteSpaces
     * MUT posPrevious
     * MUT pos
     */
    private void toNextParagraph(String fileContent){
        resetStartWhiteSpaces();
        while (!isEOF(fileContent, pos) && isBlankLine(fileContent, pos)){
            pos++;
        }
        posPrevious = pos;
    }

    private void resetStartWhiteSpaces(){
        startTab = 0;
        startWhiteSpaces = 0;
    }

    /**
     * PURE
     */
    private boolean isBlankLine(String fileContent, int peek){
        while (!isEOF(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))){
            if (!isWhiteSpaceOrTab(fileContent, peek)){
                return false;
            }
            peek++;
        }
        return true;
    }

    /**
     * PURE
     */
    private boolean isWhiteSpaceOrTab(String fileContent, int peek) {
        return fileContent.charAt(peek) == ' ' || fileContent.charAt(peek) == '\t';
    }

    private boolean isWhiteSpace(char c){
        return c == ' ';
    }
    private boolean isTab(char c){
        return c == '\t';
    }

    /**
     * PURE
     */
    private boolean isBasicParagraphSeparator(String fileContent){
        if(pos <= fileContent.length()) {
            return isCarriageReturn(fileContent.charAt(pos)) && isBlankLine(fileContent, pos + 1);
        }
        return false;
    }

    /**
     * MUT pos
     */
    private void skipLine(String fileContent){
        while (!isEOF(fileContent, pos)) {
            pos++;
        }
        pos++;
    }

    /**
     * PURE
     */
    private boolean isTidla(char c) {
        return c == '`';
    }

    /**
     * PURE
     */
    private int countTildas(String fileContent, int peek) {
        int tildaNumber = 0;
        while (isTidla(fileContent.charAt(peek))) {
            tildaNumber++;
            peek++;
        }
        return tildaNumber;
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
        while (!isEOF(fileContent, pos) && !isBasicParagraphSeparator(fileContent)){
            pos++;
        }
        pos++;
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.SIDE_PARAGRAPH;
        paragraph.prefixHTML = HTMLTagFactory.getAsidePrefix();
        paragraph.suffixHTML = HTMLTagFactory.getAsideSuffix();
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }

    /**
     * PURE
     */
    private boolean isBlockQuote(char c){
        return c == '>';
    }

    /**
     * PURE
     */
    private boolean isStartOfUnorderedListItem(String fileContent, int peek){
        return
                !isEOF(fileContent, peek+1) &&
                fileContent.charAt(peek) == '-' &&
                isWhiteSpaceOrTab(fileContent, peek+1);
    }

    /**
     * PURE
     */
    private boolean isStartOfOrderedListItem(String fileContent, int peek){
        return Character.isDigit(fileContent.charAt(peek))
                && (fileContent.charAt(peek + 1) == ')' ||
                fileContent.charAt(peek + 1) == '.') &&
                isWhiteSpaceOrTab(fileContent, peek + 1);
    }

    /**
     * PURE
     */
    private boolean isCodeBlock(int tildaNumber) {
        return tildaNumber >= 3;
    }

    /**
     * PURE
     */
    private boolean isEndOfCodeBlock(int startTildas, int endTildas, String fileContent, int peek){
        boolean noneSpacesInLine = false;
        while (!isEOF(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))){
            if(!isWhiteSpaceOrTab(fileContent, peek)) {
                noneSpacesInLine = true;
            }
            peek++;
        }
        return (startTildas <= endTildas) && !noneSpacesInLine;
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
        while (!isEOF(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
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
        while (!isEOF(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
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
        while (!isEOF(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))) {
            pos++;
        }
        if(!isEOF(fileContent, pos)) paragraph.marker = fileContent.substring(posPrevious, pos);
        pos++;
        posPrevious = pos;
        while (!isEOF(fileContent, pos) && !isEndOfCodeBlock) {
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

    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     */
    private void handleBaseParagraph(String fileContent) {
        posPrevious = pos;
        while (!isEOF(fileContent, pos) && !isBasicParagraphSeparator(fileContent)){
            pos++;
        }
        pos++;
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.SIMPLE;
        paragraph.prefixHTML = HTMLTagFactory.getDivPrefix();
        paragraph.suffixHTML = HTMLTagFactory.getDivSuffix();
        paragraphs.add(paragraph);
        toNextParagraph(fileContent);
    }

    private void handleNestedLists(String fileContent) {
        while (!isEOF(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))) {
            pos++;
        }
        if(hasNestedList(fileContent)) {
            handleNestedLists(fileContent);
        }
    }
    private void handleUnorderedList(String fileContent) {
        posPrevious = pos;
        while (!isEOF(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
        if (hasNestedList(fileContent)) {
            handleNestedLists(fileContent);
        }
        Paragraph paragraph = new Paragraph();
        paragraph.content = fileContent.substring(posPrevious, pos);
        paragraph.kind = ParagraphKind.UNORDERED_LIST_ITEM;
        paragraph.prefixHTML = HTMLTagFactory.getUlPrefix();
        paragraph.suffixHTML = HTMLTagFactory.getUlSuffix();
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
        while (!isEOF(fileContent, peek) && (isWhiteSpaceOrTab(fileContent, peek) || isCarriageReturn(fileContent.charAt(peek)))){
            if (isWhiteSpace(fileContent.charAt(peek))) numWhiteSpaces++;
            if (isTab(fileContent.charAt(peek))) numTabs++;
            peek++;
        }
        boolean indentMatches = numWhiteSpaces >= startWhiteSpaces + 2 || numTabs >= startTab + 1;
        boolean starterMatches = isStartOfOrderedListItem(fileContent, peek) || isStartOfUnorderedListItem(fileContent, peek);
        return indentMatches && starterMatches;
    }

    public List<Paragraph> parseParagraphs(String fileContent) {
        while(!isEOF(fileContent, pos)) {
            handleStartWhitespaces(fileContent);
            if (startWhiteSpaces >= 4) {
                pos += 4;
                handleIndentCodeBlock(fileContent);
                continue;
            }
            if (startTab >= 2){
                pos += 2;
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
            System.out.println(fileContent.charAt(pos));
            System.out.println(isStartOfUnorderedListItem(fileContent, pos));
            if(isStartOfUnorderedListItem(fileContent, pos)) {
                handleUnorderedList(fileContent);
                continue;
            }
            handleBaseParagraph(fileContent);
        }
        paragraphs.forEach(p -> {
            System.out.println(p);
            System.out.println();
        });
        return paragraphs;
    }



}

