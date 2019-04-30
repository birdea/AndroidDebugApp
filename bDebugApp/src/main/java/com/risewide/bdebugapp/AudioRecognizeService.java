package com.risewide.bdebugapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;

import com.risewide.bdebugapp.communication.util.IOCloser;
import com.risewide.bdebugapp.util.BLog;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AudioRecognizeService {

    private static final String TAG = "AudioRecognizeService";

    private final int mBufferSize = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private final int mBytesPerElement = 2; // 2 bytes in 16bit format

    // 설정할 수 있는 sampleRate, AudioFormat, channelConfig 값들을 정의
    private final int[] mSampleRates = new int[]{44100, 22050, 11025, 8000};
    private final short[] mAudioFormats = new short[]{AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
    private final short[] mChannelConfigs = new short[]{AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO};

    // 위의 값들 중 실제 녹음 및 재생 시 선택된 설정값들을 저장
    private int mSampleRate = 44100;//8000;
    private short mAudioFormat = 2;//AudioFormat.ENCODING_PCM_16BIT;
    private short mChannelConfig = 12;//AudioFormat.CHANNEL_IN_MONO;

    //private AudioRecord mRecorder = null;
    private Thread mRecordingThread = null;
    private MRunnable mRecordingRunnable = null;

    // 녹음한 파일을 저장할 경로
    private String mPath = "";

    public AudioRecognizeService() {
    }

    // 녹음을 수행할 Thread를 생성하여 녹음을 수행하는 함수
    public void startRecording() {
        init();
        BLog.d(TAG, "startRecording() start");
        AudioRecord recorder = getAudioRecord();
        if (recorder == null) {
            BLog.w(TAG, "startRecording() err: mRecorder == null");
            return;
        }
        BLog.d(TAG, "startRecording() state:"+recorder.getState());
//        mRecorder.startRecording();
        mRecordingRunnable = getRecordingRunnable(recorder);
        mRecordingThread = getThread(mRecordingRunnable);
        mRecordingThread.start();
        BLog.d(TAG, "startRecording() end");
    }

    List<AudioRecord> mAudioRecordList = new ArrayList<>();
    List<MRunnable> mRunnableList = new ArrayList<>();
    List<Thread> mThreadList = new ArrayList<>();

    private Thread getThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "AudioRecorder Thread");
        mThreadList.add(thread);
        return thread;
    }

    private AudioRecord getAudioRecord() {
        AudioRecord recorder = findAudioRecord();
        if (recorder == null) {
            return null;
        }
        mAudioRecordList.add(recorder);
        return recorder;
    }

    private MRunnable getRecordingRunnable(AudioRecord audioRecord) {
        MRunnable runnable = new MRunnable(audioRecord) {
            @Override
            public void stopRun() {
                isRunnable = false;
            }

            @Override
            public void run() {
                short sData[] = new short[mBufferSize];
                FileOutputStream fos = null;
                mAudioRecord.startRecording();
                try {
                    BLog.d(TAG, "writeAudioDataToFile() - start");
                    while (isRunnable) {
                        int state = mAudioRecord.getState();
                        int recordingState = mAudioRecord.getRecordingState();
                        if (state != AudioRecord.STATE_INITIALIZED || recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                            BLog.d(TAG, "writeAudioDataToFile() - state:"+state+", recordingState:"+recordingState);
                            isRunnable = false;
                            break;
                        }
                        if (fos == null) {
                            fos = new FileOutputStream(mPath);
                        }
                        mAudioRecord.read(sData, 0, mBufferSize);
                        byte bData[] = short2byte(sData);
                        fos.write(bData, 0, mBufferSize * mBytesPerElement);
                    }
                    BLog.d(TAG, "writeAudioDataToFile() - end");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOCloser.close(fos);
                    try {
                        mAudioRecord.stop();
                    } catch (Exception e) {}
                    try {
                        mAudioRecord.release();
                    } catch (Exception e) {}
                }
            }
        };
        mRunnableList.add(runnable);
        return runnable;
    }

    public void stopRecording() {
        // stops the recording activity
        BLog.d(TAG, "stopRecording()");
        stopLastRunnable();
        mRecordingRunnable.stopRun();
        try {
            mRecordingThread.interrupt();
        } catch (Exception e) {}
        mRecordingThread = null;
    }

    private void init() {
        BLog.d(TAG, "init() start");

        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        mPath = sd + "/record_audiorecord.pcm";

        for (int rate : mSampleRates) {
            for (short format : mAudioFormats) {
                for (short channel : mChannelConfigs) {
                    try {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channel, format);
                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            mSampleRate = rate;
                            mAudioFormat = format;
                            mChannelConfig = channel;

                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, mChannelConfig, mAudioFormat, bufferSize);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        BLog.i(TAG, "init() end; mSampleRate:"+mSampleRate+", mAudioFormat:"+mAudioFormat+", mChannelConfig:"+mChannelConfig);
    }

    // 녹음을 하기 위한 sampleRate, audioFormat, channelConfig 값들을 설정
    private AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short format : mAudioFormats) {
                for (short channel : mChannelConfigs) {
                    try {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channel, format);
                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            mSampleRate = rate;
                            mAudioFormat = format;
                            mChannelConfig = channel;
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRate, mChannelConfig, mAudioFormat, bufferSize);
                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                                BLog.i(TAG, "findAudioRecord() return; mSampleRate:"+mSampleRate+", mAudioFormat:"+mAudioFormat+", mChannelConfig:"+mChannelConfig);
                                return recorder;    // 적당한 설정값들로 생성된 Recorder 반환
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 적당한 설정값들을 찾지 못한 경우 Recorder를 찾지 못하고 null 반환
        BLog.d(TAG, "findAudioRecord()() return null");
        return null;
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private Thread threadPlayWave;
    private MRunnable runnablePlayWave = new MRunnable() {
        @Override
        public void stopRun() {
            isRunnable = false;
        }
        @Override
        public void run() {
            BLog.d(TAG, "playWaveFile() - start");
            init();
            BLog.d(TAG, "playWaveFile() - mSampleRate:"+mSampleRate+",mChannelConfig:"+mChannelConfig+",mAudioFormat:"+mAudioFormat);
            mSampleRate = 44100;//8000;
            mAudioFormat = 2;//AudioFormat.ENCODING_PCM_16BIT;
            mChannelConfig = 12;//AudioFormat.CHANNEL_IN_MONO;
            int minBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelConfig, mAudioFormat);
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelConfig, mAudioFormat, minBufferSize, AudioTrack.MODE_STREAM);
            int count = 0;
            byte[] data = new byte[mBufferSize];
            FileInputStream fis = null;
            DataInputStream dis = null;
            try {
                fis = new FileInputStream(mPath);
                dis = new DataInputStream(fis);
                audioTrack.play();
                while (isRunnable && (count = dis.read(data, 0, mBufferSize)) > -1) {
                    audioTrack.write(data, 0, count);
                }
                dis.close();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (audioTrack != null) {
                    audioTrack.stop();
                    audioTrack.release();
                }
                IOCloser.close(dis);
                IOCloser.close(fis);
                BLog.d(TAG, "playWaveFile() - finally");
            }
            BLog.d(TAG, "playWaveFile() - end");
        }
    };

    public void playWaveFile() {
        runnablePlayWave.isRunnable = true;
        threadPlayWave = new Thread(runnablePlayWave);
        threadPlayWave.start();
    }

    public void stopWaveFile() {
        BLog.d(TAG, "stopWaveFile() - start");
        if (threadPlayWave != null) {
            if (threadPlayWave.isAlive() && !threadPlayWave.isInterrupted()) {
                runnablePlayWave.stopRun();
                threadPlayWave.interrupt();
                threadPlayWave = null;
            }
        }
        BLog.d(TAG, "stopWaveFile() - end");
    }

    public void stopAll() {
        BLog.d(TAG, "stopAll() - start; runnable:"+mRunnableList.size());
        for (int i=0; i<mRunnableList.size(); i++) {
            mRunnableList.get(i).stopRun();
        }
        mRunnableList.clear();

        for (int i=0; i<mThreadList.size(); i++) {
            try {
                mThreadList.get(i).interrupt();
            } catch (Exception e) {}
        }
        mThreadList.clear();

        BLog.d(TAG, "stopAll() - start; audioRecord:"+mAudioRecordList.size());
        for (int i=0; i<mAudioRecordList.size(); i++) {
            AudioRecord audioRecord = mAudioRecordList.get(i);
            try {
                audioRecord.stop();
            } catch (Exception e) {
            }
            try {
                audioRecord.release();
            } catch (Exception e) {
            }
        }
        mAudioRecordList.clear();
        BLog.d(TAG, "stopAll() - end");
    }

    private void stopLastRunnable() {
        int lastIndex = mRunnableList.size() - 1;
        BLog.d(TAG, "stopLastRunnable() - lastIndex:"+lastIndex);
        if (lastIndex < 0) {
            return;
        }
        mRunnableList.get(lastIndex).stopRun();
    }

    /*
    private void writeAudioDataToFile() {
        // Write the output audio in byte

        String filePath = "/sdcard/voice8K16bitmono.pcm";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (mIsRecording) {
            // gets the voice output from microphone to byte format

            mRecorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    public abstract class MRunnable implements Runnable {

        public MRunnable(){}

        public MRunnable(AudioRecord audioRecord) {
            mAudioRecord = audioRecord;
        }

        protected boolean isRunnable = true;
        protected AudioRecord mAudioRecord;

        public abstract void stopRun();
    }
}
