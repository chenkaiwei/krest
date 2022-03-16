package com.chenkaiwei.krest.cryption.annotation;

public enum CryptionModle {

    //接口的加密模式：
    WHOLE_REQUEST,//请求内容的全部
    WHOLE_RESPONSE,//响应消息的全部
        //以上两个会把解密/加密处理好再传进方法/返回给客户端
    BOTH,//以上两者都
    CUSTOMIZE//部分数据加密，和客户端自行约定。同一个请求里可以入参部分加密，出参也部分加密，所以不用区分。
    ;

    public boolean isWholeRequest() {
        return this == WHOLE_REQUEST || this == BOTH;
    }


    public boolean isWholeResponse() {
        return this == WHOLE_RESPONSE || this == BOTH;
    }

}
