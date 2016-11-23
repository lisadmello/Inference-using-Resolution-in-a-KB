
package resolution;

import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HashTableStorage {
	Map<String, List<HashTableColumns>> hashmap = new HashMap<String, List<HashTableColumns>>();
	private String[] resolution_input;
	private int n;

	public HashTableStorage(String[] resolution_input, int n) {
		this.resolution_input = resolution_input;
		this.n = n;
	}

	public void tell(String s1) {
		int flagand = 0, flagor = 0;
		String key = "";
		for (int i = 0; i < s1.length(); i++)
			if (s1.charAt(i) == '&')
				flagand = 1;
		for (int i = 0; i < s1.length(); i++)
			if (s1.charAt(i) == '|')
				flagor = 1;
		if (flagand == 0 && flagor == 0) {
			key = "";
			int flagnegation = 0;
			for (int i = 0; i < s1.length(); i++)
				if (s1.charAt(i) == '~')
					flagnegation = 1;
			int r = 1;
			if (flagnegation == 1)
				r = 1;
			else
				r = 0;
			for (int i = r; i < s1.length(); i++) {
				if (!Character.isUpperCase(s1.charAt(i)) && !Character.isLowerCase(s1.charAt(i))
						&& !Character.isDigit(s1.charAt(i)))
					break;
				else
					key = key + s1.charAt(i);
			}
			if (flagnegation == 1)
				add(key, null, s1, s1);
			else
				add(key, s1, null, s1);
		} else if (flagand == 0 && flagor != 0) {
			key = "";
			for (int i = 0; i < s1.length(); i++) {
				if (s1.charAt(i) == '(') {
					int c = 0;
					for (int j = i + 1; j < s1.length(); j++) {
						if (s1.charAt(j) == '(')
							c++;
						if (s1.charAt(j) == ')')
							c--;
						if (c < 0) {
							s1 = s1.substring(i + 1, j);
							break;
						}
					}
					break;
				}
			}
			for (int i = 0; i < s1.length(); i++) {
				int temp = -1;
				if (s1.charAt(i) == '~') {
					for (int j = i + 1; j < s1.length(); j++) {
						if (Character.isUpperCase(s1.charAt(j))) {
							key = "";
							for (int k = j; k < s1.length(); k++) {
								if (!Character.isUpperCase(s1.charAt(k)) && !Character.isLowerCase(s1.charAt(k))
										&& !Character.isDigit(s1.charAt(i))) {
									temp = k;
									break;
								} else
									key = key + s1.charAt(k);
							}
							break;
						}
					}
					int c1 = 0, flag = 0;
					for (int m = temp; m < s1.length(); m++) {
						if (s1.charAt(m) == '(') {
							c1++;
							flag = 1;
						}
						if (s1.charAt(m) == ')')
							c1--;
						if (c1 == 0 && flag == 1) {
							add(key, null, s1.substring(i, m + 1), s1);
							i = m;
							break;
						}
					}
				} else if (Character.isUpperCase(s1.charAt(i))) {
					key = "";
					for (int k = i; k < s1.length(); k++) {
						if (!Character.isUpperCase(s1.charAt(k)) && !Character.isLowerCase(s1.charAt(k))
								&& !Character.isDigit(s1.charAt(i))) {
							temp = k;
							break;
						} else
							key = key + s1.charAt(k);
					}
					int c1 = 0, flag = 0;
					for (int m = temp; m < s1.length(); m++) {
						if (s1.charAt(m) == '(') {
							c1++;
							flag = 1;
						}
						if (s1.charAt(m) == ')')
							c1--;
						if (c1 == 0 && flag == 1) {
							add(key, s1.substring(i, m + 1), null, s1);
							i = m;
							break;
						}
					}
				}
			}
		} else if (flagor == 0 && flagand != 0) {
			ArrayList<String> clauses = new ArrayList<String>();
			for (int i = 0; i < s1.length(); i++) {
				String temp1 = "";
				int c = 0, flag = 0;
				if (s1.charAt(i) == '~' || Character.isUpperCase(s1.charAt(i))) {
					for (int j = i; j < s1.length(); j++) {
						temp1 = temp1 + s1.charAt(j);
						if (s1.charAt(j) == '(') {
							c++;
							flag = 1;
						}
						if (s1.charAt(j) == ')')
							c--;
						if (c == 0 && flag == 1) {
							i = j;
							break;
						}
					}
					clauses.add(temp1);
				}
			}
			for (int i = 0; i < clauses.size(); i++) {
				tell(clauses.get(i));
			}
		} else if (flagor != 0 && flagand != 0) {
			for (int i = 0; i < s1.length(); i++) {
				int start = -1, end = -1;
				if (s1.charAt(i) == '&') {
					for (int j = i - 1; j >= 0; j--) {
						if (s1.charAt(j) == ')') {
							end = j;
							break;
						}
					}
					int c = 0;
					for (int k = end - 1; k >= 0; k--) {
						if (s1.charAt(k) == ')')
							c++;
						if (s1.charAt(k) == '(')
							c--;
						if (c < 0) {
							start = k + 1;
							tell(s1.substring(start - 1, end + 1));
							break;
						}
					}
					for (int j = i + 1; j < s1.length(); j++) {
						if (s1.charAt(j) == '(') {
							start = j + 1;
							break;
						}
					}
					c = 0;
					for (int k = start; k < s1.length(); k++) {
						if (s1.charAt(k) == '(')
							c++;
						if (s1.charAt(k) == ')')
							c--;
						if (c < 0) {
							end = k;
							tell(s1.substring(start - 1, end + 1));
							i = k;
							break;
						}
					}

				}
			}
		}
	}

	public void add(String key, String positive, String negative, String sentence) {
		HashTableColumns h1 = new HashTableColumns(positive, negative, sentence);
		if (hashmap.containsKey(key)) {
			hashmap.get(key).add(h1);
			for (int i = 1; i < hashmap.get(key).size(); i++)
				System.out.println("key:" + key + " " + hashmap.get(key).get(i).sentence);
		} else {
			List<HashTableColumns> htc = Collections.synchronizedList(new ArrayList<HashTableColumns>());
			htc.add(h1);
			hashmap.put(key, htc);
			System.out.println("key:" + key + " " + hashmap.get(key).get(0).sentence);
		}
	}
}
