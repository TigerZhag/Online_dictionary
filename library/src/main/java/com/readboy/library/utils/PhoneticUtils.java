package com.readboy.library.utils;


import android.text.Editable;

public class PhoneticUtils
{
    public static final char[] pin1 = {'ā', 'á', 'ǎ', 'à', 'a'};
    public static final char[] pin2 = {
            0x0589 + 0xDD00, 0x058a + 0xDD00, 0x058b + 0xDD00,
            0x058c + 0xDD00, 0x058d + 0xDD00};

    public static boolean isHasChange(char c) {
        boolean change = false;
        if (c == pin2[4]) {
            return true;
        }
        return false;
    }

    public static char checkDefineChar(char ch)
    {
        if(ch == 0x0103){
            return pin2[2];
        }
        for (int i = 0; i < 5; i++)
        {
            if (ch == pin1[i])
            {
                ch = pin2[i];
                break;
            }
        }

        return ch;
    }

    public static char returnToOldCode(char ch){
        for (int i = 0; i < 5; i++)
        {
            if (ch == pin2[i])
            {
                ch = pin1[i];
                break;
            }
        }
        return ch;
    }

    public static char oldCodeToNewCode(char code)
	{
		char newCode = code;

		if(code >= 0x0550 && code < 0x05bb){
			newCode = (char)(code - 0x0550 + 0xE250);
		}

		return newCode;
	}


    public static String checkString(String str)
    {
        if (str != null) {
            String strTemp = "";
            int len = str.length();
            for (int i = 0; i < len; i++)
            {
                strTemp += checkDefineChar(str.charAt(i));
//            strTemp += oldCodeToNewCode(str.charAt(i));
            }

            return strTemp;

        }
        return null;
    }

    public static char[] checkCharseqence(char[] chars){
        for(int i = 0;i < chars.length;i++){
            for (int j = 0; j < 5; j++)
            {
                if (chars[i] == pin1[j])
                {
                    chars[i] = pin2[j];
                    break;
                }
            }
        }
        return chars;
    }

    public static void checkCharseqence(Editable editable){
        int len = editable.length();
        for(int i=0;i < len; i++){
            char ch = editable.charAt(i);
            for (int j = 0; j < 5; j++)
            {
                if (ch == pin1[j])
                {
                    ch = pin2[j];
                    editable.replace(i, i+1, ch+"");
                    break;
                }
            }
        }
    }

    public static String returnOldCharseqence(String str){
        String oldStr = "";
        int len = str.length();
        for(int i=0;i < len; i++){
            char ch = str.charAt(i);
            for (int j = 0; j < 5; j++)
            {
                if (ch == pin2[j])
                {
                    ch = pin1[j];
                    break;
                }
            }
            oldStr += ch;
        }
        return oldStr;
    }


    /*******************************************注意汉字学习拼音是GBK编码***************************************/
    private static char[] hanziPin = {0x0101, 0xE1, 0x01CE, 0xE0,      0,//'ā', 'á', 'ǎ', 'à', 0表示没有添加
                                        0   ,    0, 0x01D4,    0,      0,
                                        0   ,    0,      0,    0, 0x0261};// 0,  0, ?, 0

    public static String bihuaPinyinChangeToHanziPinyin(String str) {
        String nStr = "";
        int len = str.length();
        for(int i=0;i < len; i++){
            char ch = str.charAt(i);
            for (int j = 0; j < 4; j++)
            {
                if(ch == 0x0103){
                    ch = hanziPin[2];
                    break;
                } else if (ch == 0x016d) {
                    ch = hanziPin[7];
                    break;
                } else if (ch == pin1[j]) {
                    ch = hanziPin[j];
                    break;
                }
            }
            nStr += ch;
        }
        return nStr;
    }

    public static String HanziPinyinChangeToBiHuaPinyin(String str) {
        String nStr = "";
        int len = str.length();
        for(int i=0;i < len; i++){
            char ch = str.charAt(i);
            if (ch == hanziPin[14]) {
                ch = 'g';
                break;
            }
        }
        return nStr;
    }

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
                for (int k=0;k< BiHuaYinBiao.YinBiaoNum.length;k++) {
                    if (ch == BiHuaYinBiao.YinBiaoNum[k]) {
                        int yIndex = k+1;
                        int intNum = yIndex/ BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        int deciNum = yIndex% BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        int h = k/ BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                        h++;
                        if (!(intNum >0 && deciNum == 0)) {//判断是否整数
                            yinbiaoLevel = k% BiHuaYinBiao.SINGLE_YINBIAO_NUM;
                            yinbiaoLevel++;
//                            ch = BiHuaYinBiao.YinBiaoNum[h*BiHuaYinBiao.SINGLE_YINBIAO_NUM-1];

                            hasYinBiao = true;
                        }
                        ch = BiHuaYinBiao.YinBiaoNum[h* BiHuaYinBiao.SINGLE_YINBIAO_NUM-1];
                        break;
                    }
                }
            }
            nStr += ch;
        }
        nStr += yinbiaoLevel;
        return nStr;
    }

    //判断是否是整数
    public static boolean isNumeric(String s)
    {
        if((s != null)&&(s!=""))
            return s.matches("^[0-9]*$");
        else
            return false;
    }
}

