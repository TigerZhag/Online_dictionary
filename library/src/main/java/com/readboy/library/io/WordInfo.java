package com.readboy.library.io;

public class WordInfo {
	public String mWord;//字符总数不超过65536
	public String mYinBiao;
	public int		mSoundOffest;
	public int		mExplainOffest;
	public int		mStrokesOffest;
	
	public int		mSoundStartAddr;
	public int		mSoundLength;
	public byte[]	mbiHuaInfo;
}
