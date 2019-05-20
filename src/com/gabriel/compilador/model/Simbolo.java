package com.gabriel.compilador.model;

public class Simbolo {

	private int codigo;
	private String simbolo;

	public Simbolo(int codigo, String simbolo) {
		this.setCodigo(codigo);
		this.setSimbolo(simbolo);
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}

}
