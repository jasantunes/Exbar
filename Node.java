
public class Node extends AbstractNode {
	
	static enum LabelType {ACCEPT, REJECT};

	public static class Label implements LabelInterface {
		public LabelType label;
		public Label(boolean is_positive) {
			label = (is_positive)?Node.LabelType.ACCEPT:Node.LabelType.REJECT;
		}
		public boolean equals(Object obj) {
			return (label == ((Label) obj).label);
		}
	}
	
	
	public Object clone() {
		Node clone = new Node();
		clone.id = this.id;
		clone.label= this.label;
		return clone;
	}

	
	public Node createNewNode() {
		return new Node();
	}

	public boolean isAccepted() { return (((Label) label).label == Node.LabelType.ACCEPT); }
	public boolean isRejected() { return (((Label) label).label == Node.LabelType.REJECT); }
	
	
	public String getDotStyle() {
		String options = "label=\""+ this.toString() + "\"";

		if (isAccepted()) options += ", shape=doublecircle";
		if (isRejected()) options += ", style=filled, fillcolor=grey50";
		
		return (options.length()>0)?" ["+options+"];":"";
	}

}
