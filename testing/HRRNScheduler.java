public class HRRNScheduler {
    private double totalAvgWait = 0;
    private double totalAvgTurnaround = 0;
    private double totalCpuUtil = 0;
    private double totalThroughput = 0;
    private int runs = 0;

    /** Holds the per‑run metrics */
    public static class Metrics {
        public final double averageWait;
        public final double averageTurnaround;
        public final double cpuUtilization;
        public final double throughput;

        private Metrics(double aw, double at, double cpu, double tp) {
            this.averageWait = aw;
            this.averageTurnaround = at;
            this.cpuUtilization = cpu;
            this.throughput = tp;
        }
        @Override public String toString() {
            return "AvgWait=" + averageWait + ", AvgTurn=" + averageTurnaround +
                   ", CPU=" + cpuUtilization + "% , TP=" + throughput;
        }
    }

    /** Execute HRRN on a single workload and return the metrics */
    public Metrics run(double[] burstTime, double[] arrivalTime) {
        int n = burstTime.length;
        double[] remaining = burstTime.clone();
        double[] wait = new double[n];
        int[] start = new int[n];
        int[] completion = new int[n];
        double idle = 0, used = 0;
        for (int i = 0; i < n; i++) start[i] = -1;
        int done = 0, time = 0;
        while (done < n) {
            int idx = -1;
            double bestRatio = 0;
            for (int i = 0; i < n; i++) {
                if (remaining[i] > 0 && arrivalTime[i] <= time) {
                    double ratio = 1 + (wait[i] / burstTime[i]);
                    if (ratio > bestRatio) { bestRatio = ratio; idx = i; }
                }
            }
            if (idx == -1) { idle++; time++; continue; }
            if (start[idx] == -1) start[idx] = time;
            
            // execute one tick of the chosen job
            for (int j = 0; j < n; j++)
                if (j != idx && remaining[j] > 0 && arrivalTime[j] <= time) wait[j]++;
            remaining[idx]--; used++; time++;
            if (remaining[idx] == 0) { completion[idx] = time; done++; }
        }
        double sumWait = 0, sumTurn = 0;
        for (int i = 0; i < n; i++) {
            sumWait += wait[i];
            sumTurn += completion[i] - arrivalTime[i];
        }
        double avgWait = Math.round((sumWait / n) * 100.0) / 100.0;
        double avgTurn = Math.round((sumTurn / n) * 100.0) / 100.0;
        double cpuUtil = Math.round((used / (idle + used)) * 10000.0) / 100.0;
        double tp = Math.round((n / (idle + used)) * 100.0) / 100.0;
        // accumulate
        totalAvgWait += avgWait;
        totalAvgTurnaround += avgTurn;
        totalCpuUtil += cpuUtil;
        totalThroughput += tp;
        runs++;
        return new Metrics(avgWait, avgTurn, cpuUtil, tp);
    }

    /** Return the grand averages after all runs */
    public Metrics getOverallAverages() {
        if (runs == 0) return new Metrics(0, 0, 0, 0);
        double aw = Math.round((totalAvgWait / runs) * 100.0) / 100.0;
        double at = Math.round((totalAvgTurnaround / runs) * 100.0) / 100.0;
        double cpu = Math.round((totalCpuUtil / runs) * 100.0) / 100.0;
        double tp = Math.round((totalThroughput / runs) * 100.0) / 100.0;
        return new Metrics(aw, at, cpu, tp);
    }

    public double[] getData() {
        if (runs == 0) return new double[]{0, 0, 0, 0};
        double aw = Math.round((totalAvgWait / runs) * 100.0) / 100.0;
        double at = Math.round((totalAvgTurnaround / runs) * 100.0) / 100.0;
        double cpu = Math.round((totalCpuUtil / runs) * 100.0) / 100.0;
        double tp = Math.round((totalThroughput / runs) * 100.0) / 100.0;
        return new double[]{aw, at, cpu, tp};
    }

    
}
