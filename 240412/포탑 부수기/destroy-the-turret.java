import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
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
//			visited[r][c] = true;
			int direction = bfs(target.r, target.c);

			// 모든 경로를 돌아봤는데도 변하는게 없다면,
			// 포탑 공격
			if (direction == -1) {
				minMap[this.r][this.c] = true;
				realCannon(target.r, target.c);
			}
			// 최단 경로를 구한 경우
			else {
				int nr = target.r;
				int nc = target.c;
				int d;
				minMap[nr][nc] = true;
				// 5가 아닐때까지
				while (direction != 5) {
					d = direction % 10;
					direction /= 10;

					// 목표지점부터 거꾸로 가면서 true 체크
					nr = nr - dy[d];
					nc = nc - dx[d];
					minMap[nr][nc] = true;
					
					if(!(nr == r && nc == c)) {
						cannonsIndex.get(map[nr][nc]).addAtk((atk/2) * -1);
					}
					
				}
			}

//			for (int i = 1; i <= N; i++) {
//				for (int j = 1; j <= M; j++) {
//					System.out.print(minMap[i][j] ? "■" : "□");
//				}
//				System.out.println();
//			}
//			System.out.println();

			// 해당 자리는 데미지가 제대로 들어간다.
			target.addAtk(atk * -1);
		}

		public int bfs(int Tr, int Tc) {
			Queue<int[]> q = new ArrayDeque<>();
			boolean[][] visited = new boolean[N + 2][M + 2];
			q.offer(new int[] { r, c, 5 });

			while (!q.isEmpty()) {
				int[] cur = q.poll();

				if (cur[0] == Tr && cur[1] == Tc) {
					// 해당 지점을 찾는다면
					// 지금까지 모아온 방향 값 반환
					return cur[2];
				}

				for (int d = 0; d < 4; d++) {
					int nr = cur[0] + dy[d];
					int nc = cur[1] + dx[d];

					// 위, 왼쪽을 넘어갈 경우
					if (nc <= 0 && nr <= 0) {
						nr = N;
						nc = M;
					}

					// 위 오른쪽을 넘어갈 경우
					if (nr <= 0 && nr > N) {
						nr = N;
						nc = 1;
					}

					// 아래 왼쪽을 넘어갈 경우
					if (nr > N && nc <= 0) {
						nr = 1;
						nc = M;
					}

					// 아래 오른쪽을 넘어갈 경우
					if (nr > N && nc > M) {
						nr = 1;
						nc = 1;
					}

					// 우측이 넘어갈 경우
					if (nc > M)
						nc = 1;

					// 좌측이 넘어갈 경우
					if (nc <= 0)
						nc = M;

					// 위가 넘어갈 경우
					if (nr > N)
						nr = 1;

					// 아래가 넘어갈 경우
					if (nr <= 0)
						nr = N;

					// 인덱스가 0인것은 넘기기
					if (map[nr][nc] == 0)
						continue;

					// 죽어있는 포탑이면 스킵
					if (!cannonsIndex.get(map[nr][nc]).alive || cannonsIndex.get(map[nr][nc]).atk <= 0)
						continue;

					if (visited[nr][nc])
						continue;
					visited[nr][nc] = true;

					q.offer(new int[] { nr, nc, cur[2] * 10 + d });

				}
			}

			return -1;
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
					nc = M;
				}

				// 위 오른쪽을 넘어갈 경우
				if (nr <= 0 && nr > N) {
					nr = N;
					nc = 1;
				}

				// 아래 왼쪽을 넘어갈 경우
				if (nr > N && nc <= 0) {
					nr = 1;
					nc = M;
				}

				// 아래 오른쪽을 넘어갈 경우
				if (nr > N && nc > M) {
					nr = 1;
					nc = 1;
				}

				// 우측이 넘어갈 경우
				if (nc > M)
					nc = 1;

				// 좌측이 넘어갈 경우
				if (nc <= 0)
					nc = M;

				// 위가 넘어갈 경우
				if (nr > N)
					nr = 1;

				// 아래가 넘어갈 경우
				if (nr <= 0)
					nr = N;

				// 인덱스가 0인것은 넘기기
				if (map[nr][nc] == 0)
					continue;

				// 죽어있는 포탑이면 스킵
				if (!cannonsIndex.get(map[nr][nc]).alive || cannonsIndex.get(map[nr][nc]).atk <= 0)
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
					nc = M;
				}

				// 위 오른쪽을 넘어갈 경우
				if (nr <= 0 && nr > N) {
					nr = N;
					nc = 1;
				}

				// 아래 왼쪽을 넘어갈 경우
				if (nr > N && nc <= 0) {
					nr = 1;
					nc = M;
				}

				// 아래 오른쪽을 넘어갈 경우
				if (nr > N && nc > M) {
					nr = 1;
					nc = 1;
				}

				// 우측이 넘어갈 경우
				if (nc > M)
					nc = 1;

				// 좌측이 넘어갈 경우
				if (nc <= 0)
					nc = M;

				// 위가 넘어갈 경우
				if (nr > N)
					nr = 1;

				// 아래가 넘어갈 경우
				if (nr <= 0)
					nr = N;

				if (map[nr][nc] != 0) {
					// 자시자신이면 스킵 => 자기자신은 피해를 입지 않는다.
					if (nr == r && nc == c)
						continue;
					// 죽어있는 포탑이면 스킵
					if (!cannonsIndex.get(map[nr][nc]).alive || cannonsIndex.get(map[nr][nc]).atk <= 0)
						continue;
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
				// 숫자가 작을 수록 최근에 공격한 것
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
							return 1;
						else
							return -1;
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

		// 가장 약한 포탑과 가장 강한 포탑을 구하기 위한 리스트
		cannonSorted = new ArrayList<>();
		// 실제 인덱스가 들어있는 리스트
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

		breakK: for (int k = 0; k < K; k++) {

			// 가장 약한 포탑과 강한 포탑을 위한 정렬
			Collections.sort(cannonSorted);

			// 가장 약한 포탑 고르기
			int weekIndex = cannonSorted.get(0).idx;
//			System.out.println(cannonSorted);
//			System.out.println("정렬된 캐논 리스트");
//			for(Cannon c: cannonSorted) {
//				System.out.println(c);
//			}

			// 가장 약한 포탑에 atk 올려주기
			cannonsIndex.get(weekIndex).addAtk(N + M);
//			System.out.println("약한 포탑 atk 올려주기");
//			System.out.println("Ack 맵");
//			printMapValue();
//			System.out.println("인덱스 맵");
//			printMap();

			// 가장 강한 포탑 고르기
			int strongIndex = cannonSorted.get(cannonSorted.size() - 1).idx;

			// 가장 약한 포탑이, 가장 강한 포탑 공격하기
			// 공격하기 전 visited 배열 초기화
			minMap = new boolean[N + 2][M + 2];
			visited = new boolean[N + 2][M + 2];

//			System.out.println("공격 " + weekIndex + " -> " + strongIndex);
			cannonsIndex.get(weekIndex).attack(cannonsIndex.get(strongIndex));

//			System.out.println("Ack 맵");
//			printMapValue();
			// 정비
			for (int i = 0; i < cannonsIndex.size(); i++) {
				Cannon cannon = cannonsIndex.get(i);

				// 전에 공격을 안했으면 공격 시간 + 1
//				System.out.println("Cannon " + cannon.r + ", " + cannon.c);
//				System.out.println(cannon.idx + " ");
				cannon.setWasAtk(cannon.getWasAtk() + 1);

				// 3. 포탑이 부셔짐
				if (cannon.getAtk() <= 0) {
					// 죽은 포탑처리
					map[cannon.r][cannon.c] = 0;
					cannon.setAlive(false);

					cannonSorted.remove(cannon);

					// 홀로 남게 된다면
					if (cannonSorted.size() == 1)
						break breakK;

				}

				if (!cannon.alive)
					continue;

				// 한번도 건들지 않았으면 수리
				if (!minMap[cannon.r][cannon.c])
					cannon.addAtk(1);

			}

			cannonsIndex.get(weekIndex).setWasAtk(0);

//			System.out.println("인덱스 맵");
//			printMap();
		}

		int max = Integer.MIN_VALUE;
		for (Cannon c : cannonsIndex) {
			if (!c.alive)
				continue;
			if (max < c.atk)
				max = c.atk;
		}
		sb.append(max);
		System.out.println(sb);

	}

	public static void printMap() {
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= M; j++) {
				System.out.print(map[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void printMapValue() {
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= M; j++) {
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