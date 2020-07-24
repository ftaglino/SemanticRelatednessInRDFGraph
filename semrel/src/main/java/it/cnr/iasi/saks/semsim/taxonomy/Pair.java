package it.cnr.iasi.saks.semsim.taxonomy;

public class Pair {
	String first = "";
	String second = "";
	
	public Pair(String first, String second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public String getFirst() {
		return first;
	}
	public void setFirst(String first) {
		this.first = first;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}

	@Override
	public String toString() {
		String result = "";
		result = this.getFirst() + " " +this.getSecond();
		return result;
	}
	
}
