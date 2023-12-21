package org.example;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {
    public Paragraph() {
    }

    public Paragraph(String prefixHTML, String suffixHTML, String content, ParagraphKind kind) {
        this.prefixHTML = prefixHTML;
        this.suffixHTML = suffixHTML;
        this.content = content;
        this.kind = kind;
    }

    public String prefixHTML;
    public String suffixHTML;
    public List<SyntaxToken> children = new ArrayList<>();
    public String marker;
    public String content;
    public ParagraphKind kind;

    @Override
    public String toString() {
        return "Paragraph{" +
                "prefixHTML='" + prefixHTML + '\'' +
                ", suffixHTML='" + suffixHTML + '\'' +
                ", content='" + content + '\'' +
                ", kind=" + kind +
                '}';
    }
}
