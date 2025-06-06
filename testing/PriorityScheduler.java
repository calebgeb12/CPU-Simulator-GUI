public class PriorityScheduler {

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

        public double getAverageWait() { return averageWait; }
        public double getAverageTurnaround() { return averageTurnaround; }
        public double getCpuUtilization() { return cpuUtilization; }
        public double getThroughput() { return throughput; }

        @Override
        public String toString() {
            return "AvgWait=" + averageWait + ", AvgTurn=" + averageTurnaround + ", CPU=" + cpuUtilization + "% , TP=" + throughput;
        }
    }

    public Metrics run(double[] burst, double[] arrival, int[] priority) {
        int n = burst.length;

        double[] remaining = burst.clone();
        double[] wait = new double[n];
        int[] start = new int[n];
        int[] completion = new int[n];

        double idle = 0, used = 0;
        java.util.Arrays.fill(start, -1);

        int done = 0, time = 0;
        while (done < n) {
            int idx = -1;
            int bestPr = Integer.MAX_VALUE;
            double earliest = Double.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (remaining[i] > 0 && arrival[i] <= time) {
                    if (priority[i] < bestPr || (priority[i] == bestPr && arrival[i] < earliest)) {
                        bestPr = priority[i];
                        earliest = arrival[i];
                        idx = i;
                    }
                }
            }

            if (idx == -1) {
                idle++;
                time++;
                continue;
            }

            if (start[idx] == -1) start[idx] = time;

            while (remaining[idx] > 0) {
                for (int j = 0; j < n; j++) {
                    if (j != idx && remaining[j] > 0 && arrival[j] <= time) {
                        wait[j]++;
                    }
                }
                remaining[idx]--;
                used++;
                time++;
            }
            completion[idx] = time;
            done++;
        }

        double sumWait = 0, sumTurn = 0;
        for (int i = 0; i < n; i++) {
            sumWait += wait[i];
            sumTurn += completion[i] - arrival[i];
        }

        double avgWait = Math.round((sumWait / n) * 100.0) / 100.0;
        double avgTurn = Math.round((sumTurn / n) * 100.0) / 100.0;
        double cpu = Math.round((used / (idle + used)) * 10000.0) / 100.0;
        double tp = Math.round((n / (idle + used)) * 100.0) / 100.0;

        totalAvgWait += avgWait;
        totalAvgTurnaround += avgTurn;
        totalCpuUtil += cpu;
        totalThroughput += tp;
        runs++;

        return new Metrics(avgWait, avgTurn, cpu, tp);
    }

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
