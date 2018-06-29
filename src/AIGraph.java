import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by kimjisub on 2017. 6. 21..
 *
 */
public class AIGraph extends Frame {

	//------------------------------------------------------------------------------------------------------------- 기본 변수 {

	final static int SIZE = 5;
	final static int VNUM1 = SIZE * SIZE;
	final static int VNUM2 = SIZE * SIZE * 2;
	final static int VNUM3 = 10;


	static Vertex1[] vertex1 = new Vertex1[VNUM1];
	static Vertex2[] vertex2 = new Vertex2[VNUM2];
	static Vertex3[] vertex3 = new Vertex3[VNUM3];

	private float map[][] = new float[SIZE][SIZE];

	private ArrayList<Data.LearnData> learnData = new ArrayList<>();
	private Data.LearnData currLearnData = null;

	private ArrayList<String> log = new ArrayList<>();


	static class Vertex1 {//입력층
		float input = 0;
		float output = 0;
		Edge[] edge = new Edge[VNUM2];

		static void calcEdge() {
			for (int i = 0; i < VNUM1; i++)
				vertex1[i].output = vertex1[i].input;

			for (int i1 = 0; i1 < VNUM1; i1++) {
				for (int i2 = 0; i2 < VNUM2; i2++) {
					Vertex1 v1 = vertex1[i1];
					Edge e1 = v1.edge[i2];

					float output = v1.output * e1.weight;
					e1.send = output;
					vertex2[i2].input += output;
				}
			}
		}


	}

	static class Vertex2 {//은닉층
		float input = 0;
		float output = 0;
		Edge[] edge = new Edge[VNUM3];

		static void calcEdge() {
			for (int i2 = 0; i2 < VNUM2; i2++)
				vertex2[i2].output = sigmoid(vertex2[i2].input);

			for (int i2 = 0; i2 < VNUM2; i2++) {
				for (int i3 = 0; i3 < VNUM3; i3++) {
					Vertex2 v2 = vertex2[i2];
					Edge e2 = v2.edge[i3];

					float output = v2.output * e2.weight;
					e2.send = output;
					vertex3[i3].input += output;
				}
			}
		}
	}

	static class Vertex3 {//출력층
		float input = 0;
		float output = 0;

		static void calcEdge() {
			for (int i3 = 0; i3 < VNUM3; i3++)
				vertex3[i3].output = sigmoid(vertex3[i3].input);
		}
	}

	class Edge {
		float weight = 0;
		float send = 0;

		Edge(float weight) {
			this.weight = weight;
		}
	}


	//------------------------------------------------------------------------------------------------------------- } 기본 변수


	//------------------------------------------------------------------------------------------------------------- 메인 {

	AIGraph() {
		super("Number Awareness A.I. (by Kimjisub)");


		setSize(1400, 800);
		setLayout(new FlowLayout());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				mouseClick(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				keyPress(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});

		createGraph();
		Data.GraphData.loadGraph(vertex1, vertex2);
		saveGraph();
		calc();

		learnData = Data.LearnData.loadData();

		setVisible(true);
	}

	private void createGraph() {
		Random r = new Random();
		//(r.nextBoolean()?-1:1) * (float)Math.random()

		for (int i = 0; i < VNUM1; i++)
			vertex1[i] = new Vertex1();

		for (int i = 0; i < VNUM2; i++)
			vertex2[i] = new Vertex2();

		for (int i = 0; i < VNUM3; i++)
			vertex3[i] = new Vertex3();

		for (int i1 = 0; i1 < VNUM1; i1++) {
			for (int i2 = 0; i2 < VNUM2; i2++) {
				vertex1[i1].edge[i2] = new Edge((r.nextBoolean() ? -1 : 1) * (float) Math.random());
			}
		}

		for (int i1 = 0; i1 < VNUM2; i1++) {
			for (int i2 = 0; i2 < VNUM3; i2++) {
				vertex2[i1].edge[i2] = new Edge((r.nextBoolean() ? -1 : 1) * (float) Math.random());
			}
		}
	}

	private void initGraph(float[][] map) {
		for (int i = 0; i < VNUM1; i++) {
			int x = i / SIZE;
			int y = i % SIZE;

			vertex1[x * SIZE + y].input = map[x][y];
		}

		for (int i = 0; i < VNUM2; i++)
			vertex2[i].input = 0f;

		for (int i = 0; i < VNUM3; i++)
			vertex3[i].input = 0f;

		for (int i1 = 0; i1 < VNUM1; i1++) {
			for (int i2 = 0; i2 < VNUM2; i2++) {
				vertex1[i1].edge[i2].send = 0;
			}
		}

		for (int i1 = 0; i1 < VNUM2; i1++) {
			for (int i2 = 0; i2 < VNUM3; i2++) {
				vertex2[i1].edge[i2].send = 0;
			}
		}
	}


	private void doTrain(int num) {
		addLog("입력된 데이터를 " + num + "으로 학습하였습니다.");
		train(num);
		saveGraph();

		doClear();
	}

	private void doReTrain() {
		int count = 0;
		for (int i1 = 0; i1 < 1000; i1++) {
			for (Data.LearnData data : learnData) {
				int num = data.num;
				float[][] map_ = data.map;
				for (int x = 0; x < SIZE; x++)
					for (int y = 1; y < SIZE; y++)
						map[x][y] = map_[x][y];


				train(num);
				clearMap();
				count++;
			}
		}
		addLog("저장된 학습데이터 " + count + "개를 재학습하였습니다.");
		saveGraph();

	}

	private void doClear() {
		clearMap();

		calc();
		repaint();
	}

	private void clearMap() {
		for (int i1 = 0; i1 < SIZE; i1++) {
			for (int i2 = 0; i2 < SIZE; i2++) {
				map[i1][i2] = 0f;
			}
		}
	}

	private void saveGraph() {
		Data.GraphData.saveGraph(vertex1, vertex2);
		//addLog("신경망을 저장하였습니다.");
	}

	private void saveCurrLearnData() {
		if (currLearnData != null) {
			learnData.add(currLearnData);
			addLog("최근 학습 기록을 저장하였습니다. (현재 " + learnData.size() + "개)");

			Data.LearnData.saveData(learnData);
		} else
			addLog("최근 학습 기록이 없습니다.");

	}

	private void calc() {
		initGraph(map);
		Vertex1.calcEdge();
		Vertex2.calcEdge();
		Vertex3.calcEdge();
	}

	private void train(int num) {
		final float ALPHA = 0.9f;

		currLearnData = new Data.LearnData(num, map);
		calc();

		float[] ans = new float[VNUM3];
		for (int i = 0; i < VNUM3; i++)
			if (i == num)
				ans[i] = 1f;

		for (int i = 0; i < 1; i++) {
			float[] deltaV3 = new float[VNUM3];
			float[] deltaV2 = new float[VNUM2];


			for (int i2 = 0; i2 < VNUM2; i2++) {
				for (int i3 = 0; i3 < VNUM3; i3++) {

					float output = vertex3[i3].output;
					float err = ans[i3] - output;
					deltaV3[i3] = output * (1 - output) * err;
				}
			}

			for (int i2 = 0; i2 < VNUM2; i2++) {
				Vertex2 v2 = vertex2[i2];
				for (int i3 = 0; i3 < VNUM3; i3++) {
					float weight = v2.edge[i3].weight;
					float output = v2.output;
					float err = weight * deltaV3[i3];
					deltaV2[i2] += output * (1 - output) * err;
				}
			}

			for (int i1 = 0; i1 < VNUM1; i1++) {
				for (int i2 = 0; i2 < VNUM2; i2++) {
					Vertex1 v1 = vertex1[i1];
					float output = v1.output;
					float dw = ALPHA * deltaV2[i2] * output;
					vertex1[i1].edge[i2].weight += dw;
				}
			}

			for (int i2 = 0; i2 < VNUM2; i2++) {
				for (int i3 = 0; i3 < VNUM3; i3++) {
					Vertex2 v2 = vertex2[i2];
					float output = v2.output;
					float dw = ALPHA * deltaV3[i3] * output;
					vertex2[i2].edge[i3].weight += dw;
				}
			}

		}

	}


	//------------------------------------------------------------------------------------------------------------- } 메인


	//------------------------------------------------------------------------------------------------------------- 그래픽 및 입력 {


	//input view
	final static class in {
		final static int xOffset = 5;       //X Offset
		final static int yOffset = 25;      //Y Offset
		final static int Width = 50;       //Width
	}

	//neural view
	final static class n {
		final static int xOffset = 500;  //X Offset
		final static int yOffset = 25;       //Y Offset
		final static int Width1 = 9;        //Width
		final static int Width2 = 4;        //Width
		final static int Width3 = 9;        //Width
		final static int HWidth1 = Width1 / 2;    //Half Width
		final static int HWidth2 = Width2 / 2;    //Half Width
		final static int HWidth3 = Width3 / 2;    //Half Width
		final static int xTerm = 290;      //X Term
		final static int yTerm1 = 11;    //Y Term
		final static int yTerm2 = 5;    //Y Term
		final static int yTerm3 = 70;    //Y Term
	}

	//result view
	final static class r {
		final static int xOffset = 1110;  //X Offset
		final static int yOffset = 25;       //Y Offset
		final static int yTerm = 12;    //Y Term
		final static float ratio = 1.8f;
	}

	public void paint(Graphics g) {
		paintLog(g);
		paintInput(g);
		paintNeural(g);
		paintResultView(g);
	}

	private void paintLog(Graphics g) {

		int num = Math.min(15, log.size());
		for (int i = 0; i < num; i++) {
			String str = log.get(i);
			g.drawString(str, 0, 550 + (12 - i) * 12);
		}
	}

	private void paintInput(Graphics g) {
		for (int i = 0; i < VNUM1; i++) {
			int x = i / SIZE;
			int y = i % SIZE;

			g.setColor(Color.black);
			g.drawRect(in.xOffset + in.Width * x, in.yOffset + in.Width * y, in.Width, in.Width);
			g.setColor(getColor(vertex1[i].output));
			g.fillRect(in.xOffset + in.Width * x, in.yOffset + in.Width * y, in.Width, in.Width);
		}

		g.setColor(Color.black);
		int x = SIZE;
		for (int y = 0; y <= 10; y++) {
			g.drawRect(in.xOffset + in.Width * x, in.yOffset + in.Width * y, in.Width, in.Width);
			if (y != 10)
				g.drawString(y + "", in.xOffset + in.Width * x + 20, in.yOffset + in.Width * y + 30);
			else
				g.drawString("X", in.xOffset + in.Width * x + 20, in.yOffset + in.Width * y + 30);

		}
	}

	private void paintNeural(Graphics g) {

		for (int i1 = 0; i1 < VNUM1; i1++) {
			for (int i2 = 0; i2 < VNUM2; i2++) {
				g.setColor(getColor(vertex1[i1].edge[i2].send, 0, 3));
				g.drawLine(n.xOffset + n.HWidth1, n.yOffset + n.yTerm1 * i1 + n.HWidth1,
					n.xOffset + n.xTerm + n.HWidth2, n.yOffset + n.yTerm2 * i2 + n.HWidth2);
			}
		}
		for (int i2 = 0; i2 < VNUM2; i2++) {
			for (int i3 = 0; i3 < VNUM3; i3++) {
				g.setColor(getColor(vertex2[i2].edge[i3].send, 0, 3));
				g.drawLine(n.xOffset + n.xTerm + n.HWidth2, n.yOffset + n.yTerm2 * i2 + n.HWidth2,
					n.xOffset + n.xTerm * 2 + n.HWidth3, n.yOffset + n.yTerm3 * i3 + n.HWidth3);
			}
		}


		g.setColor(Color.black);
		for (int i = 0; i < VNUM1; i++) {
			g.setColor(Color.black);
			g.drawOval(n.xOffset, n.yOffset + n.yTerm1 * i, n.Width1, n.Width1);
			g.setColor(getColor(vertex1[i].output));
			g.fillOval(n.xOffset, n.yOffset + n.yTerm1 * i, n.Width1, n.Width1);
		}
		for (int i = 0; i < VNUM2; i++) {
			g.setColor(Color.black);
			g.drawOval(n.xOffset + n.xTerm, n.yOffset + n.yTerm2 * i, n.Width2, n.Width2);
			g.setColor(getColor(vertex2[i].output));
			g.fillOval(n.xOffset + n.xTerm, n.yOffset + n.yTerm2 * i, n.Width2, n.Width2);
		}
		for (int i = 0; i < VNUM3; i++) {
			g.setColor(Color.black);
			g.drawOval(n.xOffset + n.xTerm * 2, n.yOffset + n.yTerm3 * i, n.Width3, n.Width3);
			g.setColor(getColor(vertex3[i].output));
			g.fillOval(n.xOffset + n.xTerm * 2, n.yOffset + n.yTerm3 * i, n.Width3, n.Width3);
		}
	}

	private void paintResultView(Graphics g) {
		g.setColor(Color.black);
		int maxPercent = 0;
		int answer = 0;
		for (int i = 0; i < 10; i++) {
			int percent = (int) (vertex3[i].output * 100);
			g.drawString(i + "", r.xOffset + (int) (percent * r.ratio), r.yOffset + r.yTerm * (i + 1));
			g.drawString(percent + "%", r.xOffset + (int) (110 * r.ratio), r.yOffset + r.yTerm * (i + 1));

			if (maxPercent < percent) {
				maxPercent = percent;
				answer = i;
			}
		}

		int[] count = new int[10];
		for (int i = 0; i < learnData.size(); i++) {
			count[learnData.get(i).num]++;
		}

		for (int i = -1; i < 10; i++) {
			if (i == -1)
				g.drawString("학습 데이터", r.xOffset, r.yOffset + r.yTerm * i + 300);
			else
				g.drawString(i + " : " + count[i] + "개", r.xOffset, r.yOffset + r.yTerm * i + 300);
		}


		if (maxPercent > 80)
			g.setColor(new Color(0x66bb6a));
		else if (maxPercent > 40)
			g.setColor(Color.orange);
		else
			g.setColor(Color.red);

		g.setFont(new Font(null, Font.PLAIN, 20));
		g.drawString("answer : " + answer + " (" + maxPercent + "%)",
			r.xOffset + (int) (30 * r.ratio), r.yOffset + r.yTerm * 15);
	}

	private Color getColor(float num) {
		return getColor(num, 0, 1);
	}

	private Color getColor(float num, float def, float size) {
		num -= def;

		int weight = (int) (num / size * 0xFF);
		int rgba = 0xFFFF0000;
		if (num == 0f)
			rgba = 0x00FFFFFF;//흰색
		else if (num > 0f && num <= size)
			rgba = weight * 0x01000000;//검정
		else if (num < 0f && num >= -size)
			rgba = (-weight) * 0x01000000 + 0x000000FF;//파란
		return new Color(rgba, true);
	}

	private void addLog(String str) {
		log.add(0, str);
	}


	private void mouseClick(MouseEvent e) {
		Point point = e.getPoint();

		int x = ((int) point.getX() - in.xOffset) / in.Width;
		int y = ((int) point.getY() - in.yOffset) / in.Width;

		if (x < SIZE && y < SIZE) {//격자
			if (map[x][y] == 0f)
				map[x][y] = 1f;
			else
				map[x][y] = 0f;
			calc();
			repaint();
		} else if (x == SIZE && y < 10) {//숫자
			doTrain(y);
		} else if (x == SIZE && y == 10) {//초기화
			doClear();
		}
	}

	private void keyPress(KeyEvent e) {
		char c = e.getKeyChar();

		if (c >= '0' && c <= '9')//숫자
			doTrain(c - '0');
		else if (c == 0x08 || c == 'x')
			doClear();
		else if (c == 'a')
			doReTrain();
		else if (c == 's')
			saveCurrLearnData();


		calc();
		repaint();
	}

	//------------------------------------------------------------------------------------------------------------- } 그래픽


	//------------------------------------------------------------------------------------------------------------- 수학 식 {

	static float sigmoid(float x) {
		return 1f / (1 + (float) Math.pow(Math.E, -x));
	}

	//------------------------------------------------------------------------------------------------------------- } 수학 식


}


