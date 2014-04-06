package it.gaiacri.mobile.Object;

import it.gaiacri.mobile.R;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

public class Rubrica {
	private String avatar;
	private String nome;
	private String cognome;
	private String numero;
	private String email;
	private Bitmap image;
	private ArrayList<String> ruolo;

	public Rubrica(String avatar,String nome, String cognome, String numero, String email,
			ArrayList<String> ruolo) {
		super();
		this.avatar=avatar;
		this.nome = nome;
		this.cognome=cognome;
		this.numero = numero;
		this.email = email;
		this.ruolo = ruolo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getAvatar() {
		return "https://gaia.cri.it/"+avatar;
	}
	
	public Bitmap getBitmap(Context context) {
		if(image == null){
			return BitmapFactory.decodeResource(context.getResources(),R.drawable.default_avatar);
		}
		return image;
	}
	public void setBitmap(Bitmap b){
		this.image = b;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRuolo() {
		String ris=ruolo.get(0);
		for(int i=1;i<ruolo.size();i++)
			ris=ris.concat("\n"+ruolo.get(i));
		return ris;
	}

	public void setRuolo(ArrayList<String> ruolo) {
		this.ruolo = ruolo;
	}
	
	
}
