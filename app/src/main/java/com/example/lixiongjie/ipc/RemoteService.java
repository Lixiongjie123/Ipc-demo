package com.example.lixiongjie.ipc;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.ContentValues.TAG;

public class RemoteService extends Service {
    //CopyOnWriteArrayList是线程安全的容器让并发IPC实现可行
    private CopyOnWriteArrayList<Book> books = new CopyOnWriteArrayList<>();
    //初始化一个Boolean对象，为什么选择AtomicBoolean，线程安全呀。
    private AtomicBoolean isDestory = new AtomicBoolean(false);
    //线程安全的监听容器
    private CopyOnWriteArrayList<IOnNewBookArrivedListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        books.add(new Book(123,"Sda sa d"));
        books.add(new Book(123,"Sda sa d"));
        books.add(new Book(123,"Sda sa d"));
        new Thread(new ServiceWorker()).start();
    }

    private IBookMananger.Stub mBinder = new IBookMananger.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return books;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            books.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (!listeners.contains(listener)){
                listeners.add(listener);
            }
            Log.d(TAG, "registerListener: "+listeners.size());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (listeners.contains(listener)){
                listeners.remove(listener);
            } else {
                Log.d(TAG, "unregisterListener: not found ");
            }
            Log.d(TAG, "unregisterListener: "+listeners.size());
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
            return mBinder;
//        return mMessenger.getBinder();

        }


    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinder.asBinder().unlinkToDeath(mDeathRecipient,0);
            mBinder = null;

            //TODO：重写绑定远程Service
        }
    };





    private static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1 :
                    Log.d(TAG, "handleMessage: "+msg.getData().getString("msg"));
                   Messenger client = msg.replyTo;
                   Message replyMessage = Message.obtain(null,1);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply","hi ,How are you");
                    replyMessage.setData(bundle);
                    try {
                        client.send(replyMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessageHandler());


    @Override
    public void onDestroy() {
        isDestory.set(true);
        super.onDestroy();
    }

    //开启个线程在活着情况下不断模拟书的添加。
    private class ServiceWorker implements Runnable{
        @Override
        public void run() {
            while (!isDestory.get()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = books.size()+1;
                Book newBook = new Book(bookId,"new Book !!"+bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //新书到了调用每个接口的，onNewBookArrived去更新书籍。
    private void onNewBookArrived(Book newBook) throws RemoteException {
        books.add(newBook);
        Log.d(TAG, "listener`s Size : "+listeners.size());
        for (int i =0 ;i<listeners.size();i++){
            IOnNewBookArrivedListener listener = listeners.get(i);
            listener.onNewBookArrived(newBook);
        }
    }
}
