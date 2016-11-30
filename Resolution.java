package resolution;

import java.util.*;

public class Resolution {
	private HashTableStorage h;

	public Resolution(HashTableStorage h) {
		this.h = h;
	}

	public String resolve() {
		int flag = 0;
		String goal_state = "blank space", result = "";
		String goal = h.kb.get(h.kb.size() - 1);
		State s = new State((h.kb.get(h.kb.size() - 1)), null, false);
		s.type = 'p';
		s.knowlegde = h.kb;
		s.statehashmap = h.hashmap;
		List<State> dfsstack = Collections.synchronizedList(new ArrayList<State>());
		dfsstack.add(s);
		s.explored = true;
		while (flag == 0 && !dfsstack.isEmpty()) {
			State a = dfsstack.remove(0);
			if (a.sentence.equals(goal_state)) {
				result = "TRUE";
				flag = 1;
				break;
			} else if (a.type == 'p') {
				List<Integer> negation = new ArrayList<>();
				List<String> keys = new ArrayList<>();
				String key = "";
				int f = 0;
				for (int i = 0; i < a.sentence.length();) {
					if (a.sentence.charAt(i) == '(') {
						keys.add(key);
						key = "";
						for (int j = i + 1; j < a.sentence.length();) {
							if (a.sentence.charAt(j) == ')') {
								i = j + 1;
								f = 0;
								break;
							} else {
								j++;
							}
						}
					} else if (a.sentence.charAt(i) == '~') {
						negation.add(1);
						f = 1;
						i++;
						continue;
					} else if (Character.isUpperCase(a.sentence.charAt(i))
							|| Character.isLowerCase(a.sentence.charAt(i))) {
						key = key + a.sentence.charAt(i);
						if (Character.isUpperCase(a.sentence.charAt(i)) && f == 0) {
							negation.add(0);
						}
						i++;
					} else {
						i++;
					}
				}
				for (int m = 0; m < keys.size(); m++) {
					for (int i = 0; i < a.statehashmap.get(keys.get(m)).size(); i++) {
						if (negation.get(m) == 1) {
							int count = 1;
							for (int k1 = 0; k1 < a.children.size(); k1++) {
								if (a.children.get(k1).sentence
										.equals(a.statehashmap.get(keys.get(m)).get(i).sentence)) {
									count++;
								}
							}
							if (a.statehashmap.get(keys.get(m)).get(i).sentence.equals(a.sentence)) {
								continue;
							}
							int con = 0;
							for (int q = 0; q < a.knowlegde.size(); q++) {
								if (a.knowlegde.get(q).contains(a.statehashmap.get(keys.get(m)).get(i).sentence))
									con = 1;
							}
							if (a.statehashmap.get(keys.get(m)).get(i).positive != null && con == 1) {

								State s1 = new State(a.statehashmap.get(keys.get(m)).get(i).sentence, a, false);
								s1.knowlegde = a.knowlegde;
								s1.parent = a;
								s1.type = 'c';
								s1.statehashmap = a.statehashmap;
								s1.keyword = keys.get(m);
								s1.explored = true;
								s1.id = count;
								dfsstack.add(0, s1);
								a.addChildren(s1);
							}
						} else {
							int count = 1;
							for (int k1 = 0; k1 < a.children.size(); k1++) {
								if (a.children.get(k1).sentence
										.equals(a.statehashmap.get(keys.get(m)).get(i).sentence)) {
									count++;
								}
							}
							if (a.statehashmap.get(keys.get(m)).get(i).sentence.equals(a.sentence)) {
								continue;
							}
							int con = 0;
							for (int q = 0; q < a.knowlegde.size(); q++) {
								if (a.knowlegde.get(q).contains(a.statehashmap.get(keys.get(m)).get(i).sentence))
									con = 1;
							}
							if (a.statehashmap.get(keys.get(m)).get(i).negative != null && con == 1) {
								State s2 = new State(a.statehashmap.get(keys.get(m)).get(i).sentence, a, false);
								s2.knowlegde = a.knowlegde;
								s2.parent = a;
								s2.type = 'c';
								s2.statehashmap = a.statehashmap;
								s2.explored = true;
								s2.keyword = keys.get(m);
								s2.id = count;
								a.addChildren(s2);
								dfsstack.add(0, s2);
							}
						}
					}
				}
			} else if (a.type == 'c') {
				int index = 0;
				//find all positive occurrences in sentence of a 
				int countchildpos=0,countchildneg=0;
				int parneg=-1,parpos=-1,chipos=-1,chineg=-1;
				int ind=a.sentence.indexOf(a.keyword+"(");
				if(ind==0)
				{
					chipos=ind;
					countchildpos++;
				}
				else if(ind>0)
					if(a.sentence.charAt(ind-1)=='~')
					{
						chineg=ind-1;
						countchildneg++;
					}
					else
					{
						chipos=ind;
						countchildpos++;
					}
				while(ind>=0)
				{
					ind=a.sentence.indexOf(a.keyword+"(",ind+1);
					if(ind!=-1)
					{
						if(ind>0)
							if(a.sentence.charAt(ind-1)=='~')
							{
								chineg=ind-1;
								countchildneg++;
							}
							else
							{
								chipos=ind;
								countchildpos++;
							}
					}
				}
				
				int countparpos=0,countparneg=0;
				int ind1=a.parent.sentence.indexOf(a.keyword+"(");
				if(ind1==0)
				{
					parpos=ind1;
					countparpos++;
				}
				else if(ind1>0)
					if(a.parent.sentence.charAt(ind1-1)=='~')
					{
						parneg=ind1-1;
						countparneg++;
					}
					else
					{
						parpos=ind1;
						countparpos++;
					}
				while(ind1>=0)
				{
					ind1=a.parent.sentence.indexOf(a.keyword+"(",ind1+1);
					if(ind1!=-1)
					{
						if(ind1>0)
							if(a.parent.sentence.charAt(ind1-1)=='~')
							{
								parneg=ind1-1;
								countparneg++;
							}
							else
							{
								parpos=ind1;
								countparpos++;
							}
					}
				}
				
				

				String listchild = "",listparent = "";
				if(countparpos==1 && countchildneg==1)
				{
					
					for (int x = chineg; x < a.sentence.length(); x++) {
						if (a.sentence.charAt(x) == '(') {
							for (int m = x + 1; m < a.sentence.length(); m++) {
								if (a.sentence.charAt(m) == ')')
									break;
								listchild = listchild + a.sentence.charAt(m);
							}
							break;
						}
					}
					
					for (int y = parpos; y < a.parent.sentence.length(); y++) {
						if (a.parent.sentence.charAt(y) == '(') {
							for (int m = y + 1; m < a.parent.sentence.length(); m++) {
								if (a.parent.sentence.charAt(m) == ')')
									break;
								listparent = listparent + a.parent.sentence.charAt(m);
							}
							break;
						}
					}
				}
				
				else if(countparneg==1 && countchildpos==1)
				{
					listparent="";
					listchild="";
					for (int x = chipos; x < a.sentence.length(); x++) {
						if (a.sentence.charAt(x) == '(') {
							for (int m = x + 1; m < a.sentence.length(); m++) {
								if (a.sentence.charAt(m) == ')')
									break;
								listchild = listchild + a.sentence.charAt(m);
							}
							break;
						}
					}
					
					for (int y = parneg; y < a.parent.sentence.length(); y++) {
						if (a.parent.sentence.charAt(y) == '(') {
							for (int m = y + 1; m < a.parent.sentence.length(); m++) {
								if (a.parent.sentence.charAt(m) == ')')
									break;
								listparent = listparent + a.parent.sentence.charAt(m);
							}
							break;
						}
					}
				}else
				 if ((countchildpos>=2 || countchildneg!=2) && (countparpos==1 || countparpos==1))
						{
				
					for (int i = 0; i < a.id; i++) {
						int x = a.sentence.indexOf(a.keyword + "(", index);
						index = x + 1;
					}
					index--;
					index = index + a.keyword.length();
					listchild = "";
					for (int x = index; x < a.sentence.length(); x++) {
						if (a.sentence.charAt(x) == '(') {
							for (int m = x + 1; m < a.sentence.length(); m++) {
								if (a.sentence.charAt(m) == ')')
									break;
								listchild = listchild + a.sentence.charAt(m);
							}
							break;
						}
					}
					index = 0;
					int x = a.parent.sentence.indexOf(a.keyword + "(");
					index = x + a.keyword.length();
					listparent = "";
					for (int y = index; y < a.parent.sentence.length(); y++) {
						if (a.parent.sentence.charAt(y) == '(') {
							for (int m = y + 1; m < a.parent.sentence.length(); m++) {
								if (a.parent.sentence.charAt(m) == ')')
									break;
								listparent = listparent + a.parent.sentence.charAt(m);
							}
							break;
						}
					}
						}
				ArrayList<String> substitution = new ArrayList<>();
				substitution = unify(listparent, listchild, substitution);
				if (substitution.contains("failure")) {
					continue;
				} else {
					String newchild = a.sentence;
					String newparent = a.parent.sentence;
					if (!substitution.isEmpty())
						for (int i = 0; i < substitution.size(); i++) {
							int f = 0;
							String sub = "", val = "";
							for (int j = 0; j < substitution.get(i).length(); j++) {
								if (substitution.get(i).charAt(j) == '/') {
									f = 1;
									continue;
								}
								if (f == 0)
									sub = sub + substitution.get(i).charAt(j);
								if (f == 1)
									val = val + substitution.get(i).charAt(j);
							}
							newchild = newchild.replaceAll(sub, val);
							newparent = newparent.replaceAll(sub, val);
						}
					List<String> newchild1 = new ArrayList<>();
					List<String> newparent1 = new ArrayList<>();
					for (int g = 0; g < newchild.length(); g++) {
						String parser = "";
						if (newchild.charAt(g) == '~' || Character.isUpperCase(newchild.charAt(g))) {
							for (int j = g; j < newchild.length(); j++) {
								parser = parser + newchild.charAt(j);
								if (newchild.charAt(j) == ')') {
									g = j;
									break;
								}
							}
							parser = parser.trim();
							newchild1.add(parser);
						}
					}
					for (int g = 0; g < newparent.length(); g++) {
						String parser = "";
						if (newparent.charAt(g) == '~' || Character.isUpperCase(newparent.charAt(g))) {
							for (int j = g; j < newparent.length(); j++) {
								parser = parser + newparent.charAt(j);
								if (newparent.charAt(j) == ')') {
									g = j;
									break;
								}
							}
							parser = parser.trim();
							newparent1.add(parser);
						}
					}

					for (int i = 0; i < newchild1.size();i++) {

						for (int j = 0; j < newparent1.size(); j++) {
							if (("~" + newchild1.get(i)).equals(newparent1.get(j))
									|| ("~" + newparent1.get(j)).equals(newchild1.get(i))) {
								newchild1.remove(i);
								{
									if(i>0)
										i--;
								}
								newparent1.remove(j);
								{
									j--;
								}
								if (newparent1.size() == 0 || newchild1.size() == 0)
									break;

							}
						}
						if (newparent1.size() == 0 || newchild1.size() == 0)
							break;
					}
					String merge = "";
					if (newchild1.isEmpty() && newparent1.isEmpty()) {
						State s3 = new State("blank space", null, false);
						dfsstack.add(0, s3);
					} else if (newchild1.isEmpty() && newparent1.size() != 0) {
						for (int i = 0; i < newparent1.size(); i++) {
							if (i > 0)
								merge = merge + " | " + newparent1.get(i);
							else
								merge = merge + newparent1.get(i);
						}
					} else if (newparent1.isEmpty() && newchild1.size() != 0) {
						for (int i = 0; i < newchild1.size(); i++) {
							if (i > 0)
								merge = merge + " | " + newchild1.get(i);
							else
								merge = merge + newchild1.get(i);
						}
					} else {
						for (int i = 0; i < newchild1.size(); i++) {
							if (i > 0)
								merge = merge + " | " + newchild1.get(i);
							else
								merge = merge + newchild1.get(i);
						}
						for (int i = 0; i < newparent1.size(); i++) {
							merge = merge + " | " + newparent1.get(i);
						}
					}
					List<String> norepeat = new ArrayList<>();
					for(int l=0;l<merge.length();l++)
					{
						if(merge.charAt(l)=='~' || Character.isUpperCase(merge.charAt(l)))
						{
							String temp="";
							for(int j=l;j<merge.length();j++)
							{
								temp= temp+merge.charAt(j);
								if(merge.charAt(j)==')')
								{
									if(!norepeat.contains(temp))
										norepeat.add(temp);
									l=j;
									break;
								}
							}
								
						}
					}
					merge="";
					if(norepeat.size()==1)
						merge=merge+norepeat.get(0);
					if(norepeat.size()>1)
					for(int l=0;l<norepeat.size()-1;l++)
					merge=merge+norepeat.get(l)+" | ";
					if(norepeat.size()>1)
						merge=merge+norepeat.get(norepeat.size()-1);
//					System.out.println(merge);
//					System.out.println("sen1=" + a.sentence);
//					System.out.println("sen2=" + a.parent.sentence);
//					System.out.println("merged=" + merge);
//					System.out.println("\n");
					State s4 = new State(merge, a, false);
					s4.explored = true;
					s4.type = 'p';
					s4.merger1 = a.sentence;
					s4.merger2 = a.parent.sentence;
					s4.knowlegde = a.parent.knowlegde;
					s4.knowlegde.remove(a.parent.sentence);
					s4.knowlegde.remove(a.sentence);
					s4.knowlegde.add(merge);
					List<String> d = new ArrayList<>();
					for (int i = 0; i < s4.knowlegde.size(); i++) {
						d.add(s4.knowlegde.get(i));
					}
					for (int i = 0; i < s4.knowlegde.size(); i++) {
						for (int j = 0; j < s4.knowlegde.get(i).length(); j++)
							if (s4.knowlegde.get(i).charAt(j) == '|') {
								String temp = s4.knowlegde.get(i);
								temp = "(" + temp + ")";
								s4.knowlegde.remove(i);
								s4.knowlegde.add(i, temp);
								break;
							}
					}

					HashTableStorage h2 = new HashTableStorage();
					for (int i = 0; i < s4.knowlegde.size(); i++) {
						h2.tell(s4.knowlegde.get(i));
					}
					s4.knowlegde = d;
					s4.statehashmap = h2.hashmap;

					dfsstack.add(0, s4);
				}
			}
		}
		if (flag == 0) {
			result = "FALSE";
		}
		return result;
	}

	public boolean isNegation(String a) {
		for (int i = 0; i < a.length(); i++)
			if (a.charAt(i) == '~') {
				return true;
			}
		return false;
	}

	public String getKey(String a) {
		String key = "";
		int flagnegation = 0;
		for (int i = 0; i < a.length(); i++)
			if (a.charAt(i) == '~')
				flagnegation = 1;
		int r = 1;
		if (flagnegation == 1)
			r = 1;
		else
			r = 0;
		for (int i = r; i < a.length(); i++) {
			if (!Character.isUpperCase(a.charAt(i)) && !Character.isLowerCase(a.charAt(i))
					&& !Character.isDigit(a.charAt(i)))
				break;
			else
				key = key + a.charAt(i);
		}
		return key;
	}

	public ArrayList<String> unify(String x, String y, ArrayList<String> theta) {
		if (theta.contains("failure"))
			return theta;
		else if (x.equals(y))
			return theta;
		else if (isVariable(x))
			return unifyVar(x, y, theta);
		else if (isVariable(y))
			return unifyVar(y, x, theta);
		else if (isList(x) && isList(y)) {
			return unify(rest(x), rest(y), unify(first(x), first(y), theta));
		} else {
			theta.add("failure");
			return theta;
		}

	}

	public ArrayList<String> unifyVar(String var, String x, ArrayList<String> theta) {
		List<String> variables = new ArrayList<>();
		List<String> values = new ArrayList<>();
		for (int i = 0; i < theta.size(); i++) {
			String left = "", right = "";
			int flag = 0;
			for (int j = 0; j < theta.get(i).length(); j++) {
				if (theta.get(i).charAt(j) == '/')
					flag = 1;
				if (flag == 0)
					left = left + theta.get(i).charAt(j);
				else
					right = right + theta.get(i).charAt(j);
			}
			variables.add(left);
			values.add(right);
		}
		if (variables.contains(var)) {
			int in = -1;
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).equals(var)) {
					in = j;
					break;
				}
			}
			return unify(values.get(in), x, theta);
		} else if (variables.contains(x)) {
			int in = -1;
			for (int j = 0; j < variables.size(); j++) {
				if (variables.get(j).equals(x)) {
					in = j;
					break;
				}
			}
			return unify(var, values.get(in), theta);
		} else {
			String c = var + "/" + x;
			theta.add(c);
			return theta;
		}
	}

	public boolean isVariable(String a) {
		int flag = 0;
		for (int i = 0; i < a.length(); i++)
			if (!Character.isLowerCase(a.charAt(i)) && a.charAt(i) != ' ' && !Character.isDigit(a.charAt(i))) {
				flag = 1;
				break;
			}
		if (flag == 0)
			return true;
		else
			return false;
	}

	public boolean isList(String a) {
		int f = 0;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) == ',') {
				f = 1;
				break;
			}
		}
		if (f == 0)
			return false;
		else
			return true;
	}

	public String first(String a) {
		String firstarg = "";
		for (int i = 0; i < a.length(); i++)
			if (a.charAt(i) != ',')
				firstarg = firstarg + a.charAt(i);
			else
				break;
		return firstarg;
	}

	public String rest(String a) {
		String restargs = "";
		int m = -1;
		for (int i = 0; i < a.length(); i++)
			if (a.charAt(i) == ',') {
				m = i + 1;
				break;
			}
		for (int i = m; i < a.length(); i++) {
			restargs = restargs + a.charAt(i);
		}
		return restargs;
	}

}
