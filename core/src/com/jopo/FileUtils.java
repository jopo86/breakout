package com.jopo;

import com.badlogic.gdx.files.FileHandle;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtils {

    public FileUtils() {}

    public static String readFile(FileHandle handle) {
        FileReader reader;
        try {
            reader = new FileReader("assets\\" + handle.file());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        char[] charBuf = new char[100];
        ArrayList<Character> saveText = new ArrayList<Character>();
        try {
            reader.read(charBuf);
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        float place = 1f/10f;
        for (int i = 0; i < charBuf.length; i++) {
            if (Character.isDigit(charBuf[i])) {
                saveText.add(charBuf[i]);
            }
        }

        String str = "";
        for (Character c : saveText) {
            str += c;
        }
        return str;
    }

    public static void writeFile(FileHandle handle, String str) {
        FileWriter writer;
        try {
            writer = new FileWriter("assets\\" + handle.file());
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
