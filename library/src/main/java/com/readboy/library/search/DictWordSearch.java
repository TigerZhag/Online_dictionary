package com.readboy.library.search;


import android.content.Context;
import android.database.sqlite.SQLiteDiskIOException;

import com.readboy.depict.data.HanziPackage;
import com.readboy.depict.model.HanziInfo;
import com.readboy.library.io.DictFile;
import com.readboy.library.io.DictIOFile;
import com.readboy.library.io.DongManParam;
import com.readboy.library.utils.BiHuaYinBiao;
import com.readboy.library.utils.PhoneticUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-7-25.
 */
public class DictWordSearch {

    /* 字典类型 */
    public static final int		DICT_TYPE_ERROR		 = -1;	// 未知类型
    public static final int		DICT_TYPE_ENG		 = 0;	// 英语类型字典
    public static final int		DICT_TYPE_CHI		 = 1;	// 中文字典类型

    private DictFile mDictFile = null;

    private DictIOFile mDictIOFile = null;
    private int mAllKeyCount = 0;

    private HanziPackage mHanziPackage;

    private int currentDictID=-1;

    public DictWordSearch() {
    }

//    public boolean open(int mDictId) {
//        mDictFile = new DictFile();
//        if(  mDictFile.dictFileOpen(DictFile.DICT_LOCAL_NAME[mDictId], "r") == false ) {
//            return false;
//        }
//        if( mDictFile.dictFileInitial(mDictId) == false ) {
//            return false;
//        }
//        mAllKeyCount = mDictFile.dictFileGetKeycount();
//        return true;
//    }

//    public void close() {
//        if (mDictFile != null) {
//            mDictFile.dictFileClosed();
//            mDictFile = null;
//        }
//    }

//    public DictFile getDictFile() {
//        return mDictFile;
//    }

    private boolean openHanziPackage(Context context, HanziPackage.DataChangeListener dataChangeListener) {
        try {
            if (mHanziPackage == null) {
                mHanziPackage = new HanziPackage(context);
            }
            mAllKeyCount = mHanziPackage.getHanziCount();
            if (dataChangeListener != null) {
                if (!mHanziPackage.dataCheck(context, dataChangeListener)) {
                    dataChangeListener.onDataChange(true);
                }
            }
            return true;
        } catch (SQLiteDiskIOException e) {
            e.printStackTrace();
            if (dataChangeListener != null) {
                dataChangeListener.onDataChange(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean open(int mDictId) {
        mDictIOFile = new DictIOFile();
        if (mDictIOFile.dictFileOpen(mDictId, "r")) {
            mAllKeyCount = mDictIOFile.getAllKeycount();
        }
        return true;
    }

    public boolean open(int mDictId, Context context, HanziPackage.DataChangeListener dataChangeListener) {
        currentDictID = mDictId;
        if (mDictId == DictFile.ID_BHDICT) {
            return openHanziPackage(context, dataChangeListener);
        } else {
            close();
            mDictIOFile = new DictIOFile();
            if (mDictIOFile.dictFileOpen(context, mDictId, "r")) {
                mAllKeyCount = mDictIOFile.getAllKeycount();
            }
            return true;
        }
    }

    public void close() {
        if (mDictIOFile != null) {
            mDictIOFile.dictFileClosed();
            mDictIOFile = null;
        }
        closeHanziPackage();
    }

    public void closeHanziPackage() {
        if (mHanziPackage != null) {
            mHanziPackage.recycle();
            mHanziPackage = null;
        }
    }

    public void dissMissHanziDataDialog() {
        if (mHanziPackage != null) {
            mHanziPackage.colse();
        }
    }

    public void reset() {
        if (mHanziPackage != null) {
            mAllKeyCount = mHanziPackage.getHanziCount();
        }
    }

    public HanziPackage getHanziPackage() {
        return mHanziPackage;
    }

    public DictIOFile getDictIOFile() {
        return mDictIOFile;
    }

    public int getAllKeyCount() {
        return mAllKeyCount;
    }

    public int getDictId() {
        return currentDictID;
    }

//    public byte[] getExplainByte(int wordIndex) {
//        byte[] explainData = null;
//        if (mDictFile != null) {
//            if(mDictFile.dictFileGetDictId() != DictFile.ID_DMDICT) {
//                explainData = mDictFile.loadCurExplain(wordIndex);
//            }
//        }
//        return explainData;
//    }

    /**
     * 获取生词或单词的解释内容数据
     * @param wordIndex
     * @return
     */
    public byte[] getExplainByte(int wordIndex) {
        byte[] explainData = null;
        if (mHanziPackage != null) {
            HanziInfo hanziInfo = getHanziInfo(wordIndex);
            if (hanziInfo != null) {
                mHanziPackage.setHanzi(hanziInfo);
                String content = mHanziPackage.getRemark();
                content = PhoneticUtils.checkString(content);
                if (content != null) {
                    explainData = content.getBytes();
                }
            }
        } else if (mDictIOFile != null) {
            if(mDictIOFile.getDictID() != DictIOFile.ID_DMDICT) {
                explainData = mDictIOFile.loadCurExplain(wordIndex);
            }
        }
        return explainData;
    }

    public byte[] getExplainByte(HanziInfo hanziInfo) {
        byte[] explainData = null;
        if (hanziInfo != null) {
            mHanziPackage.setHanzi(hanziInfo);
            String content = mHanziPackage.getRemark();
            if (content != null) {
                content = PhoneticUtils.checkString(content);
                explainData = content.getBytes();
            }
        }
        return explainData;
    }

//    public int getExplainByteStart(byte[] explainData) {
//        int startIndex = 0;
//        if((mDictFile.dictFileGetEncryptSwitch() & 0xF) != 0) {    // 解释有加密
//            startIndex = (byte2int(explainData[1]) << 0) + (byte2int(explainData[2]) << 8);
//        }
//        return startIndex;
//    }

    /**
     * @param explainData
     * @return 数据的开始位置
     */
    public int getExplainByteStart(byte[] explainData) {
        int startIndex = 0;
        if((mDictIOFile.getEncryptSwitch() & 0xF) != 0) {    // 解释有加密
            startIndex = (byte2int(explainData[1]) << 0) + (byte2int(explainData[2]) << 8);
        }
        return startIndex;
    }

//    public int getExplainByteLength(byte[] explainData) {
//        int length = 0;
//        if (explainData != null) {
//            length = explainData.length;
//        }
//        if((mDictFile.dictFileGetEncryptSwitch() & 0xF) != 0) {    // 解释有加密
//            length = (byte2int(explainData[3]) << 0) + (byte2int(explainData[4]) << 8);
//        }
//        return length;
//    }

    /**
     * @param explainData
     * @return 数据的结束位置
     */
    public int getExplainByteLength(byte[] explainData) {
        int length = 0;
        if (explainData != null) {
            length = explainData.length;
        }
        if((mDictIOFile.getEncryptSwitch() & 0xF) != 0) {    // 解释有加密
            length = (byte2int(explainData[3]) << 0) + (byte2int(explainData[4]) << 8);
        }
        return length;
    }

//    public DongManParam getDongManParam(int wordIndex) {
//        DongManParam dongManParam = null;
//        if (mDictFile.dictFileGetDictId() == DictFile.ID_DMDICT) {
//            dongManParam = mDictFile.dictFileGetDMDictData(wordIndex);
//        }
//        return dongManParam;
//    }

    /**
     * 动漫数据
     * @param wordIndex
     * @return
     */
    public DongManParam getDongManParam(int wordIndex) {
        DongManParam dongManParam = null;
        if (mDictIOFile != null && mDictIOFile.getDictID() == DictIOFile.ID_DMDICT) {
            dongManParam = mDictIOFile.getDMDictData(wordIndex);
        }
        return dongManParam;
    }

//    public int getKeyIndex(String key) {
//        int addrNumber = 0;
//        if (key != null && key.length() != 0) {
//            /**
//             * 注意:将中式a转换为英式，再查找
//             */
//            key = PhoneticUtils.returnOldCharseqence(key);
//            addrNumber = mDictFile.dictFileGetDictDataWithCMP(key);
//            if (addrNumber == -1) {
//                addrNumber = 0;
//            }
//        }
//        return addrNumber&0x00ffffff; // 高位用于表示是否精确匹配的单词
//    }

    /**
     * 模糊匹配生词或单词的位置
     * @param key 字符串
     * @return
     */
    public int getKeyIndex(String key) {
        int index = 0;
        if (mHanziPackage != null) {
            if (key != null) {
                key = key.trim().toLowerCase();
                char[] chars = key.toCharArray();
                for (char c:chars) {
                    if (isEnglishOrNumber(c)) {
                        continue;
                    }
                    if (c >= 0xD800) {
                        continue;
                    }
                    if (mHanziPackage.isHanziExist(c+"")) {
                        index = mHanziPackage.getDictHanziIndex(c+"");
                        if (index >= 0) {
                            break;
                        }
                    }
                }
                if (0 > index) {
                    index = 0;
                }
            }
        } else if (mDictIOFile != null) {
            if (key != null && key.length() != 0) {
                /**
                 * 注意:将中式a转换为英式，再查找
                 */
                key = PhoneticUtils.returnOldCharseqence(key);
                index = mDictIOFile.getKeyIndex(key);
                if (index == -1) {
                    index = 0;
                }
            }
        }
        return index;
    }

    /**
     * 模糊匹配生词或单词的位置
     * @param key 字符串
     * @return
     */
    public int getKeyIndexAccurate(String key) {
        int index = -1;
        if (mHanziPackage != null) {
            if (key != null) {
                char[] chars = key.toCharArray();
                if  (key != null && key.length() == 1) {
                    if (mHanziPackage.isHanziExist(key)) {
                        index = mHanziPackage.getDictHanziIndex(key);
                    }
                }
            }
        } else if (mDictIOFile != null) {
            if (key != null && key.length() != 0) {
                /**
                 * 注意:将中式a转换为英式，再查找
                 */
                key = PhoneticUtils.returnOldCharseqence(key);
                index = mDictIOFile.getKeyIndexAccurate(key);
            }
        }
        return index;
    }

//    public int getSearchKeyIndex(String key) {
//        int addrNumber = 0;
//        if (key != null && key.length() != 0) {
//            /**
//             * 注意:将中式a转换为英式，再查找
//             */
//            key = PhoneticUtils.returnOldCharseqence(key);
//            addrNumber = mDictFile.dictFileGetDictDataWithCMP(key);
//            if (addrNumber == -1) {
//                return addrNumber;
//            }
//        }
//        return addrNumber&0x00ffffff; // 高位用于表示是否精确匹配的单词
//    }

    /**
     * 搜索匹配字符的最佳单词的位置
     * @param key
     * @return 默认返回0
     */
    public int getSearchKeyIndex(String key) {
        int index = 0;
        if (mDictIOFile != null) {
            if (key != null && key.length() != 0) {
                /**
                 * 注意:将中式a转换为英式，再查找
                 */
                key = PhoneticUtils.returnOldCharseqence(key);
                index = mDictIOFile.getKeyIndex(key);
                if (index == -1) {
                    return index;
                }
            }
        }
        return index; // 高位用于表示是否精确匹配的单词
    }

    /**
     * 精确匹配
     * @param key
     * @return 默认返回-1
     */
    public int getSearchKeyIndexByAccurate(String key) {
        int index = -1;
        if (mDictIOFile != null) {
            if (key != null && key.length() != 0) {
                /**
                 * 注意:将中式a转换为英式，再查找
                 */
                key = PhoneticUtils.returnOldCharseqence(key);
                index = mDictIOFile.getKeyIndex(key);
            }
        }
        return index; // 高位用于表示是否精确匹配的单词
    }

    public int[] getSearchKeyRange(String key) {
        if (mDictIOFile != null) {
            if (key != null && key.length() != 0) {
                /**
                 * 注意:将中式a转换为英式，再查找
                 */
                key = PhoneticUtils.returnOldCharseqence(key);
                return mDictIOFile.getKeyRange(key);
            }
        }
        return null;
    }

    /**
     * 搜索匹配字符的最佳生词
     * @param key
     * @return
     */
    public List<HanziInfo> getHanziKeyList(String key) {
        if (mHanziPackage != null) {
            List<HanziInfo> list = mHanziPackage.getHanzi(key);
            if (list != null && list.size() > 0) {
                return list;
            }
        }
        return null;
    }

//    public String getKeyWord(int index) {
//        if (mDictFile != null) {
//            return mDictFile.dictFileGetDictKeyByAddressNumber(index);
//        }
//        return null;
//    }

    /**
     * 获取生词或单词
     * @param index
     * @return
     */
    public String getKeyWord(int index) {
        if (mHanziPackage != null) {
            HanziInfo hanziInfo = getHanziInfo(index);
            if (hanziInfo != null) {
                return hanziInfo.word;
            }
        } else if (mDictIOFile != null) {
            return mDictIOFile.getKeyStringByIndex(index);
        }
        return null;
    }

    public List<String> getHanziWordList(int start, int end) {
        List<String> list = new ArrayList<String>();
        if (mHanziPackage != null) {
            if (end >= mAllKeyCount) {
                end = mAllKeyCount-1;
            }
            List<HanziInfo> data = mHanziPackage.getDictHanzi(start, end);
            if (data != null) {
                for (HanziInfo hanziInfo:data) {
                    list.add(hanziInfo.word+" "+PhoneticUtils.checkString(hanziInfo.phonetic));
                }
            }
        }
        return list;
    }

    public HanziInfo getHanziInfo(int index) {
        List<HanziInfo> data = null;
        try {
            data = mHanziPackage.getDictHanzi(index, index);
            if (data != null && data.size() > 0) {
                return data.get(0);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getHanziSoundBytes(int index) {
        if (mHanziPackage != null) {
            HanziInfo hanziInfo = getHanziInfo(index);
            return getHanziSoundBytes(hanziInfo.phonetic);
        }
        return null;
    }

    public byte[] getHanziSoundBytes(HanziInfo hanziInfo) {
        if (mHanziPackage != null) {
            return mHanziPackage.getHanziLib().getSoundData(hanziInfo);
        }
        return null;
    }

    public byte[] getHanziSoundBytes(String phonetic) {
        if (mHanziPackage != null) {
            return mHanziPackage.getHanziLib().getSoundData(phonetic);
        }
        return null;
    }

    /**
     * 获取笔画字典中的生词拼音
     * @param content
     * @param start
     * @param length
     * @return 格式为:ā
     */
    public String getBihuaPhonetic(byte[] content, int start, int length) {
//        return BiHuaYinBiao.getYinBiao(content, start, length);
        return DictWordSearch.formatBihuaPinYin(BiHuaYinBiao.getYinBiao(content, start, length));
    }

    public String getBihuaPhonetic(int index) {
        if (mHanziPackage != null) {
            List<HanziInfo> data = mHanziPackage.getDictHanzi(index, index+1);
            if (data != null) {
                return data.get(0).phonetic;
            }
        }
        return null;
    }

    /**
     * 获取笔画字典中的生词拼音
     * @param content
     * @param start
     * @param length
     * @return 格式为:a1
     */
    public String getBihuaPhoneticWithNum(byte[] content, int start, int length) {
        return DictWordSearch.formatBihuaPinYin(BiHuaYinBiao.getYinBiao(content, start, length));
    }

    private int byte2int(byte b) {
        return (b & 0xFF);
    }

    /**
     * 判断字符串是否为全是空格
     * @param str
     * @return
     */
    public static boolean isBlankString(String str) {
        if (str.length() > 0) {
            for (int i=0;i<str.length();i++) {
                if (str.charAt(i) != ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断字符串的类型(中文或英文)
     * @param s
     * @return
     */
    public static int getDictType(String s) {
        int value;

        if(s == null) {
            return DICT_TYPE_ERROR;
        }
        for(int i = 0;i < s.length();i++) {
            value = s.charAt(i);
            /**
             * 注意: 中式的a为0x058d + 0xDD00
             */
            if(value >= 'a' && value <= 'z' || value >= 'A' && value <= 'Z' || value == 0x058d + 0xDD00) {
                return DICT_TYPE_ENG;
            }else if(value >= 0x80) {
                return DICT_TYPE_CHI;
            }
        }

        return DICT_TYPE_ERROR;
    }

    /**
     * 过滤拼音中的特殊编码
     * @param str
     * @return
     */
    public static String formatBihuaPinYin(String str) {
        String nStr = "";
        int len = str.length();
        boolean hasYinBiao = false;
        int yinbiaoLevel = 0;
        for(int i=0;i < len; i++){
            char ch = str.charAt(i);
            if (ch == 0x0069) {//i编码
                ch = 'i';
            } else if (ch == 0x0075) {//u编码
                ch = 'u';
            } else if (ch == 0x00fc) {//ü编码
                ch = 'v';
            } else if (ch == 0x006f) {//o编码
                ch = 'o';
            }else if (ch == 0x0251) {//a编码
                ch = 'a';
            } else if (ch == 0x0062) {//b编码
                ch = 'b';
            } else if (!hasYinBiao) {
                for (int k=0;k<BiHuaYinBiao.YinBiaoNum.length;k++) {
                    if (ch == BiHuaYinBiao.YinBiaoNum[k]) {
                        int yIndex = k+1;
                        int intNum = yIndex/BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        int deciNum = yIndex%BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        int h = k/BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        h++;
                        if (!(intNum >0 && deciNum == 0)) {//判断是否整数
                            yinbiaoLevel = k%BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                            yinbiaoLevel++;
//                            ch = BiHuaYinBiao.YinBiaoNum[h*BiHuaYinBiao.SINGLE_YINBIAO_NUM-1];

                            hasYinBiao = true;
                        }
                        ch = BiHuaYinBiao.YinBiaoNum[h*BiHuaYinBiao.SINGLE_YINBIAO_NUM-1];
                        break;
                    }
                }
            }
            nStr += ch;
        }
        nStr += yinbiaoLevel;
        return nStr;
    }

    /**
     * 判断是否英文小写字母或阿拉伯数字
     *
     * @param c
     * @return
     */
    private static boolean isEnglishOrNumber(char c) {
        if (c >= 'a' && 'z' >= c) {
            return true;
        }else if (Character.isDigit(c)) {
            return true;
        }
        return false;
    }
}
