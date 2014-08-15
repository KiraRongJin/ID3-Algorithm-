package id3;

import java.util.*;
import java.io.*;


public class ID3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int i, j; // variable for loop
		@SuppressWarnings("resource")
		Scanner myprogram = new Scanner(System.in);
		System.out.println("<you type>");
		@SuppressWarnings("unused")
		String s = myprogram.next();
		Scanner scan_system = new Scanner(System.in);
		System.out.println("Enter names of the files dataset input-partition output-partition");
		@SuppressWarnings("unused")
		String dataset1, partition2, partition3;
		dataset1 = scan_system.next();
		partition2 = scan_system.next();
		partition3 = scan_system.next();
		scan_system.close();
		try
		{
			Scanner scan_dataset = new Scanner(new File("C:\\Users\\IBM\\workspace\\id3\\src\\id3\\testdata1\\dataset-1.txt"));
			Scanner scan_partition2 = new Scanner(new File("C:\\Users\\IBM\\workspace\\id3\\src\\id3\\testdata1\\partition-2.txt"));
			PrintWriter o_partition3 = new PrintWriter(new FileWriter("C:\\Users\\IBM\\workspace\\id3\\src\\id3\\testdata1\\partition-3.txt"));
			int m = scan_dataset.nextInt();
			int n = scan_dataset.nextInt();
			int[][] dataset = new int[m][n];
			for (i = 0; i < m; i++)
				for (j = 0; j < n; j++)
					dataset[i][j] = scan_dataset.nextInt();
			scan_dataset.close();
			
			ArrayList<partition> def_partition = new ArrayList<partition>();
			while (scan_partition2.hasNextLine())
				def_partition.add(new partition(scan_partition2.nextLine()));
			scan_partition2.close();
			
			// If we have no split dataset, create a partition, which name is X
			//  and include all instances
			if (def_partition.size() == 0)
			{
				String t;
				t = "X";
				for (i = 1; i <= m; i++)
					t = t +" "+i;
				def_partition.add(new partition(t));
			}
			
			// calculate entropy
			int record_partition = -1, record_feature = -1; // memorize the answer
			double F = 0;                         
			for (int current_partition = 0; current_partition < def_partition.size(); current_partition++)
			{
				// This array of integer is used for counting # of instances
				int[][] counter = new int[3][3];  
								
				for (i = 0; i < 3; i++)
					for (j = 0; j < 3; j++)
						counter[i][j] = 0;
				
				// total Entropy:
				// 	counter[0][1] for target attribute = 0
				//  counter[0][2] for target attribute = 1	
				// calculate total Entropy
				for (int current_instance = 0; current_instance < def_partition.get(current_partition).get_numbers_size(); current_instance++)
				{
					int instance_num;
					instance_num = def_partition.get(current_partition).get_number(current_instance)-1;
					switch (dataset[instance_num][n-1])
					{
						case 0:
							counter[0][1]++;
							break;
						case 1:
							counter[0][2]++;
							break;
					}
				}
				double entire_dataset_e;
				entire_dataset_e = entropy(counter[0][1], counter[0][2]);
				
				// features Entropy:
				//	(0, 0) for  feature value = 0
				//	(1, 0) for  feature value = 1
				//	(2, 0) for  feature value = 2
				//	(0, 1) for  feature value = 0 and target attribute = 0
				//	(1, 1) for  feature value = 1 and target attribute = 0
				//	(2, 1) for  feature value = 2 and target attribute = 0
				//	(0, 2) for  feature value = 0 and target attribute = 1
				//	(1, 2) for  feature value = 1 and target attribute = 1
				//	(2, 2) for  feature value = 2 and target attribute = 1	
				
				// calculate Entropy with different features
				for (int current_Feature = 0; current_Feature < n-1; current_Feature++)
				{
					for (i = 0; i < 3; i++)
						for (j = 0; j < 3; j++)
							counter[i][j] = 0;
					for (int current_instance = 0; current_instance < def_partition.get(current_partition).get_numbers_size(); current_instance++)
					{
						int instance_num;
						instance_num = def_partition.get(current_partition).get_number(current_instance)-1;
						counter[dataset[instance_num][current_Feature]][0]++;
						counter[dataset[instance_num][current_Feature]][dataset[instance_num][n-1]+1]++;
					}
					int total_instances;
					double current_e;
					total_instances = counter[0][0]+counter[1][0]+counter[2][0];
					current_e = (double)counter[0][0]/total_instances*entropy(counter[0][1], counter[0][2])+(double)counter[1][0]/total_instances*entropy(counter[1][1], counter[1][2])+(double)counter[2][0]/total_instances*entropy(counter[2][1], counter[2][2]);
					double current_F;
					current_F = (double)def_partition.get(current_partition).get_numbers_size()/n*(entire_dataset_e-current_e);
					if (current_F >= F)
					{
						record_partition = current_partition;
						record_feature = current_Feature;
						F = current_F;
					}
				}
			}
			
			
			// partition3 results
			if ((record_partition == -1) && (record_feature == -1))
			{
				System.out.println("All gain is 0. No partition is split.");
				for (int current_partition = 0; current_partition < def_partition.size(); current_partition++)
				{
					o_partition3.print(def_partition.get(current_partition).get_name());
					for (int current_instance = 0; current_instance < def_partition.get(current_partition).get_numbers_size(); current_instance++)
					{
						o_partition3.print(" " + def_partition.get(current_partition).get_number(current_instance));
						o_partition3.println();
					}
				}
			}
			else
			{
				int new_partition_number = 0;
				for (i = 0; i < def_partition.get(record_partition).get_numbers_size(); i++)
				{
					if (dataset[def_partition.get(record_partition).get_number(i)-1][record_feature] == 2)
					{
						new_partition_number = 3;
						break;
					}
				}
				if (new_partition_number == 0)
					new_partition_number = 2;
				System.out.print("Partition "+def_partition.get(record_partition).get_name()+" was replaced with partitions ");
				for (i = 0; i < new_partition_number; i++)
				{
					def_partition.add(new partition(def_partition.get(record_partition).get_name()+(i+1)+" "));
					System.out.print(def_partition.get(record_partition).get_name()+(i+1));
					if (i < new_partition_number-1)
						System.out.print(",");
				}
				for (i = 0; i < def_partition.get(record_partition).get_numbers_size(); i++)
					def_partition.get(def_partition.size()-new_partition_number+(dataset[def_partition.get(record_partition).get_number(i)-1][record_feature])).add_number(def_partition.get(record_partition).get_number(i));
				def_partition.remove(record_partition);
				for (i = 0; i < def_partition.size(); i++)
				{
					o_partition3.print(def_partition.get(i).get_name());
					for (j = 0; j < def_partition.get(i).get_numbers_size(); j++)
						o_partition3.print(" " + def_partition.get(i).get_number(j));
					o_partition3.println();
				}
				System.out.println(" using Feature "+(record_feature+1));
			}
			o_partition3.close();
		}
		catch (IOException exception)
		{
			exception.printStackTrace();
		}
	}
	
	// entropy
	public static double entropy(int x_in, int y_in)
	{
		if (x_in==0 || y_in==0)
		{
			return 0;
		}
		int sum;
		double x, y;
		sum = x_in+y_in;
		x = (double)x_in/sum;
		y = (double)y_in/sum;
		return x*Math.log(1/x)/Math.log(2)+y*Math.log(1/y)/Math.log(2);
	}
	
	// partition
	public static class partition
	{
		private String name;
		private List<Integer> number = new ArrayList<>();
		
		public partition(String new_partition)
		{
			String temp[] = new_partition.split("\\s");
			this.name = temp[0];
			for (int i = 1; i < temp.length; i++)
				this.number.add(Integer.parseInt(temp[i]));
		}
		
		public String get_name()
		{
			return this.name;
		}
		
		public int get_number(int i)
		{
			return this.number.get(i);
		}
		
		public int get_numbers_size()
		{
			return this.number.size();
		}
		
		public void add_number(int new_number)
		{
			this.number.add(new_number);
		}
	}

}
