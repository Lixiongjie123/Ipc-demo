// IOnNewBookArrivedListener.aidl
package com.example.lixiongjie.ipc;
import com.example.lixiongjie.ipc.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
   void onNewBookArrived(in Book newBook);
}
