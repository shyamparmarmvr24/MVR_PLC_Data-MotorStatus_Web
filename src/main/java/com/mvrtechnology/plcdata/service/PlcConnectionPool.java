package com.mvrtechnology.plcdata.service;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.stereotype.Component;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlcConnectionPool
{
    private static final long MAX_CONNECTION_AGE_MS = 5 * 60 * 1000;

    private final ConcurrentHashMap<Integer, TCPMasterConnection> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Long> connectionOpenedAt = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Object> locks = new ConcurrentHashMap<>();

    public TCPMasterConnection getConnection(Integer plantId, String ip, int port) throws Exception
    {
        TCPMasterConnection connection = connections.get(plantId);
        boolean tooOld = connectionOpenedAt.getOrDefault(plantId, 0L) > 0
                && (System.currentTimeMillis() - connectionOpenedAt.get(plantId)) > MAX_CONNECTION_AGE_MS;

        if (connection != null && connection.isConnected() && !tooOld)
        {
            return connection;
        }

        synchronized (getLock(plantId))
        {
            connection = connections.get(plantId);
            tooOld = connectionOpenedAt.getOrDefault(plantId, 0L) > 0
                    && (System.currentTimeMillis() - connectionOpenedAt.get(plantId)) > MAX_CONNECTION_AGE_MS;

            if (connection == null || !connection.isConnected() || tooOld)
            {
                if (connection != null)
                {
                    try { connection.close(); } catch (Exception ignored) {}
                }

                connection = new TCPMasterConnection(InetAddress.getByName(ip));
                connection.setPort(port);
                connection.setTimeout(1500);
                connection.connect();

                connections.put(plantId, connection);
                connectionOpenedAt.put(plantId, System.currentTimeMillis());
            }
        }

        return connection;
    }

    public Object getLock(Integer plantId)
    {
        return locks.computeIfAbsent(plantId, k -> new Object());
    }

    public void close(Integer plantId)
    {
        TCPMasterConnection connection = connections.remove(plantId);
        connectionOpenedAt.remove(plantId);
        //locks.remove(plantId);
        if (connection != null)
        {
            try { connection.close(); } catch (Exception ignored) {}
        }
    }
}