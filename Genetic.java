
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Genetic
{
	static ArrayList<Integer> X;
	static ArrayList<Double> F_X;
	static double start = 0.0;
	static ArrayList<Double> cummulative;
	static  double end = 1.0;
	static  double child1 = 0,child2 = 0;
	static int[] child1_bin;
	static int[] child2_bin;
	static int population_size;
	static int generations;
	static int start_generation=0;
    static double best_fitness;
    static int best_solution;
    static double crossover_rate=0.0;
    static double mutation_rate=0.0;
    static int child1_index;
    static int child2_index;
    static ArrayList<Double> normalized;
    public static void main(String[] args)
    {
    	X=new ArrayList<Integer>();
    	System.out.print("Enter the population : ");
    	Scanner sc = new Scanner(System.in);
    	population_size=sc.nextInt();
    	System.out.print("Enter the Crossover rate : ");
    	crossover_rate=sc.nextDouble();
    	System.out.print("Enter the Mutation Rate : ");
    	mutation_rate=sc.nextDouble();
    	System.out.println("Enter the number of generations :");
    	generations=sc.nextInt();
    	for(int i=0; i<population_size; i++)
    	{
    		//Generate initial population
    		Random random = new Random();
    		X.add((random.nextInt(2147483647)));
        }
    	while(start_generation!=generations)
    	{
    		//Create the fitness table
    		f(X);
    		double sum = sum_norm(F_X);
    		//find cumulative normalized fitness
    		normalized_f(F_X, sum);
    		cumm_normalized(normalized);
    		//Perform crossover and mutation on selected individuals
    		genetic();
    		start_generation++;
    		if(population_size == 1)
    			break;
    	}
    	System.out.println("\nThe Best X is "+X.get(best_solution)+", f("+X.get(best_solution)+") = "+best_fitness);
    	sc.close();
    }
    
    //Find f(x)
    public static void f(ArrayList<Integer> x)
    {
    	F_X=new ArrayList<Double>();
    	for(int i=0; i<x.size();i++)
    	{
    		double nrm= (Math.sin( x.get(i)*Math.PI/2147483647));
    		if(best_fitness<nrm)
    		{
    			best_fitness=nrm;
    			best_solution=i;
    		}
    		F_X.add(nrm);
    	}	
    }
    public static double sum_norm(ArrayList<Double> x)
    {
    	double sum=0.0;
    	for(int i=0;i<x.size();i++)
    	{
    		sum=sum+ (double) x.get(i);
    	}
    	return sum;
    }
  //Find normalized f(x)
   public static void normalized_f(ArrayList<Double> x,double sum)
   {   
	   normalized=new ArrayList<Double>();;
	   for(int i=0;i<x.size();i++)
	   {
		   normalized.add ((double) x.get(i) / sum);
	   }
   }
   //Find cumulated normalized fitness
   public static void cumm_normalized(ArrayList<Double> x)
   {
	   cummulative=new ArrayList<Double>();
	   double cumm=0.0;
	   for(int i=0;i<x.size();i++)
	   {
		   cumm=cumm +(double)x.get(i);
		   cummulative.add(cumm);
	   }
   }
   //Comparison between random and crossover_rate and mutation rate
   public static void genetic()
   {
	   double  r=new Random().nextDouble();
	   double genetic_rand =start+( r * (end-start));
	   if(population_size>1)
	   {
		   if(genetic_rand<crossover_rate )
		   {
			   crossover();
		   }
		   if(genetic_rand<mutation_rate)
		   {
			   mutation();
		   }
	   }
   }
 //Create the random parents for crossover
   public static void crossover()
   {
	   int	crossover_position;
	   double parent1 = new Random().nextDouble();
	   double parent2 = new Random().nextDouble();
	   for(int i=0; i<population_size;i++)
	   {
		   if(parent1<=cummulative.get(i))
		   {
			   child1 =X.get(i);
			   child1_index=i;
			   break;
		   }
		   else if(parent2<=cummulative.get(i))
		   {
			   child2 =X.get(i);
			   child2_index=i;
			   break;
		   }
	   }
	   Integer integer = Integer.valueOf((int) Math.round(child1));
	   Integer integer1 = Integer.valueOf((int) Math.round(child2));
	   child1_bin=toBinary(integer);
	   child2_bin=toBinary(integer1);
	   double random2 =new Random().nextDouble();
	   crossover_position	= (int) Math.ceil((random2)*31);
	   one_pt_crossover(child1_bin, child2_bin,crossover_position);
   }
   //Implementing one_pt_crossover
   public static void one_pt_crossover(int[] child1_bin2, int[] child2_bin2, int crossover_position) {
	
	   int[] tempa = new int[32];
	   int[] tempb = new int[32];
		//Calculate mask on basis of position i.e. Put 1s at the positions between position and 31. 
		int mask = (0x1 << (int)crossover_position) - 1;
		//Extract bits on the basis of mask
		for(int i=crossover_position;i<32;i++)
		{
			tempa[i] = child1_bin[i] & mask;
			tempb[i] = child2_bin[i] & mask;
		}
		//Zero out the parent bits on the basis of position value
		for(int i=crossover_position;i<32;i++)
		{
			child1_bin[i] = (child1_bin[i] >> crossover_position) << crossover_position;
			child2_bin[i] = (child2_bin[i] >> crossover_position) << crossover_position;
		}
		//Do the crossover by exchanging the bits.
		for(int n=crossover_position;n<32;n++)
		{
			child1_bin[n] = tempb[n] | child1_bin[n];
			
		}
		for(int n=crossover_position;n<32;n++)
		{
			child2_bin[n] = tempa[n] | child2_bin[n];
		}
		if(child1_index == 0)
		{
			X.set((child1_index), toDec(child1_bin));
		}
		else
		{
			X.set((child1_index-1), toDec(child1_bin));
		}
		if(child2_index == 0)
		{
			X.set((child2_index), toDec(child2_bin));
		}
		else
		{
			X.set((child2_index-1), toDec(child2_bin));
		}
}
//Implementing mutation
public static void mutation()
{
	int		mutation_position;
	double random = new Random().nextDouble(); 
	mutation_position	= (int) Math.ceil((random)*31);
	int ch=new Random().nextInt(1);
	switch(ch)
	{
	case 0: child1_bin=mutation1(child1_bin, mutation_position);
	case 1: child2_bin=mutation1(child2_bin,mutation_position);
	}
}
public static int[] mutation1(int[] parents, int pos)
{
	int mask =(((0x1 << pos)-1)%32);
	parents[pos]= parents[pos] ^ (mask%2);
	return parents;
}
//Decimal to binary converter
public static int[] toBinary(int dec) {
	   
	   int count=0;
	   int binary[] = new int[32];
	   int result[] =new int[32];
	   for(int i=0;i<32;i++)
	   {
		   result[i]=0;
	   }
       int index = 0;
       while(dec > 0){
           binary[index++] =dec%2;
           dec = dec/2;
       }
       count=binary.length;
       int i=0;
    	   for(i=binary.length-1;i >= 0;i--)
    	   {
    		   result[32-count]=binary[i];
    		   count--;
    		   
    	   }
       return result;
   }
//Integer array to decimal converter
public static int toDec(int[] inString)
{
	int decimal=0;
	int i;
    int binaryLength;
    binaryLength = inString.length;
    for (i = binaryLength-1, decimal = 0; i >= 0; i--) {
        if (inString[i] == 1) {
            decimal += Math.pow(2, binaryLength-i-1);
        }
    }
    return decimal;
}
   
}