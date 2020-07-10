package ru.skillbranch.skillarticles.data.local

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.room.TypeConverter
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.data.repositories.MarkdownParser
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class DateConverter {

    @TypeConverter
    fun timestampToDate(timestamp: Long) : Date = Date(timestamp)

    @TypeConverter
    fun dateToTimestamp(date: Date) : Long = date.time

}

class MarkdownConverter {

    @TypeConverter
    fun toMarkdown(content: String?): List<MarkdownElement>? = content?.let { MarkdownParser.parse(it) }

}

class TagListConverter {

    val pattern: Pattern = Pattern.compile("#([A-Za-z0-9_-]+)")

    @TypeConverter
    fun toTagList(text: String?): List<String> {
        if (text == null) {
            return emptyList()
        }

        val matcher = pattern.matcher(text)

        val hashTags = mutableListOf<String>()
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()
            hashTags.add(text.substring(start, end))
        }
        return hashTags
    }

}