import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.StringTokenizer;

/* 
 * 왕실의 기사 대결
 * 
 * L x L크기의 체스판위이며, 왼쪽 상단은 1,1
 * 빈칸 0, 함정 1, 벽 2
 * 기사들은 좌측 상단을 (r,c)로 h x w 크기의 직사각형 형태를 띄고 있다.
 * 각 기사의 체력은 k로 주어진다.
 * 
 * 1. 기사이동
 * 상하좌우 하나로 한 칸 이동 가능
 * 이동하는 위치에 기사가 있다면, 그 기사도 함께 밀린다.
 * 그 옆에 또 기사가 있다면 연쇄적으로 한 칸씩 밀리게 된다.
 * 이동하려는 방향의 끝에 벽이 있따면, 모든 기사는 이동할 수 없다.
 * 또, 사라진 기사에게 명령을 내리면 아무런 반응이 없다.
 * 
 * 2.대결 대미지
 * 기사가 다른 기사를 밀치게 되면, 밀려난 기사들은 피해를 입게 된다. 이때 각 기사들은 해당 기사가 이동한 곳에서
 * w x h 직사각형 내에 놓여 있는 함정의 수만큼 피해를 입는다.
 * 각 기사마다 피해를 받은 만큼 체력이 깍이게 되면, 현재 체력 이상의 대미지를 받을 경우 체스판에서 사라진다.
 * 단 명령을 받은 기사는 피해를 입지 않으며, 기사들은 모두 밀린 이후에 대미지를 받는다.
 * 
 * @입력
 * L : 체스판 크기
 * 0 빈칸, 1 함정, 2 벽
 * 
 * 다음 N개의 줄에 걸쳐서 초기 기사들의 정보가 주어지고, (r,c,h,w,k)순으로 주어진다.
 * 1번부터 N번기사까지 순서대로 정보가 주어진다.
 * 기사끼리 겹치지 않으며 또한 기사와 벽은 겹쳐서 주어지지 않는다.
 * 
 * Q개의 명령어가 주어지며, (i,d)는 i번 기사에게 방향 d로 한칸 이동하라는 명령임을 뜻한다.
 * i는 1이상 N이하의 값을 갖으며, 이미 사라진 기사 번호가 주어질 수도 있따.
 * d는 0,1,2,3 중에 하나이며, 위쪽, 오른쪽, 아래쪽, 왼쪽방향을 의미.
 * 
 * @출력
 * 생존한 기사들이 총 받은 대미지의 합을 출력
 * 
 * @해결방안
 * 
 * 맵의 패딩은 1을 주며, 가장자리는 모두 벽으로 지정한다.
 * 
 * 1. 기사의 이동 조건.
 * 먼저 밀리는지 확인을 한다.
 * d방향으로 계속 들어가며,
 * 	1-1 : 벽을 만난다면,
 * 			전체 이동을 하지 않는다.
 *  1-2 : 벽을 만나지 않는다면,
 *  		* d방향에 다른 기사가 있는지 확인.
 *  		* 다른 기사가 있다면 그 기사의 d방향을 확인한다.
 *  		* 만약 허공 밖에 없다면 전체 이동을 준비한다.
 *  
 * 2. 전체이동
 * 제일 끝 번호부터 차례대로 밀린다.
 * 이를 위해 큐을 활용하여, 제일 마지막 기사를 먼저 꺼내서 이동을 하자.
 * 
 * 
 */

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
			hp = k;

			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					knightMap[i][j] = idx;
				}
			}
		}

		// 오른쪽 확인
		public void checkRight() {
			// 단 한번이라도 check가 false가 나왔으면 그냥 하지 않기
			if (!check)
				return;

			// 현재 기사의 오른쪽 밖
			int nc = c + w;

			// 범위 밖 체크
			if (nc > L) {
				check = false;
				return;
			}

			// 오른쪽이 있으니, 세로 길이만큼 오른쪽 확인
			for (int i = r; i < r + h; i++) {
				// 오른쪽에 벽이 있다면,
				if (map[i][nc] == 2) {
					check = false;
					return;
				}
				// 오른쪽에 다른 기사가 있다면,
				// 재귀적으로 해당 기사에 들어가서 확인
				else if (knightMap[i][nc] != 0) {
					knights[knightMap[i][nc]].checkRight();
				}
			}

			// 큐에 넣기, 추후 역순으로 기사 옮길 예정
			queue.offer(idx);

		}

		// 오른쪽으로 밀기
		public void mvRight() {
			for (int i = r + h - 1; i >= r; i--) {
				for (int j = c + w - 1; j >= c; j--) {
					if (map[i][j + 1] == 1 && queue.size() != 1)
						k--;
					knightMap[i][j + 1] = knightMap[i][j];
					knightMap[i][j] = 0;
				}
			}
			
			//좌표 오른쪽으로 밀기
			c++;
			
			if(k==0)
				Dead();
		}

		// 왼쪽 확인
		public void checkLeft() {
			// 단 한번이라도 check가 false가 나왔으면 그냥 하지 않기
			if (!check)
				return;

			// 현재 기사의 왼쪽 밖
			int nc = c - 1;

			// 범위 밖 체크
			if (nc <= 0) {
				check = false;
				return;
			}

			// 왼쪽이 있으니, 세로 길이만큼 왼쪽 확인
			for (int i = r; i < r + h; i++) {
				// 왼쪽에 벽이 있다면,
				if (map[i][nc] == 2) {
					check = false;
					return;
				}
				// 왼쪽에 다른 기사가 있다면,
				// 재귀적으로 해당 기사에 들어가서 확인
				else if (knightMap[i][nc] != 0) {
					knights[knightMap[i][nc]].checkLeft();
				}
			}

			// 큐에 넣기, 추후 역순으로 기사 옮길 예정
			queue.offer(idx);
		}

		// 왼쪽으로 밀기
		public void mvLeft() {
			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					if (map[i][j - 1] == 1  && queue.size() != 1)
						k--;
					knightMap[i][j - 1] = knightMap[i][j];
					knightMap[i][j] = 0;
				}
			}
			
			//좌표 왼쪽으로 밀기
			c--;
			
			if(k==0)
				Dead();
		}

		// 아래쪽 확인
		public void checkDown() {
			// 단 한번이라도 check가 false가 나왔으면 그냥 하지 않기
			if (!check)
				return;

			// 현재 기사의 아래쪽 밖
			int nr = r + h;

			// 범위 밖 체크
			if (nr > L) {
				check = false;
				return;
			}

			// 아래쪽이 있으니, 가로 길이만큼 아래쪽 확인
			for (int i = c; i < c + w; i++) {
				// 아래쪽에 벽이 있다면,
				if (map[nr][i] == 2) {
					check = false;
					return;
				}
				// 아래쪽에 다른 기사가 있다면,
				// 재귀적으로 해당 기사에 들어가서 확인
				else if (knightMap[nr][i] != 0) {
					knights[knightMap[nr][i]].checkDown();
				}
			}

			// 큐에 넣기, 추후 역순으로 기사 옮길 예정
			queue.offer(idx);
		}

		// 아래쪽으로 밀기
		public void mvDown() {
			for (int i = r + h - 1; i >= r; i--) {
				for (int j = c + w - 1; j >= c; j--) {
					if (map[i + 1][j] == 1 && queue.size() != 1)
						k--;
					knightMap[i + 1][j] = knightMap[i][j];
					knightMap[i][j] = 0;
				}
			}
			
			//좌표 아래쪽으로 밀기
			r++;
			
			if(k==0)
				Dead();
		}

		// 위쪽 확인
		public void checkUp() {
			// 단 한번이라도 check가 false가 나왔으면 그냥 하지 않기
			if (!check)
				return;

			// 현재 기사의 위쪽 밖
			int nr = r - 1;

			// 범위 밖 체크
			if (nr <= 0) {
				check = false;
				return;
			}

			// 위쪽이 있으니, 가로 길이만큼 위쪽 확인
			for (int i = c; i < c + w; i++) {
				// 위쪽에 벽이 있다면,
				if (map[nr][i] == 2) {
					check = false;
					return;
				}
				// 위쪽에 다른 기사가 있다면,
				// 재귀적으로 해당 기사에 들어가서 확인
				else if (knightMap[nr][i] != 0) {
					knights[knightMap[nr][i]].checkUp();
				}
			}

			// 큐에 넣기, 추후 역순으로 기사 옮길 예정
			queue.offer(idx);
		}

		// 위쪽으로 밀기
		public void mvUp() {
			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					if (map[i - 1][j] == 1 && queue.size() != 1)
						k--;
					knightMap[i - 1][j] = knightMap[i][j];
					knightMap[i][j] = 0;
				}
			}
			
			//좌표 위쪽으로 밀기
			r--;
			
			if(k==0)
				Dead();
		}
		
		public void Dead() {
			for (int i = r; i < r + h; i++) {
				for (int j = c; j < c + w; j++) {
					knightMap[i][j] = 0;
				}
			}
		}
	}

	static int L, N, Q;
	static int[][] map, knightMap;
	static Knight[] knights;
	static Deque<Integer> queue;
	static boolean check;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());

		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());

		map = new int[L + 2][L + 2]; // padding 1
		knightMap = new int[L + 2][L + 2]; // padding 1

		queue = new ArrayDeque<>();
		knights = new Knight[N + 1];

		for (int i = 0; i < L + 2; i++) {
			Arrays.fill(map[i], 2);
		}

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

		int knight, d;
		for (int i = 0; i < Q; i++) {
			st = new StringTokenizer(br.readLine());

			knight = Integer.parseInt(st.nextToken());
			d = Integer.parseInt(st.nextToken());

			check = true;
			queue.clear();
			switch (d) {
			case 0: {
				// 위
				knights[knight].checkUp();
				if (check) {
					while (!queue.isEmpty())
						knights[queue.poll()].mvUp();
				}
				break;
			}
			case 1: {
				// 오른쪽
				knights[knight].checkRight();
				if (check) {
					while (!queue.isEmpty())
						knights[queue.poll()].mvRight();
				}
				break;
			}
			case 2: {
				// 아래
				knights[knight].checkDown();
				if (check) {
					while (!queue.isEmpty())
						knights[queue.poll()].mvDown();
				}
				break;
			}
			case 3: {
				// 왼쪽
				knights[knight].checkLeft();
				if (check) {
					while (!queue.isEmpty())
						knights[queue.poll()].mvLeft();
				}
				break;
			}
			}

//			System.out.println("맵");
//			printMap(map);
//			System.out.println("기사");
//			printMap(knightMap);
			
		}

		int sum = 0;
		for (int i = 1; i <= N; i++) {
			if (knights[i].k != 0)
				sum += knights[i].hp - knights[i].k;
		}
		sb.append(sum);
		System.out.println(sb);
	}
	
	public static void printMap(int map[][]) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				System.out.print(map[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
}