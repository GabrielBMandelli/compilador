package com.gabriel.compilador.model;

public class Parse {

	private int X;
	private int A;

	private String derivacao;

	public Parse(int X, int A, String derivacao) {
		this.X = X;
		this.A = A;
		this.derivacao = derivacao;
	}

	public int getX() {
		return X;
	}

	public int getA() {
		return A;
	}

	public String getDerivacao() {
		return derivacao;
	}

}
