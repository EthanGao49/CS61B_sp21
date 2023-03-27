package gitlet;

// import jh61b.junit.In;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import static gitlet.Repository.INDEX;
import static gitlet.Utils.*;

public class Index implements Serializable {
    private HashMap<String, String> staged;
    private HashMap<String, String> deleted;

    public void save() {
        writeObject(INDEX, this);
    }

    public Index() {
        staged = new HashMap<>();
        deleted = new HashMap<>();
    }

    public static Index readIndex(File index) {
        if (index.exists()) {
            return readObject(index, Index.class);
        }
        System.out.println("None such head file exists.");
        return new Index();
    }

    public void add(String fileName, String contentsID, Commit headCommit) {
        staged.remove(fileName);
        if (contentsID.equals(headCommit.getBlobs().get(fileName))) {
            return;
        }
        staged.put(fileName, contentsID);
    }

    public boolean isEmpty() {
        return staged.isEmpty() && deleted.isEmpty();
    }

    public void clear() {
        staged.clear();
        deleted.clear();
    }

    public HashMap<String, String> getStaged() {
        return staged;
    }

    public HashMap<String, String> getDeleted() {
        return deleted;
    }
}
