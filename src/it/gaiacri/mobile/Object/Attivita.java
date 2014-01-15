package it.gaiacri.mobile.Object;

import java.util.ArrayList;

public class Attivita {
	private String title;
	private String id;
	private String organizzatore;
	private String color;
	private ArrayList<Turno> turni;
	
	public Attivita(String title,String id, String att_organizzatore, String tur_color) {
		super();
		this.title = title;
		this.id=id;
		this.color=tur_color;
		this.organizzatore=att_organizzatore;
		this.turni=new ArrayList<Turno>();
	}
	
	
	
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getOrganizzatore() {
		return organizzatore;
	}
	public void setOrganizzatore(String organizzatore) {
		this.organizzatore = organizzatore;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId(){
		return id;
	}
	public void addTurno(Turno t){
		turni.add(t);
	}
	public int numTurni(){
		return turni.size();
	}
	public ArrayList<Turno> getTurni(){
		return turni;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIdTurno(){
		return turni.get(0).getId();
	}
	
	
}
