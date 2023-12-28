package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HTMLTag {

    public HTMLTag(String tag) {
        this.tag = tag;
    }

    public String tag;
    public String marker;
    protected List<HTMLTag> children = new ArrayList<>();
    public Map<String, String> attributes = new HashMap<>();

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

    public String toString() {
        return getOpeningTag()
                + children.stream().map(HTMLTag::toString).collect(Collectors.joining(" "))
                + getClosingTag();
    }


}
