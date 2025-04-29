// Non‑pre‑emptive? -> actually SRTF is pre‑emptive (Shortest Remaining Time First)
// Scheduler that mirrors HRRNScheduler structure: keeps running averages across workloads
public class SRTFScheduler {

    private double totalAvgWait = 0;
    private double totalAvgTurnaround = 0;
    private double totalCpuUtil = 0;
    private double totalThroughput = 0;
    private int runs = 0;

    public static class Metrics {
        private final double averageWait;
        private final double averageTurnaround;
        private final double cpuUtilization;
        private final double throughput;
        Metrics(double aw, double at, double cpu, double tp) {
            this.averageWait = aw;
            this.averageTurnaround = at;
            this.cpuUtilization = cpu;
            this.throughput = tp;
        }
        public double getAverageWait()       { return averageWait; }
        public double getAverageTurnaround() { return averageTurnaround; }
        public double getCpuUtilization()    { return cpuUtilization; }
        public double getThroughput()        { return throughput; }
        @Override public String toString() {
            return "AvgWait=" + averageWait + ", AvgTurn=" + averageTurnaround +
                   ", CPU=" + cpuUtilization + "% , TP=" + throughput;
        }
    }

    /** Execute SRTF on one workload and return metrics */
    public Metrics run(double[] burstTime, double[] arrivalTime) {
        int n = burstTime.length;
        double[] remaining = burstTime.clone();
        double[] wait = new double[n];
        int[] start = new int[n];
        int[] completion = new int[n];
        double idle = 0, used = 0;
        java.util.Arrays.fill(start, -1);
        int done = 0, time = 0;

        while (done < n) {
            int idx = -1;
            double minRemaining = Double.MAX_VALUE;
            // pick job with shortest remaining time that has arrived
            for (int i = 0; i < n; i++) {
                if (remaining[i] > 0 && arrivalTime[i] <= time && remaining[i] < minRemaining) {
                    idx = i;
                    minRemaining = remaining[i];
                }
            }
            // increment wait for others that are ready
            for (int i = 0; i < n; i++) {
                if (i != idx && remaining[i] > 0 && arrivalTime[i] <= time) {
                    wait[i]++;
                }
            }
            // CPU idle if no job ready
            if (idx == -1) {
                idle++; time++; continue;
            }
            if (start[idx] == -1) start[idx] = time;

            // run 1 tick of the chosen job (pre‑emptive)
            remaining[idx]--; used++; time++;
            if (remaining[idx] == 0) {
                completion[idx] = time;
                done++;
            }
        }

        double sumW = 0, sumT = 0;
        for (int i = 0; i < n; i++) {
            sumW += wait[i];
            sumT += completion[i] - arrivalTime[i];
        }
        double aw = Math.round((sumW / n) * 100.0) / 100.0;
        double at = Math.round((sumT / n) * 100.0) / 100.0;
        double cpu = Math.round((used / (idle + used)) * 10000.0) / 100.0;
        double tp = Math.round((n / (idle + used)) * 100.0) / 100.0;

        totalAvgWait += aw;
        totalAvgTurnaround += at;
        totalCpuUtil += cpu;
        totalThroughput += tp;
        runs++;
        return new Metrics(aw, at, cpu, tp);
    }

    /** Overall averages wrapped in Metrics */
    public Metrics getOverallAverages() {
        if (runs == 0) return new Metrics(0,0,0,0);
        double aw = Math.round((totalAvgWait / runs) * 100.0) / 100.0;
        double at = Math.round((totalAvgTurnaround / runs) * 100.0) / 100.0;
        double cpu = Math.round((totalCpuUtil / runs) * 100.0) / 100.0;
        double tp = Math.round((totalThroughput / runs) * 100.0) / 100.0;
        return new Metrics(aw, at, cpu, tp);
    }

    /** Overall averages as raw double[] {avgWait, avgTurnaround, cpuUtil, throughput} */
    public double[] getData() {
        if (runs == 0) return new double[]{0,0,0,0};
        double aw = Math.round((totalAvgWait / runs) * 100.0) / 100.0;
        double at = Math.round((totalAvgTurnaround / runs) * 100.0) / 100.0;
        double cpu = Math.round((totalCpuUtil / runs) * 100.0) / 100.0;
        double tp = Math.round((totalThroughput / runs) * 100.0) / 100.0;
        return new double[]{aw, at, cpu, tp};
    }
}
