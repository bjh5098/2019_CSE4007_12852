//Artificial intelligence CSE4007
//Assignment1 - DFS, BFS, DFID
//2015004575 배준혁


package Nqueens_dfs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Nqueens_dfs {

	public static void main(String[] args)
	{
        int n;
        String path;
        try {
            n = Integer.parseInt(args[0]);//N*N 입력
            path = args[1];//출력파일 경로
        } catch (Exception e) {
            System.out.println("factor error");
            e.printStackTrace();
            return;
        }
        
        ////<<텍스트 파일 작성 이전 콘솔화면 출력해보기>>
        // <<DFS>>
        System.out.println("> dfs");        
        long start = System.currentTimeMillis(); // dfs 실행 전 시간저장
        int[] dfsBoard = dfs(0, new int[n], n);  // dfs를 진행할 체스판, board[i] = j는 i행, j열에 퀸이 있음을 의미한다.
        double dfsTime = (System.currentTimeMillis() - start) / 1000.000; //연산이후 현재 시각에서 이전 시각을 출력
        if (null != dfsBoard) 
        {
            printBoard(dfsBoard);// 정답을 찾았다면 해당 상태 board를 출력한다.
        } 
        else 
        {
            System.out.println("No solution"); // 정답이 없다면 No solution을 출력
        }   
        System.out.println(dfsTime);

        // <<BFS>>
        System.out.println("> bfs");
        start = System.currentTimeMillis();
        int [] bfsBoard = bfs(1, n);// 맨처음 정답을 찾은 상태를 반환, 정답이 없다면 null
        double bfsTime = (System.currentTimeMillis() - start) / 1000.000;//연산이후 현재 시각에서 이전 시각을 출력
        if (null != bfsBoard)
        {
           printBoard(bfsBoard);
        }
        else
        {
            System.out.println("No solution");
        }
        System.out.println(bfsTime);

        // <<DFID>>
        System.out.println("> dfid");// dfid 실행 전 시간저장
        start = System.currentTimeMillis();
        int[] dfidBoard = dfid(n);// 맨처음 정답을 찾은 상태를 반환, 정답이 없다면 null
        double dfidTime = (System.currentTimeMillis() - start) / 1000.000;//연산이후 현재 시각에서 이전 시각을 출력
        if (null != dfidBoard)
        {
           printBoard(dfidBoard);
        }
        else
        {
            System.out.println("No solution");
        }
        System.out.println(dfidTime);

       
        writeFile(path, "result" + n + ".txt",  // 결과를 파일에 작성
                  dfsBoard, bfsBoard, dfidBoard,
                  dfsTime, bfsTime, dfidTime);
	}
	
	//////////
	
    static boolean check(int[] board)//정수형 배열에 담긴 퀸들의 위치를 바탕으로 현상황 퀸들의 배치가 적합한지 확인한다.
    {
        if (board.length <= 1)
        {
            return false;
        }
        for (int i = 0; i < board.length; i++)
        {
            for (int j = i + 1; j < board.length; j++) 
            {
                if (board[i] == board[j])// 같은 열 검사
                {
                    return false;
                } 
                else if (j - i == Math.abs(board[j] - board[i]))// 대각선 검사
                {
                    return false;
                }
            }
        }
        return true;
    }

    //깊이 우선 탐색
    static int[] dfs(int level, int[] board, int n)
    {
        if (level >= n) {// 깊이가 n에 도착했을때 검사 - pruning 하지않고
            if (check(board))
            {
                return board;
            }
            return null;
        }
        for (int i = 0; i < n; i++)
        {
            board[level] = i;
            if (null != dfs(level + 1, board, n))// 깊이 하나 늘려서 재귀호출
            {
                return board;
            }
        }
        return null;
    }
    
    //너비우선탐색 
    static int[] bfs(int level, int n) // append하면 [0], [1], [2], ... [n - 1]
    {
        Queue<List> q = new LinkedList<List>();//리스트를 담는 큐
        for (int i = 0; i < n; i++)// 1행에 depth 1인 노드들을 추가
        {
            List<Integer> temp = new LinkedList<>();
            temp.add(i);
            q.add(temp);//[0], [1], [2], ... [n - 1]
        }

        while (true) 
        {
            // 잎새노드에서 정답을 구하지 못하면 정답이 없는 것으로 간주 -> 이게 pruning이라고..
            if (level >= n) // 깊이가 n이상이면 break
            {
                break;
            }
            for (int i = 0; i < Math.pow(n, level); i++) 
            {
                List<Integer> pop = q.remove(); // Queue의 가장 맨 앞 원소를 pop으로 소환
                for (int j = 0; j < n; j++)
                {
                    List<Integer> node = new LinkedList<Integer>(pop);// pop한 노드에 연결된 자식 노드들을 탐색
                    node.add(j);//[0]~[n-1] 추가
                    if (node.size() >= n)// 모든 행에 하나씩 퀸이 놓여졌으면 실행
                    {
                        int[] board = new int[node.size()];//board크기설정
                        for (int k = 0; k < node.size(); k++)
                        {
                            board[k] = node.get(k);
                        }
                        if (check(board)) //정답인지 검사
                        {
                            return board;
                        }
                    }
                    q.add(node);// 만약 정답이 아니라면(꺼낸거 집어넣기..) - 하지만 pruning 하지않기?? 질문하기
                }
            }
            // 깊이가 level인 모든 노드에 대해 탐색했다면 깊이 증가
            level++;
        }
        return null;
    }

    // 반복적 깊이 우선 증가 탐색
    static int[] dfid(int n)
    {
        for (int level = 0; level <= n; level++)//깊이를 한 단계씩 증가
        {
            int[] board = new int[n];
            board = dfs(0, board, level);// 주어진 깊이까지 노드를 구성 후 탐색
            if (null != board)
            {
                return board;
            }
        }
        return null;
    }


    static void printBoard(int[] board)// Nqueen 보드 출력
    {
        for (int i = 0; i < board.length; i++)
        {
            System.out.printf("%d ", board[i]);
        }
        System.out.println();
    }

    //파일출력
    static void writeFile(String path, String file,
                          int[] dfs, int[] bfs, int[] dfid,
                          double dfsTime, double bfsTime, double dfidTime)
    {
        if (path.contains(" ")) {
            System.out.println("경로에 스페이스가 포함되었습니다.");
            System.out.println("전체 경로를 \"\"로 감싸거나, "
                             + "다른 경로를 지정하세요.");
            return;
        }
        String fileName = path + "\\" + file ;
        try 
        {
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, false)); 
            fw.write("> DFS\n");// 파일에 문자열 쓰기
            
            if (null != dfs)
            {
                fw.write("Location : ");
                for (int e : dfs)// dfs 각 원소 하나씩 출력
                {
                    fw.write(String.valueOf(e) + " ");
                }
            } 
            else 
            {
                fw.write("No solution");
            }

            fw.write("\n");
            fw.write("Time : ");
            fw.write(String.valueOf(dfsTime) + "\n\n");
            
            fw.write("> BFS\n");
            
            if (null != bfs)
            {
                fw.write("Location : ");
                for (int e : bfs)// bfs 각 원소 하나씩 출력
                {
                    fw.write(String.valueOf(e) + " ");
                }
            }
            else
            {
                fw.write("No solution");
            }

            fw.write("\n");
            fw.write("Time : ");
            fw.write(String.valueOf(bfsTime) + "\n\n");

            fw.write("> DFID\n");
            
            if (null != dfid)
            {
                fw.write("Location : ");
                for (int e : dfid)// dfid 각 원소 하나씩 출력
                {
                    fw.write(String.valueOf(e) + " ");
                }
            }
            else
            {
                fw.write("No solution");
            }

            fw.write("\n");
            fw.write("Time : ");
            fw.write(String.valueOf(dfidTime) + "\n");
            fw.flush();// 파일 스트림 보내기
            fw.close();// 파일 객체 닫기
        } 
        catch (Exception e)
        {
            e.printStackTrace();// 예외 처리
        }
    }

}
