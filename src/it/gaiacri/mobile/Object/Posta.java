package it.gaiacri.mobile.Object;

public class Posta {
	private String mittente;
	private String oggetto;
	private String body;

	public Posta(String oggetto,String body) {
		super();
		this.oggetto = oggetto;
		this.body=body;
	}
	
	public Posta(String oggetto,String body,String mittente) {
		super();
		this.oggetto = oggetto;
		this.body=body;
		this.mittente=mittente;
	}


	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
	}
	
	
}
