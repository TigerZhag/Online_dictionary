package com.readboy.library.io;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.readboy.library.provider.DictProviderManager;
import com.readboy.library.utils.ChineseCharFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * 代替DictFile.java的读写和搜索功能, DictFile主要用作搜索英文单词的发音
 */
public class DictIOFile {

    private static final String TAG = "DictIOFile";

    /**
     * 解码类型
     */
    public final static String DECODE_GB2312 = "GB2312";
    public static final String DECODE_UTF8 = "UTF-8";
    public static final String DECODE_UTF16 = "UTF-16";
    public static final String DECODE_UTF16LE = "UTF-16le";

    public static final int FILE_HEAD_SIZE = 32;
    /**
     * ID + addrNumber + readEnable
     */
    public static final int NEWWORD_RECORD_SIZE = 6;
    public static final int NEWWORD_TOTAL_COUNT = 1000;
    public static final int NEWWORD_TOTAL_SIZE = (NEWWORD_RECORD_SIZE * NEWWORD_TOTAL_COUNT);
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

    /**
     * 自己根据字典ID增加的一个变量
     */
    private int mDictTypeAdded;
	/*---- dict file param ----*/
    /**
     * 字典 ID
     */
    private int mDictId;
    /**
     * 字典类型
     */
    private int mDictType;
    /**
     * 单词的个数
     */
    private int mKeyCount;
    /**
     * 索引地址
     */
    private int mIndexStartAddr;
    /**
     * 加密方案
     */
    private int mEncryptSwitch;
    /**
     * 单词地址表的开始地址
     */
    private int mKeyStartAddr;
    /**
     * 解释地址表的开始地址
     */
    private int mExplainStartAddr;
    /**
     * 单词编码方式
     */
    private int mKeyCodingType;
    /**
     * 解释的编码方式
     */
    private int mExplainCodingType;
    /**
     * 解释的压缩方式
     */
    private int mExplainCompressType;
    /**
     * 单词编码方式
     */
    private String mKeyCodeType;

	/*---- 字典类型 -----*/
    /**
     * 0: English
     */
    public static final int YY_ENG = 0;
    /**
     * 1: Unicode(单词不采用此编码)
     */
    public static final int YY_UTF = 1;
    /**
     * 2: GB2312 (Chinese)
     */
    public static final int YY_CHS = 2;
    /**
     * 3: Arab
     */
    public static final int YY_ARB = 3;
    /**
     * 4: French
     */
    public static final int YY_FRE = 4;
    /**
     * 5: Japanese
     */
    public static final int YY_JPS = 5;
    /**
     * 6: Korea
     */
    public static final int YY_KRA = 6;
    /**
     * 7: Turkic
     */
    public static final int YY_TKC = 7;
    /**
     * 8: Vietnam
     */
    public static final int YY_VTN = 8;
    /**
     * 9: Indonesia
     */
    public static final int YY_IDS = 9;

    private static final int KEY_DATA_MAX = 255;
    private static final int EXPLAIN_DATA_MAX = 200 * 1024;

    /**
     * 文件对象
     */
    private RandomAccessFile mRandomAccessFile = null;

    /**
     *此返回值代表的是数据下载存放的位置。返回值类似"mnt/sdcard/.readboy/"
     * @return String
     */
    public static final String getSDcardDataPath(){
        String path = Environment.getExternalStorageDirectory().getPath();
        if (path.charAt(path.length() - 1) != File.separatorChar){
            path += File.separatorChar;
        }

        path += ".readboy/";
        return path;
    }

    private static final String INTERNAL_DIR = "/system"/*Environment.getExternalStorageDirectory().toString() */+ "/readboy";


    /**
     * 字典存放位置
     */
    public static final String[] DICT_LOCAL_NAME = {
            "dictionary/bihua.dct",
            "dictionary/chengyu.dct",
            "dictionary/dangdai.dct",
            "dictionary/dongman.dct",
            "dictionary/gdhy.dct",
            "dictionary/hanying.dct",
            "dictionary/hanyu.dct",
            "dictionary/jmyinghan.dct",
            "dictionary/xuesheng.dct",
            "dictionary/yinghan.dct",
            "dictionary/yingying.dct",
            "dictionary/wordsph.dat" };

   /* public static final String[] DICT_LOCAL_NAME = {
            "/mnt/sdcard/readboy/dictionary/bihua.dct",
            "/mnt/sdcard/readboy/dictionary/chengyu.dct",
            "/mnt/sdcard/readboy/dictionary/dangdai.dct",
            "/mnt/sdcard/readboy/dictionary/dongman.dct",
            "/mnt/sdcard/readboy/dictionary/gdhy.dct",
            "/mnt/sdcard/readboy/dictionary/hanying.dct",
            "/mnt/sdcard/readboy/dictionary/hanyu.dct",
            "/mnt/sdcard/readboy/dictionary/jmyinghan.dct",
            "/mnt/sdcard/readboy/dictionary/xuesheng.dct",
            "/mnt/sdcard/readboy/dictionary/yinghan.dct",
            "/mnt/sdcard/readboy/dictionary/yingying.dct",
            "/mnt/sdcard/readboy/dictionary/wordsph.dat" };

    *//*public static final String[] DICT_LOCAL_NAME = {
            "/mnt/sdcard2/readboy/dictionary/bihua.dct",
            "/mnt/sdcard2/readboy/dictionary/chengyu.dct",
            "/mnt/sdcard2/readboy/dictionary/dangdai.dct",
            "/mnt/sdcard2/readboy/dictionary/dongman.dct",
            "/mnt/sdcard2/readboy/dictionary/gdhy.dct",
            "/mnt/sdcard2/readboy/dictionary/hanying.dct",
            "/mnt/sdcard2/readboy/dictionary/hanyu.dct",
            "/mnt/sdcard2/readboy/dictionary/jmyinghan.dct",
            "/mnt/sdcard2/readboy/dictionary/xuesheng.dct",
            "/mnt/sdcard2/readboy/dictionary/yinghan.dct",
            "/mnt/sdcard2/readboy/dictionary/yingying.dct",
            "/mnt/sdcard2/readboy/dictionary/wordsph.dat" };
*/
    public static final String BIHUA_LOCAL_NAME = INTERNAL_DIR + "/dictionary/BiHuaDic.pin";

    private EnglishLettersInfo mEnglishLettersInfo = null;

    private Context context;

    /**
     * 提高英文词典下次查询速度，缓存词典一级字母位置
     * 注意: 程序退出时务必清除
     */
//    private static List<EnglishLettersInfo> dictFirstEnglishLetterList = null;

    /**
     * 是否英式词典
     */
    private boolean isEnglishDict = false;

    /**
     * 数据是否加密判断标志 注意：（返回值 & 0xF） !=0, 真为数据有加密，否则为数据无加密
     *
     * @return
     */
    public int getEncryptSwitch() {
        return mEncryptSwitch;
    }

    public int getDictID() {
        return mDictId;
    }

    public int getAllKeycount() {
        return mKeyCount;
    }

    /**
     * 打开文件
     *
     * @param dictID
     * @param mode
     * @return 成功返回true, 否则为false;
     */
    public boolean dictFileOpen(int dictID, String mode) {
        try {
            String path = getSDcardDataPath() + DICT_LOCAL_NAME[dictID];
            File file = new File(path);
            if (!file.exists()) {
                path = INTERNAL_DIR + File.separator + DICT_LOCAL_NAME[dictID];
            }
            mRandomAccessFile = new RandomAccessFile(path, mode);
            if (getDictTypeById(dictID) == YY_ENG) {
                /*if (dictFirstEnglishLetterList == null) {
                    dictFirstEnglishLetterList = new ArrayList<EnglishLettersInfo>();
                }*/
                dictFileInitial(dictID, true);
            } else {
                dictFileInitial(dictID, false);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean dictFileOpen(Context context, int dictID, String mode) {
        this.context = context;
        try {
            String path = getSDcardDataPath() + DICT_LOCAL_NAME[dictID];
            File file = new File(path);
            if (!file.exists()) {
                path = INTERNAL_DIR + File.separator + DICT_LOCAL_NAME[dictID];
            }
            mRandomAccessFile = new RandomAccessFile(path, mode);
            if (getDictTypeById(dictID) == YY_ENG) {
                /*if (dictFirstEnglishLetterList == null) {
                    dictFirstEnglishLetterList = new ArrayList<EnglishLettersInfo>();
                }*/
                dictFileInitial(dictID, true);
            } else {
                dictFileInitial(dictID, false);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 关闭文件
     *
     * @return 成功返回true, 否则为false;
     */
    public boolean dictFileClosed() {
        if (mRandomAccessFile != null) {
            try {
                mRandomAccessFile.close();
                mRandomAccessFile = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
//        if (englishLettersInfo != null) {
//            englishLettersInfo.lettersPosition = null;
//            englishLettersInfo = null;
//        }
        mEnglishLettersInfo = null;
        isEnglishDict = false;
        return true;
    }

    /**
     * 程序退出时，务必清除
     */
    /*public static void clearDictCache() {
        if (dictFirstEnglishLetterList != null) {
            for(EnglishLettersInfo englishLettersInfo:dictFirstEnglishLetterList) {
                englishLettersInfo.lettersPosition = null;
                englishLettersInfo = null;
            }
            dictFirstEnglishLetterList.clear();
            dictFirstEnglishLetterList = null;
        }
    }*/

    private int read(byte[] data, int count, int offset) {
        int ret = 0;
        try {
            if (mRandomAccessFile != null) {
                mRandomAccessFile.seek(offset);
                ret = mRandomAccessFile.read(data, 0, count);
            }
        } catch (IOException e) {
            Log.w(TAG, "read file error: " + e.getMessage());
        }

        return ret;
    }

    /**
     * 初始化词典文件，获取词典各个属性值
     *
     * @param dictId
     * @param isEnglishDict
     * @return
     */
    private boolean dictFileInitial(int dictId, boolean isEnglishDict) {
        byte buf[] = new byte[32];

        read(buf, buf.length, 0);

        if (byte2int(buf[0]) != 'c' || byte2int(buf[1]) != 'd') {
            return false;
        }

        int cycValueH16 = (int) (byte2int(buf[30]) << 0)
                + (int) (byte2int(buf[31]) << 8);
        int cycValueL16 = (int) (byte2int(buf[28]) << 0)
                + (int) (byte2int(buf[29]) << 8);
        if (dictFileGenerateCrc(buf, 28, cycValueH16, cycValueL16) == false) {
            return false;
        }

		/* init dict param */
        mDictId = dictId;
        mDictTypeAdded = getDictTypeById(mDictId);
        mDictType = byte2int(buf[2]);
        mKeyCount = (byte2int(buf[3]) << 0) + (byte2int(buf[4]) << 8)
                + (byte2int(buf[5]) << 16) + (byte2int(buf[6]) << 24);
        mIndexStartAddr = byte2int(buf[7]);
        mEncryptSwitch = byte2int(buf[8]);
        mKeyStartAddr = (byte2int(buf[9]) << 0) + (byte2int(buf[10]) << 8)
                + (byte2int(buf[11]) << 16) + (byte2int(buf[12]) << 24);
        mExplainStartAddr = (byte2int(buf[13]) << 0) + (byte2int(buf[14]) << 8)
                + (byte2int(buf[15]) << 16) + (byte2int(buf[16]) << 24);
        if (mDictId == ID_SPEECHDICT) {
            mKeyCodingType = 0;
            mExplainCodingType = 0;
        } else {
            mKeyCodingType = byte2int(buf[17]);
            mExplainCodingType = byte2int(buf[18]);
        }
        mExplainCompressType = byte2int(buf[19]);
        mKeyCodeType = getDataCodeType(mKeyCodingType);
        buf = null;

        /**
         * 一级目录:
         *    26个英文字母开始的位置
         */
        if (isEnglishDict) {
            this.isEnglishDict = isEnglishDict;
            /*for (EnglishLettersInfo englishLettersInfo:dictFirstEnglishLetterList) {
                if (englishLettersInfo.id == mDictId) {
                    if (englishLettersInfo.lettersPosition != null) {
                        mEnglishLettersInfo = englishLettersInfo;
                        return true;
                    }
                    dictFirstEnglishLetterList.remove(englishLettersInfo);
                }
            }*/
            mEnglishLettersInfo = initEnglishLettersInfo();
            mEnglishLettersInfo.id = mDictId;
//            dictFirstEnglishLetterList.add(mEnglishLettersInfo);
        }
        return true;
    }

    private EnglishLettersInfo initEnglishLettersInfo() {
        EnglishLettersInfo englishLettersInfo = new EnglishLettersInfo();
        englishLettersInfo.lettersPosition = new int[EnglishLettersInfo.ENGLISH_LETTERS
                .length()];

        byte[] catalogBuffer = DictProviderManager.getInstance().getDictOneLevelByID(context, mDictId);
        if (catalogBuffer != null) {
            String catalog[] = new String(catalogBuffer).split(" ");
            for (int i=0;i<englishLettersInfo.lettersPosition.length;i++) {
                englishLettersInfo.lettersPosition[i] = Integer.valueOf(catalog[i]);
            }
        } else {
            englishLettersInfo.lettersPosition = getLetterPositionFromFile();
        }

//        int s = englishLettersInfo.lettersPosition[EnglishLettersInfo.ENGLISH_LETTERS
//                .indexOf('s')];
//        englishLettersArray = null;
        return englishLettersInfo;
    }

    private int[] getLetterPositionFromFile() {
        int letterPositin[] = new int[EnglishLettersInfo.ENGLISH_LETTERS
                .length()];
        char[] englishLettersArray = EnglishLettersInfo.ENGLISH_LETTERS
                .toCharArray();
        int position = 0;
        String catalog = position+"";
        /**
         * a位置必定是0，所以从b开始
         */
        for (int i = 1; i < EnglishLettersInfo.ENGLISH_LETTERS.length(); i++) {
            /**
             * 二分法，算出26个字母的起始位置 在所有单词中，从后往前开始计算，匹配单词的第一个字母
             */
            position = getEnglishLetterFirstIndex(position, mKeyCount - 1,
                    englishLettersArray[i], 0);
            letterPositin[i] = position;
            catalog += " "+position;
        }
        byte[] catalogBuffer = catalog.getBytes();
        if (context != null) {
            DictProviderManager.getInstance().addDictOneLevel(context, getDictID(), catalogBuffer);
        }
        return letterPositin;
    }

    /**
     * 模糊匹配
     * @param str
     * @return
     */
    public int getKeyIndex(String str) {
        if (mDictTypeAdded == YY_ENG) {
            /**
             * 英文搜索
             */
//            return getEnglishKeyIndexByMatch(str);
            return getEnglishKeyIndex(str);
        }

        /**
         * 中文搜索
         */
        return getChineseKeyIndexByMatch(str);
    }

    /**
     * 精确匹配
     * @param str
     * @return
     */
    public int getKeyIndexAccurate(String str) {
        if (mDictTypeAdded == YY_ENG) {
            /**
             * 英文搜索
             */
//            return getEnglishKeyIndexByMatch(str);
            return getEnglishKeyIndexAccurate(str);
        }

        /**
         * 中文搜索
         */
        return getChineseKeyIndexByMatch(str);
    }

    public int[] getKeyRange(String str) {
        if (mDictTypeAdded == YY_ENG) {
            int[] range = getEnglishKeyIndexRange(str);
            if (range != null) {
                int[] array = new int[range[1]-range[0]+1];
                for (int i=0;i<array.length;i++) {
                    array[i] = range[0] + i;
                }
                return array;
            }
        } else {
            if (str != null) {
                str = str.trim();
                str = getFilterChineseWord(str);
                if (str.length() > 0) {
                    return getChineseKeyIndexRange(str);
                }
            }
        }
        return null;
        /*int temp[] = new int[2];
        temp[0] = getChineseKeyIndexByMatch(str);
        temp[1] = temp[0];
        return temp;*/
    }

    public int[] getEnglishKeyIndexRange(String searchStr) {
        int range[] = null;
        int index = -1;
        if (searchStr != null && mEnglishLettersInfo != null
                && mEnglishLettersInfo.lettersPosition != null) {
            searchStr = searchStr.trim();
            String lowerEnglishStr = getFilterEnglishLowerLetter(searchStr);
            /**
             * step1, step2两步是匹配要搜索字符串中的所有英文字母, 不包括特殊符号
             */
            if (lowerEnglishStr.length() > 0) {
                /**
                 * step1, 要搜索字符串的第一个字母的范围
                 */
                int englishLetterIndex = EnglishLettersInfo.ENGLISH_LETTERS
                        .indexOf(lowerEnglishStr.charAt(0));
                int startSearchIndex = mEnglishLettersInfo.lettersPosition[englishLetterIndex];
                int endSearchIndex = englishLetterIndex + 1;
                int length = EnglishLettersInfo.ENGLISH_LETTERS.length();
                if (length > endSearchIndex) {
                    endSearchIndex = mEnglishLettersInfo.lettersPosition[endSearchIndex] - 1;
                } else {
                    endSearchIndex = mKeyCount - 1;
                }
                /**
                 * step2, 从要搜索字符串的第二个字母开始匹配, 找到匹配到第几个字母的位置
                 */
                int startIndex = startSearchIndex;
                int endIndex = endSearchIndex;
                for (int k = 1; k < lowerEnglishStr.length(); k++) {
                    char c = lowerEnglishStr.charAt(k);
                    int lastPosition = getEnglishLetterFirstIndex(startIndex,
                            endIndex, c, k);
                    if (lastPosition == -1) {
                        break;
                    }
                    startIndex = lastPosition;
                    int nextPosition = getEnglishLetterLastIndex(startIndex,
                            endIndex, c, k);
                    if (nextPosition == -1) {
                        endIndex = endSearchIndex;
                    } else {
                        endIndex = nextPosition;
                    }
                    if (startIndex == endIndex) {
                        break;
                    }
                }
                /**
                 * step3, 从当前位置开始匹配完整的要搜索的字符串, 包括特殊符号
                 */
                index = startIndex;
                /**
                 * 注意: 词典每个字母开头单词没有规律(但两字母相同)
                 *      如果要搜索的单词前两个字母相同，将默认值设置为第一个
                 */
                if (lowerEnglishStr.length() > 1 && lowerEnglishStr.charAt(0) == lowerEnglishStr.charAt(1)) {
                    index = startSearchIndex;
                    if (getKeyStringByIndex(index).contentEquals(searchStr)) {
                        range = new int[3];
                        range[0] = index;
                        range[1] = index;
                        return range;
                    }
                    index = startIndex;
                }
                range = new int[3];
                range[0] = index;
                range[1] = endIndex;
                range[2] = startSearchIndex;
            }
        }
        return range;
    }

    /**
     * 模糊匹配
     * @param searchStr
     * @return
     */
    public int getEnglishKeyIndex(String searchStr) {
        int index = -1;
        int[] range = getEnglishKeyIndexRange(searchStr);
        if (range != null) {
            if (range[0] == range[1]) {
                return range[0];
            }
            /**
             * 优先级
             */
            int priority = -1;
            int level0 = 0;
            int level1 = 1;
            int level2 = 2;
            int level3 = 3;
            int level4 = 4;
            int level5 = 5;

            for (int z = range[0]; range[1] >= z; z++) {
                String tempStr = getKeyStringByIndex(z);
                if (tempStr.contentEquals(searchStr)) {
                    index = z;
                    priority = level5;
                    break;
                }
                if (tempStr.toLowerCase().contentEquals(searchStr.toLowerCase())) {
                    if (level4 > priority) {
                        index = z;
                        priority = level4;
                    }
                }
                tempStr = getFilterEnglishLowerLetterWithNumber(tempStr);
                String tempSearchStr = getFilterEnglishLowerLetterWithNumber(searchStr);
                if (tempStr.contentEquals(tempSearchStr)) {
                    if (level3 > priority) {
                        index = z;
                        priority = level3;
                    }
                }

                tempStr = getFilterEnglishLowerLetter(tempStr);
                tempSearchStr = getFilterEnglishLowerLetter(tempSearchStr);
                if (tempStr.contentEquals(tempSearchStr)) {
                    if (level2 > priority) {
                        index = z;
                        priority = level2;
                    }
                } else if (tempStr.contains(tempSearchStr)) {
                    if (level1 > priority) {
                        index = z;
                        priority = level1;
                    }
                } else if (tempSearchStr.contains(tempStr)) {
                    if (level0 > priority) {
                        index = z;
                        priority = level0;
                    }
                }

                if (z != range[2] && tempSearchStr.length() != tempStr.length()) {
                    if (index == -1) {
                        index = range[0];
                    }
                    break;
                }

                if (index == -1 && range[1] == z) {
                    index = range[0];
                }
            }
        }
        return index;
    }

    /**
     * 精确匹配
     * @param searchStr
     * @return
     */
    public int getEnglishKeyIndexAccurate(String searchStr) {
        int index = -1;
        int[] range = getEnglishKeyIndexRange(searchStr);
        if (range != null) {
            if (range[0] == range[1]) {
                return range[0];
            }
            /**
             * 优先级
             */
            int priority = -1;
            int level0 = 0;
            int level1 = 1;
            int level2 = 2;
            int level3 = 3;
            int level4 = 4;
            int level5 = 5;

            for (int z = range[0]; range[1] >= z; z++) {
                String tempStr = getKeyStringByIndex(z);
                if (tempStr.contentEquals(searchStr)) {
                    index = z;
                    priority = level5;
                    break;
                }
                if (tempStr.toLowerCase().contentEquals(searchStr.toLowerCase())) {
                    if (level4 > priority) {
                        index = z;
                        priority = level4;
                    }
                }
                tempStr = getFilterEnglishLowerLetterWithNumber(tempStr);
                String tempSearchStr = getFilterEnglishLowerLetterWithNumber(searchStr);
                if (tempStr.contentEquals(tempSearchStr)) {
                    if (level3 > priority) {
                        index = z;
                        priority = level3;
                    }
                }
                if (z != range[2] && tempSearchStr.length() != tempStr.length()) {
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 查找英文
     *
     * @param searchStr
     *            输入的字符串
     * @return 返回匹配的单词位置
     */
    public int getEnglishKeyIndexByMatch(String searchStr) {
        int index = -1;
        if (searchStr != null && mEnglishLettersInfo != null
                && mEnglishLettersInfo.lettersPosition != null) {
            searchStr = searchStr.trim();
            String lowerEnglishStr = getFilterEnglishLowerLetter(searchStr);
            /**
             * step1, step2两步是匹配要搜索字符串中的所有英文字母, 不包括特殊符号
             */
            if (lowerEnglishStr.length() > 0) {
                /**
                 * step1, 要搜索字符串的第一个字母的范围
                 */
                int englishLetterIndex = EnglishLettersInfo.ENGLISH_LETTERS
                        .indexOf(lowerEnglishStr.charAt(0));
                int startSearchIndex = mEnglishLettersInfo.lettersPosition[englishLetterIndex];
                int endSearchIndex = englishLetterIndex + 1;
                int length = EnglishLettersInfo.ENGLISH_LETTERS.length();
                if (length > endSearchIndex) {
                    endSearchIndex = mEnglishLettersInfo.lettersPosition[endSearchIndex] - 1;
                } else {
                    endSearchIndex = mKeyCount - 1;
                }
                /**
                 * step2, 从要搜索字符串的第二个字母开始匹配, 找到匹配到第几个字母的位置
                 */
                int startIndex = startSearchIndex;
                int endIndex = endSearchIndex;
                for (int k = 1; k < lowerEnglishStr.length(); k++) {
                    char c = lowerEnglishStr.charAt(k);
                    int lastPosition = getEnglishLetterFirstIndex(startIndex,
                            endIndex, c, k);
                    if (lastPosition == -1) {
                        break;
                    }
                    startIndex = lastPosition;
                    int nextPosition = getEnglishLetterLastIndex(startIndex,
                            endIndex, c, k);
                    if (nextPosition == -1) {
                        endIndex = endSearchIndex;
                    } else {
                        endIndex = nextPosition;
                    }
                    if (startIndex == endIndex) {
                        break;
                    }
                }
                /**
                 * step3, 从当前位置开始匹配完整的要搜索的字符串, 包括特殊符号
                 */
                index = startIndex;
                /**
                 * 注意: 词典每个字母开头单词没有规律(但两字母相同)
                 *      如果要搜索的单词前两个字母相同，将默认值设置为第一个
                 */
                if (lowerEnglishStr.length() > 1 && lowerEnglishStr.charAt(0) == lowerEnglishStr.charAt(1)) {
                    index = startSearchIndex;
                    if (getKeyStringByIndex(index).contentEquals(searchStr)) {
                        return index;
                    }
                    index = startIndex;
                }

                /**
                 * 优先级
                 */
                int priority = -1;
                int level0 = 0;
                int level1 = 1;
                int level2 = 2;
                int level3 = 3;
                int level4 = 4;
                int level5 = 5;

                for (int z = startIndex; endIndex >= z; z++) {
                    String tempStr = getKeyStringByIndex(z);
                    if (tempStr.contentEquals(searchStr)) {
                        index = z;
                        priority = level5;
                        break;
                    }
                    if (tempStr.toLowerCase().contentEquals(searchStr.toLowerCase())) {
                        if (level4 > priority) {
                            index = z;
                            priority = level4;
                        }
                    }
                    tempStr = getFilterEnglishLowerLetterWithNumber(tempStr);
                    String tempSearchStr = getFilterEnglishLowerLetterWithNumber(searchStr);
                    if (tempStr.contentEquals(tempSearchStr)) {
                        if (level3 > priority) {
                            index = z;
                            priority = level3;
                        }
                    }
                    /*if (Character
                            .isDigit(searchStr.charAt(searchStr.length() - 1))) {
                        if (tempStr.contains(searchStr)) {
                            if (level2 > priority) {
                                index = z;
                                priority = level2;
                            }
                        }
                    } else {
                        //去除搜索到单词末尾数字
                        if (Character
                                .isDigit(tempStr.charAt(tempStr.length() - 1))) {
                            tempStr = tempStr
                                    .substring(0, tempStr.length() - 1);
                            if (searchStr.contentEquals(tempStr)) {
                                if (level1 > priority) {
                                    index = z;
                                    priority = level1;
                                }
                            }
                        }
                    }*/

                    tempStr = getFilterEnglishLowerLetter(tempStr);
                    tempSearchStr = getFilterEnglishLowerLetter(tempSearchStr);
                    if (tempStr.contentEquals(tempSearchStr)) {
                        if (level2 > priority) {
                            index = z;
                            priority = level2;
                        }
                    } else if (tempStr.contains(tempSearchStr)) {
                        if (level1 > priority) {
                            index = z;
                            priority = level1;
                        }
                    } else if (tempSearchStr.contains(tempStr)) {
                        if (level0 > priority) {
                            index = z;
                            priority = level0;
                        }
                    }

                    /*int tempStrLength = getFilterEnglishLowerLetter(tempStr).length();
                    int searchStrLength = getFilterEnglishLowerLetter(searchStr).length();*/
                    if (z != startSearchIndex && tempSearchStr.length() != tempStr.length()) {
                        break;
                    }
                }
//                return index;
            }
        }

        return index;
    }

    /**
     * 查找合适单词位置
     *
     * @param start
     * @param end
     * @param c
     *            要匹配的字母
     * @param index
     *            匹配第几个英文字母(忽略字母前的符号)
     * @return
     *            返回位置，是第一个能匹配的单词
     */
    private int getEnglishLetterFirstIndex(int start, int end, char c, int index) {
        int result = -1;
        int startPosition = start;
        int endPosition = end;
        int lastPostion = endPosition;
        while (true) {
            // String key = getKeyStringByIndex(endPosition).replace("-",
            // "").replace("'", "");
            String s1 = getKeyStringByIndex(endPosition);
            int i1 = getIndexEnglishChar(s1, index);// 位置
            if (i1 == -1) {
                startPosition = endPosition;
                endPosition = lastPostion;
                endPosition = (startPosition + endPosition) / 2;
                if (startPosition == endPosition) {
                    return result;
                }
                continue;
            }
            int r1 = charEqualsKeyChar(c, s1, i1); // 匹配结果
            if (-1 == r1) {
                lastPostion = endPosition;
                endPosition = (startPosition + endPosition) / 2;
                if (startPosition == endPosition) {
                    return result;
                }
            } else if (0 == r1) {
                if (endPosition == 0) {
                    return 0;
                }
                result = endPosition;
                String s2 = getKeyStringByIndex(endPosition - 1);
                int i2 = getIndexEnglishChar(s2, index);
                if (i2 == -1) {
                    return endPosition;
                }
                int r2 = charEqualsKeyChar(c, s2, i2);
                if (-1 == r2) {
                    return endPosition;
                } else if (0 == r2) {
                    result = endPosition - 1;
                    lastPostion = endPosition;
                    endPosition = (startPosition + endPosition) / 2;
                    if (startPosition == endPosition) {
                        return startPosition;
                    }
                } else {
                    return endPosition;
                }
            } else {
                startPosition = endPosition;
                endPosition = lastPostion;
                endPosition = (startPosition + endPosition) / 2;
                if (startPosition == endPosition) {
                    return result;
                }
            }
        }
    }

    /**
     * 查找合适单词位置
     *
     * @param start
     * @param end
     * @param c
     *            要匹配的字母
     * @param index
     *            匹配第几个英文字母(忽略字母前的符号)
     * @return
     *            返回位置，是最后一个能匹配的单词
     */
    private int getEnglishLetterLastIndex(int start, int end, char c, int index) {
        int result = -1;
        int startPosition = start;
        int endPosition = end;
        int lastPostion = endPosition;
        while (true) {
            // String key = getKeyStringByIndex(endPosition).replace("-",
            // "").replace("'", "");
            String s1 = getKeyStringByIndex(endPosition);
            int i1 = getIndexEnglishChar(s1, index);// 位置
            if (i1 == -1) {
                if (startPosition == endPosition) {
                    return result;
                }
                startPosition = endPosition;
                endPosition = lastPostion;
                endPosition = (startPosition + endPosition) / 2;
                continue;
            }
            int r1 = charEqualsKeyChar(c, s1, i1); // 匹配结果
            if (-1 == r1) {
                if (startPosition == endPosition) {
                    return result;
                }
                lastPostion = endPosition;
                endPosition = (startPosition + endPosition) / 2;
            } else if (0 == r1) {
                if (endPosition == mKeyCount) {
                    return mKeyCount - 1;
                }
                result = endPosition;
                String s2 = getKeyStringByIndex(endPosition + 1);
                int i2 = getIndexEnglishChar(s2, index);
                if (i2 == -1) {
                    return endPosition;
                }
                int r2 = charEqualsKeyChar(c, s2, i2);
                if (-1 == r2) {
                    return endPosition;
                } else if (0 == r2) {
                    result = endPosition;
                    // lastPostion = endPosition;
                    startPosition = endPosition;
                    endPosition = lastPostion;
                    endPosition = (startPosition + endPosition) / 2;
                    if (startPosition == endPosition) {
                        return startPosition;
                    }
                } else {
                    return endPosition;
                }
            } else {
                if (startPosition == endPosition) {
                    return result;
                }
                startPosition = endPosition;
                endPosition = lastPostion;
                endPosition = (startPosition + endPosition) / 2;
            }
        }
    }

    /**
     * 字符与字符串首字母比较
     *
     * @param key
     * @param c
     * @return 默认是0，0是相等，-1为小于，1为大于,
     */
    private int charEqualsKeyChar(char c, String key, int start) {
        if (key != null && key.length() > start) {
            key = key.toLowerCase().trim();
            char firstKeyChar = key.charAt(start);
            if (firstKeyChar == c) {
                return 0;
            } else if (firstKeyChar > c) {
                return -1;
            } else if (c > firstKeyChar) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 模糊获取字符中第几个英文字母
     *
     * @param str
     * @param index
     * @return 返回都是小写字母，默认为0
     */
    private int getIndexEnglishChar(String str, int index) {
        int count = 0;
        if (str != null && str.length() > 0) {
            for (int i = 0; i < str.length(); i++) {
                char lc = getLowerEnglishChar(str.charAt(i));
                if (lc != ' ') {
                    if (count == index) {
                        return i;
                    }
                    count++;
                }
            }
        }
        return -1;
    }

    /**
     * 过滤得到英文小写字母
     *
     * @param str
     * @return
     */
    private String getFilterEnglishLowerLetter(String str) {
        String result = "";
        if (str != null) {
            for (char c : str.toCharArray()) {
                char rc = getLowerEnglishChar(c);
                if (rc != ' ') {
                    result += rc;
                }
            }
        }
        return result;
    }

    /**
     * 过滤得到英文小写字母或阿拉伯数字
     *
     * @param str
     * @return
     */
    private String getFilterEnglishLowerLetterWithNumber(String str) {
        String result = "";
        if (str != null) {
            for (char c : str.toCharArray()) {
                char rc = getLowerEnglishCharOrNumber(c);
                if (rc != ' ') {
                    result += rc;
                }
            }
        }
        return result;
    }

    /**
     * 判断是否英文小写字母
     *
     * @param c
     * @return 有则返回小写，无则为空
     */
    private char getLowerEnglishChar(char c) {
        if (c >= 'a' && 'z' >= c) {
            return c;
        } else if (c >= 'A' && 'Z' >= c) {
            c += 32;// 转为小写字母
            return c;
        }
        return ' ';
    }

    /**
     * 判断是否英文小写字母或阿拉伯数字
     *
     * @param c
     * @return
     */
    private char getLowerEnglishCharOrNumber(char c) {
        if (c >= 'a' && 'z' >= c) {
            return c;
        } else if (c >= 'A' && 'Z' >= c) {
            c += 32;// 转为小写字母
            return c;
        } else if (Character.isDigit(c)) {
            return c;
        }
        return ' ';
    }

    public int[] getChineseKeyIndexRange(String searchStr) {
        int[] range = null;
        try {
            int startIndex = 0;
            int endIndex = mKeyCount - 1;
            byte[] srcWorldBytes = searchStr.getBytes(getDataCodeType(mKeyCodingType));
            byte[] firstWorldBytes = getBytesOfChineseKeyIndex(startIndex);
            byte[] endWorldBytes = getBytesOfChineseKeyIndex(endIndex);
            if (srcWorldBytes != null && firstWorldBytes != null && endWorldBytes != null) {
                if (endWorldBytes[0] == srcWorldBytes[0]) {
                    startIndex = getChineseFirstByteIndex(startIndex, endIndex, 0, srcWorldBytes[0]);
                } else if (firstWorldBytes[0] == srcWorldBytes[0]) {
                    endIndex = getChineseLastByteIndex(startIndex, endIndex, 0, srcWorldBytes[0]);
                } else if (endWorldBytes[0] > srcWorldBytes[0] && srcWorldBytes[0] > firstWorldBytes[0]) {
                    startIndex = getChineseFirstByteIndex(startIndex, endIndex, 0, srcWorldBytes[0]);
                    endIndex = getChineseLastByteIndex(startIndex, endIndex, 0, srcWorldBytes[0]);
                }
                for (int i=1;i<srcWorldBytes.length;i++) {
                    startIndex = getChineseFirstByteIndex(startIndex, endIndex, i, srcWorldBytes[i]);
                    if (startIndex != -1) {
                        endIndex = getChineseLastByteIndex(startIndex, endIndex, i, srcWorldBytes[i]);
                    }
                    if (startIndex == -1 || endIndex == -1) {
                        startIndex = -1;
                        endIndex = -1;
                        break;
                    } else if (startIndex == endIndex) {
                        break;
                    }
                }

                if (startIndex != -1 && endIndex != -1) {
                    range = new int[endIndex-startIndex+1];
                    for (int i=0;i<range.length;i++) {
                        range[i] = getAddrNumberByIndex(startIndex+i) & 0x00ffffff;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return range;
    }

    public byte[] getBytesOfChineseKeyIndex(int index) {
        int addrNumber = getAddrNumberByIndex(index);
        byte[] data = dictFileGetDictKeyByteByAddressNumber(addrNumber);
        return data;
    }

    public int getChineseFirstByteIndex(int startIndex, int endIndex, int byteIndex, int matchByte) {
        int start = startIndex;
        int end = endIndex;
        int currentIndex = (start+end)/2;
        while (true) {
            byte[] currentBytes = getBytesOfChineseKeyIndex(currentIndex);
            if (currentBytes != null) {
                if (currentBytes.length > byteIndex) {
                    if (currentBytes[byteIndex] < matchByte) {
                        byte[] nextBytes = getBytesOfChineseKeyIndex(currentIndex+1);
                        if (nextBytes != null) {
                            if (nextBytes.length > byteIndex) {
                                if (nextBytes[byteIndex] == matchByte) {
                                    return currentIndex+1;
                                }
                            } else {
                                return -1;
                            }
                        }
                        start = currentIndex;
                    } else if (currentBytes[byteIndex] == matchByte) {
                        byte[] lastBytes = getBytesOfChineseKeyIndex(currentIndex-1);
                        if (lastBytes != null && (byteIndex >= lastBytes.length
                                || lastBytes[byteIndex] < matchByte)) {
                            return currentIndex;
                        }
                        if (currentIndex == startIndex || currentIndex-1 == startIndex) {
                            return startIndex;
                        }
                        end = currentIndex;
                    } else if (currentBytes[byteIndex] > matchByte) {
                        end = currentIndex;
                    }
                } else {
                    start = currentIndex;
                }
            }
            int tempIndex = (start+end)/2;
            if (tempIndex == currentIndex) {
                return -1;
            }
            currentIndex = tempIndex;
        }
    }

    public int getChineseLastByteIndex(int startIndex, int endIndex, int byteIndex, int matchByte) {
        int start = startIndex;
        int end = endIndex;
        int currentIndex = (start+end)/2;
        while (true) {
            byte[] currentBytes = getBytesOfChineseKeyIndex(currentIndex);
            if (currentBytes != null) {
                if (currentBytes.length > byteIndex) {
                    if (currentBytes[byteIndex] < matchByte) {
                        start = currentIndex;
                    } else if (currentBytes[byteIndex] == matchByte) {
                        byte[] nextBytes = getBytesOfChineseKeyIndex(currentIndex+1);
                        if (nextBytes != null && (byteIndex >= nextBytes.length
                                || nextBytes[byteIndex] > matchByte)) {
                            return currentIndex;
                        }
                        if (currentIndex == endIndex || currentIndex+1 == endIndex) {
                            return endIndex;
                        }
                        start = currentIndex;
                    } else if (currentBytes[byteIndex] > matchByte) {
                        byte[] lastBytes = getBytesOfChineseKeyIndex(currentIndex-1);
                        if (lastBytes != null) {
                            if (lastBytes.length > byteIndex) {
                                if (lastBytes[byteIndex] == matchByte) {
                                    return currentIndex-1;
                                }
                            } else {
                                return -1;
                            }

                        }
                        end = currentIndex;
                    }
                } else {
                    start = currentIndex;
                }
            }
            int tempIndex = (start+end)/2;
            if (tempIndex == currentIndex) {
                return -1;
            }
            currentIndex = tempIndex;
        }
    }

    /**
     * 查找最佳匹配字符串的中文位置
     * @param searchStr
     * @return
     */
    public int getChineseKeyIndexByMatch(String searchStr) {
        if (mDictId == ID_BHDICT) {
            return getChineseKeyIndexByMatch(searchStr, true);
        }
        return getChineseKeyIndexByMatch(searchStr, false);
    }

    /**
     * 查找中文
     *
     * @param searchStr
     * @param isMatchFirstWord
     *            只匹配第一个字
     * @return
     */
    public int getChineseKeyIndexByMatch(String searchStr,
                                         boolean isMatchFirstWord) {
        if (searchStr != null) {
            searchStr = searchStr.trim();
            searchStr = getFilterChineseWord(searchStr);
            if (searchStr.length() > 0) {
                if (isMatchFirstWord) {
                    searchStr = searchStr.charAt(0) + "";
                }
                int index = dictFileGetDictDataWithCMP(searchStr);
                if (index != -1) {
                    return index;
                }
                return index;
            }
        }
        return -1;
    }

    public int dictFileGetDictDataWithCMP(String comparaData) {
        int top, bottom;
        int cmpValue = 0, curIndex = 0;
        int addrNumber = -1;

        if(comparaData == null) {
            return -1;
        }
		/* 单词 整形 */
        String srcData = comparaData;
        srcData = keyPlastic(srcData);
        if(srcData.length()== 0) {
            return -1;
        }
        String scrData;
        byte[] srcByteData;
        try {
            srcByteData = srcData.getBytes(getDataCodeType(mKeyCodingType));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return -1;
        }
        top = 0;
        bottom = mKeyCount;
		/* 二分法查找 */
        scrData = dictFileGetDictKeyByIndex(3598);
        while(top <= bottom && (top + bottom)/2 < mKeyCount) {
            curIndex = (top + bottom)/2;
            scrData = dictFileGetDictKeyByIndex(curIndex);
            if(scrData == null) {
                return -1;
            }
			/* 单词 整形 */
            scrData = keyPlastic(scrData);
            cmpValue = keyCompare(srcByteData, scrData);
            if(cmpValue == 0) {
                break;
            } else if(cmpValue > 0) {
                top = curIndex + 1;
            } else {
                bottom = curIndex - 1;
            }
        }

        scrData = dictFileGetDictKeyByIndex(curIndex);
        if(scrData == null) {
            return -1;
        }
		/* 单词 整形 */
        scrData = keyPlastic(scrData);
        if(keyCompare(srcByteData, scrData) > 0) {
            if(mKeyCount > curIndex + 1) {
                curIndex++;
            }
        }

        //习惯处理一：相同单词查到首个
        if(cmpValue == 0) {
            addrNumber = getAddrNumberByIndex(curIndex);
            while(cmpValue == 0) {
                if(addrNumber == 0) {
                    return addrNumber;
                }
                scrData = getKeyStringByIndex(--addrNumber);
                if(scrData == null) {
                    break;
                }
                scrData = keyPlastic(scrData);
                cmpValue = keyCompare(srcByteData, scrData);
            }
            addrNumber++;
            return addrNumber;
        }

        //习惯处理二：查不到的单词找到最接近的那一个
        int curCount, tmpCount;
        scrData = dictFileGetDictKeyByIndex(curIndex);
        if(scrData == null) {
            return -1;
        }
        scrData = keyPlastic(scrData);
        curCount = getSimilarity(srcData, scrData);
        if(curIndex+1 < mKeyCount) {
            scrData = dictFileGetDictKeyByIndex(curIndex + 1);
            if(scrData == null) {
                return -1;
            }
            scrData = keyPlastic(scrData);
            cmpValue = keyCompare(srcByteData, scrData);
            tmpCount = getSimilarity(srcData, scrData);
            if(tmpCount > curCount) {
                curCount = tmpCount;
                curIndex++;
            } else if(tmpCount==curCount && cmpValue>0) {
                curCount = tmpCount;
                curIndex++;
            }
        }
        if(curIndex > 0) {
            scrData = dictFileGetDictKeyByIndex(curIndex - 1);
            if(scrData == null) {
                return -1;
            }
            scrData = keyPlastic(scrData);
            cmpValue = keyCompare(srcByteData, scrData);
            tmpCount = getSimilarity(srcData, scrData);
            if(tmpCount > curCount) {
                curCount = tmpCount;
                curIndex--;
            } else if(tmpCount==curCount && cmpValue<0) {
                curCount = tmpCount;
                curIndex--;
            }
        }
        if(curIndex >= mKeyCount) {
            curIndex--;
        }

        addrNumber = getAddrNumberByIndex(curIndex);
        // 中文长度相等的算完全匹配
        if(mKeyCodingType==YY_CHS && curCount==comparaData.length()) {
            return addrNumber & 0x00ffffff;
        }
        if(mKeyCodingType==YY_ENG || curCount>= 1 && mKeyCodingType==YY_CHS) {
            return (addrNumber|0xff000000) & 0x00ffffff;
        }

        return -1;//addrNumber|0xff000000;
    }

    public String keyPlastic(String data) {
        int curIndex, endIndex;
        StringBuffer sb = new StringBuffer();
        for(curIndex = 0; curIndex < data.length(); curIndex++) {
            if(data.charAt(curIndex) != ' ') {
                break;
            }
        }
        endIndex = 0;
        while(endIndex < data.length() && data.charAt(endIndex) != 0)
        {
            endIndex++;
        }

        if(mDictTypeAdded == YY_CHS)
        {
            boolean bHaveChineseHead = false;
            while(curIndex < endIndex)
            {
                if(data.charAt(curIndex) < 0x80) {
                    if(bHaveChineseHead == false) {
                        curIndex++;
                        continue;
                    } else if(data.charAt(curIndex) ==0x0d||data.charAt(curIndex) ==0x0a) {
                        return sb.toString();
                    }else if(data.charAt(curIndex) >= 'A' && data.charAt(curIndex) <= 'Z') {
                        sb.append((char)(data.charAt(curIndex) + 'a' - 'A'));
                        curIndex++;
                        continue;
                    } else if(data.charAt(curIndex) >= 'a' && data.charAt(curIndex) <= 'z') {
                        sb.append((char)data.charAt(curIndex));
                        curIndex++;
                        continue;
                    } else {
                        curIndex++;
                        continue;
                    }
                } else {
	            	/* 中文 */
                    if(data.charAt(curIndex) >= 0x4E00 && data.charAt(curIndex) <= 0x9FA5
                            || data.charAt(curIndex) < 0x1000	// 汉语拼音
                            || data.charAt(curIndex) == '…') {
                        bHaveChineseHead = true;
                        sb.append(data.charAt(curIndex));
                    } else if(sb.length() != 0){	// 符号，且关键字不为空
                        return sb.toString();
                    } else{
                        // 符号，且在开始位置处，跳过
                    }
                    curIndex++;
                }
            }
        }

        return sb.toString();
    }

    private int keyCompare(byte[] srcByte, String scr) {
        int ret = 0;

        byte[] scrByte = null;
        try {
            scrByte = scr.getBytes(getDataCodeType(mKeyCodingType));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return ret;
        }
        for(int i = 0; i < srcByte.length && i < scrByte.length; i++) {
            if(byte2int(srcByte[i]) > byte2int(scrByte[i])) {
                ret = 1;
                break;
            } else if(byte2int(srcByte[i]) < byte2int(scrByte[i])) {
                ret = -1;
                break;
            }
        }

        if(ret == 0 && srcByte.length > scrByte.length) {
            ret = 1;
        } else if(ret == 0 && srcByte.length < scrByte.length) {
            ret = -1;
        }

        return ret;
    }

    private int getSimilarity(String src, String scr) {
        int count = 0;

        for(int i = 0; i < src.length() && i < scr.length(); i++) {
            if(src.charAt(i) != scr.charAt(i)) {
                break;
            }
            count++;
        }

        return count;
    }

    public String dictFileGetDictKeyByIndex(int index) {
        byte[] data = null;
        if(index < 0) {
            return null;
        }
        int addrNumber = getAddrNumberByIndex(index);
        data = dictFileGetDictKeyByteByAddressNumber(addrNumber);
        String dataText = null;
        if(data != null) {
            try {
                dataText = new String(data, getDataCodeType(mKeyCodingType));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.w(TAG, "******************** load dict key end address failed! *************************");
        }

        return dataText;
    }

    public byte[] dictFileGetDictKeyByteByAddressNumber(int addrNumber) {
        byte[] data = null;
        if(addrNumber < 0) {
            return null;
        }
        int indexAddr = mKeyStartAddr + addrNumber * 4;
        byte[] indexData = new byte[4];
        // 单词地址的位置
        if(getDictData(indexAddr, indexData) != 4) {
            Log.w(TAG, "******************** load dict key start address failed! *************************");
        }
        // 当前单词的位置
        int startKeyAddr = (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
        indexAddr = mKeyStartAddr + (addrNumber + 1) * 4;
        if(getDictData(indexAddr, indexData) != 4) {
            Log.w(TAG, "******************** load dict key end address failed! *************************");
        }
        // 下一单词的位置
        int endKeyAddr =  (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
        // 单词内容
        int length = endKeyAddr - startKeyAddr-1;
        if(length > 0) {
            length = length > KEY_DATA_MAX ? KEY_DATA_MAX :length;
            data = new byte[length];
            if( (mEncryptSwitch & 0xF0) != 0) {
                if(getEncryptDictData(startKeyAddr, data) != length) {
                    Log.w(TAG, "******************** load dict key data failed! *************************");
                }
            } else {
                if(getDictData(startKeyAddr, data) != length) {
                    Log.w(TAG, "******************** load dict key data failed! *************************");
                }
            }
        }

        return data;
    }

    public int getAddrNumberByIndex(int index) {
        if(mIndexStartAddr == 0) {
            return index;	//mIndexStartAddr
        } else {
            int addrNumber;
            byte[] data = new byte[4];
            read( data, data.length, mIndexStartAddr + index*4);
            addrNumber = (byte2int(data[0])) + (byte2int(data[1]) << 8)
                    + (byte2int(data[2]) << 16) + (byte2int(data[3]) << 24);
            return addrNumber;
        }
    }

    /**
     * 过滤得到中文
     * @param str
     * @return
     */
    public static String getFilterChineseWord(String str) {
        String result = "";
        for (char c : str.toCharArray()) {
            if (ChineseCharFilter.isChinese(c)
                    && !ChineseCharFilter.isChineseSymbol(c + "")) {
                result += c;
            }
        }
        return result;
    }

    /**
     * 生词或单词的解释内容
     *
     * @param index
     * @return
     */
    public byte[] loadCurExplain(int index) {
        byte[] explainData = null;

        byte[] indexData = new byte[4];
        int indexAddr = mExplainStartAddr + index * 4;

        // 解释地址的位置
        if (getDictData(indexAddr, indexData) != 4) {
            Log.w(TAG,
                    "******************** load dict explain start address failed! *************************");
        }
        // 当前解释的位置
        int startExplain = (byte2int(indexData[0]))
                + (byte2int(indexData[1]) << 8)
                + (byte2int(indexData[2]) << 16)
                + (byte2int(indexData[3]) << 24);
        // 下一解释地址的位置
        indexAddr = mExplainStartAddr + (index + 1) * 4;
        if (getDictData(indexAddr, indexData) != 4) {
            Log.w(TAG,
                    "******************** load dict explain end address failed! *************************");
        }
        // 下一解释的位置
        int endExplain = (byte2int(indexData[0]))
                + (byte2int(indexData[1]) << 8)
                + (byte2int(indexData[2]) << 16)
                + (byte2int(indexData[3]) << 24);
        explainData = new byte[endExplain - startExplain - 1]; // 解释内容
        if ((mEncryptSwitch & 0xF) != 0) { // 解释有加密
            if (getEncryptDictData(startExplain, explainData) != endExplain
                    - startExplain - 1) {
                Log.w(TAG,
                        "******************** load dict explain data failed! *************************");
            }
        } else { // 解释无加密
            if (getDictData(startExplain, explainData) != endExplain
                    - startExplain - 1) {
                Log.w(TAG,
                        "******************** load dict explain data failed! *************************");
            }
        }

        return explainData;
    }

    /**
     * 定位解析无加密地址的起始位置
     *
     * @param startAddr
     * @param data
     * @return
     */
    public int getDictData(int startAddr, byte[] data) {
        if (data == null) {
            return -1;
        }
        int ret;
        ret = read(data, data.length, startAddr);
        return ret;
    }

    /**
     * 定位解析有加密地址的起始位置
     *
     * @param startAddr
     * @param data
     * @return
     */
    public int getEncryptDictData(int startAddr, byte[] data) {
        if (data == null) {
            return -1;
        }
        int ret = getDictData(startAddr, data);
        int[] chaos = { 0xF4, 0xB5, 0x13, 0x02, 0x3D, 0xCA, 0x49, 0xAE, 0x15,
                0xCC, 0x41 };
        int[] sample = { 'F', 'o', 'r', 'a', 'i', 'n', 'd', 'y', '~', 'S', 'a',
                'r', 'a', 'h', ':' };
        int chaosI, sampleI;
        for (int i = 0; i < ret; i++) {
            chaosI = chaos[i % (chaos.length)];
            sampleI = sample[i % (sample.length)];
            data[i] = (byte) (byte2int(data[i]) ^ chaosI ^ sampleI);
        }
        return ret;
    }

    /**
     * 根据索引，获取生词或单词的字符串类型
     *
     * @param index
     * @return
     */
    public String getKeyStringByIndex(int index) {
        byte[] data = getKeyByteByIndex(index);
        String dataText = null;
        if (data != null) {
            try {
                dataText = new String(data, getDataCodeType(2));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Log.w(TAG,
                    "******************** load dict key end address failed! *************************");
        }

        return dataText;
    }

    /**
     * 根据索引，获取生词或单词的字节类型
     *
     * @param addrNumber
     * @return
     */
    public byte[] getKeyByteByIndex(int addrNumber) {
        byte[] data = null;
        if (addrNumber < 0) {
            return null;
        }
        int indexAddr = mKeyStartAddr + addrNumber * 4;
        byte[] indexData = new byte[4];
        // 单词地址的位置
        if (getDictData(indexAddr, indexData) != 4) {
            Log.w(TAG,
                    "******************** load dict key start address failed! *************************");
        }
        // 当前单词的位置
        int startKeyAddr = (byte2int(indexData[0]))
                + (byte2int(indexData[1]) << 8)
                + (byte2int(indexData[2]) << 16)
                + (byte2int(indexData[3]) << 24);
        indexAddr = mKeyStartAddr + (addrNumber + 1) * 4;
        if (getDictData(indexAddr, indexData) != 4) {
            Log.w(TAG,
                    "******************** load dict key end address failed! *************************");
        }
        // 下一单词的位置
        int endKeyAddr = (byte2int(indexData[0]))
                + (byte2int(indexData[1]) << 8)
                + (byte2int(indexData[2]) << 16)
                + (byte2int(indexData[3]) << 24);
        // 单词内容
        int length = endKeyAddr - startKeyAddr - 1;
        if (length > 0) {
            length = length > KEY_DATA_MAX ? KEY_DATA_MAX : length;
            data = new byte[length];
            if ((mEncryptSwitch & 0xF0) != 0) {
                if (getEncryptDictData(startKeyAddr, data) != length) {
                    Log.w(TAG,
                            "******************** load dict key data failed! *************************");
                }
            } else {
                if (getDictData(startKeyAddr, data) != length) {
                    Log.w(TAG,
                            "******************** load dict key data failed! *************************");
                }
            }
        }

        return data;
    }

    /**
     * 获取动漫数据
     * @param addresNumber
     * @return
     */
    public DongManParam getDMDictData(int addresNumber) {
        DongManParam dongManParam = new DongManParam();
        byte indexData[] = new byte[4];

        try {
            int indexAddr = mExplainStartAddr + addresNumber * 4;
            // 解释地址的位置
            if(getDictData(indexAddr, indexData) != 4) {
                Log.w(TAG, "******************** load dict explain start address failed! *************************");
            }
            // 当前解释的位置
            int startExplain = (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                    + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
            // 下一解释地址的位置
            indexAddr = mExplainStartAddr + (addresNumber + 1) * 4;
            if(getDictData(indexAddr, indexData) != 4) {
                Log.w(TAG, "******************** load dict explain end address failed! *************************");
            }
            // 下一解释的位置
            int endExplain =  (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                    + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
            int size = endExplain - startExplain;
	    	/*mRandomAccessFile.seek(startExplain);
	    	int count = mRandomAccessFile.read();*/
            read( indexData, 1, startExplain);
            int count = indexData[0];
            if(count != 2) {	// 无动画
	    		/*mRandomAccessFile.seek(startExplain);
	    		mRandomAccessFile.read(dongManParam.explainData);*/
                dongManParam.explainData = new byte[size];
                read( dongManParam.explainData, dongManParam.explainData.length, startExplain+1);
                dongManParam.dmWidth = 0;
                dongManParam.dmHeight = 0;
                dongManParam.dmData = null;
            } else {
                int textAddr, textSize;
                byte[] dmParam = new byte[4];
                //mRandomAccessFile.read(dmParam);
                read( dmParam, dmParam.length, startExplain+1);
                textAddr = (byte2int(dmParam[0])) + (byte2int(dmParam[1]) << 8);
                textSize = (byte2int(dmParam[2])) + (byte2int(dmParam[3]) << 8);
                dongManParam.explainData = new byte[textSize-textAddr];
	    		/*mRandomAccessFile.skipBytes(2);
	    		mRandomAccessFile.read(dongManParam.explainData);		// 动漫解释
	    		mRandomAccessFile.skipBytes(2);
	    		mRandomAccessFile.read(dmParam);*/
                read( dongManParam.explainData, dongManParam.explainData.length, startExplain+7);
                read( dmParam, dmParam.length, startExplain+7+(dongManParam.explainData.length+2));
                dongManParam.dmWidth = (byte2int(dmParam[0])) + (byte2int(dmParam[1]) << 8);
                dongManParam.dmHeight = (byte2int(dmParam[2])) + (byte2int(dmParam[3]) << 8);
                dongManParam.dmData = new byte[size - textSize - 6];
	    		/*mRandomAccessFile.seek(startExplain + textSize + 6);
	    		mRandomAccessFile.read(dongManParam.dmData);*/
                read( dongManParam.dmData, dongManParam.dmData.length, startExplain + textSize + 6);
            }
        } catch (Exception e)/*(IOException e)*/ {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return dongManParam;
    }

    /*
     * CYC checkout the file head. 由于Java不支持无符号数，且机器的long 和 int 能表示的数据范围一样，
     * 校验码的4字节数据已经超出了int能表示的范围，因此将4字节拆分成高低双字节来校验。 后面是C语言的校验代码 <p>
     *
     * @param data: the data need checked length: data length value: cyc value
     *
     * @return == true means successful, ==false means failed.
     */
    public boolean dictFileGenerateCrc(byte[] data, int length, int valueH16,
                                       int valueL16) {
        int crcH16, crcL16, ic;
        int crc_table[] = { // CRC余式表
                0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7, 0x8108,
                0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef, };
        crcH16 = 0;
        crcL16 = 0;
        int pos, temp;
        for (pos = 0; pos < length; pos++) {
            temp = byte2int(data[pos]);
            ic = (crcL16 / 0x100) / 0x10;
            crcH16 = ((crcH16 & 0xFFF) << 4) + ((crcL16 & 0xF000) >> 12);
            crcL16 = (crcL16 & 0xFFF) << 4;
            crcL16 ^= crc_table[(ic ^ (temp / 0xF)) & 0xF];
            ic = (crcL16 / 0x100) / 0x10;
            crcH16 = ((crcH16 & 0xFFF) << 4) + ((crcL16 & 0xF000) >> 12);
            crcL16 = (crcL16 & 0xFFF) << 4;
            crcL16 ^= crc_table[ic ^ (temp & 0xF)];
        }
        return (boolean) (valueH16 == crcH16 && valueL16 == crcL16);
    }

    private int getDictTypeById(int id) {
        switch (id) {
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

    private String getDataCodeType(int type) {
        String charSet;
        switch (type) {
            case 0:
                charSet = "UTF-8";
                break;
            case 1:
                charSet = "UTF-16";
                break;
            case 2:
                charSet = "GB2312";
                break;
            default:
                charSet = "UTF-8";
        }
        return charSet;
    }

    private int byte2int(byte b) {
        if (b >= 0) {
            return (int) b;
        } else {
            return 0x100 + b;
        }
    }

    class EnglishLettersInfo {
        /**
         * 26个英文字母
         */
        public static final String ENGLISH_LETTERS = "abcdefghijklmnopqrstuvwxyz";
        /**
         * 词典
         */
        public int id=-1;
        /**
         * 26个英文字母在相应的词典里的开始位置(按生词个数排列)
         */
        public int[] lettersPosition;
    }

}
