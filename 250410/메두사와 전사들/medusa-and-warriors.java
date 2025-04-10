import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;

public class Main {

	static class Worior {
		int r, c, move;
		boolean alive, rock, attack;

		public Worior(int r, int c) {
			this.r = r;
			this.c = c;

			this.alive = true;
			this.rock = false;
			this.attack = false;
		}

		void move() {
			// 첫 번째 움직임
			int first = bestPathCal1(this.r, this.c);
			if (first != 0) {
				move++;

				worior_map[this.r][this.c] = 0;

				this.r += dy[first];
				this.c += dx[first];

				// 만약 메두사를 만난다면 공격
				if (this.r == Sr && this.c == Sc) {
					attack = true;
                    alive = false;
					return;
				}

				worior_map[this.r][this.c] = 1;
			}

			// 두 번째 움직임
			int second = bestPathCal2(this.r, this.c);
			if (second != 0) {
				move++;

				worior_map[this.r][this.c] = 0;

				this.r += dy2[second];
				this.c += dx2[second];

				if (this.r == Sr && this.c == Sc) {
					attack = true;
                    alive = false;
					return;
				}

				worior_map[this.r][this.c] = 1;
			}
		}

		int bestPathCal1(int r, int c) {
			int m = 0;
			int min = Integer.MAX_VALUE;

			for (int d = 0; d <= 4; d++) {
				int nr = r + dy[d];
				int nc = c + dx[d];

				if (!checkRange(nr, nc))
					continue;

				// 메두사 시야에 있어도 스킵
				if (sight[nr][nc])
					continue;

				int cal = Math.abs(nr - Sr) + Math.abs(nc - Sc);

				if (min > cal) {
					m = d;
					min = cal;
				}
			}

			return m;
		}

		int bestPathCal2(int r, int c) {
			int m = 0;
			int min = Integer.MAX_VALUE;

			for (int d = 0; d <= 4; d++) {
				int nr = r + dy2[d];
				int nc = c + dx2[d];

				if (!checkRange(nr, nc))
					continue;

				// 메두사 시야에 있어도 스킵
				if (sight[nr][nc])
					continue;

				int cal = Math.abs(nr - Sr) + Math.abs(nc - Sc);

				if (min > cal) {
					m = d;
					min = cal;
				}
			}

			return m;
		}

		@Override
		public String toString() {
			return "Worior [r=" + r + ", c=" + c + ", move=" + move + ", alive=" + alive + ", rock=" + rock
					+ ", attack=" + attack + "]";
		}

	}

	static class Medussar {
		int move() {
			int m = 0;

			int min = Integer.MAX_VALUE;
			for (int md = 1; md <= 4; md++) {
				int r = Sr + dy[md];
				int c = Sc + dx[md];

				if (!checkRange(r, c))
					continue;

				if (map[r][c] == 1)
					continue;

				Deque<int[]> q = new ArrayDeque<>();
				boolean[][] visited = new boolean[N][N];
				visited[r][c] = true;
				q.offer(new int[] { r, c, 0 });

				while (!q.isEmpty()) {
					int[] cur = q.poll();

					// 출구를 찾았다면
					if (cur[0] == Er && cur[1] == Ec) {
						// 처음 시작 방향 중에 가장 적게 이동한 노드의 방향 저장
						if (min > cur[2]) {
							m = md;
							min = cur[2];
						}
						break;
					}

					for (int d = 1; d <= 4; d++) {
						int nr = cur[0] + dy[d];
						int nc = cur[1] + dx[d];

						// 범위 체크
						if (!checkRange(nr, nc))
							continue;

						// 도로 밖 체크
						if (map[nr][nc] == 1)
							continue;

						if (visited[nr][nc])
							continue;
						visited[nr][nc] = true;

						q.offer(new int[] { nr, nc, cur[2] + 1 });
					}
				}

			} // end-of-for md

			Sr += dy[m];
			Sc += dx[m];

//			System.out.println("메두사 이동 : " + Sr + " " + Sc);
			
			// 전사를 만났다면,
			if (worior_map[Sr][Sc] == 1) {
				for (Worior w : Woriors) {
					if (!w.alive)
						continue;
					if(w.r == Sr && w.c == Sc) {
						w.alive = false;
						worior_map[Sr][Sc] = 0;
					}
				}
			}

			return m; // 갈곳이 없다면 0을 반환
		}

		void watch() {
			// 먼저 어딜 볼지 정하기
			int m = 0;
			int max = Integer.MIN_VALUE;
			int cnt;
			for (int md = 1; md <= 4; md++) {
				if (!checkRange(Sr + dy[md], Sc + dx[md]))
					continue;

				cnt = 0;
				Deque<int[]> q = new ArrayDeque<>();
				boolean[][] visited = new boolean[N][N];
				boolean[][] w_visited = new boolean[N][N];
				if (md == 1) {
					q.offer(new int[] { Sr + dy[up[0]], Sc + dx[up[0]], up[0], md });
					q.offer(new int[] { Sr + dy[up[1]], Sc + dx[up[1]], up[1], md });
					q.offer(new int[] { Sr + dy[up[2]], Sc + dx[up[2]], up[2], md });
				} else if (md == 2) {
					q.offer(new int[] { Sr + dy[down[0]], Sc + dx[down[0]], down[0], md });
					q.offer(new int[] { Sr + dy[down[1]], Sc + dx[down[1]], down[1], md });
					q.offer(new int[] { Sr + dy[down[2]], Sc + dx[down[2]], down[2], md });
				} else if (md == 3) {
					q.offer(new int[] { Sr + dy[left[0]], Sc + dx[left[0]], left[0], md });
					q.offer(new int[] { Sr + dy[left[1]], Sc + dx[left[1]], left[1], md });
					q.offer(new int[] { Sr + dy[left[2]], Sc + dx[left[2]], left[2], md });
				} else if (md == 4) {
					q.offer(new int[] { Sr + dy[right[0]], Sc + dx[right[0]], right[0], md });
					q.offer(new int[] { Sr + dy[right[1]], Sc + dx[right[1]], right[1], md });
					q.offer(new int[] { Sr + dy[right[2]], Sc + dx[right[2]], right[2], md });
				}

				while (!q.isEmpty()) {
					int cur[] = q.poll();

					if (!checkRange(cur[0], cur[1]))
						continue;
					
					// 전사가 들린곳은 넘어가기
					if(w_visited[cur[0]][cur[1]])
						continue;

					if (visited[cur[0]][cur[1]])
						continue;
					visited[cur[0]][cur[1]] = true;

					if (worior_map[cur[0]][cur[1]] == 1) {
						cnt++;
						Deque<int[]> q2 = new ArrayDeque<>();
						q2.offer(new int[] {cur[0], cur[1], cur[2], cur[3]});
						
						while(!q2.isEmpty()) {
							int[] cur2 = q2.poll();
							int nr = cur2[0];
							int nc = cur2[1];
							int nd = cur2[2];
							
							if(!checkRange(nr, nc))
								continue;
							
							if(w_visited[nr][nc])
								continue;
							w_visited[nr][nc] = true;
							
							q2.offer(new int[] { nr + dy[nd], nc + dx[nd], nd, cur2[3] });
							if (cur2[3] == 1 && (nd == up[1] || nd == up[2]))
								q2.offer(new int[] { nr + dy[up[0]], nc + dx[up[0]], up[0], cur2[3] });
							else if (cur2[3] == 2 && (nd == down[1] || nd == down[2]))
								q2.offer(new int[] { nr + dy[down[0]], nc + dx[down[0]], down[0], cur2[3] });
							else if (cur2[3] == 3 && (nd == right[1] || nd == right[2]))
								q2.offer(new int[] { nr + dy[right[0]], nc + dx[right[0]], left[0], cur2[3] });
							else if (cur2[3] == 4 && (nd == left[1] || nd == left[2]))
								q2.offer(new int[] { nr + dy[left[0]], nc + dx[left[0]], right[0], cur2[3] });
							
						}
						
					} // end-for-worior

					q.offer(new int[] { cur[0] + dy[cur[2]], cur[1] + dx[cur[2]], cur[2], cur[3] });
					if (cur[3] == 1 && (cur[2] == up[1] || cur[2] == up[2]))
						q.offer(new int[] { cur[0] + dy[up[0]], cur[1] + dx[up[0]], up[0], cur[3] });
					else if (cur[3] == 2 && (cur[2] == down[1] || cur[2] == down[2]))
						q.offer(new int[] { cur[0] + dy[down[0]], cur[1] + dx[down[0]], down[0], cur[3] });
					else if (cur[3] == 3 && (cur[2] == right[1] || cur[2] == right[2]))
						q.offer(new int[] { cur[0] + dy[right[0]], cur[1] + dx[right[0]], left[0], cur[3] });
					else if (cur[3] == 4 && (cur[2] == left[1] || cur[2] == left[2]))
						q.offer(new int[] { cur[0] + dy[left[0]], cur[1] + dx[left[0]], right[0], cur[3] });


				} // end-while-q

				// 방향 정하기
				if (max < cnt) {
					m = md;
					max = cnt;
				}

			} // end-for-md

//			System.out.println("시야 방향 : " + m);
			
			// 전사 돌로 만들기
			Deque<int[]> q = new ArrayDeque<>();
			sight = new boolean[N][N];
			boolean[][] w_visited = new boolean[N][N];
			if (m == 1) {
				q.offer(new int[] { Sr + dy[up[0]], Sc + dx[up[0]], up[0], m });
				q.offer(new int[] { Sr + dy[up[1]], Sc + dx[up[1]], up[1], m });
				q.offer(new int[] { Sr + dy[up[2]], Sc + dx[up[2]], up[2], m });
			} else if (m == 2) {
				q.offer(new int[] { Sr + dy[down[0]], Sc + dx[down[0]], down[0], m });
				q.offer(new int[] { Sr + dy[down[1]], Sc + dx[down[1]], down[1], m });
				q.offer(new int[] { Sr + dy[down[2]], Sc + dx[down[2]], down[2], m });
			} else if (m == 3) {
				q.offer(new int[] { Sr + dy[left[0]], Sc + dx[left[0]], left[0], m });
				q.offer(new int[] { Sr + dy[left[1]], Sc + dx[left[1]], left[1], m });
				q.offer(new int[] { Sr + dy[left[2]], Sc + dx[left[2]], left[2], m });
			} else if (m == 4) {
				q.offer(new int[] { Sr + dy[right[0]], Sc + dx[right[0]], right[0], m });
				q.offer(new int[] { Sr + dy[right[1]], Sc + dx[right[1]], right[1], m });
				q.offer(new int[] { Sr + dy[right[2]], Sc + dx[right[2]], right[2], m });
			}

			while (!q.isEmpty()) {
				int cur[] = q.poll();

				if (!checkRange(cur[0], cur[1]))
					continue;

				// 전사가 막은 곳은 탐색 X
				if (w_visited[cur[0]][cur[1]])
					continue;

				if (sight[cur[0]][cur[1]])
					continue;
				sight[cur[0]][cur[1]] = true;

				if (worior_map[cur[0]][cur[1]] == 1) {
					// 돌이 될 친구들 고르기
					for (Worior w : Woriors) {
						if (!w.alive)
							continue;

						if (w.r == cur[0] && w.c == cur[1]) {
							w.rock = true;
						}
					}
					// 돌이되서 막아주는 친구들을 계산하기
					// 미리 visited를 true로 가면 메두사를 피할 수 있다.
					Deque<int[]> q2 = new ArrayDeque<>();
					q2.offer(new int[] {cur[0], cur[1], cur[2], cur[3]});
					
					while(!q2.isEmpty()) {
						int[] cur2 = q2.poll();
						int nr = cur2[0];
						int nc = cur2[1];
						int nd = cur2[2];
						
						if(!checkRange(nr, nc))
							continue;
						
						if(w_visited[nr][nc])
							continue;
						w_visited[nr][nc] = true;
						
						q2.offer(new int[] { nr + dy[nd], nc + dx[nd], nd, cur2[3] });
						if (cur2[3] == 1 && (nd == up[1] || nd == up[2]))
							q2.offer(new int[] { nr + dy[up[0]], nc + dx[up[0]], up[0], cur2[3] });
						else if (cur2[3] == 2 && (nd == down[1] || nd == down[2]))
							q2.offer(new int[] { nr + dy[down[0]], nc + dx[down[0]], down[0], cur2[3] });
						else if (cur2[3] == 3 && (nd == right[1] || nd == right[2]))
							q2.offer(new int[] { nr + dy[right[0]], nc + dx[right[0]], left[0], cur2[3] });
						else if (cur2[3] == 4 && (nd == left[1] || nd == left[2]))
							q2.offer(new int[] { nr + dy[left[0]], nc + dx[left[0]], right[0], cur2[3] });
						
					}
					
					
				} // end-for-worior

				q.offer(new int[] { cur[0] + dy[cur[2]], cur[1] + dx[cur[2]], cur[2], cur[3] });
				if (cur[3] == 1 && (cur[2] == up[1] || cur[2] == up[2]))
					q.offer(new int[] { cur[0] + dy[up[0]], cur[1] + dx[up[0]], up[0], cur[3] });
				else if (cur[3] == 2 && (cur[2] == down[1] || cur[2] == down[2]))
					q.offer(new int[] { cur[0] + dy[down[0]], cur[1] + dx[down[0]], down[0], cur[3] });
				else if (cur[3] == 3 && (cur[2] == right[1] || cur[2] == right[2]))
					q.offer(new int[] { cur[0] + dy[right[0]], cur[1] + dx[right[0]], left[0], cur[3] });
				else if (cur[3] == 4 && (cur[2] == left[1] || cur[2] == left[2]))
					q.offer(new int[] { cur[0] + dy[left[0]], cur[1] + dx[left[0]], right[0], cur[3] });


			} // end-while-q

//			System.out.println("=== 메두사 시야 ===");
//			for (int i = 0; i < N; i++) {
//			    for (int j = 0; j < N; j++) {
//			        System.out.print(sight[i][j] ? "■" : "□");
//			    }
//			    System.out.println();
//			}
//			System.out.println();
		}

	}

	static int[][] map, worior_map;
	static int N, Sr, Sc, Er, Ec, M;
	static List<Worior> Woriors = new ArrayList<>();
	static int[] dy = { 0, -1, 1, 0, 0, -1, -1, 1, 1 };
	static int[] dx = { 0, 0, 0, -1, 1, -1, 1, -1, 1 };
	static int[] dy2 = { 0, -1, 1, 0, 0 }, dx2 = { 0, 0, 0, -1, 1 };
	
	static int[] up = { 1, 5, 6 };
	static int[] down = { 2, 7, 8 };
	static int[] left = { 3, 5, 7 };
	static int[] right = { 4, 6, 8 };
	
	static boolean[][] sight;

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		StringBuilder sb = new StringBuilder();

		st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		map = new int[N][N];
		worior_map = new int[N][N];

		st = new StringTokenizer(br.readLine());
		Sr = Integer.parseInt(st.nextToken());
		Sc = Integer.parseInt(st.nextToken());
		Er = Integer.parseInt(st.nextToken());
		Ec = Integer.parseInt(st.nextToken());

		st = new StringTokenizer(br.readLine());
		for (int i = 0; i < M; i++) {
			int Ar = Integer.parseInt(st.nextToken());
			int Ac = Integer.parseInt(st.nextToken());

			Woriors.add(new Worior(Ar, Ac));
			worior_map[Ar][Ac] = 1;
		}

		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}

		Medussar medussar = new Medussar();

		while (true) {
			int move_cnt = 0;
			int rock_cnt = 0;
			int attack_cnt = 0;

			// 1. 메두사 움직임
			int m = medussar.move();
			if (m == 0) {
				sb.append(-1);
				break;
			}
//			System.out.println(m);

			if (Sr == Er && Sc == Ec) {
				sb.append(0);
				break;
			}
//			System.out.println(Sr + " " + Sc);

			// 2. 메두사 시야
			medussar.watch();

			// 3. 전사 움직임 + 공격
//			System.out.println("시야 전");
			for (Worior w : Woriors) {
//				System.out.println(w);
				if (!w.alive || w.rock) // 죽었거나 돌이되면 움직일수 없다.
					continue;
//				System.out.println("이동 전 : " + w);
				w.move();
//				System.out.println("이동 후 : " + w);
			}

			// 4. 답 입력
			for (Worior w : Woriors) {
				move_cnt += w.move;
				rock_cnt += w.rock ? 1 : 0;
				attack_cnt += w.attack ? 1 : 0;
			}
			sb.append(move_cnt).append(" ").append(rock_cnt).append(" ").append(attack_cnt).append("\n");
//			System.out.println(move_cnt + " " + rock_cnt + " " + attack_cnt);
			// 초기화
			for (Worior w : Woriors) {
//				System.out.println(w);
				w.rock = false;
				w.move = 0;
				w.attack = false;
			}
//			System.out.println("==========================");
		} // end-of-while true

		System.out.println(sb);

		br.close();

	}

	static boolean checkRange(int r, int c) {
		return (r >= 0 && r < N) && (c >= 0 && c < N);
	}
}
