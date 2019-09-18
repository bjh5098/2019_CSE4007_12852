//Artificial intelligence CSE4007
//Assignment1 - DFS, BFS, DFID
//2015004575 ������


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
            n = Integer.parseInt(args[0]);//N*N �Է�
            path = args[1];//������� ���
        } catch (Exception e) {
            System.out.println("factor error");
            e.printStackTrace();
            return;
        }
        
        ////<<�ؽ�Ʈ ���� �ۼ� ���� �ܼ�ȭ�� ����غ���>>
        // <<DFS>>
        System.out.println("> dfs");        
        long start = System.currentTimeMillis(); // dfs ���� �� �ð�����
        int[] dfsBoard = dfs(0, new int[n], n);  // dfs�� ������ ü����, board[i] = j�� i��, j���� ���� ������ �ǹ��Ѵ�.
        double dfsTime = (System.currentTimeMillis() - start) / 1000.000; //�������� ���� �ð����� ���� �ð��� ���
        if (null != dfsBoard) 
        {
            printBoard(dfsBoard);// ������ ã�Ҵٸ� �ش� ���� board�� ����Ѵ�.
        } 
        else 
        {
            System.out.println("No solution"); // ������ ���ٸ� No solution�� ���
        }   
        System.out.println(dfsTime);

        // <<BFS>>
        System.out.println("> bfs");
        start = System.currentTimeMillis();
        int [] bfsBoard = bfs(1, n);// ��ó�� ������ ã�� ���¸� ��ȯ, ������ ���ٸ� null
        double bfsTime = (System.currentTimeMillis() - start) / 1000.000;//�������� ���� �ð����� ���� �ð��� ���
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
        System.out.println("> dfid");// dfid ���� �� �ð�����
        start = System.currentTimeMillis();
        int[] dfidBoard = dfid(n);// ��ó�� ������ ã�� ���¸� ��ȯ, ������ ���ٸ� null
        double dfidTime = (System.currentTimeMillis() - start) / 1000.000;//�������� ���� �ð����� ���� �ð��� ���
        if (null != dfidBoard)
        {
           printBoard(dfidBoard);
        }
        else
        {
            System.out.println("No solution");
        }
        System.out.println(dfidTime);

       
        writeFile(path, "result" + n + ".txt",  // ����� ���Ͽ� �ۼ�
                  dfsBoard, bfsBoard, dfidBoard,
                  dfsTime, bfsTime, dfidTime);
	}
	
	//////////
	
    static boolean check(int[] board)//������ �迭�� ��� ������ ��ġ�� �������� ����Ȳ ������ ��ġ�� �������� Ȯ���Ѵ�.
    {
        if (board.length <= 1)
        {
            return false;
        }
        for (int i = 0; i < board.length; i++)
        {
            for (int j = i + 1; j < board.length; j++) 
            {
                if (board[i] == board[j])// ���� �� �˻�
                {
                    return false;
                } 
                else if (j - i == Math.abs(board[j] - board[i]))// �밢�� �˻�
                {
                    return false;
                }
            }
        }
        return true;
    }

    //���� �켱 Ž��
    static int[] dfs(int level, int[] board, int n)
    {
        if (level >= n) {// ���̰� n�� ���������� �˻� - pruning �����ʰ�
            if (check(board))
            {
                return board;
            }
            return null;
        }
        for (int i = 0; i < n; i++)
        {
            board[level] = i;
            if (null != dfs(level + 1, board, n))// ���� �ϳ� �÷��� ���ȣ��
            {
                return board;
            }
        }
        return null;
    }
    
    //�ʺ�켱Ž�� 
    static int[] bfs(int level, int n) // append�ϸ� [0], [1], [2], ... [n - 1]
    {
        Queue<List> q = new LinkedList<List>();//����Ʈ�� ��� ť
        for (int i = 0; i < n; i++)// 1�࿡ depth 1�� ������ �߰�
        {
            List<Integer> temp = new LinkedList<>();
            temp.add(i);
            q.add(temp);//[0], [1], [2], ... [n - 1]
        }

        while (true) 
        {
            // �ٻ���忡�� ������ ������ ���ϸ� ������ ���� ������ ���� -> �̰� pruning�̶��..
            if (level >= n) // ���̰� n�̻��̸� break
            {
                break;
            }
            for (int i = 0; i < Math.pow(n, level); i++) 
            {
                List<Integer> pop = q.remove(); // Queue�� ���� �� �� ���Ҹ� pop���� ��ȯ
                for (int j = 0; j < n; j++)
                {
                    List<Integer> node = new LinkedList<Integer>(pop);// pop�� ��忡 ����� �ڽ� ������ Ž��
                    node.add(j);//[0]~[n-1] �߰�
                    if (node.size() >= n)// ��� �࿡ �ϳ��� ���� ���������� ����
                    {
                        int[] board = new int[node.size()];//boardũ�⼳��
                        for (int k = 0; k < node.size(); k++)
                        {
                            board[k] = node.get(k);
                        }
                        if (check(board)) //�������� �˻�
                        {
                            return board;
                        }
                    }
                    q.add(node);// ���� ������ �ƴ϶��(������ ����ֱ�..) - ������ pruning �����ʱ�?? �����ϱ�
                }
            }
            // ���̰� level�� ��� ��忡 ���� Ž���ߴٸ� ���� ����
            level++;
        }
        return null;
    }

    // �ݺ��� ���� �켱 ���� Ž��
    static int[] dfid(int n)
    {
        for (int level = 0; level <= n; level++)//���̸� �� �ܰ辿 ����
        {
            int[] board = new int[n];
            board = dfs(0, board, level);// �־��� ���̱��� ��带 ���� �� Ž��
            if (null != board)
            {
                return board;
            }
        }
        return null;
    }


    static void printBoard(int[] board)// Nqueen ���� ���
    {
        for (int i = 0; i < board.length; i++)
        {
            System.out.printf("%d ", board[i]);
        }
        System.out.println();
    }

    //�������
    static void writeFile(String path, String file,
                          int[] dfs, int[] bfs, int[] dfid,
                          double dfsTime, double bfsTime, double dfidTime)
    {
        if (path.contains(" ")) {
            System.out.println("��ο� �����̽��� ���ԵǾ����ϴ�.");
            System.out.println("��ü ��θ� \"\"�� ���ΰų�, "
                             + "�ٸ� ��θ� �����ϼ���.");
            return;
        }
        String fileName = path + "\\" + file ;
        try 
        {
            BufferedWriter fw = new BufferedWriter(new FileWriter(fileName, false)); 
            fw.write("> DFS\n");// ���Ͽ� ���ڿ� ����
            
            if (null != dfs)
            {
                fw.write("Location : ");
                for (int e : dfs)// dfs �� ���� �ϳ��� ���
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
                for (int e : bfs)// bfs �� ���� �ϳ��� ���
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
                for (int e : dfid)// dfid �� ���� �ϳ��� ���
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
            fw.flush();// ���� ��Ʈ�� ������
            fw.close();// ���� ��ü �ݱ�
        } 
        catch (Exception e)
        {
            e.printStackTrace();// ���� ó��
        }
    }

}
