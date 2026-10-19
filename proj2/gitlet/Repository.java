package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author DrAgOn
 */
public class Repository implements Serializable {
    /**
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
    /** The directory to store branches */
    public static final File BRANCHES_DIR = join(GITLET_DIR, "Branches");

    public static void initialize() {
        /* Create .gitlet directory and subdirectory init */
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            COMMITS_DIR.mkdir();
            STAGING_DIR.mkdir();
            STAGED_FOR_ADDITION.mkdir();
            STAGED_FOR_REMOVAL.mkdir();
            BLOBS_DIR.mkdir();
            BRANCHES_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists"
                    + " in the current directory.");
            System.exit(0);
        }

        /* Create initial commit */
        Commit initCommit = new Commit("initial commit");
        String commitHash = sha1(serialize(initCommit));
        File commit = join(COMMITS_DIR, commitHash);
        writeObject(commit, initCommit);
        /* Save HEAD and MASTER information */
        File head = join(GITLET_DIR, "HEAD");
        writeContents(head, "master");
        File master = join(BRANCHES_DIR, "master");
        writeContents(master, commitHash);
    }

    public static void add(String fileName) {
        File addFile = join(CWD, fileName);
        if (!addFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        /* Write file into blob */
        byte[] blobContents = readContents(addFile);
        String blobHash = sha1(blobContents);
        File blob = join(BLOBS_DIR, blobHash);
        writeContents(blob, blobContents);
        /* Staged for addition */
        File addition = join(STAGED_FOR_ADDITION, fileName);
        writeContents(addition, blobHash);
        /* Remove the removal staging */
        File removal = join(STAGED_FOR_REMOVAL, fileName);
        if (removal.exists()) {
            removal.delete();
        }
        /*
         * Compare file's hash code with current commit's file,
         * remove the addition if current blob is the same as the current commit's.
         *
         */
        Commit curCommit = returnHeadCommit();
        String curBlobHash = curCommit.returnTrack(fileName);
        if (curBlobHash != null) {
            if (curBlobHash.equals(blobHash)) {
                addition.delete();
            }
        }
    }

    /** Return the branch's name that the HEAD pointer points */
    private static String returnHeadName() {
        File head = join(GITLET_DIR, "HEAD");
        return readContentsAsString(head);
    }

    /** Return the branch's latest commit that the HEAD pointer points */
    private static Commit returnHeadCommit() {
        String curBranch = returnHeadName();
        File branch = join(BRANCHES_DIR, curBranch);
        String curCommitHash = readContentsAsString(branch);
        File curCommit = join(COMMITS_DIR, curCommitHash);
        return readObject(curCommit, Commit.class);
    }

    public static void makeCommit(String message) {
        /* Failure case */
        List<String> addition = plainFilenamesIn(STAGED_FOR_ADDITION);
        List<String> removal = plainFilenamesIn(STAGED_FOR_REMOVAL);
        if (addition.isEmpty() && removal.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        /* Read head commit */
        Commit curCommit = returnHeadCommit();
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
        String curBranchName = returnHeadName();
        File curBranch = join(BRANCHES_DIR, curBranchName);
        newCommit.setParentCommit(readContentsAsString(curBranch));
        /* Change the commit message */
        newCommit.setMessage(message);
        /* Set time */
        newCommit.setCurTime();
        /* Store the commit */
        String commitHash = sha1(serialize(newCommit));
        File commit = join(COMMITS_DIR, commitHash);
        writeObject(commit, newCommit);
        /* Move current branch point */
        File newBranch = join(BRANCHES_DIR, returnHeadName());
        writeContents(newBranch, commitHash);
    }

    public static void rm(String fileName) {
        boolean err = true;
        /* If the file is in the staging area for addition,
         *  just remove the track form staging_for_removal.
         */
        File stAddFile = join(STAGED_FOR_ADDITION, fileName);
        if (stAddFile.exists()) {
            stAddFile.delete();
            err = false;
        }
        /* If the file is tracked in the current commit tracks,
         * stage it for removal and remove the file from the working directory
         * if the user has not already done so.
         */
        Commit curCommit = returnHeadCommit();
        String fileBlobHash = curCommit.returnTrack(fileName);
        if (fileBlobHash != null) {
            File removal = join(STAGED_FOR_REMOVAL, fileName);
            File file = join(CWD, fileName);
            if (file.exists()) {
                file.delete();
            }
            writeContents(removal, fileBlobHash);
            err = false;
        }
        /* Failure case */
        if (err) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    public static void log() {
        /* Get HEAD commit */
        Commit curCommit = returnHeadCommit();
        /* Print first commit */
        System.out.println("===");
        String curBranchName = returnHeadName();
        File curBranch = join(BRANCHES_DIR, curBranchName);
        System.out.println("commit " + readContentsAsString(curBranch));
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

    public static void status() {
        System.out.println("=== Branches ===");
        List<String> branches = plainFilenamesIn(BRANCHES_DIR);
        String cur = returnHeadName();
        if (branches != null) {
            for (String branch: branches) {
                if (branch.equals(cur)) {
                    System.out.print("*");
                }
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        List<String> additions = plainFilenamesIn(STAGED_FOR_ADDITION);
        if (additions != null) {
            for (String addition: additions) {
                System.out.println(addition);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        List<String> removals = plainFilenamesIn(STAGED_FOR_REMOVAL);
        if (removals != null) {
            for (String removal: removals) {
                System.out.println(removal);
            }
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    public static void checkoutFile(String fileName) {
        /* Get HEAD commit */
        Commit curCommit = returnHeadCommit();
        String blobHash = curCommit.returnTrack(fileName);
        if (blobHash == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        /* Resume the file from current commit */
        File file = join(CWD, fileName);
        File fileBlob = join(BLOBS_DIR, blobHash);
        byte[] content = readContents(fileBlob);
        writeContents(file, content);
    }

    public static void checkoutCommitFile(String commitHash, String fileName) {
        String wholeCommitHash = returnCommitHash(commitHash);
        if (wholeCommitHash == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        File commit = join(COMMITS_DIR, wholeCommitHash);
        Commit com = readObject(commit, Commit.class);
        String blobHash = com.returnTrack(fileName);
        if (blobHash == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File file = join(CWD, fileName);
        File fileBlob = join(BLOBS_DIR, blobHash);
        byte[] content = readContents(fileBlob);
        writeContents(file, content);
    }

    /* Check if it exists an untracked file in the current commit
     * would be overwritten or deleted.
     */
    private static void hasUntrackedConflict(Set<String> affectedFiles) {
        List<String> curFileNames = plainFilenamesIn(CWD);
        String curCommitHash = returnBranchCommitHash(returnHeadName());
        Set<String> curTracks = returnTrackFiles(curCommitHash);
        List<String> additionFiles = plainFilenamesIn(STAGED_FOR_ADDITION);
        List<String> removalFiles = plainFilenamesIn(STAGED_FOR_REMOVAL);
        for (String f: curFileNames) {
            if (affectedFiles.contains(f)) {
                if ((!additionFiles.contains(f) && !curTracks.contains(f)) || removalFiles.contains(f)) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
    }

    public static void checkoutBranch(String branchName) {
        /* Failure cases */
        File branch = join(BRANCHES_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        if (returnHeadName().equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        String desCommitHash = returnBranchCommitHash(branchName);
        Set<String> desTracks = returnTrackFiles(desCommitHash);
        hasUntrackedConflict(desTracks);
        /* Clean current tracked files */
        Set<String> curTracks = returnTrackFiles(returnBranchCommitHash(returnHeadName()));
        if (curTracks != null) {
            for (String name: curTracks) {
                File curFile = join(CWD, name);
                curFile.delete();
            }
        }
        /* Retrieve all files */
        String branchCommitHash = returnBranchCommitHash(branchName);
        File branchCommit = join(COMMITS_DIR, branchCommitHash);
        Commit commit = readObject(branchCommit, Commit.class);
        Set<String> fileNames = commit.returnTracks();
        for (String fileName: fileNames) {
            File file = join(CWD, fileName);
            String fileBlob = commit.returnTrack(fileName);
            File blob = join(BLOBS_DIR, fileBlob);
            byte[] contents = readContents(blob);
            writeContents(file, contents);
        }
        /* Clean the staging area */
        List<String> additionFiles = plainFilenamesIn(STAGED_FOR_ADDITION);
        List<String> removalFiles = plainFilenamesIn(STAGED_FOR_REMOVAL);
        if (additionFiles != null) {
            for (String name: additionFiles) {
                File newAddition = join(STAGED_FOR_ADDITION, name);
                newAddition.delete();
            }
        }
        if (removalFiles != null) {
            for (String name: removalFiles) {
                File newRemoval = join(STAGED_FOR_REMOVAL, name);
                newRemoval.delete();
            }
        }
        /* Change the HEAD pointer */
        File head = join(GITLET_DIR, "HEAD");
        writeContents(head, branchName);
    }

    /** Return the commit's hash that given branch points */
    private static String returnBranchCommitHash(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        return readContentsAsString(branch);
    }

    /** Return files' name that given commit tracks */
    private static Set<String> returnTrackFiles(String commitHash) {
        File com = join(COMMITS_DIR, commitHash);
        Commit commit = readObject(com, Commit.class);
        return commit.returnTracks();
    }

    /** Return the whole commit's hash with complete or shorten commitHash, null if not exists */
    private static String returnCommitHash(String commitHash) {
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);
        for (String s: commitList) {
            if (s.startsWith(commitHash)) {
                return s;
            }
        }
        return null;
    }

    public static void makeBranch(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        if (branch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File head = join(GITLET_DIR, "HEAD");
        String currentCommit = returnBranchCommitHash(readContentsAsString(head));
        writeContents(branch, currentCommit);
    }

    private static void printCommit(String commitHash) {
        File c = join(COMMITS_DIR, commitHash);
        Commit commit = readObject(c, Commit.class);
        System.out.println("===");
        System.out.println("commit " + commitHash);
        System.out.println("Date: " + commit.returnTime());
        System.out.println(commit.returnMes());
        System.out.println();
    }

    public static void globLog() {
        List<String> commitsHash = plainFilenamesIn(COMMITS_DIR);
        if (commitsHash != null) {
            for (String commitHash: commitsHash) {
                printCommit(commitHash);
            }
        }
    }

    public static void find(String s) {
        boolean find = false;
        List<String> commitsHash = plainFilenamesIn(COMMITS_DIR);
        if (commitsHash != null) {
            for (String commitHash: commitsHash) {
                File c = join(COMMITS_DIR, commitHash);
                Commit commit = readObject(c, Commit.class);
                String message = commit.returnMes();
                if (message.contains(s)) {
                    find = true;
                    System.out.println(commitHash);
                }
            }
        }
        if (!find) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    public static void rmBranch(String branchName) {
        File branch = join(BRANCHES_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String cur = returnHeadName();
        if (cur.equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branch.delete();
    }

    public static void reset(String commitHash) {
        String headName = returnHeadName();
        File head = join(BRANCHES_DIR, headName);
        File commit = join(COMMITS_DIR, commitHash);
        if (!commit.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Set<String> desTracks = returnTrackFiles(commitHash);
        hasUntrackedConflict(desTracks);
        /* Clean current tracked files */
        Set<String> curTracks = returnTrackFiles(returnBranchCommitHash(returnHeadName()));
        if (curTracks != null) {
            for (String name: curTracks) {
                File curFile = join(CWD, name);
                curFile.delete();
            }
        }
        /* Retrieve all files */
        Commit desCommit = readObject(commit, Commit.class);
        Set<String> fileNames = desCommit.returnTracks();
        for (String fileName: fileNames) {
            File file = join(CWD, fileName);
            String fileBlob = desCommit.returnTrack(fileName);
            File blob = join(BLOBS_DIR, fileBlob);
            byte[] contents = readContents(blob);
            writeContents(file, contents);
        }
        /* Clean the staging area */
        List<String> additionFiles = plainFilenamesIn(STAGED_FOR_ADDITION);
        List<String> removalFiles = plainFilenamesIn(STAGED_FOR_REMOVAL);
        if (additionFiles != null) {
            for (String name: additionFiles) {
                File newAddition = join(STAGED_FOR_ADDITION, name);
                newAddition.delete();
            }
        }
        if (removalFiles != null) {
            for (String name: removalFiles) {
                File newRemoval = join(STAGED_FOR_REMOVAL, name);
                newRemoval.delete();
            }
        }
    }

    public static void merge(String branchName) {
        /* Failure case */
        List<String> additionFiles = plainFilenamesIn(STAGED_FOR_ADDITION);
        List<String> removalFiles = plainFilenamesIn(STAGED_FOR_REMOVAL);
        if (!additionFiles.isEmpty() || !removalFiles.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        File branch = join(BRANCHES_DIR, branchName);
        if (!branch.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String curBranchName = returnHeadName();
        if (curBranchName.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        String branchCommitHash = returnBranchCommitHash(branchName);
        File branchCommitFile = join(COMMITS_DIR, branchCommitHash);
        Commit branchCommit = readObject(branchCommitFile, Commit.class);
        /* Untracked files checking */
        Set<String> affectedFiles = new HashSet<>();
        Set<String> totalFiles = new HashSet<>();
        Set<String> branchFiles = branchCommit.returnTracks();
        Commit head = returnHeadCommit();
        Set<String> headFiles = head.returnTracks();
        File splitCommit = join(COMMITS_DIR, findSplitCommit(branchName));
        Commit split = readObject(splitCommit, Commit.class);
        Set<String> splitFiles = split.returnTracks();
        totalFiles.addAll(headFiles);
        totalFiles.addAll(branchFiles);
        totalFiles.addAll(splitFiles);
        for (String fileName: totalFiles) {
            MergeAction out = findMergeAction(split.returnTrack(fileName), head.returnTrack(fileName),
                    branchCommit.returnTrack(fileName));
            if (!out.equals(MergeAction.KEEP)) {
                affectedFiles.add(fileName);
            }
        }
        hasUntrackedConflict(affectedFiles);
        /* Two special cases */
        String splitCommitHash = findSplitCommit(branchName);
        String headCommitHash = returnBranchCommitHash(returnHeadName());
        if (splitCommitHash.equals(branchCommitHash)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitCommitHash.equals(headCommitHash)) {
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        boolean hasConflict = false;
        for (String fileName: totalFiles) {
            MergeAction out = findMergeAction(split.returnTrack(fileName), head.returnTrack(fileName),
                    branchCommit.returnTrack(fileName));
            hasConflict = execMerge(out, fileName, head.returnTrack(fileName), branchCommit.returnTrack(fileName));
        }
        String message = "Merged " + branchName + " into " + curBranchName + ".";
        makeCommit(message);
        head.setSecParentCommit(returnBranchCommitHash(branchName));
        if (hasConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    private static boolean execMerge(MergeAction mergeAction, String fileName,
                                  String currentBlobHash,
                                  String givenBlobHash) {
        boolean hasConflict = false;
        File cwdFile = join(CWD, fileName);
        switch (mergeAction) {
            case KEEP -> {}
            case TAKE -> {
                File givenBlob = join(BLOBS_DIR, givenBlobHash);
                byte[] givenContents = readContents(givenBlob);
                writeContents(cwdFile, givenContents);
                add(fileName);
            }
            case REMOVE -> {
                rm(fileName);
            }
            case CONFLICT -> {
                File currentBlob = join(BLOBS_DIR, currentBlobHash);
                File givenBlob = join(BLOBS_DIR, givenBlobHash);
                byte[] curContents = readContents(currentBlob);
                byte[] givenContents = readContents(givenBlob);
                writeContents(cwdFile, "<<<<<<< HEAD\n",
                        curContents, "=======", "\n",
                        givenContents, ">>>>>>>");
                add(fileName);
                hasConflict = true;
            }
        }
        return hasConflict;
    }

    /* Figure out merge case */
    private enum MergeAction {
        KEEP,
        TAKE,
        REMOVE,
        CONFLICT
    }

    private static MergeAction findMergeAction(String splitBlobHash,
                                               String currentBlobHash,
                                               String givenBlobHash) {
        MergeAction out = null;
        if (Objects.equals(currentBlobHash, givenBlobHash)) {
            out = MergeAction.KEEP;
        } else if (splitBlobHash != null) {
            if (Objects.equals(splitBlobHash, givenBlobHash)) {
                out = MergeAction.KEEP;
            } else if (Objects.equals(splitBlobHash, currentBlobHash)) {
                if (givenBlobHash == null) {
                    out = MergeAction.REMOVE;
                } else {
                    out = MergeAction.TAKE;
                }
            } else {
                out = MergeAction.CONFLICT;
            }
        } else if (splitBlobHash == null) {
            if (givenBlobHash == null) {
                out = MergeAction.KEEP;
            } else if (currentBlobHash == null) {
                out = MergeAction.TAKE;
            } else {
                out = MergeAction.CONFLICT;
            }
        }
        return out;
    }

    /** Find the split point's commit hash
     *  between HEAD and given branch's name.
     */
    private static String findSplitCommit(String givenBranch) {
        Commit head = returnHeadCommit();
        File branchName = join(BRANCHES_DIR, givenBranch);
        String branchCommitHash = readContentsAsString(branchName);
        File branchCommit = join(COMMITS_DIR, branchCommitHash);
        Commit branch = readObject(branchCommit, Commit.class);
        /* Set contains all commits' id of current branch */
        HashSet<String> curCommitLine = new HashSet<>();
        curCommitLine.add(returnBranchCommitHash(returnHeadName()));
        Commit p = head;
        while (p.returnParent() != null) {
            curCommitLine.add(p.returnParent());
            p = returnParentCommit(p);
        }
        /* Traverse the given branch */
        String out = null;
        if (curCommitLine.contains(branchCommitHash)) {
            out = branchCommitHash;
            return out;
        }
        Commit m = branch;
        while (m.returnParent() != null) {
            if (curCommitLine.contains(m.returnParent())) {
                out = m.returnParent();
                break;
            }
        }
        return out;
    }

    private static Commit returnParentCommit(Commit c) {
        String parentCommitHash = c.returnParent();
        File parentCommit = join(COMMITS_DIR, parentCommitHash);
        Commit parent = readObject(parentCommit, Commit.class);
        return parent;
    }
}

