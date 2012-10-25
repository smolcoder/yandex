import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class FormatConverter {

	static class FolderTree {

		class State implements Comparable<State> {
			String name;
			int id;
			int parent;
			int nesting; // nesting level
			List<Integer> succs;

			public State() {
				succs = new ArrayList<Integer>();
			}

			public State(String name, int id, int pos, int par) {
				this();
				this.name = name;
				this.id = id;
				this.parent = par;
				if (par == -1) {
					this.nesting = 0;
				} else {
					this.nesting = getState(this.parent).nesting + 1;
					getState(par).succs.add(pos);
				}
			}

			@Override
			public int compareTo(State o) {
				return id - o.id;
			}

			String fullName() {
				class GetPref {
					StringBuilder getPref(State st) {
						if (st.parent == -1) {
							return new StringBuilder(st.name);
						}
						StringBuilder sb = getPref(getState(st.parent));
						return sb.append("/" + st.name);
					}
				}
				return new GetPref().getPref(this).toString();
			}
		}

		List<State> states;
		int root;
		Map<String, Integer> mapIdByStr;
		Map<Integer, Integer> mapPosById;

		public FolderTree() {
			states = new ArrayList<State>();
			this.root = -1;
			mapIdByStr = new HashMap<String, Integer>();
			mapPosById = new HashMap<Integer, Integer>();
		}

		State getState(int index) {
			return states.get(index);
		}

		int size() {
			return states.size();
		}

		void addRelativePath(String path, int id, int parentPos) {
			String npath = path;
			if (id == 0) {
				this.root = 0;
				states.add(new State(path, id, 0, -1));
			} else {
				State cur = new State(path, id, states.size(), parentPos);
				states.add(cur);
				npath = cur.fullName();
			}
			mapIdByStr.put(npath, id);
			mapPosById.put(id, states.size() - 1);
		}

		void addFullPath(String path, int id) {
			if (id == 0) {
				this.root = 0;
				states.add(new State(path, id, 0, -1));
			} else {
				int lastSlash = path.lastIndexOf('/');
				int parId = mapIdByStr.get(path.substring(0, lastSlash));
				int parPos = mapPosById.get(parId);
				State cur = new State(path.substring(lastSlash + 1), id,
						states.size(), parPos);
				states.add(cur);
			}
			mapIdByStr.put(path, id);
			mapPosById.put(id, states.size() - 1);
		}

	}

	FolderTree readFind(Scanner in) {
		FolderTree tree = new FolderTree();
		int n = in.nextInt();
		for (int i = 0; i < n; ++i) {
			String name = in.next();
			int id = in.nextInt();
			tree.addFullPath(name, id);
		}
		return tree;
	}

	FolderTree readPyth(Scanner in) {
		FolderTree tree = new FolderTree();
		int n = in.nextInt();
		in.nextLine();

		String s = in.nextLine();
		String[] tokens = s.split(" ");
		tree.addRelativePath(tokens[0], Integer.parseInt(tokens[1]), -1);
		Stack<Integer> parents = new Stack<Integer>();
		parents.push(0);

		for (int i = 1; i < n; ++i) {
			s = in.nextLine();
			tokens = s.trim().split(" ");
			// System.out.println(Arrays.asList(tokens));
			int nesting = (s.length() - s.trim().length()) / 4;
			while (nesting <= tree.getState(parents.peek()).nesting) {
				parents.pop();
			}
			tree.addRelativePath(tokens[0], Integer.parseInt(tokens[1]),
					parents.peek());
			parents.push(tree.size() - 1);
		}
		return tree;
	}

	FolderTree readAcm1(Scanner in) {
		FolderTree tree = new FolderTree();
		int n = in.nextInt();
		String[] names = new String[n];
		int[] ids = new int[n];
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < n; ++i) {
			names[i] = in.next();
			ids[i] = in.nextInt();
			map.put(ids[i], i);
		}
		tree.addRelativePath(names[0], 0, -1);
		for (int i = 0; i < n; ++i) {
			int k = in.nextInt();
			for (int j = 0; j < k; ++j) {
				int toId = in.nextInt();
				int to = map.get(toId);
				tree.addRelativePath(names[to], toId,
						tree.mapPosById.get(ids[i]));
			}
		}
		return tree;
	}

	FolderTree readAcm2(Scanner in) {
		FolderTree tree = new FolderTree();
		int n = in.nextInt();
		String[] names = new String[n];
		int[] ids = new int[n];
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < n; ++i) {
			names[i] = in.next();
			ids[i] = in.nextInt();
			map.put(ids[i], i);
		}
		tree.addRelativePath(names[0], 0, -1);
		for (int i = 0; i < n; ++i) {
			int parId = in.nextInt();
			if (parId == -1) {
				continue;
			}
			int parPos = tree.mapPosById.get(parId);
			tree.addRelativePath(names[i], ids[i], parPos);
		}
		return tree;
	}

	FolderTree readAcm3(Scanner in) {
		FolderTree tree = new FolderTree();
		int n = in.nextInt();
		String[] names = new String[n];
		int[] ids = new int[n];
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (int i = 0; i < n; ++i) {
			names[i] = in.next();
			ids[i] = in.nextInt();
			map.put(ids[i], i);
		}
		tree.addRelativePath(names[0], 0, -1);
		for (int i = 0; i < n - 1; ++i) {
			int frId = in.nextInt();
			int toId = in.nextInt();
			int to = map.get(toId);
			int frPos = tree.mapPosById.get(frId);
			tree.addRelativePath(names[to], toId, frPos);
		}
		return tree;
	}

	FolderTree readXML(Scanner in) {
		class Parser {
			String parseType(String tag) {
				if (tag.charAt(1) == 'd') {
					return "dir";
				}
				return "file";
			}

			String parseName(String tag) {
				int fo = tag.indexOf('\'');
				int so = tag.indexOf('\'', fo + 1);
				return tag.substring(fo + 1, so);
			}

			int parseId(String tag) {
				int lo = tag.lastIndexOf('\'');
				int prelo = tag.lastIndexOf('\'', lo - 1);
				return Integer.parseInt(tag.substring(prelo + 1, lo));
			}

			boolean isCloseTag(String tag) {
				return tag.charAt(1) == '/';
			}
		}

		Parser p = new Parser();
		
		FolderTree tree = new FolderTree();
		String s = in.nextLine();
		tree.addRelativePath(p.parseName(s), p.parseId(s), -1);
		Stack<Integer> parents = new Stack<Integer>();
		parents.push(0);
		while (!parents.empty()) {
			s = in.nextLine();
			s = s.trim();
			if (p.isCloseTag(s)) {
				parents.pop();
				continue;
			}
			String name = p.parseName(s);
			int id = p.parseId(s);
			tree.addRelativePath(name, id, parents.peek());
			if (p.parseType(s).equals("dir")) {
				parents.push(tree.mapPosById.get(id));
			}
		}
		return tree;
	}

	void printFind(FolderTree tree, PrintStream out) {
		SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for (int i = 0; i < tree.size(); ++i) {
			map.put(tree.getState(i).id, i);
		}
		out.println(tree.size());
		for (Entry<Integer, Integer> e : map.entrySet()) {
			FolderTree.State st = tree.getState(e.getValue());
			out.println(st.fullName() + " " + st.id);
		}
	}

	void printPyth(final FolderTree tree, final PrintStream out) {
		class Printer {
			void print(FolderTree.State st) {
				for (int i = 0; i < st.nesting; ++i) {
					out.print("    ");
				}
				out.println(st.name + " " + st.id);
				for (int i = 0; i < st.succs.size(); ++i) {
					print(tree.getState(st.succs.get(i)));
				}
			}
		}
		out.println(tree.size());
		new Printer().print(tree.getState(0));
	}

	SortedMap<Integer, Integer> printAcm(FolderTree tree, PrintStream out) {
		SortedMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for (int i = 0; i < tree.size(); ++i) {
			map.put(tree.getState(i).id, i);
		}
		out.println(tree.size());
		for (Entry<Integer, Integer> e : map.entrySet()) {
			FolderTree.State st = tree.getState(e.getValue());
			out.println(st.name + " " + st.id);
		}
		return map;
	}
	
	void printAcm1(FolderTree tree, PrintStream out) {
		SortedMap<Integer, Integer> map = printAcm(tree, out);
		for (Entry<Integer, Integer> e : map.entrySet()) {
			FolderTree.State st = tree.getState(e.getValue());
			out.print(st.succs.size() + " ");
			for (int i = 0; i < st.succs.size(); ++i) {
				out.print(tree.getState(st.succs.get(i)).id + " ");
			}
			out.println();
		}
	}

	void printAcm2(FolderTree tree, PrintStream out) {
		SortedMap<Integer, Integer> map = printAcm(tree, out);
		for (Entry<Integer, Integer> e : map.entrySet()) {
			FolderTree.State st = tree.getState(e.getValue());
			if (st.parent == -1) {
				out.println(-1);
			} else {
				out.println(tree.getState(st.parent).id);
			}
		}
	}

	void printAcm3(FolderTree tree, PrintStream out) {
		SortedMap<Integer, Integer> map = printAcm(tree, out);
		for (Entry<Integer, Integer> e : map.entrySet()) {
			FolderTree.State st = tree.getState(e.getValue());
			for (int i = 0; i < st.succs.size(); ++i) {
				FolderTree.State to = tree.getState(st.succs.get(i));
				out.println(st.id + " " + to.id);
			}
		}
	}

	void printXML(final FolderTree tree, final PrintStream out) {		
		class Printer {
			void print(FolderTree.State st) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < st.nesting * 2; ++i) {
					sb.append(" ");
				}				
				if (st.succs.size() == 0) {					
					out.println(sb.toString() + "<file name=\'" + st.name + "\' id=\'" + st.id + "\'/>");
				} else {
					out.println(sb.toString() + "<dir name=\'" + st.name + "\' id=\'" + st.id + "\'>");
					for (int i = 0; i < st.succs.size(); ++i) {
						print(tree.getState(st.succs.get(i)));
					}
					out.println(sb.toString() + "</dir>");
				}
			}
		}
		new Printer().print(tree.getState(0));
	}
	
	void solve() throws FileNotFoundException {
		Scanner in = new Scanner(System.in);
		String f1 = in.next();
		String f2 = in.next();
		in.nextLine();		
		FolderTree tree = new FolderTree();
		if (f1.equals("find")) {
			tree = readFind(in);
		}
		if (f1.equals("python")) {
			tree = readPyth(in);
		}
		if (f1.equals("acm1")) {
			tree = readAcm1(in);
		}
		if (f1.equals("acm2")) {
			tree = readAcm2(in);
		}
		if (f1.equals("acm3")) {
			tree = readAcm3(in);
		}
		if (f1.equals("xml")) {
			tree = readXML(in);
		}
		
		
		if (f2.equals("find")) {
			printFind(tree, System.out);
		}
		if (f2.equals("python")) {
			printPyth(tree, System.out);
		}
		if (f2.equals("acm1")) {
			printAcm1(tree, System.out);
		}
		if (f2.equals("acm2")) {
			printAcm2(tree, System.out);
		}
		if (f2.equals("acm3")) {
			printAcm3(tree, System.out);
		}
		if (f2.equals("xml")) {
			printXML(tree, System.out);
		}
	}

	public void run() throws IOException {
		try {
			solve();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			new FormatConverter().run();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
