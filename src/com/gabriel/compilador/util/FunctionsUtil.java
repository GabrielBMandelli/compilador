package com.gabriel.compilador.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import com.gabriel.compilador.model.Parse;
import com.gabriel.compilador.model.Simbolo;
import com.gabriel.compilador.model.Token;

public class FunctionsUtil {

	public static Stack<Token> analiseLexica(String conteudo) throws Exception {
		int contLinha = 1;
		Stack<Token> pilha = new Stack<Token>();

		for (int i = 0; i < conteudo.length(); i++) {
			char car = ' ';
			Simbolo simbolo;
			StringBuilder builder = new StringBuilder();

			if ((i + 1) != conteudo.length())
				car = conteudo.charAt(i + 1);

			if (Character.isLetter(conteudo.charAt(i))) {
				builder.append(conteudo.charAt(i));

				while (Character.isLetterOrDigit(car)) {
					i++;
					builder.append(conteudo.charAt(i));
					car = ' ';
					if ((i + 1) != conteudo.length())
						car = conteudo.charAt(i + 1);
				}
				
				if (builder.toString().length() > 30)
					throw new Exception("Tamanho de identificador inválido na linha " + contLinha + ": " + builder.toString() + "'");
				
				if (isReservada(builder.toString())) {
					simbolo = getSimbolo(builder.toString().toUpperCase());
				} else {
					simbolo = getSimbolo("IDENTIFICADOR");
				}

				pilha.add(pilha.size(), new Token(simbolo.getCodigo(), simbolo.getSimbolo(), builder.toString(), contLinha));
			} else if (Character.isDigit(conteudo.charAt(i)) ||	(conteudo.charAt(i) == '-' && Character.isDigit(car))) {
				builder.append(conteudo.charAt(i));

				while (Character.isDigit(car)) {
					i++;
					builder.append(conteudo.charAt(i));
					car = ' ';
					if ((i + 1) != conteudo.length())
						car = conteudo.charAt(i + 1);
				}
				
				if (Character.isLetter(car))
					throw new Exception("Caracter inválido na linha " + contLinha + ": '" + car + "'");
				
				int valor = Integer.parseInt(builder.toString());
				
				if (valor > 32767 || valor < -32767)
					throw new Exception("Valor fora da escala na linha " + contLinha + ": " + valor + "'");
				
				simbolo = getSimbolo("INTEIRO");

				pilha.add(pilha.size(), new Token(simbolo.getCodigo(), simbolo.getSimbolo(), builder.toString(), contLinha));
			} else if (!Character.isWhitespace(conteudo.charAt(i))) {
				// if comentário, else if literal, else
				if (conteudo.charAt(i) == '(' && car == '*') {
					boolean comentario = true;
					while (comentario) {						
						comentario = !(conteudo.charAt(i) == '*' && car == ')');
						
						if (!comentario)
							break;
							
						i++;
						car = ' ';
						if ((i + 1) != conteudo.length())
							car = conteudo.charAt(i + 1);
					}
					i += 2;
				} else if (conteudo.charAt(i) == '\'') {
					while (car != '\'') {
						i++;
						builder.append(conteudo.charAt(i));
						car = ' ';
						if ((i + 1) != conteudo.length())
							car = conteudo.charAt(i + 1);
					}
					
					if (!builder.toString().equals("")) {
						if (builder.toString().length() > 255) {
							throw new Exception("Literal com tamanho maior que 255 na linha " + contLinha + ": '" + builder.toString() + "'");
						}
						
						simbolo = getSimbolo("LITERAL");

						pilha.add(pilha.size(), new Token(simbolo.getCodigo(), simbolo.getSimbolo(), builder.toString(), contLinha));
					}
					
					i++;
				} else {				
					builder.append(conteudo.charAt(i));
					
					if (!Character.isLetterOrDigit(car) && !Character.isWhitespace(car)) {
						if (isAtribuicao(conteudo.charAt(i), car) || isComparacao(conteudo.charAt(i), car)
								|| isDoisPontos(conteudo.charAt(i), car)) {
							builder.append(car);
							i++;
						}
					}
	
					simbolo = getSimbolo(builder.toString());
					
					if (simbolo == null)
						throw new Exception("Símbolo inválido na linha " + contLinha + ": '" + builder.toString() + "'");
					
					pilha.add(pilha.size(), new Token(simbolo.getCodigo(), simbolo.getSimbolo(), builder.toString(), contLinha));
				}
			} else if (isQuebraLinha(conteudo.charAt(i))) {
				contLinha++;
			}
		}

		return pilha;
	}
	
	public static void analiseSintatica(Stack<Token> pilhaLexica, Stack<Simbolo> pilhaExpansoes) throws Exception {
		pilhaExpansoes = iniciaPilhaExpansoes();
		
		while (!pilhaExpansoes.isEmpty()) {
			Simbolo X = pilhaExpansoes.get(0);
			Token a = pilhaLexica.get(0);
			
			if (isTerminal(X)) {
				if (X.getCodigo() == a.getCodigo()) {
					pilhaExpansoes.remove(0);
					pilhaLexica.remove(0);
				} else {
					throw new Exception("'" + X.getSimbolo() + "' esperado na linha " + a.getLinha());
				}
			} else {
				Stack<Simbolo> derivacao = parsing(X.getCodigo(), a.getCodigo());
				
				if (derivacao != null) {
					pilhaExpansoes.remove(0);
					while (!derivacao.isEmpty()) {
						pilhaExpansoes.add(0, derivacao.pop());
					}
				} else {
					throw new Exception("'" + X.getSimbolo() + "' esperado na linha " + a.getLinha());
				}
			}
		}		
	}
	
	public static Stack<Simbolo> iniciaPilhaExpansoes() {
		Stack<Simbolo> pilha = new Stack<Simbolo>();
		
		Simbolo simbolo = getSimbolo("PROGRAMA");
		pilha.add(0, new Simbolo(simbolo.getCodigo(), simbolo.getSimbolo()));
		
		return pilha;
	}
	
	private static Simbolo getSimbolo(String simbolo) {
		for (Simbolo simb : ConstantesUtil.SIMBOLOS_T) {
			if (simbolo.toUpperCase().equals(simb.getSimbolo())) {
				return simb;
			}			
		}
		
		for (Simbolo simb : ConstantesUtil.SIMBOLOS_N) {
			if (simbolo.toUpperCase().equals(simb.getSimbolo())) {
				return simb;
			}
		}
		
		return null;
	}
	
	private static boolean isReservada(String palavra) {
		for (String str : ConstantesUtil.RESERVADAS) {
			if (palavra.toUpperCase().equals(str))
				return true;
		}
		
		return false;
	}

	private static boolean isAtribuicao(char i, char car) {
		String s = String.valueOf(i) + String.valueOf(car);		
		return s.equals(":=");
	}
	
	private static boolean isDoisPontos(char i, char car) {
		String s = String.valueOf(i) + String.valueOf(car);		
		return s.equals("..");
	}
	
	private static boolean isQuebraLinha(char car) {
		return String.valueOf(car).equals("\n");
	}
	
	private static boolean isComparacao(char i, char car) {
		String s = String.valueOf(i) + String.valueOf(car);
		return (s.equals("<>") || s.equals("<=") || s.equals(">="));
	}
	
	public static boolean isTerminal(Simbolo X) {
		for (Simbolo simb : ConstantesUtil.SIMBOLOS_T) {
			if (X.getSimbolo().equals(simb.getSimbolo())) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Stack<Simbolo> parsing(int X, int A) {
		Stack<Simbolo> derivacao = null;
		
		for (Parse p : ConstantesUtil.TABELA_PARSE) {
			if (p.getX() == X && p.getA() == A) {
				if (p.getDerivacao() != null) {
					derivacao = new Stack<Simbolo>();
					
					if (!p.getDerivacao().equals("")) {
						String[] derivacoes = p.getDerivacao().split("\\|");
						
						for (String s : derivacoes) {
							Simbolo simb = getSimbolo(s);
							derivacao.push(simb);
						}
					}
					
					return derivacao;
				}
				break;
			}
		}
		
		return derivacao;
	}
	
	public static String getDateTime() { 
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
		Date date = new Date(); 
		return dateFormat.format(date); 
	}
	
	public static String getTime() { 
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); 
		Date date = new Date(); 
		return dateFormat.format(date); 
	}

}
