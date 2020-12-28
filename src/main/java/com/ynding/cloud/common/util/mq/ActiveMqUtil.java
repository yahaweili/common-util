package com.ynding.cloud.common.util.mq;

import cn.hutool.core.net.NetUtil;

import javax.swing.*;

/**
 * @author ynding
 * @version 2020/08/29
 */
public class ActiveMqUtil {

	public static void main(String[] args) {
        checkServer(8161);
    }
    public static void checkServer(int port) {
        if(NetUtil.isUsableLocalPort(port)) {
            JOptionPane.showMessageDialog(null, "ActiveMQ 服务器未启动 ");
            System.exit(1);
        }
    }
}
