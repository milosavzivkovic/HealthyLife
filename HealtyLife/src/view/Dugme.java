package view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

/**
 * Dugme sa zaobljenim uglovima i efektom pri prelasku misem.
 * Samo crta svoju pozadinu, pa izgleda isto bez obzira na Look&Feel sistema
 * (ranije je Windows tema ponekad ignorisala boju dugmeta).
 */
class Dugme extends JButton {

	private static final long serialVersionUID = 1L;

	private enum Vrsta { PUNO, OBRUB, LINK, ZAGLAVLJE }

	private final Vrsta vrsta;
	private final Color boja;

	private Dugme(String tekst, Vrsta vrsta, Color boja, Color bojaTeksta) {
		super(tekst);
		this.vrsta = vrsta;
		this.boja = boja;
		setForeground(bojaTeksta);
		setFont(Tema.font(java.awt.Font.BOLD, 13));
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
		setOpaque(false);
		setRolloverEnabled(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setBorder(vrsta == Vrsta.LINK ? new EmptyBorder(4, 8, 4, 10) : new EmptyBorder(9, 18, 9, 18));
	}

	/** Glavna akcija na ekranu (zeleno). */
	static Dugme primarno(String tekst) {
		return new Dugme(tekst, Vrsta.PUNO, Tema.PRIMARNA, Color.WHITE);
	}

	/** Akcija koja brise ili blokira (crveno). */
	static Dugme opasno(String tekst) {
		return new Dugme(tekst, Vrsta.PUNO, Tema.OPASNOST, Color.WHITE);
	}

	/** Puno dugme proizvoljne boje. */
	static Dugme boja(String tekst, Color boja) {
		return new Dugme(tekst, Vrsta.PUNO, boja, Color.WHITE);
	}

	/** Sporedna akcija: belo dugme sa ivicom (Zatvori, Otkazi...). */
	static Dugme sekundarno(String tekst) {
		return new Dugme(tekst, Vrsta.OBRUB, Tema.POVRSINA, Tema.TEKST);
	}

	/** Dugme koje izgleda kao link (Nazad, Registrujte se...). */
	static Dugme link(String tekst) {
		return new Dugme(tekst, Vrsta.LINK, null, Tema.PRIMARNA_TAMNA);
	}

	/** Providno dugme za tamnu traku na vrhu ekrana. */
	static Dugme naZaglavlju(String tekst) {
		return new Dugme(tekst, Vrsta.ZAGLAVLJE, new Color(255, 255, 255, 28), Color.WHITE);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		boolean hover = getModel().isRollover();
		boolean pritisnuto = getModel().isPressed();
		int w = getWidth(), h = getHeight();

		switch (vrsta) {
		case PUNO -> {
			Color c = !isEnabled() ? Tema.mesaj(boja, Color.WHITE, 0.5)
					: pritisnuto ? Tema.nijansa(boja, 0.80)
					: hover ? Tema.nijansa(boja, 0.90) : boja;
			g2.setColor(c);
			g2.fillRoundRect(0, 0, w, h, 10, 10);
		}
		case OBRUB -> {
			g2.setColor(pritisnuto ? Tema.IVICA : hover ? Tema.POZADINA_HOVER : boja);
			g2.fillRoundRect(0, 0, w - 1, h - 1, 10, 10);
			g2.setColor(hover ? Tema.IVICA_POLJA : Tema.IVICA);
			g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
		}
		case ZAGLAVLJE -> {
			g2.setColor(new Color(255, 255, 255, pritisnuto ? 70 : hover ? 50 : boja.getAlpha()));
			g2.fillRoundRect(0, 0, w, h, 10, 10);
		}
		case LINK -> {
			if (hover) {
				g2.setColor(Tema.PRIMARNA_SVETLA);
				g2.fillRoundRect(0, 0, w, h, 8, 8);
			}
		}
		}
		g2.dispose();
		super.paintComponent(g);
	}
}
