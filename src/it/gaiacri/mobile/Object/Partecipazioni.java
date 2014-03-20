package it.gaiacri.mobile.Object;

public class Partecipazioni {
	private String attivita_id;
	private String attivita_name;
	private String id;
	private String stato_id;
	private String stato_value;
	private Turno turno;
	
	public Partecipazioni(String attivita_id,String attivita_name, String id, String stato_id,
			String stato_value, Turno turno) {
		super();
		this.attivita_id = attivita_id;
		this.attivita_name= attivita_name;
		this.id = id;
		this.stato_id = stato_id;
		this.stato_value = stato_value;
		this.turno = turno;
	}

	public String getAttivita_id() {
		return attivita_id;
	}

	public void setAttivita_id(String attivita_id) {
		this.attivita_id = attivita_id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStato_id() {
		return stato_id;
	}

	public void setStato_id(String stato_id) {
		this.stato_id = stato_id;
	}

	public String getStato_value() {
		return stato_value;
	}

	public void setStato_value(String stato_value) {
		this.stato_value = stato_value;
	}

	public Turno getTurno() {
		return turno;
	}

	public void setTurno(Turno turno) {
		this.turno = turno;
	}

	public String getAttivita_name() {
		return attivita_name;
	}

	public void setAttivita_name(String attivita_name) {
		this.attivita_name = attivita_name;
	}
	
		
	
	

	
}
