import java.util.ArrayList;
import java.util.List;

public class Testing {
    public static void main(String[] args) {

    //data sets used for testing
        double[][] BURST_SETS = {
            {8, 1, 1, 10, 2, 15, 1, 1, 20, 1, 30, 1, 5, 1, 1, 25, 1, 1, 40, 1},
            {5, 3, 8, 6},
            {7, 4, 1, 4, 5},
            {2, 2, 2, 2, 2, 2},
            {10, 5, 7, 3},
            {12, 2, 8, 4, 6},
            {20, 1, 1, 1, 1},
            {3, 6, 4, 2, 5, 7},
            {9, 9, 9},
            {1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
        };

        double[][] ARRIVAL_SETS = {
            {0, 0, 0, 5, 5, 5, 15, 15, 15, 25, 25, 25, 40, 40, 40, 50, 50, 50, 60, 60},
            {0, 1, 2, 3},
            {0, 0, 0, 0, 0},
            {0, 1, 2, 3, 4, 5},
            {0, 2, 4, 6},
            {0, 1, 2, 3, 4},
            {0, 10, 20, 30, 40},
            {0, 0, 1, 1, 2, 2},
            {0, 0, 0},
            {0, 1, 1, 2, 2, 3, 3, 4, 4, 5}
        };

        int[][] PRIORITY_SETS = {
            {1, 3, 1, 4, 3, 1, 1, 4, 5, 5, 3, 3, 1, 2, 2, 3, 5, 3, 2, 2},
            {1, 4, 1, 5},
            {4, 1, 1, 3, 4},
            {1, 1, 1, 2, 2, 1},
            {3, 4, 5, 4},
            {3, 4, 1, 3, 3},
            {5, 4, 2, 5, 5},
            {5, 3, 1, 4, 5, 4},
            {5, 5, 4},
            {5, 4, 3, 5, 1, 2, 1, 4, 3, 2}
        };
        

        int timeQuantum = 5;

        //for running HRRN alg
        HRRNScheduler HRRNScheduler = new HRRNScheduler();
        List<HRRNScheduler.Metrics> runMetrics = new ArrayList<>();

        for (int i = 0; i < BURST_SETS.length; i++) {
            double[] bursts = BURST_SETS[i];
            double[] arrivals = ARRIVAL_SETS[i];
            HRRNScheduler.Metrics m = HRRNScheduler.run(bursts, arrivals);
            runMetrics.add(m);
        }

        //in the following order: average wait time, average turnaround, cpu utilization, thoroughput
        double[] HRRNData = HRRNScheduler.getData();

    //for running SRTF alg
        SRTFScheduler srtfScheduler = new SRTFScheduler();
        List<SRTFScheduler.Metrics> srtfRunMetrics = new ArrayList<>();

        for (int i = 0; i < BURST_SETS.length; i++) {
            srtfRunMetrics.add(srtfScheduler.run(BURST_SETS[i], ARRIVAL_SETS[i]));
        }

        //in the following order: average wait time, average turnaround, cpu utilization, thoroughput
        double[] SRTFData = srtfScheduler.getData();
        
    //for fcfs
        FCFSScheduler fcfsScheduler = new FCFSScheduler();
        List<FCFSScheduler.Metrics> fcfsRunMetrics = new ArrayList<>();

        for(int i = 0;i < BURST_SETS.length; i++) { 
            fcfsRunMetrics.add(fcfsScheduler.run(BURST_SETS[i],ARRIVAL_SETS[i]));
        }

        double[] FCFSData = fcfsScheduler.getData();

    //for sjf
        SJFScheduler sjfScheduler = new SJFScheduler();
        List<SJFScheduler.Metrics> sjfRunMetrics = new ArrayList<>();
        for(int i = 0;i < BURST_SETS.length; i++) {
            sjfRunMetrics.add(sjfScheduler.run(BURST_SETS[i],ARRIVAL_SETS[i]));
        }

        double[] SJFData = sjfScheduler.getData();
    
    
    //for priority
        PriorityScheduler prScheduler=new PriorityScheduler();
        List<PriorityScheduler.Metrics> prRunMetrics=new ArrayList<>();
        for(int i=0;i<BURST_SETS.length;i++) {
            prRunMetrics.add(prScheduler.run(BURST_SETS[i],ARRIVAL_SETS[i],PRIORITY_SETS[i]));
        }

        double[] PRData=prScheduler.getData();


    //for round robin
        RoundRobinScheduler rrScheduler = new RoundRobinScheduler();
        List<RoundRobinScheduler.Metrics> rrRunMetrics = new ArrayList<>();

        for (int i = 0; i < BURST_SETS.length; i++) {
            rrRunMetrics.add(rrScheduler.run(BURST_SETS[i], ARRIVAL_SETS[i], timeQuantum));
        }

        double[] RRData = rrScheduler.getData();

        
    //OUTPUTTING DATA HERE
        double[][] completeData = new double[][]{HRRNData, SJFData, FCFSData,PRData, SRTFData, RRData};

        String[] names = { "HRRN", "SJF", "FCFS", "Priority", "SRTF", "RR" };

        /* header */
        System.out.printf(
            "%-10s %15s %20s %15s %20s%n",
            "Algorithm", "Avg Wait (s)", "Avg Turnaround (s)", "CPU Util (%)", "Throughput (proc/unit)"
        );
        
        /* rows */
        for (int i = 0; i < completeData.length; i++) {
            double[] r = completeData[i];           // {wait, turnaround, cpu%, throughput}
            System.out.printf(
                "%-10s %15.2f %20.2f %15.2f%% %20.2f%n",
                names[i], r[0], r[1], r[2], r[3]
            );
        }
}

//used for running individual
    public static void hrrnAlgorithm(double[] burstTime, double[] arrivalTime) {

        
        int np = burstTime.length;

        double[] remainingTime = new double[np];
        double[] waitTime = new double[np];
        int[] startTime = new int[np];
        int[] completionTime = new int[np];
        double idleTime = 0;
        double usedTime = 0;

        for (int i = 0; i < np; i++) {
            remainingTime[i] = burstTime[i];
            startTime[i] = -1;
        }

        int currTime = 0;
        int completed = 0;

        while (completed != np) {
            int maxIdx = -1;
            double maxRatio = 0;

            for (int i = 0; i < np; i++) {
                double hitRatio = 1 + (waitTime[i] / burstTime[i]);
                if (arrivalTime[i] <= currTime && hitRatio > maxRatio && remainingTime[i] > 0) {
                    if (startTime[i] == -1) {
                        startTime[i] = currTime;
                    }
                    maxRatio = hitRatio;
                    maxIdx = i;
                }
            }

            for (int i = 0; i < np; i++) {
                if (arrivalTime[i] <= currTime && remainingTime[i] > 0 && i != maxIdx) {
                    waitTime[i]++;
                }
            }

            if (maxIdx == -1) {
                idleTime++;
                currTime++;
                continue;
            } else {
                usedTime++;
            }

            remainingTime[maxIdx]--;
            currTime++;

            if (remainingTime[maxIdx] == 0) {
                completed++;
                completionTime[maxIdx] = currTime;
            }
        }

        double totalWaitTime = 0;
        double totalTurnaroundTime = 0;

        for (int i = 0; i < np; i++) {
            totalWaitTime += waitTime[i];
            totalTurnaroundTime += completionTime[i] - arrivalTime[i];
        }

        double averageWaitTime = Math.round((totalWaitTime / np) * 100.0) / 100.0;
        double averageTurnaroundTime = Math.round((totalTurnaroundTime / np) * 100.0) / 100.0;
        double cpuUtilizationRate = Math.round((usedTime / (idleTime + usedTime)) * 10000.0) / 100.0;
        double throughput = Math.round((np / (idleTime + usedTime)) * 100.0) / 100.0;

        System.out.println("\nResults:");
        System.out.println("Average wait time: " + averageWaitTime);
        System.out.println("Average turnaround time: " + averageTurnaroundTime + " seconds");
        System.out.println("CPU utilization rate: " + cpuUtilizationRate + "%");
        System.out.println("Throughput: " + throughput + " processes/second");
    }

    public static void srtfAlgorithm(double[] burstTime, double[] arrivalTime) {
        int np = burstTime.length;

        double[] remainingTime = new double[np];
        double[] waitTime = new double[np];
        int[] startTime = new int[np];
        int[] completionTime = new int[np];
        double idleTime = 0;
        double usedTime = 0;

        for (int i = 0; i < np; i++) {
            remainingTime[i] = burstTime[i];
            startTime[i] = -1;
        }

        int currTime = 0;
        int completed = 0;

        while (completed != np) {
            int minIdx = -1;
            double minTime = Double.MAX_VALUE;

            //find shortest remaining time process
            for (int i = 0; i < np; i++) {
                if (arrivalTime[i] <= currTime && remainingTime[i] > 0 && remainingTime[i] < minTime) {
                    if (startTime[i] == -1) {
                        startTime[i] = currTime;
                    }
                    minTime = remainingTime[i];
                    minIdx = i;
                }
            }

            // update wait time
            for (int i = 0; i < np; i++) {
                if (arrivalTime[i] <= currTime && remainingTime[i] > 0 && i != minIdx) {
                    waitTime[i]++;
                }
            }

            if (minIdx == -1) {
                idleTime++;
                currTime++;
                continue;
            } else {
                usedTime++;
            }

            remainingTime[minIdx]--;
            currTime++;

            if (remainingTime[minIdx] == 0) {
                completed++;
                completionTime[minIdx] = currTime;
            }
        }

        double totalWaitTime = 0;
        double totalTurnaroundTime = 0;

        for (int i = 0; i < np; i++) {
            totalWaitTime += waitTime[i];
            totalTurnaroundTime += completionTime[i] - arrivalTime[i];
        }

        double averageWaitTime = Math.round((totalWaitTime / np) * 100.0) / 100.0;
        double averageTurnaroundTime = Math.round((totalTurnaroundTime / np) * 100.0) / 100.0;
        double cpuUtilizationRate = Math.round((usedTime / (idleTime + usedTime)) * 10000.0) / 100.0;
        double throughput = Math.round((np / (idleTime + usedTime)) * 100.0) / 100.0;

        System.out.println("Average wait time: " + averageWaitTime);
        System.out.println("Average turnaround time: " + averageTurnaroundTime + " seconds");
        System.out.println("CPU utilization rate: " + cpuUtilizationRate + "%");
        System.out.println("Throughput: " + throughput + " processes/second");
    }

    public static void fcfsAlgorithm(double[] burstTime, double[] arrivalTime) {
        int n = burstTime.length;
    
        double[] remainingTime = new double[n];
        double[] waitTime = new double[n];
        int[] startTime = new int[n];
        int[] completionTime = new int[n];
        double idleTime = 0, usedTime = 0;
    
        for (int i = 0; i < n; i++) {
            remainingTime[i] = burstTime[i];
            startTime[i] = -1;
        }
    
        int finished = 0, currTime = 0;
    
        while (finished < n) {
            int idx = -1;
            double earliest = Double.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (remainingTime[i] > 0 && arrivalTime[i] <= currTime && arrivalTime[i] < earliest) {
                    earliest = arrivalTime[i];
                    idx = i;
                }
            }
    
            if (idx == -1) {
                idleTime++;
                currTime++;
                continue;
            }
    
            if (startTime[idx] == -1) startTime[idx] = currTime;
    
            while (remainingTime[idx] > 0) {
                for (int i = 0; i < n; i++) {
                    if (i != idx && remainingTime[i] > 0 && arrivalTime[i] <= currTime) {
                        waitTime[i]++;
                    }
                }
                remainingTime[idx]--;
                usedTime++;
                currTime++;
            }
    
            completionTime[idx] = currTime;
            finished++;
        }
    
        double totalWT = 0, totalTT = 0;
        for (int i = 0; i < n; i++) {
            totalWT += waitTime[i];
            totalTT += completionTime[i] - arrivalTime[i];
        }
    
        double avgWT = Math.round((totalWT / n) * 100.0) / 100.0;
        double avgTT = Math.round((totalTT / n) * 100.0) / 100.0;
        double cpuUtil = Math.round((usedTime / (idleTime + usedTime)) * 10000.0) / 100.0;
        double throughput = Math.round((n / (idleTime + usedTime)) * 100.0) / 100.0;
    
        System.out.println("Average wait time: " + avgWT);
        System.out.println("Average turnaround time: " + avgTT + " seconds");
        System.out.println("CPU utilization rate: " + cpuUtil + "%");
        System.out.println("Throughput: " + throughput + " processes/second");
    }
    

    public static void sjfAlgorithm(double[] burst, double[] arrival) {
        int n = burst.length;
        double[] wt = new double[n], tat = new double[n], rt = new double[n];
        boolean[] done = new boolean[n];

        double time = 0, idle = 0, work = 0;
        int finished = 0;

        while (finished < n) {
            int idx = -1;
            double min = Double.MAX_VALUE;

            for (int i = 0; i < n; i++)
                if (!done[i] && arrival[i] <= time && burst[i] < min) {
                    min = burst[i];
                    idx = i;
                }

            if (idx == -1) { time++; idle++; continue; }

            wt[idx] = time - arrival[idx];
            rt[idx] = wt[idx];
            time   += burst[idx];
            work   += burst[idx];
            tat[idx] = time - arrival[idx];
            done[idx] = true;
            finished++;
        }

        double swt = 0, stat = 0, srt = 0;
        for (int i = 0; i < n; i++) { swt += wt[i]; stat += tat[i]; srt += rt[i]; }

        double averageWaitTime       = Math.round((swt  / n)               * 100.0)  / 100.0;
        double averageTurnaroundTime = Math.round((stat / n)               * 100.0)  / 100.0;
        double averageResponseTime   = Math.round((srt  / n)               * 100.0)  / 100.0;
        double cpuUtilizationRate    = Math.round((work / (idle + work))    * 10000.0) / 100.0; // %
        double throughput            = Math.round((n    / (idle + work))    * 100.0)  / 100.0; // jobs/unit

        System.out.println("Avg Wait Time    : " + averageWaitTime);
        System.out.println("Avg Turnaround   : " + averageTurnaroundTime);
        System.out.println("CPU Utilization  : " + cpuUtilizationRate + "%");
        System.out.println("Throughput       : " + throughput);
    }

    public static void priorityAlgorithm(double[] burstTime, double[] arrivalTime, int[] priority) {
        int n = burstTime.length;

        double[] remainingTime = burstTime.clone();
        double[] waitTime = new double[n];
        int[] startTime = new int[n];
        int[] completionTime = new int[n];
    
        double idleTime = 0, usedTime = 0;
        int finished = 0, currTime = 0;
    
        java.util.Arrays.fill(startTime, -1);
    
        while (finished < n) {
            int idx = -1;
            int bestPr = Integer.MAX_VALUE;
            double earliestArrival = Double.MAX_VALUE;
    
            for (int i = 0; i < n; i++) {
                if (remainingTime[i] > 0 && arrivalTime[i] <= currTime) {
                    if (priority[i] < bestPr ||
                        (priority[i] == bestPr && arrivalTime[i] < earliestArrival)) {
                        bestPr = priority[i];
                        earliestArrival = arrivalTime[i];
                        idx = i;
                    }
                }
            }
    
            if (idx == -1) {
                idleTime++;
                currTime++;
                continue;
            }
    
            if (startTime[idx] == -1) startTime[idx] = currTime;
    
            while (remainingTime[idx] > 0) {
                for (int j = 0; j < n; j++) {
                    if (j != idx && remainingTime[j] > 0 && arrivalTime[j] <= currTime) {
                        waitTime[j]++;
                    }
                }
                remainingTime[idx]--;
                usedTime++;
                currTime++;
            }
    
            completionTime[idx] = currTime;
            finished++;
        }
    
        double totalWT = 0, totalTT = 0;
        for (int i = 0; i < n; i++) {
            totalWT += waitTime[i];
            totalTT += completionTime[i] - arrivalTime[i];
        }
    
        double averageWaitTime = Math.round((totalWT / n) * 100.0) / 100.0;
        double averageTurnaroundTime = Math.round((totalTT / n) * 100.0) / 100.0;
        double cpuUtilizationRate = Math.round((usedTime / (idleTime + usedTime)) * 10000.0) / 100.0;
        double throughput = Math.round((n / (idleTime + usedTime)) * 100.0) / 100.0;
    
        System.out.println("Average wait time: " + averageWaitTime);
        System.out.println("Average turnaround time: " + averageTurnaroundTime + " seconds");
        System.out.println("CPU utilization rate: " + cpuUtilizationRate + "%");
        System.out.println("Throughput: " + throughput + " processes/second");
    }

    //non-preemptive
    public static void roundRobinAlgorithm(double[] burstTime, double[] arrivalTime, int timeQuantum) {
        int n = burstTime.length;

        double[] remainingTime = burstTime.clone();
        double[] waitTime = new double[n];
        int[] startTime = new int[n];
        int[] completionTime = new int[n];
        boolean[] inQueue = new boolean[n];

        java.util.Queue<Integer> q = new java.util.ArrayDeque<>();

        double idleTime = 0, usedTime = 0;
        int completed = 0, currTime = 0;

        java.util.Arrays.fill(startTime, -1);

        while (completed < n) {
            // enqueue jobs arriving at this tick
            for (int i = 0; i < n; i++) {
                if (arrivalTime[i] == currTime && remainingTime[i] > 0 && !inQueue[i]) {
                    q.add(i);
                    inQueue[i] = true;
                }
            }

            if (q.isEmpty()) {
                idleTime++;
                currTime++;
                continue;
            }

            int idx = q.poll();
            inQueue[idx] = false;
            if (startTime[idx] == -1) startTime[idx] = currTime;

            int slice = Math.min(timeQuantum, (int) remainingTime[idx]);
            for (int s = 0; s < slice; s++) {
                // every other ready process waits one tick
                for (int j = 0; j < n; j++) {
                    if (j != idx && remainingTime[j] > 0 && arrivalTime[j] <= currTime) {
                        waitTime[j]++;
                    }
                }

                remainingTime[idx]--;
                usedTime++;
                currTime++;

                // enqueue jobs that arrive during this slice
                for (int j = 0; j < n; j++) {
                    if (arrivalTime[j] == currTime && remainingTime[j] > 0 && !inQueue[j]) {
                        q.add(j);
                        inQueue[j] = true;
                    }
                }
            }

            if (remainingTime[idx] > 0) {
                q.add(idx);
                inQueue[idx] = true;
            } else {
                completionTime[idx] = currTime;
                completed++;
            }
        }

        // aggregate metrics
        double totalWaitTime = 0;
        double totalTurnaroundTime = 0;

        for (int i = 0; i < n; i++) {
            totalWaitTime += waitTime[i];
            totalTurnaroundTime += completionTime[i] - arrivalTime[i];
        }

        double averageWaitTime = Math.round((totalWaitTime / n) * 100.0) / 100.0;
        double averageTurnaroundTime = Math.round((totalTurnaroundTime / n) * 100.0) / 100.0;
        double cpuUtilizationRate = Math.round((usedTime / (idleTime + usedTime)) * 10000.0) / 100.0;
        double throughput = Math.round((n / (idleTime + usedTime)) * 100.0) / 100.0;

        System.out.println("Average wait time: " + averageWaitTime);
        System.out.println("Average turnaround time: " + averageTurnaroundTime + " seconds");
        System.out.println("CPU utilization rate: " + cpuUtilizationRate + "%");
        System.out.println("Throughput: " + throughput + " processes/second");
    }      
}