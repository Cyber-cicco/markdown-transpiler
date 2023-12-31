package org.example;

class ExpressionEvalutor {

    public static final int NUM_SW = 4;
    public static final int NUM_TAB = 2;

    /**
     * PURE
     */
    static boolean isBlankLine(String fileContent, int peek){
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
    static boolean isWhiteSpaceOrTab(String fileContent, int peek) {
        return fileContent.charAt(peek) == ' ' || fileContent.charAt(peek) == '\t';
    }

    static boolean isWhiteSpace(char c){
        return c == ' ';
    }
    static boolean isTab(char c){
        return c == '\t';
    }

    /**
     * PURE
     * Peut être des soucis de performance ?
     */
    static boolean isBasicParagraphSeparator(String fileContent, int peek){
        if (isOOB(fileContent, peek)) return true;
        return isCarriageReturn(fileContent.charAt(peek)) && isBlankLine(fileContent, peek + 1);
    }
    /**
     * PURE
     */
    static int countHeadingNumber(String fileContent, int peek) {
        int headNumber = 0;
        while (!isOOB(fileContent, peek) && fileContent.charAt(peek) == '#'){
            peek++;
            headNumber ++;
        }
        if(isOOB(fileContent, peek)){
            return headNumber;
        }
        if(isWhiteSpaceOrTab(fileContent, peek) || isCarriageReturn(fileContent.charAt(peek))) return headNumber;
        return 0;
    }

    /**
     * PURE
     */
    static boolean isHeading(int headNumber){
        return  headNumber > 0 && headNumber <= 6;
    }

    /**
     * PURE
     */
    static boolean isCarriageReturn(char c){
        return c == '\n' || c == '\r';
    }
    static boolean neighbourIsValidForDeuxPoints(String fileContent, int peek) {
        if(isOOB(fileContent, peek+1) && !isOOB(fileContent, peek-1)){
            return isDash(fileContent.charAt(peek -1));
        }
        return
                neighbourExists(fileContent, peek) &&
                        (isDash(fileContent.charAt(peek - 1)) &&
                                isPipeOrBlankLine(fileContent, peek + 1)) ||
                        (isDash(fileContent.charAt(peek + 1)) &&
                                (isPipeOrCR(fileContent, peek - 1) || isWhiteSpace(fileContent.charAt(peek -1))));
    }

    static boolean isPipeAtStartLine(String fileContent, int peek, int indentOfTag) {
        return isPipe(fileContent.charAt(peek)) &&
                (isOOB(fileContent, peek - 1) ||
                        previousIsBlankAndUnderAmount(fileContent, peek - 1, indentOfTag));
    }

    static boolean previousIsBlankAndUnderAmount(String fileContent, int peek, int indentOfTag){
        int startWhiteSpaces = 0;
        while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))){
            if(!isWhiteSpaceOrTab(fileContent, peek)) {
                return false;
            }
            peek--;
            startWhiteSpaces++;
        }
        return startWhiteSpaces < NUM_SW + indentOfTag;
    }

    static boolean isPipeAtEndOfLine(String fileContent, int peek) {
        return isPipe(fileContent.charAt(peek)) &&
                (isOOB(fileContent, peek + 1) ||
                        isBlankLine(fileContent, peek + 1));
    }

    static boolean hasEnoughTabForCodeBlock(int startTab) {
        return startTab >= NUM_TAB;
    }
    static boolean hasEnoughWhiteSpaceForCodeBlock(int startWhiteSpaces, int indentOfTag) {
        return startWhiteSpaces >= indentOfTag + NUM_SW;
    }

    static boolean isTabSeparator(String fileContent, int prevPipeCount, int peek, int indentOfTag) {
        int pipeCount = 0;
        int wsCount = 0;
        int tCount = 0;
        int dashCount = 0;
        while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))) {
            if (isWhiteSpaceAtStartLine(fileContent, peek)) {
                while (isWhiteSpace(fileContent.charAt(peek))) {
                    wsCount ++;
                    peek++;
                }
            }
            if (isTabAtStartLine(fileContent, peek)) {
                while (isWhiteSpace(fileContent.charAt(peek))) {
                    tCount ++;
                    peek++;
                }
            }
            if(isBlankLine(fileContent, peek)){
                break;
            }
            if(hasEnoughWhiteSpaceForCodeBlock(wsCount, indentOfTag)) {
                return false;
            }
            if(hasEnoughTabForCodeBlock(tCount)) {
                return false;
            }
            if (isPipeAtStartLine(fileContent, peek, indentOfTag)){
                peek++;
                continue;
            }
            if (isPipeAtEndOfLine(fileContent, peek)){
                break;
            }
            if (isPipe(fileContent.charAt(peek))){
                if(dashCount < 1){
                    return false;
                }
                pipeCount++;
                peek++;
                dashCount = 0;
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
                dashCount ++;
                continue;
            }
            return false;
        }
        return pipeCount == prevPipeCount;
    }

    static boolean isTabAtStartLine(String fileContent, int peek) {
        return isTab(fileContent.charAt(peek)) &&
                (isOOB(fileContent, peek - 1) ||
                        isCarriageReturn(fileContent.charAt(peek - 1)));
    }

    static boolean isWhiteSpaceAtStartLine(String fileContent, int peek) {
        return isWhiteSpace(fileContent.charAt(peek)) &&
                (isOOB(fileContent, peek - 1) ||
                        isCarriageReturn(fileContent.charAt(peek - 1)));
    }

    /**
     * PURE
     */
    static boolean isOOB(String fileContent, int pos) {
        return pos >= fileContent.length() || pos < 0;
    }
    /**
     * PURE
     */
    static boolean isTidla(char c) {
        return c == '`';
    }

    /**
     * PURE
     */
    static int countTildas(String fileContent, int peek) {
        int tildaNumber = 0;
        while (!isOOB(fileContent, peek) && isTidla(fileContent.charAt(peek))) {
            tildaNumber++;
            peek++;
        }
        return tildaNumber;
    }

    static boolean isDash(char c) {
        return c == '-';
    }
    static boolean isDeuxPoints(char c){
        return c == ':';
    }

    static boolean neighbourExists(String fileContent, int peek){
        return !isOOB(fileContent, peek - 1) && !isOOB(fileContent, peek + 1);
    }

    static boolean isPipeOrCR(String fileContent, int peek){
        return isPipe(fileContent.charAt(peek)) || isBlankLine(fileContent,peek);
    }

    static boolean isPipeOrBlankLine(String fileContent, int peek) {
        return isPipe(fileContent.charAt(peek)) || isBlankLine(fileContent, peek);
    }

    /**
     * PURE
     */
    static boolean isBlockQuote(char c){
        return c == '>';
    }

    /**
     * PURE
     */
    static boolean isStartOfUnorderedListItem(String fileContent, int peek){
        return
                !isOOB(fileContent, peek+1) &&
                        fileContent.charAt(peek) == '-' &&
                        isWhiteSpaceOrTab(fileContent, peek+1);
    }

    static boolean isPipe(char c) {
        return c == '|';
    }

    /**
     * PURE
     */
    static boolean isStartOfOrderedListItem(String fileContent, int peek){
        if(isOOB(fileContent, peek) || !Character.isDigit(fileContent.charAt(peek))) {
            return false;
        }
        while (!isOOB(fileContent, peek) && Character.isDigit(fileContent.charAt(peek))) {
            peek++;
        }
        return !isOOB(fileContent, peek+1) &&
                (fileContent.charAt(peek) == ')' ||
                        fileContent.charAt(peek) == '.') &&
                isWhiteSpaceOrTab(fileContent, peek + 1);
    }

    /**
     * PURE
     */
    static boolean isCodeBlock(int tildaNumber) {
        return tildaNumber >= 3;
    }

    /**
     * PURE
     */
    static boolean isEndOfCodeBlock(int startTildas, int endTildas, String fileContent, int peek){
        boolean noneSpacesInLine = false;
        while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))){
            if(!isWhiteSpaceOrTab(fileContent, peek)) {
                noneSpacesInLine = true;
            }
            peek++;
        }
        return (startTildas <= endTildas) && !noneSpacesInLine;
    }



    static boolean isTabBreaker(String fileContent, int peek, int indentOfTag) {
        if(isOOB(fileContent, peek +1)) return true;
        if (!isCarriageReturn(fileContent.charAt(peek))) return false;
        int nbHeading = countHeadingNumber(fileContent, peek + 1);
        int numWhiteSpaces = 0;
        int lastPeek = peek;
        peek++;
        while (!isOOB(fileContent, peek) && isWhiteSpaceOrTab(fileContent, peek)) {
            peek++;
            numWhiteSpaces++;
        }
        return isBasicParagraphSeparator(fileContent, lastPeek) ||
                isHeading(nbHeading) ||
                isBlockQuote(fileContent.charAt(peek)) ||
                isStartOfUnorderedListItem(fileContent, peek) ||
                hasEnoughWhiteSpaceForCodeBlock(numWhiteSpaces, indentOfTag) ||
                isStartOfOrderedListItem(fileContent, peek);
    }
    static boolean isTableau(String fileContent, int peek, int indentOfTag) {
        int pipeCount = 0;
        int lineCount = 0;
        while (!isOOB(fileContent, peek) && lineCount < 2){
            if(lineCount == 1){
                if(isTabSeparator(fileContent, pipeCount, peek, indentOfTag)){
                    while (!isOOB(fileContent, peek) && !isCarriageReturn(fileContent.charAt(peek))) {
                        peek++;
                    }
                    peek++;
                    lineCount++;
                    continue;
                } else {
                    return false;
                }
            }
            if (isPipeAtStartLine(fileContent, peek, indentOfTag)) {
                peek++;
                continue;
            }
            if(isPipeAtEndOfLine(fileContent, peek)) {
                peek++;
                if(pipeCount == 0){
                    return false;
                }
                lineCount++;
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
                lineCount++;
            }
            peek++;
        }
        return true;
    }

    static boolean checkIfStartOfExpression(char c) {
        return c == '*' || c == '~' || c == '[' || c == '!' || c == '`' || c == '(';
    }
}
