package anotherbot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Dictionary {
    // TODO
    private final Charset CHAR_SET = Charset.forName("utf-8");
    private String filename;

    public Dictionary(String filename) {
        this.filename = filename;
        if (!Util.fileExists(filename)) {
            Util.makeFile(this.filename, CHAR_SET);
        }
    }

    public void save(Collection<String> lines) { // a collection can be a set, a
                                                 // list, etc
        ArrayList<String> contents = new ArrayList<String>(lines);
        Iterator<String> it = contents.iterator();
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter(filename, true)))) {
            while (it.hasNext()) {

                out.println(it.next()); // this will append anything to the end
                                        // of the file, in other words it wont
                                        // overwrite the file
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public String getFilename() {
        return this.filename;
    }
}