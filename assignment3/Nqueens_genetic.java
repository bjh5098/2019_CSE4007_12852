import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Nqueens_genetic {
	public static Random random = new Random();
	public static void main(String[] args)
	{
		int n;// N*N 입력
		String path;
		try {
			n = Integer.parseInt(args[0]);// N*N 입력
			path = args[1];// 출력파일경로
		} catch (Exception e) {
			System.out.println("factor error");
			e.printStackTrace();
			return;
		}
		
		System.out.println("> Genetic Algorithm");
		double startTime = System.currentTimeMillis();
		
        int[] solution = doGeneticAlgorithm(n, n*n, 1000000, 0.01, n*n/5, n);// 유전 알고리즘
        double genetime = (System.currentTimeMillis() - startTime) / 1000.000;// 연산종료시간

        // 유전알고리즘의 반환된 결과를 출력한다.
        // 세대가 끝났음에도 답을 찾지 못한 경우,마지막 세대에서 가장 cost가 낮은 유전자를 반환한다.
        printBoard(solution);
        System.out.println("Total Elapsed time: " + genetime);
        writeFile(path, "result" + n + ".txt", solution, genetime);
	}
	
    public static class Chromosome implements Comparable<Chromosome>
    {
        private int cost;//겹치는 퀸 수
        private int[] information;//염색체 정보

        public Chromosome(int cost, int[] information)//생성자
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

        public int compareTo(Chromosome chromosome)//cost 점수가 낮은 순서대로 정렬
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
        //size : 1염색체 사이즈, population : 한세대 전체 염색체 수, maxgeneration : 최대 세대(한계)
    	//mutationProbability : 돌연변이 확률, numofParents : 다음세대 부모 염색체 수, k : K토너먼트의 k
        ArrayList<Chromosome> chromosomes = generateInitChromosomes(size, population);// 임의의 초기 염색체 집단 생성
        
        for (int generation = 1; generation <= maxgeneration; generation++)
        {
            System.out.println("generation: " + generation);
            for (Chromosome chromosome: chromosomes)
            {
                if (0 == chromosome.getCost())//정답인 염색체가 있다면 정보를 반환!
                {
                    return chromosome.getInformation();
                }
            }
            Collections.sort(chromosomes);// cost를 기준으로 정렬
            ArrayList<Chromosome> parents = kTournamentSelection(chromosomes, k, numofParents); //k개 유전자를 부모염색체로 선택.
            chromosomes.removeAll(chromosomes);//기존꺼 삭제
            chromosomes = generateNextChromosomes(parents, mutationProbability, population, size);//부모,직속,전달,교배,돌연변이 -> 다음 세대 염색체 생성
        }
        return chromosomes.get(0).getInformation();// 마지막 세대에서도 정답을 찾지 못한다면 가장 Cost가 낮은 친구로 반환
    }


    static ArrayList<Chromosome> generateNextChromosomes(ArrayList parents, double mutationProbability, int population, int size)
    {
    	//다음 세대의 염색체를 발현한다.직속 전달, 교배 및 돌연변이 연산
        //parents : 교배될 부모 염색체, multationProbability : 돌연변이 발현 확률, population : 세대 인구수, size : 염색체 하나 크기
    	ArrayList<Chromosome> next = new ArrayList<>();
        // 교배를 통해 자식 염색체 생성, 해당 과정 내에서 돌연변이도 발생! 다음 세대의 자식은 현재 인구수의 2배로
        ArrayList<Chromosome> childs = crossover(parents, 2 * population, size, mutationProbability);
        next.addAll(parents);// 부모는 그대로 다음 세대로 전달
        next.addAll(childs);// 추가 생성된 자식 염색체도 전달
        return next;
    }
    
    static ArrayList<Chromosome> crossover(ArrayList parents, int numofChild, int size, double mutationProbability)//교배연산 진행 및 돌연변이 발생
    {
        //parents : 부모염색체, numofChild : 생성할 자식염색체 수, size : 염색체 크기, mutationProbability : 돌연변이 확률
    	ArrayList<Chromosome> childs = new ArrayList();//자식염색체
        for (int i = 0; i < numofChild; i++) 
        {
            int randomMother = random.nextInt(parents.size());// 모 염색체
            Chromosome mother = (Chromosome) parents.get(randomMother);

            int randomFather = random.nextInt(parents.size());// 부 염색체
            Chromosome father = (Chromosome) parents.get(randomFather);

            while (true) 
            {
                if (mother.getInformation().equals(father.getInformation()))//부모가 다른 염색체라면
                {
                    break;//while 탈출
                }
                randomFather = random.nextInt(parents.size());//
                father = (Chromosome) parents.get(randomFather);//부 염색체 다시 가져오기
            }
            
            //// 여기서부터 교배를 해보자///////////////////////////////
            //중간일정부분(하나그룹)만 아빠 염색체로 교배시키기.
            int first = randomMother % mother.getInformation().length;//두지점 생성
            int second = randomMother % father.getInformation().length;//두지점 생성
            while (true)
            {
                if (first != second)
                {
                    break;
                }
                second = random.nextInt(father.getInformation().length);//랜덤지점.
            }

            if (first > second)// first < second 맞춰주기
            {
                int t = first;
                first = second;
                second = t;
            }

            int[] temp = new int[mother.getInformation().length];
            for (int j = 0; j < first; j++)// 0부터 first까지는 모 염색체에서
            {
                temp[j] = mother.getInformation()[j];
            }
            for (int j = first; j < second; j++)// first부터 second까지는 부 염색체에서
            {
                temp[j] = father.getInformation()[j];
            }
            for (int j = second; j < mother.getInformation().length; j++)// second부터 마지막까지는 모 염색체에서
            {
                temp[j] = mother.getInformation()[j];
            }
            /*
            //랜덤으로 섞어서 교배하는 방법
            int momused = 0;
            int dadused = 0;//엄마아빠 유전자가 이용되었는지 체크
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
	            if(momused == 1 && dadused == 1)//부모가 잘 섞였다면?
	            {
	            	break;
	            }
            }*/
            /////교배끝/////////////////////////////////////////////
            
            temp = mutation(temp, mutationProbability);// 확률에 따라 돌변변이발생
            Chromosome child = new Chromosome(calculateCost(temp), temp);
            childs.add(child);
        }
        return childs;
    }

    static int[] mutation(int[] chromosome, double mutationProbability)
    {
        //확률적으로 돌연변이 발생시키기
    	//chromosome : 돌연변이를 발생시킬 염색체, mutationProbability : 돌연변이가 발생할 확률
        boolean isMutation = false;// 돌연변이 발생 여부
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
            //System.out.println("Mutation!");//변이!
        }
        return chromosome;
    }

    static ArrayList<Chromosome> generateInitChromosomes(int size, int population)//<>필요한가?
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
    	//염색체 생성하기.
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
    	//휴리스틱같이 겹치는 퀸의 수를 계산한다.
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
    	//k개 선택하여, k중 가장 cost값이 낮은 염색체를 부모로 선택. N번 반복해서 N개 부모 생성.
    	// chromosomes : 전체 염색체 집단, k : 임의로 선택할 염색체 수, n 반환할 부모의 염색체 수
        ArrayList<Chromosome> parents = new ArrayList<>();
        Random random = new Random();
        ArrayList<Chromosome> candidates = new ArrayList<>();

        for (int parent = 0; parent < n; parent++)// n개의 부모를 선택하기 위해 n번 돌리기
        {
            for (int i = 0; i < k; i++)// k개 선택
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
            System.out.println("factor error(경로에 스페이스 포함)");
            return;
        }

        String fileName = path + "\\" + file ;

        try {
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, false));
            fw.write("> Genetic Algorithm\n");
            if (null != board)
            {
                fw.write("Location: ");
                for (int e : board)// board의 각 원소
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
        } catch (Exception e) {// 예외 처리
            e.printStackTrace();
        }
    }
}
