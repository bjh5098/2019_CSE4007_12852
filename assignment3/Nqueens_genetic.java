import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Nqueens_genetic {
	public static Random random = new Random();
	public static void main(String[] args)
	{
		int n;// N*N �Է�
		String path;
		try {
			n = Integer.parseInt(args[0]);// N*N �Է�
			path = args[1];// ������ϰ��
		} catch (Exception e) {
			System.out.println("factor error");
			e.printStackTrace();
			return;
		}
		
		System.out.println("> Genetic Algorithm");
		double startTime = System.currentTimeMillis();
		
        int[] solution = doGeneticAlgorithm(n, n*n, 1000000, 0.01, n*n/5, n);// ���� �˰���
        double genetime = (System.currentTimeMillis() - startTime) / 1000.000;// ��������ð�

        // �����˰����� ��ȯ�� ����� ����Ѵ�.
        // ���밡 ���������� ���� ã�� ���� ���,������ ���뿡�� ���� cost�� ���� �����ڸ� ��ȯ�Ѵ�.
        printBoard(solution);
        System.out.println("Total Elapsed time: " + genetime);
        writeFile(path, "result" + n + ".txt", solution, genetime);
	}
	
    public static class Chromosome implements Comparable<Chromosome>
    {
        private int cost;//��ġ�� �� ��
        private int[] information;//����ü ����

        public Chromosome(int cost, int[] information)//������
        {
            this.cost = cost;
            this.information = information;
        }

        public int getCost()//getter
        {
            return cost;
        }
        public int[] getInformation()
        {
            return information;
        }
        
        public void setCost(int cost)//setter
        {
            this.cost = cost;
        }

        public void setInformation(int[] information)
        {
            this.information = information;
        }

        public int compareTo(Chromosome chromosome)//cost ������ ���� ������� ����
        {
            if (this.cost > chromosome.getCost())
            {
                return 1;
            }
            else if (this.cost < chromosome.getCost())
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }

    static int[] doGeneticAlgorithm(int size, int population, int maxgeneration, double mutationProbability, int numofParents, int k)
    {
        //size : 1����ü ������, population : �Ѽ��� ��ü ����ü ��, maxgeneration : �ִ� ����(�Ѱ�)
    	//mutationProbability : �������� Ȯ��, numofParents : �������� �θ� ����ü ��, k : K��ʸ�Ʈ�� k
        ArrayList<Chromosome> chromosomes = generateInitChromosomes(size, population);// ������ �ʱ� ����ü ���� ����
        
        for (int generation = 1; generation <= maxgeneration; generation++)
        {
            System.out.println("generation: " + generation);
            for (Chromosome chromosome: chromosomes)
            {
                if (0 == chromosome.getCost())//������ ����ü�� �ִٸ� ������ ��ȯ!
                {
                    return chromosome.getInformation();
                }
            }
            Collections.sort(chromosomes);// cost�� �������� ����
            ArrayList<Chromosome> parents = kTournamentSelection(chromosomes, k, numofParents); //k�� �����ڸ� �θ𿰻�ü�� ����.
            chromosomes.removeAll(chromosomes);//������ ����
            chromosomes = generateNextChromosomes(parents, mutationProbability, population, size);//�θ�,����,����,����,�������� -> ���� ���� ����ü ����
        }
        return chromosomes.get(0).getInformation();// ������ ���뿡���� ������ ã�� ���Ѵٸ� ���� Cost�� ���� ģ���� ��ȯ
    }


    static ArrayList<Chromosome> generateNextChromosomes(ArrayList parents, double mutationProbability, int population, int size)
    {
    	//���� ������ ����ü�� �����Ѵ�.���� ����, ���� �� �������� ����
        //parents : ����� �θ� ����ü, multationProbability : �������� ���� Ȯ��, population : ���� �α���, size : ����ü �ϳ� ũ��
    	ArrayList<Chromosome> next = new ArrayList<>();
        // ���踦 ���� �ڽ� ����ü ����, �ش� ���� ������ �������̵� �߻�! ���� ������ �ڽ��� ���� �α����� 2���
        ArrayList<Chromosome> childs = crossover(parents, 2 * population, size, mutationProbability);
        next.addAll(parents);// �θ�� �״�� ���� ����� ����
        next.addAll(childs);// �߰� ������ �ڽ� ����ü�� ����
        return next;
    }
    
    static ArrayList<Chromosome> crossover(ArrayList parents, int numofChild, int size, double mutationProbability)//���迬�� ���� �� �������� �߻�
    {
        //parents : �θ𿰻�ü, numofChild : ������ �ڽĿ���ü ��, size : ����ü ũ��, mutationProbability : �������� Ȯ��
    	ArrayList<Chromosome> childs = new ArrayList();//�ڽĿ���ü
        for (int i = 0; i < numofChild; i++) 
        {
            int randomMother = random.nextInt(parents.size());// �� ����ü
            Chromosome mother = (Chromosome) parents.get(randomMother);

            int randomFather = random.nextInt(parents.size());// �� ����ü
            Chromosome father = (Chromosome) parents.get(randomFather);

            while (true) 
            {
                if (mother.getInformation().equals(father.getInformation()))//�θ� �ٸ� ����ü���
                {
                    break;//while Ż��
                }
                randomFather = random.nextInt(parents.size());//
                father = (Chromosome) parents.get(randomFather);//�� ����ü �ٽ� ��������
            }
            
            //// ���⼭���� ���踦 �غ���///////////////////////////////
            //�߰������κ�(�ϳ��׷�)�� �ƺ� ����ü�� �����Ű��.
            int first = randomMother % mother.getInformation().length;//������ ����
            int second = randomMother % father.getInformation().length;//������ ����
            while (true)
            {
                if (first != second)
                {
                    break;
                }
                second = random.nextInt(father.getInformation().length);//��������.
            }

            if (first > second)// first < second �����ֱ�
            {
                int t = first;
                first = second;
                second = t;
            }

            int[] temp = new int[mother.getInformation().length];
            for (int j = 0; j < first; j++)// 0���� first������ �� ����ü����
            {
                temp[j] = mother.getInformation()[j];
            }
            for (int j = first; j < second; j++)// first���� second������ �� ����ü����
            {
                temp[j] = father.getInformation()[j];
            }
            for (int j = second; j < mother.getInformation().length; j++)// second���� ������������ �� ����ü����
            {
                temp[j] = mother.getInformation()[j];
            }
            /*
            //�������� ��� �����ϴ� ���
            int momused = 0;
            int dadused = 0;//�����ƺ� �����ڰ� �̿�Ǿ����� üũ
            int guess = 0;
            int[] temp = new int[mother.getInformation().length];
            while(true)
            {
            	momused = 0;
            	dadused = 0;
	            for (int j = 0; j < mother.getInformation().length; j++)
	            {
	            	guess = random.nextInt(father.getInformation().length)%2;
	            	if(guess == 1)
	            	{
	            		temp[j] = mother.getInformation()[j];
	            		momused = 1;
	            	}
	            	else//guess==0
	            	{
	            		temp[j] = father.getInformation()[j];
	            		dadused = 1;
	            	}
	            	
	            }
	            if(momused == 1 && dadused == 1)//�θ� �� �����ٸ�?
	            {
	            	break;
	            }
            }*/
            /////���賡/////////////////////////////////////////////
            
            temp = mutation(temp, mutationProbability);// Ȯ���� ���� �������̹߻�
            Chromosome child = new Chromosome(calculateCost(temp), temp);
            childs.add(child);
        }
        return childs;
    }

    static int[] mutation(int[] chromosome, double mutationProbability)
    {
        //Ȯ�������� �������� �߻���Ű��
    	//chromosome : �������̸� �߻���ų ����ü, mutationProbability : �������̰� �߻��� Ȯ��
        boolean isMutation = false;// �������� �߻� ����
        for (int i = 0; i < chromosome.length; i++)
        {
            int r = random.nextInt((int) (1 / mutationProbability));
            if (r < 1)
            {
                isMutation = true;
                chromosome[i] = Math.abs(random.nextInt(chromosome.length));
            }
        }
        if (isMutation)
        {
            //System.out.println("Mutation!");//����!
        }
        return chromosome;
    }

    static ArrayList<Chromosome> generateInitChromosomes(int size, int population)//<>�ʿ��Ѱ�?
    {
        ArrayList<Chromosome> chromosomes = new ArrayList<>();

        for (int i = 0; i < population; i++)
        {
            Chromosome chromosome;
            while (true)
            {
                chromosome = generateChromosome(size);
                if (!chromosomes.contains(chromosome))
                {
                    break;
                }
            }
            chromosomes.add(chromosome);
        }
        return chromosomes;
    }

    static Chromosome generateChromosome(int size)
    {
    	//����ü �����ϱ�.
        Random random = new Random();
        int[] information = new int[size];

        for (int i = 0; i < size; i++)
        {
            information[i] = Math.abs(random.nextInt(size));
        }
        Chromosome chromosome = new Chromosome(calculateCost(information), information);
        return chromosome;
    }

    static int calculateCost(int[] information)
    {
    	//�޸���ƽ���� ��ġ�� ���� ���� ����Ѵ�.
        int cost = 0;
        for (int i = 0; i < information.length-1; i++)
        {
            for (int j = i + 1; j < information.length; j++)
            {
                if (information[i] == information[j] || Math.abs(information[i] - information[j]) == j - i)
                {
                    cost++;
                }
            }
        }
        return cost;
    }

    static ArrayList<Chromosome> kTournamentSelection(ArrayList chromosomes, int k, int n)
    {
    	//k�� �����Ͽ�, k�� ���� cost���� ���� ����ü�� �θ�� ����. N�� �ݺ��ؼ� N�� �θ� ����.
    	// chromosomes : ��ü ����ü ����, k : ���Ƿ� ������ ����ü ��, n ��ȯ�� �θ��� ����ü ��
        ArrayList<Chromosome> parents = new ArrayList<>();
        Random random = new Random();
        ArrayList<Chromosome> candidates = new ArrayList<>();

        for (int parent = 0; parent < n; parent++)// n���� �θ� �����ϱ� ���� n�� ������
        {
            for (int i = 0; i < k; i++)// k�� ����
            {
                candidates.add((Chromosome)chromosomes.get(random.nextInt(chromosomes.size())));
            }
            Collections.sort(candidates);
            parents.add(candidates.get(0));
            candidates.removeAll(candidates);
        }
        return parents;
    }

    static void printBoard(int[] board)
    {
        if (null == board)
        {
            System.out.println("No solution");
        }
        else
        {
            for (int e : board)
            {
                System.out.printf("%d ", e);
            }
            System.out.println();
        }
    }

    static void writeFile(String path, String file, int[] board, double time)
    {
        if (path.contains(" "))
        {
            System.out.println("factor error(��ο� �����̽� ����)");
            return;
        }

        String fileName = path + "\\" + file ;

        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, false));
            fw.write("> Genetic Algorithm\n");
            if (null != board)
            {
                fw.write("Location: ");
                for (int e : board)// board�� �� ����
                {
                    fw.write(String.valueOf(e) + " ");
                }
            }
            else
            {
                fw.write("No solution");
            }
            fw.write("\n");
            fw.write("Total Elapsed Time: ");
            fw.write(String.valueOf(time));
            
            fw.flush();
            fw.close();
        } catch (Exception e) {// ���� ó��
            e.printStackTrace();
        }
    }
}
