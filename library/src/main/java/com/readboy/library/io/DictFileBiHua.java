/**
 *
 * 
 */
package com.readboy.library.io;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DictFileBiHua {
	private static final int FIRST_MENU_NUM = 80;		// 一级目录 数量
	private static final int SECOND_MENU_NUM = 95;		// 二级目录数量

	private RandomAccessFile mRandomAccessFile = null;	// 文件对象
	private int[] HanZiIndex = new int[FIRST_MENU_NUM];
	private int	mFirstMenuIdx, mSecondMenuIdx;
	private int mAllcardAddr;

	public DictFileBiHua(String fileName, String mode) {
		byte[] data = new byte[3];
		try {
			mRandomAccessFile = new RandomAccessFile(fileName, mode);
			mRandomAccessFile.seek(0x80);
			mRandomAccessFile.read(data);
			mAllcardAddr = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
			mRandomAccessFile.seek(0x80+3);
			mRandomAccessFile.read(data);
			int offset = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
			mRandomAccessFile.seek(offset);
			mRandomAccessFile.read(data);
			int startAddr = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
			for(int i = 1;i < FIRST_MENU_NUM;i++) {
				mRandomAccessFile.seek(0x80+3);
				mRandomAccessFile.read(data);
				offset = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
				mRandomAccessFile.seek(offset + i*3);
				mRandomAccessFile.read(data);
				offset = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
				HanZiIndex[i-1] = (offset - startAddr) / 3;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			data = null;
		}
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

				HanZiIndex = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public FileDescriptor getFileDescriptor() {
		try {
			return mRandomAccessFile.getFD();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public byte[] getFileData(int start, int length) {
		byte[] data = new byte[length];
		try {
			synchronized (mRandomAccessFile) {
				mRandomAccessFile.seek(start);
				mRandomAccessFile.read(data);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

	public WordInfo getWordInfo(int index) {
		WordInfo word = new WordInfo();
		if( index < 0) {
			return null;
		}

		for(int i = 0;i < FIRST_MENU_NUM;i++) {
			if(index < HanZiIndex[i]) {
				mFirstMenuIdx = i;
				if(i == 0) {
					mSecondMenuIdx = index;
				} else {
					mSecondMenuIdx = index - HanZiIndex[i-1];
				}
				break;
			}
		}

		try {
			synchronized (mRandomAccessFile) {
				byte[] data = new byte[4];
				// 字的发音
				mRandomAccessFile.seek(0x80 + 3);
				mRandomAccessFile.read(data, 0, 3);
				int offset = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
				mRandomAccessFile.seek(offset + mFirstMenuIdx*3);
				mRandomAccessFile.read(data, 0, 3);
				offset = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
				mRandomAccessFile.seek(offset + mSecondMenuIdx*3);
				mRandomAccessFile.read(data, 0, 3);			// SpeechOffset
				offset = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
				offset += 3;
				word.mSoundOffest = offset;
				getSoundInfo(word, offset, false);
				int bihuaOffset = offset + 3;
				// 字的笔画信息
				mRandomAccessFile.seek(bihuaOffset+1);
				mRandomAccessFile.read(data, 0, 3);
				int idx = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16);
				idx = (idx>>8)*0xc8 + (idx&0xff);
				mRandomAccessFile.seek(mAllcardAddr + idx*4);
				mRandomAccessFile.read(data);
				word.mStrokesOffest = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16) + (byte2int(data[3]) << 24);
				int length = getBiHuaDataLength(mAllcardAddr + word.mStrokesOffest);
				word.mbiHuaInfo = new byte[length];
				mRandomAccessFile.seek(mAllcardAddr + word.mStrokesOffest);
				mRandomAccessFile.read(word.mbiHuaInfo);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return word;
	}

	// 得到声音信息
	public void getSoundInfo(WordInfo word, int offset, boolean flag) {
		int position = offset;
		byte[] data = new byte[4];
		int idx;

		try {
			synchronized (mRandomAccessFile) {
				if(flag == false) {
					mRandomAccessFile.seek(position);
					mRandomAccessFile.read(data, 0, 2);
					position = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8);
				} else {
					position -= 1;
				}
				idx = (position >> 8) * 0xc8 + (position & 0XFF);
				mRandomAccessFile.seek(mAllcardAddr + idx * 4);
				mRandomAccessFile.read(data);
				int iteml = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16) + (byte2int(data[3]) << 24);
				mRandomAccessFile.read(data);
				word.mSoundLength = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16) + (byte2int(data[3]) << 24);
				word.mSoundLength -= iteml;
				word.mSoundStartAddr = mAllcardAddr + iteml;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			data = null;
		}
	}

	// 得到笔画数据的长度
	private int getBiHuaDataLength(int addr) {
		byte data[] = new byte[4];
		int length = 0;
		try {
			synchronized (mRandomAccessFile) {
				length += 0x18;	// 文件头
				mRandomAccessFile.seek(addr+0x10);
				mRandomAccessFile.read(data);
				// 总笔画数
				int biHuaCount = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8) + (byte2int(data[2]) << 16) + (byte2int(data[3]) << 24);
				int pointCount;			// 某一笔的点数
				for(int i = 0;i < biHuaCount;i++) {
					mRandomAccessFile.seek(addr+length+1);
					mRandomAccessFile.read(data, 0, 2);
					pointCount = (byte2int(data[0]) << 0) + (byte2int(data[1]) << 8);
					length += pointCount*2 + 6;
				}
				length += 4;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			data = null;
		}
		
		return length;
	}
	
	private int byte2int(byte b) {
		if(b >= 0) {
			return (int)b;
		} else {
			return 0x100 + b;
		}
	}
}