import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

/*
 * 마법의 숲 탐색
 * 
 * R행 C열의 격자 형태로 이루어진 마법의 숲을 탐색
 * 격자는 가장 위를 1행 가장 아래를 R행으로 한다.
 * 
 * 숲의 동쪽, 서쪽, 남쪽은 벽으로 막혀 있고 북쪽으로만 들어올 수 있다.
 * 
 * 총 K명의 정령이 각자 골렘을 타고 숲을 탐색.
 * 각 골렘은 십자 모양의 구조를 가지고 있으며, 중앙 칸을 포함해 총 5칸을 차지한다.
 * 골렘의 중앙을 제외한 4칸 중 한 칸은 골렘의 출구.
 * 정령은 어떤 방향에서든 골렘에 탑승할 수 있지만, 골렘에서 내릴 때에는 정해진 출구에만 내릴수있다.
 * 
 * i번째로 숲을 탐색하는 골렘은 숲의 가장 북쪽에서 시작해 골렘의 중앙이 C_i열이 되도록
 * 위치에서 내려오기 시작
 * 
 * 초기 골렘의 출구는 d_i의 방향에 위치
 * 
 * 골렘은 숲을 탐색하기 위해 다음과 같은 우선순위로 이동 더이상 못 움직일때까지 해당과정반복
 * 1. 남쪽으로 한칸 내려간다.
 * 		그림처럼 초록색 칸들이 비어있을때만 남쪽으로 내려갈 수 있다.
 * 2. 1의 방법으로 이동할 수 없으면 서쪽 방향으로 회전하면서 내려간다.
 * 		위 그림의 초록색 칸들이 비어있을 때에만 서쪽방향으로 회전하면서 내려갈 수 있따.
 * 		이 때 서쪽 방향으로 한 칸 이동을 한 뒤 내려가기 때문에 골렘을 기준으로 서쪽 한칸
 * 		이 모두 비어 있어야 함에 우의 또한 이렇게 이동할 때 출구가 반시계방향으로 이동
 * 3. 1과 2의 방법으로 이동할 수 없으면 동쪽 방향으로 회전하면서 내려간다.
 * 		그림 처럼 오른쪽 초록색 칸들이 비어있을 때에만 동쪽 방향으로 회전하면서 내려갈 수있다.
 * 		이 때 동쪽 방향으로 한 칸 이동을 한 뒤 내려가기 때문에 골렘을 기준으로 동쪽 한 칸이
 * 		모두 비어 있어야한다.
 * 		이동할때마다 출구가 시계 방향으로 이동한다.
 * 
 * 골렘이 이동할 수 있는 강장 남쪽에 도달해 더이상 이동할 수 없으면 정령은 골렘 내에서 
 * 상하좌우 인접한 칸으로 이동이 가능하다.
 * 단, 만약 현재 위치하고 있는 골렘의 출구가 다른 골렘과 인접하고 있다면 해당 출구를 통해
 * 다른 골렘으로 이동할 수 있다.
 * 정령은 갈 수 있는 모든 칸 중 가장 남쪽의 칸으로 이동하고, 이동을 완전히 종료
 * 이때 정령의 위치가 해당 정령의 최종 위치가 된다.
 * @ 이 문제에서는 정령의 최종 위치의 행 번호의 합을 구해야하기에 각 정령이 도달하게 되는
 *   최종 위치를 누적해야한다.
 *   
 * 만약 골렘이 최대한 남쪽으로 이동했지만, 골렘의 몸 일부가 여전히 숲을 벗어난 상태라면,
 * 숲을 모두 초기화 => 골렘들 다 삭제
 * @ 단, 이 경우에 정령이 도달하는 최종 위치를 답에 포함시키지 않는다.
 * 
 * 
 * # 입력
 * 첫 번째 줄에는 숲의 크기를 의미하는 R, C, 정령의 수 K가 공백을 사이에 두고 주어진다.
 * 
 * 그 다음 줄부터 K개의 줄에 걸쳐 각 골렘이 출발하는 열 C_i, 골렘의 출구 방향 정보 d_i가
 * 공백을 사이에 두고 주어진다.
 * 
 * 골렘의 출구 방향 정보 d_i는 0과 3사이의 수로 주어지며 각각의 숫자 0,1,2,3은
 * 북,동,남,서를 의미
 * 
 * 5≤R,C≤70
 * 1≤K≤1000
 * 2≤ c_i ≤C−1
 * 0 ≤ d_i ≤ 3
 * 
 * # 출력
 * 첫 번째 줄에 각 정령들이 최종적으로 위치한 행의 총합을 출력
 * 
 * 
 * 
 * 
 */

public class Main {
	
	static int R,C,K, map[][], sum;
	static final int GAP = 3; // 3층위에서 시작한다고 가정, 이래야 출발선을 인지하기 편할듯
	static boolean visited[][];
	static StringTokenizer st;
	static StringBuilder sb = new StringBuilder();
	static Deque<int[]> q;
	static int[] dx = {0,1,0,-1};
	static int[] dy = {-1,0,1,0};
	
	static class Golam {
		int r; // 중심좌표
		int c; // 중심좌표
		int d;
		
		public Golam(int c, int d) {
			this.r = 0;
			this.c = c;
			this.d = d;
		}
		
		int trip() {
			int result = 0;
			q = new ArrayDeque<>();
			visited = new boolean[R][C];
			visited[r][c] = true;
			q.offer(new int[] {r,c});
			
			int nr, nc, cur[];
			while(!q.isEmpty()) {
				cur = q.poll();
				if(cur[0] > result) {
					result = cur[0];
					// 만약 최하층을 갔으면, 다른것이 의미가 없기 때문에 바로 while문 탈출
					if(result + 1 == R)
						break;
				}
				for(int i=0; i<4; i++) {
					nr = cur[0] + dy[i];
					nc = cur[1] + dx[i];
					// 범위를 벗어났을 때
					if(nr < GAP || nr >= R || nc < 0 || nc >= C)
						continue;
					
					// 1에서는 상하좌우의 3으로만 갈 수있다.
					if(map[cur[0]][cur[1]] == 1) {
						if(map[nr][nc] == 3) {
							// 한번 들린 곳
							if(visited[nr][nc])
								continue;
							visited[nr][nc] = true;
							q.offer(new int[] {nr, nc});
						}
					}
					// 2,3은 어디든 갈 수 있다.
					else {
						if(map[nr][nc] == 0)
							continue;
						
						// 한번 들린 곳
						if(visited[nr][nc])
							continue;
						visited[nr][nc] = true;
						q.offer(new int[] {nr, nc});
					}
					
				}
			}
			
			return result - GAP + 1;
		}
		
		boolean position() {
			if(r<=GAP) {
				init();
				return false;
			}
			map[r][c] = 3; // 3은 중심을 나타냄
			map[r-1][c] = 1;
			map[r][c+1] = 1;
			map[r+1][c] = 1;
			map[r][c-1] = 1;
			
			// 2는 입구
			switch(d) {
			case 0:
				map[r-1][c] = 2;
				break;
			case 1:
				map[r][c+1] = 2;
				break;
			case 2:
				map[r+1][c] = 2;
				break;
			case 3:
				map[r][c-1] = 2;
				break;
			}
			return true;
		}
		
		void down() {
			while(true) {
				// 아래 한칸 이동이 가능하다면,
				if((r!=R) && (r+2 < R) && (map[r+1][c-1] == 0 && map[r+2][c] == 0 && map[r+1][c+1] == 0)) {
					r++; // 한 칸 내려가기
				}
				// 왼쪽
				else if(		// 왼쪽 확인
						// 아래 확인
						(r+2 < R && c-2 >= 0) &&
						(map[r-1][c-1] == 0 && map[r][c-2] == 0 && map[r+1][c-1] == 0) &&
						(map[r+2][c-1] == 0 && map[r+1][c-2] == 0)
						) {
					r++;
					c--;
					spinL();
				}
					
				//오른쪽
				else if(
						// 오른쪽 확인
						// 아래 확인
						(c+2 < C && r+2 < R) &&
						(map[r-1][c+1] == 0 && map[r][c+2] == 0 && map[r+2][c+1] == 0) &&
						(map[r+2][c+1] == 0 && map[r+1][c+2] == 0) 
						) {
					r++;
					c++;
					spinR();
				}
				
				else
					break;
					
			}
		}
		
		void spinR() {
			++d;
			if(d>3)
				d=0;
		}
		
		void spinL() {
			--d;
			if(d<0)
				d=3;
		}
		
		void init() {
			map = new int[R][C];
		}
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		st = new StringTokenizer(br.readLine());
		
		R = Integer.parseInt(st.nextToken()) + GAP;
		C = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[R][C];
		
		int c, d;
		for (int i = 0; i < K; i++) {
			st = new StringTokenizer(br.readLine());
			
			c = Integer.parseInt(st.nextToken());
			d = Integer.parseInt(st.nextToken());
			
//			System.out.println(c + " " + d);
			
			Golam golam = new Golam(c-1, d);
			

			golam.down();

			
			if(golam.position()) {
				sum += golam.trip();
//				printMap();
//				int tmp = golam.trip();
//				System.out.println(golam.r + " " + golam.c);
//				System.out.println(tmp);
//				sum += tmp;
//				System.out.println(sum + "\n");
			}
			
		}
		
		sb.append(sum);
		System.out.println(sb);
		
	}

	
	static void printMap() {
		System.out.println("print Map");
		for (int i = GAP-1; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
}