import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static class Cannon implements Comparable<Cannon> {
		int idx, r, c, atk, past;
		boolean alive;

		public Cannon(int idx, int r, int c, int atk) {
			super();
			this.idx = idx;
			this.r = r;
			this.c = c;
			this.atk = atk;
			this.past = 0; // 0이면 이전에 공격했었다. 매턴마다 1씩증가 공격하면 0으로 지정

			if (atk == 0)
				alive = false;
			else
				alive = true;
		}

		public void attack(Cannon target) {
			// visited 배열 초기화
			for (int i = 1; i <= N; i++) {
				System.arraycopy(init[i], 1, visited[i], 1, M + 1);
			}
			// 공격자 visited
			visited[r][c] = true;
			if (bfs(target)) {
//				System.out.println("레이저");
				// 레이저 공격
				// path배열의 역순으로 처리
				int attackIdx = path[target.idx];
				while (attackIdx != idx) {
					cannonIndex.get(attackIdx - 1).addAtk((atk / 2) * -1);
					visited[cannonIndex.get(attackIdx - 1).r][cannonIndex.get(attackIdx - 1).c] = true;
					attackIdx = path[attackIdx];
				}
			} else {
//				System.out.println("포탄");
				// 포탄 공격
				for (int d = 0; d < 8; d++) {
					int nr = target.r + dy[d];
					int nc = target.c + dx[d];

					nr = reRangeR(nr);
					nc = reRangeC(nc);

					if (map[nr][nc] == 0 || (nr == r && nc == c))
						continue;

					cannonIndex.get(idxMap[nr][nc] - 1).addAtk((atk / 2) * -1);
					visited[nr][nc] = true;
				}
			}

			// 마지막 공격 대상자 처리
			visited[target.r][target.c] = true;
			target.addAtk(atk * -1);
		}

		public boolean bfs(Cannon target) {
			// 초기화
			q.clear();
			for (int i = 1; i <= N; i++) {
				System.arraycopy(init[i], 1, selected[i], 1, M + 1);
			}

			int nr = r;
			int nc = c;

			selected[nr][nc] = true;
			q.offer(new int[] { nr, nc });

			while (!q.isEmpty()) {
				int[] cur = q.poll();

				// 공격 대상자를 찾으면,
				if (cur[0] == target.r && cur[1] == target.c) {
					return true;
				}

				for (int d = 0; d < 4; d++) {
					nr = cur[0] + dy[d];
					nc = cur[1] + dx[d];

					nr = reRangeR(nr);
					nc = reRangeC(nc);

					// 0인 포탑 제외
					if (map[nr][nc] == 0)
						continue;

					if (selected[nr][nc])
						continue;
					selected[nr][nc] = true;

					path[idxMap[nr][nc]] = idxMap[cur[0]][cur[1]];
					q.offer(new int[] { nr, nc });
				}
			}

			return false;

		}

		public int reRangeR(int r) {
			// 위
			if (r <= 0) {
				return N;
			}
			// 아래
			if (r > N)
				return 1;

			return r;
		}

		public int reRangeC(int c) {
			// 왼쪽
			if (c <= 0) {
				return M;
			}
			// 오른쪽
			if (c > M)
				return 1;

			return c;
		}

		public int getAtk() {
			return atk;
		}

		public void setAtk(int atk) {
			this.atk = atk;
		}

		public void addAtk(int atk) {
			this.atk += atk;
		}

		public int getPast() {
			return past;
		}

		public void setPast(int past) {
			this.past = past;
		}

		public void addPast(int past) {
			this.past += past;
		}

		

		@Override
		public String toString() {
			return "Cannon [idx=" + idx + ", r=" + r + ", c=" + c + ", atk=" + atk + ", past=" + past + ", alive="
					+ alive + "]";
		}

		@Override
		public int compareTo(Cannon o) {
			// 공격력이 가장 낮은 포탑
			if (atk > o.atk)
				return 1;
			else if (atk < o.atk)
				return -1;
			else {
				// 가장 최근에 공격한 포탑
				if (past > o.past)
					return 1;
				else if (past < o.past)
					return -1;
				else {
					// 행과 열의 합이 가장 큰 포탑
					if ((r + c) > (o.r + o.c))
						return -1;
					else if ((r + c) < (o.r + o.c))
						return 1;
					else {
						// 열값이 가장 큰 포탑
						if (c > o.c)
							return -1;
						else
							return 1;
					}
				}
			}
		}

	}

	static int N, M, K, idx, map[][], idxMap[][], path[];
	static boolean[][] selected, init, visited;
	static int[] dx = { 1, 0, -1, 0, -1, 1, -1, 1 };
	static int[] dy = { 0, 1, 0, -1, -1, -1, 1, 1 };
	static List<Cannon> cannonIndex, cannonSort;
	static Queue<int[]> q = new ArrayDeque<>();

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());

		map = new int[N + 2][M + 2];
		idxMap = new int[N + 2][M + 2];
		init = new boolean[N + 2][M + 2];
		selected = new boolean[N + 2][M + 2];
		visited = new boolean[N + 2][M + 2];

		cannonIndex = new ArrayList<>();
		cannonSort = new ArrayList<>();

		int tmp;
		idx = 1;
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= M; j++) {
				tmp = Integer.parseInt(st.nextToken());
				map[i][j] = tmp;
				if (tmp != 0) {
					idxMap[i][j] = idx;
					cannonIndex.add(new Cannon(idx++, i, j, tmp));
				}
			}
		}

		path = new int[cannonIndex.size() + 1];

		for (Cannon c : cannonIndex) {
			cannonSort.add(c);
		}

		for (int k = 0; k < K; k++) {
			if(idx-1==1)
				break;

//			System.out.println(k + "분 공격 전");
//			printIdxMap();
//			printMap();
			
			Collections.sort(cannonSort);
			
//			System.out.println("정렬 리스트");
//			for(Cannon c: cannonSort) {
//				System.out.println(c);
//			}
			
			// 1. 공격자 선정
			Cannon weakCannon = cannonSort.get(0);
			// 선정된 공격자에게 N + M 공격력 증가
			weakCannon.addAtk(N + M);

			// 2. 공격대상자 선정
			Cannon strongCannon = cannonSort.get(cannonSort.size() - 1);
			// 공격
//			System.out.print(weakCannon.idx + " -> " + strongCannon.idx + " : ");
			weakCannon.attack(strongCannon);

			// 3. 포탑이 부서진다.
			// 4. 포탑 정비
//			for (int i = 1; i <=N; i++) {
//				for (int j = 1; j <= M; j++) {
//					System.out.print(visited[i][j]?"■":"□");
//				}
//				System.out.println();
//			}
//			System.out.println();
			for (int i = 1; i <= N; i++) {
				for (int j = 1; j <= M; j++) {
					// 죽은 포탑 제외
					if (map[i][j] == 0)
						continue;

					Cannon c = cannonIndex.get(idxMap[i][j] - 1);
					// 공격 시도 시간 증가
					c.addPast(1);
					
					// 포탑이 죽어 있다면,
					if (c.getAtk() <= 0) {
						c.alive = false;
						map[i][j] = 0;
						cannonSort.remove(c);
						idx--;
					}

					// 공격을 하지도, 받지도 않은 포탑이라면
					if (!visited[i][j]) {
						c.addAtk(1);
					}

				}
			}
			//가장 약한 포탑은 공격기간이 늘지 않는다.
			weakCannon.setPast(0);
			
//			System.out.println(k + "분 공격 이후");
//			printIdxMap();
//			printMap();
		} // k end
		int max = Integer.MIN_VALUE;
		for (Cannon c : cannonSort) {
			if (max < c.atk)
				max = c.atk;
		}
		sb.append(max);
		System.out.println(sb);
	}

	public static void printMap() {
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= M; j++) {
				if (map[i][j] == 0)
					System.out.print("0\t");
				else
					System.out.print(cannonIndex.get(idxMap[i][j] - 1).getAtk() + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void printIdxMap() {
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= M; j++) {
				if (map[i][j] == 0)
					System.out.print("0\t");
				else
					System.out.print(cannonIndex.get(idxMap[i][j] - 1).idx + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}
}