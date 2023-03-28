package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Yichun Gao
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
        branches.put("head", "master");
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
        for (Map.Entry<String, String> entry : stagingArea.getStaged().entrySet()) {
            newHeadBlobs.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : stagingArea.getDeleted().entrySet()) {
            newHeadBlobs.remove(entry.getKey());
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
        String currBranch = branches.get("head");
        branches.put(currBranch, newCommitID);
        writeObject(BRANCHES, branches);
    }

    public static void removeOneFile(String fileName) {
        checkIfGitletDir();
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
            File file = join(CWD, fileName);
            file.delete();
            return;
        }
        System.out.println("No reason to remove the file.");
    }

    public static void printLog() {
        checkIfGitletDir();
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
        checkIfGitletDir();
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

    private static byte[] getBlobContents(String contentsID) {
        File filePrefix = join(BLOBS_DIR, contentsID.substring(0, 2));
        File file = join(filePrefix, contentsID.substring(2));
        byte[] fileContents = readContents(file);
        return fileContents;
    }

    public static void findCommit(String arg) {
        checkIfGitletDir();
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
        checkIfGitletDir();
        HashMap<String, String> branches = readObject(BRANCHES, HashMap.class);
        Commit headCommit = getHeadCommit();
        Index stagingArea = Index.readIndex(INDEX);
        String headID = readContentsAsString(HEAD);

        StringBuilder sb  = new StringBuilder();
        /** Print branches. */
        sb.append("=== Branches ===\n");
        for (Map.Entry<String, String> entry : branches.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("head")) {
                if (key.equals(branches.get("head"))) {
                    sb.append("*" + key + "\n");
                } else {
                    sb.append(key + "\n");
                }
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

        /** Modified files*/
        File[] fileList = CWD.listFiles(File::isFile);
        sb.append("\n=== Modified files ===\n");
        for (File file : fileList) {
            if ( (! stagingArea.containKey(file.getName())) && (! unModifiedCheck(file))) {
                sb.append(file.getName() + "\n");
            }
        }

        /** Untracked files. */
        sb.append("\n=== Untracked Files ===\n");
        for (File file : fileList) {
            if (! trackedCheck(file)) {
                sb.append(file.getName() + "\n");
            }
        }

        System.out.println(sb);
    }

    public static void checkout(String[] args) throws IOException {
        checkIfGitletDir();
        if (args.length == 3 && args[1].equals("--")) {
            checkoutFile(args[2]);
        } else if (args.length == 2) {
            checkoutBranch(args[1]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkoutFile(args[1], args[3]);
        }
    }

    private static boolean unModifiedCheck(File file) {
        Commit headCommit = getHeadCommit();
        byte[] fileContent = readContents(file);
        String contentID = sha1(fileContent);
        if (headCommit.getBlobs().containsKey(file.getName())) {
            return contentID.equals(headCommit.getBlobs().get(file.getName()));
        }
        /** File is not contained in the head commit. */
        return false;
    }

    private static boolean trackedCheck(File file) {
        Commit headCommit = getHeadCommit();
        Index stagingArea = Index.readIndex(INDEX);
        return stagingArea.containKey(file.getName()) || headCommit.getBlobs().containsKey(file.getName());
    }

    /** Checkout a file to the version in the most recent commit. */
    private static void checkoutFile(String fileName) throws IOException {
        Commit headCommit = getHeadCommit();
        HashMap<String, String> blobs = headCommit.getBlobs();
        if (!blobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File file = join(CWD, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        String contentsID = blobs.get(fileName);
        byte[] fileContents = getBlobContents(contentsID);
        writeContents(file, fileContents);
    }

    /** Checkout a file to the specific version according to the commit id. */
    private static void checkoutFile(String commitID, String fileName) throws IOException {
        Commit targetCommit = Commit.readCommit(commitID);
        HashMap<String, String> blobs = targetCommit.getBlobs();
        if (!blobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        File file = join(CWD, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        String contentsID = blobs.get(fileName);
        byte[] fileContents = getBlobContents(contentsID);
        writeContents(file, fileContents);
    }

    private static void checkoutCommit(String commitID) throws IOException {
        Commit targetCommit = Commit.readCommit(commitID);
        File[] fileList = CWD.listFiles(File::isFile);

        /** Delete the files that are not contained in target commit. */
        for (File file : fileList) {
            if (! targetCommit.contain(file)) {
                restrictedDelete(file);
            }
        }

        /** Overwrite the files in the work space. */
        HashMap<String, String> blobs = targetCommit.blobsClone();
        for (Map.Entry<String, String> entry : blobs.entrySet()) {
            File file = join(CWD, entry.getKey());
            if (! file.exists()) {
                file.createNewFile();
            }
            byte[] content = getBlobContents(entry.getValue());
            writeContents(file, content);
        }
    }

    /** TODO: This function has not been tested. */
    private static void checkoutBranch(String branchName) throws IOException {
        HashMap<String, String> branches = readObject(BRANCHES, HashMap.class);
        String head = readContentsAsString(HEAD);
        Commit headCommits = getHeadCommit();
        Index stagingArea = Index.readIndex(INDEX);
        if (!branches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }

        String branchID = branches.get(branchName);
        if (branches.get("head").equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        File[] currFileList = CWD.listFiles(File::isFile);
        for (File file : currFileList) {
            if ((!stagingArea.isEmpty()) || (!unModifiedCheck(file)) || (!trackedCheck(file)) ){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        //Todo: Change the current work space to the version in target branch.
        checkoutCommit(branchID);

        writeContents(HEAD, branchID);
        branches.put("head", branchName);
        writeObject(BRANCHES, branches);
    }

    public static void createNewBranch(String branchName) {
        checkIfGitletDir();
        HashMap<String, String> branches = readObject(BRANCHES, HashMap.class);
        String head = readContentsAsString(HEAD);
        branches.put(branchName, head);
        branches.put("head", branchName);
        writeObject(BRANCHES, branches);
    }


    public static void deleteBranch(String branchName) {
        HashMap<String, String> branches = readObject(BRANCHES, HashMap.class);

        if (!branches.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }

        if (branches.get("head").equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        branches.remove(branchName);
        writeObject(BRANCHES, branches);
    }

    public static void mergeBranch(String branchName) {

    }
}
