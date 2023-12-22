package org.example;

import java.util.HashMap;
import java.util.Map;

public class HTMLTagFactory {

    private static final HTMLTag SECTION = new HTMLTag("section");
    private static final Map<Integer, HTMLTag> HEADINGMAP = new HashMap<>();
    private static final HTMLTag ASIDE = new HTMLTag("aside");
    private static final HTMLTag BLOCKQUOTE = new HTMLTag("blockquote");
    private static final HTMLTag CODE = new HTMLTag("code");
    private static final HTMLTag DIV = new HTMLTag("div");
    public static final HTMLTag UL = new HTMLTag("ul");
    public static final HTMLTag OL = new HTMLTag("ol");
    public static String getAsidePrefix(){
        return ASIDE.getOpeningTag();
    }
    public static String getAsideSuffix(){
        return ASIDE.getClosingTag();
    }
    public static String getSectionPrefix(){
        return SECTION.getOpeningTag();
    }
    public static String getSectionSuffix(){
        return SECTION.getClosingTag();
    }

    public static String getHeadingPrefix(int num){
        if (num < 1 || num > 6){
            throw new RuntimeException();
        }
        if(HEADINGMAP.containsKey(num)){
            return HEADINGMAP.get(num).getOpeningTag();
        }
        HEADINGMAP.put(num, new HTMLTag("h" + num));
        return HEADINGMAP.get(num).getOpeningTag();
    }

    public static String getHeadingSuffix(int num){
        if (num < 1 || num > 6){
            throw new RuntimeException();
        }
        if(HEADINGMAP.containsKey(num)){
            return HEADINGMAP.get(num).getClosingTag();
        }
        throw new RuntimeException();
    }

    public static String getBlockQuoteSuffix(){
        return BLOCKQUOTE.getClosingTag();
    }

    public static String getCodePrefix(){
        return CODE.getOpeningTag();
    }

    public static String getCodeSuffix(){
        return CODE.getClosingTag();
    }

    public static String getUlPrefix(){
        return UL.getOpeningTag();
    }
    public static String getUlSuffix(){
        return UL.getClosingTag();
    }
    public static String getOlPrefix(){
        return DIV.getOpeningTag();
    }
    public static String getOlSuffix() {
        return OL.getOpeningTag();
    }
    public static String getDivPrefix(){
        return OL.getClosingTag();
    }
    public static String getDivSuffix(){
        return DIV.getClosingTag();
    }
    public static String getBlockQuotePrefix(){
        return BLOCKQUOTE.getOpeningTag();
    }
}
