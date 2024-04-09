import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

public class Main {

	static class Knight {
		int idx, r, c, h, w, k, hp;

		public Knight(int idx, int r, int c, int h, int w, int k) {
			super();
			this.idx = idx;
			this.r = r;
			this.c = c;
			this.h = h;
			this.w = w;
			this.k = k;
			this.hp = k;

			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					knightMap[i][j] = idx;
				}
			}
		}

		public void Right() {
			int nc = c + w;

			move[idx] = true;

			// 범위 밖
			if (nc > L) {
				check = false;
				return;
			}

			boolean flag = true;
			for (int i = r; i < r + h; i++) {
				if (map[i][nc] == 2) {
					check = false;
					return ;
				}
				else if (knightMap[i][nc] != 0)
					knights[knightMap[i][nc]].Right();
			}
		}

		public boolean moveRight() {
			// 이동 프로세스
			for (int i = r + h - 1; i >= r; i--) {
				for (int j = c + w - 1; j >= c; j--) {
					// 기사 이동
					// 오른쪽으로 밀기
					knightTmp[i][j + 1] = knightMap[i][j];
					if (map[i][j + 1] == 1)
						k--;
				}
			}
			c = c + 1;
			return k <= 0 ? true : false;
		}

		public void Left() {
			int nc = c - 1;

			move[idx] = true;

			// 범위 밖
			if (nc <= 0) {
				check = false;
				return;
			}
			
			for (int i = r; i < r + h; i++) {
				if (map[i][nc] == 2) {
					check = false;
					return;
				}
				else if (knightMap[i][nc] != 0) {
					knights[knightMap[i][nc]].Left();
				}
			}
		}

		public boolean moveLeft() {

			// 이동 프로세스
			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					// 기사 이동
					// 왼쪽으로 밀기
					knightTmp[i][j - 1] = knightMap[i][j];
					if (map[i][j - 1] == 1)
						k--;
				}
			}
			c = c - 1;
			return k <= 0 ? true : false;
		}

		public void Down() {
			int nr = r + h;

			move[idx] = true;

			// 범위 밖
			if (nr > L) {
				check = false;
				return;
			}

			boolean flag = true;
			for (int i = c; i < c + w; i++) {
				if (map[nr][i] == 2) {
					check = false;
					return;
				}
				else if (knightMap[nr][i] != 0)
					knights[knightMap[nr][i]].Down();
			}
		}

		public boolean moveDown() {

			// 이동 프로세스
			for (int i = r + h - 1; i >= r; i--) {
				for (int j = c + w - 1; j >= c; j--) {
					// 기사 이동
					// 아래로 밀기
					knightTmp[i + 1][j] = knightMap[i][j];
					if (map[i + 1][j] == 1)
						k--;
				}
			}
			r = r + 1;
			return k <= 0 ? true : false;
		}

		public void Up() {
			int nr = r - 1;

			move[idx] = true;

			// 범위 밖
			if (nr <= 0) {
				check = false;
				return;
			}

			boolean flag = true;
			for (int i = c; i < c + w; i++) {
				if (map[nr][i] == 2) {
					check = false;
					return;
				}
				else if (knightMap[nr][i] != 0)
					knights[knightMap[nr][i]].Up();
			}
		}

		public boolean moveUp() {
			// 이동 프로세스
			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					// 기사 이동
					// 위로 밀기
					knightTmp[i - 1][j] = knightMap[i][j];
					if (map[i - 1][j] == 1)
						k--;
				}
			}
			r = r - 1;
			return k <= 0 ? true : false;
		}

		public void stay() {
			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					knightTmp[i][j] = idx;
				}
			}
		}

		public void Dead() {
			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					knightTmp[i][j] = 0;
				}
			}
			idx = 0;
		}

	}

	static int[][] map, knightMap, knightTmp, initMap;
	static int L, N, Q;
	static Knight[] knights;
	static boolean[] move, init;
	static boolean check;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());

		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());

		map = new int[L + 2][L + 2];
		knightMap = new int[L + 2][L + 2];
		knightTmp = new int[L + 2][L + 2];
		initMap = new int[L + 2][L + 2];

		for (int i = 0; i < L + 2; i++) {
			Arrays.fill(map[i], 2);
		}

		knights = new Knight[N + 1];
		move = new boolean[N + 1];
		init = new boolean[N + 1];

		for (int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		int r, c, h, w, k;
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			r = Integer.parseInt(st.nextToken());
			c = Integer.parseInt(st.nextToken());
			h = Integer.parseInt(st.nextToken());
			w = Integer.parseInt(st.nextToken());
			k = Integer.parseInt(st.nextToken());
			knights[i] = new Knight(i, r, c, h, w, k);
		}

//		System.out.println("맵");
//		printMap(map);
//		System.out.println("기사");
//		printMap(knightMap);

		int knight;
		int d;
		int hp;
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());
			knight = Integer.parseInt(st.nextToken());
			d = Integer.parseInt(st.nextToken());
			
			if(knights[knight].k == 0)
				continue;

			check = true;
			
			switch (d) {
			case 0: {
				knights[knight].Up();
				if (check) {
					hp = knights[knight].k;
					for (int j = 1; j <= N; j++) {
						if (!move[j])
							continue;
						if (knights[j].moveUp() && j != knight)
							knights[j].Dead();
					}
					knights[knight].k = hp;
				} else {
					for (int j = 1; j <= N; j++) {
						if (move[j])
							knights[j].stay();
					}
				}
				break;
			}
			case 1: {
				knights[knight].Right();
				if (check) {
					hp = knights[knight].k;
					for (int j = 1; j <= N; j++) {
						if (!move[j])
							continue;
						if (knights[j].moveRight() && j != knight)
							knights[j].Dead();
					}
					knights[knight].k = hp;

				} else {
					for (int j = 1; j <= N; j++) {
						if (move[j])
							knights[j].stay();
					}
				}

				break;
			}
			case 2: {
				knights[knight].Down();
				if (check) {
					hp = knights[knight].k;
					for (int j = 1; j <= N; j++) {
						if (!move[j])
							continue;
						if (knights[j].moveDown() && j != knight)
							knights[j].Dead();
					}
					knights[knight].k = hp;

				} else {
					for (int j = 1; j <= N; j++) {
						if (move[j])
							knights[j].stay();
					}
				}

				break;
			}
			case 3: {
				knights[knight].Left();
				if (check) {
					hp = knights[knight].k;
					for (int j = 1; j <= N; j++) {
						if (!move[j])
							continue;
						if (knights[j].moveLeft() && j != knight)
							knights[j].Dead();
					}
					knights[knight].k = hp;

				} else {
					for (int j = 1; j <= N; j++) {
						if (move[j])
							knights[j].stay();
					}
				}

				break;
			}
			}

			// 가만히 있던 애들도 제자리 지키게 맵 업데이트
			for (int j = 1; j <= N; j++) {
				if (!move[j]) {
					knights[j].stay();
				}
			}
			copyMap();
			System.arraycopy(init, 0, move, 0, N + 1);

//			System.out.println(knight + ", " + d);
//			System.out.println("맵");
//			printMap(map);
//			System.out.println("기사");
//			printMap(knightMap);
//			System.out.println("기사 피");
//			for (int j = 1; j <= N; j++) {
//				System.out.print(j + " : (" + knights[j].k + "/" + knights[j].hp +") , ");
//			}
//			System.out.println();

			initMap();
		}

		int sum = 0;
		for (int i = 1; i <= N; i++) {
			if (knights[i].k > 0)
				sum += knights[i].hp - knights[i].k;
		}

		sb.append(sum);
		System.out.println(sb);

	}

	static void printMap(int map[][]) {
//		System.out.println();
		for (int i = 0; i <= L + 1; i++) {
			for (int j = 0; j <= L + 1; j++) {
				System.out.print(map[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	static void copyMap() {
		for (int i = 0; i <= L + 1; i++) {
			System.arraycopy(knightTmp[i], 0, knightMap[i], 0, L + 2);
		}
	}

	static void initMap() {
		for (int i = 0; i <= L + 1; i++) {
			System.arraycopy(initMap[i], 0, knightTmp[i], 0, L + 2);
		}
	}

}