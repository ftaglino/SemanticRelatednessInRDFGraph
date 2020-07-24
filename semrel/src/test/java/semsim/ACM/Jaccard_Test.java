package semsim.ACM;

import java.util.HashSet;
import java.util.Set;

import it.cnr.iasi.saks.similarity.otherMethods.Jaccard;

public class Jaccard_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Set<String> s1 = new HashSet<String>();
		s1.add("100");
		s1.add("200");
		s1.add("300");
		s1.add("400");
		
		Set<String> s2 = new HashSet<String>();
		s2.add("100");
		s2.add("200");
		s2.add("300");
		
		System.out.println("s1: "+s1);
		System.out.println("s2: "+s2);
		
		Jaccard jacc = new Jaccard();
		
		Set<String> union = jacc.setUnion(s1, s2);
		System.out.println("UNION: "+union);
		Set<String> intersection = jacc.setIntersection(s1, s2);
		
		
		System.out.println("INTERSECTION:"+intersection);
		
		double sim = jacc.jaccard(s1, s2);
		
		System.out.println(sim);
	}

}
