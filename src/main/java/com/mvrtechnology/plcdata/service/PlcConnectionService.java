package com.mvrtechnology.plcdata.service;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.springframework.stereotype.Service;
import java.net.InetAddress;

@Service
public class PlcConnectionService
{
    public TCPMasterConnection getConnection(String ip, int port) throws Exception
    {
        InetAddress address = InetAddress.getByName(ip);

        TCPMasterConnection connection = new TCPMasterConnection(address);
        connection.setPort(port);
        connection.setTimeout(500);

        connection.connect();

        return connection;
    }
}