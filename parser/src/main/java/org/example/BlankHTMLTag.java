package org.example;

public class BlankHTMLTag extends HTMLTag {
    public BlankHTMLTag() {
        super("");
    }

    public BlankHTMLTag(String fileContent, int posPrevious, int pos){
        super("");
        content = fileContent.substring(posPrevious, pos);
    }

    public String content;

    @Override
    public String toString(){
        return content;
    }
}
