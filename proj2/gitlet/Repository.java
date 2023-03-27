package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File INDEX = join(GITLET_DIR, "index");
    public static final File BRANCHES = join(GITLET_DIR, "branches");
    public static final File HEAD = join(GITLET_DIR, "head");

    /* TODO: fill in the rest of this class. */
    public static void initRepo() throws IOException {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        INDEX.createNewFile(); //Index
        HEAD.createNewFile(); //String
        BRANCHES.createNewFile(); //HashMap<String, String>

        Index index = new Index();
        index.save();

        HashMap<String, String> branches = new HashMap<>();

        Commit root = new Commit("Init Repo", new HashMap<String, String>(), new String[2]);
        String commitID = sha1(root.toString());

        root.save(commitID);
        writeContents(HEAD, commitID);
        branches.put("master", commitID);
        writeObject(BRANCHES, branches);
    }

    public static void addOneFile(String file) throws IOException {
        /** Check file existence. */
        checkIfGitletDir();
        File newFile = join(CWD, file);
        if (!newFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        /** Store the contents of the file. */
        byte[] fileContents = readContents(newFile);
        String contentsID = sha1(fileContents);
        File blobPrefix = join(BLOBS_DIR, contentsID.substring(0, 2));
        if (!blobPrefix.exists()) {
            blobPrefix.mkdir();
        }
        File blob = join(blobPrefix, contentsID.substring(2));
        if (!blob.exists()) {
            blob.createNewFile();
            writeContents(blob, fileContents);
        }

        /** Update the stagingArea. */
        Index stagingArea = Index.readIndex(INDEX);
        Commit headCommit = getHeadCommit();
        stagingArea.add(file, contentsID, headCommit);
        stagingArea.save();
    }

    public static void commit(String message) {
        checkIfGitletDir();
        Index stagingArea = Index.readIndex(INDEX);
        if (stagingArea.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        /** Copy the headCommit's blobs */
        String headID = readContentsAsString(HEAD);
        Commit headCommit = getHeadCommit();
        HashMap<String, String> newHeadBlobs = headCommit.blobsClone();

        /** Modify the new hashMap according to the stagingArea.*/
        for (Map.Entry<String, String> entry: stagingArea.getStaged().entrySet()) {
            newHeadBlobs.put(entry.getKey(), entry.getValue());
        }

        /** Create a new Commit object and save it. */
        String[] parents = new String[2];
        parents[0] = headID;
        Commit newCommit = new Commit(message, newHeadBlobs, parents);
        String newCommitID = sha1(newCommit.toString());
        newCommit.save(newCommitID);

        /** Clear stagingArea and save it */
        stagingArea.clear();
        stagingArea.save();

        /** Modify and save the head pointer and master pointer */
        writeContents(HEAD, newCommitID);
        HashMap<String, String> branches = readObject(BRANCHES, HashMap.class);
        branches.put("master", newCommitID);
        writeObject(BRANCHES, branches);
    }

    public static void removeOneFile(String fileName) {
        Index stagingArea = Index.readIndex(INDEX);
        Commit headCommit = getHeadCommit();
        if (stagingArea.getStaged().containsKey(fileName)) {
            stagingArea.getStaged().remove(fileName);
            stagingArea.save();
            return;
        }
        if (headCommit.getBlobs().containsKey(fileName)) {
            stagingArea.getDeleted().put(fileName, "DELETE");
            stagingArea.save();
            return;
        }
        System.out.println("No reason to remove the file.");
    }

    public static void printLog() {
        Commit headCommit = getHeadCommit();
        String head = readContentsAsString(HEAD);
        while (!headCommit.getMessage().equals("Init Repo")) {
            System.out.println(headCommit.toString(head));
            head = headCommit.getParents()[0];
            headCommit = Commit.readCommit(head);
        }
        System.out.println(headCommit.toString(head));
    }

    public static void printGlobalLog() {
        File[] prefixList = COMMITS_DIR.listFiles();
        for (File prefix : prefixList) {
            for (String fileName : plainFilenamesIn(prefix)) {
                File file = join(prefix, fileName);
                Commit commit = readObject(file, Commit.class);
                System.out.println(commit.toString(prefix + fileName));
            }
        }
    }
    private static void checkIfGitletDir() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    private static Commit getHeadCommit() {
        String head = readContentsAsString(HEAD);
        return Commit.readCommit(head);
    }

    public static void findCommit(String arg) {
        File[] prefixList = COMMITS_DIR.listFiles();
        for (File prefix : prefixList) {
            for (String fileName : plainFilenamesIn(prefix)) {
                File file = join(prefix, fileName);
                Commit commit = readObject(file, Commit.class);
                if (commit.getMessage().equals(arg)) {
                    System.out.println(commit.toString(prefix + fileName));
                }
            }
        }
    }

    public static void printStatus() {
        HashMap<String, String> branches = readObject(BRANCHES, HashMap.class);
        Commit headCommit = getHeadCommit();
        Index stagingArea = Index.readIndex(INDEX);
        String headID = readContentsAsString(HEAD);

        StringBuilder sb  = new StringBuilder();
        /** Print branches. */
        sb.append("=== Branches ===\n");
        for (Map.Entry<String, String> entry : branches.entrySet()) {
            if (entry.getValue().equals(headID)) {
                sb.append("*" + entry.getKey() + "\n");
            } else {
                sb.append((entry.getKey()) + "\n");
            }
        }

        /** Print Staged Files */
        sb.append("\n=== Staged Files ===\n");
        for (Map.Entry<String, String> entry : stagingArea.getStaged().entrySet()) {
            sb.append(entry.getKey() + "\n");
        }

        /** Print Removed Files */
        sb.append("\n=== Removed Files ===\n");
        for (Map.Entry<String, String> entry : stagingArea.getDeleted().entrySet()) {
            sb.append(entry.getKey() + "\n");
        }

        /**Print modification not staged for commit.*/
        //sb.append("\n=== Modification Not Staged for Commit ===\n");

        System.out.println(sb);
    }

    public static void checkout(String[] args) {
    }
}
