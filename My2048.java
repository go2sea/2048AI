import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class My2048 extends JFrame
{
	JPanel numpanel = new JPanel();
	JPanel scorepanel = new JPanel();

	JLabel numlabellist[][] = new JLabel[4][4];// 数字格子
	int numlist[][] = new int[4][4];// 数组格子对应数值
	int blanks = 16;// 空白格子数
	int score = 0;// 总得分
	JLabel scorelabel = new JLabel();

	double smoothweight = 0.1;
	double monoweight = 1.0;
	double emptyweight = 2.7;
	double maxweight = 1.0;
	int maxdeepth;// 搜索深度
	int searchresult = 0;// 搜索所得的最佳reduce方向

	public static void main(String[] args)
	{
		new My2048().LaunchFrame();
	}

	public void AddNumArea()
	{
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
			{
				numlabellist[i][j] = new JLabel();
				numlist[i][j] = 0;
				numlabellist[i][j].setBackground(Color.LIGHT_GRAY);
				numlabellist[i][j].setBorder(new TitledBorder(""));
				numlabellist[i][j].setFont(new Font("Romantic", Font.BOLD, 35));
				numlabellist[i][j].setHorizontalAlignment(JTextField.CENTER);
				numpanel.add(numlabellist[i][j]);
			}
	}

	public void AddScoreLabel()
	{
		scorelabel.setBackground(Color.MAGENTA);
		scorelabel.setFont(new Font("Romantic", Font.BOLD, 35));
		scorelabel.setHorizontalAlignment(JTextField.CENTER);
		scorelabel.setText("score:" + Integer.toString(score));
		scorepanel.add(scorelabel);
	}

	// 随机选一个空白格子
	public int RandomIndex(int blanks)
	{
		Random random = new Random(System.currentTimeMillis());
		int result = random.nextInt(blanks) + 1;
		return result;
	}

	// 2,4随机选一个，其中4出现的概率为十分之一
	public int RandomValue()
	{
		Random random = new Random();
		int result = random.nextInt(10);
		if (result <= 8)
			return 2;
		return 4;
	}

	// 空白处产生一新值，自带refresh功能，show表示是否刷新界面
	// 若show为true，随机新值并刷新界面，若show为false,在[x][y]填充value但去刷新界面（用于搜索）
	public void NewValue(boolean show, int x, int y, int value)
	{
		if (!show)// 定值，用于搜索
		{
			// System.out.println(x+"   "+y);
			numlist[x][y] = value;
			if (numlist[x][y] == 0)
				blanks--;
			return;
		}
		int newvalue = RandomValue();// 新值
		int index = RandomIndex(blanks);// 新值得位置
		blanks--;// 空白格子数-1
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
			{
				if (numlist[i][j] == 0)
					index--;
				if (index == 0)
				{
					numlist[i][j] = newvalue;
					// 相应label上显示新值
					numlabellist[i][j].setText(Integer.toString(newvalue));
					return;
				}
			}
	}

	public boolean LeftReduce(boolean changescore)
	{
		boolean changed = false;// 是否有改变（移动和相加），作为后续是否产生新值得依据
		boolean has0before;
		int p;// 行指针
		int value;// 当前值
		for (int i = 0; i < 4; i++)
		{
			p = 0;
			value = 0;
			has0before = false;
			for (int j = 0; j < 4; j++)
			{
				if (numlist[i][j] > 0 && has0before)// 移动方向上之前有空格，可移动，changed为true
					changed = true;
				if (numlist[i][j] == 0)
					has0before = true;
				if (numlist[i][j] > 0)
				{
					if (numlist[i][j] == value)// 相加放p位置
					{
						int sum = value * 2;
						numlist[i][j] = 0;
						numlist[i][p] = sum;
						value = 0;
						p++;
						if (changescore)
							score += sum;
						changed = true;// 相加，changed为true
					} else
					{
						if (value > 0)// value放p中，当前值放value中
						{
							numlist[i][p] = value;
							p++;
						}
						value = numlist[i][j];
						numlist[i][j] = 0;

					}
				}
			}
			if (value > 0)// 到最后value中可能有值
				numlist[i][p] = value;
		}
		return changed;
	}

	public boolean RightReduce(boolean changescore)
	{
		boolean changed = false;// 是否有改变（移动和相加），作为后续是否产生新值得依据
		boolean has0before;
		int p;// 行指针
		int value;// 当前值
		for (int i = 0; i < 4; i++)
		{
			p = 3;
			value = 0;
			has0before = false;
			for (int j = 3; j >= 0; j--)
			{
				if (numlist[i][j] > 0 && has0before)// 移动方向上之前有空格，可移动，changed为true
					changed = true;
				if (numlist[i][j] == 0)
					has0before = true;
				if (numlist[i][j] > 0)
				{
					if (numlist[i][j] == value)// 相加放p位置
					{
						int sum = value * 2;
						numlist[i][j] = 0;
						numlist[i][p] = sum;
						value = 0;
						p--;
						if (changescore)
							score += sum;
						changed = true;// 相加，changed为true
					} else
					{
						if (value > 0)// value放p中，当前值放value中
						{
							numlist[i][p] = value;
							p--;
						}
						value = numlist[i][j];
						numlist[i][j] = 0;
					}
				}
			}
			if (value > 0)// 到最后value中可能有值
				numlist[i][p] = value;
		}
		return changed;
	}

	public boolean UpReduce(boolean changescore)
	{
		boolean changed = false;// 是否有改变（移动和相加），作为后续是否产生新值得依据
		boolean has0before;
		int p;// 行指针
		int value;// 当前值
		for (int j = 0; j < 4; j++)
		{
			p = 0;
			value = 0;
			has0before = false;
			for (int i = 0; i < 4; i++)
			{
				if (numlist[i][j] > 0 && has0before)// 移动方向上之前有空格，可移动，changed为true
					changed = true;
				if (numlist[i][j] == 0)
					has0before = true;
				if (numlist[i][j] > 0)
				{
					if (numlist[i][j] == value)// 相加放p位置
					{
						int sum = value * 2;
						numlist[i][j] = 0;
						numlist[p][j] = sum;
						value = 0;
						p++;
						if (changescore)
							score += sum;
						changed = true;// 相加，changed为true
					} else
					{
						if (value > 0)// value放p中，当前值放value中
						{
							numlist[p][j] = value;
							p++;
						}
						value = numlist[i][j];
						numlist[i][j] = 0;
					}
				}
			}
			if (value > 0)// 到最后value中可能有值
				numlist[p][j] = value;
		}
		return changed;
	}

	// changesum为true表示积分可以更改，否则保持不变（用于搜索）
	public boolean DownReduce(boolean changescore)
	{
		boolean changed = false;// 是否有改变（移动和相加），作为后续是否产生新值得依据
		boolean has0before;
		int p;// 行指针
		int value;// 当前值
		for (int j = 0; j < 4; j++)
		{
			p = 3;
			value = 0;
			has0before = false;
			for (int i = 3; i >= 0; i--)
			{
				if (numlist[i][j] > 0 && has0before)// 移动方向上之前有空格，可移动，changed为true
					changed = true;
				if (numlist[i][j] == 0)
					has0before = true;
				if (numlist[i][j] > 0)
				{
					if (numlist[i][j] == value)// 相加放p位置
					{
						int sum = value * 2;
						numlist[i][j] = 0;
						numlist[p][j] = sum;
						value = 0;
						p--;
						if (changescore)
							score += sum;
						changed = true;// 相加，changed为true
					} else
					{
						if (value > 0)// value放p中，当前值放value中
						{
							numlist[p][j] = value;
							p--;
						}
						value = numlist[i][j];
						numlist[i][j] = 0;
					}
				}
			}
			if (value > 0)// 到最后value中可能有值
				numlist[p][j] = value;
		}
		return changed;
	}

	// 刷新，show表示是否刷新界面
	public void Refresh(boolean show)
	{
		blanks = 0;
		if (show)
			scorelabel.setText("score:" + Integer.toString(score));
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
			{
				if (show && numlist[i][j] != 0)
					numlabellist[i][j].setText(Integer.toString(numlist[i][j]));
				if (numlist[i][j] == 0)
				{
					if (show)
						numlabellist[i][j].setText(null);
					blanks++;
				}
			}
	}

	public boolean CheckOut()
	{
		if (blanks > 0)
			return false;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 3; j++)
			{
				if (numlist[i][j] == numlist[i][j + 1])
					return false;
			}

		for (int j = 0; j < 4; j++)
			for (int i = 0; i < 3; i++)
			{
				if (numlist[i][j] == numlist[i + 1][j])
					return false;
			}
		return true;
	}

	public void Out()
	{
		JOptionPane.showMessageDialog(null, "游戏结束！", "2048PC版", 2);
		System.exit(0);
	}

	public void PrintNumlist()
	{
		System.out.println("blanks:" + blanks);
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				System.out.print(numlist[i][j]);
				System.out.print(' ');
			}
			System.out.println();
		}
		System.out.println("***********************");
	}

	// 单调率，是负数，越平滑越大，后期乘以monoweight(干脆方法内乘了）
	public double Mononess()
	{
		double total[] = { 0, 0, 0, 0 };
		// 纵向
		for (int i = 0; i < 4; i++)
		{
			int current = 0;
			int next = current + 1;
			double currentvalue, nextvalue;
			while (next < 4)
			{
				while (next < 4 && numlist[i][next] <= 0)
					next++;
				if (next == 4)
					next--;
				currentvalue = (numlist[i][current] > 0) ? Math.log(numlist[i][current]) / Math.log(2) : 0;
				nextvalue = (numlist[i][next] > 0) ? Math.log(numlist[i][next]) / Math.log(2) : 0;
				if (currentvalue > nextvalue)
					total[0] += nextvalue - currentvalue;
				else if (nextvalue > currentvalue)
					total[1] += currentvalue - nextvalue;
				current = next;
				next++;
			}
		}

		// 横向
		for (int j = 0; j < 4; j++)
		{
			int current = 0;
			int next = current + 1;
			double currentvalue, nextvalue;
			while (next < 4)
			{
				while (next < 4 && numlist[next][j] <= 0)
					next++;
				if (next == 4)
					next--;
				currentvalue = (numlist[current][j] > 0) ? Math.log(numlist[current][j]) / Math.log(2) : 0;
				nextvalue = (numlist[next][j] > 0) ? Math.log(numlist[next][j]) / Math.log(2) : 0;
				if (currentvalue > nextvalue)
					total[2] += nextvalue - currentvalue;
				else if (nextvalue > currentvalue)
					total[3] += currentvalue - nextvalue;
				current = next;
				next++;
			}
		}
//		System.out.println("Math.max(total[0], total[1]) : " + Math.max(total[0], total[1]));
//		System.out.println("Math.max(total[2], total[3]) : " + Math.max(total[2], total[3]));
		return (Math.max(total[0], total[1]) + Math.max(total[2], total[3])) * monoweight;
	}

	// 平滑率，是负数，越平滑越大，后期乘以smoothweight(干脆方法内乘了）
	public double Smoothness()
	{
		double smoothness = 0;
		// 向右 向下两个方向
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (numlist[i][j] <= 0)
					continue;
				double currentvalue = Math.log(numlist[i][j]) / Math.log(2);
				double nextvalue = 0;
				// 对每一个非0格子，向下向右分别检索最近的非空格子
				// 向右
				int jj;
				for (jj = j + 1; jj < 4; jj++)
					if (numlist[i][jj] > 0)
						break;
				if (jj < 4)
				{
					nextvalue = Math.log(numlist[i][jj]) / Math.log(2);
					smoothness -= Math.abs(currentvalue - nextvalue);
				}
				// 向下
				int ii;
				for (ii = i + 1; ii < 4; ii++)
					if (numlist[ii][j] > 0)
						break;	
				if (ii < 4)
				{
					nextvalue = Math.log(numlist[ii][j]) / Math.log(2);
					smoothness -= Math.abs(currentvalue - nextvalue);
				}
			}
		}
		return smoothness * smoothweight;
	}

	// 最大数，后期乘以maxweight(干脆方法内乘了）
	public int Maxness()
	{
		int max = 0;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (numlist[i][j] > max)
					max = numlist[i][j];
//		return Math.log(max) / Math.log(2) * maxweight;
		return max;
	}

	public double Emptyness()
	{
		return Math.log(blanks) * emptyweight;
	}

	// 返回格局评分
	public double GetScore()
	{
		return Emptyness() + Math.log(Maxness()) / Math.log(2) * maxweight + Smoothness() + Mononess();
	}

	// 0,1,2,3分别代表：LeftReduce RightReduce UpReduce DownReduce
	// changesum为true表示积分可以更改，否则保持不变（用于搜索）
	public boolean Reduce(int i, boolean changesum)
	{
		if (i == 0)
			return LeftReduce(changesum);
		if (i == 1)
			return RightReduce(changesum);
		if (i == 2)
			return UpReduce(changesum);
		if (i == 3)
			return DownReduce(changesum);
		return false;
	}

	// max节点，用子节点（min节点）的beta更新alpha
	public Map.Entry<Double, Double> SearchMax(int nowdeepth, double alpha_in, double beta_in)
	{
		// System.out.println(nowdeepth);
		double alpha = alpha_in;
		double beta = beta_in;
		int temphere[][] = new int[4][4];
		int bestreduce = -1;// 此步所得的最佳reduce方向
		// 备份当前格局
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				temphere[i][j] = numlist[i][j];

		// 叶节点，递归出口
		if (nowdeepth >= maxdeepth)
		{
			for (int i = 0; i < 4; i++)
				if (Reduce(i, false))
				{
					double score = GetScore();

					if (score > alpha || bestreduce == -1)
					{
						if (score > alpha)
							alpha = score;
						bestreduce = i;// 修改最佳reduce方向
					}
					// 恢复原先格局
					for (int ii = 0; ii < 4; ii++)
						for (int jj = 0; jj < 4; jj++)
							numlist[ii][jj] = temphere[ii][jj];
					Refresh(false);
				}
			Map.Entry<Double, Double> result = new AbstractMap.SimpleEntry<Double, Double>(alpha, beta);
			searchresult = bestreduce;
			return result;
		}
		// 非叶节点
		for (int i = 0; i < 4; i++)
		{
			if (Reduce(i, false))
			{
				// min节点的beta赋给父节点的alpha：若比父节点alpha大
				Map.Entry<Double, Double> entry = SearchMin2(nowdeepth + 1, alpha, beta);
				if (entry.getValue().doubleValue() > alpha || bestreduce == -1)
				{
					if (entry.getValue().doubleValue() > alpha)
						alpha = entry.getValue().doubleValue();
					bestreduce = i;// 修改最佳reduce方向
				}
				// 恢复原先格局
				for (int ii = 0; ii < 4; ii++)
					for (int jj = 0; jj < 4; jj++)
						numlist[ii][jj] = temphere[ii][jj];
				Refresh(false);
				if (alpha >= beta)// alpha-beta剪枝
					break;
			}
		}
		Map.Entry<Double, Double> result = new AbstractMap.SimpleEntry<Double, Double>(alpha, beta);
		searchresult = bestreduce;
		return result;
	}

	// min节点（min节点只走让当前格局最差的一步，因此只有一个分支）
	public Map.Entry<Double, Double> SearchMin(int nowdeepth, double alpha_in, double beta_in)
	{
		// System.out.println(nowdeepth);
		double alpha = alpha_in;
		double beta = beta_in;
		// 搜索对于当前格局最不利的赋值位置
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
			{
				if (numlist[i][j] > 0)// 该位置非空
					continue;
				// System.out.println("i:"+i+"    j:"+j);
				NewValue(false, i, j, 2);// 赋值为2
				double score = GetScore();
				if (score < beta)
					beta = score;
				NewValue(false, i, j, 4);// 赋值为2
				score = GetScore();
				if (score < beta)
					beta = score;
				// 恢复原先格局
				numlist[i][j] = 0;
				blanks++;
			}
		// min节点只选择让#当前格局#最差的位置和值，所以只有一个分支
		if (nowdeepth < maxdeepth)
		{
			Map.Entry<Double, Double> entry = SearchMax(nowdeepth + 1, alpha, beta);
			if (entry.getKey().doubleValue() < beta)
				beta = entry.getKey().doubleValue();
		}

		Map.Entry<Double, Double> result = new AbstractMap.SimpleEntry<Double, Double>(alpha, beta);
		return result;
	}

	// min节点（分支数：blanks，只考虑2不考虑4）
	public Map.Entry<Double, Double> SearchMin2(int nowdeepth, double alpha_in, double beta_in)
	{
		double alpha = alpha_in;
		double beta = beta_in;
		// 搜索对于当前格局最不利的赋值位置
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
			{
				if (numlist[i][j] > 0)// 该位置非空
					continue;
				Map.Entry<Double, Double> entry = null;
				// 2
				NewValue(false, i, j, 2);// 赋值为2
				entry = SearchMax(nowdeepth + 1, alpha, beta);
				if (entry.getKey().doubleValue() < beta)
				{
					beta = entry.getKey().doubleValue();
					if (alpha >= beta)// alpha-beta剪枝
					{
						Map.Entry<Double, Double> result = new AbstractMap.SimpleEntry<Double, Double>(alpha, beta);
						return result;
					}
				}
				// //4
				// NewValue(false, i, j, 4);// 赋值为2
				// entry = SearchMax(nowdeepth + 1, alpha, beta);
				// if (entry.getKey().doubleValue() < beta)
				// {
				// beta = entry.getKey().doubleValue();
				// if (alpha >= beta)// alpha-beta剪枝
				// {
				// Map.Entry<Double, Double> result = new
				// AbstractMap.SimpleEntry<Double, Double>(alpha, beta);
				// return result;
				// }
				// }
				numlist[i][j] = 0;
				blanks++;
			}

		Map.Entry<Double, Double> result = new AbstractMap.SimpleEntry<Double, Double>(alpha, beta);
		return result;
	}

	// AI程序（无尽版）
	public void AIEndless()
	{
		double count = 0;
		double total[] = { 0, 0, 0, 0 };
		int bestever = 0;// 历史最好成绩
		while (true)
		{
			count++;
			while (!CheckOut())
			{
				// 动态调整搜索深度
				if (blanks <= 1)
					maxdeepth = 11;
				else if (blanks <= 4)
					maxdeepth = 9;
				else
					maxdeepth = 7;

				SearchMax(1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
				Reduce(searchresult, true);
				Refresh(true);
				NewValue(true, 0, 0, 0);
			}
			// 重置
			int maxnum = Maxness();
			if (maxnum >= 1024)
			{
				int result = (int) (Math.log(maxnum / 1024) / Math.log(2));
				total[result]++;
			}
			if (score > bestever)
				bestever = score;

			// 打印
			System.out.println("****************************************");
			PrintNumlist();
			System.out.println("count:" + (int) count);
			System.out.println("score:" + (int) score);
			System.out.println("maxnum:" + (int) (Maxness() / maxweight));
			System.out.println("bestever:" + bestever);

			System.out.println("1024:" + (int) total[0] + "      " + total[0] / count);
			System.out.println("2048:" + (int) total[1] + "      " + total[1] / count);
			System.out.println("4096:" + (int) total[2] + "      " + total[2] / count);
			System.out.println("8192:" + (int) total[3] + "      " + total[3] / count);

			System.out.println("****************************************");
			for (int i = 0; i < 4; i++)
				for (int j = 0; j < 4; j++)
					numlist[i][j] = 0;
			blanks = 16;
			score = 0;
			Refresh(true);
			NewValue(true, 0, 0, 0);
			NewValue(true, 0, 0, 0);
		}
	}

	// AI程序（无尽版）
	public void AI()
	{
		while (!CheckOut())
		{
			 // 动态调整搜索深度
			 if (blanks <= 1)
			 maxdeepth = 11;
			 else if (blanks <= 4)
			 maxdeepth = 9;
			 else
			 maxdeepth = 7;
			
			SearchMax(1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			Reduce(searchresult, true);
			Refresh(true);
			NewValue(true, 0, 0, 0);
		}
		Out();
		System.exit(0);
	}

	public void LaunchFrame()
	{
		setTitle("2048PC版"); // 设置窗体标题
		setBounds(700, 100, 400, 475);
		setLayout(new BorderLayout());

		setResizable(false); // 禁止调整窗体大小
		numpanel.setLayout(new GridLayout(4, 4)); // 设置空布局
		numpanel.setLocation(0, 200);
		AddScoreLabel();// 添加计分器
		AddNumArea();// 添加数字格子
		getContentPane().add(scorepanel, BorderLayout.NORTH);
		getContentPane().add(numpanel, BorderLayout.CENTER);

		NewValue(true, 0, 0, 0);
		NewValue(true, 0, 0, 0);
		setVisible(true);

		// addListener();
		//AI();
		AIEndless();
	}

	// 添加键盘监听
	public void addListener()
	{
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}

		});

		this.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == 37)// 左键
				{
					if (LeftReduce(true))
					{
						Refresh(true);// 更新blank，numlabellist
						NewValue(true, 0, 0, 0);
					}
				}
				if (e.getKeyCode() == 39)// 右键
				{
					if (RightReduce(true))
					{
						Refresh(true);// 更新blank，numlabellist
						NewValue(true, 0, 0, 0);
					}
				}
				if (e.getKeyCode() == 38)// 上键
				{
					if (UpReduce(true))
					{
						Refresh(true);// 更新blank，numlabellist
						NewValue(true, 0, 0, 0);
					}
				}
				if (e.getKeyCode() == 40)// 上键
				{
					if (DownReduce(true))
					{
						Refresh(true);// 更新blanks，numlabellist,scorelabel
						NewValue(true, 0, 0, 0);
					}
				}
				if (e.getKeyCode() == 32)// 空格键
				{
					if (LeftReduce(true))
					{
						Refresh(true);// 更新blanks，numlabellist,scorelabel
						NewValue(true, 0, 0, 0);
					} else if (RightReduce(true))
					{
						Refresh(true);// 更新blanks，numlabellist,scorelabel
						NewValue(true, 0, 0, 0);
					} else if (UpReduce(true))
					{
						Refresh(true);// 更新blanks，numlabellist,scorelabel
						NewValue(true, 0, 0, 0);
					} else if (DownReduce(true))
					{
						Refresh(true);// 更新blanks，numlabellist,scorelabel
						NewValue(true, 0, 0, 0);
					}
				}
				System.out.println(e.getKeyCode());

				if (CheckOut())// 检查游戏是否结束
					Out();
				PrintNumlist();

				System.out.println("mononess:" + Double.toString(Mononess()));
				System.out.println("smoothness:" + Double.toString(Smoothness()));
				System.out.println("maxnum:" + Double.toString(Maxness()));
				System.out.println("blanks:" + Double.toString(Emptyness()));
			}

		});
	}

}
