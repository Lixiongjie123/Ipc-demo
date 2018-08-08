// IBookMananger.aidl
package com.example.lixiongjie.ipc;
import com.example.lixiongjie.ipc.Book;
import com.example.lixiongjie.ipc.IOnNewBookArrivedListener;
// Declare any non-default types here with import statements
interface IBookMananger {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookArrivedListener listener);
    void unregisterListener(IOnNewBookArrivedListener listener);

}
