package com.example.psandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private String songNames[];
    static private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    int seekBarValue;

    //
    private int bufferSize;
    protected int byteOffset;
    protected int fileLengh;
    AudioTrack at;

    Thread audioThread;
    int globalPosition;
    //
    Context mContext;
    int minBufferSize;
    boolean STOPPED;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBarValue = 44000;

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        listView = findViewById(R.id.listView);
        final ArrayList<File> songs = readSongs(Environment.getExternalStorageDirectory());

        songNames = new String[songs.size()];

        for (int i = 0; i < songs.size(); ++i) {
            songNames[i] = songs.get(i).getName().toString().replace(".mp3", "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.song_layout,
                R.id.textView, songNames);

        listView.setAdapter(adapter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue = progress * 1000;
                //


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (audioThread != null){
                    at.stop();

                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //globalPosition = position;
                /*
                if (audioThread != null) {
                    at.stop();
                }*/

                audioThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        int i = 0;
                        byte[] music = null;
                        File file = songs.get(globalPosition);
                        InputStream is = null;
                        try {
                            is = new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        minBufferSize = AudioTrack.getMinBufferSize(seekBarValue,
                                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
                        at = new AudioTrack(AudioManager.STREAM_MUSIC, seekBarValue,
                                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                                minBufferSize, AudioTrack.MODE_STREAM);
                        try {
                            music = new byte[512];
                            at.play();

                            while ((i = is.read(music)) != -1)
                                at.write(music, 0, i);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        at.stop();
                        at.release();

                    }
                };
                audioThread.start();

            }
        });





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                globalPosition = position;
                /*
                if (audioThread != null) {
                    at.stop();
                }*/

                audioThread = new Thread() {
                    @Override
                    public void run() {
                        super.run();

                        int i = 0;
                        byte[] music = null;
                        File file = songs.get(globalPosition);
                        InputStream is = null;
                        try {
                            is = new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        minBufferSize = AudioTrack.getMinBufferSize(seekBarValue,
                                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
                        at = new AudioTrack(AudioManager.STREAM_MUSIC, seekBarValue,
                                AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
                                minBufferSize, AudioTrack.MODE_STREAM);
                        try {
                            music = new byte[512];
                            at.play();

                            while ((i = is.read(music)) != -1)
                                at.write(music, 0, i);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        at.stop();
                        at.release();

                    }
                };
                audioThread.start();


            }

        });

        //


    }

    private ArrayList<File> readSongs(File root) {
        ArrayList<File> arrayList = new ArrayList<File>();
        File files[] = root.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                arrayList.addAll(readSongs(file));
            } else {
                if (file.getName().endsWith(".wav")) {
                    arrayList.add(file);
                }
            }
        }
        return arrayList;
    }

}
