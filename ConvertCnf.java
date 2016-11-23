package resolution;

import java.util.ArrayList;
import java.util.List;

public class ConvertCnf {
	private String[] resolution_input;
	private int n;
	public String[] kb;
	public int count=0;

	public ConvertCnf(String[] resolution_input, int n) {
		this.resolution_input = resolution_input;
		this.n = n;
	}

	public void computeCnf() {
		int nq = Integer.parseInt(resolution_input[0]);
		
		HashTableStorage h1 = new HashTableStorage(resolution_input,n);
		for (int i = (2 + nq); i < resolution_input.length; i++) {
			count++;
			// System.out.println(resolution_input[i]);
			String step1 = implicationElimination(resolution_input[i]);
			String step2 = negationElimination(step1);
			String step21 = removeRedundantBraces(step2);
			// System.out.println(step21);
			String step4 = distributeAndOverOr(step21);
			String step5 = standardize(step4);
			 System.out.println(step5);
			h1.tell(step5);
		}
		

	}

	public String standardize(String s1) {
		String s2 = "";
		int flagand = 0, flagor = 0,andcount=0;
		for (int i = 0; i < s1.length(); i++)
			if (s1.charAt(i) == '&')
			{
				flagand = 1;
				andcount++;
			}
		for (int i = 0; i < s1.length(); i++)
			if (s1.charAt(i) == '|')
				flagor = 1;
		if (flagand == 0 && flagor == 0) {
			int flag=0,flag1=0;
			for(int j=0;j<s1.length();)
			{
				if(Character.isLowerCase(s1.charAt(j)))
				{
					if(Character.isUpperCase(s1.charAt(j-1)))
					{
						flag1=1;
					}
					flag=1;
					j++;
					continue;
				}
				if((s1.charAt(j)==')' || s1.charAt(j)==',') && flag==1 && flag1!=1)
				{
					int temp=s1.length();
					s1=s1.substring(0,j)+count+s1.substring(j,temp);
					j=j+2;
					s2=s1;
				}
				else
					j++;
			}
		}
		else if (flagand == 0 && flagor == 1)
		{
			int flag=0;
			for(int j=0;j<s1.length();)
			{
				if(Character.isLowerCase(s1.charAt(j)))
				{
					flag=1;
					j++;
					continue;
				}
				else if((s1.charAt(j)==')' || s1.charAt(j)==',') && flag==1)
				{
					int temp=s1.length();
					s1=s1.substring(0,j)+count+s1.substring(j,temp);
					j=j+2;
					s2=s1;
					flag=0;
				}
				else
					j++;
			}
		}
		else if(flagand == 1 && flagor == 0)
		{
			int flag=0;
			for(int j=0;j<s1.length();)
			{
				if(Character.isLowerCase(s1.charAt(j)))
				{
					flag=1;
					j++;
					continue;
				}
				if((s1.charAt(j)==')' || s1.charAt(j)==',') && flag==1)
				{
					int temp=s1.length();
					if(s1.charAt(j)==',')
					{
					s1=s1.substring(0,j)+count+s1.substring(j,temp);
					}
					if(s1.charAt(j)==')')
					{
					s1=s1.substring(0,j)+count+s1.substring(j,temp);
					count++;
					}
					j=j+2;
					s2=s1;
					flag=0;
				}
				else
					j++;
			}
			count--;
		}
		else if(flagand == 1 && flagor == 1)
		{
			count--;
			for(int i=0;i<s1.length();)
			{
				int s=-1,f=-1;
				if(s1.charAt(i)=='&' && andcount!=0)
				{
					andcount--;
					for(int k=i-1;k>=0;k--)
					{
						if(s1.charAt(k)==')')
						{
							int c=0;
							f=k+1;
							for(int j=k-1;j>=0;j--)
							{
								if(s1.charAt(j)==')')
									c++;
								if(s1.charAt(j)=='(')
									c--;
								if(c<0)
								{
									s=j;
									count++;
									String s3=standardize(s1.substring(s,f));
									int temp=s1.length();
									s3.trim();
									int t=s1.substring(0,s).length()+s3.length();
									s1=s1.substring(0,s)+s3+s1.substring(f,temp);
									for(int m=t;m<s1.length();m++)
										if(s1.charAt(m)=='&')
										{
											i=m+1;
											break;
										}
									break;
								}
							}
							break;
						}
					}
				}
				else if(andcount==0)
				{
					for(int k=i;k<s1.length();k++)
					{
						if(s1.charAt(k)=='(')
						{
							int c=0;
							s=k;
							for(int j=k+1;j<s1.length();j++)
							{
								if(s1.charAt(j)=='(')
									c++;
								if(s1.charAt(j)==')')
									c--;
								if(c<0)
								{
									f=j+1;
									count++;
									String s3=standardize(s1.substring(s,f));
									s3.trim();
									int temp=s1.length();
									s1=s1.substring(0,s)+s3+s1.substring(f,temp);
									i=s1.length();
									break;
								}
							}
							break;
						}
					}
				}
				else
				{
					i++;
				}
			}
		}
		return s1;
	}

	public String implicationElimination(String s1) {
		String b = "", a = "";
		int c = 0, st1 = -1, st2 = -1, fn1 = -1, fn2 = -1;
		for (int i = 0; i < s1.length();) {
			if (s1.charAt(i) == '=' && s1.charAt(i + 1) == '>') {
				for (int j = (i + 2); j < s1.length(); j++)
					if (s1.charAt(j) != ' ') {
						st2 = j;
						break;
					}
				c = 0;
				for (int j = (i + 2); j < s1.length(); j++) {
					if (s1.charAt(j) == '(')
						c++;
					if (s1.charAt(j) == ')')
						c--;
					if (c < 0) {
						fn2 = j;
						break;
					}
				}
				b = s1.substring(st2, fn2);

				c = 0;
				for (int j = (i - 1); j >= 0; j--)
					if (s1.charAt(j) == ')') {
						fn1 = j;
						break;
					}
				for (int j = (i - 1); j >= 0; j--) {
					if (s1.charAt(j) == ')')
						c++;
					if (s1.charAt(j) == '(')
						c--;
					if (c < 0) {
						st1 = (j + 1);
						break;
					}
				}
				a = "(" + "~" + s1.substring(st1, fn1 + 1) + ")";
				int temp = s1.length();
				s1 = s1.substring(0, st1) + a + " | " + b + s1.substring(fn2, temp);
				i = 0;
			} else {
				i++;
			}
		}
		return s1;
	}

	public String negationElimination(String s1) {
		int st = -1, fn = -1, flag = 0;
		for (int i = 0; i < s1.length(); i++) {
			flag = 0;
			if (s1.charAt(i) == '~')
				flag = 1;
			if (flag == 1) {
				for (int j = i + 1; j < s1.length(); j++) {
					if (s1.charAt(j) == ' ')
						continue;
					if (s1.charAt(j) == '(') {
						st = j + 1;
						break;
					} else {
						flag = 0;
						break;
					}
				}
			}
			if (flag == 1) {
				int c = 0;
				for (int j = st; j < s1.length(); j++) {

					if (s1.charAt(j) == '(') {
						c++;
					}
					if (s1.charAt(j) == ')')
						c--;
					if (c < 0) {
						fn = j - 1;
						break;
					}
				}
				int temp = s1.length();
				String s11 = s1.substring(0, i);
				String s22 = s1.substring(fn + 2, temp);
				int a = 0, b = 0;
				a = s11.length();
				String s3 = pushNegation(s1.substring(st, fn + 1));
				s1 = s11 + s3 + s22;
				b = s3.length();
				i = a + b;
			}
		}

		return s1;
	}

	public String distributeAndOverOr(String s1) {
		int flagand = 0, flagor = 0;
		String s2 = "";
		for (int i = 0; i < s1.length(); i++) {
			if (s1.charAt(i) == '&')
				flagand = 1;
		}
		for (int i = 0; i < s1.length(); i++) {
			if (s1.charAt(i) == '|')
				flagor = 1;
		}
		if (flagand == 0 && flagor == 0)
			return s1;
		else if (flagand == 0) {
			s2 = s2 + "(";
			for (int i = 0; i < s1.length();) {
				if (s1.charAt(i) == '~' || Character.isUpperCase(s1.charAt(i))) {
					for (int j = i; j < s1.length(); j++) {
						s2 = s2 + s1.charAt(j);
						if (s1.charAt(j) == ')') {
							i = j + 1;
							break;
						}
					}
				} else if (s1.charAt(i) == '|') {
					s2 = s2 + " | ";
					i++;
				} else {
					i++;
				}
			}
			s2 = s2 + ")";
			s1 = s2;
		} else if (flagor == 0) {
			// s2 = s2 + "(";
			for (int i = 0; i < s1.length();) {
				if (s1.charAt(i) == '~' || Character.isUpperCase(s1.charAt(i))) {
					for (int j = i; j < s1.length(); j++) {
						s2 = s2 + s1.charAt(j);
						if (s1.charAt(j) == ')') {
							i = j + 1;
							break;
						}
					}
				} else if (s1.charAt(i) == '&') {
					s2 = s2 + " & ";
					i++;
				} else {
					i++;
				}
			}
			// s2 = s2 + ")";
			s1 = s2;

		} else {
			int count1 = 0, count2 = 0, s = -1;
			for (int i = 0; i < s1.length(); i++) {
				count1 = 0;
				count2 = 0;
				if (s1.charAt(i) == '|' || s1.charAt(i) == '&') {
					char sym = s1.charAt(i);
					for (int j = i - 1; j >= 0;) {
						if (s1.charAt(j) == ')') {
							count1++;
							j--;
						} else if (s1.charAt(j) == ' ')
							j--;
						else
							break;
					}
					for (int j = i + 1; j < s1.length();) {
						if (s1.charAt(j) == '(') {
							count2++;
							j++;
						} else if (s1.charAt(j) == ' ')
							j++;
						else
							break;
					}
					if (count1 >= 2 && count2 == 0) {
						int s3 = -1, f1 = -1;
						String complete = "", r = "", l = "";
						for (int j = i + 1; j < s1.length(); j++) {
							r = r + s1.charAt(j);
							if (s1.charAt(j) == ')')
								break;
						}
						int c = 0;
						for (int j = i - 1; j >= 0; j--) {
							if (s1.charAt(j) == ')')
								c++;
							if (s1.charAt(j) == '(')
								c--;
							if (c < 0) {
								s = j + 1;
								l = s1.substring(s, i);
								break;
							}
						}
						int start = -1, end = -1;
						for (int j = 0; j < l.length(); j++) {
							if (s1.charAt(j) == '(') {
								start = j;
								break;
							}
						}
						for (int j = l.length() - 1; j >= 0; j--) {
							if (s1.charAt(j) == ')') {
								end = j;
								break;
							}
						}
						l = l.substring(start + 1, end - 1);
						l.trim();
						complete = complete + "(";
						int flag = 0;
						for (int k = 0; k < l.length();) {

							if (l.charAt(k) == '~') {
								complete = complete + "(";
								complete = complete + l.charAt(k);
								complete = complete + l.charAt(k + 1);
								flag = 0;
								k = k + 2;
							} else if (Character.isUpperCase(l.charAt(k))) {
								complete = complete + "(";
								complete = complete + l.charAt(k);
								flag = 0;
								k = k + 1;
							} else if (l.charAt(k) == ')') {
								if (flag == 1)
									break;
								complete = complete + l.charAt(k);
								complete = complete + " " + sym + r + ")";
								flag = 1;
								k++;
							} else {
								complete = complete + l.charAt(k);
								if (l.charAt(k) != ' ')
									flag = 0;
								k++;
							}

						}
						complete = complete + ")";
						s2 = complete;
						c = 0;
						for (int j = i - 1; j >= 0; j--) {
							if (s1.charAt(j) == ')')
								c++;
							if (s1.charAt(j) == '(')
								c--;
							if (c < 0) {
								s3 = j;
								break;
							}
						}
						c = 0;
						for (int j = i + 1; j < s1.length(); j++) {
							if (s1.charAt(j) == '(')
								c++;
							if (s1.charAt(j) == ')')
								c--;
							if (c < 0) {
								f1 = j;
								break;
							}
						}
						int temp = s1.length();
						s1 = s1.substring(0, s3) + s2 + " " + s1.substring(f1 + 1, temp);
						i = s3 + s2.length();

					}

					else if (count1 == 1 && count2 >= 1) {
						int s3 = -1, f1 = -1;
						String complete = "", r = "", l = "";
						int c = 0;
						for (int j = i - 1; j >= 0; j--) {
							if (s1.charAt(j) == ')')
								c++;
							if (s1.charAt(j) == '(')
								c--;
							if (c < 0) {
								l = s1.substring(j + 1, i);
								break;
							}
						}
						for (int j = i + 1; j < s1.length(); j++) {
							if (s1.charAt(j) == '(') {
								s = j + 1;
								break;
							}
						}
						c = 0;
						for (int j = s; j < s1.length(); j++) {
							if (s1.charAt(j) == '(')
								c++;
							if (s1.charAt(j) == ')')
								c--;
							if (c < 0) {
								r = s1.substring(s, j);
								break;
							}
						}

						l.trim();
						r.trim();
						complete = complete + "(";
						int flag = 0;
						for (int k = 0; k < r.length();) {
							if (r.charAt(k) == '~') {
								complete = complete + "(";
								complete = complete + r.charAt(k);
								complete = complete + r.charAt(k + 1);
								flag = 0;
								k = k + 2;
							} else if (Character.isUpperCase(r.charAt(k))) {
								complete = complete + "(";
								complete = complete + r.charAt(k);
								flag = 0;
								k = k + 1;
							} else if (r.charAt(k) == ')') {
								if (flag == 1)
									break;
								complete = complete + r.charAt(k);
								complete = complete + " " + sym + " " + l + ")";
								flag = 1;
								k++;
							} else {
								complete = complete + r.charAt(k);
								if (r.charAt(k) != ' ')
									flag = 0;
								k++;
							}

						}
						complete = complete + ")";
						s2 = complete;
						c = 0;
						for (int j = i - 1; j >= 0; j--) {
							if (s1.charAt(j) == ')')
								c++;
							if (s1.charAt(j) == '(')
								c--;
							if (c < 0) {
								s3 = j;
								break;
							}
						}
						c = 0;
						for (int j = i + 1; j < s1.length(); j++) {
							if (s1.charAt(j) == '(')
								c++;
							if (s1.charAt(j) == ')')
								c--;
							if (c < 0) {
								f1 = j;
								break;
							}
						}
						int temp = s1.length();
						s1 = s1.substring(0, s3) + " " + s2 + " " + s1.substring(f1 + 1, temp);
						i = s3 + s2.length();
					}

				}
			}
		}

		return s1;
	}

	public String pushNegation(String s1) {
		String s2;
		s2 = "";
		for (int i = 0; i < s1.length();) {
			if (Character.isUpperCase(s1.charAt(i))) {
				s2 = s2 + "(" + "~";
				for (int j = i; j < s1.length(); j++) {
					s2 = s2 + s1.charAt(j);
					if (s1.charAt(j) == ')') {
						i = j + 1;
						s2 = s2 + ')';
						break;
					}
				}
			} else if (s1.charAt(i) == '&') {
				s2 = s2 + " | ";
				i++;
			} else if (s1.charAt(i) == '|') {
				s2 = s2 + " & ";
				i++;
			} else if (s1.charAt(i) == '~') {
				int flag1 = 0, f = -1;
				for (int j = i + 1; j < s1.length(); j++)
					if (s1.charAt(j) == ' ')
						j++;
					else if (s1.charAt(j) != ' ' && s1.charAt(j) == '(') {
						flag1 = 1;
						int c = 0;
						for (int k = j + 1; k < s1.length(); k++) {
							if (s1.charAt(k) == '(')
								c++;
							if (s1.charAt(k) == ')')
								c--;
							if (c < 0) {
								f = k;
								break;
							}
						}
						break;
					} else
						break;
				if (flag1 == 1) {
					int temp1 = s1.length();
					s1 = s1.substring(0, i) + " " + s1.substring(i + 1, temp1);
					String s3 = s1.substring(i, f + 1);
					String s4 = negationElimination(s3);
					s2 = s2 + s4;
					i = f + 1;
				}

				else {
					for (int j = i + 1; j < s1.length(); j++) {
						s2 = s2 + s1.charAt(j);
						if (s1.charAt(j) == ')') {
							i = j + 1;
							break;
						}
					}
				}
			} else {
				i++;
			}
		}
		return s2;
	}

	public String removeRedundantBraces(String s1) {
		int st = -1, fn = -1;
		List<Integer> in = new ArrayList<Integer>();
		String s2 = "";
		for (int i = 0; i < s1.length(); i++)
			if (s1.charAt(i) == '&' || s1.charAt(i) == '|') {
				int c = 0;
				for (int j = i - 1; j >= 0; j--) {
					if (s1.charAt(j) == ')')
						c++;
					if (s1.charAt(j) == '(')
						c--;
					if (c < 0) {
						st = j;
						break;
					}
				}
				c = 0;
				for (int j = i + 1; j < s1.length(); j++) {
					if (s1.charAt(j) == '(')
						c++;
					if (s1.charAt(j) == ')')
						c--;
					if (c < 0) {
						fn = j;
						break;
					}
				}
				in.add(st);
				in.add(fn);
			}
		for (int i = 0; i < s1.length();) {
			if (in.contains(i)) {
				s2 = s2 + s1.charAt(i);
				i++;
			} else if (s1.charAt(i) == '~' || Character.isUpperCase(s1.charAt(i))) {
				while (s1.charAt(i) != ')') {
					s2 = s2 + s1.charAt(i);
					i++;
				}
				s2 = s2 + s1.charAt(i);
				i++;
			} else if (s1.charAt(i) == '|' || s1.charAt(i) == '&') {
				s2 = s2 + " " + s1.charAt(i) + " ";
				i++;
			} else {
				i++;
			}
		}
		return s2;
	}

}
