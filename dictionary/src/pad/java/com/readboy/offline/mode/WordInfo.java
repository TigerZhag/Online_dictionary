package com.readboy.offline.mode;

import java.text.CollationKey;
import java.text.Collator;
import java.text.RuleBasedCollator;

import static java.util.Locale.CHINA;

public class WordInfo implements Comparable<WordInfo>
{
    private static final RuleBasedCollator sCollator =
            (RuleBasedCollator) Collator.getInstance(CHINA);

    public String word;
    public int dictionaryId;
    public int wordIndex;
    public boolean readable;

    public WordInfo(String word, int dictionaryId, int wordIndex, boolean readable)
    {
        this.word = word;
        this.dictionaryId = dictionaryId;
        this.wordIndex = wordIndex;
        this.readable = readable;
    }

    @Override
    public int compareTo(WordInfo another)
    {
        CollationKey c1 = sCollator.getCollationKey(this.toString());
        CollationKey c2 = sCollator.getCollationKey(another.toString());

        return sCollator.compare(c1.getSourceString(), c2.getSourceString());
    }

    @Override
    public String toString()
    {
        return word + dictionaryId + wordIndex;
    }
}
