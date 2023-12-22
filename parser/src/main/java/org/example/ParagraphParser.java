package org.example;

import java.util.ArrayList;
import java.util.List;

import static org.example.ExpressionEvalutor.*;

public class ParagraphParser {


    public int pos = 0;
    private int posPrevious = 0;
    private int startWhiteSpaces = 0;
    private int startTab = 0;
    List<HTMLTag> bodyStructure = new ArrayList<>();


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
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))) {
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
        HTMLTag tag = new HTMLTag("code");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        bodyStructure.add(tag);
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
        HTMLTag tag = new HTMLTag("h" + headNumber);
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        bodyStructure.add(tag);
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
        HTMLTag tag = new HTMLTag("blockquote");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        bodyStructure.add(tag);
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
        HTMLTag tag = new HTMLTag("code");
        pos += tildaNumber;
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))) {
            pos++;
        }
        if(!isOOB(fileContent, pos)) tag.marker = fileContent.substring(posPrevious, pos);
        pos++;
        posPrevious = pos;
        while (!isOOB(fileContent, pos)) {
            if(isTidla(fileContent.charAt(pos))) {
                int endTildaNumber = countTildas(fileContent, pos);
                if (isEndOfCodeBlock(tildaNumber, endTildaNumber, fileContent, pos+endTildaNumber)) break;
            }
            pos++;
        }
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        bodyStructure.add(tag);
        pos++;
        skipLine(fileContent);
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
        HTMLTag tag = new HTMLTag("div");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        bodyStructure.add(tag);
        toNextParagraph(fileContent);
    }

    private void handleTableau(String fileContent) {
        while (!isOOB(fileContent, pos) && !isTabBreaker(fileContent, pos)) {
            pos++;
        }
        pos++;
        HTMLTag tag = new HTMLTag("table");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        bodyStructure.add(tag);
        toNextParagraph(fileContent);
    }

    private void handleNestedLists(String fileContent, int numWhiteSpaces, int numTabs) {
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))) {
            pos++;
        }
        pos++;
        if(hasNestedList(fileContent, numWhiteSpaces, numTabs)) {
            handleNestedLists(fileContent, numWhiteSpaces + 4, numTabs + 1);
        }
    }
    private void handleOrderedList(String fileContent) {
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
        if (hasNestedList(fileContent, startWhiteSpaces, startTab)) {
            handleNestedLists(fileContent, startWhiteSpaces, startTab);
        }
        HTMLTag tag = new HTMLTag("ol");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        bodyStructure.add(tag);
        toNextParagraph(fileContent);
    }
    private void handleUnorderedList(String fileContent) {
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
        if (hasNestedList(fileContent, startWhiteSpaces, startTab)) {
            handleNestedLists(fileContent, startWhiteSpaces, startTab);
        }
        HTMLTag tag = new HTMLTag("ul");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        bodyStructure.add(tag);
        toNextParagraph(fileContent);
    }

    /**
     * PURE
     */
    private boolean hasNestedList(String fileContent, int numWhiteSpacesB, int numTabsB) {
        int numWhiteSpaces = 0;
        int numTabs = 0;
        int peek = pos;
        while (!isOOB(fileContent, peek) && (isWhiteSpaceOrTab(fileContent, peek))){
            if (isWhiteSpace(fileContent.charAt(peek))) numWhiteSpaces++;
            if (isTab(fileContent.charAt(peek))) numTabs++;
            peek++;
        }
        boolean indentMatches = numWhiteSpaces >= numWhiteSpacesB + 2 || numTabs >= numTabsB + 1;
        boolean starterMatches = isStartOfOrderedListItem(fileContent, peek) || isStartOfUnorderedListItem(fileContent, peek);
        return indentMatches && starterMatches;
    }

    private void handleBlankLine(String fileContent) {
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
    }

    public List<HTMLTag> parseParagraphs(String fileContent) {
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
        bodyStructure.forEach(p -> {
            System.out.println(p);
            System.out.println();
        });
        return bodyStructure;
    }

}

