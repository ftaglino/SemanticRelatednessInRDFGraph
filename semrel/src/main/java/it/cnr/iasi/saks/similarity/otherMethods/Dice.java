package it.cnr.iasi.saks.similarity.otherMethods;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import it.cnr.iasi.saks.semrel.Utils;

public class Dice {
	public double dice(Set<String> s1, Set<String> s2) {
		double result = 0.0d;
		
		Set<String> intersection = new HashSet<String>();
		intersection.addAll(s1);
		intersection = setIntersection(intersection, s2);
		
		result = 2*(double)intersection.size() / (double)(s1.size() + s2.size()); 
		
		return result;
	}
	
	public Set<String> setIntersection(Set<String> s1, Set<String> s2) {
		Set<String> result = new HashSet<String>();
		result.addAll(s1);
		result.retainAll(s2);
		return result;
	}
	
	
	public Vector<Set<String>> loadAnnotations(String annot_file, int num_res) {
		Vector<Set<String>> result = new Vector<Set<String>>();
		Set<String> temp = new HashSet<String>();
		System.out.println(num_res);
		for(int i=0; i<num_res; i++)
			result.add(temp);
		
		try {
            BufferedReader b = new BufferedReader(new FileReader(annot_file));
            String out = "";
            String line = "";
            while ((line = b.readLine()) != null) {
            	Set<String> ofv = new HashSet<String>();
            	StringTokenizer st = new StringTokenizer(line, " ");
            	int id_res = Integer.valueOf(st.nextToken()).intValue();
            	while(st.hasMoreTokens())
            		ofv.add(st.nextToken());
            	result.setElementAt(ofv, id_res);
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return result;
	}
}
