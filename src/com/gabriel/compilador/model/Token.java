package com.gabriel.compilador.model;

public class Token {

	private int codigo;
	private String simbolo;
	private String valor;
	private int linha;

	public Token(int codigo, String simbolo, String valor, int linha) {
		this.codigo = codigo;
		this.valor = valor;
		this.simbolo = simbolo;
		this.linha = linha;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}

	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

}
