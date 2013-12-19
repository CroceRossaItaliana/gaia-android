package it.gaiacri.mobile.Object;

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
}
