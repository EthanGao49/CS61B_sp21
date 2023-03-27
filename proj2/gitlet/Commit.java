package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.Map;

import static gitlet.Repository.COMMITS_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Yichun Gao
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this commit. */
    private Date Date;
    /**
     * The blobs of this commit.
     * A hashMap from file name to SHA1 ID.
     */
    private HashMap<String, String> blobs;
    private String[] parents;

    /* TODO: fill in the rest of this class. */
    public Commit(String message, HashMap<String, String> blobs, String[] parent) {
        this.message = message;
        this.blobs = blobs;
        this.parents = parent;
        this.Date = new Date();
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public Date getDate() {
        return Date;
    }

    public String[] getParents() {
        return parents;
    }

    public void save(String ID) {
        File commitPrefix = join(COMMITS_DIR ,ID.substring(0, 2));
        if (!commitPrefix.exists()) {
            commitPrefix.mkdir();
        }
        writeObject(join(commitPrefix, ID.substring(2)), this);
    }

    public static Commit readCommit (String ID) {
        File commitPrefix = join(COMMITS_DIR, ID.substring(0, 2));
        File commit = join(commitPrefix, ID.substring(2));
        if (commit.exists()) {
            return readObject(commit, Commit.class);
        }
        System.out.println("None such commit exists.");
        return null;
    }

    public String toString(String commitID) {
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        sb.append("Commit: " + commitID + "\n");
        if (parents[1] != null) {
            sb.append("Merged: " + parents[0].substring(0, 10) + parents[1].substring(0, 10));
        }
        sb.append("Date: " + getDate().toString() + "\n");
        sb.append("Message: " + message + "\n");
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        sb.append("Date: " + getDate().toString() + "\n");
        sb.append("Message: " + message + "\n");
        sb.append("Blobs: " + blobs.toString() + "\n");
        sb.append("First Parent: " + parents[0] + "\n");
        return sb.toString();
    }

    public HashMap<String, String> blobsClone() {
        HashMap<String, String> newBlobs = new HashMap<>();
        for (Map.Entry<String, String> entry : blobs.entrySet()) {
            newBlobs.put(entry.getKey(), entry.getValue());
        }
        return newBlobs;
    }

}
