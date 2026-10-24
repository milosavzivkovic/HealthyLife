package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;

/**
 * Vizuelni stil aplikacije na jednom mestu: boje, fontovi i gotove
 * komponente (polja, kartice, tabele, zaglavlja, dijalozi).
 * Ranije je svaka boja i font bio ponovljen na desetine mesta u GUI-ju.
 */
final class Tema {

	private Tema() {
	}

	// ═══════════════════════════════════════════════════════════
	// BOJE
	// ═══════════════════════════════════════════════════════════

	static final Color PRIMARNA         = new Color(22, 163, 74);
	static final Color PRIMARNA_TAMNA   = new Color(21, 128, 61);
	static final Color PRIMARNA_SVETLA  = new Color(220, 252, 231);
	static final Color ZAGLAVLJE        = new Color(20, 83, 45);
	static final Color ZAGLAVLJE_TEKST  = new Color(187, 247, 208);
	static final Color GRADIJENT_KRAJ   = new Color(13, 148, 136);

	static final Color POZADINA         = new Color(248, 250, 252);
	static final Color POZADINA_HOVER   = new Color(241, 245, 249);
	static final Color POVRSINA         = Color.WHITE;
	static final Color IVICA            = new Color(226, 232, 240);
	static final Color IVICA_POLJA      = new Color(203, 213, 225);

	static final Color TEKST            = new Color(15, 23, 42);
	static final Color TEKST_SPOREDNI   = new Color(100, 116, 139);
	static final Color TEKST_LABELE     = new Color(51, 65, 85);

	static final Color OPASNOST         = new Color(220, 38, 38);
	static final Color UPOZORENJE       = new Color(217, 119, 6);
	static final Color INFO             = new Color(37, 99, 235);
	static final Color LJUBICASTA       = new Color(124, 58, 237);

	static final Color CIP_DA_POZADINA  = new Color(220, 252, 231);
	static final Color CIP_DA_TEKST     = new Color(21, 128, 61);
	static final Color CIP_NE_POZADINA  = new Color(241, 245, 249);
	static final Color CIP_NE_TEKST     = new Color(100, 116, 139);

	/** Font sa simbolima (zvezdice) - Segoe UI ih nema. */
	static Font simboli(int velicina) {
		return new Font("Segoe UI Symbol", Font.PLAIN, velicina);
	}

	static Font font(int stil, int velicina) {
		return new Font("Segoe UI", stil, velicina);
	}

	/** Tamnija (f < 1) ili svetlija (f > 1) varijanta boje. */
	static Color nijansa(Color c, double f) {
		return new Color(ogranici(c.getRed() * f), ogranici(c.getGreen() * f), ogranici(c.getBlue() * f), c.getAlpha());
	}

	/** Mesavina dve boje; t = 0 daje a, t = 1 daje b. */
	static Color mesaj(Color a, Color b, double t) {
		return new Color(ogranici(a.getRed() + (b.getRed() - a.getRed()) * t),
				ogranici(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
				ogranici(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
	}

	private static int ogranici(double v) {
		return (int) Math.max(0, Math.min(255, Math.round(v)));
	}

	private static Graphics2D glatko(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return g2;
	}

	// ═══════════════════════════════════════════════════════════
	// TEKST
	// ═══════════════════════════════════════════════════════════

	static JLabel naslov(String tekst, int velicina) {
		JLabel l = new JLabel(tekst);
		l.setFont(font(Font.BOLD, velicina));
		l.setForeground(TEKST);
		return l;
	}

	static JLabel sporedni(String tekst) {
		JLabel l = new JLabel(tekst);
		l.setFont(font(Font.PLAIN, 13));
		l.setForeground(TEKST_SPOREDNI);
		return l;
	}

	static JLabel labelaPolja(String tekst) {
		JLabel l = new JLabel(tekst);
		l.setFont(font(Font.BOLD, 13));
		l.setForeground(TEKST_LABELE);
		return l;
	}

	/** Tekst sa prelamanjem koji se samo prikazuje (sastojci, komentari...). */
	static JTextArea tekstPrikaz(String tekst) {
		JTextArea ta = new JTextArea();
		// Bez ovoga JTextArea pri upisu teksta "skroluje do kursora" (cak i kasnije, kada se
		// prozor prikaze), pa ceo ekran skoci nadole. Mora se podesiti PRE upisa teksta.
		((javax.swing.text.DefaultCaret) ta.getCaret()).setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
		ta.setText(tekst == null || tekst.isBlank() ? "-" : tekst);
		ta.setFont(font(Font.PLAIN, 14));
		ta.setForeground(TEKST_LABELE);
		ta.setEditable(false);
		ta.setFocusable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setOpaque(false);
		ta.setBorder(null);
		return ta;
	}

	// ═══════════════════════════════════════════════════════════
	// POLJA ZA UNOS
	// ═══════════════════════════════════════════════════════════

	/** Ivica sa zaobljenim uglovima. */
	static final class ZaobljenaIvica implements Border {
		private final Color boja;
		private final int radijus;
		private final int debljina;

		ZaobljenaIvica(Color boja, int radijus, int debljina) {
			this.boja = boja;
			this.radijus = radijus;
			this.debljina = debljina;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
			Graphics2D g2 = glatko(g);
			g2.setColor(boja);
			for (int i = 0; i < debljina; i++) {
				g2.drawRoundRect(x + i, y + i, w - 1 - 2 * i, h - 1 - 2 * i, radijus, radijus);
			}
			g2.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(debljina, debljina, debljina, debljina);
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}
	}

	private static Border ivicaPolja(boolean fokus) {
		// Pri fokusu je ivica deblja za 1px, pa je unutrasnji razmak manji za 1px - tekst se ne pomera.
		return fokus
				? new CompoundBorder(new ZaobljenaIvica(PRIMARNA, 10, 2), new EmptyBorder(8, 11, 8, 11))
				: new CompoundBorder(new ZaobljenaIvica(IVICA_POLJA, 10, 1), new EmptyBorder(9, 12, 9, 12));
	}

	private static void stilizujPolje(JTextComponent p) {
		p.setFont(font(Font.PLAIN, 14));
		p.setForeground(TEKST);
		p.setCaretColor(TEKST);
		p.setBackground(POVRSINA);
		p.setOpaque(false);
		p.setBorder(ivicaPolja(false));
		p.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				p.setBorder(ivicaPolja(true));
			}

			@Override
			public void focusLost(FocusEvent e) {
				p.setBorder(ivicaPolja(false));
			}
		});
	}

	/** Crta zaobljenu belu pozadinu polja i sivi tekst-uputstvo kada je polje prazno. */
	private static void nacrtajPozadinuPolja(JTextComponent p, Graphics g) {
		Graphics2D g2 = glatko(g);
		g2.setColor(p.isEditable() ? p.getBackground() : POZADINA_HOVER);
		g2.fillRoundRect(0, 0, p.getWidth() - 1, p.getHeight() - 1, 10, 10);
		g2.dispose();
	}

	private static void nacrtajUputstvo(JTextComponent p, Graphics g, String uputstvo) {
		if (uputstvo == null || p.getDocument().getLength() > 0) return;
		Graphics2D g2 = glatko(g);
		g2.setColor(IVICA_POLJA.darker());
		g2.setFont(p.getFont());
		FontMetrics fm = g2.getFontMetrics();
		Insets in = p.getInsets();
		int y = (p.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
		g2.drawString(uputstvo, in.left, y);
		g2.dispose();
	}

	static final class Polje extends JTextField {
		private static final long serialVersionUID = 1L;
		private final String uputstvo;

		Polje(String uputstvo) {
			this.uputstvo = uputstvo;
			stilizujPolje(this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			nacrtajPozadinuPolja(this, g);
			super.paintComponent(g);
			nacrtajUputstvo(this, g, uputstvo);
		}
	}

	static final class PoljeLozinke extends JPasswordField {
		private static final long serialVersionUID = 1L;
		private final String uputstvo;

		PoljeLozinke(String uputstvo) {
			this.uputstvo = uputstvo;
			stilizujPolje(this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			nacrtajPozadinuPolja(this, g);
			super.paintComponent(g);
			nacrtajUputstvo(this, g, uputstvo);
		}
	}

	static Polje polje(String uputstvo) {
		return new Polje(uputstvo);
	}

	static Polje polje(String uputstvo, String vrednost) {
		Polje p = new Polje(uputstvo);
		p.setText(vrednost);
		return p;
	}

	static PoljeLozinke poljeLozinke(String uputstvo) {
		return new PoljeLozinke(uputstvo);
	}

	/** Visered polje za unos (opis, komentar, sastojci...). */
	static JTextArea oblast(String vrednost) {
		JTextArea ta = new JTextArea(vrednost == null ? "" : vrednost, 3, 0);
		ta.setFont(font(Font.PLAIN, 14));
		ta.setForeground(TEKST);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setBorder(new EmptyBorder(8, 10, 8, 10));
		return ta;
	}

	/** Oblast za unos u skrol panelu sa zaobljenom ivicom koja pozeleni pri fokusu. */
	static JScrollPane skrolOblasti(JTextArea ta, int visina) {
		JScrollPane sp = new JScrollPane(ta);
		Border obicna = new ZaobljenaIvica(IVICA_POLJA, 10, 1);
		Border fokus = new ZaobljenaIvica(PRIMARNA, 10, 2);
		sp.setBorder(obicna);
		sp.getViewport().setBackground(POVRSINA);
		sp.setPreferredSize(new Dimension(100, visina));
		sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, visina));
		sp.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		ta.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				sp.setBorder(fokus);
			}

			@Override
			public void focusLost(FocusEvent e) {
				sp.setBorder(obicna);
			}
		});
		return sp;
	}

	// ═══════════════════════════════════════════════════════════
	// PANELI
	// ═══════════════════════════════════════════════════════════

	/** Bela kartica sa zaobljenim uglovima i tankom ivicom. */
	static class Kartica extends JPanel {
		private static final long serialVersionUID = 1L;
		private Color ivica = IVICA;

		Kartica(LayoutManager lm) {
			super(lm);
			setOpaque(false);
			setBackground(POVRSINA);
		}

		void setIvica(Color ivica) {
			this.ivica = ivica;
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = glatko(g);
			g2.setColor(getBackground());
			g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
			g2.setColor(ivica);
			g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
			g2.dispose();
		}
	}

	static Kartica kartica(LayoutManager lm, int razmak) {
		Kartica k = new Kartica(lm);
		k.setBorder(new EmptyBorder(razmak, razmak, razmak, razmak));
		return k;
	}

	/**
	 * Klikabilna kartica za kontrolnu tablu: obojena traka, naslov, sadrzaj
	 * (npr. brojka i opis) i "Otvori ->". Ivica se oboji kada je mis iznad.
	 */
	static JPanel plocica(String naslov, JComponent sadrzaj, Color boja, Runnable akcija) {
		Kartica k = kartica(new BorderLayout(0, 8), 24);
		k.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

		JPanel traka = new JPanel();
		traka.setBackground(boja);
		traka.setPreferredSize(new Dimension(36, 5));
		JPanel trakaOmot = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		trakaOmot.setOpaque(false);
		trakaOmot.add(traka);

		JPanel gore = new JPanel(new BorderLayout(0, 12));
		gore.setOpaque(false);
		gore.add(trakaOmot, BorderLayout.NORTH);
		gore.add(naslov(naslov, 19), BorderLayout.CENTER);

		JLabel strelica = new JLabel("Otvori →");
		strelica.setFont(font(Font.BOLD, 13));
		strelica.setForeground(boja);
		JPanel dno = new JPanel(new BorderLayout());
		dno.setOpaque(false);
		if (sadrzaj != null) dno.add(sadrzaj, BorderLayout.WEST);
		dno.add(strelica, BorderLayout.EAST);

		k.add(gore, BorderLayout.NORTH);
		k.add(dno, BorderLayout.SOUTH);
		k.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				k.setIvica(boja);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				k.setIvica(IVICA);
			}

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				akcija.run();
			}
		});
		return k;
	}

	/** Panel sa zeleno-tirkiznim prelivom (pocetna, prijava). */
	static class GradijentPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		GradijentPanel(LayoutManager lm) {
			super(lm);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = glatko(g);
			int w = getWidth(), h = getHeight();
			g2.setPaint(new GradientPaint(0, 0, PRIMARNA_TAMNA, w, h, GRADIJENT_KRAJ));
			g2.fillRect(0, 0, w, h);
			// Diskretni krugovi kao ukras
			g2.setColor(new Color(255, 255, 255, 18));
			g2.fillOval(w - h / 2, -h / 3, h, h);
			g2.fillOval(-h / 4, h / 2, h * 2 / 3, h * 2 / 3);
			g2.dispose();
		}
	}

	/**
	 * Panel za JScrollPane koji uvek prati sirinu prozora, pa se dugacak tekst
	 * (sastojci, komentari) prelama umesto da izadje van ekrana.
	 */
	static class SkrolPanel extends JPanel implements javax.swing.Scrollable {
		private static final long serialVersionUID = 1L;

		SkrolPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBackground(POZADINA);
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(java.awt.Rectangle r, int o, int d) {
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(java.awt.Rectangle r, int o, int d) {
			return Math.max(r.height - 32, 16);
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}

	static JScrollPane skrol(JComponent sadrzaj) {
		JScrollPane sp = new JScrollPane(sadrzaj);
		sp.setBorder(null);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		sp.getViewport().setBackground(POZADINA);
		return sp;
	}

	/** Panel sa pozadinom aplikacije i razmakom oko sadrzaja. */
	static JPanel omotac(Component sadrzaj, int gore, int levo, int dole, int desno) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(POZADINA);
		p.setBorder(new EmptyBorder(gore, levo, dole, desno));
		p.add(sadrzaj, BorderLayout.CENTER);
		return p;
	}

	/** Tamnozelena traka na vrhu ekrana: naslov, podnaslov i dugmad desno. */
	static JPanel zaglavlje(String naslov, String podnaslov, JComponent... desno) {
		JPanel h = new JPanel(new BorderLayout(20, 0));
		h.setBackground(ZAGLAVLJE);
		h.setBorder(new EmptyBorder(14, 28, 14, 28));

		JPanel levo = new JPanel();
		levo.setLayout(new BoxLayout(levo, BoxLayout.Y_AXIS));
		levo.setOpaque(false);
		JLabel n = new JLabel(naslov);
		n.setFont(font(Font.BOLD, 20));
		n.setForeground(Color.WHITE);
		levo.add(n);
		if (podnaslov != null) {
			JLabel p = new JLabel(podnaslov);
			p.setFont(font(Font.PLAIN, 13));
			p.setForeground(ZAGLAVLJE_TEKST);
			levo.add(Box.createVerticalStrut(2));
			levo.add(p);
		}

		JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		d.setOpaque(false);
		for (JComponent c : desno) d.add(c);

		JPanel dOmot = new JPanel(new java.awt.GridBagLayout()); // vertikalno centriranje dugmadi
		dOmot.setOpaque(false);
		dOmot.add(d);

		h.add(levo, BorderLayout.WEST);
		h.add(dOmot, BorderLayout.EAST);
		return h;
	}

	/** Labela za ime prijavljenog korisnika u zaglavlju. */
	static JLabel imeUZaglavlju() {
		JLabel l = new JLabel(" ");
		l.setFont(font(Font.BOLD, 14));
		l.setForeground(ZAGLAVLJE_TEKST);
		l.setBorder(new EmptyBorder(0, 0, 0, 8));
		return l;
	}

	/** Donja traka dijaloga/ekrana: tekst levo, dugmad desno. */
	static JPanel podnozje(JComponent levo, JComponent... dugmad) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(POVRSINA);
		p.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, IVICA), new EmptyBorder(12, 20, 12, 20)));
		if (levo != null) p.add(levo, BorderLayout.WEST);
		JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		d.setOpaque(false);
		for (JComponent c : dugmad) d.add(c);
		p.add(d, BorderLayout.EAST);
		return p;
	}

	/** Prazno stanje: krupan naslov i objasnjenje na sredini. */
	static JPanel prazno(String naslov, String opis) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setOpaque(false);
		p.setBorder(new EmptyBorder(30, 20, 30, 20));
		JLabel n = naslov(naslov, 17);
		n.setForeground(TEKST_LABELE);
		n.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel o = sporedni(opis);
		o.setAlignmentX(Component.CENTER_ALIGNMENT);
		p.add(n);
		p.add(Box.createVerticalStrut(6));
		p.add(o);
		return p;
	}

	// ═══════════════════════════════════════════════════════════
	// CIPOVI (male zaobljene oznake)
	// ═══════════════════════════════════════════════════════════

	static JLabel cip(String tekst, Color pozadina, Color boja) {
		JLabel l = new JLabel(tekst) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = glatko(g);
				g2.setColor(getBackground());
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
				g2.dispose();
				super.paintComponent(g);
			}
		};
		l.setOpaque(false);
		l.setFont(font(Font.BOLD, 12));
		l.setBorder(new EmptyBorder(4, 12, 4, 12));
		postaviCip(l, tekst, pozadina, boja);
		return l;
	}

	static void postaviCip(JLabel cip, String tekst, Color pozadina, Color boja) {
		cip.setText(tekst);
		cip.setBackground(pozadina);
		cip.setForeground(boja);
		cip.repaint();
	}

	/** Cip "Posno"/"Nije posno", "Halal"/"Nije halal"... */
	static void postaviDaNe(JLabel cip, boolean da, String tekstDa, String tekstNe) {
		postaviCip(cip, da ? tekstDa : tekstNe,
				da ? CIP_DA_POZADINA : CIP_NE_POZADINA, da ? CIP_DA_TEKST : CIP_NE_TEKST);
	}

	// ═══════════════════════════════════════════════════════════
	// STATUSI ZAHTEVA I RACUNA
	// ═══════════════════════════════════════════════════════════

	static final String[] STATUSI_ZAHTEVA = { "poslat", "prihvacen", "neprihvacen", "odgovoren", "dostavljen_odgovor" };

	/** Citljiv naziv statusa (u bazi ostaje originalna vrednost). */
	static String nazivStatusa(String s) {
		if (s == null) return "-";
		return switch (s) {
		case "poslat" -> "Poslat";
		case "prihvacen" -> "Prihvaćen";
		case "neprihvacen" -> "Odbijen";
		case "odgovoren" -> "Čeka plaćanje";
		case "dostavljen_odgovor" -> "Završen";
		case "placen" -> "Plaćen";
		case "neplacen" -> "Nije plaćen";
		default -> s;
		};
	}

	/** {pozadina, tekst} za cip statusa. */
	static Color[] bojeStatusa(String s) {
		if (s == null) s = "";
		return switch (s) {
		case "poslat" -> new Color[] { new Color(219, 234, 254), new Color(30, 64, 175) };
		case "prihvacen" -> new Color[] { new Color(224, 231, 255), new Color(67, 56, 202) };
		case "neprihvacen" -> new Color[] { new Color(254, 226, 226), new Color(185, 28, 28) };
		case "odgovoren", "neplacen" -> new Color[] { new Color(254, 243, 199), new Color(180, 83, 9) };
		case "dostavljen_odgovor", "placen" -> new Color[] { CIP_DA_POZADINA, CIP_DA_TEKST };
		default -> new Color[] { CIP_NE_POZADINA, CIP_NE_TEKST };
		};
	}

	// ═══════════════════════════════════════════════════════════
	// TABELE
	// ═══════════════════════════════════════════════════════════

	/**
	 * Model tabele koji se ne moze menjati direktno i koji zna tip kolone
	 * (pa se brojevi sortiraju kao brojevi, a ne kao tekst).
	 */
	static DefaultTableModel model(String... kolone) {
		return new DefaultTableModel(kolone, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int c) {
				for (int r = 0; r < getRowCount(); r++) {
					Object v = getValueAt(r, c);
					if (v != null) return v.getClass();
				}
				return Object.class;
			}
		};
	}

	static void stilizujTabelu(JTable t) {
		t.setFont(font(Font.PLAIN, 13));
		t.setRowHeight(38);
		t.setShowVerticalLines(false);
		t.setShowHorizontalLines(true);
		t.setGridColor(IVICA);
		t.setIntercellSpacing(new Dimension(0, 1));
		t.setFillsViewportHeight(true);
		t.setBackground(POVRSINA);
		t.setSelectionBackground(PRIMARNA_SVETLA);
		t.setSelectionForeground(TEKST);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.setAutoCreateRowSorter(false);

		JTableHeader th = t.getTableHeader();
		th.setReorderingAllowed(false);
		th.setPreferredSize(new Dimension(0, 40));
		th.setDefaultRenderer(new ZaglavljeTabele());

		TableCellRenderer r = new Celija();
		t.setDefaultRenderer(Object.class, r);
		t.setDefaultRenderer(Number.class, r);
		t.setDefaultRenderer(Double.class, r);
		t.setDefaultRenderer(Float.class, r);
		t.setDefaultRenderer(Integer.class, r);
	}

	/** Celija tabele: naizmenicne boje redova, razmak, decimale na nasi nacin, brojevi desno. */
	static class Celija extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
			super.getTableCellRendererComponent(t, v, sel, false, row, col);
			setBorder(new EmptyBorder(0, 12, 0, 12));
			if (v instanceof Double d) setText(broj(d));
			setHorizontalAlignment(v instanceof Number ? SwingConstants.RIGHT : SwingConstants.LEFT);
			if (!sel) {
				setBackground(row % 2 == 0 ? POVRSINA : POZADINA);
				setForeground(TEKST);
			}
			return this;
		}
	}

	private static class ZaglavljeTabele extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
			super.getTableCellRendererComponent(t, v, false, false, row, col);
			setOpaque(true);
			setBackground(POZADINA_HOVER);
			setForeground(TEKST_LABELE);
			setFont(font(Font.BOLD, 12));
			setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, IVICA_POLJA), new EmptyBorder(0, 12, 0, 12)));
			Class<?> tip = t.getModel().getColumnClass(t.convertColumnIndexToModel(col));
			setHorizontalAlignment(Number.class.isAssignableFrom(tip) ? SwingConstants.RIGHT : SwingConstants.LEFT);
			return this;
		}
	}

	/** Kolona sa statusom prikazanim kao obojeni cip. */
	static class StatusCelija implements TableCellRenderer {
		private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
		private final JLabel cip = cip("", POVRSINA, TEKST);

		StatusCelija() {
			cip.setBorder(new EmptyBorder(2, 10, 2, 10));
			panel.add(cip);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
			String s = v == null ? "" : v.toString();
			Color[] boje = bojeStatusa(s);
			postaviCip(cip, nazivStatusa(s), boje[0], boje[1]);
			panel.setBackground(sel ? t.getSelectionBackground() : row % 2 == 0 ? POVRSINA : POZADINA);
			return panel;
		}
	}

	static void sakrijKolonu(JTable t, int kolona) {
		TableColumn c = t.getColumnModel().getColumn(kolona);
		c.setMinWidth(0);
		c.setMaxWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);
	}

	static void sirineKolona(JTable t, int... sirine) {
		for (int i = 0; i < sirine.length && i < t.getColumnCount(); i++) {
			if (sirine[i] > 0) t.getColumnModel().getColumn(i).setPreferredWidth(sirine[i]);
		}
	}

	static JScrollPane skrolTabele(JTable t) {
		JScrollPane sp = new JScrollPane(t);
		sp.setBorder(new LineBorder(IVICA, 1));
		sp.getViewport().setBackground(POVRSINA);
		return sp;
	}

	/**
	 * Povezuje polje za pretragu sa tabelom: dok kucas, prikazuju se samo
	 * redovi koji sadrze uneti tekst (u bilo kojoj koloni). Klik na zaglavlje sortira.
	 */
	static TableRowSorter<TableModel> pretraga(JTable t, JTextField polje) {
		TableRowSorter<TableModel> s = new TableRowSorter<>(t.getModel());
		t.setRowSorter(s);
		Runnable filtriraj = () -> {
			String q = polje.getText().trim();
			s.setRowFilter(q.isEmpty() ? null : RowFilter.regexFilter("(?iu)" + Pattern.quote(q)));
		};
		polje.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filtriraj.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filtriraj.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filtriraj.run();
			}
		});
		return s;
	}

	/**
	 * Indeks izabranog reda u MODELU (-1 ako nista nije izabrano).
	 * Kada je tabela filtrirana ili sortirana, red na ekranu nije isti kao red u modelu.
	 */
	static int izabraniRed(JTable t) {
		int v = t.getSelectedRow();
		return v < 0 ? -1 : t.convertRowIndexToModel(v);
	}

	/** Labela "Ukupno: 12 korisnika" / "Prikazano: 3 od 12" koja se sama osvezava. */
	static JLabel brojac(JTable t, String jedinica) {
		JLabel l = sporedni(" ");
		Runnable osvezi = () -> {
			int svi = t.getModel().getRowCount();
			int vidljivi = t.getRowCount();
			l.setText(vidljivi == svi ? "Ukupno: " + svi + " " + jedinica : "Prikazano: " + vidljivi + " od " + svi);
		};
		t.getModel().addTableModelListener(e -> osvezi.run());
		if (t.getRowSorter() != null) t.getRowSorter().addRowSorterListener(e -> osvezi.run());
		osvezi.run();
		return l;
	}

	static Polje poljePretrage(String uputstvo) {
		Polje p = polje(uputstvo);
		p.setPreferredSize(new Dimension(300, 38));
		return p;
	}

	/** Traka iznad tabele: pretraga levo, dodatne kontrole desno. */
	static JPanel trakaAlata(JComponent levo, JComponent... desno) {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.setBorder(new EmptyBorder(0, 0, 12, 0));
		if (levo != null) p.add(levo, BorderLayout.WEST);
		JPanel d = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		d.setOpaque(false);
		for (JComponent c : desno) d.add(c);
		p.add(d, BorderLayout.EAST);
		return p;
	}

	// ═══════════════════════════════════════════════════════════
	// DIJALOZI
	// ═══════════════════════════════════════════════════════════

	/** Modalni dijalog sa zaglavljem; zatvara se i tasterom Esc. */
	static JDialog dijalog(Window vlasnik, String naslov, String podnaslov, int sirina, int visina) {
		JDialog d = new JDialog(vlasnik, naslov, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		d.setSize(sirina, visina);
		d.setLocationRelativeTo(vlasnik);
		d.setLayout(new BorderLayout());
		d.getContentPane().setBackground(POZADINA);
		d.add(zaglavlje(naslov, podnaslov), BorderLayout.NORTH);
		d.getRootPane().registerKeyboardAction(e -> d.dispose(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		return d;
	}

	// ═══════════════════════════════════════════════════════════
	// CHAT BALON (Inbox korisnika i poruke nutricioniste)
	// ═══════════════════════════════════════════════════════════

	static final Color CHAT_POZADINA = new Color(241, 245, 249);
	private static final int MAKS_SIRINA_BALONA = 380;

	/**
	 * Jedan balon poruke. "desno" = poruka prijavljene osobe (zelena, desno),
	 * inace poruka sagovornika (bela, levo). Sirina se prilagodjava tekstu,
	 * a dugacke poruke se prelamaju (ranije su balon razvlacile u jedan red).
	 */
	static JPanel chatBalon(String ko, String tekst, boolean desno) {
		Kartica balon = new Kartica(new BorderLayout(0, 4));
		balon.setBackground(desno ? PRIMARNA_SVETLA : POVRSINA);
		balon.setIvica(desno ? new Color(187, 247, 208) : IVICA);
		balon.setBorder(new EmptyBorder(8, 12, 10, 12));

		JLabel koLbl = new JLabel(ko);
		koLbl.setFont(font(Font.BOLD, 12));
		koLbl.setForeground(desno ? PRIMARNA_TAMNA : TEKST_SPOREDNI);

		// HTML labela se pouzdano prelama i pri uvecanju ekrana (125%, 150%),
		// za razliku od JTextArea kojoj je tekst izlazio van balona.
		JLabel poruka = new JLabel();
		poruka.setFont(font(Font.PLAIN, 14));
		poruka.setForeground(TEKST);
		String t = tekst == null ? "" : tekst;
		FontMetrics fm = poruka.getFontMetrics(poruka.getFont());
		int prirodna = 0;
		for (String r : t.split("\n")) prirodna = Math.max(prirodna, fm.stringWidth(r));
		String html = t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
		poruka.setText(prirodna > MAKS_SIRINA_BALONA
				? "<html><div style='width:" + MAKS_SIRINA_BALONA + "px'>" + html + "</div></html>"
				: "<html>" + html + "</html>");

		balon.add(koLbl, BorderLayout.NORTH);
		balon.add(poruka, BorderLayout.CENTER);

		JPanel red = new JPanel(new FlowLayout(desno ? FlowLayout.RIGHT : FlowLayout.LEFT, 16, 4));
		red.setOpaque(false);
		red.add(balon);
		red.setAlignmentX(Component.LEFT_ALIGNMENT);
		red.setMaximumSize(new Dimension(Integer.MAX_VALUE, red.getPreferredSize().height));
		return red;
	}

	/** Mala napomena u prepisci (status, uputstvo za placanje...). */
	static JPanel chatNapomena(String tekst, Color boja, boolean desno) {
		JLabel l = new JLabel(tekst);
		l.setFont(font(Font.ITALIC, 12));
		l.setForeground(boja);
		JPanel red = new JPanel(new FlowLayout(desno ? FlowLayout.RIGHT : FlowLayout.LEFT, 22, 0));
		red.setOpaque(false);
		red.add(l);
		red.setAlignmentX(Component.LEFT_ALIGNMENT);
		red.setMaximumSize(new Dimension(Integer.MAX_VALUE, red.getPreferredSize().height));
		return red;
	}

	// ═══════════════════════════════════════════════════════════
	// FORMATIRANJE
	// ═══════════════════════════════════════════════════════════

	private static final DecimalFormat BROJ;
	static {
		DecimalFormatSymbols s = new DecimalFormatSymbols();
		s.setDecimalSeparator(',');
		s.setGroupingSeparator('.');
		BROJ = new DecimalFormat("#,##0.##", s);
	}

	/** 12.5 -> "12,5", 1200.0 -> "1.200" */
	static String broj(double d) {
		synchronized (BROJ) {
			return BROJ.format(d);
		}
	}

	static String novac(double iznos) {
		return broj(iznos) + " RSD";
	}

	/** Vrednost za polje za unos: 12.0 -> "12", 12.5 -> "12.5" */
	static String zaUnos(double d) {
		return d == Math.rint(d) ? String.valueOf((long) d) : String.valueOf(d);
	}

	/** Parsira broj i sa decimalnom tackom i sa zarezom ("12,5" i "12.5"). */
	static double parsirajBroj(String s) {
		return Double.parseDouble(s.trim().replace(',', '.'));
	}

	private static final DateTimeFormatter DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

	/** "2026-05-01 14:30:00" -> "01.05.2026. 14:30" */
	static String datum(String izBaze) {
		if (izBaze == null) return "-";
		try {
			String s = izBaze.replace(' ', 'T');
			if (s.length() > 19) s = s.substring(0, 19);
			return LocalDateTime.parse(s).format(DATUM);
		} catch (RuntimeException e) {
			return izBaze;
		}
	}

	/** "★★★★☆" za ocenu 4 */
	static String zvezdice(int ocena) {
		int o = Math.max(0, Math.min(5, ocena));
		return "★".repeat(o) + "☆".repeat(5 - o);
	}
}
