package com.uiowa_facial_paralysis.facialparalysisapp;

import android.arch.persistence.room.TypeConverter;
import java.util.ArrayList;

public class ImageConverter
{
    @TypeConverter
    public static ArrayList<byte[]> byteArrayToArrayList(byte[] data)
    {
        ArrayList<byte[]> toReturn = new ArrayList<>();
        toReturn.add(data);
        return toReturn;
    }

    @TypeConverter
    public static byte[] ArrayListMemberToByteArray(ArrayList<byte[]> someObjects, int indexWanted)
    {
        return someObjects.get(indexWanted);
    }
}
