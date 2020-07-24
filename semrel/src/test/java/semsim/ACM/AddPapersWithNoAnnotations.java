package semsim.ACM;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import it.cnr.iasi.saks.semrel.Utils;

public class AddPapersWithNoAnnotations {

	final static int ROWS = 1103;
	public static void main(String[] args) {	
		final String WEIGHTING_MODE = "af";		
		String in_folder = "target/test-classes/semsim/ACM/dataFromAntonio/";
		String in_folder_1 = "target/test-classes/semsim/ACM/dataFromAntonio/output2/";
		String out_folder = "/semsim/ACM/dataFromAntonio/output2/";
		
		String in_file = in_folder+"ListaPaperSenzaAnnotazione.txt";
		String in_file_1 = in_folder_1+"semsim_"+WEIGHTING_MODE+".txt";
		
		System.out.println(in_file);
		System.out.println(in_file_1);
		
		String out_file = out_folder+"semsim_COMPLETE_"+WEIGHTING_MODE+".txt";
		
		
		Vector<Integer> papersWithNoAnnotation = new Vector<Integer>(); 
		try {
            BufferedReader b = new BufferedReader(new FileReader(in_file));
            String line = "";
            while ((line = b.readLine()) != null) {
            	papersWithNoAnnotation.add(Integer.valueOf(line));
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		int i = 0;
		int row = 0;
		int newEmptyRow = i+Integer.valueOf(papersWithNoAnnotation.elementAt(i));
		try {
            BufferedReader b = new BufferedReader(new FileReader(in_file_1));
            String line = "";
            String emptyLine = "";
            while ((line = b.readLine()) != null) {
            	line = insertColumsWithZero(line, papersWithNoAnnotation);
            	if(row == newEmptyRow) {
//            		System.out.println(newEmptyRow);
            		// create empty row
            		emptyLine = createEmptyRow(row);
            		Utils.println(out_file, emptyLine, true);
            		//Utils.println(out_file, line, true);
            		i = i + 1;
            		row = row + 1;
            		if(i<papersWithNoAnnotation.size())
            			newEmptyRow = i+papersWithNoAnnotation.elementAt(i);
            	}
            	Utils.println(out_file, line, true);
            	row = row + 1;
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }	
	
	}
	
	private static String createEmptyRow(int r) {
		String result = "";
		for(int i=0; i<ROWS; i++)
			if(i!=r)
				result = result + "0.0 ";
			else
				result = result + "1.0 ";
		return result;
	}
	
	private static String insertColumsWithZero(String line, Vector<Integer> columnsWithZero) {
		String result = "";
		StringTokenizer st = new StringTokenizer(line, " ");
		int i = 0;
		int row = 0;
		int emptyColumn = i + columnsWithZero.get(i);
		while(st.hasMoreTokens()) {
			if(row == emptyColumn) {
				result = result + "0.0 ";
				i = i + 1;
				if(i<columnsWithZero.size())
					emptyColumn = i + columnsWithZero.get(i);
				row = row + 1;
			}
			result = result + st.nextToken() + " ";
			row = row + 1;
		}
		return result;
	}

}
