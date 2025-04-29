public class SJFScheduler {

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

    public Metrics run(double[] burst, double[] arrival) {
        int n = burst.length;

        double[] wait = new double[n];
        double[] tat = new double[n];
        boolean[] done = new boolean[n];

        double time = 0, idle = 0, work = 0;
        int finished = 0;

        while (finished < n) {
            int idx = -1;
            double min = Double.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!done[i] && arrival[i] <= time && burst[i] < min) {
                    min = burst[i];
                    idx = i;
                }
            }

            if (idx == -1) {
                time++;
                idle++;
                continue;
            }

            wait[idx] = time - arrival[idx];
            time += burst[idx];
            work += burst[idx];
            tat[idx] = time - arrival[idx];
            done[idx] = true;
            finished++;
        }

        double sumWait = 0, sumTurn = 0;
        for (int i = 0; i < n; i++) {
            sumWait += wait[i];
            sumTurn += tat[i];
        }

        double avgWait = Math.round((sumWait / n) * 100.0) / 100.0;
        double avgTurn = Math.round((sumTurn / n) * 100.0) / 100.0;
        double cpu = Math.round((work / (idle + work)) * 10000.0) / 100.0;
        double tp = Math.round((n / (idle + work)) * 100.0) / 100.0;

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