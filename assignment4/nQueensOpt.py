#2015004575 Bae Jun Hyeok
#CSE4007_Assignment4
from z3 import *
import time

# Number of Queens
print("N: ") ## N입력유도
N = int(input()) ## N입력받기

start = time.time() #시작시간 측정
# Variables
X = [Int("x_%s" % (row)) for row in range(N)] # 배열 X는 1차원 배열로 queen의 위치를 표기

# Constraints
domain = [And(X[row] >= 0, X[row] < N) # 배열 X에 표기되는 퀸들의 위치는 0부터 N-1까지의 값
          for row in range(N) ] 

rowConst = [X[i] != X[j] # 배열 X의 i성분과 j성분은 서로 같은 성분이 아님.
            for i in range(N-1) for j in range(i+1,N)]

digConst = [And(X[i] - X[j] != i-j, X[i]-X[j] != j-i) # 대각선으로 서로 마주하는 퀸이 있으면 안됨. index차이값 != 퀸위치차이값
            for i in range(N-1) for j in range(i+1,N) ]

n_queens_c = domain + rowConst + digConst # 모든 Constraints 조합

s = Solver() # z3 Solver() 생성
s.add(n_queens_c) # Constraints 입력

if s.check() == sat: # 성공여부?
    m = s.model() # 연산
    r = [m.evaluate(X[i]) for i in range(N)] # 결과값 r에 저장 
    print_matrix(r) # 결과값 출력

print("elapsed time: ", time.time() - start, " sec") #종료시간 측정 및 출력

