import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

/*
 * 루돌프의 반란
 * 
 * 메모리 
 * 시간 
 * 
 * 1번부터 P번까지 P명의 산타가 있다.
 * 
 * 1. 게임판의 구성
 * N x N
 * 각 위치는 (r,c)의 형태로 표현되며, 좌상단(1,1)
 * 
 * 게임은 총 M개의 턴에 걸쳐 진행되며, 매 턴마다 루돌프와 산타들이 한번씩 움직인다.
 * 루돌프가 한번 움직인 뒤, 1번 산타부터 P번 산타까지 순서대로 움직인다.
 * 이때, 기절해 있거나 격자 밖으로 빠져나가 게임에서 탈락한 산타들은 움직일 수 없다.
 * 
 * 게임판에서 두칸(r1, c1),(r2,c2) 사이의 거리는 (r1 - r2)^2 + (c1-c2)^2으로 계산된다.
 * 
 * 2. 루돌프의 움직임
 * 루돌프는 가장 가까운 산타를 향해 1칸 돌진한다. => 게임에서 탈락하지 않은 산타 중 가장 가까운 산타
 * 만약 가장 가까운 산타가 2명 이상이라면, r 좌표가 큰 산타를 향해 돌진 한다. r이 동일한 경우, c좌표가 큰 산타를
 * 향해 돌진한다. => 우선순위큐를 사용해야할 지도..?
 * 
 * 루돌프는 상하좌우, 대각석, 8방향 중 하나로 돌진 가능하다.(편의상 대각선도 1칸 전진이라고 생각)
 * 가장 우선순위가 높은 산타를 향해 8방향 중 가장 가까워지는 방향으로 한 칸 돌진한다.
 * 
 * 3. 산타의 움직임
 * 산타는 1~P까지 순서대로 움직인다.
 * 기절해있거나 이미 게임에서 탈락한 산타는 움직일 수 없다.
 * 산타는 루돌프에게 거리가 가장 가까워지는 방향으로 1칸 이동한다.
 * 산타는 다른 산타가 있는 칸이나 게임판 밖으로 움직일 수 없다.
 * 움직일 수 있는 칸이 없다면 산타는 움직이지 않는다.
 * 움직일 수 있는 칸이 있더라도 만약 루돌프로부터 가까워질 수 있는 방법이 없다면 안 움직인다.
 * 산타는 상하좌우 인접한 4방향으로 움직일 수 있다. 우선순위는 상우하좌 이다.
 * 
 * 4. 충돌
 * * 산타와 루돌프가 같은 칸에 있게 되면 충돌이 발생
 * * 루돌프가 움직여서 충돌이 일어난 경우, 해당 산타는 C만큼의 점수를 얻게 된다. 이와 동시에 산타는 루돌프가 
 *   이동해온 방향으로 C칸 만큼 밀려나게 된다.
 * * 산타가 움직여서 충돌이 일어난 경우, 해당 산타는 D만큼의 점수를 얻게 된다. 이와 동시에 산타는 자신이 이동해온
 *   반대 방향으로 D칸 만큼 밀려나게 됩니다.
 * * 밀려날떄 충돌이 일어나지 않고, 정확히 원하는 위치에 도달한다.
 * * 만약 밀려난 위치가 게임판 밖이라면 산타는 게임에서 탈락한다.
 * * 만약 밀려난 칸에 다른 산타가 있는 경우 상호작용이 발생한다.
 * 
 * 5. 상호작용
 * * 루돌프와의 충돌 후 산타는 포물선의 궤적으로 이동하여 착지하게 되는 칸에서만 상호작용이 발생한다.
 * * 산타는 충돌 후 찾기하게 되는 칸에 다른 산타가 있다면 그 산타는 1칸 해당 방향으로 밀린다.
 *   그 옆에 산타가 있다면 연쇄적으로 1칸씩 밀려나는 것을 반복하게 된다.
 *   이 때도 게임판 밖으로 밀려나오게 된 산타의 경우 게임에서 탈락된다.
 *   
 * 6. 기절
 * * 산타는 루돌프와 충돌 후 기절을 하게 된다. 현재 k번째 턴이었다면, (k+1)번쨰 턴까지 기절하게 되어(k+2)번째
 *   턴부터 다시 정상상태가 된다.
 * * 기절한 산타는 움직일 수 없게 된다. 단, 기절한 도중 충돌이나 상호작용으로 인해 밀려날 수는 있다.
 * * 루돌프는 기절한 산타를 돌진 대상으로 선택가능
 * 
 * 7. 게임종료
 * * M번의 턴에 걸쳐 루돌프, 산타가 순서대로 움직이 이후 게임이 종료된다.
 * * 만약 P명의 산타가 모두 게임에서 탈락하게 되면 그 즉시 게임이 종료된다.
 * * 매 턴 이후 아직 탈락하지 않은 산타들에게는 1점씩을 추가로 부여한다.
 * 
 * 게임이 끝났을 떄 각 산타가 얻은 최종 점수를 구하는 프로그램
 * 
 * @입력
 * 첫 번쨰 줄에 N,M,P,C,D가 공백을 사이에 두고 주어진다.
 * 다음 줄에는 루돌프의 초기 위치 (r,c)가 주어진다.
 * 다음 P개의 줄에 산타의 번호 P_n과 초기 위치 (r,c)가 공백을 사이에 두고 주어진다.
 * 
 * @출력
 * 게임이 끝났을 떄, 각 산타가 얻은 최종 점수 1번부터 P번까지 순서대로 공백을 사이에 두고 출력한다.
 * 
 * @해결방안
 * 산타의 state는 정상 0, 기절 1, 게임오버 2로 지정.
 * 기절은 k번째에 기절을 당하면 k + 1까지는 가만히, k+2때 움직일 수 있어야한다.
 * 기절과 탈락은 움직일 수 없다.
 * 
 * 루돌프와 상호작용,
 * 1. 루돌프 박치기
 * 		* 루돌프파워(C)만큼 루돌프가 이동해온 방향으로 밀린다.
 * 2. 루돌프를 터치
 * 		* 산타파워(D)만큼 산타가 이동해온 반대 방향으로 밀린다.
 * 
 * 밀렸을 떄,
 * 밀린 지점에 다른 산타가 있다면?
 * 밀린 방향으로 원래 있던 산타를 1칸 민다.
 * 밀렸는데 또 다른 산타가 있다면?
 * 그 산타도 1칸밀린다.
 * 이 떄, 밖으로 밀리면 탈락.
 * 
 * 밀쳐지는 것은 마지막 산타부터 이동 되고, 그 다음 산타가 되고 마지막에 루돌프가 이동하게끔
 * 큐로 구현해서 이동시키자.
 * 
 * 루돌프는 8방향으로 이동 가능
 * 
 * 산타는 BFS로 루돌프과의 거리를 구해야한다. 단순거리로는 막혔을때도 적용이되어서
 * 제대로 작동 안된다.
 * 
 */

public class Main {

	static class Santa {
		int idx, r, c, state, distance, stack;
		int point;

		public Santa(int idx, int r, int c) {
			this.idx = idx;
			this.r = r;
			this.c = c;
			this.state = 0;

			map[r][c] = idx;

			point = 0;

		}

		public void crushed(boolean rudolph, boolean santa, int d, int point) {
			// 루돌프와 부딪혔기 떄문에 점수 얻기 C,D는 외부에서 결정
			this.point += point;

			// 루돌프와 부딪혔기 때문에 기절
			state = 1;
//			System.out.println(idx + " 산타 기절!");

			// 기절 스택
			stack = 1;

			// 날라가기 프로세스
			flying(rudolph, santa, d);
		}

		public void flying(boolean rudolph, boolean santa, int d) {
			int nr = -1;
			int nc = -1;
			if (rudolph) {
				nr = r + dy[d] * C;
				nc = c + dx[d] * C;
			} else if (santa) {
				nr = r + dy[d] * D;
				nc = c + dx[d] * D;
			} else {
				nr = r + dy[d];
				nc = c + dx[d];
			}
			// 맵 밖은 게임오버
			if (nr <= 0 || nc <= 0 || nr > N || nc > N) {
				state = 2;
				map[r][c] = 0;
				r = -1000;
				c = -1000;
				return;
			}

			// 날라가야가하기 떄문에 먼저 map을 비워준다.
			if (!santa)
				map[r][c] = 0;

			// 다른 산타가 있는 곳에 떨어진다면,
			if (map[nr][nc] != 0) {
				// 해당 산타도 날라가야한다.
				santas[map[nr][nc]].flying(false, false, d);
			}

			map[nr][nc] = idx;
			r = nr;
			c = nc;

		}

		public void move(int d) {
			if (d == -1) {
				// 못움직이니까 그대로 가만히
				return;
			}
			int nr = r + dy[d];
			int nc = c + dx[d];

			// 루돌프를 만났다면,
			if (nr == Rr && nc == Rc) {
				map[r][c] = 0;

				r = nr;
				c = nc;

				crushed(false, true, (d + 2) % 4, D); // 반대 방향으로 가기
			}
			// 루돌프를 만나지 않았다면 단순이동
			else {
				// 게임판 밖으로는 움직일 수 없다.
				if (nr <= 0 || nc <= 0 || nr > N || nc > N)
					return;

				// 다른 산타가 있는 칸은 못간다.
				if (map[nr][nc] != 0)
					return;

				// 자리 옮기기
				// 어차피 산타들이 없는데로 이동하기 떄문에 그냥 이동해도된다.
				map[nr][nc] = idx;
				map[r][c] = 0;
				r = nr;
				c = nc;

			}

		}

	}

	static int N, M, P, C, D, Rr, Rc, map[][];
	static int[] dx = { 0, 1, 0, -1, -1, 1, 1, -1 };
	static int[] dy = { -1, 0, 1, 0, -1, -1, 1, 1 };
	static Deque<int[]> queue;
	static boolean visited[][], init[][];
	static PriorityQueue<Santa> pq;
	static Santa[] santas;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st = null;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());

		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());

		map = new int[N + 2][N + 2];

		st = new StringTokenizer(br.readLine());
		Rr = Integer.parseInt(st.nextToken());
		Rc = Integer.parseInt(st.nextToken());

		map[Rr][Rc] = 31; // 최대 산타 수가 30이기 때문에

		// 루돌프가 노릴 산타를 구하기 위한 우선순위 큐
		pq = new PriorityQueue<>(new Comparator<Santa>() {
			@Override
			public int compare(Santa o1, Santa o2) {
				if (o1.distance > o2.distance)
					return 1;
				else if (o1.distance < o2.distance)
					return -1;
				else {
					if (o1.r > o2.r)
						return -1;
					else if (o1.r < o2.r)
						return 1;
					else {
						if (o1.c > o2.c)
							return -1;
						else
							return 1;
					}
				}
			}
		});

		santas = new Santa[P + 1];
		int Pn, Sr, Sc;
		for (int i = 1; i <= P; i++) {
			st = new StringTokenizer(br.readLine());
			Pn = Integer.parseInt(st.nextToken());
			Sr = Integer.parseInt(st.nextToken());
			Sc = Integer.parseInt(st.nextToken());
			santas[Pn] = new Santa(Pn, Sr, Sc);
		}
//		System.out.println("시작");
//		printMap();

		for (int i = 0; i < M; i++) {
			// 루돌프 이동
			Santa target = findSanta();

			int d = setDirectionRudol(target);
			int nr = Rr + dy[d];
			int nc = Rc + dx[d];

			// 산타와 충돌
			if (map[nr][nc] != 0) {
				target.crushed(true, false, d, C);
			}
			map[nr][nc] = 31;
			map[Rr][Rc] = 0;
			Rr = nr;
			Rc = nc;

//			System.out.println((i + 1) + "번 : 루돌프 이동");
//			printMap();

			// 산타 이동
			for (int p = 1; p <= P; p++) {
				// 정상 컨디션
				if (santas[p].state == 0) {
					santas[p].move(setDirectionSanta(santas[p]));
					santas[p].stack--;
				}
				// 기절
				else if (santas[p].state == 1) {
					// 스택이 다 떨어져야 움직일 수 있다.
					if (santas[p].stack == 0) {
						santas[p].state = 0;
//						System.out.println(p + " 산타 기절 풀림!");
					}
					santas[p].stack--;
				}
				// 게임 오버
				else {
					continue;
				}
//				System.out.println(p + " 산타 이동 " + santas[p].state);
//				printMap();
			}

			int cnt = 0;
			for (int j = 1; j <= P; j++) {
				if (santas[j].state == 2)
					cnt++;
				else
					santas[j].point++;
			}
//			for (int j = 1; j <= P; j++) {
//				System.out.print(j + " : " + santas[j].point + " / ");
//			}
//			System.out.println();
			// 게임 끝내기
			if (cnt == N)
				break;

		}

		for (int i = 1; i <= P; i++) {
			sb.append(santas[i].point).append(" ");
		}

		System.out.println(sb);
	}

	public static void printMap() {
		for (int i = 1; i < N + 1; i++) {
			for (int j = 1; j < N + 1; j++) {
				System.out.print(map[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}

	public static Santa findSanta() {
		pq.clear();
		for (int i = 1; i <= P; i++) {
			if (santas[i].state == 2)
				continue;
			santas[i].distance = calD(santas[i]);
			pq.offer(santas[i]);
		}
		return pq.poll();
	}

	public static int setDirectionSanta(Santa S) {
		int minD = -1;
		int minValue = (int) (Math.pow(Rr - S.r, 2) + Math.pow(Rc - S.c, 2));
		for (int d = 0; d < 4; d++) {
			int nr = S.r + dy[d];
			int nc = S.c + dx[d];

			if (nr <= 0 || nc <= 0 || nr > N || nc > N)
				continue;

			// 탐색할 곳이 빈칸이거나 루돌프면
			if (map[nr][nc] == 0 || map[nr][nc] == 31) {
				int cal = (int) (Math.pow(Rr - nr, 2) + Math.pow(Rc - nc, 2));
				if (cal < minValue) {
					minD = d;
					minValue = cal;
				}
			}

		}
		return minD;
	}

	public static int setDirectionRudol(Santa S) {
		if (Rr > S.r) {
			if (Rc > S.c)
				return 4;
			else if (Rc == S.c)
				return 0;
			else
				return 5;
		} else if (Rr == S.r) {
			if (Rc > S.c)
				return 3;
			else if (Rc < S.c)
				return 1;
			else
				return -1;
		} else {
			if (Rc > S.c)
				return 7;
			else if (Rc == S.c)
				return 2;
			else
				return 6;
		}
	}

	public static int calD(Santa s) {
		return (int) (Math.pow(Rr - s.r, 2) + Math.pow(Rc - s.c, 2));
	}

}