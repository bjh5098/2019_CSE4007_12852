//Artificial intelligence CSE4007
//Assignment2 - Local Search - Hill Climbing
//2015004575 ������

package Nqueens_hill_climbing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class Nqueens_hill_climbing {

	public static void main(String[] args) {

		Random random = new Random();
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
		System.out.println(">Hill Climbing");

		int[] board = new int[n];// ü���� ����
		double startTime = System.currentTimeMillis();// ������۽ð�
		boolean check = true; // Local optimum���� Ż������ ���ϰ�

		/*@@count sum
		int totalrestartnum = 0;
		int count = 0;
		int minnum = 9999;
		int maxnum = -1;
		int success = 0;
		for (int k = 0; k < 10000; k++) {
			count = 0;
			check = true;
		//@@count sum*/

		for (int cnt = 0; cnt < 10000; cnt++)// ������������ұ�, �ϴ� 10000? ��κ� 3~4 �̳��� ����.
		{
			// System.out.println("Random restart count");// Random restart
			// totalrestartnum++;// @@count sum
			// count++;// @@count sum
			ArrayList<Integer> sack = new ArrayList<Integer>();

			for (int l = 0; l < board.length; l++) {
				sack.add(l);
			}

			for (int i = 0; i < board.length; i++) {
				int select = random.nextInt(board.length - i);
				board[i] = sack.get(select);
				sack.remove(select);
			}

			if (doHillClimbing(board, getHeuristicCost(board)))// ������ ã�´ٸ� true ��ȯ
			{
				// success++;
				check = false;// ����ã��.
				break;// for�� out

			}
		}

		//if (minnum > count) { minnum = count;}
		//if (maxnum < count) { maxnum = count;}

		double hilltime = (System.currentTimeMillis() - startTime) / 1000.000;// ��������ð�
		if (check)// ���� ��ã�Ҵٸ� true
		{
			System.out.println("Couldn't escape the Local Optimum");
		}
		/*System.out.println("N : " + n);
		System.out.println("total random restart: " + totalrestartnum); double avg =
		totalrestartnum/10000.00;//@@calc avg restart
		System.out.println("avg random restart: " + avg);
		System.out.println("min random restart: " + minnum);
		System.out.println("max random restart: " + maxnum);
		System.out.println("Success count: " + success);
		*/
		printBoard(board);
		System.out.println("Total Elapsed time: " + hilltime);
		writeFile(path, "result" + n + ".txt", board, hilltime, check);
	}

	// Hill Climbing ��/���� ����
	static boolean doHillClimbing(int[] currentBoard, int currentCost)// ���纸���Ȳ, ���� �޸���ƽ��
	{
		int minCost = currentCost;// ���� �޸���ƽ��
		int[] minBoard = currentBoard;
		//int row = 0, col = 0;
		if (minCost == 0) return true;
		int[] cloneBoard = currentBoard.clone();
		for (int i = 0; i < currentBoard.length-1; i++)// ���� ����ŭ ������
		{
			int currentState;
			currentState = currentBoard[i];

			for (int j = i+1; j < currentBoard.length; j++)
			{
				cloneBoard = currentBoard.clone();
				int tmp = cloneBoard[i];
				cloneBoard[i]=cloneBoard[j];
				cloneBoard[j]=tmp;

				int successorCost = getHeuristicCost(cloneBoard);// �޸���ƽ�� ���ϱ�

				if (successorCost < minCost)// �����ڰ� �����º��� �޸���ƽ���� ������
				{
					minCost = successorCost;// ���� ���� �������� ���¸� �� �޸���ƽ ���� �����Ѵ�.
					minBoard = cloneBoard;
				}
			}
		}

		if (minCost == 0) // �޸���ƽ���� 0�̶��
		{
			return true;
		}
		else if (minCost < currentCost)// ���� �޸���ƽ���� ���� ���º��� �۴ٸ�
		{
			currentBoard = minBoard;
			return doHillClimbing(currentBoard, minCost);// �ٽ� hill climbing
		}
		else// Local optimum���
		{
			return false;
		}
	}

	// �޸���ƽ �� ����ϱ�
	static int getHeuristicCost(int[] board) {// �޸���ƽ ���� ����� ü������ ����
		int cost = 0;

		for (int i = 0; i < board.length - 1; i++) {
			for (int j = i + 1; j < board.length; j++) {
				// �ϳ��� �࿡�� �ϳ��� ���ո� ������ �����Ƿ�
				// �ೢ���� �񱳿� �밢�� �񱳸� �Ѵ�.
				if (board[i] == board[j] || Math.abs(board[i] - board[j]) == j - i)// �ೢ���� || �밢����
				{
					cost++;
				}
			}
		}
		return cost;
	}

	static void printBoard(int[] board)// ���
	{
		for (int i = 0; i < board.length; i++) {
			System.out.printf("%d ", board[i]);
		}
		System.out.println();
	}

	static void writeFile(String path, String file, int[] board, double time, boolean check)// ��������ۼ�
	{
		if (path.contains(" ")) {
			System.out.println("factor error(��ο� �����̽� ����)");
			return;
		}

		String fileName = path + "\\" + file;

		try {
			BufferedWriter fw;
			fw = new BufferedWriter(new FileWriter(fileName, false));

			fw.write(">Hill Climbing\n");
			if (null != board && check == false) {
				for (int e : board)// �������
				{
					fw.write(String.valueOf(e) + " ");
				}
			} else {
				fw.write("No solution");//
			}
			fw.write("\n");
			if (check) {
				fw.write("Couldn't escape the Local Optimum\n");
			}

			fw.write("Total Elapsed Time: ");
			fw.write(String.valueOf(time));// ����ð�

			fw.flush();
			fw.close();
		} catch (Exception e) {// ���� ó��
			e.printStackTrace();
		}
	}

}
