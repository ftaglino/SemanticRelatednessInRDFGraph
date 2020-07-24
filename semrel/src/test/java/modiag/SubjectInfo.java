package modiag;

public class SubjectInfo {
	String id = "";
	String label = "";
	String description = "";
	String valuesMeaning = "";
	
	public SubjectInfo(String id, String label, String description, String valuesMeaning) {
		super();
		this.id = id;
		this.label = label;
		this.description = description;
		this.valuesMeaning = valuesMeaning;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getValuesMeaning() {
		return valuesMeaning;
	}
	public void setValuesMeaning(String valuesMeaning) {
		this.valuesMeaning = valuesMeaning;
	}
	
	
}
