package ru.skillbranch.skillarticles

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.skillbranch.skillarticles.ui.custom.markdown.Element
import ru.skillbranch.skillarticles.ui.custom.markdown.MarkdownParser

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun parse_list_item() {
        val result = MarkdownParser.parse(unorderedListString)
        val actual = prepare<Element.UnorderedListItem>(result.elements)
        assertEquals(expectedUnorderedList, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_header() {
        val result = MarkdownParser.parse(headerString)
        val actual = prepare<Element.Header>(result.elements)

        assertEquals(expectedHeader, actual)

        val actualLevels = result.elements.spread()
            .filterIsInstance<Element.Header>()
            .map { it.level }
        assertEquals(listOf(1,2,3,4,5,6), actualLevels)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_quote() {
        val result = MarkdownParser.parse(quoteString)
        val actual = prepare<Element.Quote>(result.elements)

        assertEquals(expectedQuote, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_italic() {
        val result = MarkdownParser.parse(italicString)
        val actual = prepare<Element.Italic>(result.elements)

        assertEquals(expectedItalic, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_bold() {
        val result = MarkdownParser.parse(boldString)
        val actual = prepare<Element.Bold>(result.elements)

        assertEquals(expectedBold, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_strike() {
        val result = MarkdownParser.parse(strikeString)
        val actual = prepare<Element.Strike>(result.elements)

        assertEquals(expectedStrike, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_combine() {
        val result = MarkdownParser.parse(combineEmphasisString)

        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)

        assertEquals(expectedCombine["italic"], actualItalic)
        assertEquals(expectedCombine["bold"], actualBold)
        assertEquals(expectedCombine["strike"], actualStrike)

        printResults(actualItalic)
        printResults(actualBold)
        printResults(actualStrike)

        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_rule() {
        val result = MarkdownParser.parse(ruleString)
        val actual = prepare<Element.Rule>(result.elements)
        assertEquals(3, actual.size)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_inline_code() {
        val result = MarkdownParser.parse(inlineString)
        val actual = prepare<Element.InlineCode>(result.elements)

        assertEquals(expectedInline, actual)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_link() {
        val result = MarkdownParser.parse(linkString)
        val actual = prepare<Element.Link>(result.elements)
        val actualLink = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map { it.link }

        assertEquals(expectedLink["titles"], actual)
        assertEquals(expectedLink["links"], actualLink)

        printResults(actual)
        println("")
        printElements(result.elements)
    }

    @Test
    fun parse_ordered_list() {
        val unparsed = """
            1. text
            2. text
            3. text
             1. text
            A text
            1  
        """.trimIndent()
        val result = MarkdownParser.parse(unparsed)

        val orders: List<String> = result.elements.spread()
            .filterIsInstance<Element.OrderedListItem>()
            .map { it.order }
        assertEquals(3, orders.size)
        assertEquals("1.", orders[0])
        assertEquals("2.", orders[1])
        assertEquals("3.", orders[2])
    }

    @Test
    fun parse_block_code_text() {
        val unparsed = """
            ```code block
            code block
            code block```
            
            before ```code block
            code block
            code block```
            
            ```code block
            code block
            code block``` after
        """.trimIndent()

        val result = MarkdownParser.parse(unparsed)

        val types: List<Element.BlockCode.Type> = result.elements.spread()
            .filterIsInstance<Element.BlockCode>()
            .map { it.type }
        assertEquals(3, types.size)
        assertEquals(Element.BlockCode.Type.START, types[0])
        assertEquals(Element.BlockCode.Type.MIDDLE, types[1])
        assertEquals(Element.BlockCode.Type.END, types[2])
    }

    @Test
    fun parse_all() {
        val result = MarkdownParser.parse(markdownString)
        val actualUnorderedList = prepare<Element.UnorderedListItem>(result.elements)
        val actualHeaders = prepare<Element.Header>(result.elements)
        val actualQuotes = prepare<Element.Quote>(result.elements)
        val actualItalic = prepare<Element.Italic>(result.elements)
        val actualBold = prepare<Element.Bold>(result.elements)
        val actualStrike = prepare<Element.Strike>(result.elements)
        val actualRule = prepare<Element.Rule>(result.elements)
        val actualInline = prepare<Element.InlineCode>(result.elements)
        val actualLinkTitles = prepare<Element.Link>(result.elements)
        val actualLinks = result.elements.spread()
            .filterIsInstance<Element.Link>()
            .map { it.link }

        assertEquals(expectedMarkdown["unorderedList"], actualUnorderedList)
        assertEquals(expectedMarkdown["header"], actualHeaders)
        assertEquals(expectedMarkdown["quote"], actualQuotes)
        assertEquals(expectedMarkdown["italic"], actualItalic)
        assertEquals(expectedMarkdown["bold"], actualBold)
        assertEquals(expectedMarkdown["strike"], actualStrike)
        assertEquals(3, actualRule.size)
        assertEquals(expectedMarkdown["inline"], actualInline)
        assertEquals(expectedMarkdown["linkTitles"], actualLinkTitles)
        assertEquals(expectedMarkdown["links"], actualLinks)

        println("")
        printElements(result.elements)
    }

    @Test
    fun test_clear_simple() {
        val clearSimpleText = """
            before header text
            # Header1
            ---
            before `text
            after` text **bold**
        """.trimIndent()

        val expected = """
            before header text
            Header1
 
            before `text
            after` text bold
        """.trimIndent()

        val result = MarkdownParser.clear(clearSimpleText)
        var formattedResult = result?.replace("\n", "\\n")
        print(formattedResult)

        assertEquals(result, expected)
    }

    @Test
    fun test_clear() {
        val result = MarkdownParser.clear(markdownString)
        assertEquals(result, markdownOptionallyClearString)
    }

    private fun printResults(list:List<String>) {
        val iterator = list.iterator()
        while (iterator.hasNext()){
            println("find >> ${iterator.next()}")
        }
    }

    private fun printElements(list:List<Element>) {
        val iterator = list.iterator()
        while (iterator.hasNext()){
            println("element >> ${iterator.next()}")
        }
    }

    private fun Element.spread():List<Element> {
        val elements = mutableListOf<Element>()
        elements.add(this)
        elements.addAll(this.elements.spread())
        return elements
    }

    private fun List<Element>.spread() : List<Element> {
        val elements = mutableListOf<Element>()
        if (isNotEmpty()) elements.addAll(
            fold(mutableListOf()) {acc, el -> acc.also { it.addAll(el.spread()) }}
        )
        return elements
    }

    private inline fun <reified T:Element> prepare(list:List<Element>) : List<String> {
        return list
            .fold(mutableListOf<Element>()){ acc, el -> //spread inner elements
                acc.also { it.addAll(el.spread()) }
            }
            .filterIsInstance<T>() //filter only expected instance
            .map { it.text.toString() } //transform to element text
    }
}
