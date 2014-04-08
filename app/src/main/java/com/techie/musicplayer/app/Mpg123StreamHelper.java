package com.techie.musicplayer.app;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author: larry.pham
 * @date: 2014.04.08
 * <p/>
 * Description:
 * Copyright (C) 2014 TechieDB Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Mpg123StreamHelper {
    public static final String TAG = Mpg123StreamHelper.class.getSimpleName();
    public static final int SOCKET_TIMEOUT= 30000;

    public BufferedInputStream mInStream;
    public static FileInputStream mInFileStream;
    public HttpURLConnection mConnection;
    public long mTotalFileLength, mCurrentFileLength, mCurrentOffset;

    public URL mUrl;
    public String mPath;

    public FileOutputStream mDownloadOutStream;
    public InputStream mInDownloadStream;
    public Handler mStreamDownloadHandler;

    public Mpg123StreamHelper(){
        Log.w(TAG, "Mpg123StreamHelper Constructor");
        mInStream = null;
        mPath = null;
    }

    public Mpg123StreamHelper(String inPath){
        Log.w(TAG, "Mpg123StreamHelper Constructor: " + inPath);
        mInStream = null;
        this.mPath = inPath;
    }


    public class StreamDownloader extends Thread{
        public static final int SIZE= 1024;
        long mOffSet, mTotalSize;
        long StreamEndPos, mDownloadSize;
        public long getDownloadSize(){ return mDownloadSize; }
        public long getOffSet(){ return mOffSet; }
        public void setOffSet(long offSet){ this.mOffSet = offSet; }
        public long getTotalSize(){ return this.mTotalSize; }

        public StreamDownloader(){
            setName("StreamDownloader");
            mInDownloadStream = null;
            mDownloadOutStream= null;
        }

        public StreamDownloader(InputStream inStream){
            setName("StreamDownloader");
            mInDownloadStream = mInStream;
            mStreamDownloadHandler = null;
        }

        public static final int OPEN    = 0;
        public static final int READ    = 1;
        public static final int SEEK    = 2;
        public static final int QUIT    = 3;
        public byte[] buffer = new byte[SIZE];

        public void run(){
            Looper.prepare();
            mStreamDownloadHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case  OPEN:
                            Log.d(TAG, "StreamDownloader Handler switched to OPEN status ");
                            break;
                        case QUIT:
                            Log.d(TAG, "StreamDownloader Handler switched to QUIT status ");
                            if (mInStream != null){
                                try{
                                    mInStream.close();
                                }catch (IOException ex){
                                    ex.printStackTrace();
                                }
                            }
                    }
                    super.handleMessage(msg);
                }
            };
        }
    }
}
