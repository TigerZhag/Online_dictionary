package com.readboy.library.utils;

/**
 * 获取生词的音标
 * 如非必要，请勿修改解析方法!
 * 解析方法是词典ScollView的parseExp方法的简化版
 * 修改时,请保持两个解析的方法的一致性!
 * @author Administrator
 *
 */
public class BiHuaYinBiao {

    private final static int RICHEDIT_SETCOLOR	= 1;

    /**
     * 解析方法
     * @param content
     * @param start
     * @param end
     * @return
     */
    public static String getYinBiao(byte[] content, int start, int end) {
        if(content == null) {
            return null;
        }
        int i;
        int value;
        String yinBiao = "";

        int length = content.length;
        boolean IS_START = false;//是否开始读取音标
        for(i = start;i <length;) {
            value = content[i]&0xff;

            if(value == 0x0D || value == 0x0A) {		// 换行
                if (IS_START) {
                    break;
                }
                while(++i<content.length) {
                    value = content[i]&0xff;
                    if(value!=0x0D && value!=0x0A) break;
                }
                if(i>=content.length) {
                    break;
                }
            }

            if(value == RICHEDIT_SETCOLOR) {		// 是设置颜色命令
                i += 3;
                continue;
            } else if(value == 0xFE) {				// 音标区域
                value = content[++i]&0xff;
                if(value == 0xBB) {		// 换行
                    if (IS_START) {
                        break;
                    }
                } else if(value >= 0x50 && mYinBiaoList[value-0x50] != 0) {
                    yinBiao += ""+mYinBiaoList[value-0x50];
                    IS_START = true;
                }
            }else if(value >= 0x80) {				// 汉字区域
                i++;
            }
            i++;
        }

        return yinBiao;
    }

    /**
     * 单个字母音标的个数
     */
    public final static int SINGLE_YINBIAO_NUM = 5;

    /**
     * 拼音音标
     */
    public final static char YinBiaoNum[]  = {
            /*********** a *******/
            0x0101,		/* 0x89 'ā'*/
            0x00e1,		/* 0x8a 'á'*/
            0x0103,		/* 0x8b '?'*/
            0x00e0,		/* 0x8c 'à'*/
      'a',//0x0251,		/* 0x8d 'ɑ'*/

            /*********** o *******/
            0x014d,		/* 0xa3 'ō'*/
            0x00f3,		/* 0xa4 'ó'*/
            0x01d2,		/* 0xa5 'ǒ'*/
            0x00f2,		/* 0xa6 'ò'*/
      'o',//0x006f,		/* 0xa7 'o'*/

            /*********** e *******/
            0x0113,		/* 0x91 'ē'*/
            0x00e9,		/* 0x92 'é'*/
            0x011b,		/* 0x93 'ě'*/
            0x00e8,		/* 0x94 'è'*/
      'e',//0x0065,		/* 0x95 'e'*/

            /*********** i *******/
            0x012b,		/* 0x99 'ī'*/
            0x00ed,		/* 0x9a 'í'*/
            0x01d0,		/* 0x9b 'ǐ'*/
            0x00ec,		/* 0x9c 'ì'*/
      'i',//0x0069,		/* 0x9d 'i'*/

            /*********** u *******/
            0,
            0x01d8,		/* 0xb3 'ǘ'*/
            0x01da,		/* 0xb4 'ǚ'*/
            0x01dc,		/* 0xb5 'ǜ'*/
        'v',//0x00fc,    * 0xb6 'ü'*/

            /*********** e *******/
            0x0113,		/* 0x91 'ē'*/
            0x00e9,		/* 0x92 'é'*/
            0x011b,		/* 0x93 'ě'*/
            0x00e8,		/* 0x94 'è'*/
        'e',//0x0065		/* 0x95 'e'*/
            /*********** u *******/
            0x016b,		/* 0xad 'ū'*/
            0x00fa,		/* 0xae 'ú'*/
            0x016d,		/* 0xaf '?'*/
            0x00f9,		/* 0xb0 'ù'*/
        'u'//0x0075,		/* 0xb1 'u'*/
    };

    public final static char mYinBiaoList[] = {
            0x0062,		/* 0x50 'b' */
            0x0064,		/* 0x51 'd' */
            0x00f0,		/* 0x52 'e'*/
            0,		/* 0x53 ''*/
            0x0066,		/* 0x54 'f'*/
            0x0067,		/* 0x55 'g' */
            0x0068,		/* 0x56 'h'*/
            0x006a,		/* 0x57 'j'*/
            0x006b,		/* 0x58 'k'*/
            0x006c,		/* 0x59 'l'*/
            0x006d,		/* 0x5a 'm'*/
            0x006e,		/* 0x5b 'n'*/
            0x014b,		/* 0x5c '?'*/
            0x0070,		/* 0x5d 'p'*/
            0x0072,		/* 0x5e 'r'*/
            0x0073,		/* 0x5f 's'*/
            0x0283,		/* 0x60 '?'*/
            0x0074,		/* 0x61 't'*/
            0,		/* 0x62 ''*/
            0x03b8,		/* 0x63 'θ'*/
            0x0076,		/* 0x64 'v'*/
            0x0077,		/* 0x65 'w'*/
            0x0078,		/* 0x66 'x'*/
            0x007a,		/* 0x67 'z'*/
            0x0292,		/* 0x68 '?'*/
            0x0069,		/* 0x69 'i'*/
            0x026a,		/* 0x6a '?'*/
            0x025b,		/* 0x6b '?'*/
            0x00e6,		/* 0x6c '?'*/
            0x0061,		/* 0x6d 'a'*/
            0x0254,		/* 0x6e '?'*/
            0x0075,		/* 0x6f 'u'*/
            0x006d,		/* 0x70 'm'*/
            0,		/* 0x71 ''*/
            0x028c,		/* 0x72 '?'*/
            0x025c,		/* 0x73 '?'*/
            0x0259,		/* 0x74 '?'*/
            0x0259,		/* 0x75 '?'*/
            0,		/* 0x76 ''*/
            0x006f,		/* 0x77 'o'*/
            0x25c,		/* 0x78 '?'*/
            0,		/* 0x79 ''*/
            0,		/* 0x7a ''*/
            0x02d0,		/* 0x7b '?'*/
            0x02c8,		/* 0x7c '?'*/
            0,		/* 0x7d ''*/
            0,		/* 0x7e ''*/
            0,		/* 0x7f ''*/
            0x0259,		/* 0x80 '?'*/
            0x0254,		/* 0x81 '?'*/
            0x02cc,		/* 0x82 '?'*/
            0,		/* 0x83 '?'*/
            0x0075,		/* 0x84 'u'*/
            0x0075,		/* 0x85 'u'*/
            0x006e,		/* 0x86 'n'*/
            0,		/* 0x87 ''*/
            0,		/* 0x88 ''*/
            0x0101,		/* 0x89 'ā'*/
            0x00e1,		/* 0x8a 'á'*/
            0x0103,		/* 0x8b '?'*/
            0x00e0,		/* 0x8c 'à'*/
            0x0251,		/* 0x8d 'ɑ'*/
            0x0062,		/* 0x8e 'b'*/
            0x0063,		/* 0x8f 'c'*/
            0x0064,		/* 0x90 'd'*/
            0x0113,		/* 0x91 'ē'*/
            0x00e9,		/* 0x92 'é'*/
            0x011b,		/* 0x93 'ě'*/
            0x00e8,		/* 0x94 'è'*/
            0x0065,		/* 0x95 'e'*/
            0x0066,		/* 0x96 'f'*/
            0x0067,		/* 0x97 'g'*/
            0x0068,		/* 0x98 'h'*/
            0x012b,		/* 0x99 'ī'*/
            0x00ed,		/* 0x9a 'í'*/
            0x01d0,		/* 0x9b 'ǐ'*/
            0x00ec,		/* 0x9c 'ì'*/
            0x0069,		/* 0x9d 'i'*/
            0x006a,		/* 0x9e 'j'*/
            0x006b,		/* 0x9f 'k'*/
            0x006c,		/* 0xa0 'l'*/
            0x006d,		/* 0xa1 'm'*/
            0x006e,		/* 0xa2 'n'*/
            0x014d,		/* 0xa3 'ō'*/
            0x00f3,		/* 0xa4 'ó'*/
            0x01d2,		/* 0xa5 'ǒ'*/
            0x00f2,		/* 0xa6 'ò'*/
            0x006f,		/* 0xa7 'o'*/
            0x0070,		/* 0xa8 'p'*/
            0x0071,		/* 0xa9 'q'*/
            0x0072,		/* 0xaa 'r'*/
            0x0073,		/* 0xab 's'*/
            0x0074,		/* 0xac 't'*/
            0x016b,		/* 0xad 'ū'*/
            0x00fa,		/* 0xae 'ú'*/
            0x016d,		/* 0xaf '?'*/
            0x00f9,		/* 0xb0 'ù'*/
            0x0075,		/* 0xb1 'u'*/
            0,		/* 0xb2 ''*/
            0x01d8,		/* 0xb3 'ǘ'*/
            0x01da,		/* 0xb4 'ǚ'*/
            0x01dc,		/* 0xb5 'ǜ'*/
            0x00fc,		/* 0xb6 'ü'*/
            0x0077,		/* 0xb7 'w'*/
            0x0078,		/* 0xb8 'x'*/
            0x0079,		/* 0xb9 'y'*/
            0x007a,		/* 0xba 'z'*/
            0,		/* 0xbb ''*/
            0,		/* 0xbc ''*/
            0,		/* 0xbd ''*/
            0,		/* 0xbe ''*/
            0,		/* 0xbf ''*/
    };
}

