package com.gabriel.compilador;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gabriel.compilador.frame.frmCompilador;

public class App {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		new frmCompilador().setVisible(true);
	}

}
