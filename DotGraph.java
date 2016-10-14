

import java.io.*;

public class DotGraph extends GraphViz {

	private String _filename;

	public DotGraph(String filename) {
		_filename = filename;
		addln(start_graph());
//		addln("rankdir=LR;");
	}

//	private void write_source_dot_to_file(String filename) {
//	    try {
//	        BufferedWriter out_dot = new BufferedWriter(new FileWriter(filename));
//	        out_dot.write(getDotSource());
//	        out_dot.close();
//	    } catch (IOException e) {}
//	}


	
	public void writeGraphToFile() throws java.io.IOException {
		super.addln(end_graph());
		super.writeGraphToFile(getDotSource().getBytes(), new File(_filename+".dot"));
		super.writeGraphToFile(getGraph(getDotSource()), new File(_filename+".gif"));
	}

	public static String sanitize(String s) {
//		StringBuffer sb = new StringBuffer(s.length());
//		for (char c : s.toCharArray())
//			switch (c) {
//			case '\\': sb.append("\\\\"); break;
//			//case ' ': sb.append(" <SP> "); break;
//			case '"': sb.append("\\\""); break;
//			default: sb.append(c); break;
//			}
//		return sb.toString();
		// truncate size
//		if (s.length() >= 15) s = truncate(s, 5, 5);

		String[] replace_list = {
				"\\\\r\\\\n",	"<CRLF>",
				"^ ",			"<SP>",
				" $",			"<SP>",
				"\\\\r",		"<LF>",
				"\\\\n",		"<CR>",
				"\\\\",			"\\\\\\\\",
				"\"",			"\\\\\""};
		
		for (int i=0; i<replace_list.length; i += 2)
			s = s.replaceAll(replace_list[i], replace_list[i+1]);
		
		
		return s;
	}
	
	public static String truncate(String long_string, int prefix, int sufix) {
		String truncated = "";
		if (prefix > 0)
			truncated += long_string.substring(0, prefix) + " ";
		truncated += "<...>";
		if (sufix > 0)
			truncated += " " + long_string.substring(long_string.length()-sufix, long_string.length());
		return truncated;
	}
}
