package org.example;

public class AutoClosingHTMLTag extends HTMLTag{
    public AutoClosingHTMLTag(String tag) {
        super(tag);
    }

    @Override
    public String toString() {
        return getAutoClosingTag();
    }
}
