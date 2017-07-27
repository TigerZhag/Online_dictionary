package com.readboy.library.search;

import android.content.Context;

import com.readboy.library.io.DictFile;
import com.readboy.library.utils.PhoneticUtils;
import com.readboy.sound.NewSound;
import com.readboy.sound.Sound;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DictWordSound {

    private DictFile mAudioFile = null;
    private Sound mSound = null;
    private NewSound newSound = null;
    private boolean isSoundPauseSate = false;

    public DictWordSound() {
        // TODO Auto-generated constructor stub
        mSound = new Sound();
        newSound = new NewSound();
    }

    public boolean open() {
        String path = DictFile.getSDcardDataPath() + DictFile.DICT_LOCAL_NAME[DictFile.DICT_LOCAL_NAME.length-1];
        File file = new File(path);
        if (!file.exists()) {
            path = DictFile.INTERNAL_DIR + File.separator + DictFile.DICT_LOCAL_NAME[DictFile.DICT_LOCAL_NAME.length-1];
        }
        mAudioFile = new DictFile();
        if(  mAudioFile.dictFileOpen(path, "r") == false ) {
            onDestroy();
            return false;
        }
        if( mAudioFile.dictFileInitial(DictFile.DICT_LOCAL_NAME.length-1) == false ) {
            onDestroy();
            return false;
        }
        return true;
    }

    public void close() {
        /*if (mAudioFile != null) {
            mAudioFile.dictFileClosed();
        }*/
        onDestroy();
    }

    /**
     * 判断英文单词能否发音
     * @param keyWord
     * @return
     */
    public boolean canPlay(String keyWord) {
        if (mAudioFile != null) {
            /**
             * 注意:将中式a转换为英式，再查找
             */
            keyWord = PhoneticUtils.returnOldCharseqence(keyWord);
            int audioAddrNumber = mAudioFile.dictFileGetDictDataWithCMP(keyWord);
            if((audioAddrNumber != -1) && ((audioAddrNumber&0xff000000)==0)) {
                return true;
            }
        }
        return false;
    }

    public byte[] getSoundByte(String keyWord) {
        if (mAudioFile == null) {
            return null;
        }
        keyWord = PhoneticUtils.returnOldCharseqence(keyWord);
        int audioAddrNumber = mAudioFile.dictFileGetDictDataWithCMP(keyWord);
        return getSoundByte(audioAddrNumber);
    }

    private byte[] getSoundByte(int audioAddrNumber) {
        if((audioAddrNumber != -1) && ((audioAddrNumber&0xff000000)==0)) {		// 发音需要找到精确匹配的单词
            int startAddr = mAudioFile.dictFileGetExplainStartAddr();
            int indexAddr = startAddr + audioAddrNumber * 4;
            byte[] indexData = new byte[4];
            // 解释地址的位置
            if(mAudioFile.dictFileGetDictData(indexAddr, indexData) != 4) {
//	    		Log.w(TAG, "******************** load dict explain start address failed! *************************");
            }
            // 当前解释的位置
            int startExplain = (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                    + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
            // 下一解释地址的位置
            indexAddr = startAddr + (audioAddrNumber + 1) * 4;
            if(mAudioFile.dictFileGetDictData(indexAddr, indexData) != 4) {
//	    		Log.w(TAG, "******************** load dict explain end address failed! *************************");
            }
            // 下一解释的位置
            int endExplain =  (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                    + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
            try {

				/* 单个汉字发音数据不能超过10K */
                int length = endExplain-startExplain;
                if(length>10000) {
//					Toast.makeText(this, "汉字发音异常", Toast.LENGTH_SHORT).show();
                    return null;
                }
                byte[] data = new byte[length];
                mAudioFile.dictFileGetDictData(startExplain, data);
                return data;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 单词发音
     * @param keyWord
     * @return
     */
    public boolean playWord(String keyWord) {
        return playWord(keyWord, 0);
    }

    public boolean playWord1(String keyWord, Sound.OnCompletionListener completionListener, Sound.OnErrorListener errorListener) {
        return playWord1(keyWord, 0, completionListener, errorListener);
    }

    public boolean playWord2(String keyWord, NewSound.OnCompletionListener completionListener, NewSound.OnErrorListener errorListener) {
        return playWord2(keyWord, 0, completionListener, errorListener);
    }

    public boolean playWord(String keyWord, int speed) {
        if (mAudioFile != null) {
            /**
             * 注意:将中式a转换为英式，再查找
             */
            keyWord = PhoneticUtils.returnOldCharseqence(keyWord);
            int audioAddrNumber = mAudioFile.dictFileGetDictDataWithCMP(keyWord);
            return playWord1(audioAddrNumber, speed, null, null);
        }
        return false;
    }
    /**
     * 单词发音
     * @param keyWord
     * @param speed
     * @return
     */
    public boolean playWord1(String keyWord, int speed, Sound.OnCompletionListener completionListener, Sound.OnErrorListener errorListener) {
        if (mAudioFile != null) {
            /**
             * 注意:将中式a转换为英式，再查找
             */
            keyWord = PhoneticUtils.returnOldCharseqence(keyWord);
            int audioAddrNumber = mAudioFile.dictFileGetDictDataWithCMP(keyWord);
            return playWord1(audioAddrNumber, speed, completionListener, errorListener);
        }
        return false;
    }

    /**
     * 单词发音
     * @param keyWord
     * @param speed
     * @return
     */
    public boolean playWord2(String keyWord, int speed, NewSound.OnCompletionListener completionListener, NewSound.OnErrorListener errorListener) {
        if (mAudioFile != null) {
            /**
             * 注意:将中式a转换为英式，再查找
             */
            keyWord = PhoneticUtils.returnOldCharseqence(keyWord);
            int audioAddrNumber = mAudioFile.dictFileGetDictDataWithCMP(keyWord);
            return playWord2(audioAddrNumber, speed, completionListener, errorListener);
        }
        return false;
    }

    /**
     * 单词发音
     * @param audioAddrNumber
     * @param speed
     * @return
     */
    private boolean playWord1(int audioAddrNumber, int speed, Sound.OnCompletionListener completionListener, Sound.OnErrorListener errorListener) {

        if (mAudioFile == null || mSound == null) {
            return false;
        }
        if((audioAddrNumber != -1) && ((audioAddrNumber&0xff000000)==0)) {		// 发音需要找到精确匹配的单词
            int startAddr = mAudioFile.dictFileGetExplainStartAddr();
            int indexAddr = startAddr + audioAddrNumber * 4;
            byte[] indexData = new byte[4];
            // 解释地址的位置
            if(mAudioFile.dictFileGetDictData(indexAddr, indexData) != 4) {
//	    		Log.w(TAG, "******************** load dict explain start address failed! *************************");
            }
            // 当前解释的位置
            int startExplain = (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                    + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
            // 下一解释地址的位置
            indexAddr = startAddr + (audioAddrNumber + 1) * 4;
            if(mAudioFile.dictFileGetDictData(indexAddr, indexData) != 4) {
//	    		Log.w(TAG, "******************** load dict explain end address failed! *************************");
            }
            // 下一解释的位置
            int endExplain =  (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                    + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
            try {

				/* 单个汉字发音数据不能超过10K */
                int length = endExplain-startExplain;
                if(length>10000) {
//					Toast.makeText(this, "汉字发音异常", Toast.LENGTH_SHORT).show();
                    return false;
                }
                byte[] data = new byte[length];
                mAudioFile.dictFileGetDictData(startExplain, data);
                mSound.setDataSource(data);
                mSound.setSpeed(speed);
                if (completionListener != null) {
                    mSound.setOnCompletionListener(completionListener);
                }
                if (errorListener != null) {
                    mSound.setOnErrorListener(errorListener);
                }
                mSound.start();
                return true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;

    }

    /**
     * 单词发音
     * @param audioAddrNumber
     * @param speed
     * @return
     */
    private boolean playWord2(int audioAddrNumber, int speed, NewSound.OnCompletionListener completionListener, NewSound.OnErrorListener errorListener) {

        if (mAudioFile == null || newSound == null) {
            return false;
        }
        if((audioAddrNumber != -1) && ((audioAddrNumber&0xff000000)==0)) {		// 发音需要找到精确匹配的单词
            int startAddr = mAudioFile.dictFileGetExplainStartAddr();
            int indexAddr = startAddr + audioAddrNumber * 4;
            byte[] indexData = new byte[4];
            // 解释地址的位置
            if(mAudioFile.dictFileGetDictData(indexAddr, indexData) != 4) {
//	    		Log.w(TAG, "******************** load dict explain start address failed! *************************");
            }
            // 当前解释的位置
            int startExplain = (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                    + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
            // 下一解释地址的位置
            indexAddr = startAddr + (audioAddrNumber + 1) * 4;
            if(mAudioFile.dictFileGetDictData(indexAddr, indexData) != 4) {
//	    		Log.w(TAG, "******************** load dict explain end address failed! *************************");
            }
            // 下一解释的位置
            int endExplain =  (byte2int(indexData[0])) + (byte2int(indexData[1]) << 8)
                    + (byte2int(indexData[2]) << 16) + (byte2int(indexData[3]) << 24);
            try {

				/* 单个汉字发音数据不能超过10K */
                int length = endExplain-startExplain;
                if(length>10000) {
//					Toast.makeText(this, "汉字发音异常", Toast.LENGTH_SHORT).show();
                    return false;
                }
                byte[] data = new byte[length];
                mAudioFile.dictFileGetDictData(startExplain, data);
                newSound.setDataSource(data);
                newSound.setSpeed(speed);
                if (completionListener != null) {
                    newSound.setOnCompletionListener(completionListener);
                }
                if (errorListener != null) {
                    newSound.setOnErrorListener(errorListener);
                }
                newSound.start();
                return true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;

    }

    private final String ASSERT_PINYIN_DOC = "wordsound/"; //asset文件下存放拼音播放文件的位置
    /**
     * 根据拼音直接读取assert笔画语音文件
     * @return
     */
    public byte[] getBihuaSound(String phonetic, Context mContext)
    {
        byte[] data = new byte[4];
        InputStream is = null;
        try {
            is = mContext.getResources().getAssets().open(ASSERT_PINYIN_DOC+phonetic+".mp3");
            int length = is.available();
            if (length > 0) {
                data = new byte[length];
                is.read(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    public void playBihuaSound(String phonetic, Context mContext, int speed) {
        try {
            byte[] data = getBihuaSound(phonetic, mContext);
            mSound.setDataSource(data);
            mSound.setSpeed(speed);
            mSound.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void playSound(byte[] data, int speed) {
        try {
            mSound.setDataSource(data);
            mSound.setSpeed(speed);
            mSound.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void playNewSound(byte[] data, int speed) {
        try {
            newSound.setDataSource(data);
            newSound.setSpeed(speed);
            newSound.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void reStartSound() {
        if (mSound != null && isSoundPauseSate) {
            mSound.start();
            isSoundPauseSate = false;
        }
    }

    public void pauseSound() {
        if (mSound != null && mSound.isPlaying()) {
            mSound.pause();
            isSoundPauseSate = true;
        }
    }

    public void stopSound() {
        if (mSound != null) {
            mSound.stop();
            mSound.release();
        }
        isSoundPauseSate = false;
    }

    public void onDestroy() {
        if (mSound != null) {
            mSound.stop();
            mSound.release();
        }
        if (newSound != null) {
            newSound.stop();
            newSound.release();
        }
        if (mAudioFile != null) {
            mAudioFile.dictFileClosed();
            mAudioFile = null;
        }
    }

    private int byte2int(byte b) {
        if(b >= 0) {
            return (int)b;
        } else {
            return 0x100 + b;
        }
    }
}
