import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

	static class Person {
		int idx, r, c, Tr, Tc; 
		boolean alive = false;
		
		public Person(int idx, int r, int c, int tr, int tc) {
			super();
			this.idx = idx;
			this.r = r;
			this.c = c;
			this.Tr = tr;
			this.Tc = tc;
			visited[r][c] = true;
		}

		// 1 가고싶은 편의점 방향 한칸 전진
		public void move(int t) {
			
			if(idx+1 > t)
				return;
			
			int minD = -1;
			int minValue = Integer.MAX_VALUE;
			int tmp;
			for (int d = 0; d < 4; d++) {
				int nr = r + dy[d];
				int nc = c + dx[d];
				
				if(nr <=0 || nc <=0 || nr > N || nc > N)
					continue;
				
				if(visited[nr][nc])
					continue;
				
				tmp = calD(nr, nc, Tr, Tc);
				if(tmp < minValue) {
					minValue = tmp;
					minD = d;
				}
				
			}
			
			r += dy[minD];
			c += dx[minD];
			
			isAlive();
		}
		
		// 2만약 편의점에 도착한다면 멈춘다.
		// 다른 사람은 못지나가게 visited 처리
		public void isAlive() {
			if(Tr == r && Tc == c) {
				alive = true;
				visited[Tr][Tc] = true;
				market--;
			}
		}

		@Override
		public String toString() {
			return "Person [idx=" + idx + ", r=" + r + ", c=" + c + ", Tr=" + Tr + ", Tc=" + Tc + "]";
		}

	}

	static int N, M, map[][], baseIdx, market;
	static int[] dx = {0, -1, 1, 0};
	static int[] dy = {-1, 0, 0, 1};
	static List<int[]> baseCamp;
	static Person[] people;
	static boolean visited[][];
	static PriorityQueue<int[]> pq;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());

		map = new int[N + 2][N + 2];
		visited = new boolean[N + 2][N + 2];
		baseCamp = new ArrayList<>();
		people = new Person[M];

		// 원하는 편의점에서 가장 가까운 베이스캠프를 찾기 위한 우선순위큐
		// 각 사람마다 clear()한 후 다같이 사용하자.
		pq = new PriorityQueue<>(new Comparator<int[]>() {
			@Override
			public int compare(int[] o1, int[] o2) {
				// 거리가 짧은게 우선 순위
				if (o1[1] > o2[1])
					return 1;
				else if (o1[1] < o2[1])
					return -1;
				else {
					// 행이 작은게 우선순위
					if (o1[2] > o2[2])
						return 1;
					else if (o1[2] < o2[2])
						return -1;
					else {
						// 열이 작은게 우선순위
						if (o1[3] > o2[3])
							return 1;
						else
							return -1;
					}
				}
			}
		});

		int tmp;
		int baseIdx = 0;
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				tmp = Integer.parseInt(st.nextToken());
				map[i][j] = tmp;
				if (tmp == 1)
					baseCamp.add(new int[] { baseIdx++, i, j, 0 });
			}
		}

		int x, y, targetMarket;
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			y = Integer.parseInt(st.nextToken());
			x = Integer.parseInt(st.nextToken());

			// 우선순위 큐를 비우고 좌표 넣기
			pq.clear();

			for (int[] bc : baseCamp) {
				if (bc[3] == 1)
					continue;
				// 베이스캠프 인덱스, 최단 거리, 베이스캠프 행(r/y), 베이스캠프 열(c/x)
				pq.offer(new int[] { bc[0], calD(x, y, bc[2], bc[1]), bc[1], bc[2]}); 
			}
			baseCamp.get(pq.peek()[0])[3] = 1; // 선택된 베이스캠프는 못하게 플래그 세우기
			people[i] = new Person(i, pq.peek()[2], pq.peek()[3] ,y, x);
		}

//		for (int i = 0; i < M; i++) {
//			System.out.println(people[i]);
//		}
		market = M;
		int T = 1;
		while(market > 0) {
			
			for (int i = 0; i < M; i++) {
				if(!people[i].alive)
					people[i].move(T);
			}
			
			T++;
		}
		
		sb.append(T);
		System.out.println(sb);
	}

	public static int calD(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
	}

}