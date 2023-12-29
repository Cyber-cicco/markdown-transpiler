package org.example;

import javax.swing.*;

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

    public HTMLTag getListTagBasedOnStart(String fileContent){
        if(isStartOfOrderedListItem(fileContent, pos)) {
            return new HTMLTag("ol");
        }
        return new HTMLTag("ul");
    }

    private void walkList(String fileContent, HTMLTag listItem, HTMLTag list, int startingWhiteSpaces, int indentOfTag){
        while (!isOOB(fileContent, pos)) {
            // cas où l'on trouve une ligne de séparation entre un élément de la liste et la suite du texte.
            if(isBasicParagraphSeparator(fileContent, pos)){

                BlankHTMLTag content = new BlankHTMLTag();
                content.content = fileContent.substring(posPrevious, pos);
                listItem.children.add(content);
                list.children.add(listItem);
                posPrevious = pos;
                skipLinesWhileBlank(fileContent);
                int numWhiteSpaces = countWhiteSpaces(fileContent, pos);

                if (numWhiteSpaces >= startingWhiteSpaces + 2) {
                    parseParagraphs(fileContent, listItem, startingWhiteSpaces + 2);

                    if(isStartOfUnorderedListItem(fileContent, pos) || isStartOfOrderedListItem(fileContent, pos)) {
                        HTMLTag newListItem = getListTagBasedOnStart(fileContent);
                        walkList(fileContent, newListItem, list, startingWhiteSpaces, indentOfTag + numWhiteSpaces);
                        break;
                    }
                }

                int peek = pos + numWhiteSpaces;

                if((isStartOfUnorderedListItem(fileContent, peek) || isStartOfOrderedListItem(fileContent, peek)) && numWhiteSpaces == indentOfTag) {

                    pos = peek;
                    HTMLTag newListItem = getListTagBasedOnStart(fileContent);
                    walkList(fileContent, newListItem, list, startingWhiteSpaces, indentOfTag + numWhiteSpaces);
                }
                break;
            }

            if(isCarriageReturn(fileContent.charAt(pos))) {
                pos++;
                int numWhiteSpaces = countWhiteSpaces(fileContent, pos);

                if(numWhiteSpaces <= indentOfTag + 4) {

                    pos += numWhiteSpaces;
                    printLine(fileContent, pos);

                    if(isStartOfUnorderedListItem(fileContent, pos) || isStartOfOrderedListItem(fileContent, pos)) {
                        BlankHTMLTag content = new BlankHTMLTag();
                        content.content = fileContent.substring(posPrevious, pos);
                        listItem.children.add(content);
                        list.children.add(listItem);
                        posPrevious = pos;
                        skipLinesWhileBlank(fileContent);

                        if(numWhiteSpaces < indentOfTag){
                            break;
                        }

                        HTMLTag newListItem = getListTagBasedOnStart(fileContent);
                        walkList(fileContent, newListItem, list, startingWhiteSpaces, indentOfTag + numWhiteSpaces);
                        break;
                    }
                }
            }
            pos++;
        }
    }

    private void printLine(String fileContent, int peek) {
        while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))) {
            peek ++;
        }
        System.out.println(fileContent.substring(pos, peek));
    }

    private void skipWhiteSpaces(String fileContent) {
        while (!isOOB(fileContent, pos) && isWhiteSpace(fileContent.charAt(pos))){
            pos++;
        }
    }

    private void handleList(String fileContent, int startingWhiteSpaces, HTMLTag parentTag, int indentOfTag) {
        posPrevious = pos;
        HTMLTag tag = new HTMLTag("li");
        HTMLTag listItem = getListTagBasedOnStart(fileContent);
        walkList(fileContent, listItem, tag, startingWhiteSpaces, indentOfTag);
        parentTag.children.add(tag);
        toNextParagraph(fileContent);
    }

    private void skipLinesWhileBlank(String fileContent) {
        while (!isOOB(fileContent, pos) && isBlankLine(fileContent, pos)){
            skipLine(fileContent);
        }
    }

    private int countWhiteSpaces(String fileContent, int peek) {
        int numWhiteSpaces = 0;
        while (!isOOB(fileContent, peek) && isWhiteSpace(fileContent.charAt(peek))) {
            numWhiteSpaces++;
            peek++;
        }
        return numWhiteSpaces;
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
                handleList(fileContent, startWhiteSpaces, parentTag, indentOfTag);
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

