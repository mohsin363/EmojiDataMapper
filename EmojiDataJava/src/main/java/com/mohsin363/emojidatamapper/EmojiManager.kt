package com.mohsin363.emojidatamapper

import com.mohsin363.emojidatamapper.EmojiLoader.loadEmojis

object EmojiManager {
    private val emojis: List<Emoji>
    private val pristineEmojis: List<Emoji>
    val all: List<Emoji> get() = pristineEmojis
    val allWithSkinVariations: List<Emoji> get() = emojis
    private val emojisByAlias = mutableMapOf<String, MutableList<Emoji>>()
    private val emojisByCategory = mutableMapOf<Category, MutableList<Emoji>>()
    internal val emojiTree: EmojiTrie

    init {
        val emojis = loadEmojis().sortedWith(Comparator { a, b ->
            when {
                a.isObsolete && !b.isObsolete -> 1
                !a.isObsolete && b.isObsolete -> -1
                else -> 0
            }
        }).toList()

        this.emojis = emojis

        val shorterUnicodeFirst =
            Comparator<Emoji> { a, b -> b.unified.unicode.compareTo(a.unified.unicode) }

        val namesInsertedInCategories = mutableSetOf<String>()
        val pristineEmojis = mutableListOf<Emoji>()
        for (emoji in emojis) {
            // Alias map
            for (alias in emoji.aliases) {
                val emojiList = (emojisByAlias[alias] ?: mutableListOf()) + emoji
                emojisByAlias[alias] = emojiList.sortedWith(shorterUnicodeFirst).toMutableList()
            }

            if (!emoji.isPristine) continue

            pristineEmojis += emoji

            // Category map
            if (namesInsertedInCategories.add(emoji.unified)) {
                val emojiListForCategory =
                    (emojisByCategory[emoji.category] ?: mutableListOf()) + emoji
                emojisByCategory[emoji.category] =
                    emojiListForCategory.sortedBy { it.sortOrder }.toMutableList()
            }

        }
        this.pristineEmojis = pristineEmojis
        emojiTree = EmojiTrie(emojis)
    }

    /**
     * Returns the [Emoji] for a given alias.
     *
     * @param alias the alias
     * @return the associated [Emoji], null if the alias
     * is unknown
     */
    @JvmStatic
    fun getForAlias(alias: String?): Emoji? {
        alias ?: return null
        return emojisByAlias[alias.trimAlias()]?.firstOrNull()
    }

    /**
     * Returns the [Emoji] for a given unicode.
     *
     * @param unicode the the unicode
     * @return the associated [Emoji], null if the
     * unicode is unknown
     */
    @JvmStatic
    fun getByUnicode(unicode: String?): Emoji? {
        unicode ?: return null
        return emojiTree.getEmoji(unicode)
    }

    /**
     * Tests if a given String is an emoji.
     *
     * @param string the string to test
     * @return true if the string is an emoji's unicode, false else
     */
    @JvmStatic
    fun isEmoji(string: String?): Boolean {
        return string != null && emojiTree.isEmoji(string.toCharArray())
    }

    @JvmStatic
    fun getByCategory(category: Category) = emojisByCategory[category].orEmpty()

    /**
     * Checks if sequence of chars contain an emoji.
     *
     * @param sequence Sequence of char that may contain emoji in full or
     * partially.
     * @return if char sequence in an exact match for an emoji
     */
    @JvmStatic
    fun isEmoji(sequence: CharArray?) = emojiTree.isEmoji(sequence)
}
