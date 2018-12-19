package com.lqwawa.intleducation.lqpay.network;


/**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 15:05
  * 描    述：网络访问接口简单工厂.
  * 修订历史：
  * ================================================
  */

public class NetworkClientFactory {

    public static NetworkClientInterf newClient() {

                return new NetworkClient();

    }
}
