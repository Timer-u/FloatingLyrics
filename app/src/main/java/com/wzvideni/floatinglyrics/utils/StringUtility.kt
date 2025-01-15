package com.wzvideni.floatinglyrics.utils

import org.apache.commons.text.CharacterPredicate
import org.apache.commons.text.RandomStringGenerator
import org.apache.commons.text.similarity.JaccardSimilarity
import org.apache.commons.text.similarity.JaroWinklerSimilarity

// 字符串工具类
class StringUtility {
    companion object {
        // 随机字符串生成器
        val randomStringGenerator: RandomStringGenerator = RandomStringGenerator.Builder()
            .withinRange('0'.code, 'z'.code) // 指定字符范围，包括数字到字母
            .filteredBy(CharacterPredicate { Character.isLetterOrDigit(it) }) // 只生成字母或数字
            .get()

        // Jaccard相似度是两个集合之间的相似度度量，定义为两个集合的交集大小除以它们的并集大小。
        val jaccardSimilarity = JaccardSimilarity()

        // Jaro-Winkler相似度是基于Jaro相似度的改进，用于衡量两个字符串之间的相似度，考虑了字符的顺序和相对位置。
        val jaroWinklerSimilarity = JaroWinklerSimilarity()
    }
}
