package resolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State {

	public String sentence;
	public State parent;
	public boolean explored;
	public String merger1="";
	public String merger2="";
	public String keyword;
	public int id=1;
	public List<State> children = null;
	public List<String> knowlegde = null;
	public char type;
	public Map<String, List<HashTableColumns>> statehashmap = new HashMap<String, List<HashTableColumns>>();
	public State(String sentence, State parent, boolean explored) {
		this.sentence = sentence;
		this.parent = parent;
		this.explored = false;
		this.children = new ArrayList<>();
		this.knowlegde= new ArrayList<>();
	}
	
	public void addChildren(State x)
	{
		children.add(x);
	}

}
