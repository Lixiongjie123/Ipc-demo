package com.example.lixiongjie.ipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tx;
    private Messenger mService;
    private String TAG = "MainActivity";
    private IBookMananger iBookMananger;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what) {
               case 1:
                   Log.d(TAG, "newBook is coming: "+(Book)msg.obj);
                   break;
               default:
                   super.handleMessage(msg);
           }

        }
    };



    private ServiceConnection serviceConnection =  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //通过传入Binder，将Binder转换成实现AIDL接口的对象，这里是跨进程的，所以的得到的是代理对象。
             iBookMananger = IBookMananger.Stub.asInterface(iBinder);
            try {
                //调用实现AIDL的对象的代理方法，从远程得到List对象
               List<Book> books = iBookMananger.getBookList();
               tx .setText(books.get(0).bookName);
                Log.d(TAG, "onServiceConnected: "+books.size());
               Book book = new Book(123,"可爱女人");
                iBookMananger.addBook(book);
                //同样插入数据，需要调用代理对象的添加方法。
               List<Book> newBooks = iBookMananger.getBookList();
                Log.d(TAG, "onServiceConnected: "+newBooks);
                iBookMananger.registerListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,RemoteService.class);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        tx = findViewById(R.id.tx);
    }

    private ServiceConnection mConncetion = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);
            Message message =Message.obtain(null,1);
            Bundle data = new Bundle();
            data.putString("msg","hello");
            message.setData(data);
            //回复的Messenger需要提前设置。
            message.replyTo = mGetReplyMessenger;
            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onDestroy() {
        if (iBookMananger!=null && iBookMananger.asBinder().isBinderAlive()){
            try {
                iBookMananger.unregisterListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(serviceConnection);
        super.onDestroy();
    }

    private Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

    private class MessengerHandler extends Handler  {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    tx.setText(msg.getData().getString("reply"));
                    break;
                    default:
                        super.handleMessage(msg);
            }
        }
    }

    private IOnNewBookArrivedListener listener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(1,newBook).sendToTarget();
        }
    };


    //    private void persistToFile(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//             Book book = new Book(1,"童话故事");
//             File file = new File();
//             if (!file.exists()){
//                 file.mkdirs();
//             }
//             File cache = new File();
//                ObjectOutputStream out = null;
//                try {
//                    out = new ObjectOutputStream(new FileOutputStream(cache));
//                    out.writeObject(book);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    try {
//                        out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
}
