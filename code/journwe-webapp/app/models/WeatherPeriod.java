package models;

public class WeatherPeriod {

	private String temperature;

	private String lowTemperature;

	private String date;

	private String condition;

	private String icon;


	public WeatherPeriod(){

	}

	public WeatherPeriod(String temperature, String icon){
		this.setTemperature(temperature);
		this.setIcon(icon);
	}

	public void setTemperature(String temperature){
		this.temperature = temperature;
	} 

	public String getTemperature(){
		return this.temperature;
	} 

	public void setLowTemperature(String lowTemperature){
		this.lowTemperature = lowTemperature;
	} 

	public String getLowTemperature(){
		return this.lowTemperature;
	} 

	public void setDate(String date){
		this.date = date;
	} 

	public String getDate(){
		return this.date;
	} 

	public void setCondition(String condition){
		this.condition = condition;
	} 

	public String getCondition(){
		return this.condition;
	} 

	public void setIcon(String icon){
		this.icon = icon;
	} 

	public String getIcon(){
		return this.icon;
	} 
}
