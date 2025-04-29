using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows.Forms.VisualStyles;

namespace CpuSchedulingWinForms
{
    public static class Algorithms
    {

        public static void hrrnAlgorithm(string userInput)
        {
            int np = Convert.ToInt16(userInput);

            double[] burstTime = new double[np];
            double[] arrivalTime = new double[np];
            double[] remainingTime = new double[np];
            double[] waitTime = new double[np];
            int[] startTime = new int[np]; //Track turnaround times
            int[] completionTime = new int[np]; //Track completion times
            double idleTime = 0;
            double usedTime = 0;
            double totalBurstTime = 0;
            double twt = 0.0, awt;
            int num;

            DialogResult result = MessageBox.Show("Highest Response Ratio Next ", "", MessageBoxButtons.YesNo, MessageBoxIcon.Information);

            if (result == DialogResult.Yes)
            {
                //input burst times
                for (num = 0; num <= np - 1; num++)
                {
                    //MessageBox.Show("Enter Burst time for P" + (num + 1) + ":", "Burst time for Process", MessageBoxButtons.OK, MessageBoxIcon.Question);
                    //Console.WriteLine("\nEnter Burst time for P" + (num + 1) + ":");

                    string input =
                    Microsoft.VisualBasic.Interaction.InputBox("Enter Burst time: ",
                                                       "Burst time for P" + (num + 1),
                                                       "",
                                                       -1, -1);

                    burstTime[num] = Convert.ToInt64(input);
                    remainingTime[num] = burstTime[num];
                    totalBurstTime += Convert.ToInt64(input);

                    //var input = Console.ReadLine();
                    //bp[num] = Convert.ToInt32(input);
                }

                //input arrival times
                for (num = 0; num <= np - 1; num++)
                {
                    //MessageBox.Show("Enter Burst time for P" + (num + 1) + ":", "Burst time for Process", MessageBoxButtons.OK, MessageBoxIcon.Question);
                    //Console.WriteLine("\nEnter Burst time for P" + (num + 1) + ":");

                    string input =
                    Microsoft.VisualBasic.Interaction.InputBox("Enter Arrival time: ",
                                                       "Burst time for P" + (num + 1),
                                                       "",
                                                       -1, -1);

                    arrivalTime[num] = Convert.ToInt64(input);

                    //var input = Console.ReadLine();
                    //bp[num] = Convert.ToInt32(input);
                }

                for (int i = 0; i < np; i++)
                {
                    startTime[i] = -1;
                }

                //calculate
                int currTime = 0;
                int completed = 0;
                int currIdx = -1;

                while (completed != np)
                {
                    int maxIdx = -1;
                    double maxRatio = 0;

                    //find highest hit ratio
                    for (int i = 0; i < np; i++)
                    {
                        double hitRatio = 1 + (waitTime[i] / burstTime[i]);
                        if (arrivalTime[i] <= currTime && hitRatio > maxRatio && remainingTime[i] > 0)
                        {
                            if (startTime[i] == -1)
                            {
                                startTime[i] = currTime;
                            }

                            maxRatio = hitRatio;
                            maxIdx = i;
                        }
                    }

                    //calculate waitTime
                    for (int i = 0; i < np; i++)
                    {
                        if (arrivalTime[i] <= currTime && remainingTime[i] > 0 && i != maxIdx)
                        {
                            waitTime[i]++;
                        }
                    }

                    if (maxIdx == -1)
                    {
                        idleTime++;
                        currTime++; //No available process yet, increment time
                        continue;
                    }

                    else
                    {
                        usedTime++;
                    }

                    // Process execution
                    remainingTime[maxIdx]--;
                    currTime++;

                    //If the process is completed
                    if (remainingTime[maxIdx] == 0)
                    {
                        completed++;
                        completionTime[maxIdx] = currTime; // Record completion time
                    }
                }

                double totalWaitTime = 0;
                double totalTurnaroundTime = 0;

                for (int i = 0; i < np; i++)
                {
                    totalWaitTime += waitTime[i];
                }

                for (int i = 0; i < np; i++)
                {
                    totalTurnaroundTime += completionTime[i] - arrivalTime[i];
                }

                double averageWaitTime = Math.Round(totalWaitTime / np, 2);
                double averageTurnaroundTime = Math.Round(totalTurnaroundTime / (double) np, 2);
                double cpuUtilizationRate = Math.Round(usedTime / (idleTime + usedTime), 2) * 100;
                double throughput = Math.Round(((np) / (idleTime + usedTime)), 2);

                MessageBox.Show("Average wait time: " + Math.Round(averageWaitTime, 2));
                MessageBox.Show("Average turnaround time: " + averageTurnaroundTime + " seconds");
                MessageBox.Show("CPU utilization rate: " + cpuUtilizationRate + "%");
                MessageBox.Show("Throughput: " + throughput + " processes / second");

            }

            else if (result == DialogResult.No)
            {
                //this.Hide();
                //Form1 frm = new Form1();
                //frm.ShowDialog();
            }
        }

        public static void srtfAlgorithm(string userInput) 
        {
            int np = Convert.ToInt16(userInput);

            double[] burstTime = new double[np];
            double[] arrivalTime = new double[np];
            double[] remainingTime = new double[np];
            double[] waitTime = new double[np];
            int[] startTime = new int[np]; //Track turnaround times
            int[] completionTime = new int[np]; //Track completion times
            double idleTime = 0;
            double usedTime = 0;
            double totalBurstTime = 0;
            double twt = 0.0, awt;
            int num;

            DialogResult result = MessageBox.Show("Shortest Remaining Time First ", "", MessageBoxButtons.YesNo, MessageBoxIcon.Information);

            if (result == DialogResult.Yes)
            {
                //input burst times
                for (num = 0; num <= np - 1; num++)
                {
                    //MessageBox.Show("Enter Burst time for P" + (num + 1) + ":", "Burst time for Process", MessageBoxButtons.OK, MessageBoxIcon.Question);
                    //Console.WriteLine("\nEnter Burst time for P" + (num + 1) + ":");

                    string input =
                    Microsoft.VisualBasic.Interaction.InputBox("Enter Burst time: ",
                                                       "Burst time for P" + (num + 1),
                                                       "",
                                                       -1, -1);

                    burstTime[num] = Convert.ToInt64(input);
                    remainingTime[num] = burstTime[num];
                    totalBurstTime += Convert.ToInt64(input);

                    //var input = Console.ReadLine();
                    //bp[num] = Convert.ToInt32(input);
                }

                //input arrival times
                for (num = 0; num <= np - 1; num++)
                {
                    //MessageBox.Show("Enter Burst time for P" + (num + 1) + ":", "Burst time for Process", MessageBoxButtons.OK, MessageBoxIcon.Question);
                    //Console.WriteLine("\nEnter Burst time for P" + (num + 1) + ":");

                    string input =
                    Microsoft.VisualBasic.Interaction.InputBox("Enter Arrival time: ",
                                                       "Burst time for P" + (num + 1),
                                                       "",
                                                       -1, -1);

                    arrivalTime[num] = Convert.ToInt64(input);

                    //var input = Console.ReadLine();
                    //bp[num] = Convert.ToInt32(input);
                }

                for (int i = 0; i < np; i++) {
                    startTime[i] = -1;
                }

                //calculate
                int currTime = 0;
                int completed = 0;
                int currIdx = -1;

                while (completed != np)
                {
                    int minIdx = -1;
                    int minTime = int.MaxValue;

                    //find shortest remaining time process
                    for (int i = 0; i < np; i++)
                    {
                        if (arrivalTime[i] <= currTime && remainingTime[i] > 0 && remainingTime[i] < minTime)
                        {
                            if (startTime[i] == -1) {
                                startTime[i] = currTime;
                            }

                            minTime = (int) remainingTime[i];
                            minIdx = i;
                        }
                    }

                    //calculate waitTime
                    for (int i = 0; i < np; i++) {
                        if (arrivalTime[i] <= currTime && remainingTime[i] > 0 && i != minIdx) {
                            waitTime[i]++;
                        }
                    }

                    if (minIdx == -1)
                    {
                        idleTime++;
                        currTime++; //No available process yet, increment time
                        continue;
                    }

                    else
                    {
                        usedTime++;
                    }

                    // Process execution
                    remainingTime[minIdx]--;
                    currTime++;

                    //If the process is completed
                    if (remainingTime[minIdx] == 0)
                    {
                        completed++;
                        completionTime[minIdx] = currTime; // Record completion time
                    }
                }

                double totalWaitTime = 0;
                double totalTurnaroundTime = 0;

                for (int i = 0; i < np; i++) {
                    totalWaitTime += waitTime[i];
                }

                for (int i = 0; i < np; i++) {
                    totalTurnaroundTime += completionTime[i] - arrivalTime[i];
                }

                double averageWaitTime = Math.Round(totalWaitTime / np, 2);
                double averageTurnaroundTime = Math.Round(totalTurnaroundTime / np, 2);
                double cpuUtilizationRate = Math.Round(usedTime / (idleTime + usedTime), 2) * 100;
                double throughput = Math.Round(((np) / (idleTime + usedTime)), 2);


                MessageBox.Show("Average wait time: " + Math.Round(averageWaitTime, 2));
                MessageBox.Show("Average turnaround time: " + averageTurnaroundTime + " seconds");
                MessageBox.Show("CPU utilization rate: " + cpuUtilizationRate + "%");
                MessageBox.Show("Throughput: " + throughput + " processes / second");

            }

            else if (result == DialogResult.No)
            {
                //this.Hide();
                //Form1 frm = new Form1();
                //frm.ShowDialog();
            }
        }

        public static void fcfsAlgorithm(string userInput)
        {
            int np = Convert.ToInt16(userInput);
            int npX2 = np * 2;

            double[] bp = new double[np];
            double[] wtp = new double[np];
            string[] output1 = new string[npX2];
            double twt = 0.0, awt; 
            int num;

            DialogResult result = MessageBox.Show("First Come First Serve Scheduling ", "", MessageBoxButtons.YesNo, MessageBoxIcon.Information);

            if (result == DialogResult.Yes)
            {
                for (num = 0; num <= np - 1; num++)
                {
                    //MessageBox.Show("Enter Burst time for P" + (num + 1) + ":", "Burst time for Process", MessageBoxButtons.OK, MessageBoxIcon.Question);
                    //Console.WriteLine("\nEnter Burst time for P" + (num + 1) + ":");

                    string input =
                    Microsoft.VisualBasic.Interaction.InputBox("Enter Burst time: ",
                                                       "Burst time for P" + (num + 1),
                                                       "",
                                                       -1, -1);

                    bp[num] = Convert.ToInt64(input);

                    //var input = Console.ReadLine();
                    //bp[num] = Convert.ToInt32(input);
                }

                for (num = 0; num <= np - 1; num++)
                {
                    if (num == 0)
                    {
                        wtp[num] = 0;
                    }
                    else
                    {
                        wtp[num] = wtp[num - 1] + bp[num - 1];
                        MessageBox.Show("Waiting time for P" + (num + 1) + " = " + wtp[num], "Job Queue", MessageBoxButtons.OK, MessageBoxIcon.None);
                    }
                }
                for (num = 0; num <= np - 1; num++)
                {
                    twt = twt + wtp[num];
                }
                awt = twt / np;
                MessageBox.Show("Average waiting time for " + np + " processes" + " = " + awt + " sec(s)", "Average Awaiting Time", MessageBoxButtons.OK, MessageBoxIcon.None);
            }
            

            else if (result == DialogResult.No)
            {
                //this.Hide();
                //Form1 frm = new Form1();
                //frm.ShowDialog();
            }
        }

        public static void sjfAlgorithm(string userInput)
        {
            int np = Convert.ToInt16(userInput);

            double[] bp = new double[np];
            double[] wtp = new double[np];
            double[] p = new double[np];
            double twt = 0.0, awt; 
            int x, num;
            double temp = 0.0;
            bool found = false;

            DialogResult result = MessageBox.Show("Shortest Job First Scheduling", "", MessageBoxButtons.YesNo, MessageBoxIcon.Information);

            if (result == DialogResult.Yes)
            {
                for (num = 0; num <= np - 1; num++)
                {
                    string input =
                        Microsoft.VisualBasic.Interaction.InputBox("Enter burst time: ",
                                                           "Burst time for P" + (num + 1),
                                                           "",
                                                           -1, -1);

                    bp[num] = Convert.ToInt64(input);
                }
                for (num = 0; num <= np - 1; num++)
                {
                    p[num] = bp[num];
                }
                for (x = 0; x <= np - 2; x++)
                {
                    for (num = 0; num <= np - 2; num++)
                    {
                        if (p[num] > p[num + 1])
                        {
                            temp = p[num];
                            p[num] = p[num + 1];
                            p[num + 1] = temp;
                        }
                    }
                }
                for (num = 0; num <= np - 1; num++)
                {
                    if (num == 0)
                    {
                        for (x = 0; x <= np - 1; x++)
                        {
                            if (p[num] == bp[x] && found == false)
                            {
                                wtp[num] = 0;
                                MessageBox.Show("Waiting time for P" + (x + 1) + " = " + wtp[num], "Waiting time:", MessageBoxButtons.OK, MessageBoxIcon.None);
                                //Console.WriteLine("\nWaiting time for P" + (x + 1) + " = " + wtp[num]);
                                bp[x] = 0;
                                found = true;
                            }
                        }
                        found = false;
                    }
                    else
                    {
                        for (x = 0; x <= np - 1; x++)
                        {
                            if (p[num] == bp[x] && found == false)
                            {
                                wtp[num] = wtp[num - 1] + p[num - 1];
                                MessageBox.Show("Waiting time for P" + (x + 1) + " = " + wtp[num], "Waiting time", MessageBoxButtons.OK, MessageBoxIcon.None);
                                //Console.WriteLine("\nWaiting time for P" + (x + 1) + " = " + wtp[num]);
                                bp[x] = 0;
                                found = true;
                            }
                        }
                        found = false;
                    }
                }
                for (num = 0; num <= np - 1; num++)
                {
                    twt = twt + wtp[num];
                }
                MessageBox.Show("Average waiting time for " + np + " processes" + " = " + (awt = twt / np) + " sec(s)", "Average waiting time", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }

        public static void priorityAlgorithm(string userInput)
        {
            int np = Convert.ToInt16(userInput);

            DialogResult result = MessageBox.Show("Priority Scheduling ", "", MessageBoxButtons.YesNo, MessageBoxIcon.Information);

            if (result == DialogResult.Yes)
            {
                double[] bp = new double[np];
                double[] wtp = new double[np + 1];
                int[] p = new int[np];
                int[] sp = new int[np];
                int x, num;
                double twt = 0.0;
                double awt;
                int temp = 0;
                bool found = false;
                for (num = 0; num <= np - 1; num++)
                {
                    string input =
                        Microsoft.VisualBasic.Interaction.InputBox("Enter burst time: ",
                                                           "Burst time for P" + (num + 1),
                                                           "",
                                                           -1, -1);

                    bp[num] = Convert.ToInt64(input);
                }
                for (num = 0; num <= np - 1; num++)
                {
                    string input2 =
                        Microsoft.VisualBasic.Interaction.InputBox("Enter priority: ",
                                                           "Priority for P" + (num + 1),
                                                           "",
                                                           -1, -1);

                    p[num] = Convert.ToInt16(input2);
                }
                for (num = 0; num <= np - 1; num++)
                {
                    sp[num] = p[num];
                }
                for (x = 0; x <= np - 2; x++)
                {
                    for (num = 0; num <= np - 2; num++)
                    {
                        if (sp[num] > sp[num + 1])
                        {
                            temp = sp[num];
                            sp[num] = sp[num + 1];
                            sp[num + 1] = temp;
                        }
                    }
                }
                for (num = 0; num <= np - 1; num++)
                {
                    if (num == 0)
                    {
                        for (x = 0; x <= np - 1; x++)
                        {
                            if (sp[num] == p[x] && found == false)
                            {
                                wtp[num] = 0;
                                MessageBox.Show("Waiting time for P" + (x + 1) + " = " + wtp[num], "Waiting time", MessageBoxButtons.OK);
                                //Console.WriteLine("\nWaiting time for P" + (x + 1) + " = " + wtp[num]);
                                temp = x;
                                p[x] = 0;
                                found = true;
                            }
                        }
                        found = false;
                    }
                    else
                    {
                        for (x = 0; x <= np - 1; x++)
                        {
                            if (sp[num] == p[x] && found == false)
                            {
                                wtp[num] = wtp[num - 1] + bp[temp];
                                MessageBox.Show("Waiting time for P" + (x + 1) + " = " + wtp[num], "Waiting time", MessageBoxButtons.OK);
                                //Console.WriteLine("\nWaiting time for P" + (x + 1) + " = " + wtp[num]);
                                temp = x;
                                p[x] = 0;
                                found = true;
                            }
                        }
                        found = false;
                    }
                }
                for (num = 0; num <= np - 1; num++)
                {
                    twt = twt + wtp[num];
                }
                MessageBox.Show("Average waiting time for " + np + " processes" + " = " + (awt = twt / np) + " sec(s)", "Average waiting time", MessageBoxButtons.OK, MessageBoxIcon.Information);
                //Console.WriteLine("\n\nAverage waiting time: " + (awt = twt / np));
                //Console.ReadLine();
            }
            else
            {
                //this.Hide();
            }
        }

        public static void roundRobinAlgorithm(string userInput)
        {
            int np = Convert.ToInt16(userInput);
            int i, counter = 0;
            double total = 0.0;
            double timeQuantum;
            double waitTime = 0, turnaroundTime = 0;
            double averageWaitTime, averageTurnaroundTime;
            double[] arrivalTime = new double[10];
            double[] burstTime = new double[10];
            double[] temp = new double[10];
            int x = np;

            DialogResult result = MessageBox.Show("Round Robin Scheduling", "", MessageBoxButtons.YesNo, MessageBoxIcon.Information);

            if (result == DialogResult.Yes)
            {
                for (i = 0; i < np; i++)
                {
                    string arrivalInput =
                            Microsoft.VisualBasic.Interaction.InputBox("Enter arrival time: ",
                                                               "Arrival time for P" + (i + 1),
                                                               "",
                                                               -1, -1);

                    arrivalTime[i] = Convert.ToInt64(arrivalInput);

                    string burstInput =
                            Microsoft.VisualBasic.Interaction.InputBox("Enter burst time: ",
                                                               "Burst time for P" + (i + 1),
                                                               "",
                                                               -1, -1);

                    burstTime[i] = Convert.ToInt64(burstInput);

                    temp[i] = burstTime[i];
                }
                string timeQuantumInput =
                            Microsoft.VisualBasic.Interaction.InputBox("Enter time quantum: ", "Time Quantum",
                                                               "",
                                                               -1, -1);

                timeQuantum = Convert.ToInt64(timeQuantumInput);
                Helper.QuantumTime = timeQuantumInput;

                for (total = 0, i = 0; x != 0;)
                {
                    if (temp[i] <= timeQuantum && temp[i] > 0)
                    {
                        total = total + temp[i];
                        temp[i] = 0;
                        counter = 1;
                    }
                    else if (temp[i] > 0)
                    {
                        temp[i] = temp[i] - timeQuantum;
                        total = total + timeQuantum;
                    }
                    if (temp[i] == 0 && counter == 1)
                    {
                        x--;
                        //printf("nProcess[%d]tt%dtt %dttt %d", i + 1, burst_time[i], total - arrival_time[i], total - arrival_time[i] - burst_time[i]);
                        MessageBox.Show("Turnaround time for Process " + (i + 1) + " : " + (total - arrivalTime[i]), "Turnaround time for Process " + (i + 1), MessageBoxButtons.OK);
                        MessageBox.Show("Wait time for Process " + (i + 1) + " : " + (total - arrivalTime[i] - burstTime[i]), "Wait time for Process " + (i + 1), MessageBoxButtons.OK);
                        turnaroundTime = (turnaroundTime + total - arrivalTime[i]);
                        waitTime = (waitTime + total - arrivalTime[i] - burstTime[i]);                        
                        counter = 0;
                    }
                    if (i == np - 1)
                    {
                        i = 0;
                    }
                    else if (arrivalTime[i + 1] <= total)
                    {
                        i++;
                    }
                    else
                    {
                        i = 0;
                    }
                }
                averageWaitTime = Convert.ToInt64(waitTime * 1.0 / np);
                averageTurnaroundTime = Convert.ToInt64(turnaroundTime * 1.0 / np);
                MessageBox.Show("Average wait time for " + np + " processes: " + averageWaitTime + " sec(s)", "", MessageBoxButtons.OK);
                MessageBox.Show("Average turnaround time for " + np + " processes: " + averageTurnaroundTime + " sec(s)", "", MessageBoxButtons.OK);
            }
        }
    }
}

