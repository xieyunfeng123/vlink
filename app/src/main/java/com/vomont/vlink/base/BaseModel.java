package com.vomont.vlink.base;


public interface BaseModel
{
    interface HttpListener<T>
    {
        void onSucess(T object);
        
        void onFail();
        
        void onError();
        
        
    }
}
