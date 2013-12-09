package it.gaiacri.mobile.Object;

import java.util.ArrayList;

public class Attivita {
	private String title;
	private String id;
	private ArrayList<Turno> turni;
	
	public Attivita(String title,String id) {
		super();
		this.title = title;
		this.id=id;
		this.turni=new ArrayList<Turno>();
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
