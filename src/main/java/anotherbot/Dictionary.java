package anotherbot;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Dictionary {
    // TODO
    private final String SEPARATOR = ":";
    private final Charset CHAR_SET = Charset.forName("utf-8");

    private String filename;

    public Dictionary(String filename) {
        this.filename = filename;
        if (!Util.fileExists(filename)) {
            Util.makeFile(this.filename, CHAR_SET);
        }
    }

    // this should mean overwrite the current contents. of course it probably
    // wont end up doing that.
    public void save(Collection<String> lines) throws FileNotFoundException,
            IOException { // a collection can be a set, a
        // list, etc
        ArrayList<String> linesToWrite = new ArrayList<String>(lines);
        Iterator<String> it = linesToWrite.iterator();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(filename, false)))) {// false=don't append
            while (it.hasNext()) {
                out.println(it.next());
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    // saveOld is just appending whatever text is sent to it to the end of the
    // file. for testing.
    public void saveOld(Collection<String> lines) throws FileNotFoundException,
            IOException { // a collection can be a set, a
        // list, etc
        ArrayList<String> linesToWrite = new ArrayList<String>(lines);
        Iterator<String> it = linesToWrite.iterator();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(filename, true)))) { // true=append
            while (it.hasNext()) {
                out.println(it.next() + SEPARATOR); // this will append anything
                                                    // to the end
                // of the file, in other words it wont
                // overwrite the file
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void saveLeftSide(String word) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(filename, true)))) {
            out.println(word + ":");
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void saveRightSide(String leftSide, String wordRight)
            throws FileNotFoundException, IOException {
        ArrayList<String> dictionaryContents = Util
                .getFileContents(this.filename); // apparently to avoid the
        // nightmarish hell that is
        // RandomAccessFile we have to
        // look for the left side word
        // in the arraylist, modify it
        // there, then rewrite the
        // entire file with the new
        // arraylist
        // System.out.println(dictionaryContents.toString());
    }

    public String getFilename() {
        return this.filename;
    }

    // TODO
    // should get each word before a colon
    public ArrayList<String> getLeftSide() {
        return null;
    }

}