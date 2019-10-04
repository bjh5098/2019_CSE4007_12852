//Artificial intelligence CSE4007
//Assignment2 - Local Search - Hill Climbing
//2015004575 배준혁

package Nqueens_hill_climbing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

public class Nqueens_hill_climbing {

	public static void main(String[] args) {

		Random random = new Random();
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
		System.out.println(">Hill Climbing");

		int[] board = new int[n];// 체스판 생성
		double startTime = System.currentTimeMillis();// 연산시작시간
		boolean check = true; // Local optimum에서 탈출하지 못하고

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

		for (int cnt = 0; cnt < 10000; cnt++)// 몇번돌려봐야할까, 일단 10000? 대부분 3~4 이내로 끝남.
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

			if (doHillClimbing(board, getHeuristicCost(board)))// 정답을 찾는다면 true 반환
			{
				// success++;
				check = false;// 정답찾음.
				break;// for문 out

			}
		}

		//if (minnum > count) { minnum = count;}
		//if (maxnum < count) { maxnum = count;}

		double hilltime = (System.currentTimeMillis() - startTime) / 1000.000;// 연산종료시간
		if (check)// 답을 못찾았다면 true
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

	// Hill Climbing 참/거짓 리턴
	static boolean doHillClimbing(int[] currentBoard, int currentCost)// 현재보드상황, 현재 휴리스틱값
	{
		int minCost = currentCost;// 받은 휴리스틱값
		int[] minBoard = currentBoard;
		//int row = 0, col = 0;
		if (minCost == 0) return true;
		int[] cloneBoard = currentBoard.clone();
		for (int i = 0; i < currentBoard.length-1; i++)// 보드 열만큼 돌리기
		{
			int currentState;
			currentState = currentBoard[i];

			for (int j = i+1; j < currentBoard.length; j++)
			{
				cloneBoard = currentBoard.clone();
				int tmp = cloneBoard[i];
				cloneBoard[i]=cloneBoard[j];
				cloneBoard[j]=tmp;

				int successorCost = getHeuristicCost(cloneBoard);// 휴리스틱값 구하기

				if (successorCost < minCost)// 후행자가 현상태보다 휴리스틱값이 작으면
				{
					minCost = successorCost;// 가장 작은 후행자의 상태를 및 휴리스틱 값을 저장한다.
					minBoard = cloneBoard;
				}
			}
		}

		if (minCost == 0) // 휴리스틱값이 0이라면
		{
			return true;
		}
		else if (minCost < currentCost)// 이전 휴리스틱값이 현재 상태보다 작다면
		{
			currentBoard = minBoard;
			return doHillClimbing(currentBoard, minCost);// 다시 hill climbing
		}
		else// Local optimum라면
		{
			return false;
		}
	}

	// 휴리스틱 값 계산하기
	static int getHeuristicCost(int[] board) {// 휴리스틱 값을 계산할 체스판의 상태
		int cost = 0;

		for (int i = 0; i < board.length - 1; i++) {
			for (int j = i + 1; j < board.length; j++) {
				// 하나의 행에는 하나의 여왕만 놓여져 있으므로
				// 행끼리의 비교와 대각선 비교만 한다.
				if (board[i] == board[j] || Math.abs(board[i] - board[j]) == j - i)// 행끼리비교 || 대각선비교
				{
					cost++;
				}
			}
		}
		return cost;
	}

	static void printBoard(int[] board)// 출력
	{
		for (int i = 0; i < board.length; i++) {
			System.out.printf("%d ", board[i]);
		}
		System.out.println();
	}

	static void writeFile(String path, String file, int[] board, double time, boolean check)// 출력파일작성
	{
		if (path.contains(" ")) {
			System.out.println("factor error(경로에 스페이스 포함)");
			return;
		}

		String fileName = path + "\\" + file;

		try {
			BufferedWriter fw;
			fw = new BufferedWriter(new FileWriter(fileName, false));

			fw.write(">Hill Climbing\n");
			if (null != board && check == false) {
				for (int e : board)// 보드출력
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
			fw.write(String.valueOf(time));// 연산시간

			fw.flush();
			fw.close();
		} catch (Exception e) {// 예외 처리
			e.printStackTrace();
		}
	}

}
