package it.gaiacri.mobile.Object;

public class Turno {
	private String desc;
	private String id;
	private String start;
	private String end;
	private String url;
	private String color;
	
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
			boolean puoPartecipare, boolean partecipa, String partecipazione) {
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
	
}
