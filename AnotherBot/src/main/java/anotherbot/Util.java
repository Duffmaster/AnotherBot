package anotherbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Util {
    public static void makeFile(String filename, String[] lines,
            Charset encoding) {
        if (fileExists(filename))
            return;// so we dont overwrite an already existing file

        BufferedWriter writer = null; // side note: writer.close() will not work
                                      // if we put this inside the try block
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), encoding));
            for (int i = 0; i < lines.length; i++) {
                writer.write(lines[i]);
                writer.newLine();
            }
        } catch (IOException x) {
            x.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public static void makeFile(String filename, Charset encoding) {
        if (fileExists(filename))
            return;
        BufferedWriter writer = null; // side note: writer.close() will not work
                                      // if we put this inside the try block
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), encoding));
            writer.write("");
        } catch (IOException x) {
            x.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    public static boolean fileExists(String filename) {
        if (new File(filename).exists())
            return true;
        else
            return false;
    }

    public static ArrayList<String> getFileContents(String filename)
            throws IOException, FileNotFoundException {
        // StringBuilder content = new StringBuilder();
        ArrayList<String> content = new ArrayList<String>();
        try (LineNumberReader reader = new LineNumberReader(new FileReader(
                filename))) { // why am i using linenumberreader?
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.add(line);
            }
        }
        return content;
    }
    
    public void printFileContents(String filename) throws IOException, FileNotFoundException {
        //ArrayList<String> content = new ArrayList<String>();
        try (LineNumberReader reader = new LineNumberReader(new FileReader(
                filename))) { // why am i using linenumberreader?
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(reader.getLineNumber()+" "+line);
            }
        }
    }

}
