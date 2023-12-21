package org.example;

public class Main {

    public static final String TEST_MARKDOWN = """
# Heading 1
## Heading 2
### Heading 3
#### Heading 4

**Bold Text**

*Italic Text*

~~Strikethrough Text~~

---

1. Ordered List Item 1
2. Ordered List Item 2
1. Nested Ordered List Item
3. Ordered List Item 3

- Unordered List Item 1
    - Unordered List Item 2
- Nested Unordered List Item
- Unordered List Item 3

> Blockquote: This is a blockquote. Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: **This** is a blockquote.Blockquote: This is a blockquote.

[Link](https://www.example.com)

![Image Alt Text](https://placekitten.com/200/300)

`Inline Code`

```python
# Code Block
def hello_world():
print("Hello, World!")
```

| Header 1 | Header 2 | Header 3 |
|-----|----------|----------|
| Row 1, Col 1 | Row 1, Col 2 | Row 1, Col 3 |
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 |
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |

| Left-aligned | Center-aligned | Right-aligned |
|:-------------:|:--------------:|:-------------:|
| Left          | Center         | Right         |

- [x] Task 1
- [ ] Task 2
- [ ] Task 3

This is some text with a footnote[^1].

[^1]: Here is the footnote content.

Term 1
:   Definition 1

Term 2
:   Definition 2
            """;
    public static void main(String[] args) {
        ParagraphParser parser = new ParagraphParser();
        parser.parseParagraphs(TEST_MARKDOWN);
    }
}