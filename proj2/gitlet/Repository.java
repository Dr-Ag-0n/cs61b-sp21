package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author DrAgOn
 */
public class Repository implements Serializable {
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
    /** The directory to store commits */
    public static final File COMMITS_DIR = join(GITLET_DIR, "Commits");
    /** The directory of staging area */
    public static final File STAGING_DIR = join(GITLET_DIR, "Staging_Area");
    public static final File STAGED_FOR_ADDITION = join(STAGING_DIR, "Staged_for_addition");
    public static final File STAGED_FOR_REMOVAL = join(STAGING_DIR, "Staged_for_removal");
    /** The directory to store blobs */
    public static final File BLOBS_DIR = join(GITLET_DIR, "Blobs");

    /* TODO: fill in the rest of this class. */
    public static void initialize() {
        /* Create .gitlet directory and subdirectory init */
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            COMMITS_DIR.mkdir();
            STAGING_DIR.mkdir();
            STAGED_FOR_ADDITION.mkdir();
            STAGED_FOR_REMOVAL.mkdir();
            BLOBS_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }

        /* Create initial commit */
        Commit initCommit = new Commit("initial commit", "00:00:00 UTC, Thursday, 1 January 1970");
        String commitHash = sha1(serialize(initCommit));
        File commit = join(COMMITS_DIR, commitHash);
        writeObject(commit, initCommit);
        /* Save HEAD and MASTER information */
        File head = join(GITLET_DIR, "HEAD");
        writeContents(head, commitHash);
        File master = join(GITLET_DIR, "MASTER");
        writeContents(master, commitHash);
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        File addition = join(STAGED_FOR_ADDITION, fileName);
        /* Write file into blob */
        byte[] blobContents = readContents(addFile);
        String blobHash = sha1(blobContents);
        File blob = join(BLOBS_DIR, blobHash);
        writeContents(blob, blobContents);
        /* Staged for addition */
        writeContents(addition, blobHash);
        /*
         * Compare file's hash code with current commit's file,
         * remove the addition if current blob is the same as the current commit's.
         */
        File head = join(GITLET_DIR, "HEAD");
        String curCommitHash = readContentsAsString(head);
        File cur = join(COMMITS_DIR, curCommitHash);
        Commit curCommit = readObject(cur, Commit.class);
        String curBlob = curCommit.returnBlobHash(fileName);
        if (curBlob != null) {
            if (curBlob.equals(blobHash)) {
                addition.delete();
            }
        }
    }

    public static void makeCommit(String message) {
        /* Read head commit */
        File head = join(GITLET_DIR, "HEAD");
        String curCommitHash = readContentsAsString(head);
        File cur = join(COMMITS_DIR, curCommitHash);
        Commit curCommit = readObject(cur, Commit.class);
        /* Copy head commit */
        Commit newCommit = new Commit(curCommit);
        /* Add new track */
        List<String> additionFiles = plainFilenamesIn(STAGED_FOR_ADDITION);
        if (additionFiles != null) {
            for (String name: additionFiles) {
                File newAddition = join(STAGED_FOR_ADDITION, name);
                String blobName = readContentsAsString(newAddition);
                newCommit.storeTrack(name, blobName);
                newAddition.delete();
            }
        }
        /* Remove the track in the staged area for removal */
        List<String> removalFiles = plainFilenamesIn(STAGED_FOR_REMOVAL);
        if (removalFiles != null) {
            for (String name: removalFiles) {
                newCommit.rmTrack(name);
                File newRemoval = join(STAGED_FOR_REMOVAL, name);
                newRemoval.delete();
            }
        }
        /* Set parent commit */
        newCommit.setParentCommit(curCommitHash);
        /* Change the commit message */
        newCommit.setMessage(message);
        /* Set time */
        newCommit.setCurTime();
        /* Store the commit */
        String commitHash = sha1(serialize(newCommit));
        File commit = join(COMMITS_DIR, commitHash);
        writeObject(commit, newCommit);
        /* Move HEAD and MASTER point */
        File newHead = join(GITLET_DIR, "HEAD");
        writeContents(newHead, commitHash);
        File newMaster = join(GITLET_DIR, "MASTER");
        writeContents(newMaster, commitHash);
    }

    public static void rm(String fileName) {
        /* If the file is in the staging area for addition,
         *  just remove the track form staging_for_removal.
         */
        File StAddFile = join(STAGED_FOR_ADDITION, fileName);
        if (StAddFile.exists()) {
            StAddFile.delete();
        }
        /* If the file is tracked in the current commit tracks,
         *  stage it for removal and remove the file from the working directory if the user has not already done so.
         */
        File head = join(GITLET_DIR, "HEAD");
        String curCommitHash = readContentsAsString(head);
        File cur = join(COMMITS_DIR, curCommitHash);
        Commit curCommit = readObject(cur, Commit.class);
        String fileBlobHash = curCommit.returnTrack(fileName);
        if (fileBlobHash != null) {
            File removal = join(STAGED_FOR_REMOVAL, fileName);
            File file = join(CWD, fileName);
            if (file.exists()) {
                file.delete();
                writeContents(removal, fileBlobHash);
                return;
            }
        }
        /* Failure case */
        System.out.println("No reason to remove the file.");
        System.exit(0);
    }

    public static void log() {
        /* Get HEAD commit */
        File head = join(GITLET_DIR, "HEAD");
        String curCommitHash = readContentsAsString(head);
        File cur = join(COMMITS_DIR, curCommitHash);
        Commit curCommit = readObject(cur, Commit.class);
        /* Print first commit */
        System.out.println("===");
        String commitHash = readContentsAsString(head);
        System.out.println("commit " + commitHash);
        System.out.println("Date: " + curCommit.returnTime());
        System.out.println(curCommit.returnMes());
        System.out.println();
        /* Print remaining commit */
        String preCommitHash = curCommit.returnParent();
        while (preCommitHash != null) {
            File pre = join(COMMITS_DIR, preCommitHash);
            Commit preCommit = readObject(pre, Commit.class);
            System.out.println("===");
            System.out.println("commit " + preCommitHash);
            System.out.println("Date: " + preCommit.returnTime());
            System.out.println(preCommit.returnMes());
            System.out.println();
            preCommitHash = preCommit.returnParent();
        }
    }
}
