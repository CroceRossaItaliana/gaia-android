package it.gaiacri.mobile.Object;

import org.json.JSONObject;

public class Turno {
	private String desc;
	private String id;
	private String start;
	private String end;
	private String url;
	private String color;
	
	
	private int y,m,d,h,i;
	
	private boolean pieno;
	private boolean futuro;
	private boolean scoperto;
	private boolean puoPartecipare;
	private boolean partecipa;
	private String partecipazione;
	
	
	public Turno(String desc, String id, String start, String end, String url,
			String color) {
		super();
		this.desc = desc;
		this.id = id;
		this.start = start;
		this.end = end;
		this.url = url;
		this.color = color;
	}
	
	public Turno(String desc, String id, String start, String end, String url,
			String color, boolean pieno, boolean futuro, boolean scoperto,
			boolean puoPartecipare, boolean partecipa, String partecipazione,int y,int m,int d, int h, int i) {
		super();
		this.desc = desc;
		this.id = id;
		this.start = start;
		this.end = end;
		this.url = url;
		this.color = color;
		this.pieno = pieno;
		this.futuro = futuro;
		this.scoperto = scoperto;
		this.puoPartecipare = puoPartecipare;
		this.partecipa = partecipa;
		this.partecipazione = partecipazione;
		this.y=y;
		this.m=m;
		this.d=d;
		this.h=h;
		this.i=i;
	}



	public String getDesc() {
		return desc;
	}

	public String getId() {
		return id;
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

	public String getUrl() {
		return url;
	}

	public String getColor() {
		return color;
	}
	public String getDurata(){
		//doMagic
		String durata="";
		if(y!=0)
			durata=durata.concat(y + " anni ");
		if(m!=0)
			durata=durata.concat(m + " mesi ");
		if(d!=0)
			durata=durata.concat(d + " giorni ");
		if(h!=0)
			durata=durata.concat(h + " ore ");
		if(i!=0)
			durata=durata.concat(i + " min ");
		return durata.trim();
	}
	public int getPartecipa(){
		//vari if che ritornano diversi numeri in base allo stato delle variabili
		
		if((pieno == false) && (futuro == true) && (puoPartecipare==true) && (partecipa == false) ){//&& (partecipazione == false)){
			return 0;
		}else{
			if((futuro == true) && (partecipa == true) ){//&& (partecipazione == true)){
				return 1;
			}else{
				if((pieno == true) && (scoperto == false) && (puoPartecipare==false) && (futuro == true)){ //&& (partecipazione == false)){
					return 2;
				}else{
					if((scoperto == false) || (puoPartecipare==false)){
						return 3;
					}
				}
			}
				
		}
		return 2;
	}
	
	public String getPartecipazione(){
		return partecipazione;
	}
	public boolean getPart(){
		return this.partecipa;
	}
	public void setPart(boolean value){
		this.partecipa=value;
	}
	public boolean isFuturo(){
		return futuro;
	}
	
	public static Turno create(JSONObject turno){
		String tur_id=turno.optString("id");
		String tur_titolo=turno.optString("nome");
		String tur_start=(turno.optJSONObject("inizio")).optString("date");
		String tur_end=(turno.optJSONObject("fine")).optString("date");
		String tur_color="#3135B0";//turno.optString("color");
		boolean tur_pieno=turno.optBoolean("pieno");
		boolean tur_futuro=turno.optBoolean("futuro");
		boolean tur_scoperto=turno.optBoolean("scoperto");
		boolean tur_puoRichiedere=turno.optBoolean("puoRichiedere");
		boolean tur_partecipa=turno.optBoolean("partecipa");
		
		JSONObject partecipazione=turno.optJSONObject("partecipazione");
		String tur_partecipazione="";
		if(partecipazione != null){
			tur_partecipazione=partecipazione.optString("id");
		}
		
		JSONObject tur_durata=turno.optJSONObject("durata");
		int tur_y=tur_durata.optInt("y");
		int tur_m=tur_durata.optInt("m");
		int tur_d=tur_durata.optInt("d");
		int tur_h=tur_durata.optInt("h");
		int tur_i=tur_durata.optInt("i");
		Turno t=new Turno(tur_titolo, tur_id, tur_start, tur_end, "",
				tur_color, tur_pieno, tur_futuro,tur_scoperto,
				tur_puoRichiedere, tur_partecipa, tur_partecipazione,
				tur_y,tur_m,tur_d,tur_h,tur_i);		
		return t;
	}
	
	
}