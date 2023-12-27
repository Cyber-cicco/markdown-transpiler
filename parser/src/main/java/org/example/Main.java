package org.example;

public class Main {

    public static final String TEST_MARKDOWN = """
| Header 1 | Header 2 | Header 3 |
:-:|----------|-:|
Row 1, Col 1 | Row 1, Col 2 | Row 1, Col 3 |
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 |
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
# Heading 1
## Heading 2
### Heading 3
##### Heading 4

**Bold Text**

*Italic Text*

~~Strikethrough Text~~

caca
---

caca
caca\s

**caca**  aezu***cac <html></html>
    caca



> Blockquote: This is a blockquote. Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: This is a blockquote.Blockquote: **This** is a blockquote.Blockquote: This is a blockquote. \s
> caca
> caca

>caca
caca

- Unordered List Item 1
- Unordered List Item 2
    - Nested Unordered List Item
- Unordered List Item 3

3. Ordered List Item 1

2) Ordered List Item 2
    3 Nested Ordered List Item \s

 5) caca

 - pipi

3. Ordered List Item 3

- Unordered List Item 1

- Unordered List Item 2

  - Nested Unordered List Item

- Unordered List Item 3
[Link](https://www.example.com)

![Image Alt Text](https://placekitten.com/200/306)

`Inline Code`

```python
# Code Block
def hello_world():
``   print("Hello, World!")
```

caca

| Header 1 | Header 2 | Header 3 |
:-:|----------|-:
Row 1, Col 1 | Row 1, Col 2 | Row 1, Col 3 |
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 |
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |

| Left-aligned | Center-aligned | Right-aligned |
|:------------|:--------------:|-------------:|
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

| Header 1 | Header 2 | Header 3 |
|:-:|----------|----------|            
| Header 1 | Header 2 | Header 3 |
>|:-:|----------|----------|            
""";
    public static final String TEST_TABLEAU = """
| Header 1 | Header 2 | Header 3 |
   :-:|----------|-:                       
| Row 1, Col 1 | Row 1, Col 2 | Row 1, Col 3 |
| Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 |
| Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |

oui
            """;

    public static final String TEST_LIST = """
- Unordered List Item 1
- Unordered List Item 2

    | Header 1 | Header 2 | Header 3 |
    :-:|----------|-:                       
    | Row 1, Col 1 | Row 1, Col 2 | Row 1, Col 3 |
    | Row 2, Col 1 | Row 2, Col 2 | Row 2, Col 3 |
    | Row 3, Col 1 | Row 3, Col 2 | Row 3, Col 3 |
    
    - Nested Unordered List Item
- Unordered List Item 3
""";
    public static void main(String[] args) {
        ParagraphParser parser = new ParagraphParser();
        parser.init(TEST_LIST);
    }

}