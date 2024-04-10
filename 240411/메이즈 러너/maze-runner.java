/*
 * 
 * 벽 내구도 1~9
 * 움직일 수 있는 칸이 2개 이상이면 상하로 움직이는것을 우선시
 * 움직일 수 없으면 가만히 있기.
 * 한칸에 2명 이상의 참가자가 있을 수 있다.
 * 
 * 한명 이상의 참가자와 출구를 포함한 가장 작은 정사각형
 * => 최소 한명과 출구를 꼭짓점으로하는 가장 작은 정사각형을 찾는게 관건.
 *    모든 참가자들을 출구까지의 정사각형 크기를 먼저 구하고 하자.
 * 
 * 
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class Main {

	static class Person {
		int idx, r, c, dis;
		boolean escape = false;

		public Person(int idx, int r, int c) {
			super();
			this.idx = idx + flagBit;
			this.r = r;
			this.c = c;
			this.dis = 0;

			map[r][c] = idx + flagBit;
		}

		public boolean move() {
			// 어디로 향할지 결정
			int minD = -1;
			int minValue = calD(Er, Ec, r, c);
			for (int d = 0; d < 4; d++) {
				int nr = r + dy[d];
				int nc = c + dx[d];

				if (Er == nr && Ec == nc) {
					// 사람이 도착지점에 들어오면
					// 탈출
					escape = true;
					dis++;

					// 맵에서 지우기
					idx -= flagBit; // 잠깐 플래그 비트 빼기
					map[r][c] ^= idx; // idx 추출
					if (map[r][c] == flagBit)
						map[r][c] = 0;

					return true;
				}

				// 경계 밖 체크
				if (nr <= 0 || nc <= 0 || nc > N || nr > N)
					continue;

				// 벽인지 체크
				if (map[nr][nc] >= 1 && map[nr][nc] <= 9)
					continue;

				// 다음갈 지점과 출구까지의 거리
				int cal = calD(Er, Ec, nr, nc);
				// 가장 짧은 길을 선택
				// 우선순위는 상하
				if (cal < minValue) {
					minD = d;
					minValue = cal;
				}

			}

			// 만약 갈곳이 없다면 함수 나오기
			if (minD == -1)
				return false;

			// 이동
			int nr = r + dy[minD];
			int nc = c + dx[minD];

			// 가는 곳이 빈칸이면
			if (map[nr][nc] == 0) {
				map[r][c] = 0;
				map[nr][nc] = idx;
			}
			// 가는 곳에 사람이 한명이라도 있다면?
			else {
				idx -= flagBit; // 잠깐 플래그 비트 빼기
				map[r][c] ^= idx; // idx 추출
				if (map[r][c] == flagBit)
					map[r][c] = 0;
				map[r + dy[minD]][c + dx[minD]] |= idx; // idx 넣기
				idx += flagBit; // 다시 플래그 비트 넣기
			}

			r = nr;
			c = nc;
			dis++;

			return false;
		}
	}

	static int map[][];
	static int N, M, K, Er, Ec;
	static Person[] people;
	static int[] dx = { 0, 0, -1, 1 };
	static int[] dy = { -1, 1, 0, 0 };
	static final int flagBit = 1024;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());

		map = new int[N + 2][N + 2];

		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		people = new Person[M + 1];

		int r, c;
		for (int i = 0; i < M; i++) {
			st = new StringTokenizer(br.readLine());
			r = Integer.parseInt(st.nextToken());
			c = Integer.parseInt(st.nextToken());
			people[i + 1] = new Person(1 << i, r, c);
		}
		st = new StringTokenizer(br.readLine());
		Er = Integer.parseInt(st.nextToken());
		Ec = Integer.parseInt(st.nextToken());
		map[Er][Ec] = -1; // 출구 표시
//		System.out.println("시작");
//		printMap();

		int escape = 0;
		for (int k = 0; k < K; k++) {
			// 사람 이동
			for (int p = 1; p <= M; p++) {
				if (people[p].escape)
					continue;

				if (people[p].move())
					escape++;
			}
			// 모든 사람이 탈출하면 게임 끝
			if (escape == M)
				break;
//			System.out.println("사람 이동");
//			printMap();

			// 배열 돌리기
			// 1. 출구에서 가장 가까운 사람 찾기
			int target = findPerson();
//			System.out.println(target);

			// 2. 출구와 사람의 r과 c를 비교하여 더 큰것을 정사각형의 변의 길이로 선택
			int edge = Math.max(Math.abs(Er - people[target].r), Math.abs(Ec - people[target].c));

			// 3. 출구를 기준으로 예비점을 왼쪽 위로 정사각형의 길이 만큼 사각형을 만든다.
			int[] vertex = findVertex(people[target], edge);

//			System.out.println(Arrays.toString(vertex));

			// 4. 해당 좌표에서 배열 돌리기를 실행
			spin(vertex, edge);

//			System.out.println("돌리기 : " + Arrays.toString(vertex) + ", edge : " + edge);
//			printMap();
		}

		int sum = 0;
		for (int i = 1; i <= M; i++) {
			sum += people[i].dis;
		}
		sb.append(sum).append("\n");
		sb.append(Er).append(" ").append(Ec);
		System.out.println(sb);

	}

	public static void spin(int[] vertex, int edge) {
		int r = vertex[0];
		int c = vertex[1];

		int[][] tmp = new int[edge + 1][edge + 1];

		for (int i = r; i <= r + edge; i++) {
			for (int j = c; j <= c + edge; j++) {
				// 벽이면 내구도 하나 깍기
				if (1 <= map[i][j] && map[i][j] <= 9)
					map[i][j]--;
				tmp[(j - c)][edge - (i - r)] = map[i][j];

				// 사람이면 좌표 변경
				if (map[i][j] > flagBit) {
					for (int k = 0; k < M; k++) {
						// 좌표 안에 해당 k에 해당되는 사람이 있다면?
						if ((map[i][j] - flagBit & (1 << k)) != 0) {
							people[k + 1].r = (j - c) + r;
							people[k + 1].c = edge - (i - r) + c;
						}
					}
				}

				// 출구면 좌표 변경
				if (map[i][j] == -1) {
					Er = (j - c) + r;
					Ec = edge - (i - r) + c;
				}

			}
		}

		// 원 map에 붙여 넣기
		for (int i = 0; i < edge + 1; i++) {
			System.arraycopy(tmp[i], 0, map[r + i], c, edge + 1);
		}

		// 확인용
//		printMap();
//		for (int i = 0; i < tmp.length; i++) {
//			for (int j = 0; j < tmp[0].length; j++) {
//				System.out.print(tmp[i][j] + "\t");
//			}
//			System.out.println();
//		}
	}

	public static int[] findVertex(Person p, int edge) {

		int[] cur = new int[] { Er - edge, Ec - edge };
		for (int i = cur[0]; i <= cur[0] + edge; i++) {
			for (int j = cur[1]; j <= cur[1] + edge; j++) {
				// 범위를 벗어난다면 제외
				if (i <= 0 || j <= 0 || i > N || j > N)
					continue;

				// 왼쪽 임시점을 기준으로 사각형 안에 타겟이 있는지?
				if ((i <= p.r && p.r <= i + edge) && (j <= p.c && p.c <= j + edge)) {
					// 있으면 그 임시점 좌표를 반환
					return new int[] { i, j };
				}
			}
		}

		return null;
	}

	public static int findPerson() {
		PriorityQueue<int[]> pq = new PriorityQueue<>(new Comparator<int[]>() {

			@Override
			public int compare(int[] o1, int[] o2) {
				if (o1[1] > o2[1]) {
					return 1;
				} else if (o1[1] < o2[1])
					return -1;
				else {
					if (o1[2] > o2[2])
						return 1;
					else
						return -1;
				}
			}
		});

		for (int i = 1; i <= M; i++) {
			// 탈출 안 한 사람 중에서
			if (!people[i].escape) {
				pq.offer(new int[] { i, people[i].r, people[i].c });
			}
		}

		return pq.poll()[0];
	}

	public static int calD(int x1, int y1, int x2, int y2) {
		return Math.abs(x1 - x2) + Math.abs(y1 - y2);
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
}