import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class SuffAutoTest {
	
	@Test
	public void testInput() {
		String[] s = {"abacaba", "mycabarchive", "acabistrue"};
		String ans = SuffAutomata.LCS(s);
		assertEquals("cab", ans);
	}

	@Test
	public void testSmall() {
		String[] s1 = {"a", "a", "a"};
		String ans = SuffAutomata.LCS(s1);
		assertEquals("a", ans);
				
		String[] s2 = {"ab", "a", "ac", "asdfsdfgsdfg", "qwerqwerqwera", "a", "a", "a", "a", "a"};
		ans = SuffAutomata.LCS(s2);
		assertEquals("a", ans);
		
		String[] s3 = {"abcd", "dabc", "abcde", "abct", "aaaabcdddd"};
		ans = SuffAutomata.LCS(s3);
		assertEquals("abc", ans);
		
		String[] s4 = {"cabcab", "abcabcabc", "dcabcabcd", "cabcat", "aaacabcadddd"};
		ans = SuffAutomata.LCS(s4);
		assertEquals("cabca", ans);
		
		String[] s5 = {"abbbbbbbbbbbbbbb", "bb", "b", "bccccccccccccccccccccccccccccd"};
		ans = SuffAutomata.LCS(s5);
		assertEquals("b", ans);
	}
	
	@Test
	public void testEmpty() {
		String[] s1 = {"a", "b", "a"};
		String ans = SuffAutomata.LCS(s1);
		assertEquals("", ans);
		
		String[] s2 = {"a", "bc", "de", "zz"};
		ans = SuffAutomata.LCS(s2);
		assertEquals("", ans);
		
		String[] s3= {"a", "ab", "bc", "cd", "de", "efg", "fghi"};
		ans = SuffAutomata.LCS(s3);
		assertEquals("", ans);
	}
	
	@Test
	public void testWhole() {
		String[] s1 = {"qwerqwerqwerabcdzvcncvncvbn", "abcdz", "zxcvzxvcabcdzzcxvzxcv", "yuoyupoabcdzxxx"};
		String ans = SuffAutomata.LCS(s1);
		assertEquals("abcdz", ans);
	}
	
	@Test
	public void testPerformance1() {
		String[] s = new String[10];
		for (int i = 0; i < 10; ++i) {
			StringBuilder sb = new StringBuilder();		
			for (int j = 0; j < 10000; ++j) {
				sb.append((char)('a' + i));
			}
			s[i] = sb.toString();
		}
		String ans = SuffAutomata.LCS(s);
		assertEquals("", ans);
	}
	
	@Test
	public void testPerformance2() {
		String[] s = new String[10];
		for (int i = 0; i < 10; ++i) {
			StringBuilder sb = new StringBuilder();		
			for (int j = 0; j < 10000; ++j) {
				sb.append('a');
			}
			s[i] = sb.toString();
		}
		String ans = SuffAutomata.LCS(s);
		assertEquals(s[0], ans);
	}
	
	@Test
	public void testPerformance3() {
		List<Character> start = new ArrayList<Character>();
		for (char c = 'a'; c <= 'z'; ++c) {
			start.addAll(Collections.nCopies(384, c));
		}
		String[] strings = new String[10];
		for (int i = 0; i < 10; ++i) {
			Collections.shuffle(start);
			StringBuilder s = new StringBuilder();
			for (int j = 0; j < start.size(); ++j) {
				s.append(start.get(j));
			}
			strings[i] = s.toString();
		}
		assertEquals("dvn", SuffAutomata.LCS(strings));
	}
	
	@Test
	public void testPerformance4() {
		String[] s = new String[10];
		for (int i = 0; i < 10; ++i) {
			StringBuilder sb = new StringBuilder("a");		
			for (int j = 1; j < 10000; ++j) {
				sb.append((char)('b' + i));
			}
			s[i] = sb.toString();
		}
		String ans = SuffAutomata.LCS(s);
		assertEquals("a", ans);
	}

}
