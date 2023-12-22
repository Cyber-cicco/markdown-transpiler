package org.example;

public class BlankHTMLTag extends HTMLTag {
    public BlankHTMLTag() {
        super("");
    }

    public String content;

    @Override
    public String toString(){
        return content;
    }
}
