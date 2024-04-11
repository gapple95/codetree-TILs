import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class Main {

	static class Cannon implements Comparable<Cannon> {
		int idx, r, c, atk;
		int wasAtk;
		boolean alive;

		public Cannon(int idx, int r, int c, int atk) {
			super();
			this.idx = idx;
			this.r = r;
			this.c = c;
			this.atk = atk;
			if (this.atk == 0)
				this.alive = false;
			else
				this.alive = true;
			this.wasAtk = 0;
		}

		public void attack(Cannon target) {

			// 레이저 공격
			minRange = Integer.MAX_VALUE;
			visited[r][c] = true;
			dfs(r, c, 0);
			if (minRange == Integer.MAX_VALUE) {
				// 포탑 공격
				minMap[this.r][this.c] = true;
				realCannon(target.r, target.c);
			}
			// 최단 경로를 구한 경우
			else {
				
				for (int i = 0; i < N + 2; i++) {
					for (int j = 0; j < N + 2; j++) {
						if ((i == r && j == c) || (i == target.r && j == target.c))
							continue;
						if (minMap[i][j])
							cannonsIndex.get(map[i][j]).addAtk(this.atk / 2 * -1);
					}
				}
			}
//			for (int i = 1; i <= N; i++) {
//				for (int j = 1; j <= N; j++) {
//					System.out.print(minMap[i][j] ? "■" : "□");
//				}
//				System.out.println();
//			}
			target.addAtk(atk * -1);
		}

		public void dfs(int r, int c, int range) {
			// 결국 목표지점에 도달한다면
			if (cannonSorted.get(cannonSorted.size() - 1).r == r && cannonSorted.get(cannonSorted.size() - 1).c == c) {
				if (minRange > range) {
					for (int i = 0; i < N + 2; i++) {
						System.arraycopy(visited[i], 0, minMap[i], 0, M + 2);
					}
					minRange = range;
				}
				return;
			}

			for (int d = 0; d < 4; d++) {
				int nr = r + dy[d];
				int nc = c + dx[d];

				// 위, 왼쪽을 넘어갈 경우
				if (nc <= 0 && nr <= 0) {
					nr = N;
					nc = N;
				}

				// 위 오른쪽을 넘어갈 경우
				if (nr <= 0 && nr > N) {
					nr = N;
					nc = 0;
				}

				// 아래 왼쪽을 넘어갈 경우
				if (nr > N && nc <= 0) {
					nr = 0;
					nc = N;
				}

				// 아래 오른쪽을 넘어갈 경우
				if (nr > N && nc > N) {
					nr = 0;
					nc = 0;
				}

				// 우측이 넘어갈 경우
				if (nc > N)
					nc = 1;

				// 좌측이 넘어갈 경우
				if (nc <= 0)
					nc = N;

				// 위가 넘어갈 경우
				if (nr > N)
					nr = 1;

				// 아래가 넘어갈 경우
				if (nr <= 0)
					nr = N;

				if (map[nr][nc] == 0)
					continue;

				if (visited[nr][nc])
					continue;

				visited[nr][nc] = true;
				dfs(nr, nc, range + 1);
				visited[nr][nc] = false;
			}
		}

		public void realCannon(int r, int c) {
			minMap[r][c] = true;
			for (int d = 0; d < 8; d++) {
				int nr = r + dy8[d];
				int nc = c + dx8[d];

				// 위, 왼쪽을 넘어갈 경우
				if (nc <= 0 && nr <= 0) {
					nr = N;
					nc = N;
				}

				// 위 오른쪽을 넘어갈 경우
				if (nr <= 0 && nr > N) {
					nr = N;
					nc = 0;
				}

				// 아래 왼쪽을 넘어갈 경우
				if (nr > N && nc <= 0) {
					nr = 0;
					nc = N;
				}

				// 아래 오른쪽을 넘어갈 경우
				if (nr > N && nc > N) {
					nr = 0;
					nc = 0;
				}

				// 우측이 넘어갈 경우
				if (nc > N)
					nc = 1;

				// 좌측이 넘어갈 경우
				if (nc <= 0)
					nc = N;

				// 위가 넘어갈 경우
				if (nr > N)
					nr = 1;

				// 아래가 넘어갈 경우
				if (nr <= 0)
					nr = N;

				if (map[nr][nc] != 0) {
					cannonsIndex.get(map[nr][nc]).addAtk((atk / 2) * -1);
					minMap[nr][nc] = true;
				}
			}
		}

		@Override
		public int compareTo(Cannon o) {
			// 공격력이 가장 낮은 포탑
			if (atk > o.atk)
				return 1;
			else if (atk < o.atk)
				return -1;
			else {
				// 가장 최근 공격한 포탑
				if (wasAtk > o.wasAtk)
					return 1;
				else if (wasAtk < o.wasAtk)
					return -1;
				else {
					// 행과 열의 합이 가장 큰 포탑
					if ((r + c) > (o.r + o.c))
						return -1;
					else if ((r + c) < (o.r + o.c))
						return 1;
					else {
						// 열 값이 가장 큰 포탑
						if (c > o.c)
							return -1;
						else
							return 1;
					}

				}
			}
		}

		public int getAtk() {
			return atk;
		}

		public void addAtk(int atk) {
			this.atk += atk;
		}

		public void setAtk(int atk) {
			this.atk = atk;
		}

		public int getWasAtk() {
			return wasAtk;
		}

		public void setWasAtk(int wasAtk) {
			this.wasAtk = wasAtk;
		}

		public boolean isAlive() {
			return alive;
		}

		public void setAlive(boolean alive) {
			this.alive = alive;
		}

		@Override
		public String toString() {
			return "Cannon [idx=" + idx + ", r=" + r + ", c=" + c + ", atk=" + atk + ", wasAtk=" + wasAtk + ", alive="
					+ alive + "]";
		}

	}

	static int map[][], N, M, K;
	static List<Cannon> cannonSorted, cannonsIndex;
	static int[] dx = { 1, 0, -1, 0 };
	static int[] dy = { 0, 1, 0, -1 };
	static int[] dx8 = { -1, 0, 1, -1, 1, -1, 0, 1 };
	static int[] dy8 = { -1, -1, -1, 0, 0, 1, 1, 1 };

	static int minRange;
	static boolean[][] minMap, visited;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());

		map = new int[N + 2][M + 2];

		cannonSorted = new ArrayList<>();
		cannonsIndex = new ArrayList<>();
		cannonsIndex.add(new Cannon(0, 0, 0, 0));

		int atk, idx = 1;
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= M; j++) {
				atk = Integer.parseInt(st.nextToken());
				if (atk != 0) {
					map[i][j] = idx;
					cannonsIndex.add(new Cannon(idx++, i, j, atk));
				} else
					map[i][j] = 0;
			}
		}

//		System.out.println("시작");
//		printCannons();
//		printMap();

		// 정렬될 리스트 복사
		for (Cannon cannon : cannonsIndex) {
			cannonSorted.add(cannon);
		}
		// 0 제거
		cannonSorted.remove(0);

		for (int k = 0; k < K; k++) {

			// 가장 약한 포탑과 강한 포탑을 위한 정렬
			Collections.sort(cannonSorted);

			// 가장 약한 포탑 고르기
			int weekIndex = cannonSorted.get(0).idx;
//			System.out.println(cannonSorted);

			// 가장 약한 포탑에 atk 올려주기
			cannonsIndex.get(weekIndex).addAtk(N + M);
//			System.out.println("약한 포탑 atk 올려주기");
//			printMapValue();
//			printMap();

			// 가장 강한 포탑 고르기
			int strongIndex = cannonSorted.get(cannonSorted.size() - 1).idx;

			// 가장 약한 포탑이, 가장 강한 포탑 공격하기
			// 공격하기 전 visited 배열 초기화
			minMap = new boolean[N + 2][M + 2];
			visited = new boolean[N + 2][M + 2];

			cannonsIndex.get(weekIndex).attack(cannonsIndex.get(strongIndex));
//			printMap();
			// 정비
			for (int i = 1; i <= N; i++) {
				for (int j = 1; j <= N; j++) {
					Cannon cannon = cannonsIndex.get(map[i][j]);
					if (!cannon.alive)
						continue;
					// 한번도 건들지 않았으면 수리
					if (!minMap[i][j])
						cannon.addAtk(1);
					if (cannon.getAtk() == 0) {
						// 죽은 포탑처리
						for (int c = 0; c < cannonSorted.size(); c++) {
							if (cannonSorted.get(c).idx == map[i][j]) {
								cannonSorted.remove(c);
								break;
							}
						}
						map[i][j] = 0;
						cannon.setAlive(false);

					}
					// 전에 공격을 안했으면 + 1
					cannon.setWasAtk(cannon.getWasAtk() + 1);
				}
			}

			cannonsIndex.get(weekIndex).setWasAtk(0);

//			printMapValue();
//			break;
		}
		
		int max = Integer.MIN_VALUE;
		for(Cannon c: cannonsIndex) {
			if(!c.alive)
				continue;
			if(max < c.atk)
				max = c.atk;
		}
		sb.append(max);
		System.out.println(sb);

	}

	public static void printMap() {
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				System.out.print(map[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void printMapValue() {
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				System.out.print(cannonsIndex.get(map[i][j]).atk + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void printCannons() {
		for (Cannon c : cannonSorted) {
			if (c.alive)
				System.out.println(c);
		}
		System.out.println();
	}
}