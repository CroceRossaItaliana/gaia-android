package it.gaiacri.mobile.Object;

public class Posta {
	private String id;
	private String mittente;
	private String mittente_nome_completo;
	private String oggetto;
	private String body;

	public Posta(String oggetto,String body) {
		super();
		this.oggetto = oggetto;
		this.body=body;
	}
	
	public Posta(String id,String oggetto,String body,String mittente) {
		super();
		this.id=id;
		this.oggetto = oggetto;
		this.body=body;
		this.mittente=mittente;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMittente() {
		return mittente;
	}

	public void setMittente(String mittente) {
		this.mittente = mittente;
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

	public String getNomeMittente() {
		return mittente_nome_completo;
	}

	public void setNomeMittente(String mittente_nome_completo) {
		this.mittente_nome_completo = mittente_nome_completo;
	}
	



	
	
}
