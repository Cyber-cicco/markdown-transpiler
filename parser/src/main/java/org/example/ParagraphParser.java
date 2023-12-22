package org.example;

import java.util.ArrayList;
import java.util.List;

public class ParagraphParser {


    public int pos = 0;
    private int posPrevious = 0;
    private int startWhiteSpaces = 0;
    private int startTab = 0;
    List<Paragraph> paragraphs = new ArrayList<>();

    /**
     * PURE
     */
    private boolean isOOB(String fileContent, int pos) {
        return pos >= fileContent.length() || pos < 0;
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
     * PURE
     */
    private boolean isBlankLine(String fileContent, int peek){
        while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))){
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
     * Peut être des soucis de performance ?
     */
    private boolean isBasicParagraphSeparator(String fileContent, int peek){
        if (isOOB(fileContent, peek + 1)) return true;
        return isCarriageReturn(fileContent.charAt(peek)) && isBlankLine(fileContent, peek + 1);
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
                !isOOB(fileContent, peek+1) &&
                fileContent.charAt(peek) == '-' &&
                isWhiteSpaceOrTab(fileContent, peek+1);
    }

    private boolean isPipe(char c) {
        return c == '|';
    }

    /**
     * PURE
     */
    private boolean isStartOfOrderedListItem(String fileContent, int peek){
       return !isOOB(fileContent, peek+2) &&
                Character.isDigit(fileContent.charAt(peek)) &&
                (fileContent.charAt(peek + 1) == ')' ||
                fileContent.charAt(peek + 1) == '.') &&
                isWhiteSpaceOrTab(fileContent, peek + 2);
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
        while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))){
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

    private boolean isDash(char c) {
        return c == '-';
    }
    private boolean isDeuxPoints(char c){
        return c == ':';
    }

    private boolean neighbourExists(String fileContent, int peek){
        return !isOOB(fileContent, peek - 1) && !isOOB(fileContent, peek + 1);
    }

    private boolean isPipeOrCR(String fileContent, int peek){
        return isPipe(fileContent.charAt(peek)) || isCarriageReturn(fileContent.charAt(peek));
    }

    private boolean neighbourIsValidForDeuxPoints(String fileContent, int peek) {
        return
                neighbourExists(fileContent, peek) &&
                (isDash(fileContent.charAt(peek - 1)) &&
                isPipeOrCR(fileContent, peek + 1)) ||
                (isDash(fileContent.charAt(peek + 1)) &&
                isPipeOrCR(fileContent, peek - 1));
    }

    private boolean isPipeAtStartLine(String fileContent, int peek) {
        return isPipe(fileContent.charAt(peek)) &&
                (isOOB(fileContent, peek - 1) ||
                isCarriageReturn(fileContent.charAt(peek - 1)));
    }

    private boolean isPipeAtEndOfLine(String fileContent, int peek) {
        return isPipe(fileContent.charAt(peek)) &&
                (isOOB(fileContent, peek + 1) ||
                isCarriageReturn(fileContent.charAt(peek + 1)));
    }

    public boolean isTabSeparator(String fileContent, int prevPipeCount, int peek) {
        int pipeCount = 0;
        while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))) {
            if (isPipeAtStartLine(fileContent, peek)){
                peek++;
                continue;
            }
            if (isPipeAtEndOfLine(fileContent, peek)){
                break;
            }
            if (isPipe(fileContent.charAt(peek))){
                pipeCount++;
                peek++;
                continue;
            }
            if(isDeuxPoints(fileContent.charAt(peek))) {
                if(neighbourIsValidForDeuxPoints(fileContent, peek)) {
                    peek++;
                    continue;
                }
            }
            if (isDash(fileContent.charAt(peek))) {
                peek++;
                continue;
            }
            return false;
        }
        return pipeCount == prevPipeCount;
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

    private boolean isTableau(String fileContent, int peek) {
        int pipeCount = 0;
        int prevPipeCount = 0;
        int lineCount = 0;
        while (!isOOB(fileContent, peek) && !isBasicParagraphSeparator(fileContent, peek)){
            if(lineCount == 1){
                if(isTabSeparator(fileContent, prevPipeCount, peek)){
                    while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))) {
                        peek++;
                    }
                    peek++;
                    lineCount++;
                    pipeCount = 0;
                    continue;
                } else {
                    return false;
                }
            }
            if (isPipeAtStartLine(fileContent, peek)) {
                peek++;
                continue;
            }
            if(isPipeAtEndOfLine(fileContent, peek)) {
                peek++;
                if(pipeCount == 0){
                    return false;
                }
                if(!(lineCount < 1 || prevPipeCount == pipeCount)){
                    return false;
                };
                lineCount++;
                prevPipeCount = pipeCount;
                pipeCount = 0;
                if(!isBasicParagraphSeparator(fileContent, peek)){
                    peek++;
                }
                continue;
            }
            if (isPipe(fileContent.charAt(peek))) {
                pipeCount++;
            }
            if(isOOB(fileContent, peek + 1) || isCarriageReturn(fileContent.charAt(peek+1))){
                if(pipeCount == 0) return false;
            }
            if (isCarriageReturn(fileContent.charAt(peek))) {
                if(!(lineCount < 1 || prevPipeCount == pipeCount)){
                    return false;
                };
                lineCount++;
                prevPipeCount = pipeCount;
                pipeCount = 0;
            }
            peek++;
        }
        return true;
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
        while (!isOOB(fileContent, pos) && !isBasicParagraphSeparator(fileContent, pos)) {
            pos++;
        }
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
            if (startWhiteSpaces >= 4) {
                pos += startWhiteSpaces;
                handleIndentCodeBlock(fileContent);
                continue;
            }
            if (startTab >= 2){
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

