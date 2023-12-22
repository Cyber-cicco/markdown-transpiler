package org.example;

class ExpressionEvalutor {


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
        if (isOOB(fileContent, peek + 1)) return true;
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
        return
                neighbourExists(fileContent, peek) &&
                        (isDash(fileContent.charAt(peek - 1)) &&
                                isPipeOrCR(fileContent, peek + 1)) ||
                        (isDash(fileContent.charAt(peek + 1)) &&
                                isPipeOrCR(fileContent, peek - 1));
    }

    static boolean isPipeAtStartLine(String fileContent, int peek) {
        return isPipe(fileContent.charAt(peek)) &&
                (isOOB(fileContent, peek - 1) ||
                        isCarriageReturn(fileContent.charAt(peek - 1)));
    }

    static boolean isPipeAtEndOfLine(String fileContent, int peek) {
        return isPipe(fileContent.charAt(peek)) &&
                (isOOB(fileContent, peek + 1) ||
                        isCarriageReturn(fileContent.charAt(peek + 1)));
    }

    static boolean hasEnoughTabForCodeBlock(int startTab) {
        return startTab >= 2;
    }
    static boolean hasEnoughWhiteSpaceForCodeBlock(int startWhiteSpaces) {
        return startWhiteSpaces >= 4;
    }

    static boolean isTabSeparator(String fileContent, int prevPipeCount, int peek) {
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
        while (isTidla(fileContent.charAt(peek))) {
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
        return isPipe(fileContent.charAt(peek)) || isCarriageReturn(fileContent.charAt(peek));
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
        return !isOOB(fileContent, peek+2) &&
                Character.isDigit(fileContent.charAt(peek)) &&
                (fileContent.charAt(peek + 1) == ')' ||
                        fileContent.charAt(peek + 1) == '.') &&
                isWhiteSpaceOrTab(fileContent, peek + 2);
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



    static boolean isTabBreaker(String fileContent, int peek) {
        if(isOOB(fileContent, peek +1)) return true;
        if (!isCarriageReturn(fileContent.charAt(peek))) return false;
        int nbHeading = countHeadingNumber(fileContent, peek + 1);
        return isBasicParagraphSeparator(fileContent, peek) ||
                isHeading(nbHeading) ||
                isBlockQuote(fileContent.charAt(peek + 1)) ||
                isStartOfUnorderedListItem(fileContent, peek + 1) ||
                isStartOfOrderedListItem(fileContent, peek + 1);
    }
    static boolean isTableau(String fileContent, int peek) {
        int pipeCount = 0;
        int prevPipeCount = 0;
        int lineCount = 0;
        while (!isOOB(fileContent, peek) && lineCount < 2){
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
}
