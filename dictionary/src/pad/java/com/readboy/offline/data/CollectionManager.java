package com.readboy.offline.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.readboy.offline.mode.WordInfo;

import java.util.ArrayList;
import java.util.Collections;

import static com.readboy.offline.provider.CollectionProvider.AUTHORITY;
import static com.readboy.offline.provider.CollectionProvider.DICTIONARY_ID;
import static com.readboy.offline.provider.CollectionProvider.DICTIONARY_TYPE;
import static com.readboy.offline.provider.CollectionProvider.READABLE;
import static com.readboy.offline.provider.CollectionProvider.SCHEME;
import static com.readboy.offline.provider.CollectionProvider.WORD;
import static com.readboy.offline.provider.CollectionProvider.WORD_INDEX;

@SuppressWarnings("unused")
public class CollectionManager
{
    /* 字典ID */
    public static final int ID_BHDICT = 0;
    public static final int ID_CYDICT = 1;
    public static final int ID_LWDICT = 2;
    public static final int ID_DMDICT = 3;
    public static final int ID_GDDICT = 4;
    public static final int ID_HYDICT = 5;
    public static final int ID_XDDICT = 6;
    public static final int ID_JMDICT = 7;
    public static final int ID_XSDICT = 8;
    public static final int ID_YHDICT = 9;
    public static final int ID_YYDICT = 10;
    public static final int ID_NEWWORD = 11;
    public static final int ID_SPEECHDICT = 12;

    /* 字典类型 */
    public static final int YY_ENG = 0;    // 0: English
    public static final int YY_UTF = 1;    // 1: Unicode(单词不采用此编码)
    public static final int YY_CHS = 2;    // 2: GB2312 (Chinese)
    public static final int YY_ARB = 3;    // 3: Arab
    public static final int YY_FRE = 4;    // 4: French
    public static final int YY_JPS = 5;    // 5: Japanese
    public static final int YY_KRA = 6;    // 6: Korea
    public static final int YY_TKC = 7;    // 7: Turkic
    public static final int YY_VTN = 8;    // 8: Vietnam
    public static final int YY_IDS = 9;    // 9: Indonesia

    private static final int MAX_NUMBER = 1000;

    public static final int SUCCESSFULLY = 0;
    public static final int EXISTED = 1;
    public static final int OVERFLOW = 2;

    public static final Uri sUri = Uri.parse(SCHEME + AUTHORITY + "/item");

    /**
     * 向生词库中添加一个单词
     *
     * @param word         单词
     * @param dictionaryId 词典ID
     * @param wordIndex    单词索引
     * @param readable     是否能发音
     * @return 0：添加成功, 1：生词库中已存在该生词, 2：生词库中生词已满(1000个)，无法添加
     */
    public static int addWord(Context context, String word, int dictionaryId,
                              int wordIndex,
                              boolean readable)
    {
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor;
        cursor = contentResolver.query(sUri, null,
                DICTIONARY_ID + " = " + dictionaryId + " and " +
                        WORD_INDEX + " = " + wordIndex, null, null);
        if (cursor != null) // 检查是否存在
        {
            if (cursor.getCount() != 0)
            {
                cursor.close();
                return EXISTED;
            }
            cursor.close();
        }

        int type = getDictTypeById(dictionaryId);
        cursor = contentResolver.query(sUri, null, DICTIONARY_TYPE + " = " + type,
                null, null);
        int count;
        if (cursor != null) // 检查是否溢出
        {
            count = cursor.getCount();
            cursor.close();
            if (count >= MAX_NUMBER)
            {
                return OVERFLOW;
            }
        }

        ContentValues values = new ContentValues();
        values.put(DICTIONARY_ID, dictionaryId);
        values.put(WORD_INDEX, wordIndex);
        values.put(READABLE, readable ? 1 : 0);
        values.put(DICTIONARY_TYPE, type);
        values.put(WORD, word);
        contentResolver.insert(sUri, values);
        if (cursor != null) {
            cursor.close();
        }

        return SUCCESSFULLY;
    }

    /**
     * 从生词库删除一个单词
     *
     * @param dictionaryId 词典ID
     * @param wordIndex    单词索引
     * @return 被删除单词在数据库中的行号
     */
    public static int deleteWord(Context context, int dictionaryId, int wordIndex)
    {
        return context.getContentResolver().delete(sUri,
                DICTIONARY_ID + " = " + dictionaryId + " and " +
                        WORD_INDEX + " = " + wordIndex, null);
    }

    /**
     * 清空生词库中某种类型词典的所有单词
     *
     * @param type 词典类型
     */
    public static void clear(Context context, int type)
    {
        if (type == YY_ENG || type == YY_CHS)
        {
            Cursor c = context.getContentResolver().query(sUri, null,
                    DICTIONARY_TYPE + " = " + type, null, null);
            if (c != null)
            {
                c.close();
                context.getContentResolver().delete(sUri,
                        DICTIONARY_TYPE + " = " + type, null);
            }
        }
    }

    /**
     * 生词库是否包含该单词
     *
     * @param dictionaryId 词典ID
     * @param wordIndex    单词索引
     * @return 是否包含
     */
    public static boolean contains(Context context, int dictionaryId, int wordIndex)
    {
        boolean ret = false;
        Cursor c = context.getContentResolver().query(sUri, null,
                DICTIONARY_ID + " = " + dictionaryId + " and " +
                        WORD_INDEX + " = " + wordIndex, null, null
        );
        if (c != null)
        {
            if (c.getCount() != 0)
            {
                ret = true;
            }
            c.close();
        }
        return ret;
    }

    public static int getEnglishWordsNumber(Context context)
    {
        int count = 0;

        Cursor c = context.getContentResolver().query(sUri, null,
                DICTIONARY_TYPE + " = " + YY_ENG, null, null);
        if (c != null)
        {
            count = c.getCount();
            c.close();
        }

        return count;
    }

    public static int getChineseWordsNumber(Context context)
    {
        int count = 0;

        Cursor c = context.getContentResolver().query(sUri, null,
                DICTIONARY_TYPE + " = " + YY_CHS, null, null);
        if (c != null)
        {
            count = c.getCount();
            c.close();
        }

        return count;
    }

    public static int getReadableWordsNumber(Context context)
    {
        int count = 0;

        Cursor c = context.getContentResolver().query(sUri, null,
                DICTIONARY_TYPE + " = " + YY_ENG + " and " +
                        READABLE + " != " + 0, null, null);
        if (c != null)
        {
            count = c.getCount();
            c.close();
        }

        return count;
    }

    public static ArrayList<WordInfo> getEnglishWords(Context context)
    {
        ArrayList<WordInfo> words = new ArrayList<WordInfo>();

        Cursor cursor = context.getContentResolver().query(sUri, null,
                DICTIONARY_TYPE + " = " + YY_ENG, null, null);
        if (cursor == null || !cursor.moveToFirst())
        {
            if (cursor != null) {
                cursor.close();
            }
            return words;
        }

        do
        {
            words.add(new WordInfo(cursor.getString(cursor.getColumnIndex(WORD)),
                    cursor.getInt(cursor.getColumnIndex(DICTIONARY_ID)),
                    cursor.getInt(cursor.getColumnIndex(WORD_INDEX)),
                    cursor.getInt(cursor.getColumnIndex(READABLE)) != 0));

        }
        while (cursor.moveToNext());
        cursor.close();
        Collections.sort(words);
        return words;
    }

    public static ArrayList<WordInfo> getChineseWords(Context context)
    {
        ArrayList<WordInfo> words = new ArrayList<WordInfo>();

        Cursor cursor = context.getContentResolver().query(sUri, null,
                DICTIONARY_TYPE + " = " + YY_CHS, null, null);
        if (cursor == null || !cursor.moveToFirst())
        {
            if (cursor != null) {
                cursor.close();
            }
            return words;
        }

        do
        {
            words.add(new WordInfo(cursor.getString(cursor.getColumnIndex(WORD)),
                    cursor.getInt(cursor.getColumnIndex(DICTIONARY_ID)),
                    cursor.getInt(cursor.getColumnIndex(WORD_INDEX)),
                    cursor.getInt(cursor.getColumnIndex(READABLE)) != 0));

        }
        while (cursor.moveToNext());
        cursor.close();

        Collections.sort(words);
        return words;
    }

    public static ArrayList<WordInfo> getReadableWords(Context context)
    {
        ArrayList<WordInfo> words = new ArrayList<WordInfo>();
        Cursor cursor = context.getContentResolver().query(sUri, null,
                DICTIONARY_TYPE + " = " + YY_ENG + " and " +
                        READABLE + " != " + 0, null, null);
        if (cursor == null || !cursor.moveToFirst())
        {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

//        String[] words = new String[cursor.getCount()];
        int i = 0;
        do
        {
//            words[i++] = cursor.getString(cursor.getColumnIndex(WORD));
            words.add(new WordInfo(cursor.getString(cursor.getColumnIndex(WORD)),
                    cursor.getInt(cursor.getColumnIndex(DICTIONARY_ID)),
                    cursor.getInt(cursor.getColumnIndex(WORD_INDEX)),
                    cursor.getInt(cursor.getColumnIndex(READABLE)) != 0));
        }
        while (cursor.moveToNext());
        cursor.close();
        Collections.sort(words);
        return words;
    }

    private static String reformatWord(int dictionaryId, String word)
    {
        int curIndex, endIndex;
        StringBuilder sb = new StringBuilder();
        int dictType = getDictTypeById(dictionaryId);
        for (curIndex = 0; curIndex < word.length(); curIndex++)
        {
            if (word.charAt(curIndex) != ' ')
            {
                break;
            }
        }

        endIndex = 0;
        while (endIndex < word.length() && word.charAt(endIndex) != 0)
        {
            if (dictType == YY_ENG)
            {
                if (word.charAt(endIndex) >= 0x80)
                {
                    break;
                }
            }
            endIndex++;
        }

        if (dictType == YY_ENG)
        {
            int i;
            String filterChar;
            String s1 = " ;*.{}\'\"?/()![]<>0123456789&|^%$#@~`+=";
            String s2 = " ;*.{}\'\"?/()![]<>&|^%$#@~`+=";
            filterChar = s1;

            while (curIndex < endIndex) // 先滤除字符
            {
                for (i = 0; i < filterChar.length(); i++)
                {
                    if (word.charAt(curIndex) == filterChar.charAt(i))
                    {
                        curIndex++;
                        if (curIndex >= endIndex)
                        {
                            return sb.toString();
                        }
                        i = 0;
                    }
                    if (word.charAt(curIndex) == 0
                            || word.charAt(curIndex) == ','
                            && sb.length() != 0)
                    {
                        return sb.toString();
                    }
                }
                if (word.charAt(curIndex) == ' ')
                {
                    if (word.charAt(curIndex) == ' ')
                    {
                        curIndex++;
                        continue;
                    }
                    else if (word.charAt(curIndex) == '\0')
                    {
                        return sb.toString();
                    }
                }

                if (word.charAt(curIndex) > 0x80)
                {
                    curIndex += 2;
                    continue;
                }
                if (word.charAt(curIndex) < 0x20 || word.charAt(curIndex) > 'z')
                {
                    curIndex++;
                    continue;
                }

                if (word.charAt(curIndex) >= 'A' && word.charAt(curIndex) <= 'Z')
                {
                    sb.append((char) (word.charAt(curIndex) + ' '));
                }
                else
                {
                    sb.append(word.charAt(curIndex));
                }
                curIndex++;
            }
        }
        else if (dictType == YY_UTF)
        {
            boolean bHaveChineseHead = false;
            String s1 = "。，、；：？！…—‘’“”～∶〔〕〈〉《》「」『』．〖〗【】（）［］｛｝";
            while (curIndex < endIndex)
            {
                if (word.charAt(curIndex) < 0x80) // 英文
                {
                    if (!bHaveChineseHead)
                    {
                        curIndex++;
                    }
                    else if (word.charAt(curIndex) >= 'A' && word.charAt(curIndex) <= 'Z')
                    {
                        sb.append((char) (word.charAt(curIndex) + 'a' - 'A'));
                        curIndex++;
                    }
                    else if (word.charAt(curIndex) >= 'a' && word.charAt(curIndex) <= 'z')
                    {
                        sb.append(word.charAt(curIndex));
                        curIndex++;
                    }
                    else
                    {
                        curIndex++;
                    }
                }
                else // 中文
                {
                    if (word.charAt(curIndex) >= 0x4E00 && word.charAt(curIndex) <= 0x9FA5)
                    {
                        bHaveChineseHead = true;
                        sb.append(word.charAt(curIndex));
                    }
                    else if (sb.length() != 0) // 符号，且关键字不为空
                    {
                        return sb.toString();
                    }
                    curIndex++;
                }
            }
        }

        return sb.toString();
    }

    private static int getDictTypeById(int id)
    {
        switch (id)
        {
            case ID_LWDICT:
            case ID_DMDICT:
            case ID_YHDICT:
            case ID_YYDICT:
            case ID_NEWWORD:
            case ID_SPEECHDICT:
                return YY_ENG;
            case ID_BHDICT:
            case ID_CYDICT:
            case ID_GDDICT:
            case ID_HYDICT:
            case ID_XDDICT:
            case ID_JMDICT:
            case ID_XSDICT:
            default:
                return YY_CHS;
        }
    }
}

