package com.ynding.cloud.common.util.mq;

import cn.hutool.core.util.NetUtil;

import javax.swing.*;

/**
 * @author ynding
 * @version 2020/08/29
 */
public class RabbitMqUtil {
 
    public static void main(String[] args) {
        checkServer(15672);
    }
    public static void checkServer(int port) {
        if(NetUtil.isUsableLocalPort(port)) {
            JOptionPane.showMessageDialog(null, "RabbitMQ 服务器未启动 ");
            System.exit(1);
        }
    }
}