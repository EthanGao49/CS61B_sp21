package timingtest;
import edu.princeton.cs.algs4.Stopwatch;
//import org.checkerframework.checker.units.qual.A;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        Ns.addLast(1000);
        Ns.addLast(2000);
        Ns.addLast(4000);
        Ns.addLast(8000);
        Ns.addLast(16000);
        Ns.addLast(32000);
        Ns.addLast(64000);
        Ns.addLast(128000);
        Ns.addLast(256000);
        Ns.addLast(512000);
        Ns.addLast(1024000);

        for(int i = 0; i < Ns.size(); i++){
            AList<Integer> tempAList = new AList<>();
            int counts = 0;
            Stopwatch sw = new Stopwatch();
            for(int j = 0; j < Ns.get(i); j++){
                tempAList.addLast(1);
                counts += 1;
            }
            double timeInSeconds = sw.elapsedTime();
            opCounts.addLast(counts);
            times.addLast(timeInSeconds);
        }

        printTimingTable(Ns, times, opCounts);
    }
}
