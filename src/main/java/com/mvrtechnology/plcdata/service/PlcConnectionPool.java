package com.mvrtechnology.plcdata.service;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.stereotype.Component;
import java.net.InetAddress;

@Component
public class PlcConnectionPool {

    // Each thread gets its own connection
    private final ThreadLocal<TCPMasterConnection> threadConnection = new ThreadLocal<>();

    public TCPMasterConnection getConnection(String ip, int port) throws Exception {

        TCPMasterConnection connection = threadConnection.get();

        // If no connection create new
        if (connection == null || !connection.isConnected()) {

            connection = new TCPMasterConnection(InetAddress.getByName(ip));
            connection.setPort(port);
            connection.setTimeout(5000);

            connection.connect();

            threadConnection.set(connection);
        }

        return connection;
    }

    public void close() {
        TCPMasterConnection connection = threadConnection.get();

        if (connection != null) {
            try {
                if (connection.isConnected()) {
                    connection.close();
                }
            } catch (Exception ignored) {}

            threadConnection.remove();
        }
    }
}