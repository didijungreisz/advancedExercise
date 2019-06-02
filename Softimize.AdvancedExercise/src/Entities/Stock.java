package Entities;

public class Stock implements Entity {

	private int id;
	private String name;
	private double value;
	private String date;

	public Stock(int id, String name, double value, String date) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}
	@Override
	public String toString() {
		return "id : " + id + " , name : " + name + " , value : " + value + " , date : " + date ;
	}

}
