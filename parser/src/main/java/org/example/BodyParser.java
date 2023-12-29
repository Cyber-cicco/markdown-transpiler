package org.example;

import static org.example.ExpressionEvalutor.*;

/**
 * <div>
 * |
 *   <code>
 *   |
 *   </code>
 * |
 * |
 * </div>
 */
public class BodyParser {

    private int pos;
    private int posPrevious;
    public void reinit(int newPos) {
        pos = newPos;
        posPrevious = pos;
    }
    public static class TagToIndex{HTMLTag tag; int indexDebut; int indexFin; }
    public int parseTagBodyStructure(HTMLTag parentTag, String fileContent, EndCondition endCondition){
        while (!isOOB(fileContent, pos) && !endCondition.check(fileContent, pos)) {
            checkPotentialTags(fileContent, parentTag, endCondition);
            if(isOOB(fileContent, pos)){
                break;
            }
            pos++;
        }
        parentTag.children.add(new BlankHTMLTag(fileContent, posPrevious, pos));
        System.out.println(parentTag);
        return pos;
    }

    private void checkPotentialTags(String fileContent, HTMLTag parentTag, EndCondition endCondition) {
        switch (fileContent.charAt(pos)) {
            case '*' -> {
                handleBoldOrItalicPossibility(fileContent,  parentTag, endCondition);
            }
            case '~' -> {
                handleStrikeThroughPossibility(fileContent, parentTag, endCondition);
            }
            case '[' -> {
                handleLinkOrFootNotePossibility(fileContent, parentTag, endCondition);
            }
            case '!' -> {
                handleImagePossibility(fileContent, parentTag, endCondition);
            }
            case '`' -> {
                handleCodePossibility(fileContent, parentTag, endCondition);
            }
        }
    }

    private void changePosForNewCodeBreakpoint(int peek){
        posPrevious = pos;
        pos = peek;
    }

    private void addContentToParent(String fileContent, HTMLTag parentTag, int peek){
        HTMLTag parentContent = new BlankHTMLTag(fileContent, posPrevious, pos);
        parentTag.children.add(parentContent);
        changePosForNewCodeBreakpoint(peek);
    }

    private void handleCodePossibility(String fileContent, HTMLTag parentTag, EndCondition endCondition) {
        int peek = pos + 1;
        while(!endCondition.check(fileContent, peek)) {
            if(isTidla(fileContent.charAt(peek))) {
                peek++;
                addContentToParent(fileContent, parentTag, peek);
                HTMLTag codeTag = new HTMLTag("code");
                HTMLTag codeContent = new BlankHTMLTag(fileContent, posPrevious, pos);
                codeTag.children.add(codeContent);
                parentTag.children.add(codeTag);
                posPrevious = pos;
                break;
            }
            peek++;
        }
    }

    private void handleImagePossibility(String fileContent, HTMLTag parentTag, EndCondition endCondition) {
    }

    private void handleLinkOrFootNotePossibility(String fileContent, HTMLTag parentTag, EndCondition endCondition) {
    }

    private void handleStrikeThroughPossibility(String fileContent, HTMLTag parentTag, EndCondition endCondition) {
    }

    private void handleBoldOrItalicPossibility(String fileContent, HTMLTag parentTag, EndCondition endCondition) {
    }
}
