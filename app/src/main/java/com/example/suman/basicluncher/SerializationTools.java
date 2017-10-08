package com.example.suman.basicluncher;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by suman on 10/7/2017.
 */

public class SerializationTools {
    public static void serializeData(AppSerializableData obj){
        FileOutputStream fos;
        try{
            fos = MainActivity.activity.openFileOutput("data", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static AppSerializableData loadSerializedData(){
        ObjectInputStream inputStream = null;
        try{
            inputStream = new ObjectInputStream(MainActivity.activity.openFileInput("data"));
            Object obj = inputStream.readObject();

            if(obj instanceof AppSerializableData){
                return (AppSerializableData) obj;
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if(inputStream != null){
                    inputStream.close();
                }
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
        return null;
    }
}
