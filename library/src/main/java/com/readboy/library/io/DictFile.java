/**
 * 鏂囦欢鎿嶄綔绫伙紝鍖呮嫭11鏈瓧鍏歌祫鏂欏拰鍔ㄦ极
 * 
 */
package com.readboy.library.io;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

/**
 * 主要用作搜索英文单词的发音, 搜索功能由DictIOFile代替
 */
public class DictFile {
	private static final String TAG = "DictFile";

	public static final int FILE_HEAD_SIZE = 32;
	public static final int NEWWORD_RECORD_SIZE = 6;	// ID + addrNumber + readEnable
	public static final int NEWWORD_TOTAL_COUNT = 1000;
	public static final int NEWWORD_TOTAL_SIZE = (NEWWORD_RECORD_SIZE*NEWWORD_TOTAL_COUNT);
	/* 字典ID */
	public static final int ID_BHDICT		= 0;
	public static final int ID_CYDICT		= 1;
	public static final int ID_LWDICT		= 2;
	public static final int ID_DMDICT		= 3;
	public static final int ID_GDDICT		= 4;
	public static final int ID_HYDICT		= 5;
	public static final int ID_XDDICT		= 6;
	public static final int ID_JMDICT		= 7;
	public static final int ID_XSDICT		= 8;
	public static final int ID_YHDICT		= 9;
	public static final int ID_YYDICT		= 10;

	public static final int ID_NEWWORD	= 11;
	public static final int ID_SPEECHDICT	= 12;

	/* 字典类型 */
	public static final int  YY_ENG = 0;    // 0: English
	public static final int  YY_UTF = 1;    // 1: Unicode(单词不采用此编码)
	public static final int  YY_CHS = 2;    // 2: GB2312 (Chinese)
	public static final int  YY_ARB = 3;    // 3: Arab
	public static final int  YY_FRE = 4;    // 4: French
	public static final int  YY_JPS = 5;    // 5: Japanese
	public static final int  YY_KRA = 6;    // 6: Korea
	public static final int  YY_TKC = 7;    // 7: Turkic
	public static final int  YY_VTN = 8;    // 8: Vietnam
	public static final int  YY_IDS = 9;    // 9: Indonesia

	private static final int KEY_DATA_MAX = 255;
	private static final int EXPLAIN_DATA_MAX = 200*1024;
	private int mDictTypeAdded;			// 自己根据字典ID增加的一个变量
	/* dict file param */
	private int mDictId;				// 字典 ID
	private int mDictType;				// 字典类型
	private int mKeyCount;				// 单词的个数
	private int mIndexStartAddr;		// 索引地址
	private int mEncryptSwitch;			// 加密方案
	private int mKeyStartAddr;			// 单词地址表的开始地址
	private int mExplainStartAddr;		// 解释地址表的开始地址
	private int mKeyCodingType;			// 单词编码方式
	private int mExplainCodingType;		// 解释的编码方式
	private int mExplainCompressType;	// 解释的压缩方式
	private String mKeyCodeType;		// 单词编码方式

	private int mEngCount;				// 英文单词个数(生词库)
	private int mChiCount;				// 中文单词个数(生词库)
//	private byte[] mAddrBuf1;
//	private byte[] mAddrBuf2;
//	private byte[] mKeyBuf;
	RandomAccessFile mRandomAccessFile = null;	// 文件对象
//	private int mDictFileHandle;

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

	public static final String INTERNAL_DIR = "/system"/*Environment.getExternalStorageDirectory().toString() */+ "/readboy";

	public static final String[] DICT_LOCAL_NAME = {	// 字典存放位置
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
			"dictionary/wordsph.dat"

//		"/mnt/sdcard/dictionary/bihua.dct",
//		"/mnt/sdcard/dictionary/chengyu.dct",
//		"/mnt/sdcard/dictionary/dangdai.dct",
//		"/mnt/sdcard/dictionary/dongman.dct",
//		"/mnt/sdcard/dictionary/gdhy.dct",
//		"/mnt/sdcard/dictionary/hanying.dct",
//		"/mnt/sdcard/dictionary/hanyu.dct",
//		"/mnt/sdcard/dictionary/jmyinghan.dct",
//		"/mnt/sdcard/dictionary/xuesheng.dct",
//		"/mnt/sdcard/dictionary/yinghan.dct",
//		"/mnt/sdcard/dictionary/yingying.dct",
//		"/mnt/sdcard/dictionary/wordsph.dat"

//		"/mnt/vfs/Dict_Dct/bihua.dct",
//		"/mnt/vfs/Dict_Dct/chengyu.dct",
//		"/mnt/vfs/Dict_Dct/dangdai.dct",
//		"/mnt/vfs/Dict_Dct/dongman.dct",
//		"/mnt/vfs/Dict_Dct/gdhy.dct",
//		"/mnt/vfs/Dict_Dct/hanying.dct",
//		"/mnt/vfs/Dict_Dct/hanyu.dct",
//		"/mnt/vfs/Dict_Dct/jmyinghan.dct",
//		"/mnt/vfs/Dict_Dct/xuesheng.dct",
//		"/mnt/vfs/Dict_Dct/yinghan.dct",
//		"/mnt/vfs/Dict_Dct/yingying.dct",
//		"/mnt/vfs/Dict_Dct/audio.lib"
		};

	/*public static final String[] DICT_LOCAL_NAME = {	// 字典存放位置
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
			"/mnt/sdcard/readboy/dictionary/wordsph.dat"

//		"/mnt/sdcard/dictionary/bihua.dct",
//		"/mnt/sdcard/dictionary/chengyu.dct",
//		"/mnt/sdcard/dictionary/dangdai.dct",
//		"/mnt/sdcard/dictionary/dongman.dct",
//		"/mnt/sdcard/dictionary/gdhy.dct",
//		"/mnt/sdcard/dictionary/hanying.dct",
//		"/mnt/sdcard/dictionary/hanyu.dct",
//		"/mnt/sdcard/dictionary/jmyinghan.dct",
//		"/mnt/sdcard/dictionary/xuesheng.dct",
//		"/mnt/sdcard/dictionary/yinghan.dct",
//		"/mnt/sdcard/dictionary/yingying.dct",
//		"/mnt/sdcard/dictionary/wordsph.dat"

//		"/mnt/vfs/Dict_Dct/bihua.dct",
//		"/mnt/vfs/Dict_Dct/chengyu.dct",
//		"/mnt/vfs/Dict_Dct/dangdai.dct",
//		"/mnt/vfs/Dict_Dct/dongman.dct",
//		"/mnt/vfs/Dict_Dct/gdhy.dct",
//		"/mnt/vfs/Dict_Dct/hanying.dct",
//		"/mnt/vfs/Dict_Dct/hanyu.dct",
//		"/mnt/vfs/Dict_Dct/jmyinghan.dct",
//		"/mnt/vfs/Dict_Dct/xuesheng.dct",
//		"/mnt/vfs/Dict_Dct/yinghan.dct",
//		"/mnt/vfs/Dict_Dct/yingying.dct",
//		"/mnt/vfs/Dict_Dct/audio.lib"
	};*/

	/*public static final String[] DICT_LOCAL_NAME = {	// 字典存放位置
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
			"/mnt/sdcard2/readboy/dictionary/wordsph.dat"

//		"/mnt/sdcard/dictionary/bihua.dct",
//		"/mnt/sdcard/dictionary/chengyu.dct",
//		"/mnt/sdcard/dictionary/dangdai.dct",
//		"/mnt/sdcard/dictionary/dongman.dct",
//		"/mnt/sdcard/dictionary/gdhy.dct",
//		"/mnt/sdcard/dictionary/hanying.dct",
//		"/mnt/sdcard/dictionary/hanyu.dct",
//		"/mnt/sdcard/dictionary/jmyinghan.dct",
//		"/mnt/sdcard/dictionary/xuesheng.dct",
//		"/mnt/sdcard/dictionary/yinghan.dct",
//		"/mnt/sdcard/dictionary/yingying.dct",
//		"/mnt/sdcard/dictionary/wordsph.dat"

//		"/mnt/vfs/Dict_Dct/bihua.dct",
//		"/mnt/vfs/Dict_Dct/chengyu.dct",
//		"/mnt/vfs/Dict_Dct/dangdai.dct",
//		"/mnt/vfs/Dict_Dct/dongman.dct",
//		"/mnt/vfs/Dict_Dct/gdhy.dct",
//		"/mnt/vfs/Dict_Dct/hanying.dct",
//		"/mnt/vfs/Dict_Dct/hanyu.dct",
//		"/mnt/vfs/Dict_Dct/jmyinghan.dct",
//		"/mnt/vfs/Dict_Dct/xuesheng.dct",
//		"/mnt/vfs/Dict_Dct/yinghan.dct",
//		"/mnt/vfs/Dict_Dct/yingying.dct",
//		"/mnt/vfs/Dict_Dct/audio.lib"
	};*/

	public static final String NEWWORD_LOCAL_NAME	= INTERNAL_DIR + "/dictionary/newword.dct";
	public static final String BIHUA_LOCAL_NAME		= INTERNAL_DIR + "/dictionary/BiHuaDic.pin";
//	public static final String NEWWORD_LOCAL_NAME	= "/mnt/sdcard/dictionary/newword.dct";
//	public static final String BIHUA_LOCAL_NAME		= "/mnt/sdcard/dictionary/BiHuaDic.pin";
//	public static final String NEWWORD_LOCAL_NAME	= "/mnt/vfs/Dict_Dct/newword.dct";
//	public static final String BIHUA_LOCAL_NAME		= "/mnt/vfs/Dict_Dct/BiHuaDic.pin";

/*	public native int open(String fileName, int mode);

	public native int close(int handle);

	public native int seek(int handle, int offset, int origin);

	public native int read(int handle, byte[] data, int count, int offset);

	public native int write(int handle, byte[] data, int count);

	public native int getLength(int handle);

	public native int byteToInt(byte[] data, int i);

	public native short byteToShort(byte[] data, int i);

	public native static void initApp();*/
    /* open the RandomAccessFile with the file name.
    * <p>
    *
    * @param fileName: the name of the file need opened
    * 		     mode: the file access mode, either "r", "rw", "rws" or "rwd".
    * @return !=null means successful, ==0 means failed.
    */
	public boolean dictFileOpen(String fileName, String mode) {
		 try {
			mRandomAccessFile = new RandomAccessFile(fileName, mode);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

//		mDictFileHandle = open(fileName, 0);

		return true;
	}

    /* close the RandomAccessFile with the RandomAccessFile handle.
    * <p>
    *
    * @return ==true means successful, ==false means failed.
    */
	public boolean dictFileClosed() {
		if(mRandomAccessFile != null) {
			try {
				mRandomAccessFile.close();
				mRandomAccessFile = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
//		close(mDictFileHandle);

		return true;
	}

    /* check the file head.
    * <p>
    *
    * @return !=null means successful, ==null means failed.
    */
	public boolean dictFileInitial(int dictId)
	{
		byte buf[] = new byte[32];

		/*if(mRandomAccessFile == null) {
			return false;
		}
		try {
			mRandomAccessFile.seek(0);
			int count = mRandomAccessFile.read(buf);
			if(count != 32) {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}*/

		read( buf, buf.length, 0);

		if(byte2int(buf[0]) != 'c' || byte2int(buf[1]) != 'd') {
			return false;
		}

		int cycValueH16 = (int)(byte2int(buf[30]) << 0) + (int)(byte2int(buf[31]) << 8);
		int cycValueL16 = (int)(byte2int(buf[28]) << 0) + (int)(byte2int(buf[29]) << 8);
		if( dictFileGenerateCrc(buf, 28, cycValueH16, cycValueL16) == false ) {
			return false;
		}

		/* init dict param */
		mDictId = dictId;
		mDictTypeAdded = getDictTypeById(mDictId);
		mDictType = byte2int(buf[2]);
		mKeyCount = (byte2int(buf[3]) << 0) + (byte2int(buf[4]) << 8) + (byte2int(buf[5]) << 16) + (byte2int(buf[6]) << 24);
		mIndexStartAddr = byte2int(buf[7]);
		mEncryptSwitch = byte2int(buf[8]);
		mKeyStartAddr = (byte2int(buf[9]) << 0) + (byte2int(buf[10]) << 8) + (byte2int(buf[11]) << 16) + (byte2int(buf[12]) << 24);
		mExplainStartAddr = (byte2int(buf[13]) << 0) + (byte2int(buf[14]) << 8) + (byte2int(buf[15]) << 16) + (byte2int(buf[16]) << 24);
		if(mDictId == ID_SPEECHDICT) {
			mKeyCodingType = 0;
			mExplainCodingType = 0;
		} else {
			mKeyCodingType = byte2int(buf[17]);
			mExplainCodingType = byte2int(buf[18]);
		}
		mExplainCompressType = byte2int(buf[19]);
		mKeyCodeType = getDataCodeType(mKeyCodingType);
		buf = null;
//		mAddrBuf1 = new byte[4];
//		mAddrBuf2 = new byte[4];
//		mKeyBuf = new byte[KEY_DATA_MAX];

	    return true;
	}


    /* get the file descriptor.
    * <p>
    *
    * @return !=null means successful, ==null means failed.
    */
	public FileDescriptor getFileDescriptor() {
		FileDescriptor fileDescriptor = null;
		try {
			fileDescriptor = mRandomAccessFile.getFD();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fileDescriptor;
	}

    /* CYC checkout the file head.
    * 由于Java不支持无符号数，且机器的long 和 int 能表示的数据范围一样，
    * 校验码的4字节数据已经超出了int能表示的范围，因此将4字节拆分成高低双字节来校验。
    * 后面是C语言的校验代码
    * <p>
    *
    * @param data:		the data need checked
    * 		 length:	data length
    * 		 value:		cyc value
    *
    * @return == true means successful, ==false means failed.
    */
	public boolean dictFileGenerateCrc(byte[] data, int length, int valueH16, int valueL16 ) {
	    int crcH16, crcL16, ic;
	    int crc_table[] = { //CRC余式表
	        0x0000,0x1021,0x2042,0x3063,0x4084,0x50a5,0x60c6,0x70e7,
	        0x8108,0x9129,0xa14a,0xb16b,0xc18c,0xd1ad,0xe1ce,0xf1ef,
	    };
	    crcH16 = 0; crcL16 = 0;
	    int pos, temp;
	    for(pos = 0; pos < length; pos++)
	    {
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
	    return (boolean)(valueH16 == crcH16 && valueL16 == crcL16);
	  /*
	    BOOL DictFile_Generatecrc(UINT8 data, UINT32 Len,UINT32 CRCValue)
	    {
	        UINT32 crc;
	        UINT8  ic;
	        UINT32 crc_table[16] =
	        { //CRC余式表
	            0x0000,0x1021,0x2042,0x3063,0x4084,0x50a5,0x60c6,0x70e7,
	            0x8108,0x9129,0xa14a,0xb16b,0xc18c,0xd1ad,0xe1ce,0xf1ef,
	        };
	        crc = 0;
	        while(Len-- != 0)
	        {
	            ic = ((UINT8)(crc/0x100))/0x10;
	            crc <<= 4;
	            crc ^= crc_table[(ic ^ (data / 0xF)) & 0xF];
	            ic = ((UINT8)(crc/0x100))/0x10;
	            crc <<= 4;
	            crc ^= crc_table[ic ^ (data & 0xF)];
	            data++;
	        }
	        return (BOOL)(CRCValue == crc);
	    }
	    */
	}

    /* get CYC value
    * 由于Java不支持无符号数，且机器的long 和 int 能表示的数据范围一样，
    * 校验码的4字节数据已经超出了int能表示的范围，因此将4字节拆分成高低双字节来校验。
    * <p>
    *
    * @param data:		the data need checked
    * 		 length:	data length
    * 		 value:		cyc value
    *
    * @return the cyc value.
    */
	public int dictFileGetGenerateCrc(byte[] data, int length) {
	    int crcH16, crcL16, ic;
	    int crc_table[] = { //CRC余式表
	        0x0000,0x1021,0x2042,0x3063,0x4084,0x50a5,0x60c6,0x70e7,
	        0x8108,0x9129,0xa14a,0xb16b,0xc18c,0xd1ad,0xe1ce,0xf1ef,
	    };
	    crcH16 = 0; crcL16 = 0;
	    int pos, temp;
	    for(pos = 0; pos < length; pos++)
	    {
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
	    return (crcH16 << 16) + crcL16;
	}

    /* get the dict addrNumber by the key string value.
    * <p>
    *
    * @ param comparaData: the key string
    *
    * @return the addrNumber(notice that the high byte is ff means the index of the data just similar with the compared data).
    */
	public int dictFileGetDictDataWithCMP(String comparaData) {
		int top, bottom;
		int cmpValue = 0, curIndex = 0;
		int addrNumber = -1;

		if(comparaData == null) {
			return -1;
		}
		/* 单词 整形 */
		String srcData = comparaData;
		if(mDictId == ID_BHDICT) {	// 笔画字典只要第一个字就行
			srcData = srcData.substring(0, 1);
		}
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
		//System.gc();
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
			addrNumber = dictFileGetAddrNumber(curIndex);
			while(cmpValue == 0) {
				if(addrNumber == 0) {
					return addrNumber;
				}
				scrData = dictFileGetDictKeyByAddressNumber(--addrNumber);
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

		addrNumber = dictFileGetAddrNumber(curIndex);
		//System.gc();
//		if(curCount==srcData.length() && mDictId==ID_CYDICT) {
//			return addrNumber;
//		}
		// 中文长度相等的算完全匹配
		if(mKeyCodingType==YY_CHS && curCount==comparaData.length()) {
			return addrNumber;
		}
		if(mKeyCodingType==YY_ENG || curCount>= 1 && mKeyCodingType==YY_CHS) {
			return addrNumber|0xff000000;
		}

		return -1;//addrNumber|0xff000000;
	}

    /* get the dict addrNumber by the key string value.
    * <p>
    *
    * @ param comparaData: the key string
    *
    * @return the addrNumber(notice that the high byte is ff means the index of the data just similar with the compared data).
    */
	public int dictFileGetDictDataWithFullCMP(String comparaData) {
		int top, bottom;
		int cmpValue = 0, curIndex = 0;
		int addrNumber = -1;

		if(comparaData == null) {
			return -1;
		}
		/* 单词 整形 */
		String srcData = comparaData;
		if(mDictId == ID_BHDICT) {	// 笔画字典只要第一个字就行
			srcData = srcData.substring(0, 1);
		}
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
		//System.gc();
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
			addrNumber = dictFileGetAddrNumber(curIndex);
			while(cmpValue == 0) {
				if(addrNumber == 0) {
					return addrNumber;
				}
				scrData = dictFileGetDictKeyByAddressNumber(--addrNumber);
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

		addrNumber = dictFileGetAddrNumber(curIndex);
		//System.gc();
//		if(curCount==srcData.length() && mDictId==ID_CYDICT) {
//			return addrNumber;
//		}
		// 中文长度相等的算完全匹配
		if(mKeyCodingType==YY_ENG || curCount>= 1 && mKeyCodingType==YY_CHS) {
			return addrNumber|0xff000000;
		}

		return -1;//addrNumber|0xff000000;
	}

    /* get the dict data.
    * <p>
    *
    * @ param startAddr: the data start address
    * 		       data: the data buffer
    *
    * @return the data length.
    */
	public int dictFileGetDictData(int startAddr, byte[] data) {
		if(data == null) {
			return -1;
		}

		int ret;
		/*try {
			mRandomAccessFile.seek(startAddr);
			ret = mRandomAccessFile.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}*/

		ret = read( data, data.length, startAddr);

		return ret;
	}


    /* get the address number.
    * <p>
    *
    * @ param index: the index number
    *
    * @return the address number.
    */
	public int dictFileGetAddrNumber(int index) {
		if(mIndexStartAddr == 0) {
			return index;	//mIndexStartAddr
		} else {
			int addrNumber;
			byte[] data = new byte[4];
			/*try {
				mRandomAccessFile.seek(mIndexStartAddr + index*4);
				mRandomAccessFile.read(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			read( data, data.length, mIndexStartAddr + index*4);
			addrNumber = (byte2int(data[0])) + (byte2int(data[1]) << 8)
	    			+ (byte2int(data[2]) << 16) + (byte2int(data[3]) << 24);
			return addrNumber;
		}
	}

    /* get the dict key content.
    * <p>
    *
    * @ param index: the key index
    *
    * @return the data.
    */
	public String dictFileGetDictKeyByIndex(int index) {
		byte[] data = null;
		if(index < 0) {
			return null;
		}
		int addrNumber = dictFileGetAddrNumber(index);
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


    /* get the dict key content.
    * <p>
    *
    * @ param index: the key index
    *
    * @return the data.
    */
	public String dictFileGetDictKeyByAddressNumber(int addrNumber) {
		byte[] data;
		if(addrNumber < 0) {
			return null;
		}

		data = dictFileGetDictKeyByteByAddressNumber(addrNumber);

		String dataText = null;
		if(data != null) {
			try {
				dataText = new String(data, getDataCodeType(2));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.w(TAG, "******************** load dict key end address failed! *************************");
		}

		return dataText;
	}


    /* get the dict key content.
    * <p>
    *
    * @ param index: the key index
    *
    * @return the data.
    */
	public byte[] dictFileGetDictKeyByteByAddressNumber(int addrNumber) {
		byte[] data = null;
		if(addrNumber < 0) {
			return null;
		}
    	int indexAddr = mKeyStartAddr + addrNumber * 4;
    	byte[] indexData = new byte[4];
    	// 单词地址的位置
    	if(dictFileGetDictData(indexAddr, indexData) != 4) {
    		Log.w(TAG, "******************** load dict key start address failed! *************************");
    	}
    	// 当前单词的位置
    	int startKeyAddr = (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
    			+ (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
    	indexAddr = mKeyStartAddr + (addrNumber + 1) * 4;
    	if(dictFileGetDictData(indexAddr, indexData) != 4) {
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
	    		if(dictFileGetEncryptDictData(startKeyAddr, data) != length) {
	        		Log.w(TAG, "******************** load dict key data failed! *************************");
	        	}
	    	} else {
	        	if(dictFileGetDictData(startKeyAddr, data) != length) {
	        		Log.w(TAG, "******************** load dict key data failed! *************************");
	        	}
	    	}
    	}

    	return data;
	}


    /* get the dict data.
    * <p>
    *
    * @param index: the data index
    *
    * @return dict data.
    */
	public byte[] loadCurExplain(int addrNumber) {
    	byte[] explainData = null;

    	byte[] indexData = new byte[4];
		int indexAddr = mExplainStartAddr + addrNumber * 4;

		// 解释地址的位置
    	if(dictFileGetDictData(indexAddr, indexData) != 4) {
    		Log.w(TAG, "******************** load dict explain start address failed! *************************");
    	}
    	// 当前解释的位置
    	int startExplain = (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
    			+ (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
    	// 下一解释地址的位置
    	indexAddr = mExplainStartAddr + (addrNumber + 1) * 4;
    	if(dictFileGetDictData(indexAddr, indexData) != 4) {
    		Log.w(TAG, "******************** load dict explain end address failed! *************************");
    	}
    	// 下一解释的位置
    	int endExplain =  (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
    			+ (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
    	explainData = new byte[endExplain - startExplain-1];    	// 解释内容
    	if((mEncryptSwitch & 0xF) != 0) {			// 解释有加密
    		if(dictFileGetEncryptDictData(startExplain, explainData) != endExplain - startExplain-1) {
        		Log.w(TAG, "******************** load dict explain data failed! *************************");
        	}
    	} else {							// 解释无加密
        	if(dictFileGetDictData(startExplain, explainData) != endExplain - startExplain-1) {
        		Log.w(TAG, "******************** load dict explain data failed! *************************");
        	}
    	}

    	return explainData;
	}

    /* get the dong man dict data.
    * <p>
    *
    * @ param addresNumber: the data address number
    * 		       gifData: the data buffer of the gif
    * 				  data: the data buffer of the explain
    * @return the gifsize(L16:width, H16:height).
    */
	public DongManParam dictFileGetDMDictData(int addresNumber) {
		DongManParam dongManParam = new DongManParam();
		byte indexData[] = new byte[4];

		try {
			int indexAddr = mExplainStartAddr + addresNumber * 4;
	    	// 解释地址的位置
	    	if(dictFileGetDictData(indexAddr, indexData) != 4) {
	    		Log.w(TAG, "******************** load dict explain start address failed! *************************");
	    	}
	    	// 当前解释的位置
	    	int startExplain = (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
	    			+ (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
	    	// 下一解释地址的位置
	    	indexAddr = mExplainStartAddr + (addresNumber + 1) * 4;
	    	if(dictFileGetDictData(indexAddr, indexData) != 4) {
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

    /* get the dict data.
    * <p>
    *
    * @ param startAddr: the data start address
    * 		       data: the data buffer
    * 			  count: the count of the data want to get
    *
    * @return the data length.
    */
	public int dictFileGetDictData(int startAddr, byte[] data, int count) {
		if(data == null || count > data.length) {
			return -1;
		}

		int ret;
		/*try {
			mRandomAccessFile.seek(startAddr);
			ret = mRandomAccessFile.read(data, 0, count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}*/
		ret = read( data, count, startAddr);

		return ret;
	}

    /* get the encrypt dict data.
    * <p>
    *
    * @ param startAddr: the data start address
    * 		       data: the data buffer
    *
    * @return the data length.
    */
	public int dictFileGetEncryptDictData(int startAddr, byte[] data) {
		if(data == null) {
			return -1;
		}

		int ret = dictFileGetDictData(startAddr, data);

	    int[] chaos = {0xF4,0xB5,0x13,0x02,0x3D,0xCA,0x49,0xAE,0x15,0xCC,0x41};
	    int[] sample = {'F','o','r','a','i','n','d','y','~','S','a','r','a','h',':'};
	    int chaosI, sampleI;
	    for(int i = 0; i < ret; i++)
	    {
	    	chaosI = chaos[i % (chaos.length)];
	    	sampleI = sample[i % (sample.length)];
	    	data[i] = (byte) (byte2int(data[i]) ^ chaosI ^ sampleI);
	    }

		return ret;
	}


    /* get the encrypt dict data.
    * <p>
    *
    * @ param startAddr: the data start address
    * 		       data: the data buffer
    *
    * @return the data length.
    */
	public int dictFileGetEncryptDictData(int startAddr, byte[] data, int count) {
		if(data == null || count > data.length) {
			return -1;
		}

		int ret = dictFileGetDictData(startAddr, data, count);

	    int[] chaos = {0xF4,0xB5,0x13,0x02,0x3D,0xCA,0x49,0xAE,0x15,0xCC,0x41};
	    int[] sample = {'F','o','r','a','i','n','d','y','~','S','a','r','a','h',':'};
	    int chaosI, sampleI;
	    for(int i = 0; i < ret - 1; i++)
	    {
	    	chaosI = chaos[i % (chaos.length)];
	    	sampleI = sample[i % (sample.length)];
	    	data[i] = (byte) (byte2int(data[i]) ^ chaosI ^ sampleI);
	    }

		return ret;
	}

	private int read(byte[] data, int count, int offset) {
		int ret = 0;
		try {
			if(mRandomAccessFile != null) {
				mRandomAccessFile.seek(offset);
				ret = mRandomAccessFile.read(data, 0, count);
			}
		} catch (IOException e) {
			Log.w(TAG, "read file error: "+e.getMessage());
		}

		return ret;
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
	    	if(mDictTypeAdded == YY_ENG) {
	    		if(data.charAt(endIndex) >= 0x80) {
	    			break;
	    		}
	    	}
    		endIndex++;
	    }

    	if(mDictTypeAdded == YY_ENG)
		{
		    int i;
		    String filteChar;
//		    String s1 = " -;*.{}\'\"?/()![]<>0123456789&|^%$#@~`+=";
//		    String s2 = " -;*.{}\'\"?/()![]<>&|^%$#@~`+=";
		    String s1 = " -;*.{}\'\"?/()![]<>0123456789&|^%$#@~`+=";
		    String s2 = " -;*.{}\'\"?/()![]<>&|^%$#@~`+=";
//            String s3 = " ;*.{}\'\"?/()![]<>&|^%$#@~`+=";
		    /*if(mDictId == ID_NEWWORD) {
		    	filteChar = s2;
		    } else */{
		    	filteChar = s1;
		    }

		    while(curIndex < endIndex) {
		        //先滤除字符
		        for (i = 0; i < filteChar.length(); i++) {
		            if(data.charAt(curIndex) == filteChar.charAt(i)) {
		            	curIndex++;
		            	if(curIndex >= endIndex) {
		            		return sb.toString();
		            	}
		                i = 0;
		            }
		            if(data.charAt(curIndex) ==','||data.charAt(curIndex) ==0x0d||data.charAt(curIndex) ==0x0a) {
		            	if(sb.length()!=0) {
		            		return sb.toString();
		            	}
		            	curIndex++;
		            	if(curIndex >= endIndex) {
		            		return sb.toString();
		            	}
		                i = 0;
		            }
		            if(data.charAt(curIndex) == 0) {
		            	return sb.toString();
		            }
		        }
		        if(data.charAt(curIndex) == ' ') {
		            if(data.charAt(curIndex) == ' ') {
		            	curIndex++;
		                continue;
		            } else if(data.charAt(curIndex) == '\0') {
							return sb.toString();
		            }
		        }

		        if(data.charAt(curIndex) > 0x80) {
		            curIndex += 2;
		            continue;
		        }
		        if(data.charAt(curIndex) < 0x20 || data.charAt(curIndex) > 'z') {
		        	curIndex++;
		            continue;
		        }

		        //然后把字符转成小写
		        if(data.charAt(curIndex) >= 'A' && data.charAt(curIndex) <= 'Z') {
		        	sb.append((char)(data.charAt(curIndex) + 'a' - 'A'));
		        } else {
		        	sb.append((char)data.charAt(curIndex));
		        }
		        curIndex++;
		    }
		}
		else if(mDictTypeAdded == YY_CHS)
		{
		    boolean bHaveChineseHead = false;
		    String s1 = "。，、；：？！…—‘’“”～∶〔〕〈〉《》「」『』．〖〗【】（）［］｛｝";
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

	private int getDictTypeById(int id) {
		switch(id) {
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
		switch(type) {
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

    /* get the dict type.
    * <p>
    *
    *
    * @return the dict type.
    */
	public int dictFileGetDictType() {
		return mDictType;
	}

    /* get the dict key count.
    * <p>
    *
    *
    * @return the dict key count.
    */
	public int dictFileGetKeycount() {
		return mKeyCount;
	}


    /* get the dict key count.
    * <p>
    *
    *
    * @return the dict key count.
    */
	public int dictFileGetEngKeycount() {
		return mEngCount;
	}

    /* get the dict key count.
    * <p>
    *
    *
    * @return the dict key count.
    */
	public int dictFileGetChiKeycount() {
		return mChiCount;
	}

    /* get the dict index start address.
    * <p>
    *
    *
    * @return the dict index start address.
    */
	public int dictFileGetIndexStartAddr() {
		return mIndexStartAddr;
	}

    /* get the dict encrypt switch.
    * <p>
    *
    *
    * @return the dict encrypt switch.
    */
	public int dictFileGetEncryptSwitch() {
		return mEncryptSwitch;
	}

	/* get the dict key start address.
    * <p>
    *
    *
    * @return the dict key start address.
    */
	public int dictFileGetKeyStartAddr() {
		return mKeyStartAddr;
	}

    /* get the dict explain start address.
    * <p>
    *
    *
    * @return the dict explain start address.
    */
	public int dictFileGetExplainStartAddr() {
		return mExplainStartAddr;
	}

    /* get the dict key coding type.
    * <p>
    *
    *
    * @return the dict key coding type.
    */
	public int dictFileGetKeyCodingType() {
		return mKeyCodingType;
	}

	/* get the dict explain coding type.
    * <p>
    *
    *
    * @return the dict explain coding type.
    */
	public int dictFileGetExplainCodingType() {
		return mExplainCodingType;
	}

    /* get the dict explain compress type.
    * <p>
    *
    *
    * @return the dict explain compress type.
    */
	public int dictFileGetExplainCompressType() {
		return mExplainCompressType;
	}


    /* get the dict explain compress type.
    * <p>
    *
    *
    * @return the dict explain compress type.
    */
	public int dictFileGetDictId() {
		return mDictId;
	}

	private int byte2int(byte b) {
		if(b >= 0) {
			return (int)b;
		} else {
			return 0x100 + b;
		}
	}


//	/************************************************************************************************************/
//	/**************************************      下面是生词库文件操作部分           *************************************/
//	/************************************************************************************************************/
//	
//	
//	private boolean newwordCreate(File f) {
//		byte[] data = new byte[FILE_HEAD_SIZE];
//		try {
//			FileOutputStream fout = new FileOutputStream(f);
//			data[0] = 'c';
//			data[1] = 'd';
//			int cycValue = dictFileGetGenerateCrc(data, 28);
//			data[FILE_HEAD_SIZE-4] = (byte) (cycValue & 0xff);
//			data[FILE_HEAD_SIZE-3] = (byte) ((cycValue >> 8) & 0xff);
//			data[FILE_HEAD_SIZE-2] = (byte) ((cycValue >> 16) & 0xff);
//			data[FILE_HEAD_SIZE-1] = (byte) (cycValue >> 24);
//			try {
//				fout.write(data);
//				data = new byte[NEWWORD_TOTAL_SIZE];
//				Arrays.fill(data, (byte) 0);
//				fout.write(data);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				return false;
//			}
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//	
//	public boolean newwordOpen(String path) {
//		File f = new File(path);
//		if(!f.exists()) {
//			if(!newwordCreate(f)) {
//				return false;
//			}
//			f.setReadable(true, false);
//			f.setWritable(true, false);
//		}
//
//		try {
//			mRandomAccessFile = new RandomAccessFile(path, "rws");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		
//		return true;
//	}
//	
//    /* check the file head.
//    * <p>
//    * 
//    * @return !=null means successful, ==null means failed.
//    */ 
//	public boolean newwordFileInitial(int dictId)
//	{
//		byte buf[] = new byte[32];
//		
//		if(mRandomAccessFile == null) {
//			return false;
//		}
//		try {
//			mRandomAccessFile.seek(0);
//			int count = mRandomAccessFile.read(buf);
//			if(count != 32) {
//				return false;
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		
//		if(byte2int(buf[0]) != 'c' || byte2int(buf[1]) != 'd') {
//			return false;
//		}
//		
//		int cycValueH16 = (int)(byte2int(buf[30]) << 0) + (int)(byte2int(buf[31]) << 8);
//		int cycValueL16 = (int)(byte2int(buf[28]) << 0) + (int)(byte2int(buf[29]) << 8);
//		if( dictFileGenerateCrc(buf, 28, cycValueH16, cycValueL16) == false ) {
//			return false;
//		}
//		
//		mDictId = dictId;
//		mEngCount = (byte2int(buf[3]) << 0) + (byte2int(buf[4]) << 8) + (byte2int(buf[5]) << 16) + (byte2int(buf[6]) << 24);
//		mChiCount = (byte2int(buf[7]) << 0) + (byte2int(buf[8]) << 8) + (byte2int(buf[9]) << 16) + (byte2int(buf[10]) << 24);
//		
//		return true;
//	}
//	
//	/* @get newword item by index
//	 * 
//	 * @the index in dict
//	 * @type:  0:eng	1:chi
//	 * 
//	 * @the newword item
//	 */
//	public NewwordParam newwordGetItemByIndex(int index, int type) {
//		if(type != 0 && type != 1 || index < 0) {
//			return null;
//		}
//		
//		if(type == 0 && index >= mEngCount || type == 1 && index >= mChiCount) {
//			return null;
//		}
//		
//		NewwordParam parm = new NewwordParam();
//		int addr = FILE_HEAD_SIZE + index*NEWWORD_RECORD_SIZE;
//		if(type == 1) {
//			addr += NEWWORD_TOTAL_SIZE/2;
//		}
//		byte[] data = new byte[NEWWORD_RECORD_SIZE];
//		try {
//			mRandomAccessFile.seek(addr);
//			mRandomAccessFile.read(data);
//			parm.dictId = byte2int(data[0]);
//			parm.addrNumber = (byte2int(data[1]) << 0) + (byte2int(data[2]) << 8) + (byte2int(data[3]) << 16) + (byte2int(data[4]) << 24);
//			parm.readEnable = data[5]==0 ? false : true;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//		
//		return parm;
//	}
//	
//	synchronized public int addNewword(NewwordParam param, int position) {
//		int type = getDictTypeById(param.dictId);
//		if(type != YY_ENG && type != YY_CHS) {
//			return 1;
//		}
//		
//		int addr, count, countPosition;
//		if(type == YY_ENG) {
//			if(mEngCount >= NEWWORD_TOTAL_COUNT/2) {
//				return 2;
//			}
//			count = mEngCount + 1;
//			countPosition = 3;
//			addr = FILE_HEAD_SIZE + position*NEWWORD_RECORD_SIZE;
//		} else {
//			if(mChiCount >= NEWWORD_TOTAL_COUNT/2) {
//				return 2;
//			}
//			count = mChiCount + 1;
//			countPosition = 7;
//			addr = FILE_HEAD_SIZE + NEWWORD_TOTAL_SIZE/2 + position*NEWWORD_RECORD_SIZE;
//		}
//		
//		byte[] data = new byte[FILE_HEAD_SIZE+NEWWORD_TOTAL_SIZE];
//		try {
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.read(data, 0, addr);
//
//			data[countPosition+0] = (byte)((count>>0) & 0xff);
//			data[countPosition+1] = (byte)((count>>8) & 0xff);
//			data[countPosition+2] = (byte)((count>>16) & 0xff);
//			data[countPosition+3] = (byte)((count>>24) & 0xff);
//			data[addr+0] = (byte)param.dictId;
//			data[addr+1] = (byte)((param.addrNumber>>0) & 0xff);
//			data[addr+2] = (byte)((param.addrNumber>>8) & 0xff);
//			data[addr+3] = (byte)((param.addrNumber>>16) & 0xff);
//			data[addr+4] = (byte)((param.addrNumber>>24) & 0xff);
//			data[addr+5] = (byte) (param.readEnable==true ? 1 : 0);
//			if(count-1 > position) {
//				mRandomAccessFile.read(data, 
//						addr + NEWWORD_RECORD_SIZE, 
//						(count-1-position)*NEWWORD_RECORD_SIZE);
//			}
//			if(type == YY_ENG) {
//				mEngCount++;
//				mRandomAccessFile.seek(FILE_HEAD_SIZE + NEWWORD_TOTAL_SIZE/2);
//				mRandomAccessFile.read(data, 
//						FILE_HEAD_SIZE + NEWWORD_TOTAL_SIZE/2, 
//						NEWWORD_TOTAL_SIZE/2);
//			} else {
//				mChiCount++;
//			}
//			int cycValue = dictFileGetGenerateCrc(data, 28);
//			data[FILE_HEAD_SIZE-4] = (byte) (cycValue & 0xff);
//			data[FILE_HEAD_SIZE-3] = (byte) ((cycValue >> 8) & 0xff);
//			data[FILE_HEAD_SIZE-2] = (byte) ((cycValue >> 16) & 0xff);
//			data[FILE_HEAD_SIZE-1] = (byte) (cycValue >> 24);
//
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.write(data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return 3;
//		}
//		
//		return 0;
//	}
//	
//	/**
//	 * 
//	 * @param position: the item position will be remove
//	 * @param type: 0:Eng	1:Chi
//	 * @return
//	 */
//	public int removeNewword(int position, int type) {
//		if(type != 0 && type != 1) {
//			return 1;
//		}
//		
//		int addr, count, countPosition;
//		if(type == 0) {
//			if(mEngCount <= 0) {
//				return 2;
//			}
//			count = mEngCount - 1;
//			countPosition = 3;
//			addr = FILE_HEAD_SIZE + position*NEWWORD_RECORD_SIZE;
//		} else {
//			if(mChiCount <= 0) {
//				return 2;
//			}
//			count = mChiCount - 1;
//			countPosition = 7;
//			addr = FILE_HEAD_SIZE + NEWWORD_TOTAL_SIZE/2 + position*NEWWORD_RECORD_SIZE;
//		}
//		
//		byte[] data = new byte[FILE_HEAD_SIZE+NEWWORD_TOTAL_SIZE];
//		try {
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.read(data, 0, addr);
//
//			data[countPosition+0] = (byte)((count>>0) & 0xff);
//			data[countPosition+1] = (byte)((count>>8) & 0xff);
//			data[countPosition+2] = (byte)((count>>16) & 0xff);
//			data[countPosition+3] = (byte)((count>>24) & 0xff);
//			if(count > position) {
//				mRandomAccessFile.seek(addr+NEWWORD_RECORD_SIZE);
//				mRandomAccessFile.read(data, addr, (count-position)*NEWWORD_RECORD_SIZE);
//			}
//			if(type == 0) {
//				mEngCount = count;
//				mRandomAccessFile.seek(FILE_HEAD_SIZE + NEWWORD_TOTAL_SIZE/2);
//				mRandomAccessFile.read(data, 
//						FILE_HEAD_SIZE + NEWWORD_TOTAL_SIZE/2, 
//						NEWWORD_TOTAL_SIZE/2);
//			} else {
//				mChiCount = count;
//			}
//			int cycValue = dictFileGetGenerateCrc(data, 28);
//			data[FILE_HEAD_SIZE-4] = (byte) (cycValue & 0xff);
//			data[FILE_HEAD_SIZE-3] = (byte) ((cycValue >> 8) & 0xff);
//			data[FILE_HEAD_SIZE-2] = (byte) ((cycValue >> 16) & 0xff);
//			data[FILE_HEAD_SIZE-1] = (byte) (cycValue >> 24);
//
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.write(data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return 3;
//		}
//		
//		return 0;
//	}
//	
//	/**
//	 * 
//	 * @param type :the type of new word will be remove
//	 * @return
//	 */
//	public int clearNewword(int type) {
//		if(type != 0 && type != 1) {
//			return 1;
//		}
//		
//		int addr, count, countPosition;
//		
//		count = 0;
//		if(type == 0) {
//			count = mChiCount;
//			countPosition = 3;
//			addr = FILE_HEAD_SIZE + NEWWORD_TOTAL_SIZE/2;
//		} else {
//			count = mEngCount;
//			countPosition = 7;
//			addr = FILE_HEAD_SIZE;
//		}
//		
//		byte[] data = new byte[FILE_HEAD_SIZE+NEWWORD_TOTAL_SIZE];
//		try {
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.read(data, 0, FILE_HEAD_SIZE);
//
//			data[countPosition+0] = 0;
//			data[countPosition+1] = 0;
//			data[countPosition+2] = 0;
//			data[countPosition+3] = 0;
//			mRandomAccessFile.seek(addr);
//			mRandomAccessFile.read(data, addr, count*NEWWORD_RECORD_SIZE);
//			if(type == 0) {
//				mEngCount = 0;
//			} else {
//				mChiCount = 0;
//			}
//			int cycValue = dictFileGetGenerateCrc(data, 28);
//			data[FILE_HEAD_SIZE-4] = (byte) (cycValue & 0xff);
//			data[FILE_HEAD_SIZE-3] = (byte) ((cycValue >> 8) & 0xff);
//			data[FILE_HEAD_SIZE-2] = (byte) ((cycValue >> 16) & 0xff);
//			data[FILE_HEAD_SIZE-1] = (byte) (cycValue >> 24);
//
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.write(data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return 3;
//		}
//		
//		return 0;
//	}
//	
//	/*public int saveNewword(List<NewwordItem> list, int type) {
//		if(type != 0 && type != 1) {
//			return 1;
//		}
//		
//		int addr, countPosition;
//		int count = list.size();
//		if(type == 0) {
//			if(count > NEWWORD_TOTAL_SIZE/2) {
//				return 2;
//			}
//			countPosition = 3;
//			addr = FILE_HEAD_SIZE;
//		} else {
//			if(count > NEWWORD_TOTAL_SIZE/2) {
//				return 3;
//			}
//			countPosition = 7;
//			addr = FILE_HEAD_SIZE + NEWWORD_TOTAL_SIZE/2;
//		}
//		
//		byte[] data = new byte[FILE_HEAD_SIZE+NEWWORD_TOTAL_SIZE];
//		try {
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.read(data);
//
//			data[countPosition+0] = (byte)((count>>0) & 0xff);
//			data[countPosition+1] = (byte)((count>>8) & 0xff);
//			data[countPosition+2] = (byte)((count>>16) & 0xff);
//			data[countPosition+3] = (byte)((count>>24) & 0xff);
//			int p, id, addrNumber;
//			for(int i = 0;i < list.size();i++) {
//				p = addr+i*NEWWORD_RECORD_SIZE;
//				id = list.get(i).getDictId();
//				addrNumber = list.get(i).getAddrNumber();
//				data[p+0] = (byte)id;
//				data[p+1] = (byte)((addrNumber>>0) & 0xff);
//				data[p+2] = (byte)((addrNumber>>8) & 0xff);
//				data[p+3] = (byte)((addrNumber>>16) & 0xff);
//				data[p+4] = (byte)((addrNumber>>24) & 0xff);
//			}
//			if(type == YY_ENG) {
//				mEngCount++;
//			} else {
//				mChiCount++;
//			}
//			int cycValue = dictFileGetGenerateCrc(data, 28);
//			data[FILE_HEAD_SIZE-4] = (byte) (cycValue & 0xff);
//			data[FILE_HEAD_SIZE-3] = (byte) ((cycValue >> 8) & 0xff);
//			data[FILE_HEAD_SIZE-2] = (byte) ((cycValue >> 16) & 0xff);
//			data[FILE_HEAD_SIZE-1] = (byte) (cycValue >> 24);
//
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.write(data);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return 4;
//		}
//		
//		return 0;
//	}*/
//	
//	/**
//	 * 
//	 * @param type 0:eng	1:chi
//	 */
//	public void setStartNewwordType(int type) {
//		try {
//			byte[] data = new byte[FILE_HEAD_SIZE];
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.read(data);
//			data[11] = (byte) type;
//			int cycValue = dictFileGetGenerateCrc(data, 28);
//			data[FILE_HEAD_SIZE-4] = (byte) (cycValue & 0xff);
//			data[FILE_HEAD_SIZE-3] = (byte) ((cycValue >> 8) & 0xff);
//			data[FILE_HEAD_SIZE-2] = (byte) ((cycValue >> 16) & 0xff);
//			data[FILE_HEAD_SIZE-1] = (byte) (cycValue >> 24);
//
//			mRandomAccessFile.seek(0);
//			mRandomAccessFile.write(data);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * 
//	 *
//	 */
//	public int getStartNewwordType() {
//		int type = 0;
//		
//		try {
//			mRandomAccessFile.seek(11);
//			type = mRandomAccessFile.readByte();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return type;
//	}	
}