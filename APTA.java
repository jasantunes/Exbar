import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class APTA <T extends AbstractNode> {
	
	T root;
	
	public APTA(T root) {
		this.root = root;
	}
	
	public AbstractNode getNodeById(int id) {
		for (AbstractNode n : getListOfNodes())
			if (n.id == id) return n;
		return null;
		
	}
	public HashSet<AbstractNode> getListOfNodes() {
		return root.getAllDescendents();
	}
	
	public void draw(String filename) throws IOException {
		System.out.println("saving graph to " + filename + ".gif...");
		DotGraph p = new DotGraph(filename);
		p.addln("rankdir=LR;");

		/* initial state */
		p.addln("null [shape=plaintext label=\"\"];");
		p.addln("null -> " + root.toString());
		p.addln(root.toString() + root.getDotStyle());
		
		/* remaining nodes */
		HashSet<AbstractNode> all_nodes = getListOfNodes();
		for (AbstractNode n : all_nodes) {
			for (String line : n.toDot())
				p.addln(line);
		}
		
		p.writeGraphToFile();
		System.out.println();
	}
	
	
	@SuppressWarnings("unchecked")
	private static <T extends AbstractNode> T clone_rec(T orig, HashMap<T,T> visited_nodes) {
		T clone = visited_nodes.get(orig);
		if (clone != null) return clone;
		
		clone = (T) orig.clone();
		visited_nodes.put(orig, clone);

		// create new transitions (for each old transition)
		for (Entry<AbstractNode.MessageType, AbstractNode> t : orig.transitions.entrySet()) {
			T cloned_child = clone_rec((T) t.getValue(), visited_nodes);
			cloned_child.parent = clone;
			clone.transitions.put(t.getKey(), cloned_child);
		}
		
		return clone;
	}
	
	protected APTA<T> clone() {
		// walk through the tree, clone nodes and correct references to new nodes
		T cloned_root = clone_rec(this.root, new HashMap<T, T>());
		APTA<T> clone = new APTA<T>(cloned_root);
		return clone;
	}
	



	
}
