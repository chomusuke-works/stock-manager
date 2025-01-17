package ch.stockmanager.types;

public class YearSegment {
	public int id;
	public String name;
	public String startDate;
	public String endDate;
	public int priority;

	public YearSegment(int id, String name, String from, String to, int priority) {
		this.id = id;
		this.name = name;
		this.startDate = from;
		this.endDate = to;
		this.priority = priority;
	}
}