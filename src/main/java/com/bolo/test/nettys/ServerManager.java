package com.bolo.test.nettys;

/**
 * @Author wangyue
 * @Date 19:25
 */
public class ServerManager {

    private AbstractNettyServer tcpServer;

    public ServerManager() {
        tcpServer = (AbstractNettyServer)AppContext.getBean(AppContext.TCP_SERVER);
    }

    public void startServer(int port) throws Exception {
        tcpServer.startServer(port);
    }

    public void startServer() throws Exception {
        tcpServer.startServer();
    }
    public void stopServer() throws Exception {
        tcpServer.stopServer();
    }


    public static void main(String[] args) throws Exception {
        ServerManager manager = new ServerManager();
        //manager.startServer(args[0]);
        manager.startServer();
    }
}
