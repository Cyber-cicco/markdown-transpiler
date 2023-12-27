package org.example;

import java.util.List;

import static org.example.ExpressionEvalutor.*;

public class ParagraphParser {


    public int pos = 0;
    private int posPrevious = 0;
    private int startWhiteSpaces = 0;
    private int startTab = 0;
    HTMLTag rootTag = new HTMLTag("main");


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

    private boolean isIndentedForCodeBlock(int numWhiteSpaces, int intendedWhiteSpaces){
        return numWhiteSpaces >= intendedWhiteSpaces;
    }

    private void walkIndentedCodeBlock(String fileContent, int intendedWhiteSpaces){
        int numWhiteSpaces = 0;
        int peek = pos;
        while (!isOOB(fileContent, peek) && isWhiteSpace(fileContent.charAt(peek))) {
            peek++;
            numWhiteSpaces++;
        }
        if(isIndentedForCodeBlock(numWhiteSpaces, intendedWhiteSpaces)) {
            pos = peek;
            while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))) {
                pos++;
            }
            pos++;
            walkIndentedCodeBlock(fileContent, intendedWhiteSpaces);
        }
    }
    /**
     * MUT pos
     * MUT posPrevious
     * MUT paragraphs
     * MUT startTab
     * MUT startWhiteSpaces
     */
    private void handleIndentCodeBlock(String fileContent, HTMLTag parentTag, int intendedWhiteSpaces, int intendedTabs){
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isCarriageReturn(fileContent.charAt(pos))){
            pos++;
        }
        pos++;
        HTMLTag tag = new HTMLTag("code");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        parentTag.children.add(tag);
        toNextParagraph(fileContent);
    }

    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     * MUT startTab
     * MUT startWhiteSpaces
     */
    private void handleHeading(String fileContent, int headNumber, HTMLTag parentTag){
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
        parentTag.children.add(tag);
        toNextParagraph(fileContent);
    }

    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     * MUT startTab
     * MUT startWhiteSpaces
     */
    private void handleBlockQuote(String fileContent, HTMLTag parentTag) {
        pos++;
        posPrevious = pos;
        while (!isOOB(fileContent, pos) && !isBasicParagraphSeparator(fileContent, pos)){
            pos++;
        }
        HTMLTag tag = new HTMLTag("blockquote");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        parentTag.children.add(tag);
        toNextParagraph(fileContent);
    }


    /**
     * MUT posPrevious
     * MUT paragraphs
     * MUT pos
     * MUT startTab
     * MUT startWhiteSpaces
     */
    private void handleCodeBlock(String fileContent, int tildaNumber, HTMLTag parentTag) {
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
        parentTag.children.add(tag);
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
    private void handleBaseParagraphOrTableau(String fileContent, HTMLTag parentTag, int indentOfTag) {
        posPrevious = pos;
        boolean isTableau = isTableau(fileContent, pos, indentOfTag);
        if(isTableau){
            handleTableau(fileContent, parentTag, indentOfTag);
            return;
        }
        handleBaseParagraph(fileContent, parentTag);
    }

    private void handleBaseParagraph(String fileContent, HTMLTag parentTag) {
        while (!isOOB(fileContent, pos) && !isBasicParagraphSeparator(fileContent, pos)) {
            pos++;
        }
        HTMLTag tag = new HTMLTag("p");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        parentTag.children.add(tag);
        toNextParagraph(fileContent);
    }

    private void handleTableau(String fileContent, HTMLTag parentTag, int indentOfTag) {
        while (!isOOB(fileContent, pos) && !isTabBreaker(fileContent, pos, indentOfTag)) {
            pos++;
        }
        pos++;
        HTMLTag tag = new HTMLTag("table");
        BlankHTMLTag child = new BlankHTMLTag();
        child.content = fileContent.substring(posPrevious, pos);
        tag.children.add(child);
        parentTag.children.add(tag);
        toNextParagraph(fileContent);
    }

    private void handleList(String fileContent, int startingWhiteSpaces, HTMLTag parentTag, int indentOfTag) {
        HTMLTag tag = new HTMLTag("li");
        String sublistTag = "";
        while (!isOOB(fileContent, pos)) {
            if (isStartOfOrderedListItem(fileContent, pos)) {
                sublistTag = "ol";
                posPrevious = pos;
            }
            if (isStartOfUnorderedListItem(fileContent, pos)) {
                sublistTag = "ul";
                pos++;
                posPrevious = pos;
            }
            if(isBasicParagraphSeparator(fileContent, pos)){
                handleListBreakPoint(fileContent, sublistTag, tag);
                while (!isOOB(fileContent, pos) && isBlankLine(fileContent, pos)){
                    skipLine(fileContent);
                }
                int numWhiteSpaces = 0;
                int peek = pos;
                while (!isOOB(fileContent, peek) && isWhiteSpace(fileContent.charAt(peek))){
                    peek++;
                    numWhiteSpaces++;
                }
                if(numWhiteSpaces >= indentOfTag) {
                    parseParagraphs(fileContent, tag, startingWhiteSpaces + 2);
                }
                if(!(isStartOfOrderedListItem(fileContent, pos) || isStartOfUnorderedListItem(fileContent, pos))) {
                    break;
                }
            }
            if(!isOOB(fileContent, pos + startingWhiteSpaces + 4) && isCarriageReturn(fileContent.charAt(pos))){
                pos++;
                while (isWhiteSpace(fileContent.charAt(pos))) {
                    pos++;
                }
                if(isStartOfUnorderedListItem(fileContent, pos) || isStartOfOrderedListItem(fileContent, pos)){
                    handleListBreakPoint(fileContent, sublistTag, tag);
                    continue;
                }
            }
            pos++;
        }
        pos++;
        parentTag.children.add(tag);
        toNextParagraph(fileContent);
    }

    private void handleListBreakPoint(String fileContent, String sublistTag, HTMLTag tag) {
        HTMLTag unorderedItem = new HTMLTag(sublistTag);
        BlankHTMLTag content = new BlankHTMLTag();
        content.content = fileContent.substring(posPrevious, pos);
        unorderedItem.children.add(content);
        tag.children.add(unorderedItem);
        while (!isOOB(fileContent, pos) && isBlankLine(fileContent, pos)){
            skipLine(fileContent);
        }
        posPrevious = pos;
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

    public HTMLTag parseParagraphs(String fileContent, HTMLTag parentTag, int indentOfTag) {
        while(!isOOB(fileContent, pos)) {
            handleStartWhitespaces(fileContent);
            if(startWhiteSpaces < indentOfTag) {
                break;
            }
            if (isBlankLine(fileContent, pos)){
                handleBlankLine(fileContent);
            }
            if (hasEnoughWhiteSpaceForCodeBlock(startWhiteSpaces, indentOfTag)) {
                handleIndentCodeBlock(fileContent, parentTag, NUM_SW, NUM_TAB);
                continue;
            }
            if (hasEnoughTabForCodeBlock(startTab)){
                pos += startTab;
                handleIndentCodeBlock(fileContent, parentTag, NUM_SW, NUM_TAB);
                continue;
            }
            int headNumber = countHeadingNumber(fileContent, pos);
            if(isHeading(headNumber)) {
                handleHeading(fileContent, headNumber, parentTag);
                continue;
            }
            if(isBlockQuote(fileContent.charAt(pos))) {
                handleBlockQuote(fileContent, parentTag);
                continue;
            }
            int tildaNumber = countTildas(fileContent, pos);
            if (isCodeBlock(tildaNumber)) {
                handleCodeBlock(fileContent, tildaNumber, parentTag);
                continue;
            }
            if(isStartOfUnorderedListItem(fileContent, pos) || isStartOfOrderedListItem(fileContent, pos)) {
                handleList(fileContent, pos, parentTag, indentOfTag);
                continue;
            }
            handleBaseParagraphOrTableau(fileContent, parentTag, indentOfTag);
        }
        return parentTag;
    }

    public void init(String fileContent){
        parseParagraphs(fileContent, rootTag, 0);
        System.out.println(rootTag);
    }

}

