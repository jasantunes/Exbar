import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

public abstract class AbstractNode {
	public interface LabelInterface {
		public boolean equals(Object obj);
//		public void add(Object obj);
	}
	
	public static class MessageType {
		Character type;
		public MessageType(char m) { type = m; }
		@Override
		public boolean equals(Object obj) { return type.equals(((MessageType) obj).type); }
		@Override
		public int hashCode() { return type.hashCode(); }
		@Override public String toString() { return type.toString(); }
	}
	
	
	private static int LAST_ID=0;
	public int id;
	public LabelInterface label;
	public AbstractNode parent;
	public HashMap<MessageType,AbstractNode> transitions;

	public AbstractNode() {
		id = ++LAST_ID;
		parent = null;
		transitions = new HashMap<MessageType, AbstractNode>();
	}
	
	public abstract AbstractNode createNewNode();
	public abstract Object clone();
	public abstract String getDotStyle();	

	@Override
	public String toString() {
		return "S"+id;
	}
	
	public Set<MessageType> getSymbols() {
		return transitions.keySet();
	}
	
	public HashSet<AbstractNode> getAllDescendents() {
		HashSet<AbstractNode> descendents = new HashSet<AbstractNode>();
		getAllDescendents_rec(descendents);
		return descendents;
	}

	public void getAllDescendents_rec(HashSet<AbstractNode> descendents) {
		if (descendents.add(this)) {
			for (AbstractNode child : transitions.values())
				child.getAllDescendents_rec(descendents);
		}
	}

	
	public Entry<MessageType,AbstractNode> getTransitionTo(AbstractNode child) {
		for (Entry<MessageType,AbstractNode> entry : transitions.entrySet())
			if (entry.getValue().equals(child))
				return entry;
		return null;
	}
	
	public String print_transition_symbols(AbstractNode child) {
		ArrayList<MessageType> symbols = new ArrayList<MessageType>(this.transitions.size());
		for (Entry<MessageType, AbstractNode> t : this.transitions.entrySet())
			if (t.getValue() == child) symbols.add(t.getKey());
		
		StringBuffer result = new StringBuffer();
 		int i=0;
 		while (i<symbols.size()-1) {
 			result.append(symbols.get(i++));
 			result.append(", ");
 		}
 		if (i < symbols.size()) result.append(symbols.get(i));
		return result.toString();
	}
	
	
	public ArrayList<String> toDot() {
		ArrayList<String> dot_source = new ArrayList<String>(transitions.size());
		
		// FROM STATE
		String from = this.toString();
		
		// for each state (get all transitions)
		for (AbstractNode child : new HashSet<AbstractNode>(transitions.values())) {
			String to = child.toString();
			String options = child.getDotStyle();
			dot_source.add(to + options);
			
			String symbols = print_transition_symbols(child);
			dot_source.add(from + " -> " + to + " [label=\""+ symbols +"\"];");
		}
		
		return dot_source;
	}

}
