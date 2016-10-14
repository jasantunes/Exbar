import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class Exbar <T extends AbstractNode> {

	private int max_red;
	protected APTA<T> tree;
	private Stack<APTA<T>> undo_stack;

	public Exbar(T root) {
		tree = new APTA<T>(root); 
		max_red = 1;
		undo_stack = new Stack<APTA<T>>();
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	// FIGURE 3

	public boolean walkit(T r, T b, Integer merge_score) {
		if (b.label != null) {
			if (r.label != null) {
				if (r.label.equals(b.label))
					merge_score++;
				else
					return false; /* returns to caller of try_merge */
			} else
				r.label = b.label;
		}

		// dotimes (i, alphabet_size)
		Set<AbstractNode.MessageType> alphabet = b.getSymbols();
		for (AbstractNode.MessageType i : alphabet) {
			T r_child = (T) r.transitions.get(i);
			T b_child = (T) b.transitions.get(i);

			if (b_child != null) {
				if (r_child != null) {
					/* recurse */
					if (!walkit(r_child, b_child, merge_score))
						return false;
				} else {
					/* splice branch */
					r.transitions.put(i, b_child);
					b_child.parent = r;
				}
			}
		}
		return true;
	}

	/* undo side-effects of matching call to try_merge */
	public void undo_merge() {
		tree = undo_stack.pop();
	}

	public boolean try_merge(T r, T b, Integer merge_score) {

		// work on cloned tree and nodes and push old one to undo_stack
		undo_stack.push(tree);
		tree = tree.clone();
		T clone_r = (T) tree.getNodeById(r.id);
		T clone_b = (T) tree.getNodeById(b.id);

		// make blue node's father point to red node instead
		for (Entry<AbstractNode.MessageType, AbstractNode> entry : clone_b.parent.transitions.entrySet()) {
			if (entry.getValue() == clone_b)
				entry.setValue(clone_r);
		}
		return walkit(clone_r, clone_b, merge_score);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// FIGURE 4

	public boolean exh_search(Collection<T> red_list) {
		red_list = update_red_list(red_list);

		if (red_list.size() <= max_red) { /* failing this test causes cutoffs */
			ArrayList<T> blue_list = blue_list(red_list);

			if (blue_list.size() == 0) /* no_blue_node_exists() */
				return true; /* found_a_solution() */
			else {
				for (T B : blue_list) { /* pick_blue_node() */
					for (T R : red_list) { /* try all possible merges */
						if (try_merge(R, B, new Integer(0)) != false)
							if (exh_search(red_list)) return true;
						undo_merge(); /* undoes side effects of try_merging() */
					}
					if (exh_search(extend_list(red_list, B))) /* try NOT merging */
						return true;
				}
			}
		}
		return false;
	}
	
	public void main() {
		ArrayList<T> red_list = new ArrayList<T>();
		red_list.add(tree.root);
		
		max_red = 1;
		int max_nodes = tree.getListOfNodes().size();
		while (!exh_search(red_list) && max_red <= max_nodes)
			max_red++;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////
	// Misc
	private ArrayList<T> blue_list(Collection<T> red_list) {
		ArrayList<T> blue_list = new ArrayList<T>(red_list.size());
		for (T r : red_list)
			if (r.transitions.size() > 0) {
				// add children of r (which is not in red)
				for (AbstractNode child : r.transitions.values())
					if (!red_list.contains(child))
						blue_list.add((T)child);
			}
		return blue_list;
	}


	private Collection<T> extend_list(Collection<T> red_list, T blue) {
		ArrayList<T> extended_list = new ArrayList<T>(red_list);
		extended_list.add(blue);
		return extended_list;
	}


	private Collection<T> update_red_list(final Collection<T> red_list) {
		// create list of all nodes and populate it
		HashSet<AbstractNode> nodes = tree.getListOfNodes();
		HashMap<Integer, T> all_nodes = new HashMap<Integer, T>(nodes.size());
		for (AbstractNode n : tree.getListOfNodes())
			all_nodes.put(n.id, (T) n);

		ArrayList<T> updated_list = new ArrayList<T>(red_list.size());
		for (T n : red_list)
			updated_list.add(all_nodes.get(n.id));

		return updated_list;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	public static void buildAPTA(APTA<Node> tree, String session, boolean is_positive) {
		Node current = tree.root;
		
		for (char s : session.toCharArray()) {
			Node child = (Node) current.transitions.get(s);
			if (child == null) {
				child = tree.root.createNewNode();
				current.transitions.put(new AbstractNode.MessageType(s), child);
				child.parent = current;
			}
			
			current = child;
		}
		current.label = new Node.Label(is_positive);
	}
	
	public static void main(String[] args) {
		Exbar<Node> exbar = new Exbar<Node>(new Node());
		
		{ /* build APTA */
			buildAPTA(exbar.tree, "001", true);
			buildAPTA(exbar.tree, "01", true);
			buildAPTA(exbar.tree, "1", true);
			buildAPTA(exbar.tree, "101", true);

			buildAPTA(exbar.tree, "", false);
			buildAPTA(exbar.tree, "0", false);
			buildAPTA(exbar.tree, "00", false);
			buildAPTA(exbar.tree, "000", false);
			buildAPTA(exbar.tree, "010", false);
			buildAPTA(exbar.tree, "011", false);
			buildAPTA(exbar.tree, "10", false);
			buildAPTA(exbar.tree, "100", false);
			buildAPTA(exbar.tree, "11", false);
			buildAPTA(exbar.tree, "110", false);
			buildAPTA(exbar.tree, "111", false);
		}

		try { exbar.tree.draw("test/tree-0"); } catch (IOException e) { e.printStackTrace(); }
		exbar.main();
		System.out.println("Found a solution");
		try { exbar.tree.draw("test/tree-" + exbar.max_red); } catch (IOException e) { e.printStackTrace(); }


	}


}
