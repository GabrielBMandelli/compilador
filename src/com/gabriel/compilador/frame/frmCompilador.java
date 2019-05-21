package com.gabriel.compilador.frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import com.gabriel.compilador.components.TextLineNumber;
import com.gabriel.compilador.model.Simbolo;
import com.gabriel.compilador.model.Token;
import com.gabriel.compilador.util.FileUtil;
import com.gabriel.compilador.util.FunctionsUtil;

public class frmCompilador extends JFrame {
	
	private JPanel painelTopo;
	private JPanel painelTexto;
	private JPanel painelTabelas;
	private JPanel painelConsole;
	
	private JButton btnStop;
	
	private JTextArea txtEditor, txtConsole;
	private JTable tabLexica, tabSintatica;
	private JLabel lblPilhaLexica, lblPilhaSintatica, lblDebugOff, lblDebugOn;
	
	private DefaultTableModel modeloTabLexica = new DefaultTableModel();
	private DefaultTableModel modeloTabSintatica = new DefaultTableModel();
	
	private Stack<Token> pilhaLexica = new Stack<Token>();
	private Stack<Simbolo> pilhaSintatica = new Stack<Simbolo>();
	
	private String arquivoAtual = "";
	//private Boolean debug = false;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public frmCompilador() {
		super("Compilador");
		this.setSize(900, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setResizable(false);
		
		initComponents();
		setDebugOff();
	}
	
	public void setTexto(String texto) {
		txtEditor.setText(texto);
	}
	
	public String getTexto() {
		return txtEditor.getText();
	}
	
	private void setConsole(String texto) {
		txtConsole.setText(texto);
	}
	
	private void addConsole(String texto) {
		String text = FunctionsUtil.getTime() + " # " + texto;
		if (txtConsole.getText().trim().equals(""))
			txtConsole.setText(text);
		else
			txtConsole.setText(txtConsole.getText() + "\n" + text);
	}
	
	private void limparValores() {
		arquivoAtual = "";
		//debug = false;
		
		setConsole("");
		
		pilhaLexica = null;
		pilhaLexica = new Stack<Token>();
		modeloTabLexica.setNumRows(0);
		
		pilhaSintatica = null;
		pilhaSintatica = new Stack<Simbolo>();
		modeloTabSintatica.setNumRows(0);
	}
	
	private void novoArquivo() {
		setTexto("");
		limparValores();
	}
	
	private void abrirArquivo() throws IOException {
		JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
        //Exibe o diálogo. Deve ser passado por parâmetro o JFrame de origem.
        fc.showOpenDialog(null);
        //Captura o objeto File que representa o arquivo selecionado.
        File selFile = fc.getSelectedFile();
        
        if (selFile != null) {
        	setTexto(FileUtil.lerArquivo(selFile.getAbsolutePath()));
        	limparValores();
        	arquivoAtual = selFile.getAbsolutePath();
        }
	}
	
	private void salvarArquivo() {
		if (arquivoAtual.equals("")) {            
            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            //Exibe o diálogo. Deve ser passado por parâmetro o JFrame de origem.
            fc.showSaveDialog(null);
            //Captura o objeto File que representa o arquivo selecionado.
            
            File selFile = fc.getSelectedFile();
            if (selFile != null) {
            	arquivoAtual = selFile.getAbsolutePath();
            }
        }
		
    	FileUtil.gravarArquivo(arquivoAtual, getTexto());
	}
	
	private void setDebugOn() {
		lblDebugOff.setVisible(false);
		lblDebugOn.setVisible(true);
		btnStop.setVisible(true);
	}
	
	private void setDebugOff() {
		lblDebugOn.setVisible(false);
		lblDebugOff.setVisible(true);
		btnStop.setVisible(false);
	}
	
	private void compilar() {
		String conteudo = getTexto();
		
		try {
			addConsole("Inicia análise Léxica...");
			pilhaLexica = FunctionsUtil.analiseLexica(conteudo);
			addConsole("Análise Léxica concluída com sucesso!");
		
			addConsole("Inicia análise Sintática...");
			FunctionsUtil.analiseSintatica(pilhaLexica, pilhaSintatica);
			addConsole("Análise Sintática concluída com sucesso");
		} catch (Exception e) {
			addConsole(e.getMessage());
		}
	}
	
	private void debugar() {
		setDebugOn();
	}
	
	/*
	private void analiseLexica(String conteudo) {		
		try {
			addConsole("Inicia análise Léxica...");
			pilhaLexica = FunctionsUtil.analiseLexica(conteudo);
			addConsole("Análise Léxica concluída com sucesso!");
		} catch (Exception e) {
			setConsole(e.getMessage());
		}
		
		modeloTabLexica.setNumRows(0);
		
		for (Token t : pilhaLexica) {
			modeloTabLexica.addRow(new Object[] {t.getCodigo(), t.getValor()});
		}
	}
	
	private void analiseSintatica() throws Exception {
		if (debug) {
			if (!pilhaSintatica.isEmpty()) {
				Simbolo X = pilhaSintatica.get(0);
				Token a = pilhaLexica.get(0);
				
				if (FunctionsUtil.isTerminal(X)) {
					if (X.getCodigo() == a.getCodigo()) {
						pilhaSintatica.remove(0);
						pilhaLexica.remove(0);
					} else {
						throw new Exception("'" + X.getSimbolo() + "' esperado na linha " + a.getLinha());
					}
				} else {
					Stack<Simbolo> derivacao = FunctionsUtil.parsing(X.getCodigo(), a.getCodigo());
					
					if (derivacao != null) {
						pilhaSintatica.remove(0);
						while (!derivacao.isEmpty()) {
							pilhaSintatica.add(0, derivacao.pop());
						}
					} else {
						throw new Exception("Símbolo '" + a.getValor() + "' inválido na linha " + a.getLinha());
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "A Análise Sintática já terminou.");
			}
		} else {
			debug = true;
			pilhaSintatica = FunctionsUtil.iniciaPilhaExpansoes();
		}
		
		modeloTabLexica.setNumRows(0);
		
		for (Token t : pilhaLexica) {
			modeloTabLexica.addRow(new Object[] {t.getCodigo(), t.getValor()});
		}
		
		modeloTabSintatica.setNumRows(0);
		
		for (Simbolo t : pilhaSintatica) {
			modeloTabSintatica.addRow(new Object[] {t.getCodigo(), t.getSimbolo()});
		}
		
		if (pilhaSintatica.isEmpty())
			JOptionPane.showMessageDialog(null, "Análise Sintática concluída.");
	}
	*/
	
	private void initComponents() {
		painelTopo = new JPanel();
		painelTopo.setBounds(2, 2, 890, 45);
		painelTopo.setVisible(true);
		painelTopo.setLayout(null);
		painelTopo.setBorder(BorderFactory.createRaisedBevelBorder());
		
		Icon icoNewFile = new ImageIcon("src/com/gabriel/compilador/frame/img/file.png");
		JButton btnNewFile = new JButton(icoNewFile);
		btnNewFile.setBounds(10, 5, 35, 35);
		btnNewFile.setFocusable(false);
		btnNewFile.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				novoArquivo();				
			}
		});
		painelTopo.add(btnNewFile);
		
		Icon icoOpen = new ImageIcon("src/com/gabriel/compilador/frame/img/folder.png");
		JButton btnOpen = new JButton(icoOpen);
		btnOpen.setBounds(50, 5, 35, 35);
		btnOpen.setFocusable(false);
		btnOpen.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					abrirArquivo();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		painelTopo.add(btnOpen);
		
		Icon icoSave = new ImageIcon("src/com/gabriel/compilador/frame/img/save.png");
		JButton btnSave = new JButton(icoSave);
		btnSave.setBounds(90, 5, 35, 35);
		btnSave.setFocusable(false);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				salvarArquivo();
			}
		});
		painelTopo.add(btnSave);
		
		Icon icoRun = new ImageIcon("src/com/gabriel/compilador/frame/img/play.png");
		JButton btnRun = new JButton(icoRun);
		btnRun.setBounds(130, 5, 35, 35);
		btnRun.setFocusable(false);
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				compilar();
			}
		});
		painelTopo.add(btnRun);
		
		Icon icoDebug = new ImageIcon("src/com/gabriel/compilador/frame/img/skip.png");
		JButton btnDebug = new JButton(icoDebug);
		btnDebug.setBounds(170, 5, 35, 35);
		btnDebug.setFocusable(false);
		btnDebug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					debugar();
				} catch (Exception e) {
					addConsole(e.getMessage());
				}
			}
		});
		painelTopo.add(btnDebug);
		
		Icon icoStop = new ImageIcon("src/com/gabriel/compilador/frame/img/stop.png");
		btnStop = new JButton(icoStop);
		btnStop.setBounds(310, 5, 35, 35);
		btnStop.setFocusable(false);
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				setDebugOff();
			}
		});
		painelTopo.add(btnStop);
		
		lblDebugOff = new JLabel("DEBUG: OFF");
		lblDebugOff.setBounds(213, 4, 100, 40);
		lblDebugOff.setHorizontalAlignment(JLabel.LEFT);
		lblDebugOff.setFont(new Font("Arial", Font.BOLD, 14));
		painelTopo.add(lblDebugOff);
		
		lblDebugOn = new JLabel("DEBUG: ON");
		lblDebugOn.setBounds(213, 4, 100, 40);
		lblDebugOn.setHorizontalAlignment(JLabel.LEFT);
		lblDebugOn.setFont(new Font("Arial", Font.BOLD, 14));
		painelTopo.add(lblDebugOn);
		
		this.getContentPane().add(painelTopo);

		painelTexto = new JPanel();
		painelTexto.setBounds(2, 50, 625, 470);
		painelTexto.setVisible(true);
		painelTexto.setLayout(null);
		painelTexto.setBackground(new Color(255, 255, 255));
		painelTexto.setBorder(BorderFactory.createRaisedBevelBorder());
		
		txtEditor = new JTextArea();
	    JScrollPane scrollEditor = new JScrollPane(txtEditor);
	    TextLineNumber tln = new TextLineNumber(txtEditor);
	    scrollEditor.setRowHeaderView(tln);
	    scrollEditor.setBounds(2, 2, 621, 466);
	    scrollEditor.setVisible(true);
	    painelTexto.add(scrollEditor);
		
		this.getContentPane().add(painelTexto);

		painelTabelas = new JPanel();
		painelTabelas.setBounds(631, 50, 260, 470);
		painelTabelas.setVisible(true);
		painelTabelas.setLayout(null);
		painelTabelas.setBorder(BorderFactory.createRaisedBevelBorder());
		
		tabLexica = new JTable(modeloTabLexica);
		modeloTabLexica.addColumn("Código");
		modeloTabLexica.addColumn("Palavra");
		
		lblPilhaLexica = new JLabel("Pilha Léxica");
		lblPilhaLexica.setBounds(2, 2, 255, 22);
		lblPilhaLexica.setHorizontalAlignment(JLabel.CENTER);
		lblPilhaLexica.setFont(new Font("Arial", Font.BOLD, 15));
		painelTabelas.add(lblPilhaLexica);
		
		JScrollPane scrollTabelaLex = new JScrollPane(tabLexica);
		scrollTabelaLex.setBounds(2, 27, 255, 210);
		scrollTabelaLex.setVisible(true);
	    painelTabelas.add(scrollTabelaLex);
	    
	    tabSintatica = new JTable(modeloTabSintatica);
	    modeloTabSintatica.addColumn("Código");
	    modeloTabSintatica.addColumn("Palavra");
		
		lblPilhaSintatica = new JLabel("Pilha Sintática");
		lblPilhaSintatica.setBounds(2, 240, 255, 22);
		lblPilhaSintatica.setHorizontalAlignment(JLabel.CENTER);
		lblPilhaSintatica.setFont(new Font("Arial", Font.BOLD, 15));
		painelTabelas.add(lblPilhaSintatica);
		
		JScrollPane scrollTabelaSint = new JScrollPane(tabSintatica);
		scrollTabelaSint.setBounds(2, 263, 255, 205);
		scrollTabelaSint.setVisible(true);
		painelTabelas.add(scrollTabelaSint);
		
		this.getContentPane().add(painelTabelas);
		
		painelConsole = new JPanel();
		painelConsole.setBounds(2, 522, 889, 148);
		painelConsole.setVisible(true);
		painelConsole.setLayout(null);
		painelConsole.setBorder(BorderFactory.createRaisedBevelBorder());
		
		txtConsole = new JTextArea();
		txtConsole.setEditable(false);
		
	    JScrollPane scrollConsole = new JScrollPane(txtConsole);
	    scrollConsole.setBounds(2, 2, 884, 144);
	    scrollConsole.setVisible(true);
	    painelConsole.add(scrollConsole);
		
		this.getContentPane().add(painelConsole);
	}

}
