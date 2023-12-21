package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HTMLTag {

    public HTMLTag(String tag) {
        this.tag = tag;
    }

    public String tag;
    public Map<String, String> attributes;

    public String getAutoClosingTag(){
        List<String> attributeList = new ArrayList<>();
        for(String key : attributes.keySet()){
            attributeList.add(key + "=" + "\"" + attributes.get(key) + "\"");
        }
        return "<" + tag + " " + String.join(" ", attributeList) + "/>";
    }
    public String getOpeningTag(){
        List<String> attributeList = new ArrayList<>();
        for(String key : attributes.keySet()){
            attributeList.add(key + "=" + "\"" + attributes.get(key) + "\"");
        }
        return "<" + tag + " " + String.join(" ", attributeList) + ">";
    }

    public String getClosingTag(){
        return "</" + tag + ">";
    }
}
