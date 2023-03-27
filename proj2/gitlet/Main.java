package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Yichun Gao
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        argsCheck(args);
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.initRepo();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.addOneFile(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                Repository.commit(args[1]);
                break;
            case "rm":
                Repository.removeOneFile(args[1]);
                break;
            case "log":
                Repository.printLog();
                break;
            case "global-log":
                Repository.printGlobalLog();
                break;
            case "find":
                Repository.findCommit(args[1]);
                break;
            case "status":
                Repository.printStatus();
            case "checkout":
                Repository.checkout(args);
        }
    }

    private static void argsCheck(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init", "log", "global-log", "status":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "add", "rm", "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "commit":
                if (args.length != 2) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                break;
            case "checkout":
                if (args.length < 2 || args.length > 4) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;

            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
