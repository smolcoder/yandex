import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;


public class SuffAutomata implements Cloneable {
	
	class State {
		int pos;
		int num;
		State sufLink;
		int length;
		Map<Character, State> next;	
		State par;
		
		State() {
			num = 0;
			pos = -1;
			sufLink = null;
			length = 0;
			next = new HashMap<Character, State>();
		}
		
		State(int length, int num) {
			this();
			this.num = num;
			this.length = length;
		}
		
		public String toString() {
			StringBuilder res = new StringBuilder();
			
			return res.toString();
		}
				
	}
	
	private List<State> states;
	private State last;		
	
	public SuffAutomata(String s) {
		states = new ArrayList<State>();
		last = createState(0);
		for (int i = 0; i < s.length(); ++i) {
			extend(s.charAt(i), i);
		}
	}
	
	private State createState(int length) {
		State res = new State(length, states.size());
		states.add(res);
		return res;
	}	
	
	public State getState(int index) {
		return states.get(index);
	}
	
	private void extend(char c, int pos) {
		State cur = createState(last.length + 1);
		cur.pos = pos;
		State p = last;
		while (p != null && !p.next.containsKey(c)) {
			p.next.put(c , cur);
			p = p.sufLink;
		}
		if (p == null) {
			cur.sufLink = states.get(0);
			last = cur;
			return;
		}
		State q = p.next.get(c);
		if (p.length + 1 == q.length) {
			cur.sufLink = q;
		} else {
			State clone = createState(p.length + 1);
			clone.next = new HashMap<Character, State>(q.next);
			clone.sufLink = q.sufLink;
			clone.pos = pos;
			q.sufLink = clone; 
			while (p != null && p.next.get(c).equals(q)) {
				p.next.put(c, clone);
				p = p.sufLink;
			}
			cur.sufLink = clone;			
		}
		last = cur;
	}
	
	void dfs(char c, int st, int[] masks) {		
		for (Entry<Character, State> e : states.get(st).next.entrySet()) {			
			if (e.getValue() != null) {
				dfs(e.getKey(), e.getValue().num, masks);
				if ('A' <= e.getKey() && e.getKey() <= 'J') {
					masks[st] |= 1 << (e.getKey() - 'A');
				} else {
					masks[st] |= masks[e.getValue().num];
				}
			}
		}
	}
	
	public int size() {
		return states.size();
	}
	public int getMaxLen(int stateNum) {
		return states.get(stateNum).length;
	}
	
	static void LCS(String[] strings) {
		int k = strings.length;
		char[] delimiters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < k; ++i) {
			sb.append(strings[i] + delimiters[i]);
		}
		SuffAutomata sa = new SuffAutomata(sb.toString());
		int[] masks = new int[sa.size()];
		Arrays.fill(masks, 0);
		sa.dfs(' ', 0, masks);
		int ans = 0;
		int bestState = -1;
		for (int i = 0; i < sa.size(); ++i) {
			if (masks[i] == ((1 << k)  - 1) && sa.getMaxLen(i) > ans) {
				bestState = i;
				ans = sa.getMaxLen(i);
			}
		}
		//System.out.println(ans + " " + sa.getState(bestState).pos);
		for (int i = sa.getState(bestState).pos - ans + 1; i <= sa.getState(bestState).pos; ++i) {
			System.out.print(sb.charAt(i));
		}
	}
	
	public String toString() {
		StringBuilder res = new StringBuilder();
		for (State st : states) {
			res.append(st.num + " (len = " + st.length + "): ");
			for (Entry<Character, State> e : st.next.entrySet()) {
				res.append(e.getKey() + "->" + e.getValue().num + "  ");
			}
			if (st.sufLink != null) {
				res.append("sufLink = " + st.sufLink.num);
			}
			res.append("\n");
		}
		return res.toString();
	}
	
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		MyScanner in = new MyScanner("in.txt");
//		String s = "abbb";
//		SuffAutomata sa = new SuffAutomata(s);
//		System.out.print(sa);
		int k = in.nextInt();
		String[] s = new String[k];
		for (int i = 0; i < k; ++i) {
			s[i] = in.next();
		}
		LCS(s);		
	}
	
	static class MyScanner {
		BufferedReader br;
		StringTokenizer st;

		MyScanner(String file) throws FileNotFoundException {
			br = new BufferedReader(new FileReader(new File(file)));
		}

		String next() throws IOException {
			if (st == null || !st.hasMoreTokens()) {
				st = new StringTokenizer(br.readLine());
			}
			return st.nextToken();
		}

		int nextInt() throws NumberFormatException, IOException {
			return Integer.parseInt(next());
		}
		
		boolean hasNext() throws IOException {
			if (br.ready()) {
				return true;
			}
			return (st != null && st.hasMoreTokens());
		}
	}
}
