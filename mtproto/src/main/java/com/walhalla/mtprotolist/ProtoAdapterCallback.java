package com.walhalla.mtprotolist;


public interface ProtoAdapterCallback<T> {
    void categorySelected(int position, T title);

    void shareProxy(int adapterPosition, T data);

    void qrProxyCode(int adapterPosition, T data);

    void handleProxyIntent(int adapterPosition, T data);

    void viewProxy(int adapterPosition, T data);

    void copyClipboard(String host);

    void delete(T data);

    void info(T data);

    void copyPort(T data);
}