package com.example.lixiongjie.ipc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
//    private void recoverFromFile(){
//        new  Thread(new Runnable() {
//            @Override
//            public void run() {
//                Book book = null;
//                File cache = new File();
//                if (cache.exists()){
//                    ObjectInputStream inputStream =null;
//                    try {
//                        inputStream = new ObjectInputStream(new  FileInputStream(""));
//                        book = (Book) inputStream.readObject();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    finally {
//                        try {
//                            inputStream.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            }
//        }).start();
//    }
}
