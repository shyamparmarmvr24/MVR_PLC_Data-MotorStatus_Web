package com.mvrtechnology.plcdata.util;

import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

public class ModbusReader
{
    public static int readWord(TCPMasterConnection connection, int address) throws Exception
    {
        int offset = address - 40000;

        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(offset, 1);
        request.setUnitID(0);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();

        ReadMultipleRegistersResponse response =
                (ReadMultipleRegistersResponse) transaction.getResponse();

        return response.getRegisterValue(0);
    }

    public static int readSignedInt16(TCPMasterConnection connection, int address) throws Exception
    {
        int value = readWord(connection, address);

        if (value > 32767)
        {
            value = value - 65536;
        }

        return value;
    }

    public static int readInt32(TCPMasterConnection connection, int address) throws Exception
    {
        int offset = address - 40000;

        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(offset, 2);
        request.setUnitID(0);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();

        ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();

        int high = response.getRegisterValue(0);
        int low = response.getRegisterValue(1);

        return (high << 16) | low;
    }

    public static float readFloat(TCPMasterConnection connection, int address) throws Exception
    {
        int offset = address - 40000;

        ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(offset, 2);
        request.setUnitID(0);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();

        ReadMultipleRegistersResponse response =
                (ReadMultipleRegistersResponse) transaction.getResponse();

        int high = response.getRegisterValue(0);
        int low = response.getRegisterValue(1);

        int combined = (low << 16) | high;

        return Float.intBitsToFloat(combined);
    }

    public static boolean readCoil(TCPMasterConnection connection, int address) throws Exception
    {
        int offset = address - 1;

        ReadCoilsRequest request = new ReadCoilsRequest(offset, 1);
        request.setUnitID(0);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();

        ReadCoilsResponse response = (ReadCoilsResponse) transaction.getResponse();

        return response.getCoilStatus(0);
    }
}