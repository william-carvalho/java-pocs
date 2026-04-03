package com.example.redisclone.client;

import com.example.redisclone.protocol.ProtocolConstants;
import com.example.redisclone.util.PortUtils;

public class RedisCloneClientApplication {

    public static void main(String[] args) throws Exception {
        String host = PortUtils.parseHost(args, 0, ProtocolConstants.DEFAULT_HOST);
        int port = PortUtils.parsePort(args, 1, ProtocolConstants.DEFAULT_PORT);

        SocketClient socketClient = new SocketClient(host, port);
        socketClient.startInteractiveSession();
    }
}

