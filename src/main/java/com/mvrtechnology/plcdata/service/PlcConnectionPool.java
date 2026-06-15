package com.mvrtechnology.plcdata.service;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.stereotype.Component;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlcConnectionPool
{
    private final ConcurrentHashMap<Integer,TCPMasterConnection>
            connections = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Integer,Object>
            locks = new ConcurrentHashMap<>();

    public TCPMasterConnection getConnection(
            Integer plantId,
            String ip,
            int port) throws Exception
    {
        TCPMasterConnection connection =
                connections.get(plantId);

        if(connection != null &&
                connection.isConnected())
        {
            return connection;
        }

        synchronized (getLock(plantId))
        {
            connection = connections.get(plantId);

            if(connection == null ||
                    !connection.isConnected())
            {
                connection =
                        new TCPMasterConnection(
                                InetAddress.getByName(ip));

                connection.setPort(port);

                connection.setTimeout(2000);

                connection.connect();

                connections.put(
                        plantId,
                        connection);
            }
        }

        return connection;
    }

    public Object getLock(Integer plantId)
    {
        return locks.computeIfAbsent(
                plantId,
                k -> new Object());
    }

    public void close(Integer plantId)
    {
        TCPMasterConnection connection =
                connections.remove(plantId);

        locks.remove(plantId);

        if(connection != null)
        {
            try
            {
                connection.close();
            }
            catch (Exception ignored)
            {
            }
        }
    }
}