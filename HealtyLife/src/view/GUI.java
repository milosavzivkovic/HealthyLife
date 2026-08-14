package view;

import dao.DAO;
import model.Administrator;
import model.Komentar;
import model.KomentarDTO;
import model.Korisnik;
import model.Nutricionista;
import model.Proizvod;
import model.RacunDTO;
import model.ZahtevZaSaradnju;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;

import controller.AdminKontroler;
import controller.BarKodKontoler;
import controller.KomentarKontroler;
import controller.KorisnikKontroler;
import controller.NutricionistaKontroler;
import controller.ProizvodKontroler;
import controller.ZahtevKontroler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color PRIMARY_START  = new Color(102, 126, 234);
    private static final Color PRIMARY_END    = new Color(118, 75, 162);
    private static final Color BACKGROUND     = new Color(249, 250, 251);
    private static final Color SURFACE        = Color.WHITE;
    private static final Color TEXT_PRIMARY   = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR   = new Color(229, 231, 235);
    private static final Color HEADER_COLOR   = new Color(20, 40, 70);
    private static final Color ACCENT         = new Color(70, 130, 180);
    private static final Color BG             = new Color(245, 245, 250);
    private static final Color CARD_BG        = Color.WHITE;
    private static final Color TABLE_ROW1     = new Color(250, 250, 252);
    private static final Color TABLE_ROW2     = Color.WHITE;
    private static final Color NUTR_BORDER    = new Color(220, 220, 230);
    private static final Color TABLE_HEADER_C = new Color(235, 238, 245);
    private static final Color ERROR_RED      = new Color(200, 50, 60);
    private static final Color SUCCESS_GREEN  = new Color(40, 160, 80);

    private static final String CARD_HOME               = "HOME";
    private static final String CARD_ADMIN              = "ADMIN";
    private static final String CARD_ADMIN_KORISNICI    = "ADMIN_KORISNICI";
    private static final String CARD_ADMIN_NUTRICIONISTI = "ADMIN_NUTRICIONISTI";
    private static final String CARD_ADMIN_PROIZVODI     = "ADMIN_PROIZVODI";
    private static final String CARD_ADMIN_ZAHTEVI       = "ADMIN_ZAHTEVI";
    private static final String CARD_KORISNIK           = "KORISNIK";
    private static final String CARD_NUTRICIONISTA      = "NUTRICIONISTA";
    private static final String CARD_LOGIN_ADMIN        = "LOGIN_ADMIN";
    private static final String CARD_LOGIN_KOR          = "LOGIN_KOR";
    private static final String CARD_REG_KOR            = "REG_KOR";
    private static final String CARD_LOGIN_NUTRI        = "LOGIN_NUTRI";
    private static final String CARD_REG_NUTRI          = "REG_NUTRI";

    private CardLayout cardLayout;
    private JPanel     cardContainer;
    private DAO        dao;

    private DefaultTableModel adminKorisniciModel;
    private DefaultTableModel adminNutricionistiModel;
    private DefaultTableModel adminProizvodiModel;

    private JTextField  barkodField;
    private JLabel      nazivLabel, barkodInfoLabel, posanLabel, halalLabel;
    private JLabel      energijaLabel, mastiLabel, zasiceneMastiLabel;
    private JLabel      ugljeniHidratiLabel, seceriLabel, proteiniLabel, soLabel, vlaknaLabel, dozaLabel;
    private JTextArea   sastojciArea;
    private JPanel      komentariNutricionistaPanel; // dynamically populated on product load

    private JLabel korisnikImeLabel;
    private JLabel nutricionistaImeLabel;
    private JLabel administratorImeLabel;

    private JTextField     adminEmailField;
    private JPasswordField adminPassField;
    private JLabel         adminErrorLbl;

    private JTextField     korLoginEmailField;
    private JPasswordField korLoginPassField;
    private JLabel         korLoginErrorLbl;

    private JTextField     korRegImeField, korRegPrezimeField, korRegEmailField;
    private JPasswordField korRegPassField;
    private JLabel         korRegErrorLbl;

    private JTextField     nutriLoginEmailField;
    private JPasswordField nutriLoginPassField;
    private JLabel         nutriLoginErrorLbl;

    private JTextField     nutriRegImeField, nutriRegPrezimeField,
                           nutriRegEmailField, nutriRegSpecField;
    private JPasswordField nutriRegPassField;
    private JLabel         nutriRegErrorLbl;
    Korisnik korisnik;
    Nutricionista nutricionista;
    Administrator administrator;

    public void prikaziPocetniPanelAdmin() {
        prikaziPocetniPanelAdmin(administrator);
    }

    public GUI() {
        dao = new DAO();
        setTitle("Healthy Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.add(buildPanelPocetna(),                    CARD_HOME);
        cardContainer.add(buildPanelAdmin(),                      CARD_ADMIN);
        cardContainer.add(buildPanelAdminKorisnici(),             CARD_ADMIN_KORISNICI);
        cardContainer.add(buildPanelAdminNutricionisti(),         CARD_ADMIN_NUTRICIONISTI);
        cardContainer.add(buildPanelAdminProizvodi(),             CARD_ADMIN_PROIZVODI);
        cardContainer.add(buildPanelAdminZahtevi(),               CARD_ADMIN_ZAHTEVI);
        cardContainer.add(buildPanelKorisnik(),                   CARD_KORISNIK);
        cardContainer.add(buildPanelNutricionista(),              CARD_NUTRICIONISTA);
        cardContainer.add(buildPanelLoginAdmin(),                 CARD_LOGIN_ADMIN);
        cardContainer.add(buildPanelLoginKorisnik(),              CARD_LOGIN_KOR);
        cardContainer.add(buildPanelRegistracijaKorisnik(),       CARD_REG_KOR);
        cardContainer.add(buildPanelLoginNutricionista(),         CARD_LOGIN_NUTRI);
        cardContainer.add(buildPanelRegistracijaNutricionista(),  CARD_REG_NUTRI);
        setContentPane(cardContainer);
        prikaziPocetniPanel();
    }

    public void prikaziPocetniPanel()              { cardLayout.show(cardContainer, CARD_HOME); }
    public void prikaziPocetniPanelAdmin(Administrator a) {
        if (a != null && administratorImeLabel != null) {
            administratorImeLabel.setText(administrator.getIme() + " " + administrator.getPrezime());
        }
        cardLayout.show(cardContainer, CARD_ADMIN);
    }

    public void prikaziPocetniPanelKorisnika(Korisnik k) {
        if (k != null && korisnikImeLabel != null) {
            korisnikImeLabel.setText(k.getIme() + " " + k.getPrezime());
        }
        cardLayout.show(cardContainer, CARD_KORISNIK);
    }

    public void prikaziPocetniPanelNutricionista(Nutricionista n) { 
    	if (n != null && nutricionistaImeLabel != null) {
            nutricionistaImeLabel.setText(n.getIme() + " " + n.getPrezime());
        }
    	cardLayout.show(cardContainer, CARD_NUTRICIONISTA); 
    	}

    public void prikaziPanelLogovanjeAdmin() {
        resetField(adminEmailField); resetField(adminPassField); adminErrorLbl.setText(" ");
        cardLayout.show(cardContainer, CARD_LOGIN_ADMIN);
    }
    public void prikaziPanelLogovanjeKorisnika() {
        resetField(korLoginEmailField); resetField(korLoginPassField); korLoginErrorLbl.setText(" ");
        cardLayout.show(cardContainer, CARD_LOGIN_KOR);
    }
    public void prikaziPanelRegistracijaKorisnika() {
        resetField(korRegImeField); resetField(korRegPrezimeField);
        resetField(korRegEmailField); resetField(korRegPassField); korRegErrorLbl.setText(" ");
        cardLayout.show(cardContainer, CARD_REG_KOR);
    }
    public void prikaziPanelLogovanjeNutricioniste() {
        resetField(nutriLoginEmailField); resetField(nutriLoginPassField); nutriLoginErrorLbl.setText(" ");
        cardLayout.show(cardContainer, CARD_LOGIN_NUTRI);
    }
    public void prikaziPanelRegistracijaNutricioniste() {
        resetField(nutriRegImeField); resetField(nutriRegPrezimeField);
        resetField(nutriRegEmailField); resetField(nutriRegPassField);
        resetField(nutriRegSpecField); nutriRegErrorLbl.setText(" ");
        cardLayout.show(cardContainer, CARD_REG_NUTRI);
    }
    private void resetField(JTextField f) { if (f != null) f.setText(""); }

    public void neispravniPodaci() { JOptionPane.showMessageDialog(this, "Neispravni podaci!", "Greska", JOptionPane.ERROR_MESSAGE); }
    public void prikaziGresku(String p) { JOptionPane.showMessageDialog(this, p, "Greska", JOptionPane.ERROR_MESSAGE); }
    public void nepostojeciKorisnik() { korLoginErrorLbl.setText("Korisnik ne postoji. Proverite email i lozinku."); }
    public void korisnikVecPostoji(String e) { korRegErrorLbl.setText("Korisnik sa emailom " + e + " vec postoji."); }
    public void nutricionistaVecPostoji(String e) { nutriRegErrorLbl.setText("Nutricionista sa emailom " + e + " vec postoji."); }
    public void obrisanProizvodPoruka(String barKod) { JOptionPane.showMessageDialog(this, "Proizvod sa bar-kodom: "+barKod+" je uspesno obrisan", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE); }
    public void azuriranProizvodPoruka(String barKod) { JOptionPane.showMessageDialog(this, "Proizvod sa bar-kodom: "+barKod+" je uspesno azuriran", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE); }
    public void dodatProizvodPoruka(String barKod) { JOptionPane.showMessageDialog(this, "Proizvod sa bar-kodom: "+barKod+" je uspesno dodat", "Obaveštenje", JOptionPane.INFORMATION_MESSAGE); }
    // ═══════════════════════════════════════════════════════════
    // POCETNA
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelPocetna() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND);
        main.add(buildNavbar(), BorderLayout.NORTH);
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.add(buildHeroSection());
        content.add(buildFeaturesSection());
        content.add(buildBenefitsSection());
        content.add(Box.createVerticalStrut(40));
        JScrollPane sp = new JScrollPane(content);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setBorder(null);
        sp.getViewport().setBackground(BACKGROUND);
        main.add(sp, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(PRIMARY_START);
        navbar.setPreferredSize(new Dimension(1300, 70));
        navbar.setBorder(new EmptyBorder(0, 30, 0, 30));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);
        JButton adminBtn = new JButton("Admin");
        adminBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.setBorderPainted(false);
        adminBtn.setContentAreaFilled(false);
        adminBtn.setFocusPainted(false);
        adminBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        adminBtn.setUI(new BasicButtonUI());
        adminBtn.setPreferredSize(new Dimension(80, 40));
        adminBtn.addActionListener(e -> prikaziPanelLogovanjeAdmin());
        JLabel logo = new JLabel("HEALTHY LIFE");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        left.add(adminBtn); left.add(logo);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);
        JButton loginBtn = new JButton("Prijavi se \u25bc");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(Color.WHITE);
        loginBtn.setForeground(PRIMARY_END);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        loginBtn.setPreferredSize(new Dimension(150, 40));
        loginBtn.addActionListener(e -> showLoginDropdown(loginBtn));
        right.add(loginBtn);
        navbar.add(left, BorderLayout.WEST);
        navbar.add(right, BorderLayout.EAST);
        return navbar;
    }

    private JPanel buildHeroSection() {
        JPanel hero = new JPanel(new GridBagLayout());
        hero.setBackground(PRIMARY_START);
        hero.setPreferredSize(new Dimension(1300, 400));
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setOpaque(false);
        c.setBorder(new EmptyBorder(40, 20, 40, 20));
        JLabel title = new JLabel("<html><div style='text-align:center; font-size:48px; font-weight:bold; color:white;'>Aplikacija za<br>zdravu ishranu</div></html>");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sub = new JLabel("Jednostavno unesi bar kod proizvoda.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        sub.setForeground(new Color(230, 230, 255));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        c.add(title); c.add(Box.createVerticalStrut(15)); c.add(sub);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        hero.add(c, gbc);
        return hero;
    }

    private JPanel buildFeaturesSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(SURFACE);
        section.setBorder(new EmptyBorder(60, 60, 60, 60));
        JLabel title = new JLabel("Kako Healthy Life funkcionise?", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 40, 0));
        JPanel cards = new JPanel(new GridLayout(1, 3, 30, 0));
        cards.setBackground(SURFACE);
        cards.add(buildFeatureCard("Unesi bar kod", "Jednostavno unesi bar kod proizvoda i zapocni analizu."));
        cards.add(buildFeatureCard("Proveri sastav", "Dobij detaljne informacije o sastojcima, nutritivnim vrednostima i zdravstvenoj oceni."));
        cards.add(buildFeatureCard("Donesi odluku", "Napravi informisanu odluku o tome da li je proizvod prikladan za tvoju ishranu."));
        section.add(title, BorderLayout.NORTH);
        section.add(cards, BorderLayout.CENTER);
        return section;
    }

    private JPanel buildFeatureCard(String cardTitle, String desc) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true), new EmptyBorder(28, 28, 28, 28)));
        JLabel t = new JLabel("<html><div style='text-align:center;'>" + cardTitle + "</div></html>", SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.BOLD, 20)); t.setForeground(TEXT_PRIMARY);
        JTextArea d = new JTextArea(desc);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 14)); d.setForeground(TEXT_SECONDARY);
        d.setLineWrap(true); d.setWrapStyleWord(true); d.setEditable(false);
        d.setOpaque(false); d.setBorder(null);
        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildBenefitsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(BACKGROUND);
        section.setBorder(new EmptyBorder(60, 80, 60, 80));
        JLabel title = new JLabel("Zasto koristiti Healthy Life?", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 36)); title.setForeground(TEXT_PRIMARY); title.setBorder(new EmptyBorder(0, 0, 40, 0));
        JPanel grid = new JPanel(new GridLayout(1, 2, 40, 0));
        grid.setBackground(BACKGROUND);
        grid.add(buildBenefitCard("Zdravija ishrana", "Donesi bolje odluke o svojoj ishrani uz detaljne informacije o svakom proizvodu."));
        grid.add(buildBenefitCard("Brzo i jednostavno", "Sve informacije za nekoliko sekundi - samo unesi bar kod i proveri sastav proizvoda."));
        section.add(title, BorderLayout.NORTH); section.add(grid, BorderLayout.CENTER);
        return section;
    }

    private JPanel buildBenefitCard(String cardTitle, String desc) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true), new EmptyBorder(30, 30, 30, 30)));
        JLabel t = new JLabel(cardTitle); t.setFont(new Font("Segoe UI", Font.BOLD, 24)); t.setForeground(TEXT_PRIMARY);
        JTextArea d = new JTextArea(desc);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 15)); d.setForeground(TEXT_SECONDARY);
        d.setLineWrap(true); d.setWrapStyleWord(true); d.setEditable(false);
        d.setOpaque(false); d.setBorder(null);
        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        return card;
    }

    private void showLoginDropdown(Component parent) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        JMenuItem userItem = new JMenuItem("Korisnik"); userItem.setFont(new Font("Segoe UI", Font.BOLD, 14)); userItem.setBorder(new EmptyBorder(10, 15, 10, 15)); userItem.addActionListener(e -> prikaziPanelLogovanjeKorisnika()); popup.add(userItem);
        JMenuItem nutriItem = new JMenuItem("Nutricionista"); nutriItem.setFont(new Font("Segoe UI", Font.BOLD, 14)); nutriItem.setBorder(new EmptyBorder(10, 15, 10, 15)); nutriItem.addActionListener(e -> prikaziPanelLogovanjeNutricioniste()); popup.add(nutriItem);
        popup.show(parent, 0, parent.getHeight());
    }

    // ═══════════════════════════════════════════════════════════
    // LOGIN ADMIN 
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelLoginAdmin() {
        JPanel laOuter = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_START, getWidth(), getHeight(), PRIMARY_END));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        JPanel laCard = new JPanel();
        laCard.setLayout(new BoxLayout(laCard, BoxLayout.Y_AXIS));
        laCard.setBackground(SURFACE);
        laCard.setMinimumSize(new Dimension(500, 10));
        laCard.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        laCard.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 205, 220), 1, true), new EmptyBorder(48, 52, 48, 52)));
        JLabel laTitle = new JLabel("Admin prijava", SwingConstants.CENTER);
        laTitle.setFont(new Font("Segoe UI", Font.BOLD, 30)); laTitle.setForeground(TEXT_PRIMARY); laTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel laSub = new JLabel("Pristup administratorskom panelu", SwingConstants.CENTER);
        laSub.setFont(new Font("Segoe UI", Font.PLAIN, 14)); laSub.setForeground(TEXT_SECONDARY); laSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel laEmailLbl = new JLabel("E-mail"); laEmailLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); laEmailLbl.setForeground(new Color(55, 65, 81)); laEmailLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel laPassLbl = new JLabel("Lozinka"); laPassLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); laPassLbl.setForeground(new Color(55, 65, 81)); laPassLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminEmailField = new JTextField();
        adminEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); adminEmailField.setPreferredSize(new Dimension(396, 48)); adminEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); adminEmailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminEmailField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        adminPassField = new JPasswordField();
        adminPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); adminPassField.setPreferredSize(new Dimension(396, 48)); adminPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); adminPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminPassField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        adminErrorLbl = new JLabel(" "); adminErrorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); adminErrorLbl.setForeground(ERROR_RED); adminErrorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton laPrijaviBtn = new JButton("Prijavi se kao Admin");
        laPrijaviBtn.setFont(new Font("Segoe UI", Font.BOLD, 15)); laPrijaviBtn.setBackground(PRIMARY_END); laPrijaviBtn.setForeground(Color.WHITE);
        laPrijaviBtn.setFocusPainted(false); laPrijaviBtn.setBorderPainted(false); laPrijaviBtn.setOpaque(true);
        laPrijaviBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); laPrijaviBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); laPrijaviBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        laPrijaviBtn.addActionListener(e ->{
        	String email=adminEmailField.getText();
        	String lozinka=new String(adminPassField.getPassword());
        	if (AdminKontroler.validacijaAdmina(email, lozinka)) {
				try {
					administrator=dao.nadjiAdministratora(email, lozinka);
					if (administrator.getIdAdministratora() != 0) {
						prikaziPocetniPanelAdmin(administrator);
					} else {
						prikaziGresku("Administrator ne postoji!");
					}
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, e2.getMessage(), "Greška!", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				prikaziGresku("Morate uneti podatke");
			}
        });
        JButton laNazadBtn = new JButton("\u2190 Nazad na početnu");
        laNazadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13)); laNazadBtn.setForeground(TEXT_SECONDARY);
        laNazadBtn.setBorderPainted(false); laNazadBtn.setContentAreaFilled(false); laNazadBtn.setFocusPainted(false);
        laNazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); laNazadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        laNazadBtn.addActionListener(e -> prikaziPocetniPanel());
        laCard.add(laTitle); laCard.add(Box.createVerticalStrut(6)); laCard.add(laSub); laCard.add(Box.createVerticalStrut(30));
        laCard.add(laEmailLbl); laCard.add(Box.createVerticalStrut(7)); laCard.add(adminEmailField); laCard.add(Box.createVerticalStrut(18));
        laCard.add(laPassLbl); laCard.add(Box.createVerticalStrut(7)); laCard.add(adminPassField);
        laCard.add(Box.createVerticalStrut(8)); laCard.add(adminErrorLbl); laCard.add(Box.createVerticalStrut(16));
        laCard.add(laPrijaviBtn); laCard.add(Box.createVerticalStrut(16)); laCard.add(laNazadBtn);
        GridBagConstraints laGbc = new GridBagConstraints();
        laGbc.gridx = 0; laGbc.gridy = 0; laGbc.anchor = GridBagConstraints.CENTER; laGbc.fill = GridBagConstraints.NONE;
        laOuter.add(laCard, laGbc);
        return laOuter;
    }

    // ═══════════════════════════════════════════════════════════
    // LOGIN KORISNIK 
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelLoginKorisnik() {
        JPanel lkOuter = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_START, getWidth(), getHeight(), PRIMARY_END));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        JPanel lkCard = new JPanel();
        lkCard.setLayout(new BoxLayout(lkCard, BoxLayout.Y_AXIS));
        lkCard.setBackground(SURFACE);
        lkCard.setMinimumSize(new Dimension(500, 10));
        lkCard.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        lkCard.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 205, 220), 1, true), new EmptyBorder(48, 52, 48, 52)));
        JLabel lkTitle = new JLabel("Prijava korisnika", SwingConstants.CENTER);
        lkTitle.setFont(new Font("Segoe UI", Font.BOLD, 30)); lkTitle.setForeground(TEXT_PRIMARY); lkTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lkEmailLbl = new JLabel("E-mail"); lkEmailLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); lkEmailLbl.setForeground(new Color(55, 65, 81)); lkEmailLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lkPassLbl = new JLabel("Lozinka"); lkPassLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); lkPassLbl.setForeground(new Color(55, 65, 81)); lkPassLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        korLoginEmailField = new JTextField();
        korLoginEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); korLoginEmailField.setPreferredSize(new Dimension(396, 48)); korLoginEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); korLoginEmailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        korLoginEmailField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        korLoginPassField = new JPasswordField();
        korLoginPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); korLoginPassField.setPreferredSize(new Dimension(396, 48)); korLoginPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); korLoginPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        korLoginPassField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        korLoginErrorLbl = new JLabel(" "); korLoginErrorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); korLoginErrorLbl.setForeground(ERROR_RED); korLoginErrorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton lkPrijaviBtn = new JButton("Prijavi se");
        lkPrijaviBtn.setFont(new Font("Segoe UI", Font.BOLD, 15)); lkPrijaviBtn.setBackground(PRIMARY_END); lkPrijaviBtn.setForeground(Color.WHITE);
        lkPrijaviBtn.setFocusPainted(false); lkPrijaviBtn.setBorderPainted(false); lkPrijaviBtn.setOpaque(true);
        lkPrijaviBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); lkPrijaviBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); lkPrijaviBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // ── login korisnika ce biti povezan sa kontrolerom koji ce pozvati prikaziPocetniPanelKorisnika(k) ──
        lkPrijaviBtn.addActionListener(e -> {
        	String email=korLoginEmailField.getText();
        	String lozinka=new String(korLoginPassField.getPassword());
        	if (KorisnikKontroler.validacijaKorisnika(email, lozinka)) {
				try {
					korisnik=dao.nadjiKorisnika(email, lozinka);
					if (korisnik.getIdKorisnika() != 0) {
						prikaziPocetniPanelKorisnika(korisnik);
					} else {
						prikaziGresku("Korisnik ne postoji!");
					}
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, e2.getMessage(), "Greška!", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				prikaziGresku("Morate uneti podatke");
			}
        	
        });
        
        JButton lkRegLink = new JButton("Nemate nalog? Registrujte se \u2192");
        lkRegLink.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lkRegLink.setForeground(PRIMARY_START);
        lkRegLink.setBorderPainted(false); lkRegLink.setContentAreaFilled(false); lkRegLink.setFocusPainted(false);
        lkRegLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); lkRegLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        lkRegLink.addActionListener(e -> prikaziPanelRegistracijaKorisnika());
        JButton lkNazadBtn = new JButton("\u2190 Nazad na početnu");
        lkNazadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lkNazadBtn.setForeground(TEXT_SECONDARY);
        lkNazadBtn.setBorderPainted(false); lkNazadBtn.setContentAreaFilled(false); lkNazadBtn.setFocusPainted(false);
        lkNazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); lkNazadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        lkNazadBtn.addActionListener(e -> prikaziPocetniPanel());
        lkCard.add(lkTitle); lkCard.add(Box.createVerticalStrut(32));
        lkCard.add(lkEmailLbl); lkCard.add(Box.createVerticalStrut(7)); lkCard.add(korLoginEmailField); lkCard.add(Box.createVerticalStrut(18));
        lkCard.add(lkPassLbl); lkCard.add(Box.createVerticalStrut(7)); lkCard.add(korLoginPassField);
        lkCard.add(Box.createVerticalStrut(8)); lkCard.add(korLoginErrorLbl); lkCard.add(Box.createVerticalStrut(16));
        lkCard.add(lkPrijaviBtn); lkCard.add(Box.createVerticalStrut(14)); lkCard.add(lkRegLink); lkCard.add(Box.createVerticalStrut(8)); lkCard.add(lkNazadBtn);
        GridBagConstraints lkGbc = new GridBagConstraints();
        lkGbc.gridx = 0; lkGbc.gridy = 0; lkGbc.anchor = GridBagConstraints.CENTER; lkGbc.fill = GridBagConstraints.NONE;
        lkOuter.add(lkCard, lkGbc);
        return lkOuter;
    }

    // ═══════════════════════════════════════════════════════════
    // REGISTRACIJA KORISNIK
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelRegistracijaKorisnik() {
        JPanel rkOuter = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_START, getWidth(), getHeight(), PRIMARY_END));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        JPanel rkCard = new JPanel();
        rkCard.setLayout(new BoxLayout(rkCard, BoxLayout.Y_AXIS));
        rkCard.setBackground(SURFACE);
        rkCard.setMinimumSize(new Dimension(500, 10));
        rkCard.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        rkCard.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 205, 220), 1, true), new EmptyBorder(44, 52, 44, 52)));
        JLabel rkTitle = new JLabel("Registracija korisnika", SwingConstants.CENTER);
        rkTitle.setFont(new Font("Segoe UI", Font.BOLD, 30)); rkTitle.setForeground(TEXT_PRIMARY); rkTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rkImeLbl = new JLabel("Ime"); rkImeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rkImeLbl.setForeground(new Color(55, 65, 81)); rkImeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rkPrezimeLbl = new JLabel("Prezime"); rkPrezimeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rkPrezimeLbl.setForeground(new Color(55, 65, 81)); rkPrezimeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rkEmailLbl = new JLabel("E-mail"); rkEmailLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rkEmailLbl.setForeground(new Color(55, 65, 81)); rkEmailLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rkPassLbl = new JLabel("Lozinka"); rkPassLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rkPassLbl.setForeground(new Color(55, 65, 81)); rkPassLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        korRegImeField = new JTextField();
        korRegImeField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); korRegImeField.setPreferredSize(new Dimension(396, 48)); korRegImeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); korRegImeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        korRegImeField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        korRegPrezimeField = new JTextField();
        korRegPrezimeField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); korRegPrezimeField.setPreferredSize(new Dimension(396, 48)); korRegPrezimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); korRegPrezimeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        korRegPrezimeField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        korRegEmailField = new JTextField();
        korRegEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); korRegEmailField.setPreferredSize(new Dimension(396, 48)); korRegEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); korRegEmailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        korRegEmailField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        korRegPassField = new JPasswordField();
        korRegPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); korRegPassField.setPreferredSize(new Dimension(396, 48)); korRegPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); korRegPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        korRegPassField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        korRegErrorLbl = new JLabel(" "); korRegErrorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); korRegErrorLbl.setForeground(ERROR_RED); korRegErrorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton rkRegBtn = new JButton("Registruj se");
        rkRegBtn.setFont(new Font("Segoe UI", Font.BOLD, 15)); rkRegBtn.setBackground(SUCCESS_GREEN); rkRegBtn.setForeground(Color.WHITE);
        rkRegBtn.setFocusPainted(false); rkRegBtn.setBorderPainted(false); rkRegBtn.setOpaque(true);
        rkRegBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); rkRegBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); rkRegBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // ── Registracija korisnika - povazana sa DAO ──
        rkRegBtn.addActionListener(e -> {
            String rkIme     = korRegImeField.getText().trim();
            String rkPrezime = korRegPrezimeField.getText().trim();
            String rkEmail   = korRegEmailField.getText().trim();
            String rkLozinka = new String(korRegPassField.getPassword());
            if (!KorisnikKontroler.validirajPodatke(rkIme, rkPrezime, rkEmail, rkLozinka)) {
                neispravniPodaci();
                return;
            }
            try {
                if (dao.proveriPostojecegKorisnika(rkEmail)) {
                    korisnikVecPostoji(rkEmail);
                    return;
                }
                int id=dao.dodajKorisnika(rkIme, rkPrezime, rkEmail, rkLozinka);
                korRegErrorLbl.setText(" ");
                JOptionPane.showMessageDialog(this, "Uspesno ste se registrovali!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                korisnik = new Korisnik(id, rkIme, rkPrezime, rkEmail, rkLozinka);
                prikaziPocetniPanelKorisnika(korisnik);
            } catch (Exception ex) {
                korRegErrorLbl.setText("Greska: " + ex.getMessage());
            }
        });
        JButton rkLoginLink = new JButton("Vec imate nalog? Prijavite se \u2192");
        rkLoginLink.setFont(new Font("Segoe UI", Font.PLAIN, 13)); rkLoginLink.setForeground(PRIMARY_START);
        rkLoginLink.setBorderPainted(false); rkLoginLink.setContentAreaFilled(false); rkLoginLink.setFocusPainted(false);
        rkLoginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); rkLoginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        rkLoginLink.addActionListener(e -> prikaziPanelLogovanjeKorisnika());
        JButton rkNazadBtn = new JButton("\u2190 Nazad na početnu");
        rkNazadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13)); rkNazadBtn.setForeground(TEXT_SECONDARY);
        rkNazadBtn.setBorderPainted(false); rkNazadBtn.setContentAreaFilled(false); rkNazadBtn.setFocusPainted(false);
        rkNazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); rkNazadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rkNazadBtn.addActionListener(e -> prikaziPocetniPanel());
        rkCard.add(rkTitle); rkCard.add(Box.createVerticalStrut(28));
        rkCard.add(rkImeLbl); rkCard.add(Box.createVerticalStrut(7)); rkCard.add(korRegImeField); rkCard.add(Box.createVerticalStrut(16));
        rkCard.add(rkPrezimeLbl); rkCard.add(Box.createVerticalStrut(7)); rkCard.add(korRegPrezimeField); rkCard.add(Box.createVerticalStrut(16));
        rkCard.add(rkEmailLbl); rkCard.add(Box.createVerticalStrut(7)); rkCard.add(korRegEmailField); rkCard.add(Box.createVerticalStrut(16));
        rkCard.add(rkPassLbl); rkCard.add(Box.createVerticalStrut(7)); rkCard.add(korRegPassField);
        rkCard.add(Box.createVerticalStrut(8)); rkCard.add(korRegErrorLbl); rkCard.add(Box.createVerticalStrut(16));
        rkCard.add(rkRegBtn); rkCard.add(Box.createVerticalStrut(14)); rkCard.add(rkLoginLink); rkCard.add(Box.createVerticalStrut(8)); rkCard.add(rkNazadBtn);
        GridBagConstraints rkGbc = new GridBagConstraints();
        rkGbc.gridx = 0; rkGbc.gridy = 0; rkGbc.anchor = GridBagConstraints.CENTER; rkGbc.fill = GridBagConstraints.NONE;
        rkOuter.add(rkCard, rkGbc);
        return rkOuter;
    }

    // ═══════════════════════════════════════════════════════════
    // LOGIN NUTRICIONISTA
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelLoginNutricionista() {
        JPanel lnOuter = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_START, getWidth(), getHeight(), PRIMARY_END));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        JPanel lnCard = new JPanel();
        lnCard.setLayout(new BoxLayout(lnCard, BoxLayout.Y_AXIS));
        lnCard.setBackground(SURFACE);
        lnCard.setMinimumSize(new Dimension(500, 10));
        lnCard.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        lnCard.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 205, 220), 1, true), new EmptyBorder(48, 52, 48, 52)));
        JLabel lnTitle = new JLabel("Prijava nutricioniste", SwingConstants.CENTER);
        lnTitle.setFont(new Font("Segoe UI", Font.BOLD, 30)); lnTitle.setForeground(TEXT_PRIMARY); lnTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lnEmailLbl = new JLabel("E-mail"); lnEmailLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); lnEmailLbl.setForeground(new Color(55, 65, 81)); lnEmailLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lnPassLbl = new JLabel("Lozinka"); lnPassLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); lnPassLbl.setForeground(new Color(55, 65, 81)); lnPassLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriLoginEmailField = new JTextField();
        nutriLoginEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); nutriLoginEmailField.setPreferredSize(new Dimension(396, 48)); nutriLoginEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); nutriLoginEmailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriLoginEmailField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        nutriLoginPassField = new JPasswordField();
        nutriLoginPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); nutriLoginPassField.setPreferredSize(new Dimension(396, 48)); nutriLoginPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); nutriLoginPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriLoginPassField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        nutriLoginErrorLbl = new JLabel(" "); nutriLoginErrorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); nutriLoginErrorLbl.setForeground(ERROR_RED); nutriLoginErrorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton lnPrijaviBtn = new JButton("Prijavi se");
        lnPrijaviBtn.setFont(new Font("Segoe UI", Font.BOLD, 15)); lnPrijaviBtn.setBackground(PRIMARY_END); lnPrijaviBtn.setForeground(Color.WHITE);
        lnPrijaviBtn.setFocusPainted(false); lnPrijaviBtn.setBorderPainted(false); lnPrijaviBtn.setOpaque(true);
        lnPrijaviBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); lnPrijaviBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); lnPrijaviBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        lnPrijaviBtn.addActionListener(e -> {
        	String email=nutriLoginEmailField.getText();
        	String lozinka=new String(nutriLoginPassField.getPassword());
        	if (NutricionistaKontroler.validacijaNutricioniste(email, lozinka)) {
				try {
					nutricionista=dao.nadjiNutricionistu(email, lozinka);
					if (nutricionista.getIdNutricioniste() != 0) {
						prikaziPocetniPanelNutricionista(nutricionista);
					} else {
						prikaziGresku("Nutricionista ne postoji!");
					}
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, e2.getMessage(), "Greška!", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				prikaziGresku("Morate uneti podatke");
			}
        });
        JButton lnRegLink = new JButton("Nemate nalog? Registrujte se \u2192");
        lnRegLink.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lnRegLink.setForeground(PRIMARY_START);
        lnRegLink.setBorderPainted(false); lnRegLink.setContentAreaFilled(false); lnRegLink.setFocusPainted(false);
        lnRegLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); lnRegLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        lnRegLink.addActionListener(e -> prikaziPanelRegistracijaNutricioniste());
        JButton lnNazadBtn = new JButton("\u2190 Nazad na početnu");
        lnNazadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13)); lnNazadBtn.setForeground(TEXT_SECONDARY);
        lnNazadBtn.setBorderPainted(false); lnNazadBtn.setContentAreaFilled(false); lnNazadBtn.setFocusPainted(false);
        lnNazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); lnNazadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        lnNazadBtn.addActionListener(e -> prikaziPocetniPanel());
        lnCard.add(lnTitle); lnCard.add(Box.createVerticalStrut(32));
        lnCard.add(lnEmailLbl); lnCard.add(Box.createVerticalStrut(7)); lnCard.add(nutriLoginEmailField); lnCard.add(Box.createVerticalStrut(18));
        lnCard.add(lnPassLbl); lnCard.add(Box.createVerticalStrut(7)); lnCard.add(nutriLoginPassField);
        lnCard.add(Box.createVerticalStrut(8)); lnCard.add(nutriLoginErrorLbl); lnCard.add(Box.createVerticalStrut(16));
        lnCard.add(lnPrijaviBtn); lnCard.add(Box.createVerticalStrut(14)); lnCard.add(lnRegLink); lnCard.add(Box.createVerticalStrut(8)); lnCard.add(lnNazadBtn);
        GridBagConstraints lnGbc = new GridBagConstraints();
        lnGbc.gridx = 0; lnGbc.gridy = 0; lnGbc.anchor = GridBagConstraints.CENTER; lnGbc.fill = GridBagConstraints.NONE;
        lnOuter.add(lnCard, lnGbc);
        return lnOuter;
    }

    // ═══════════════════════════════════════════════════════════
    // REGISTRACIJA NUTRICIONISTA
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelRegistracijaNutricionista() {
        JPanel rnOuter = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_START, getWidth(), getHeight(), PRIMARY_END));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        JPanel rnCard = new JPanel();
        rnCard.setLayout(new BoxLayout(rnCard, BoxLayout.Y_AXIS));
        rnCard.setBackground(SURFACE);
        rnCard.setMinimumSize(new Dimension(500, 10));
        rnCard.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        rnCard.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 205, 220), 1, true), new EmptyBorder(44, 52, 44, 52)));
        JLabel rnTitle = new JLabel("Registracija nutricioniste", SwingConstants.CENTER);
        rnTitle.setFont(new Font("Segoe UI", Font.BOLD, 30)); rnTitle.setForeground(TEXT_PRIMARY); rnTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rnImeLbl = new JLabel("Ime"); rnImeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rnImeLbl.setForeground(new Color(55, 65, 81)); rnImeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rnPrezimeLbl = new JLabel("Prezime"); rnPrezimeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rnPrezimeLbl.setForeground(new Color(55, 65, 81)); rnPrezimeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rnEmailLbl = new JLabel("E-mail"); rnEmailLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rnEmailLbl.setForeground(new Color(55, 65, 81)); rnEmailLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rnPassLbl = new JLabel("Lozinka"); rnPassLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rnPassLbl.setForeground(new Color(55, 65, 81)); rnPassLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel rnSpecLbl = new JLabel("Licenca"); rnSpecLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); rnSpecLbl.setForeground(new Color(55, 65, 81)); rnSpecLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriRegImeField = new JTextField();
        nutriRegImeField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); nutriRegImeField.setPreferredSize(new Dimension(396, 48)); nutriRegImeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); nutriRegImeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriRegImeField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        nutriRegPrezimeField = new JTextField();
        nutriRegPrezimeField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); nutriRegPrezimeField.setPreferredSize(new Dimension(396, 48)); nutriRegPrezimeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); nutriRegPrezimeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriRegPrezimeField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        nutriRegEmailField = new JTextField();
        nutriRegEmailField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); nutriRegEmailField.setPreferredSize(new Dimension(396, 48)); nutriRegEmailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); nutriRegEmailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriRegEmailField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        nutriRegPassField = new JPasswordField();
        nutriRegPassField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); nutriRegPassField.setPreferredSize(new Dimension(396, 48)); nutriRegPassField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); nutriRegPassField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriRegPassField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        nutriRegSpecField = new JTextField();
        nutriRegSpecField.setFont(new Font("Segoe UI", Font.PLAIN, 15)); nutriRegSpecField.setPreferredSize(new Dimension(396, 48)); nutriRegSpecField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); nutriRegSpecField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nutriRegSpecField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(180, 185, 200), 1, true), new EmptyBorder(0, 14, 0, 14)));
        nutriRegErrorLbl = new JLabel(" "); nutriRegErrorLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); nutriRegErrorLbl.setForeground(ERROR_RED); nutriRegErrorLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton rnRegBtn = new JButton("Registruj se");
        rnRegBtn.setFont(new Font("Segoe UI", Font.BOLD, 15)); rnRegBtn.setBackground(SUCCESS_GREEN); rnRegBtn.setForeground(Color.WHITE);
        rnRegBtn.setFocusPainted(false); rnRegBtn.setBorderPainted(false); rnRegBtn.setOpaque(true);
        rnRegBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); rnRegBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); rnRegBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rnRegBtn.addActionListener(e -> {
            String rnIme     = nutriRegImeField.getText().trim();
            String rnPrezime = nutriRegPrezimeField.getText().trim();
            String rnEmail   = nutriRegEmailField.getText().trim();
            String rnLozinka = new String(nutriRegPassField.getPassword());
            String rnLicenca = nutriRegSpecField.getText().trim();
            if (!NutricionistaKontroler.validirajPodatke(rnIme, rnPrezime, rnLicenca, rnEmail, rnLozinka)) {
                neispravniPodaci();
                return;
            }
            try {
                if (dao.proveriPostojecegNutricionistu(rnEmail)) {
                	nutricionistaVecPostoji(rnEmail);
                    return;
                }
                int id= dao.dodajNutricionistu(rnIme, rnPrezime, rnLicenca, rnEmail, rnLozinka);
                nutriRegErrorLbl.setText(" ");
                JOptionPane.showMessageDialog(this, "Uspesno ste se registrovali!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                nutricionista = new Nutricionista(id, rnIme, rnPrezime, rnLicenca, rnEmail, rnLozinka);
                prikaziPocetniPanelNutricionista(nutricionista);
            } catch (Exception ex) {
                nutriRegErrorLbl.setText("Greska: " + ex.getMessage());
            }
        });
        JButton rnLoginLink = new JButton("Vec imate nalog? Prijavite se \u2192");
        rnLoginLink.setFont(new Font("Segoe UI", Font.PLAIN, 13)); rnLoginLink.setForeground(PRIMARY_START);
        rnLoginLink.setBorderPainted(false); rnLoginLink.setContentAreaFilled(false); rnLoginLink.setFocusPainted(false);
        rnLoginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); rnLoginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        rnLoginLink.addActionListener(e -> prikaziPanelLogovanjeNutricioniste());
        JButton rnNazadBtn = new JButton("\u2190 Nazad na početnu");
        rnNazadBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13)); rnNazadBtn.setForeground(TEXT_SECONDARY);
        rnNazadBtn.setBorderPainted(false); rnNazadBtn.setContentAreaFilled(false); rnNazadBtn.setFocusPainted(false);
        rnNazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); rnNazadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rnNazadBtn.addActionListener(e -> prikaziPocetniPanel());
        rnCard.add(rnTitle); rnCard.add(Box.createVerticalStrut(28));
        rnCard.add(rnImeLbl); rnCard.add(Box.createVerticalStrut(7)); rnCard.add(nutriRegImeField); rnCard.add(Box.createVerticalStrut(16));
        rnCard.add(rnPrezimeLbl); rnCard.add(Box.createVerticalStrut(7)); rnCard.add(nutriRegPrezimeField); rnCard.add(Box.createVerticalStrut(16));
        rnCard.add(rnSpecLbl); rnCard.add(Box.createVerticalStrut(7)); rnCard.add(nutriRegSpecField); rnCard.add(Box.createVerticalStrut(16));
        rnCard.add(rnEmailLbl); rnCard.add(Box.createVerticalStrut(7)); rnCard.add(nutriRegEmailField); rnCard.add(Box.createVerticalStrut(16));
        rnCard.add(rnPassLbl); rnCard.add(Box.createVerticalStrut(7)); rnCard.add(nutriRegPassField);
        rnCard.add(Box.createVerticalStrut(8)); rnCard.add(nutriRegErrorLbl); rnCard.add(Box.createVerticalStrut(16));
        rnCard.add(rnRegBtn); rnCard.add(Box.createVerticalStrut(14)); rnCard.add(rnLoginLink); rnCard.add(Box.createVerticalStrut(8)); rnCard.add(rnNazadBtn);
        GridBagConstraints rnGbc = new GridBagConstraints();
        rnGbc.gridx = 0; rnGbc.gridy = 0; rnGbc.anchor = GridBagConstraints.CENTER; rnGbc.fill = GridBagConstraints.NONE;
        rnOuter.add(rnCard, rnGbc);
        return rnOuter;
    }

    // ═══════════════════════════════════════════════════════════
    // ADMIN PANEL
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelAdmin() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND);
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(PRIMARY_START); top.setPreferredSize(new Dimension(0, 80)); top.setBorder(new EmptyBorder(0, 30, 0, 30));
        JLabel titleLbl = new JLabel("ADMIN PANEL", SwingConstants.LEFT); titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 28)); titleLbl.setForeground(Color.WHITE);
        administratorImeLabel = new JLabel("", SwingConstants.CENTER);
        administratorImeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        administratorImeLabel.setForeground(new Color(200, 220, 255));
        JButton logoutBtn = new JButton("Odjavi se"); logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); logoutBtn.setForeground(Color.WHITE); logoutBtn.setBackground(new Color(220, 53, 69));
        logoutBtn.setFocusPainted(false); logoutBtn.setBorderPainted(false); logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(10, 20, 10, 20)); 
        logoutBtn.addActionListener(e ->{
        	administrator=null;
        	prikaziPocetniPanel();
        });
        top.add(titleLbl, BorderLayout.WEST); top.add(administratorImeLabel, BorderLayout.CENTER); top.add(logoutBtn, BorderLayout.EAST);

        JButton korisniciBtn     = createBigButton("KORISNICI");
        JButton nutricionistiBtn = createBigButton("NUTRICIONISTI");
        JButton proizvodiBtn     = createBigButton("PROIZVODI");
        JButton zaradaBtn        = createBigButton("EVIDENCIJA ZARADE");
        JButton zahteviBtn       = createBigButton("ZAHTEVI ZA SARADNJU");
        korisniciBtn.setPreferredSize(new Dimension(0, 280));
        nutricionistiBtn.setPreferredSize(new Dimension(0, 280));
        proizvodiBtn.setPreferredSize(new Dimension(0, 280));
        zaradaBtn.setPreferredSize(new Dimension(0, 280));
        zahteviBtn.setPreferredSize(new Dimension(0, 280));
        korisniciBtn.addActionListener(e     -> administracijaKorisnikaPanel());
        nutricionistiBtn.addActionListener(e -> administracijaNutricionistaPanel());
        proizvodiBtn.addActionListener(e     -> administracijaProizvodaPanel());
        zaradaBtn.addActionListener(e        -> prikaziPanelEvidencijeZarade());
        zahteviBtn.addActionListener(e       -> administracijaZahtevaPanel());

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(SURFACE);
        center.setBorder(new EmptyBorder(40, 50, 40, 50));


        JPanel topRow = new JPanel(new GridLayout(1, 3, 20, 0));
        topRow.setBackground(SURFACE);
        topRow.add(korisniciBtn);
        topRow.add(nutricionistiBtn);
        topRow.add(proizvodiBtn);

        GridBagConstraints rowGbcTop = new GridBagConstraints();
        rowGbcTop.gridx = 0; rowGbcTop.gridy = 0; rowGbcTop.fill = GridBagConstraints.BOTH; rowGbcTop.weightx = 1.0; rowGbcTop.weighty = 1.0; rowGbcTop.insets = new Insets(0, 0, 20, 0);
        center.add(topRow, rowGbcTop);

        JPanel bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setBackground(SURFACE);

        JPanel leftSpacer = new JPanel(); leftSpacer.setOpaque(false);
        GridBagConstraints gbcLeftSpacer = new GridBagConstraints();
        gbcLeftSpacer.fill = GridBagConstraints.BOTH; gbcLeftSpacer.weighty = 1.0;
        gbcLeftSpacer.weightx = 0.5; gbcLeftSpacer.gridx = 0; gbcLeftSpacer.insets = new Insets(0, 0, 0, 0);
        bottomRow.add(leftSpacer, gbcLeftSpacer);

        GridBagConstraints gbcZarada = new GridBagConstraints();
        gbcZarada.fill = GridBagConstraints.BOTH; gbcZarada.weighty = 1.0;
        gbcZarada.weightx = 1.0; gbcZarada.gridx = 1; gbcZarada.insets = new Insets(0, 0, 0, 10);
        bottomRow.add(zaradaBtn, gbcZarada);

        GridBagConstraints gbcZahtevi = new GridBagConstraints();
        gbcZahtevi.fill = GridBagConstraints.BOTH; gbcZahtevi.weighty = 1.0;
        gbcZahtevi.weightx = 1.0; gbcZahtevi.gridx = 2; gbcZahtevi.insets = new Insets(0, 10, 0, 0);
        bottomRow.add(zahteviBtn, gbcZahtevi);

        JPanel rightSpacer = new JPanel(); rightSpacer.setOpaque(false);
        GridBagConstraints gbcRightSpacer = new GridBagConstraints();
        gbcRightSpacer.fill = GridBagConstraints.BOTH; gbcRightSpacer.weighty = 1.0;
        gbcRightSpacer.weightx = 0.5; gbcRightSpacer.gridx = 3; gbcRightSpacer.insets = new Insets(0, 0, 0, 0);
        bottomRow.add(rightSpacer, gbcRightSpacer);

        GridBagConstraints rowGbcBottom = new GridBagConstraints();
        rowGbcBottom.gridx = 0; rowGbcBottom.gridy = 1; rowGbcBottom.fill = GridBagConstraints.BOTH; rowGbcBottom.weightx = 1.0; rowGbcBottom.weighty = 1.0; rowGbcBottom.insets = new Insets(0, 0, 0, 0);
        center.add(bottomRow, rowGbcBottom);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(SURFACE); footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        JLabel footerLbl = new JLabel("Healthy Life Admin Panel \u00a9 2026"); footerLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); footerLbl.setForeground(Color.GRAY); footer.add(footerLbl);
        main.add(top, BorderLayout.NORTH); main.add(center, BorderLayout.CENTER); main.add(footer, BorderLayout.SOUTH);
        return main;
    }

    // ═══════════════════════════════════════════════════════════
    // ADMIN - PANEL KORISNICI
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelAdminKorisnici() {
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(BG);
        JPanel akHeader = new JPanel(new BorderLayout()); akHeader.setBackground(HEADER_COLOR); akHeader.setPreferredSize(new Dimension(0, 70)); akHeader.setBorder(new EmptyBorder(0, 25, 0, 25));
        JPanel akHeaderLeft = new JPanel(); akHeaderLeft.setLayout(new BoxLayout(akHeaderLeft, BoxLayout.Y_AXIS)); akHeaderLeft.setOpaque(false);
        JLabel akTitle = new JLabel("Korisnici sistema"); akTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); akTitle.setForeground(Color.WHITE);
        JLabel akInfo = new JLabel("Selektujte korisnika i kliknite 'Blokiraj korisnika'"); akInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); akInfo.setForeground(new Color(200, 210, 240));
        akHeaderLeft.add(Box.createVerticalStrut(12)); akHeaderLeft.add(akTitle); akHeaderLeft.add(Box.createVerticalStrut(3)); akHeaderLeft.add(akInfo);
        JButton akNazadBtn = new JButton("\u2190 Nazad na Admin panel"); akNazadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13)); akNazadBtn.setForeground(Color.WHITE);
        akNazadBtn.setBorderPainted(false); akNazadBtn.setContentAreaFilled(false); akNazadBtn.setFocusPainted(false); akNazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); akNazadBtn.addActionListener(e -> prikaziPocetniPanelAdmin());
        akHeader.add(akHeaderLeft, BorderLayout.WEST); akHeader.add(akNazadBtn, BorderLayout.EAST); main.add(akHeader, BorderLayout.NORTH);
        String[] akKolone = {"ID", "Ime", "Prezime", "Email"};
        adminKorisniciModel = new DefaultTableModel(akKolone, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable akTable = new JTable(adminKorisniciModel); styleTable(akTable);
        akTable.getColumnModel().getColumn(0).setMinWidth(0); akTable.getColumnModel().getColumn(0).setMaxWidth(0); akTable.getColumnModel().getColumn(0).setWidth(0);
        akTable.getColumnModel().getColumn(1).setPreferredWidth(180); akTable.getColumnModel().getColumn(2).setPreferredWidth(180); akTable.getColumnModel().getColumn(3).setPreferredWidth(400);
        akTable.setRowHeight(40); akTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane akScroll = new JScrollPane(akTable); akScroll.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(20, 25, 0, 25), new LineBorder(NUTR_BORDER, 1, true)));
        main.add(akScroll, BorderLayout.CENTER);
        JPanel akFooter = new JPanel(new BorderLayout()); akFooter.setBackground(BG); akFooter.setBorder(new EmptyBorder(12, 25, 16, 25));
        JLabel akCount = new JLabel("Ukupno korisnika: 0"); akCount.setFont(new Font("Segoe UI", Font.PLAIN, 12)); akCount.setForeground(Color.GRAY);
        JButton akBlokirajBtn = new JButton("Blokiraj korisnika");
        akBlokirajBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); akBlokirajBtn.setBackground(new Color(200, 50, 60)); akBlokirajBtn.setForeground(Color.WHITE);
        akBlokirajBtn.setFocusPainted(false); akBlokirajBtn.setBorderPainted(false); akBlokirajBtn.setOpaque(true); akBlokirajBtn.setBorder(new EmptyBorder(10, 24, 10, 24)); akBlokirajBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        akBlokirajBtn.addActionListener(e -> {
            int row = akTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Molimo selektujte korisnika.", "Nije selektovano", JOptionPane.WARNING_MESSAGE); return; }
            int id = (int) adminKorisniciModel.getValueAt(row, 0);
            String ime = (String) adminKorisniciModel.getValueAt(row, 1);
            String prezime = (String) adminKorisniciModel.getValueAt(row, 2);
            int ok = JOptionPane.showConfirmDialog(this, "Blokiraj korisnika: " + ime + " " + prezime + "?", "Potvrda", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) return;
            try { 
            	dao.blokirajKorisnika(id); 
            	JOptionPane.showMessageDialog(this, "Korisnik " + ime + " " + prezime + " je blokiran.", "Uspesno", JOptionPane.INFORMATION_MESSAGE);
            	osveziTabeluKorisnici();
            	}
            catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }
        });
        akFooter.add(akCount, BorderLayout.WEST); akFooter.add(akBlokirajBtn, BorderLayout.EAST); main.add(akFooter, BorderLayout.SOUTH);
        adminKorisniciModel.addTableModelListener(ev -> akCount.setText("Ukupno korisnika: " + adminKorisniciModel.getRowCount()));
        return main;
    }

    // ═══════════════════════════════════════════════════════════
    // ADMIN - PANEL NUTRICIONISTI
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelAdminNutricionisti() {
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(BG);
        JPanel anHeader = new JPanel(new BorderLayout()); anHeader.setBackground(HEADER_COLOR); anHeader.setPreferredSize(new Dimension(0, 70)); anHeader.setBorder(new EmptyBorder(0, 25, 0, 25));
        JPanel anHeaderLeft = new JPanel(); anHeaderLeft.setLayout(new BoxLayout(anHeaderLeft, BoxLayout.Y_AXIS)); anHeaderLeft.setOpaque(false);
        JLabel anTitle = new JLabel("Nutricionisti sistema"); anTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); anTitle.setForeground(Color.WHITE);
        JLabel anInfo = new JLabel("Selektujte nutricionistu i kliknite 'Blokiraj nutricionistu'"); anInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); anInfo.setForeground(new Color(200, 210, 240));
        anHeaderLeft.add(Box.createVerticalStrut(12)); anHeaderLeft.add(anTitle); anHeaderLeft.add(Box.createVerticalStrut(3)); anHeaderLeft.add(anInfo);
        JButton anNazadBtn = new JButton("\u2190 Nazad na Admin panel"); anNazadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13)); anNazadBtn.setForeground(Color.WHITE);
        anNazadBtn.setBorderPainted(false); anNazadBtn.setContentAreaFilled(false); anNazadBtn.setFocusPainted(false); anNazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); anNazadBtn.addActionListener(e -> prikaziPocetniPanelAdmin());
        anHeader.add(anHeaderLeft, BorderLayout.WEST); anHeader.add(anNazadBtn, BorderLayout.EAST); main.add(anHeader, BorderLayout.NORTH);
        String[] anKolone = {"ID", "Ime", "Prezime", "Licenca", "Email"};
        adminNutricionistiModel = new DefaultTableModel(anKolone, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        JTable anTable = new JTable(adminNutricionistiModel); styleTable(anTable);
        anTable.getColumnModel().getColumn(0).setMinWidth(0); anTable.getColumnModel().getColumn(0).setMaxWidth(0); anTable.getColumnModel().getColumn(0).setWidth(0);
        anTable.getColumnModel().getColumn(1).setPreferredWidth(150); anTable.getColumnModel().getColumn(2).setPreferredWidth(150); anTable.getColumnModel().getColumn(3).setPreferredWidth(150); anTable.getColumnModel().getColumn(4).setPreferredWidth(280);
        anTable.setRowHeight(40); anTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane anScroll = new JScrollPane(anTable); anScroll.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(20, 25, 0, 25), new LineBorder(NUTR_BORDER, 1, true)));
        main.add(anScroll, BorderLayout.CENTER);
        JPanel anFooter = new JPanel(new BorderLayout()); anFooter.setBackground(BG); anFooter.setBorder(new EmptyBorder(12, 25, 16, 25));
        JLabel anCount = new JLabel("Ukupno nutricionista: 0"); anCount.setFont(new Font("Segoe UI", Font.PLAIN, 12)); anCount.setForeground(Color.GRAY);
        JButton anBlokirajBtn = new JButton("Blokiraj nutricionistu");
        anBlokirajBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); anBlokirajBtn.setBackground(new Color(200, 50, 60)); anBlokirajBtn.setForeground(Color.WHITE);
        anBlokirajBtn.setFocusPainted(false); anBlokirajBtn.setBorderPainted(false); anBlokirajBtn.setOpaque(true); anBlokirajBtn.setBorder(new EmptyBorder(10, 24, 10, 24)); anBlokirajBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        anBlokirajBtn.addActionListener(e -> {
            int row = anTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Molimo selektujte nutricionistu.", "Nije selektovano", JOptionPane.WARNING_MESSAGE); return; }
            int id = (int) adminNutricionistiModel.getValueAt(row, 0);
            String ime = (String) adminNutricionistiModel.getValueAt(row, 1);
            String prezime = (String) adminNutricionistiModel.getValueAt(row, 2);
            int ok = JOptionPane.showConfirmDialog(this, "Blokiraj nutricionistu: " + ime + " " + prezime + "?", "Potvrda", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) return;
            try { 
            	dao.blokirajNutricionistu(id);
            	JOptionPane.showMessageDialog(this, "Nutricionista " + ime + " " + prezime + " je blokiran.", "Uspesno", JOptionPane.INFORMATION_MESSAGE);
            	osveziTabeluNutricionisti();
            }
            catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }
        });
        anFooter.add(anCount, BorderLayout.WEST); anFooter.add(anBlokirajBtn, BorderLayout.EAST); main.add(anFooter, BorderLayout.SOUTH);
        adminNutricionistiModel.addTableModelListener(ev -> anCount.setText("Ukupno nutricionista: " + adminNutricionistiModel.getRowCount()));
        return main;
    }
    
    public void osveziTabeluKorisnici() {
    	if (adminKorisniciModel == null) return;
        adminKorisniciModel.setRowCount(0);
        try {
            for (Korisnik k : dao.vratiListuKorisnika()) {
                adminKorisniciModel.addRow(new Object[]{ k.getIdKorisnika(), k.getIme(), k.getPrezime(), k.getEmail() });
            }
        } catch (Exception ex) { prikaziGresku("Greska pri ucitavanju korisnika: " + ex.getMessage()); return; }
    }
    
    public void osveziTabeluNutricionisti() {
    	if (adminNutricionistiModel == null) return;
        adminNutricionistiModel.setRowCount(0);
        try {
            for (Nutricionista n : dao.vratiListuNutricionista()) {
                adminNutricionistiModel.addRow(new Object[]{ n.getIdNutricioniste(), n.getIme(), n.getPrezime(), n.getLicenca(), n.getEmail() });
            }
        } catch (Exception ex) { prikaziGresku("Greska pri ucitavanju nutricionista: " + ex.getMessage()); return; }
	}
    
    public void administracijaKorisnikaPanel() {
        osveziTabeluKorisnici();
        cardLayout.show(cardContainer, CARD_ADMIN_KORISNICI);
    }

    public void administracijaNutricionistaPanel() {
        osveziTabeluNutricionisti();
        cardLayout.show(cardContainer, CARD_ADMIN_NUTRICIONISTI);
    }

    public void administracijaProizvodaPanel() {
        osveziTabeluProizvodi();
        cardLayout.show(cardContainer, CARD_ADMIN_PROIZVODI);
    }

    public void administracijaZahtevaPanel() {
        cardLayout.show(cardContainer, CARD_ADMIN_ZAHTEVI);
    }

    public void osveziTabeluProizvodi() {
        if (adminProizvodiModel == null) return;
        adminProizvodiModel.setRowCount(0);
        try {
            for (Proizvod p : dao.vratiListuProizvoda()) {
                adminProizvodiModel.addRow(new Object[]{
                    p.getBarKod(), p.getNaziv(),
                    p.isPosan() ? "Da" : "Ne", p.isHalal() ? "Da" : "Ne",
                    p.getEnergijaKJ(), p.getEnergijaKcal(),
                    p.getMasti(), p.getZasiceneMasti(),
                    p.getUgljeniHidrati(), p.getSeceri(),
                    p.getVlakna(), p.getProteini(), p.getSo(),
                    p.getPreporucenaDnevnaDoza(), p.getSastojci()
                });
            }
        } catch (Exception ex) { prikaziGresku("Greska pri ucitavanju proizvoda: " + ex.getMessage()); }
    }

    public void prikaziPanelEvidencijeZarade() {
        JDialog dialog = new JDialog(this, "Evidencija zarade", true);
        dialog.setSize(1050, 540); dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout()); dialog.getContentPane().setBackground(BG);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(HEADER_COLOR);
        JLabel headerLbl = new JLabel("Svi racuni — obračun provizije (20%)");
        headerLbl.setFont(new Font("Segoe UI", Font.BOLD, 17)); headerLbl.setForeground(Color.WHITE);
        header.add(headerLbl);
        dialog.add(header, BorderLayout.NORTH);

        // ID kolona je sakrivena (index 0)
        String[] kolone = {"ID", "Korisnik", "Nutricionista", "Iznos (RSD)", "Status", "Provizija (RSD)", "Datum"};
        DefaultTableModel model = new DefaultTableModel(kolone, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        try {
            ArrayList<RacunDTO> racuni = dao.vratiSveRacune();
            for (RacunDTO r : racuni) {
                model.addRow(new Object[]{
                    r.getIdRacuna(),
                    r.getImeKorisnika(),
                    r.getImeNutricioniste(),
                    r.getIznos() + " RSD",
                    "placen".equals(r.getStatus()) ? "Placen" : "Neplacen",
                    r.getProvizija() > 0 ? r.getProvizija() + " RSD" : "-",
                    r.getDatum()
                });
            }
        } catch (Exception ex) { prikaziGresku("Greska pri ucitavanju racuna: " + ex.getMessage()); }

        JTable table = new JTable(model); styleTable(table);
        // Sakrij ID kolonu
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 15, 5, 15), new LineBorder(NUTR_BORDER, 1, true)));
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG); footer.setBorder(new EmptyBorder(8, 15, 12, 15));

        JLabel infoLbl = new JLabel("Ukupno: " + model.getRowCount() + " racuna");
        infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); infoLbl.setForeground(Color.GRAY);

        JLabel ukupnoProvLbl = new JLabel("");
        ukupnoProvLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ukupnoProvLbl.setForeground(new Color(30, 100, 50));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton provBtn    = styleBtn("Obračunaj proviziju", new Color(70, 100, 180));
        JButton zatvoriBtn = styleBtn("Zatvori", new Color(200, 60, 60));

        provBtn.addActionListener(e -> {
            try {
                double provizija = dao.obracunajProviziju();
                ukupnoProvLbl.setText("Ukupna provizija: " + provizija + " RSD");
            } catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }
        });

        zatvoriBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(ukupnoProvLbl); btnPanel.add(provBtn); btnPanel.add(zatvoriBtn);
        footer.add(infoLbl, BorderLayout.WEST); footer.add(btnPanel, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════
    // ADMIN - PANEL PROIZVODI
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelAdminProizvodi() {
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(BG);

        // ── Header ──
        JPanel apHeader = new JPanel(new BorderLayout());
        apHeader.setBackground(HEADER_COLOR); apHeader.setPreferredSize(new Dimension(0, 70)); apHeader.setBorder(new EmptyBorder(0, 25, 0, 25));
        JPanel apHeaderLeft = new JPanel(); apHeaderLeft.setLayout(new BoxLayout(apHeaderLeft, BoxLayout.Y_AXIS)); apHeaderLeft.setOpaque(false);
        JLabel apTitle = new JLabel("Proizvodi u sistemu"); apTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); apTitle.setForeground(Color.WHITE);
        JLabel apInfo = new JLabel("Dupli klik na red = ažuriranje svih polja  |  Selektuj + 'Obriši' = brisanje"); apInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); apInfo.setForeground(new Color(200, 210, 240));
        apHeaderLeft.add(Box.createVerticalStrut(12)); apHeaderLeft.add(apTitle); apHeaderLeft.add(Box.createVerticalStrut(3)); apHeaderLeft.add(apInfo);
        JButton apNazadBtn = new JButton("\u2190 Nazad na Admin panel");
        apNazadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13)); apNazadBtn.setForeground(Color.WHITE);
        apNazadBtn.setBorderPainted(false); apNazadBtn.setContentAreaFilled(false); apNazadBtn.setFocusPainted(false);
        apNazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        apNazadBtn.addActionListener(e -> prikaziPocetniPanelAdmin());
        apHeader.add(apHeaderLeft, BorderLayout.WEST); apHeader.add(apNazadBtn, BorderLayout.EAST);
        main.add(apHeader, BorderLayout.NORTH);

        // ── Tabela — sve kolone, nijedna nije direktno editabilna (koristimo dialog) ──
        String[] apKolone = {
            "Barkod", "Naziv", "Posno", "Halal",
            "Energija (kJ)", "Energija (kcal)", "Masti (g)", "Zas. masti (g)",
            "Ugljeni hid. (g)", "Šećeri (g)", "Vlakna (g)", "Proteini (g)", "So (g)",
            "Prep. dnevna doza", "Sastojci"
        };

        adminProizvodiModel = new DefaultTableModel(apKolone, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable apTable = new JTable(adminProizvodiModel);
        styleTable(apTable);
        apTable.setRowHeight(38);
        apTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        apTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        apTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        apTable.getColumnModel().getColumn(2).setPreferredWidth(55);
        apTable.getColumnModel().getColumn(3).setPreferredWidth(55);
        for (int i = 4; i <= 12; i++) apTable.getColumnModel().getColumn(i).setPreferredWidth(85);
        apTable.getColumnModel().getColumn(13).setPreferredWidth(130);
        apTable.getColumnModel().getColumn(14).setPreferredWidth(180);

        apTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = apTable.getSelectedRow();
                    if (row == -1) return;
                    prikaziFormuZaAzuriranje(row);
                }
            }
        });

        JScrollPane apScroll = new JScrollPane(apTable);
        apScroll.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(20, 25, 0, 25), new LineBorder(NUTR_BORDER, 1, true)));
        main.add(apScroll, BorderLayout.CENTER);

        JPanel apFooter = new JPanel(new BorderLayout()); apFooter.setBackground(BG); apFooter.setBorder(new EmptyBorder(12, 25, 16, 25));
        JLabel apCount = new JLabel("Ukupno proizvoda: 0"); apCount.setFont(new Font("Segoe UI", Font.PLAIN, 12)); apCount.setForeground(Color.GRAY);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); btns.setBackground(BG);

        JButton apDodajBtn = new JButton("+ Dodaj proizvod");
        apDodajBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); apDodajBtn.setBackground(SUCCESS_GREEN); apDodajBtn.setForeground(Color.WHITE);
        apDodajBtn.setFocusPainted(false); apDodajBtn.setBorderPainted(false); apDodajBtn.setOpaque(true);
        apDodajBtn.setBorder(new EmptyBorder(10, 24, 10, 24)); apDodajBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        apDodajBtn.addActionListener(e -> dodajProizvodAdminPanel());

        JButton apObrisiBtn = new JButton("Obriši proizvod");
        apObrisiBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); apObrisiBtn.setBackground(new Color(200, 50, 60)); apObrisiBtn.setForeground(Color.WHITE);
        apObrisiBtn.setFocusPainted(false); apObrisiBtn.setBorderPainted(false); apObrisiBtn.setOpaque(true);
        apObrisiBtn.setBorder(new EmptyBorder(10, 24, 10, 24)); apObrisiBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        apObrisiBtn.addActionListener(e -> {
            int row = apTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Molimo selektujte proizvod.", "Nije selektovano", JOptionPane.WARNING_MESSAGE); return; }
            String barkod = (String) adminProizvodiModel.getValueAt(row, 0);
            String naziv  = (String) adminProizvodiModel.getValueAt(row, 1);
            int ok = JOptionPane.showConfirmDialog(this,
                "Brišete proizvod:\n\"" + naziv + "\" (barkod: " + barkod + ")?",
                "Potvrda brisanja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) return;
            try { dao.obrisiProizvod(barkod); adminProizvodiModel.removeRow(row); obrisanProizvodPoruka(barkod); }
            catch (Exception ex) { prikaziGresku("Greška: " + ex.getMessage()); }
        });

        btns.add(apDodajBtn); btns.add(apObrisiBtn);
        apFooter.add(apCount, BorderLayout.WEST); apFooter.add(btns, BorderLayout.EAST);
        main.add(apFooter, BorderLayout.SOUTH);
        adminProizvodiModel.addTableModelListener(ev -> apCount.setText("Ukupno proizvoda: " + adminProizvodiModel.getRowCount()));
        return main;
    }

    // ═══════════════════════════════════════════════════════════
    // FORMA ZA AZURIRANJE — dupli klik na red
    // ═══════════════════════════════════════════════════════════
    private void prikaziFormuZaAzuriranje(int row) {
        String barkod   = s(adminProizvodiModel.getValueAt(row, 0));
        String naziv    = s(adminProizvodiModel.getValueAt(row, 1));
        boolean posan   = "Da".equals(adminProizvodiModel.getValueAt(row, 2));
        boolean halal   = "Da".equals(adminProizvodiModel.getValueAt(row, 3));
        String enKJ     = s(adminProizvodiModel.getValueAt(row, 4));
        String enKcal   = s(adminProizvodiModel.getValueAt(row, 5));
        String masti    = s(adminProizvodiModel.getValueAt(row, 6));
        String zasMasti = s(adminProizvodiModel.getValueAt(row, 7));
        String ugljH    = s(adminProizvodiModel.getValueAt(row, 8));
        String seceri   = s(adminProizvodiModel.getValueAt(row, 9));
        String vlakna   = s(adminProizvodiModel.getValueAt(row, 10));
        String proteini = s(adminProizvodiModel.getValueAt(row, 11));
        String so       = s(adminProizvodiModel.getValueAt(row, 12));
        String doza     = s(adminProizvodiModel.getValueAt(row, 13));
        String sastojci = s(adminProizvodiModel.getValueAt(row, 14));

        JDialog d = new JDialog(this, "Ažuriranje — Barkod: " + barkod, true);
        d.setSize(620, 700); d.setLocationRelativeTo(this); d.setLayout(new BorderLayout()); d.getContentPane().setBackground(BG);

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14)); hdr.setBackground(HEADER_COLOR);
        hdr.add(makeWhiteLbl("Ažurirajte podatke o proizvodu"));
        d.add(hdr, BorderLayout.NORTH);

        // ── Forma — direktno kreiramo i čuvamo reference ──
        JPanel form = new JPanel(); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG); form.setBorder(new EmptyBorder(18, 28, 10, 28));

        JTextField fBarkod = pField(barkod); fBarkod.setEditable(false); fBarkod.setBackground(new Color(235, 235, 242));
        JTextField fNaziv    = pField(naziv);
        JCheckBox  cbPosan   = pCheck("Posan", posan);
        JCheckBox  cbHalal   = pCheck("Halal", halal);
        JTextField fEnKJ     = pField(enKJ);
        JTextField fEnKcal   = pField(enKcal);
        JTextField fMasti    = pField(masti);
        JTextField fZasMasti = pField(zasMasti);
        JTextField fUgljH    = pField(ugljH);
        JTextField fSeceri   = pField(seceri);
        JTextField fVlakna   = pField(vlakna);
        JTextField fProteini = pField(proteini);
        JTextField fSo       = pField(so);
        JTextArea  taDoza    = pArea(doza);
        JTextArea  taSastojci= pArea(sastojci);

        form.add(pLbl("Barkod (nije editabilan)")); form.add(Box.createVerticalStrut(4)); form.add(fBarkod); form.add(Box.createVerticalStrut(12));
        form.add(pLbl("Naziv"));                    form.add(Box.createVerticalStrut(4)); form.add(fNaziv);   form.add(Box.createVerticalStrut(12));
        JPanel cbRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0)); cbRow.setBackground(BG); cbRow.setAlignmentX(Component.LEFT_ALIGNMENT); cbRow.add(cbPosan); cbRow.add(cbHalal);
        form.add(cbRow); form.add(Box.createVerticalStrut(12));
        form.add(pRow("Energija (kJ)", fEnKJ, "Energija (kcal)", fEnKcal));         form.add(Box.createVerticalStrut(10));
        form.add(pRow("Masti (g)", fMasti, "Zasićene masti (g)", fZasMasti)); form.add(Box.createVerticalStrut(10));
        form.add(pRow("Ugljeni hidrati (g)", fUgljH, "Šećeri (g)", fSeceri)); form.add(Box.createVerticalStrut(10));
        form.add(pRow("Vlakna (g)", fVlakna, "Proteini (g)", fProteini));           form.add(Box.createVerticalStrut(10));
        form.add(pRowSingle("So (g)", fSo));                                         form.add(Box.createVerticalStrut(12));
        form.add(pLbl("Preporučena dnevna doza")); form.add(Box.createVerticalStrut(4));
        form.add(pAreaScroll(taDoza, 75));          form.add(Box.createVerticalStrut(12));
        form.add(pLbl("Sastojci"));                 form.add(Box.createVerticalStrut(4));
        form.add(pAreaScroll(taSastojci, 90));      form.add(Box.createVerticalStrut(10));

        JScrollPane scrollForm = new JScrollPane(form); scrollForm.setBorder(null); scrollForm.getVerticalScrollBar().setUnitIncrement(12);
        d.add(scrollForm, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); footer.setBackground(BG); footer.setBorder(new MatteBorder(1, 0, 0, 0, NUTR_BORDER));
        JButton sacuvajBtn = styleBtn("Sačuvaj izmene", SUCCESS_GREEN);
        JButton otkaziBtn  = styleBtn("Otkaži", new Color(160, 160, 175)); otkaziBtn.setForeground(HEADER_COLOR);
        otkaziBtn.addActionListener(ev -> d.dispose());
        sacuvajBtn.addActionListener(ev -> {
            try {
            	String bk = fBarkod.getText().trim();
                String n=fNaziv.getText().trim();
                String s=taSastojci.getText().trim();
                String pos=cbPosan.isSelected() ? "1" : "0";
                String h=cbHalal.isSelected() ? "1" : "0";
                String energijaKJ=fEnKJ.getText().trim();
                String energijaKcal=fEnKcal.getText().trim();
                String m=fMasti.getText().trim();
                String zasiceneMasti=fZasMasti.getText().trim();
                String ugljeniHidrati=fUgljH.getText().trim();
                String sec=fSeceri.getText().trim();
                String v=fVlakna.getText().trim();
                String prot=fProteini.getText().trim();
                String so1=fSo.getText().trim();
                String preporucenaDoza=taDoza.getText().trim();
                
                if (!ProizvodKontroler.validirajProizvod(n, bk, s, pos, h, energijaKJ, energijaKcal, m, zasiceneMasti, ugljeniHidrati, sec, v, prot, so1, preporucenaDoza)) { 
                	prikaziGresku("Polja su obavezna");
                	return; 
                	}
                Proizvod p = new Proizvod(bk, n, s, Integer.parseInt(pos), Integer.parseInt(h), 
                		Integer.parseInt(energijaKJ), Integer.parseInt(energijaKcal), Double.parseDouble(m), 
                		Double.parseDouble(zasiceneMasti), Double.parseDouble(ugljeniHidrati), Double.parseDouble(sec), 
                		Double.parseDouble(v), Double.parseDouble(prot), Double.parseDouble(so1), preporucenaDoza);
                dao.azurirajProizvod(p);
                adminProizvodiModel.setValueAt(p.getNaziv(),    row, 1);
                adminProizvodiModel.setValueAt(cbPosan.isSelected() ? "Da" : "Ne", row, 2);
                adminProizvodiModel.setValueAt(cbHalal.isSelected() ? "Da" : "Ne", row, 3);
                adminProizvodiModel.setValueAt(p.getEnergijaKJ(),    row, 4);
                adminProizvodiModel.setValueAt(p.getEnergijaKcal(),  row, 5);
                adminProizvodiModel.setValueAt(p.getMasti(),          row, 6);
                adminProizvodiModel.setValueAt(p.getZasiceneMasti(),  row, 7);
                adminProizvodiModel.setValueAt(p.getUgljeniHidrati(), row, 8);
                adminProizvodiModel.setValueAt(p.getSeceri(),         row, 9);
                adminProizvodiModel.setValueAt(p.getVlakna(),         row, 10);
                adminProizvodiModel.setValueAt(p.getProteini(),       row, 11);
                adminProizvodiModel.setValueAt(p.getSo(),             row, 12);
                adminProizvodiModel.setValueAt(p.getPreporucenaDnevnaDoza(), row, 13);
                adminProizvodiModel.setValueAt(p.getSastojci(),       row, 14);
                azuriranProizvodPoruka(barkod);
                d.dispose();
            } catch (NumberFormatException nfe) {
                prikaziGresku("Greška: Numerička polja moraju biti brojevi!");
            } catch (Exception ex) {
                prikaziGresku("Greška pri ažuriranju: " + ex.getMessage());
            }
        });
        footer.add(sacuvajBtn); footer.add(otkaziBtn);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════
    // FORMA ZA DODAVANJE NOVOG PROIZVODA
    // ═══════════════════════════════════════════════════════════
    private void dodajProizvodAdminPanel() {
        JDialog d = new JDialog(this, "Dodaj novi proizvod", true);
        d.setSize(620, 720); d.setLocationRelativeTo(this); d.setLayout(new BorderLayout()); d.getContentPane().setBackground(BG);

        JPanel hdr = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14)); hdr.setBackground(HEADER_COLOR);
        hdr.add(makeWhiteLbl("Unos novog proizvoda u bazu"));
        d.add(hdr, BorderLayout.NORTH);

        JPanel form = new JPanel(); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG); form.setBorder(new EmptyBorder(18, 28, 10, 28));

        // ── Kreiranje i čuvanje direktnih referenci ──
        JTextField fBarkod   = pField("");
        JTextField fNaziv    = pField("");
        JCheckBox  cbPosan   = pCheck("Posan", false);
        JCheckBox  cbHalal   = pCheck("Halal", false);
        JTextField fEnKJ     = pField("");
        JTextField fEnKcal   = pField("");
        JTextField fMasti    = pField("");
        JTextField fZasMasti = pField("");
        JTextField fUgljH    = pField("");
        JTextField fSeceri   = pField("");
        JTextField fVlakna   = pField("");
        JTextField fProteini = pField("");
        JTextField fSo       = pField("");
        JTextArea  taDoza    = pArea("");
        JTextArea  taSastojci= pArea("");

        form.add(pLbl("Barkod *"));                 
        form.add(Box.createVerticalStrut(4)); 
        form.add(fBarkod);   form.add(Box.createVerticalStrut(12));
        form.add(pLbl("Naziv"));                    
        form.add(Box.createVerticalStrut(4)); form.add(fNaziv);    
        form.add(Box.createVerticalStrut(12));
        JPanel cbRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0)); cbRow.setBackground(BG); cbRow.setAlignmentX(Component.LEFT_ALIGNMENT); cbRow.add(cbPosan); cbRow.add(cbHalal);
        form.add(cbRow); form.add(Box.createVerticalStrut(12));
        form.add(pRow("Energija (kJ)", fEnKJ, "Energija (kcal)", fEnKcal));              form.add(Box.createVerticalStrut(10));
        form.add(pRow("Masti (g)", fMasti, "Zasićene masti (g)", fZasMasti));      form.add(Box.createVerticalStrut(10));
        form.add(pRow("Ugljeni hidrati (g)", fUgljH, "Šećeri (g)", fSeceri)); form.add(Box.createVerticalStrut(10));
        form.add(pRow("Vlakna (g)", fVlakna, "Proteini (g)", fProteini));                form.add(Box.createVerticalStrut(10));
        form.add(pRowSingle("So (g)", fSo));                                              form.add(Box.createVerticalStrut(12));
        form.add(pLbl("Preporučena dnevna doza")); form.add(Box.createVerticalStrut(4));
        form.add(pAreaScroll(taDoza, 75));          form.add(Box.createVerticalStrut(12));
        form.add(pLbl("Sastojci"));                 form.add(Box.createVerticalStrut(4));
        form.add(pAreaScroll(taSastojci, 90));      form.add(Box.createVerticalStrut(10));

        JScrollPane scrollForm = new JScrollPane(form); scrollForm.setBorder(null); scrollForm.getVerticalScrollBar().setUnitIncrement(12);
        d.add(scrollForm, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); footer.setBackground(BG); footer.setBorder(new MatteBorder(1, 0, 0, 0, NUTR_BORDER));
        JButton dodajBtn  = styleBtn("Dodaj proizvod", SUCCESS_GREEN);
        JButton otkaziBtn = styleBtn("Otkaži", new Color(160, 160, 175)); otkaziBtn.setForeground(HEADER_COLOR);
        otkaziBtn.addActionListener(ev -> d.dispose());
        dodajBtn.addActionListener(ev -> {
            try {
                String barkod = fBarkod.getText().trim();
                String naziv=fNaziv.getText().trim();
                String sastojci=taSastojci.getText().trim();
                String posan=cbPosan.isSelected() ? "1" : "0";
                String halal=cbHalal.isSelected() ? "1" : "0";
                String energijaKJ=fEnKJ.getText().trim();
                String energijaKcal=fEnKcal.getText().trim();
                String masti=fMasti.getText().trim();
                String zasiceneMasti=fZasMasti.getText().trim();
                String ugljeniHidrati=fUgljH.getText().trim();
                String seceri=fSeceri.getText().trim();
                String vlakna=fVlakna.getText().trim();
                String proteini=fProteini.getText().trim();
                String so=fSo.getText().trim();
                String preporucenaDoza=taDoza.getText().trim();
                
                if (!ProizvodKontroler.validirajProizvod(naziv, barkod, sastojci, posan, halal, energijaKJ, energijaKcal, masti, zasiceneMasti, ugljeniHidrati, seceri, vlakna, proteini, so, preporucenaDoza)) { 
                	prikaziGresku("Polja su obavezna");
                	return; 
                	}
                Proizvod p = new Proizvod(barkod, naziv, sastojci, Integer.parseInt(posan), Integer.parseInt(halal), 
                		Integer.parseInt(energijaKJ), Integer.parseInt(energijaKcal), Double.parseDouble(masti), 
                		Double.parseDouble(zasiceneMasti), Double.parseDouble(ugljeniHidrati), Double.parseDouble(seceri), 
                		Double.parseDouble(vlakna), Double.parseDouble(proteini), Double.parseDouble(so), preporucenaDoza);
                dao.dodajProizvod(p);
                osveziTabeluProizvodi();
                dodatProizvodPoruka(barkod);
                d.dispose();
            } catch (NumberFormatException nfe) {
                prikaziGresku("Greška: Numerčdka polja moraju biti brojevi!");
            } catch (Exception ex) {
                prikaziGresku("Greška pri dodavanju: " + ex.getMessage());
            }
        });
        footer.add(dodajBtn); footer.add(otkaziBtn);
        d.add(footer, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private JLabel makeWhiteLbl(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 15)); l.setForeground(Color.WHITE); return l;
    }
    private JLabel pLbl(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 13)); l.setForeground(HEADER_COLOR); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }
    private JTextField pField(String val) {
        JTextField f = new JTextField(val);
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(0, 10, 0, 10)));
        return f;
    }
    private JCheckBox pCheck(String lbl, boolean selected) {
        JCheckBox cb = new JCheckBox(lbl); cb.setBackground(BG); cb.setSelected(selected); cb.setFont(new Font("Segoe UI", Font.BOLD, 13)); return cb;
    }
    private JTextArea pArea(String val) {
        JTextArea ta = new JTextArea(val, 3, 0);
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ta.setLineWrap(true); ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(6, 8, 6, 8));
        return ta;
    }
    private JScrollPane pAreaScroll(JTextArea ta, int height) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(new LineBorder(NUTR_BORDER, 1, true));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return sp;
    }
    /** Dva polja (sa labelama) u jednom redu — prima direktne reference na polja */
    private JPanel pRow(String l1, JTextField f1, String l2, JTextField f2) {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setBackground(BG); row.setAlignmentX(Component.LEFT_ALIGNMENT); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        JPanel p1 = new JPanel(); p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS)); p1.setBackground(BG);
        p1.add(pLbl(l1)); p1.add(Box.createVerticalStrut(4)); p1.add(f1);
        JPanel p2 = new JPanel(); p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS)); p2.setBackground(BG);
        p2.add(pLbl(l2)); p2.add(Box.createVerticalStrut(4)); p2.add(f2);
        row.add(p1); row.add(p2); return row;
    }
    /** Jedno polje u redu sa labelom — prima direktnu referencu na polje */
    private JPanel pRowSingle(String lbl, JTextField f) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setBackground(BG); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(pLbl(lbl)); p.add(Box.createVerticalStrut(4));
        f.setMaximumSize(new Dimension(260, 38)); p.add(f); return p;
    }
    private String s(Object o) { return o == null ? "" : o.toString(); }

    // ═══════════════════════════════════════════════════════════
    // KORISNIK PANEL 
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelKorisnik() {
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(0, 25, 0, 25));

        // Leva strana: naziv panela
        JLabel titleLbl = new JLabel("Korisnički Panel");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);

        // Centar: ime i prezime ulogovanog korisnika
        korisnikImeLabel = new JLabel("", SwingConstants.CENTER);
        korisnikImeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        korisnikImeLabel.setForeground(new Color(200, 220, 255));

        // Desna strana: dugme Odjavi se
        JButton odjaviBtn = new JButton("Odjavi se");
        odjaviBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        odjaviBtn.setForeground(Color.WHITE);
        odjaviBtn.setBackground(new Color(220, 53, 69));
        odjaviBtn.setFocusPainted(false);
        odjaviBtn.setBorderPainted(false);
        odjaviBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        odjaviBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        odjaviBtn.addActionListener(e ->{
        	korisnik = null;
        	ocistiPoljaKorisnik();
        	resetujKomentare();
        	barkodField.setText("");
        	prikaziPocetniPanel();
        });

        header.add(titleLbl,       BorderLayout.WEST);
        header.add(korisnikImeLabel, BorderLayout.CENTER);
        header.add(odjaviBtn,      BorderLayout.EAST);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));
        body.add(buildKorisnikSearchPanel());
        body.add(Box.createVerticalStrut(15));
        body.add(buildProductCard());
        body.add(Box.createVerticalStrut(15));
        body.add(buildNutritionPanel());
        body.add(Box.createVerticalStrut(15));
        body.add(buildSastojciPanel());
        body.add(Box.createVerticalStrut(15));
        body.add(buildKomentariNutricionistaPanelWrapper());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        main.add(header, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildKorisnikSearchPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); p.setBackground(BG); p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel lbl = new JLabel("Unesite barkod:"); lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14)); lbl.setForeground(HEADER_COLOR);
        barkodField = new JTextField(22); barkodField.setFont(new Font("Segoe UI", Font.PLAIN, 14)); barkodField.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(6, 10, 6, 10)));
        JButton pretraziBtn   = styleBtn("Pretraži",            new Color(30, 80, 140));
        JButton nutrBtn       = styleBtn("Pregled nutricionista", new Color(25, 85, 55));
        JButton skeniranBtn   = styleBtn("Pregled skeniranih",  new Color(100, 60, 160));
        JButton inboxBtn      = styleBtn("Inbox",               new Color(160, 90, 30));
        pretraziBtn.addActionListener(e -> prikaziPanelBarKod());
        barkodField.addActionListener(e -> prikaziPanelBarKod());
        nutrBtn.addActionListener(e -> prikaziPanelNutricionisti());
        skeniranBtn.addActionListener(e -> prikaziPanelSkenirani());
        inboxBtn.addActionListener(e -> prikaziPanelInbox());
        JButton racuniBtn = styleBtn("Moji racuni", new Color(130, 80, 180));
        racuniBtn.addActionListener(e -> prikaziPanelMojiRacuni());
        p.add(lbl); p.add(barkodField); p.add(pretraziBtn); p.add(skeniranBtn); p.add(nutrBtn); p.add(inboxBtn); p.add(racuniBtn);
        return p;
    }

    private JPanel buildProductCard() {
        JPanel card = new JPanel(new BorderLayout(15, 10)); card.setBackground(CARD_BG); card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(20, 20, 20, 20))); card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        JPanel left = new JPanel(); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); left.setBackground(CARD_BG);
        nazivLabel = new JLabel("Naziv proizvoda"); nazivLabel.setFont(new Font("Segoe UI", Font.BOLD, 20)); nazivLabel.setForeground(HEADER_COLOR);
        barkodInfoLabel = new JLabel("Bar kod: -"); barkodInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13)); barkodInfoLabel.setForeground(Color.GRAY);
        JPanel tagPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); tagPanel.setBackground(CARD_BG);
        posanLabel = makeTag("-"); halalLabel = makeTag("-"); tagPanel.add(posanLabel); tagPanel.add(halalLabel);
        left.add(nazivLabel); left.add(Box.createVerticalStrut(8)); left.add(tagPanel); left.add(Box.createVerticalStrut(6)); left.add(barkodInfoLabel);
        card.add(left, BorderLayout.CENTER);
        return card;
    }

    private JLabel makeTag(String text) {
        JLabel tag = new JLabel(text); tag.setFont(new Font("Segoe UI", Font.PLAIN, 12)); tag.setForeground(new Color(80, 80, 80)); tag.setOpaque(true);
        tag.setBackground(new Color(240, 240, 245)); tag.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(4, 10, 4, 10)));
        return tag;
    }

    private JPanel buildNutritionPanel() {
        JPanel wrapper = new JPanel(); 
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS)); 
        wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(15, 20, 15, 20)));
        JLabel title = new JLabel("Nutritivne Vrednosti (na 100g/ml)"); 
        title.setFont(new Font("Segoe UI", Font.BOLD, 17)); 
        title.setForeground(HEADER_COLOR);
        wrapper.add(title); 
        wrapper.add(Box.createVerticalStrut(12)); 
        wrapper.add(buildNutritionHeader());
        energijaLabel = new JLabel("-"); 
        mastiLabel = new JLabel("-"); 
        zasiceneMastiLabel = new JLabel("-"); 
        ugljeniHidratiLabel = new JLabel("-"); 
        seceriLabel = new JLabel("-"); 
        vlaknaLabel = new JLabel("-"); 
        proteiniLabel = new JLabel("-"); 
        soLabel = new JLabel("-");
        dozaLabel = new JLabel("-");
        wrapper.add(buildNutritionRow("Energija", energijaLabel, true)); 
        wrapper.add(buildNutritionRow("Masti", mastiLabel, false));
        wrapper.add(buildNutritionRow("Zasićene masti", zasiceneMastiLabel, true)); 
        wrapper.add(buildNutritionRow("Ugljeni hidrati", ugljeniHidratiLabel, false));
        wrapper.add(buildNutritionRow("Šećeri", seceriLabel, true)); 
        wrapper.add(buildNutritionRow("Vlakna", vlaknaLabel, false)); 
        wrapper.add(buildNutritionRow("Proteini", proteiniLabel, true)); 
        wrapper.add(buildNutritionRow("So", soLabel, false));
        wrapper.add(buildNutritionRow("Preporučena dnevna doza", dozaLabel, true));
        return wrapper;
    }

    private JPanel buildNutritionHeader() {
        JPanel row = new JPanel(new GridLayout(1, 2)); row.setBackground(new Color(235, 238, 245)); row.setBorder(new EmptyBorder(8, 10, 8, 10)); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JLabel l1 = new JLabel("Nutritivna vrednost"); l1.setFont(new Font("Segoe UI", Font.BOLD, 13)); l1.setForeground(HEADER_COLOR);
        JLabel l2 = new JLabel("Količina"); l2.setFont(new Font("Segoe UI", Font.BOLD, 13)); l2.setForeground(HEADER_COLOR); l2.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(l1); row.add(l2); return row;
    }

    private JPanel buildNutritionRow(String naziv, JLabel vrednostLabel, boolean alt) {
        JPanel row = new JPanel(new GridLayout(1, 2)); row.setBackground(alt ? TABLE_ROW1 : TABLE_ROW2);
        row.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0, 0, 1, 0, NUTR_BORDER), new EmptyBorder(8, 10, 8, 10))); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel nameLbl = new JLabel(naziv); nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); nameLbl.setForeground(new Color(50, 50, 60));
        vrednostLabel.setFont(new Font("Segoe UI", Font.BOLD, 13)); vrednostLabel.setForeground(HEADER_COLOR); vrednostLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(nameLbl); row.add(vrednostLabel); return row;
    }

    private JPanel buildSastojciPanel() {
        JPanel wrapper = new JPanel(); wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS)); wrapper.setBackground(CARD_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(15, 20, 15, 20)));
        JLabel title = new JLabel("Sastav Proizvoda"); title.setFont(new Font("Segoe UI", Font.BOLD, 17)); title.setForeground(HEADER_COLOR);
        wrapper.add(title); wrapper.add(Box.createVerticalStrut(10));
        sastojciArea = new JTextArea("-"); sastojciArea.setFont(new Font("Segoe UI", Font.PLAIN, 13)); sastojciArea.setForeground(new Color(60, 60, 70));
        sastojciArea.setBackground(new Color(248, 249, 252)); sastojciArea.setLineWrap(true); sastojciArea.setWrapStyleWord(true); sastojciArea.setEditable(false);
        sastojciArea.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(10, 12, 10, 12)));
        wrapper.add(sastojciArea); return wrapper;
    }

    private JPanel buildKomentariNutricionistaPanelWrapper() {
        komentariNutricionistaPanel = new JPanel();
        komentariNutricionistaPanel.setLayout(new BoxLayout(komentariNutricionistaPanel, BoxLayout.Y_AXIS));
        komentariNutricionistaPanel.setBackground(CARD_BG);
        komentariNutricionistaPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(15, 20, 15, 20)));
        JLabel title = new JLabel("Komentari nutricionista");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(HEADER_COLOR);
        komentariNutricionistaPanel.add(title);
        komentariNutricionistaPanel.add(Box.createVerticalStrut(10));
        JLabel placeholder = new JLabel("Pretražite proizvod da biste videli komentare nutricionista.");
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        placeholder.setForeground(Color.GRAY);
        komentariNutricionistaPanel.add(placeholder);
        return komentariNutricionistaPanel;
    }

    /**
     * Osvezava panel komentara nutricionista za dati barkod.
     * Poziva se nakon sto se ucita proizvod.
     */
    public void osveziKomentareNutricionista(String barkod) {
        if (komentariNutricionistaPanel == null) return;
        Component titleComp = komentariNutricionistaPanel.getComponent(0);

        komentariNutricionistaPanel.removeAll();
        komentariNutricionistaPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.gridx = 0; gbcTitle.gridy = 0;
        gbcTitle.weightx = 1.0; gbcTitle.fill = GridBagConstraints.HORIZONTAL;
        gbcTitle.insets = new Insets(0, 0, 10, 0);
        komentariNutricionistaPanel.add(titleComp, gbcTitle);

        int gridRow = 1;
        try {
            ArrayList<KomentarDTO> komentari = dao.vratiKomentareZaProizvod(barkod);
            if (komentari == null || komentari.isEmpty()) {
                JLabel prazno = new JLabel("Nema komentara nutricionista za ovaj proizvod.");
                prazno.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                prazno.setForeground(Color.GRAY);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0; gbc.gridy = gridRow; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
                komentariNutricionistaPanel.add(prazno, gbc);
            } else {
                for (KomentarDTO k : komentari) {
                    String ime      = k.getIme()      != null ? k.getIme()      : "";
                    String prezime  = k.getPrezime()  != null ? k.getPrezime()  : "";
                    String ocena    = k.getOcena()    != 0    ? String.valueOf(k.getOcena()) : "-";
                    String komentar = k.getKomentar() != null ? k.getKomentar() : "-";
                    String doza     = k.getPreporucenaDoza() != null ? k.getPreporucenaDoza() : "-";

                    JPanel card = new JPanel(new BorderLayout());
                    card.setBackground(new Color(248, 249, 253));
                    card.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(200, 210, 230), 1, true),
                        new EmptyBorder(12, 15, 12, 15)));

                    JPanel cardHeader = new JPanel(new BorderLayout());
                    cardHeader.setOpaque(false);
                    JLabel imeLbl = new JLabel(ime + " " + prezime);
                    imeLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    imeLbl.setForeground(HEADER_COLOR);
                    JLabel ocenaLbl = new JLabel("Ocena: " + ocena + "/5");
                    ocenaLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    ocenaLbl.setForeground(new Color(180, 120, 10));
                    cardHeader.add(imeLbl, BorderLayout.WEST);
                    cardHeader.add(ocenaLbl, BorderLayout.EAST);

                    JPanel cardBody = new JPanel(new GridBagLayout());
                    cardBody.setOpaque(false);

                    JLabel komLbl = new JLabel("Komentar:");
                    komLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    komLbl.setForeground(new Color(80, 80, 100));
                    GridBagConstraints cbGbc0 = new GridBagConstraints();
                    cbGbc0.gridx = 0; cbGbc0.gridy = 0; cbGbc0.weightx = 1.0; cbGbc0.fill = GridBagConstraints.HORIZONTAL; cbGbc0.anchor = GridBagConstraints.WEST; cbGbc0.insets = new Insets(8, 0, 2, 0);
                    cardBody.add(komLbl, cbGbc0);

                    JTextArea komArea = new JTextArea(komentar);
                    komArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    komArea.setForeground(new Color(40, 40, 60));
                    komArea.setBackground(new Color(248, 249, 253));
                    komArea.setEditable(false); komArea.setLineWrap(true); komArea.setWrapStyleWord(true); komArea.setBorder(null);
                    GridBagConstraints cbGbc1 = new GridBagConstraints();
                    cbGbc1.gridx = 0; cbGbc1.gridy = 1; cbGbc1.weightx = 1.0; cbGbc1.fill = GridBagConstraints.BOTH; cbGbc1.anchor = GridBagConstraints.WEST; cbGbc1.weighty = 0; cbGbc1.insets = new Insets(0, 0, 6, 0);
                    cardBody.add(komArea, cbGbc1);

                    JLabel dozaLbl2 = new JLabel("Preporučena doza:");
                    dozaLbl2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    dozaLbl2.setForeground(new Color(80, 80, 100));
                    GridBagConstraints cbGbc2 = new GridBagConstraints();
                    cbGbc2.gridx = 0; cbGbc2.gridy = 2; cbGbc2.weightx = 1.0; cbGbc2.fill = GridBagConstraints.HORIZONTAL; cbGbc2.anchor = GridBagConstraints.WEST; cbGbc2.insets = new Insets(0, 0, 2, 0);
                    cardBody.add(dozaLbl2, cbGbc2);

                    JTextArea dozaArea2 = new JTextArea(doza);
                    dozaArea2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    dozaArea2.setForeground(new Color(40, 40, 60));
                    dozaArea2.setBackground(new Color(248, 249, 253));
                    dozaArea2.setEditable(false); dozaArea2.setLineWrap(true); dozaArea2.setWrapStyleWord(true); dozaArea2.setBorder(null);
                    GridBagConstraints cbGbc3 = new GridBagConstraints();
                    cbGbc3.gridx = 0; cbGbc3.gridy = 3; cbGbc3.weightx = 1.0; cbGbc3.fill = GridBagConstraints.BOTH; cbGbc3.anchor = GridBagConstraints.WEST; cbGbc3.insets = new Insets(0, 0, 0, 0);
                    cardBody.add(dozaArea2, cbGbc3);

                    card.add(cardHeader, BorderLayout.NORTH);
                    card.add(cardBody, BorderLayout.CENTER);

                    GridBagConstraints cardGbc = new GridBagConstraints();
                    cardGbc.gridx = 0; cardGbc.gridy = gridRow++;
                    cardGbc.weightx = 1.0; cardGbc.fill = GridBagConstraints.HORIZONTAL;
                    cardGbc.insets = new Insets(0, 0, 10, 0);
                    komentariNutricionistaPanel.add(card, cardGbc);
                }
            }
        } catch (Exception ex) {
            JLabel errLbl = new JLabel("Greška pri učitavanju komentara: " + ex.getMessage());
            errLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            errLbl.setForeground(ERROR_RED);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = gridRow; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
            komentariNutricionistaPanel.add(errLbl, gbc);
        }

        GridBagConstraints spacerGbc = new GridBagConstraints();
        spacerGbc.gridx = 0; spacerGbc.gridy = gridRow + 1; spacerGbc.weighty = 1.0; spacerGbc.fill = GridBagConstraints.VERTICAL;
        komentariNutricionistaPanel.add(Box.createVerticalGlue(), spacerGbc);

        komentariNutricionistaPanel.revalidate();
        komentariNutricionistaPanel.repaint();
    }

    public void prikaziPanelBarKod() {
        String barkod = barkodField.getText().trim();
        if (!BarKodKontoler.validirajBarKod(barkod)) { prikaziGresku("Morate uneti ispravan bar-kod"); return; }
        try {
        	if ("aktivan".equals(dao.proveriBarKod(barkod))) {
        		Proizvod p = dao.nadjiProizvod(barkod);
                if (p == null) { ocistiPoljaKorisnik(); JOptionPane.showMessageDialog(this, "Proizvod nije pronađen.", "Nije pronađen", JOptionPane.INFORMATION_MESSAGE); }
                else {
                	dao.unosSkeniranih(korisnik.getIdKorisnika(), p.getBarKod(), LocalDate.now());
                	prikazInformacijaOProizvodu(p);
                }
			}else {
				prikaziGresku("Ne posedujemo informacije o proizvodu sa unetim bar kodom");
			}
        } catch (Exception ex) { prikaziGresku("Greška pri povezivanju sa bazom: " + ex.getMessage()); }
    }

    public void prikazInformacijaOProizvodu(Proizvod p) {
    	barkodInfoLabel.setText("Bar kod: - "+p.getBarKod() );
        nazivLabel.setText(p.getNaziv());
        posanLabel.setText(p.isPosan() ? "Posno" : "Nije posno"); 
        posanLabel.setBackground(p.isPosan() ? new Color(220, 245, 220) : new Color(245, 240, 240));
        halalLabel.setText(p.isHalal() ? "Halal" : "Nije halal"); 
        halalLabel.setBackground(p.isHalal() ? new Color(220, 245, 220) : new Color(245, 240, 240));
        energijaLabel.setText(p.getEnergijaKJ() + " kJ / " + p.getEnergijaKcal() + " kcal");
        mastiLabel.setText(p.getMasti() + "g"); 
        zasiceneMastiLabel.setText(p.getZasiceneMasti() + "g"); 
        ugljeniHidratiLabel.setText(p.getUgljeniHidrati() + "g");
        seceriLabel.setText(p.getSeceri() + "g");
        vlaknaLabel.setText(p.getVlakna() + "g");
        proteiniLabel.setText(p.getProteini() + "g"); 
        soLabel.setText(p.getSo() + "g");
        dozaLabel.setText(p.getPreporucenaDnevnaDoza());
        sastojciArea.setText(p.getSastojci() != null ? p.getSastojci() : "-");
        osveziKomentareNutricionista(p.getBarKod());
        revalidate(); 
        repaint();
    }

    private void ocistiPoljaKorisnik() {
        nazivLabel.setText("Naziv proizvoda"); barkodInfoLabel.setText("Bar kod: -");
        posanLabel.setText("-"); posanLabel.setBackground(new Color(240, 240, 245)); halalLabel.setText("-"); halalLabel.setBackground(new Color(240, 240, 245));
        energijaLabel.setText("-"); mastiLabel.setText("-"); zasiceneMastiLabel.setText("-"); ugljeniHidratiLabel.setText("-"); 
        seceriLabel.setText("-"); vlaknaLabel.setText("-"); dozaLabel.setText("-");
        proteiniLabel.setText("-"); soLabel.setText("-"); sastojciArea.setText("-");
    }

    private void resetujKomentare() {
        if (komentariNutricionistaPanel == null) return;
        komentariNutricionistaPanel.removeAll();
        komentariNutricionistaPanel.setLayout(new BoxLayout(komentariNutricionistaPanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Komentari nutricionista");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(HEADER_COLOR);
        komentariNutricionistaPanel.add(title);
        komentariNutricionistaPanel.add(Box.createVerticalStrut(10));
        JLabel placeholder = new JLabel("Pretražite proizvod da biste videli komentare nutricionista.");
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        placeholder.setForeground(Color.GRAY);
        komentariNutricionistaPanel.add(placeholder);
        komentariNutricionistaPanel.revalidate();
        komentariNutricionistaPanel.repaint();
    }

    // ═══════════════════════════════════════════════════════════
    // POMOCNE METODE - ZAHTEVI ZA SARADNJU
    // ═══════════════════════════════════════════════════════════

    private void dodajZahtevURed(DefaultTableModel model, ZahtevZaSaradnju z) {
        model.addRow(new Object[]{
            z.getIdZahteva(),
            z.getIdKorisnika(),
            z.getIdNutricioniste(),
            z.getTip()     != null ? z.getTip()     : "-",
            z.getOpis()    != null ? z.getOpis()     : "-",
            z.getOdgovor() != null ? z.getOdgovor()  : "-",
            z.getStatus()  != null ? z.getStatus()   : "-"
        });
    }

    private void popuniTabeluZahteva(DefaultTableModel model, String status) {
        model.setRowCount(0);
        try {
            if ("Svi".equals(status)) {
                ArrayList<ZahtevZaSaradnju> lista = dao.vratiSveZahteve();
                for (ZahtevZaSaradnju z : lista) dodajZahtevURed(model, z);
            } else {
                ArrayList<ZahtevZaSaradnju> lista = dao.vratiSveZahtevePoStatusu(status);
                for (ZahtevZaSaradnju z : lista) dodajZahtevURed(model, z);
            }
        } catch (Exception ex) {
            prikaziGresku("Greska pri ucitavanju zahteva: " + ex.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════
    // ADMIN - PANEL ZAHTEVI ZA SARADNJU
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelAdminZahtevi() {
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(BG);

        // ── Header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR); header.setPreferredSize(new Dimension(0, 70)); header.setBorder(new EmptyBorder(0, 25, 0, 25));
        JPanel headerLeft = new JPanel(); headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS)); headerLeft.setOpaque(false);
        JLabel titleLbl = new JLabel("Zahtevi za saradnju"); titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20)); titleLbl.setForeground(Color.WHITE);
        JLabel infoLbl  = new JLabel("Pregled svih zahteva korisnika prema nutricionistima - dupli klik za prikaz detalja"); infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); infoLbl.setForeground(new Color(200, 210, 240));
        headerLeft.add(Box.createVerticalStrut(12)); headerLeft.add(titleLbl); headerLeft.add(Box.createVerticalStrut(3)); headerLeft.add(infoLbl);
        JButton nazadBtn = new JButton("\u2190 Nazad na Admin panel");
        nazadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13)); nazadBtn.setForeground(Color.WHITE);
        nazadBtn.setBorderPainted(false); nazadBtn.setContentAreaFilled(false); nazadBtn.setFocusPainted(false);
        nazadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nazadBtn.addActionListener(e -> prikaziPocetniPanelAdmin());
        header.add(headerLeft, BorderLayout.WEST); header.add(nazadBtn, BorderLayout.EAST);
        main.add(header, BorderLayout.NORTH);

        // ── Toolbar: filter po statusu ──
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(new Color(235, 238, 245));
        toolbar.setBorder(new MatteBorder(0, 0, 1, 0, NUTR_BORDER));
        JLabel filterLbl = new JLabel("Filtriraj po statusu:");
        filterLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); filterLbl.setForeground(HEADER_COLOR);
        String[] statusi = {"Svi", "poslat", "prihvacen", "neprihvacen", "odgovoren", "dostavljen_odgovor"};
        JComboBox<String> statusCombo = new JComboBox<>(statusi);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(220, 32));
        statusCombo.setBackground(Color.WHITE);
        toolbar.add(filterLbl); toolbar.add(statusCombo);
        main.add(toolbar, BorderLayout.NORTH);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(header, BorderLayout.NORTH);
        northPanel.add(toolbar, BorderLayout.SOUTH);
        main.add(northPanel, BorderLayout.NORTH);

        // ── Tabela ──
        String[] kolone = {"ID", "Korisnik ID", "Nutricionista ID", "Tip", "Opis", "Odgovor", "Status"};
        DefaultTableModel zahteviModel = new DefaultTableModel(kolone, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable zahteviTable = new JTable(zahteviModel); styleTable(zahteviTable);
        zahteviTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        zahteviTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        zahteviTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        zahteviTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        zahteviTable.getColumnModel().getColumn(4).setPreferredWidth(280);
        zahteviTable.getColumnModel().getColumn(5).setPreferredWidth(280);
        zahteviTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        zahteviTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        zahteviTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Dupli klik — otvara detalje zahteva
        zahteviTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = zahteviTable.getSelectedRow();
                    if (row == -1) return;
                    int    id          = (int)    zahteviModel.getValueAt(row, 0);
                    int    korisnikId  = (int)    zahteviModel.getValueAt(row, 1);
                    int    nutriId     = (int)    zahteviModel.getValueAt(row, 2);
                    String tip         = (String) zahteviModel.getValueAt(row, 3);
                    String opis        = (String) zahteviModel.getValueAt(row, 4);
                    String odgovor     = (String) zahteviModel.getValueAt(row, 5);
                    String status      = (String) zahteviModel.getValueAt(row, 6);

                    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(main), "Detalji zahteva #" + id, true);
                    dialog.setSize(600, 480);
                    dialog.setLocationRelativeTo(main);
                    dialog.setLayout(new BorderLayout());
                    dialog.getContentPane().setBackground(CARD_BG);

                    // Naslov
                    JPanel dlgHeader = new JPanel(new BorderLayout());
                    dlgHeader.setBackground(HEADER_COLOR);
                    dlgHeader.setBorder(new EmptyBorder(16, 24, 16, 24));
                    JLabel dlgTitle = new JLabel("Zahtev #" + id);
                    dlgTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
                    dlgTitle.setForeground(Color.WHITE);
                    dlgHeader.add(dlgTitle, BorderLayout.WEST);
                    dialog.add(dlgHeader, BorderLayout.NORTH);

                    JPanel fields = new JPanel(new GridBagLayout());
                    fields.setBackground(CARD_BG);
                    fields.setBorder(new EmptyBorder(20, 24, 10, 24));
                    int fRow = 0;

                    String[][] podaci = {
                        {"ID zahteva",       String.valueOf(id)},
                        {"Korisnik ID",      String.valueOf(korisnikId)},
                        {"Nutricionista ID", String.valueOf(nutriId)},
                        {"Tip",              tip},
                        {"Status",           status}
                    };

                    for (String[] par : podaci) {
                        JPanel row2 = new JPanel(new BorderLayout(10, 0));
                        row2.setBackground(CARD_BG);
                        row2.setBorder(new EmptyBorder(4, 0, 4, 0));
                        JLabel lbl = new JLabel(par[0] + ":");
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        lbl.setForeground(HEADER_COLOR);
                        lbl.setPreferredSize(new Dimension(160, 28));
                        JLabel val2 = new JLabel(par[1]);
                        val2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        val2.setForeground(TEXT_PRIMARY);
                        row2.add(lbl, BorderLayout.WEST);
                        row2.add(val2, BorderLayout.CENTER);
                        GridBagConstraints fgbcRow = new GridBagConstraints();
                        fgbcRow.gridx = 0; fgbcRow.gridy = fRow++; fgbcRow.fill = GridBagConstraints.HORIZONTAL; fgbcRow.weightx = 1.0; fgbcRow.anchor = GridBagConstraints.WEST; fgbcRow.insets = new Insets(0, 0, 0, 0);
                        fields.add(row2, fgbcRow);
                    }

                    GridBagConstraints fgbcSep = new GridBagConstraints();
                    fgbcSep.gridx = 0; fgbcSep.gridy = fRow++; fgbcSep.fill = GridBagConstraints.HORIZONTAL; fgbcSep.weightx = 1.0; fgbcSep.anchor = GridBagConstraints.WEST; fgbcSep.insets = new Insets(8, 0, 4, 0);
                    JSeparator sep = new JSeparator(); sep.setForeground(NUTR_BORDER);
                    fields.add(sep, fgbcSep);

                    // Opis - label
                    JLabel opisLbl = new JLabel("Opis:");
                    opisLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    opisLbl.setForeground(HEADER_COLOR);
                    GridBagConstraints fgbcOpisLbl = new GridBagConstraints();
                    fgbcOpisLbl.gridx = 0; fgbcOpisLbl.gridy = fRow++; fgbcOpisLbl.fill = GridBagConstraints.HORIZONTAL; fgbcOpisLbl.weightx = 1.0; fgbcOpisLbl.anchor = GridBagConstraints.WEST; fgbcOpisLbl.insets = new Insets(4, 0, 4, 0);
                    fields.add(opisLbl, fgbcOpisLbl);

                    // Opis - textarea puna sirina
                    JTextArea opisArea = new JTextArea(opis != null ? opis : "-");
                    opisArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    opisArea.setLineWrap(true); opisArea.setWrapStyleWord(true);
                    opisArea.setEditable(false); opisArea.setBackground(new Color(245, 247, 252));
                    opisArea.setBorder(new EmptyBorder(8, 10, 8, 10));
                    JScrollPane opisScroll = new JScrollPane(opisArea);
                    opisScroll.setPreferredSize(new Dimension(0, 90));
                    opisScroll.setBorder(new LineBorder(NUTR_BORDER, 1, true));
                    GridBagConstraints fgbcOpisScroll = new GridBagConstraints();
                    fgbcOpisScroll.gridx = 0; fgbcOpisScroll.gridy = fRow++; fgbcOpisScroll.fill = GridBagConstraints.BOTH; fgbcOpisScroll.weightx = 1.0; fgbcOpisScroll.weighty = 0.4; fgbcOpisScroll.anchor = GridBagConstraints.WEST; fgbcOpisScroll.insets = new Insets(0, 0, 0, 0);
                    fields.add(opisScroll, fgbcOpisScroll);

                    // Odgovor - label
                    JLabel odgovorLbl = new JLabel("Odgovor:");
                    odgovorLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    odgovorLbl.setForeground(HEADER_COLOR);
                    GridBagConstraints fgbcOdgLbl = new GridBagConstraints();
                    fgbcOdgLbl.gridx = 0; fgbcOdgLbl.gridy = fRow++; fgbcOdgLbl.fill = GridBagConstraints.HORIZONTAL; fgbcOdgLbl.weightx = 1.0; fgbcOdgLbl.anchor = GridBagConstraints.WEST; fgbcOdgLbl.insets = new Insets(10, 0, 4, 0);
                    fields.add(odgovorLbl, fgbcOdgLbl);

                    // Odgovor - textarea puna sirina
                    JTextArea odgovorArea2 = new JTextArea(odgovor != null && !odgovor.equals("-") ? odgovor : "Nema odgovora.");
                    odgovorArea2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    odgovorArea2.setLineWrap(true); odgovorArea2.setWrapStyleWord(true);
                    odgovorArea2.setEditable(false); odgovorArea2.setBackground(new Color(245, 247, 252));
                    odgovorArea2.setBorder(new EmptyBorder(8, 10, 8, 10));
                    JScrollPane odgovorScroll = new JScrollPane(odgovorArea2);
                    odgovorScroll.setPreferredSize(new Dimension(0, 110));
                    odgovorScroll.setBorder(new LineBorder(NUTR_BORDER, 1, true));
                    GridBagConstraints fgbcOdgScroll = new GridBagConstraints();
                    fgbcOdgScroll.gridx = 0; fgbcOdgScroll.gridy = fRow++; fgbcOdgScroll.fill = GridBagConstraints.BOTH; fgbcOdgScroll.weightx = 1.0; fgbcOdgScroll.weighty = 0.6; fgbcOdgScroll.anchor = GridBagConstraints.WEST; fgbcOdgScroll.insets = new Insets(0, 0, 10, 0);
                    fields.add(odgovorScroll, fgbcOdgScroll);

                    JScrollPane mainScroll = new JScrollPane(fields);
                    mainScroll.setBorder(null);
                    mainScroll.getViewport().setBackground(CARD_BG);
                    dialog.add(mainScroll, BorderLayout.CENTER);

                    // Dugme zatvori
                    JPanel dlgFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
                    dlgFooter.setBackground(CARD_BG);
                    dlgFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
                    JButton zatvoriBtn2 = styleBtn("Zatvori", new Color(200, 60, 60));
                    zatvoriBtn2.addActionListener(ae -> dialog.dispose());
                    dlgFooter.add(zatvoriBtn2);
                    dialog.add(dlgFooter, BorderLayout.SOUTH);

                    dialog.setVisible(true);
                }
            }
        });

        // Poseban renderer za status — bojenje ćelija
        zahteviTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    String s = val != null ? val.toString() : "";
                    switch (s) {
                        case "poslat":              setBackground(new Color(220, 235, 255)); setForeground(new Color(30, 60, 140)); break;
                        case "prihvacen":           setBackground(new Color(210, 245, 220)); setForeground(new Color(20, 110, 50)); break;
                        case "neprihvacen":         setBackground(new Color(255, 220, 220)); setForeground(new Color(160, 30, 30)); break;
                        case "odgovoren":           setBackground(new Color(255, 245, 200)); setForeground(new Color(140, 100, 10)); break;
                        case "dostavljen_odgovor":  setBackground(new Color(220, 245, 235)); setForeground(new Color(10, 100, 70)); break;
                        default:                    setBackground(row % 2 == 0 ? TABLE_ROW1 : TABLE_ROW2); setForeground(TEXT_PRIMARY);
                    }
                }
                return this;
            }
        });

        // Punjenje tabele pri otvaranju — ucitaj sve zahteve
        popuniTabeluZahteva(zahteviModel, "Svi");
        statusCombo.addActionListener(e -> {
            String odabrani = (String) statusCombo.getSelectedItem();
            popuniTabeluZahteva(zahteviModel, odabrani);
        });

        JScrollPane scroll = new JScrollPane(zahteviTable);
        scroll.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(15, 15, 5, 15), new LineBorder(NUTR_BORDER, 1, true)));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        main.add(scroll, BorderLayout.CENTER);

        // ── Footer ──
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG); footer.setBorder(new EmptyBorder(8, 15, 12, 15));
        JLabel countLbl = new JLabel(""); countLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); countLbl.setForeground(Color.GRAY);
        // Ažuriraj count svaki put kad se model menja
        zahteviModel.addTableModelListener(e2 -> countLbl.setText("Ukupno prikazano: " + zahteviModel.getRowCount() + " zahteva"));
        countLbl.setText("Ukupno prikazano: " + zahteviModel.getRowCount() + " zahteva");
        JButton osveziBtn = styleBtn("Osveži", new Color(70, 100, 180));
        osveziBtn.addActionListener(e -> {
            String odabrani = (String) statusCombo.getSelectedItem();
            popuniTabeluZahteva(zahteviModel, odabrani);
        });
        footer.add(countLbl, BorderLayout.WEST); footer.add(osveziBtn, BorderLayout.EAST);
        main.add(footer, BorderLayout.SOUTH);

        return main;
    }

    // ═══════════════════════════════════════════════════════════
    // NUTRICIONISTA PANEL
    // ═══════════════════════════════════════════════════════════
    private JPanel buildPanelNutricionista() {
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(BG);
        JPanel npHeader = new JPanel(new BorderLayout()); npHeader.setBackground(HEADER_COLOR); npHeader.setPreferredSize(new Dimension(0, 60)); npHeader.setBorder(new EmptyBorder(0, 25, 0, 25));
        JLabel npTitleLbl = new JLabel("Nutricionista Panel"); npTitleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22)); npTitleLbl.setForeground(Color.WHITE);
        nutricionistaImeLabel = new JLabel("", SwingConstants.CENTER);
        nutricionistaImeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nutricionistaImeLabel.setForeground(new Color(200, 220, 255));
        JButton npOdjaviBtn = new JButton("Odjavi se"); npOdjaviBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); npOdjaviBtn.setForeground(Color.WHITE); npOdjaviBtn.setBackground(new Color(220, 53, 69));
        npOdjaviBtn.setFocusPainted(false); npOdjaviBtn.setBorderPainted(false); npOdjaviBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); npOdjaviBtn.setBorder(new EmptyBorder(10, 20, 10, 20)); 
        npOdjaviBtn.addActionListener(e ->{
        	nutricionista=null;
        	prikaziPocetniPanel();
        });
        npHeader.add(npTitleLbl, BorderLayout.WEST); npHeader.add(nutricionistaImeLabel, BorderLayout.CENTER); npHeader.add(npOdjaviBtn, BorderLayout.EAST);
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 30, 0)); cardsPanel.setBackground(BG); cardsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        JPanel mc1 = new JPanel(new BorderLayout(0, 12)); mc1.setBackground(CARD_BG); mc1.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(30, 30, 30, 30)));
        JPanel mc1Top = new JPanel(); mc1Top.setLayout(new BoxLayout(mc1Top, BoxLayout.Y_AXIS)); mc1Top.setBackground(CARD_BG);
        JPanel mc1Bar = new JPanel(); mc1Bar.setBackground(new Color(70, 130, 180)); mc1Bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        JLabel mc1Title = new JLabel("Pregled poruka korisnika"); mc1Title.setFont(new Font("Segoe UI", Font.BOLD, 18)); mc1Title.setForeground(HEADER_COLOR); mc1Title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel mc1Sub = new JLabel("Pregledajte sve poruke koje su poslali korisnici"); mc1Sub.setFont(new Font("Segoe UI", Font.PLAIN, 13)); mc1Sub.setForeground(Color.GRAY); mc1Sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        mc1Top.add(mc1Bar); mc1Top.add(Box.createVerticalStrut(20)); mc1Top.add(mc1Title); mc1Top.add(Box.createVerticalStrut(8)); mc1Top.add(mc1Sub);
        JButton mc1Btn = new JButton("Otvori \u2192"); mc1Btn.setFont(new Font("Segoe UI", Font.BOLD, 13)); mc1Btn.setBackground(new Color(70, 130, 180)); mc1Btn.setForeground(Color.WHITE);
        mc1Btn.setFocusPainted(false); mc1Btn.setBorderPainted(false); mc1Btn.setBorder(new EmptyBorder(10, 22, 10, 22)); mc1Btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); mc1Btn.addActionListener(e -> prikaziPanelPrikaziZahteve());
        JPanel mc1BtnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); mc1BtnWrap.setBackground(CARD_BG); mc1BtnWrap.add(mc1Btn);
        mc1.add(mc1Top, BorderLayout.CENTER); mc1.add(mc1BtnWrap, BorderLayout.SOUTH);
        JPanel mc2 = new JPanel(new BorderLayout(0, 12)); mc2.setBackground(CARD_BG); mc2.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(30, 30, 30, 30)));
        JPanel mc2Top = new JPanel(); mc2Top.setLayout(new BoxLayout(mc2Top, BoxLayout.Y_AXIS)); mc2Top.setBackground(CARD_BG);
        JPanel mc2Bar = new JPanel(); mc2Bar.setBackground(new Color(60, 160, 100)); mc2Bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        JLabel mc2Title = new JLabel("Pregled proizvoda"); mc2Title.setFont(new Font("Segoe UI", Font.BOLD, 18)); mc2Title.setForeground(HEADER_COLOR); mc2Title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel mc2Sub = new JLabel("Pregledajte sve proizvode u bazi podataka"); mc2Sub.setFont(new Font("Segoe UI", Font.PLAIN, 13)); mc2Sub.setForeground(Color.GRAY); mc2Sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        mc2Top.add(mc2Bar); mc2Top.add(Box.createVerticalStrut(20)); mc2Top.add(mc2Title); mc2Top.add(Box.createVerticalStrut(8)); mc2Top.add(mc2Sub);
        JButton mc2Btn = new JButton("Otvori \u2192"); mc2Btn.setFont(new Font("Segoe UI", Font.BOLD, 13)); mc2Btn.setBackground(new Color(60, 160, 100)); mc2Btn.setForeground(Color.WHITE);
        mc2Btn.setFocusPainted(false); mc2Btn.setBorderPainted(false); mc2Btn.setBorder(new EmptyBorder(10, 22, 10, 22)); mc2Btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); mc2Btn.addActionListener(e -> prikaziPanelProizvodi());
        JPanel mc2BtnWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); mc2BtnWrap.setBackground(CARD_BG); mc2BtnWrap.add(mc2Btn);
        mc2.add(mc2Top, BorderLayout.CENTER); mc2.add(mc2BtnWrap, BorderLayout.SOUTH);
        cardsPanel.add(mc1); cardsPanel.add(mc2);
        main.add(npHeader, BorderLayout.NORTH); main.add(cardsPanel, BorderLayout.CENTER);
        return main;
    }

    public void prikaziPanelNutricionisti() {
        try {
            ArrayList<Nutricionista> lista = dao.vratiListuNutricionista();
            if (lista.isEmpty()) { JOptionPane.showMessageDialog(this, "Nema dostupnih nutricionista.", "Informacija", JOptionPane.INFORMATION_MESSAGE); return; }
            JDialog dialog = new JDialog(this, "Pregled nutricionista", true);
            dialog.setSize(700, 420); dialog.setLocationRelativeTo(this); dialog.getContentPane().setBackground(BG); dialog.setLayout(new BorderLayout(10, 10));
            JLabel naslov = new JLabel("Dostupni nutricionisti"); naslov.setFont(new Font("Segoe UI", Font.BOLD, 18)); naslov.setForeground(HEADER_COLOR); naslov.setBorder(new EmptyBorder(15, 15, 5, 15));
            dialog.add(naslov, BorderLayout.NORTH);
            String[] kolone = {"Ime", "Prezime", "Email", "Licenca"};
            Object[][] podaci = new Object[lista.size()][4];
            for (int i = 0; i < lista.size(); i++) { Nutricionista n = lista.get(i); podaci[i][0]=n.getIme(); podaci[i][1]=n.getPrezime(); podaci[i][2]=n.getEmail(); podaci[i][3]=n.getLicenca(); }
            DefaultTableModel model = new DefaultTableModel(podaci, kolone) { public boolean isCellEditable(int r, int c) { return false; } };
            JTable tabela = new JTable(model); styleTable(tabela);
            JScrollPane scrollTabela = new JScrollPane(tabela); scrollTabela.setBorder(new LineBorder(NUTR_BORDER, 1, true));
            JPanel tabelaWrapper = new JPanel(new BorderLayout()); tabelaWrapper.setBackground(BG); tabelaWrapper.setBorder(new EmptyBorder(0, 15, 0, 15)); tabelaWrapper.add(scrollTabela, BorderLayout.CENTER);
            dialog.add(tabelaWrapper, BorderLayout.CENTER);
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); bottom.setBackground(BG);
            JButton posaljiBtn = styleBtn("Pošalji poruku", new Color(30, 80, 140)); JButton zatvoriBtn = styleBtn("Zatvori", new Color(130, 130, 145));
            posaljiBtn.addActionListener(e -> { int row = tabela.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(dialog, "Odaberite nutricionistu!", "Upozorenje", JOptionPane.WARNING_MESSAGE); return; } prikaziPanelSlanjePoruke(dialog, lista.get(row)); });
            zatvoriBtn.addActionListener(e -> dialog.dispose());
            bottom.add(posaljiBtn); bottom.add(zatvoriBtn);
            dialog.add(bottom, BorderLayout.SOUTH); dialog.setVisible(true);
        } catch (Exception ex) { prikaziGresku("Greška: " + ex.getMessage()); }
    }

    public void prikaziPanelSlanjePoruke(JDialog parent, Nutricionista nutri) {
        JDialog forma = new JDialog(parent, "Pošalji poruku", true);
        forma.setSize(450, 390); forma.setLocationRelativeTo(parent); forma.getContentPane().setBackground(BG); forma.setLayout(new BorderLayout(10, 10));
        JLabel naslov = new JLabel("Poruka za: " + nutri.getIme() + " " + nutri.getPrezime()); naslov.setFont(new Font("Segoe UI", Font.BOLD, 16)); naslov.setForeground(HEADER_COLOR); naslov.setBorder(new EmptyBorder(15, 15, 5, 15));
        forma.add(naslov, BorderLayout.NORTH);
        JPanel center = new JPanel(); center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS)); center.setBackground(BG); center.setBorder(new EmptyBorder(5, 15, 5, 15));
        JLabel emailInfo = new JLabel("Email: " + nutri.getEmail()); emailInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12)); emailInfo.setForeground(Color.GRAY); emailInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        // ── Tip poruke: radio buttons ──
        JLabel tipLbl = new JLabel("Tip zahteva:"); tipLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); tipLbl.setForeground(HEADER_COLOR); tipLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JRadioButton rbKonsultacija = new JRadioButton("Konsultacija"); rbKonsultacija.setFont(new Font("Segoe UI", Font.PLAIN, 13)); rbKonsultacija.setBackground(BG); rbKonsultacija.setSelected(true);
        JRadioButton rbPlanIshrane  = new JRadioButton("Plan ishrane");  rbPlanIshrane.setFont(new Font("Segoe UI", Font.PLAIN, 13));  rbPlanIshrane.setBackground(BG);
        ButtonGroup tipGrupa = new ButtonGroup(); tipGrupa.add(rbKonsultacija); tipGrupa.add(rbPlanIshrane);
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); radioPanel.setBackground(BG); radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT); radioPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        radioPanel.add(rbKonsultacija); radioPanel.add(rbPlanIshrane);
        // ── Tekstualno polje za poruku ──
        JTextArea porukaArea = new JTextArea(); porukaArea.setFont(new Font("Segoe UI", Font.PLAIN, 13)); porukaArea.setLineWrap(true); porukaArea.setWrapStyleWord(true); porukaArea.setBackground(CARD_BG); porukaArea.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(8, 10, 8, 10)));
        JScrollPane scrollArea = new JScrollPane(porukaArea); scrollArea.setBorder(null); scrollArea.setPreferredSize(new Dimension(0, 120)); scrollArea.setAlignmentX(Component.LEFT_ALIGNMENT); scrollArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        center.add(emailInfo); center.add(Box.createVerticalStrut(10)); center.add(tipLbl); center.add(Box.createVerticalStrut(4)); center.add(radioPanel); center.add(Box.createVerticalStrut(10)); center.add(scrollArea);
        forma.add(center, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); btnPanel.setBackground(BG);
        JButton posaljiBtn = styleBtn("Pošalji", new Color(25, 85, 55)); JButton odustaniBtn = styleBtn("Odustani", new Color(130, 130, 145));
        posaljiBtn.addActionListener(e -> {
            String tekst = porukaArea.getText().trim();
            String tip = rbKonsultacija.isSelected() ? "Konsultacija" : "Plan ishrane";
            if (!ZahtevKontroler.validirajZahtev(tip, tekst)) { prikaziGresku("Unesite poruku."); return; }
            try {
            	dao.posaljiZahtevNutricionisti(korisnik.getIdKorisnika(), nutri.getIdNutricioniste(), tip, tekst);
            	uspesnoPoslatZahtev(nutri, tip, tekst);
                forma.dispose();
			} catch (Exception e2) {
				prikaziGresku("Greška: "+e2.getMessage());
			}
        });
        odustaniBtn.addActionListener(e -> forma.dispose());
        btnPanel.add(posaljiBtn); btnPanel.add(odustaniBtn);
        forma.add(btnPanel, BorderLayout.SOUTH); forma.setVisible(true);
    }

    public void uspesnoPoslatZahtev(Nutricionista n, String tip, String poruka) { JOptionPane.showMessageDialog(this, "Poruka (" + tip + ") uspešno poslata " + n.getIme() + " " + n.getPrezime() + "!", "Uspeh", JOptionPane.INFORMATION_MESSAGE); }

    // ═══════════════════════════════════════════════════════════
    // PREGLED SKENIRANIH PROIZVODA (korisnik)
    // ═══════════════════════════════════════════════════════════
    public void prikaziPanelSkenirani() {
        JDialog dialog = new JDialog(this, "Pregled skeniranih proizvoda", true);
        dialog.setSize(780, 500); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout()); dialog.getContentPane().setBackground(BG);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14)); header.setBackground(HEADER_COLOR);
        JLabel lbl = new JLabel("Vaši skenirani proizvodi"); lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); lbl.setForeground(Color.WHITE); header.add(lbl);
        dialog.add(header, BorderLayout.NORTH);

        String[] kolone = {"Barkod", "Naziv", "Energija (kcal)", "Masti (g)", "Proteini (g)", "Ugljeni hid. (g)", "So (g)"};
        DefaultTableModel model = new DefaultTableModel(kolone, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        try {
            ArrayList<Proizvod> lista = dao.vratiListuSkeniranih(korisnik.getIdKorisnika());

            if (lista != null) {
                for (Proizvod p : lista) {
                    model.addRow(new Object[]{
                        p.getBarKod(), p.getNaziv(), p.getEnergijaKcal(),
                        p.getMasti(), p.getProteini(), p.getUgljeniHidrati(), p.getSo()
                    });
                }
            }
        } catch (Exception ex) { prikaziGresku("Greška pri učitavanju skeniranih: " + ex.getMessage()); }

        JTable table = new JTable(model); styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(15, 15, 5, 15), new LineBorder(NUTR_BORDER, 1, true)));
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout()); bottom.setBackground(BG); bottom.setBorder(new EmptyBorder(0, 15, 12, 15));
        JLabel countLbl = new JLabel("Ukupno skeniranih: " + model.getRowCount()); countLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); countLbl.setForeground(Color.GRAY);
        JButton zatvoriBtn = styleBtn("Zatvori", new Color(200, 60, 60)); zatvoriBtn.addActionListener(e -> dialog.dispose());
        bottom.add(countLbl, BorderLayout.WEST); bottom.add(zatvoriBtn, BorderLayout.EAST);
        dialog.add(bottom, BorderLayout.SOUTH); dialog.setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════
    // ═══════════════════════════════════════════════════════════
    // MOJI RACUNI — korisnik placa racune za plan ishrane
    // ═══════════════════════════════════════════════════════════
    public void prikaziPanelMojiRacuni() {
        JDialog dialog = new JDialog(this, "Moji racuni", true);
        dialog.setSize(780, 450); dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout()); dialog.getContentPane().setBackground(BG);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(HEADER_COLOR);
        JLabel hLbl = new JLabel("Vasi racuni za planove ishrane");
        hLbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); hLbl.setForeground(Color.WHITE);
        header.add(hLbl);
        dialog.add(header, BorderLayout.NORTH);

        // ID kolona je sakrivena (index 0)
        String[] kolone = {"ID", "Nutricionista", "Iznos (RSD)", "Status", "Datum"};
        DefaultTableModel model = new DefaultTableModel(kolone, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        try {
            ArrayList<RacunDTO> racuni = dao.vratiRacuneZaKorisnika(korisnik.getIdKorisnika());
            for (RacunDTO r : racuni) {
                model.addRow(new Object[]{
                    r.getIdRacuna(),
                    r.getImeNutricioniste(),
                    r.getIznos() + " RSD",
                    "placen".equals(r.getStatus()) ? "Placen" : "Neplacen",
                    r.getDatum()
                });
            }
        } catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }

        JTable table = new JTable(model); styleTable(table);
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 15, 5, 15), new LineBorder(NUTR_BORDER, 1, true)));
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BG); bottom.setBorder(new EmptyBorder(8, 15, 12, 15));

        JLabel infoLbl = new JLabel("Ukupno: " + model.getRowCount() + " racuna");
        infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); infoLbl.setForeground(Color.GRAY);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton platiBtn   = styleBtn("Plati izabrani", new Color(40, 160, 80));
        JButton zatvoriBtn = styleBtn("Zatvori", new Color(200, 60, 60));

        platiBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(dialog, "Odaberite racun.", "Upozorenje", JOptionPane.WARNING_MESSAGE); return; }
            if ("Placen".equals(model.getValueAt(row, 3))) {
                JOptionPane.showMessageDialog(dialog, "Ovaj racun je vec placen.", "Informacija", JOptionPane.INFORMATION_MESSAGE); return;
            }
            String iznos = (String) model.getValueAt(row, 2);
            int potvrda = JOptionPane.showConfirmDialog(dialog, "Platiti racun od " + iznos + "?", "Potvrda placanja", JOptionPane.YES_NO_OPTION);
            if (potvrda != JOptionPane.YES_OPTION) return;
            int idRacuna = (int) model.getValueAt(row, 0);
            try {
                dao.platiRacun(idRacuna);
                JOptionPane.showMessageDialog(dialog, "Racun je uspesno placen!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                model.setValueAt("Placen", row, 3);
            } catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }
        });

        zatvoriBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(platiBtn); btnPanel.add(zatvoriBtn);
        bottom.add(infoLbl, BorderLayout.WEST); bottom.add(btnPanel, BorderLayout.EAST);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // INBOX — poruke korisnika sa nutricionistima (chat prikaz)
    // ═══════════════════════════════════════════════════════════
    public void prikaziPanelInbox() {
        JDialog dialog = new JDialog(this, "Inbox — poruke sa nutricionistima", true);
        dialog.setSize(800, 580); dialog.setLocationRelativeTo(this); 
        dialog.setLayout(new BorderLayout()); dialog.getContentPane().setBackground(BG);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14)); 
        header.setBackground(HEADER_COLOR);
        JLabel lbl = new JLabel("Vaše poruke sa nutricionistima"); 
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); 
        lbl.setForeground(Color.WHITE); header.add(lbl);
        dialog.add(header, BorderLayout.NORTH);

        // Leva strana: lista konverzacija (nutricionisti sa kojima je korisnik komunicirao)
        DefaultListModel<String> listModel = new DefaultListModel<>();
        ArrayList<Nutricionista> sagovorniciList = new ArrayList<>();
        try {
            ArrayList<Nutricionista> sagovornici = dao.vratiSagovornikeKorisnika(korisnik.getIdKorisnika());
            if (sagovornici != null) {
                for (Nutricionista n : sagovornici) {
                    listModel.addElement(n.getIme() + " " + n.getPrezime());
                    sagovorniciList.add(n);
                }
            }
        } catch (Exception ex) { listModel.addElement("Greška pri učitavanju"); }

        JList<String> konvList = new JList<>(listModel);
        konvList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        konvList.setBackground(CARD_BG);
        konvList.setSelectionBackground(new Color(210, 225, 245));
        konvList.setFixedCellHeight(38);
        konvList.setBorder(new EmptyBorder(5, 10, 5, 10));
        JScrollPane listScroll = new JScrollPane(konvList);
        listScroll.setPreferredSize(new Dimension(200, 0));
        listScroll.setBorder(new MatteBorder(0, 0, 0, 1, NUTR_BORDER));

        // Desna strana: chat poruke za izabranog nutricionistu
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(245, 246, 250));
        JScrollPane chatScroll = new JScrollPane(chatPanel);
        chatScroll.setBorder(null);
        chatScroll.getVerticalScrollBar().setUnitIncrement(12);

        JLabel chatPlaceholder = new JLabel("  Odaberite nutricionistu sa liste da vidite poruke.");
        chatPlaceholder.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        chatPlaceholder.setForeground(Color.GRAY);
        chatPlaceholder.setAlignmentX(Component.LEFT_ALIGNMENT);
        chatPanel.add(Box.createVerticalStrut(15));
        chatPanel.add(chatPlaceholder);

        konvList.addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int idx = konvList.getSelectedIndex();
            if (idx < 0 || idx >= sagovorniciList.size()) return;
            Nutricionista odabrani = sagovorniciList.get(idx);
            chatPanel.removeAll();
            chatPanel.add(Box.createVerticalStrut(10));
            try {
                ArrayList<ZahtevZaSaradnju> zahtevi = dao.vratiZahteve(korisnik.getIdKorisnika(), odabrani.getIdNutricioniste());
                if (zahtevi == null || zahtevi.isEmpty()) {
                    JLabel prazan = new JLabel("  Nema poruka u ovoj konverzaciji.");
                    prazan.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    prazan.setForeground(Color.GRAY);
                    chatPanel.add(prazan);
                } else {
                    for (ZahtevZaSaradnju z : zahtevi) {
                        String tipTekst = (z.getTip() != null && !z.getTip().isEmpty()) ? "  [" + z.getTip() + "]" : "";

                        chatPanel.add(napraviChatBalon(
                            "Vi" + tipTekst,
                            z.getOpis() != null ? z.getOpis() : "",
                            true
                        ));

                        // Balon nutricioniste — konsultacije se prikazuju odmah (besplatne),
                        // plan ishrane samo nakon plaćanja (status dostavljen_odgovor)
                        if (z.getOdgovor() != null && !z.getOdgovor().trim().isEmpty()) {
                            boolean jeKonsultacija = "Konsultacija".equals(z.getTip());
                            boolean placeno = "dostavljen_odgovor".equals(z.getStatus());

                            if (jeKonsultacija || placeno) {
                                chatPanel.add(napraviChatBalon(
                                    odabrani.getIme() + " " + odabrani.getPrezime(),
                                    z.getOdgovor(),
                                    false
                                ));
                            } else if ("odgovoren".equals(z.getStatus())) {
                                // Plan ishrane — odgovor postoji ali čeka plaćanje
                                JPanel obavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
                                obavPanel.setBackground(new Color(255, 248, 220));
                                obavPanel.setBorder(BorderFactory.createCompoundBorder(
                                    new LineBorder(new Color(220, 180, 60), 1, true),
                                    new EmptyBorder(4, 8, 4, 8)));
                                obavPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                                obavPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                                JLabel obavLbl = new JLabel("Platite račun u sekciji 'Moji računi' da biste videli odgovor na traženi plan ishrane.");
                                obavLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                                obavLbl.setForeground(new Color(140, 100, 10));
                                obavPanel.add(obavLbl);
                                chatPanel.add(obavPanel);
                            }
                        }
                        chatPanel.add(Box.createVerticalStrut(6));
                    }
                }
            } catch (Exception ex) {
                JLabel errLbl = new JLabel("  Greška: " + ex.getMessage());
                errLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                errLbl.setForeground(ERROR_RED);
                chatPanel.add(errLbl);
            }
            chatPanel.add(Box.createVerticalStrut(10));
            chatPanel.revalidate(); chatPanel.repaint();
            SwingUtilities.invokeLater(() -> {
                JScrollBar sb = chatScroll.getVerticalScrollBar(); sb.setValue(sb.getMaximum());
            });
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, chatScroll);
        split.setDividerLocation(200); split.setDividerSize(1); split.setBorder(null);
        dialog.add(split, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); bottom.setBackground(BG);
        JButton zatvoriBtn = styleBtn("Zatvori", new Color(200, 60, 60)); zatvoriBtn.addActionListener(e -> dialog.dispose());
        bottom.add(zatvoriBtn);
        dialog.add(bottom, BorderLayout.SOUTH); dialog.setVisible(true);
    }

    /**
     * Kreira jedan chat balon — korisnikov (plavi, desno) ili nutricionistin (zeleni, levo).
     */
    private JPanel napraviChatBalon(String naziv, String tekst, boolean jeKorisnik) {
        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBackground(jeKorisnik ? new Color(220, 235, 255) : new Color(230, 250, 230));
        bubble.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(jeKorisnik ? new Color(180, 210, 240) : new Color(170, 220, 180), 1, true),
            new EmptyBorder(8, 12, 8, 12)));
        bubble.setMaximumSize(new Dimension(560, Integer.MAX_VALUE));
        bubble.setAlignmentX(jeKorisnik ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

        JLabel koLbl = new JLabel(naziv);
        koLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        koLbl.setForeground(jeKorisnik ? new Color(30, 70, 140) : new Color(30, 110, 60));
        koLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea taTekst = new JTextArea(tekst);
        taTekst.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        taTekst.setEditable(false); taTekst.setLineWrap(true); taTekst.setWrapStyleWord(true);
        taTekst.setBackground(bubble.getBackground()); taTekst.setBorder(null);
        taTekst.setAlignmentX(Component.LEFT_ALIGNMENT);

        bubble.add(koLbl);
        bubble.add(Box.createVerticalStrut(3));
        bubble.add(taTekst);

        JPanel row = new JPanel(new FlowLayout(jeKorisnik ? FlowLayout.RIGHT : FlowLayout.LEFT, 15, 4));
        row.setBackground(new Color(245, 246, 250));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        row.add(bubble);
        return row;
    }

    public void prikaziPanelProizvodi() {
        JDialog dialog = new JDialog(this, "Pregled svih proizvoda", true);
        dialog.setSize(1500, 700); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout()); dialog.getContentPane().setBackground(BG);
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14)); header.setBackground(HEADER_COLOR);
        JLabel lbl = new JLabel("Svi proizvodi  \u2014  dupli klik na red za detalje"); lbl.setFont(new Font("Segoe UI", Font.BOLD, 15)); lbl.setForeground(Color.WHITE); header.add(lbl);
        dialog.add(header, BorderLayout.NORTH);
        // Kolone 0-12 su vidljive; kolone 13 (preporucena doza) i 14 (sastojci) su skrivene
        String[] kolone = {"Barkod","Naziv","Posno","Halal","Energija (kJ)","Energija (kcal)","Masti (g)","Zas. masti (g)","Ugljeni hid. (g)","Šećeri (g)","Vlakna (g)","Proteini (g)","So (g)","_doza","_sastojci"};
        DefaultTableModel model = new DefaultTableModel(kolone, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        try { for (Proizvod p : dao.vratiListuProizvoda()) { model.addRow(new Object[]{p.getBarKod(), p.getNaziv(), p.isPosan()?"Da":"Ne", p.isHalal()?"Da":"Ne", p.getEnergijaKJ(), p.getEnergijaKcal(), p.getMasti(), p.getZasiceneMasti(), p.getUgljeniHidrati(), p.getSeceri(), p.getVlakna(), p.getProteini(), p.getSo(), p.getPreporucenaDnevnaDoza(), p.getSastojci()}); } } catch (Exception ex) { prikaziGresku("Greška: " + ex.getMessage()); }
        JTable table = new JTable(model); styleTable(table);
        table.getColumnModel().getColumn(13).setMinWidth(0); table.getColumnModel().getColumn(13).setMaxWidth(0); table.getColumnModel().getColumn(13).setWidth(0);
        table.getColumnModel().getColumn(14).setMinWidth(0); table.getColumnModel().getColumn(14).setMaxWidth(0); table.getColumnModel().getColumn(14).setWidth(0);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if (r == -1) return;
                if (e.getClickCount() == 2) {
                	try {
                		prikaziDetaljeProizvoda(dao.nadjiProizvod((String) model.getValueAt(r, 0)));
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(dialog, e2.getMessage(), "Upozorenje", JOptionPane.WARNING_MESSAGE);
					}
                    
                }
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(15, 15, 15, 15), new LineBorder(NUTR_BORDER, 1, true)));
        dialog.add(scroll, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout()); bottom.setBackground(BG); bottom.setBorder(new EmptyBorder(0, 15, 12, 15));
        JLabel countLbl = new JLabel("Ukupno: " + model.getRowCount()); countLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); countLbl.setForeground(Color.GRAY);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); btnPanel.setOpaque(false);
        JButton oceniTabelaBtn = styleBtn("Oceni izabrani", ACCENT);
        JButton zatvoriBtn = styleBtn("Zatvori", new Color(200, 60, 60));
        oceniTabelaBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(dialog, "Odaberite proizvod iz tabele!", "Upozorenje", JOptionPane.WARNING_MESSAGE); return; }
            prikaziPanelOcenaNutricioniste((String) model.getValueAt(r, 0), (String) model.getValueAt(r, 1));
        });
        zatvoriBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(oceniTabelaBtn); btnPanel.add(zatvoriBtn);
        bottom.add(countLbl, BorderLayout.WEST); bottom.add(btnPanel, BorderLayout.EAST);
        dialog.add(bottom, BorderLayout.SOUTH); dialog.setVisible(true);
    }

    
    private void prikaziDetaljeProizvoda(Proizvod p) {

        JDialog d = new JDialog(this, "Detalji proizvoda", true);
        d.setSize(500, 640);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(BG);

        // ── Header ──
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(HEADER_COLOR);
        JLabel headerLbl = new JLabel(p.getNaziv());
        headerLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLbl.setForeground(Color.WHITE);
        header.add(headerLbl);
        d.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(18, 24, 18, 24));

        int r2 = 0;
        String[][] shortFields = {
            {"Barkod", p.getBarKod()},
            {"Naziv", p.getNaziv()},
            {"Posno", p.isPosan()? "Da":"Ne"},
            {"Halal", p.isHalal()? "Da":"Ne"},
            {"Energija (kJ)", String.valueOf(p.getEnergijaKJ())},
            {"Energija (kcal)", String.valueOf(p.getEnergijaKcal())},
            {"Masti (g)", String.valueOf(p.getMasti())},
            {"Zasićene masti (g)", String.valueOf(p.getZasiceneMasti())},
            {"Ugljeni hidrati (g)", String.valueOf(p.getUgljeniHidrati())},
            {"Šećeri (g)", String.valueOf(p.getSeceri())},
            {"Vlakna (g)", String.valueOf(p.getVlakna())},
            {"Proteini (g)", String.valueOf(p.getProteini())},
            {"So (g)", String.valueOf(p.getSo())}
        };
        for (String[] sf : shortFields) {
            GridBagConstraints gcL = new GridBagConstraints();
            gcL.gridx = 0; gcL.gridy = r2; gcL.anchor = GridBagConstraints.WEST; gcL.insets = new Insets(4, 0, 4, 14);
            JLabel fl = new JLabel(sf[0] + ":"); fl.setFont(new Font("Segoe UI", Font.BOLD, 13)); fl.setForeground(HEADER_COLOR);
            content.add(fl, gcL);

            GridBagConstraints gcF = new GridBagConstraints();
            gcF.gridx = 1; gcF.gridy = r2; gcF.anchor = GridBagConstraints.WEST; gcF.fill = GridBagConstraints.HORIZONTAL; gcF.weightx = 1.0; gcF.insets = new Insets(4, 0, 4, 0);
            JTextField tf = new JTextField(sf[1] == null ? "" : sf[1]);
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13)); tf.setEditable(false); tf.setBackground(CARD_BG);
            tf.setBorder(BorderFactory.createCompoundBorder(new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(4, 8, 4, 8)));
            tf.setPreferredSize(new Dimension(220, 30));
            content.add(tf, gcF);
            r2++;
        }
        // -- Preporucena dnevna doza (TextArea) --
        GridBagConstraints gcDozaLbl = new GridBagConstraints();
        gcDozaLbl.gridx = 0; gcDozaLbl.gridy = r2; gcDozaLbl.gridwidth = 2; gcDozaLbl.anchor = GridBagConstraints.WEST; gcDozaLbl.insets = new Insets(10, 0, 3, 0);
        JLabel dozaLbl = new JLabel("Preporučena dnevna doza:"); dozaLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); dozaLbl.setForeground(HEADER_COLOR);
        content.add(dozaLbl, gcDozaLbl); r2++;
        GridBagConstraints gcDozaArea = new GridBagConstraints();
        gcDozaArea.gridx = 0; gcDozaArea.gridy = r2; gcDozaArea.gridwidth = 2; gcDozaArea.fill = GridBagConstraints.BOTH; gcDozaArea.weightx = 1.0; gcDozaArea.weighty = 0.4; gcDozaArea.insets = new Insets(0, 0, 5, 0);
        JTextArea dozaArea = new JTextArea(p.getPreporucenaDnevnaDoza() == null ? "" : p.getPreporucenaDnevnaDoza());
        dozaArea.setFont(new Font("Segoe UI", Font.PLAIN, 13)); dozaArea.setEditable(false); dozaArea.setBackground(CARD_BG); dozaArea.setLineWrap(true); dozaArea.setWrapStyleWord(true); dozaArea.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane dozaScroll = new JScrollPane(dozaArea);
        dozaScroll.setBorder(new LineBorder(NUTR_BORDER, 1, true));
        dozaScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dozaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dozaScroll.setPreferredSize(new Dimension(0, 85));
        content.add(dozaScroll, gcDozaArea); r2++;
        // -- Sastojci (TextArea) --
        GridBagConstraints gcSasLbl = new GridBagConstraints();
        gcSasLbl.gridx = 0; gcSasLbl.gridy = r2; gcSasLbl.gridwidth = 2; gcSasLbl.anchor = GridBagConstraints.WEST; gcSasLbl.insets = new Insets(10, 0, 3, 0);
        JLabel sasLbl = new JLabel("Sastojci:"); sasLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); sasLbl.setForeground(HEADER_COLOR);
        content.add(sasLbl, gcSasLbl); r2++;
        GridBagConstraints gcSasArea = new GridBagConstraints();
        gcSasArea.gridx = 0; gcSasArea.gridy = r2; gcSasArea.gridwidth = 2; gcSasArea.fill = GridBagConstraints.BOTH; gcSasArea.weightx = 1.0; gcSasArea.weighty = 0.6; gcSasArea.insets = new Insets(0, 0, 5, 0);
        JTextArea sasArea = new JTextArea(p.getSastojci() == null ? "" : p.getSastojci());
        sasArea.setFont(new Font("Segoe UI", Font.PLAIN, 13)); sasArea.setEditable(false); sasArea.setBackground(CARD_BG); sasArea.setLineWrap(true); sasArea.setWrapStyleWord(true); sasArea.setBorder(new EmptyBorder(6, 8, 6, 8));
        JScrollPane sasScroll = new JScrollPane(sasArea);
        sasScroll.setBorder(new LineBorder(NUTR_BORDER, 1, true));
        sasScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sasScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sasScroll.setPreferredSize(new Dimension(0, 100));
        content.add(sasScroll, gcSasArea);

        JScrollPane scrollContent = new JScrollPane(content);
        scrollContent.setBorder(null);
        scrollContent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollContent.getVerticalScrollBar().setUnitIncrement(14);
        d.add(scrollContent, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(BG);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, NUTR_BORDER));
        JButton oceniBtn = styleBtn("Oceni proizvod", ACCENT);
        JButton zatvoriBtn2 = styleBtn("Zatvori", new Color(200, 60, 60));
        oceniBtn.addActionListener(e -> { d.dispose(); prikaziPanelOcenaNutricioniste(p.getBarKod(), p.getNaziv()); });
        zatvoriBtn2.addActionListener(e -> d.dispose());
        footer.add(oceniBtn);
        footer.add(zatvoriBtn2);
        d.add(footer, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(HEADER_COLOR);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
    public void prikaziPanelOcenaNutricioniste(String barkod, String naziv) {
        JDialog d = new JDialog(this, "Ocena proizvoda", true);
        d.setSize(480, 600);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        d.getContentPane().setBackground(BG);

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(HEADER_COLOR);
        JLabel headerLbl = new JLabel("Ocenite: " + naziv);
        headerLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        headerLbl.setForeground(Color.WHITE);
        header.add(headerLbl);
        d.add(header, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG);
        content.setBorder(new EmptyBorder(20, 25, 20, 25));

        

        // Barkod
        JLabel barkodLbl = new JLabel("Barkod: " + barkod);
        barkodLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        barkodLbl.setForeground(Color.GRAY);
        barkodLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Ocene
        ButtonGroup group = new ButtonGroup();
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioPanel.setBackground(BG);
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JRadioButton[] ocene = new JRadioButton[5];
        for (int i = 0; i < 5; i++) {
            ocene[i] = new JRadioButton(String.valueOf(i + 1));
            ocene[i].setBackground(BG);
            group.add(ocene[i]);
            radioPanel.add(ocene[i]);
        }

        // Tekstualna polja
        JTextArea komentarArea = new JTextArea(); 
        JTextArea dozaArea = new JTextArea();
        komentarArea.setLineWrap(true);
        komentarArea.setWrapStyleWord(true);
        komentarArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); 

        dozaArea.setLineWrap(true);
        dozaArea.setWrapStyleWord(true);
        dozaArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); 
        
        // Stil za polja
        LineBorder border = new LineBorder(NUTR_BORDER, 1, true);
        komentarArea.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(5, 5, 5, 5)));
        dozaArea.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(5, 5, 5, 5)));

        // Srazmerno pakovanje
        content.add(barkodLbl);
        content.add(Box.createVerticalStrut(15));
        content.add(createLabel("Ocena (1 - 5):"));
        content.add(radioPanel);
        content.add(Box.createVerticalStrut(15));
        content.add(createLabel("Komentar:"));
        content.add(Box.createVerticalStrut(5));
        JScrollPane ks = new JScrollPane(komentarArea);
        ks.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150)); 
        ks.setBorder(null);
        ks.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        ks.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        content.add(ks);
        content.add(Box.createVerticalStrut(15));
        content.add(createLabel("Preporučena dnevna doza:"));
        content.add(Box.createVerticalStrut(5));
        JScrollPane ds = new JScrollPane(dozaArea);
        ds.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80)); 
        ds.setBorder(null);
        ds.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ds.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        content.add(ds);

        d.add(content, BorderLayout.CENTER);

        // Footer
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(BG);
        bottom.setBorder(new MatteBorder(1, 0, 0, 0, NUTR_BORDER));
        JButton otkaziBtn = styleBtn("Otkaži", new Color(200, 200, 210));
        JButton sacuvajBtn = styleBtn("Sačuvaj ocenu", ACCENT);
        
        otkaziBtn.addActionListener(e -> d.dispose());
        sacuvajBtn.addActionListener(e -> {
        	String komentar=komentarArea.getText().trim();
        	String preporucenaDoza=dozaArea.getText().trim();
        	int izabranaOcena = 0; // Podrazumevana vrednost
        	for (JRadioButton rb : ocene) {
        	    if (rb.isSelected()) {
        	        izabranaOcena = Integer.parseInt(rb.getText());
        	        break; 
        	    }
        	}
        	if (!KomentarKontroler.validacijaOcene(komentar, izabranaOcena, preporucenaDoza)) {
				prikaziGresku("Ako želite da ocenite proizvod morate popuniti polja");
			} else {
				try {
					dao.unesiOcenuProizvoda(nutricionista.getIdNutricioniste(), barkod, komentar, preporucenaDoza, izabranaOcena);
					uspesnoDodataOcena(naziv, izabranaOcena, komentar);
					d.dispose();
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, e2.getMessage(), "Greška", JOptionPane.ERROR_MESSAGE);
				}
			}
        	});
        
        bottom.add(otkaziBtn); bottom.add(sacuvajBtn);
        d.add(bottom, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    public void uspesnoDodataOcena(String naziv, int ocena, String komentar) { JOptionPane.showMessageDialog(this, "Ocena uspešno sačuvana!\nProizvod: " + naziv + "\nOcena: " + ocena, "Uspešno", JOptionPane.INFORMATION_MESSAGE); }

    public void prikaziPanelPrikaziZahteve() {
        JDialog dialog = new JDialog(this, "Poruke korisnika", true);
        dialog.setSize(860, 600); dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout()); dialog.getContentPane().setBackground(BG);

        // ── Header ──
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        header.setBackground(HEADER_COLOR);
        JLabel lbl = new JLabel("Poruke korisnika"); lbl.setFont(new Font("Segoe UI", Font.BOLD, 17)); lbl.setForeground(Color.WHITE);
        header.add(lbl);
        dialog.add(header, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        ArrayList<Korisnik> posiljaociList = new ArrayList<>();
        try {
            ArrayList<Korisnik> posiljaoci = dao.vratiZahteve(nutricionista.getIdNutricioniste());
            if (posiljaoci != null) {
                for (Korisnik k : posiljaoci) {
                    listModel.addElement(k.getIme() + " " + k.getPrezime());
                    posiljaociList.add(k);
                }
            }
        } catch (Exception ex) { listModel.addElement("Greška pri učitavanju"); }

        JList<String> korisnikList = new JList<>(listModel);
        korisnikList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        korisnikList.setBackground(CARD_BG);
        korisnikList.setSelectionBackground(new Color(210, 225, 245));
        korisnikList.setFixedCellHeight(38);
        korisnikList.setBorder(new EmptyBorder(5, 10, 5, 10));
        JScrollPane listScroll = new JScrollPane(korisnikList);
        listScroll.setPreferredSize(new Dimension(200, 0));
        listScroll.setBorder(new MatteBorder(0, 0, 0, 1, NUTR_BORDER));

        // ── Desna strana: chat + polje za odgovor ──
        JPanel desnaStrana = new JPanel(new BorderLayout());
        desnaStrana.setBackground(new Color(245, 246, 250));

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(245, 246, 250));
        JScrollPane chatScroll = new JScrollPane(chatPanel);
        chatScroll.setBorder(null);
        chatScroll.getVerticalScrollBar().setUnitIncrement(12);

        JLabel chatPlaceholder = new JLabel("  Odaberite korisnika sa liste da vidite zahteve.");
        chatPlaceholder.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        chatPlaceholder.setForeground(Color.GRAY);
        chatPanel.add(Box.createVerticalStrut(15));
        chatPanel.add(chatPlaceholder);

        // ── Panel za unos odgovora (inicijalno skriven) ──
        JPanel odgovorPanel = new JPanel(new BorderLayout(8, 0));
        odgovorPanel.setBackground(CARD_BG);
        odgovorPanel.setBorder(new EmptyBorder(10, 12, 10, 12));
        odgovorPanel.setVisible(false);

        JTextArea odgovorArea = new JTextArea(3, 0);
        odgovorArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        odgovorArea.setLineWrap(true); odgovorArea.setWrapStyleWord(true);
        odgovorArea.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(6, 8, 6, 8)));
        JScrollPane odgovorScroll = new JScrollPane(odgovorArea);
        odgovorScroll.setBorder(null);

        JButton posaljiOdgovorBtn = styleBtn("Pošalji odgovor", new Color(25, 85, 55));
        JPanel odgovorBtnPanel = new JPanel(new BorderLayout());
        odgovorBtnPanel.setOpaque(false);
        odgovorBtnPanel.add(posaljiOdgovorBtn, BorderLayout.SOUTH);

        odgovorPanel.add(odgovorScroll, BorderLayout.CENTER);
        odgovorPanel.add(odgovorBtnPanel, BorderLayout.EAST);

        desnaStrana.add(chatScroll, BorderLayout.CENTER);
        desnaStrana.add(odgovorPanel, BorderLayout.SOUTH);

        // ── Holder za selektovani zahtev ──
        final ZahtevZaSaradnju[] selektovaniZahtev = {null};

        // ── Listener za izbor korisnika ──
        korisnikList.addListSelectionListener(ev -> {
            if (ev.getValueIsAdjusting()) return;
            int idx = korisnikList.getSelectedIndex();
            if (idx < 0 || idx >= posiljaociList.size()) return;
            Korisnik odabraniKor = posiljaociList.get(idx);
            selektovaniZahtev[0] = null;
            odgovorArea.setText("");
            odgovorPanel.setVisible(false);
            chatPanel.removeAll();
            chatPanel.add(Box.createVerticalStrut(10));
            try {
                ArrayList<ZahtevZaSaradnju> zahtevi = dao.vratiZahteve(odabraniKor.getIdKorisnika(), nutricionista.getIdNutricioniste());
                if (zahtevi == null || zahtevi.isEmpty()) {
                    JLabel prazan = new JLabel("  Nema zahteva od ovog korisnika.");
                    prazan.setFont(new Font("Segoe UI", Font.ITALIC, 13)); prazan.setForeground(Color.GRAY);
                    chatPanel.add(prazan);
                } else {
                    for (ZahtevZaSaradnju z : zahtevi) {
                        String tipTekst = (z.getTip() != null && !z.getTip().isEmpty()) ? "  [" + z.getTip() + "]" : "";

                        // Balon korisnika (opis/zahtev) — uvek se prikazuje
                        chatPanel.add(napraviChatBalon(
                            odabraniKor.getIme() + " " + odabraniKor.getPrezime() + tipTekst,
                            z.getOpis() != null ? z.getOpis() : "",
                            false
                        ));

                        // Balon nutricioniste (odgovor) — samo ako postoji
                        if (z.getOdgovor() != null && !z.getOdgovor().trim().isEmpty()) {
                            chatPanel.add(napraviChatBalon("Vi", z.getOdgovor(), true));
                            // Status labela ispod odgovora
                            String stTekst = ""; Color stBoja = Color.GRAY;
                            String stVal = z.getStatus() != null ? z.getStatus() : "";
                            if ("odgovoren".equals(stVal)) { stTekst = "  Odgovoreno — ceka se placanje"; stBoja = new Color(180, 120, 20); }
                            else if ("dostavljen_odgovor".equals(stVal)) { stTekst = "  Zavrseno"; stBoja = new Color(30, 120, 60); }
                            if (!stTekst.isEmpty()) {
                                JLabel stLbl = new JLabel(stTekst);
                                stLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11)); stLbl.setForeground(stBoja);
                                JPanel stRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
                                stRow.setBackground(new Color(245, 246, 250)); stRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
                                stRow.add(stLbl); chatPanel.add(stRow);
                            }
                        } else {
                            // Prikazujemo dugmad prema statusu zahteva
                            String statusZ = z.getStatus() != null ? z.getStatus() : "poslat";
                            JPanel akcijeBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
                            akcijeBtnPanel.setBackground(new Color(245, 246, 250));
                            akcijeBtnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                            final ZahtevZaSaradnju ovajZahtev = z;

                            if ("poslat".equals(statusZ)) {
                                // Zahtev ceka — nudimo Prihvati / Odbij
                                JButton prihvatiBtn = styleBtn("Prihvati", new Color(40, 160, 80));
                                prihvatiBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                                prihvatiBtn.setBorder(new EmptyBorder(4, 12, 4, 12));
                                JButton odbijBtn = styleBtn("Odbij", new Color(200, 50, 60));
                                odbijBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                                odbijBtn.setBorder(new EmptyBorder(4, 12, 4, 12));
                                prihvatiBtn.addActionListener(ae -> {
                                    int p = JOptionPane.showConfirmDialog(dialog,
                                        "Prihvatate zahtev od " + odabraniKor.getIme() + " " + odabraniKor.getPrezime() + "?",
                                        "Potvrda", JOptionPane.YES_NO_OPTION);
                                    if (p != JOptionPane.YES_OPTION) return;
                                    try {
                                        dao.prihvatiZahtev(ovajZahtev.getIdZahteva());
                                        JOptionPane.showMessageDialog(dialog, "Zahtev je prihvacen.", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                                        int idx1 = korisnikList.getSelectedIndex();
                                        korisnikList.clearSelection(); korisnikList.setSelectedIndex(idx1);
                                    } catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }
                                });
                                odbijBtn.addActionListener(ae -> {
                                    int p = JOptionPane.showConfirmDialog(dialog,
                                        "Odbijate zahtev od " + odabraniKor.getIme() + " " + odabraniKor.getPrezime() + "?",
                                        "Potvrda", JOptionPane.YES_NO_OPTION);
                                    if (p != JOptionPane.YES_OPTION) return;
                                    try {
                                        dao.odbijZahtev(ovajZahtev.getIdZahteva());
                                        JOptionPane.showMessageDialog(dialog, "Zahtev je odbijen.", "Informacija", JOptionPane.INFORMATION_MESSAGE);
                                        int idx2 = korisnikList.getSelectedIndex();
                                        korisnikList.clearSelection(); korisnikList.setSelectedIndex(idx2);
                                    } catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }
                                });
                                akcijeBtnPanel.add(prihvatiBtn); akcijeBtnPanel.add(odbijBtn);

                            } else if ("prihvacen".equals(statusZ)) {
                                // Prihvacen — nudimo Odgovori
                                JButton odgovoriBtn = styleBtn("Odgovori", new Color(70, 130, 180));
                                odgovoriBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                                odgovoriBtn.setBorder(new EmptyBorder(4, 12, 4, 12));
                                odgovoriBtn.addActionListener(ae -> {
                                    selektovaniZahtev[0] = ovajZahtev;
                                    odgovorArea.setText("");
                                    odgovorPanel.setVisible(true);
                                    odgovorArea.requestFocus();
                                    dialog.revalidate();
                                });
                                akcijeBtnPanel.add(odgovoriBtn);

                            } else if ("neprihvacen".equals(statusZ)) {
                                // Odbijen — ponovo nudimo Prihvati/Odbij da korisnik može da promeni odluku
                                JButton prihvatiBtn2 = styleBtn("Prihvati", new Color(40, 160, 80));
                                prihvatiBtn2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                                prihvatiBtn2.setBorder(new EmptyBorder(4, 12, 4, 12));
                                prihvatiBtn2.addActionListener(ae -> {
                                    try {
                                        dao.prihvatiZahtev(ovajZahtev.getIdZahteva());
                                        int idx2 = korisnikList.getSelectedIndex();
                                        korisnikList.clearSelection(); korisnikList.setSelectedIndex(idx2);
                                    } catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }
                                });
                                JButton odbijBtn2 = styleBtn("Odbij", new Color(200, 60, 60));
                                odbijBtn2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                                odbijBtn2.setBorder(new EmptyBorder(4, 12, 4, 12));
                                odbijBtn2.addActionListener(ae -> {
                                    try {
                                        dao.odbijZahtev(ovajZahtev.getIdZahteva());
                                        int idx2 = korisnikList.getSelectedIndex();
                                        korisnikList.clearSelection(); korisnikList.setSelectedIndex(idx2);
                                    } catch (Exception ex) { prikaziGresku("Greska: " + ex.getMessage()); }
                                });
                                akcijeBtnPanel.add(prihvatiBtn2); akcijeBtnPanel.add(odbijBtn2);
                            }
                            chatPanel.add(akcijeBtnPanel);
                        }
                        chatPanel.add(Box.createVerticalStrut(6));
                    }
                }
            } catch (Exception ex) {
                JLabel errLbl = new JLabel("  Greška: " + ex.getMessage());
                errLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12)); errLbl.setForeground(ERROR_RED);
                chatPanel.add(errLbl);
            }
            chatPanel.add(Box.createVerticalStrut(10));
            chatPanel.revalidate(); chatPanel.repaint();
            SwingUtilities.invokeLater(() -> {
                JScrollBar sb = chatScroll.getVerticalScrollBar(); sb.setValue(sb.getMaximum());
            });
        });

        // ── Akcija slanja odgovora ──
        posaljiOdgovorBtn.addActionListener(e -> {
            if (selektovaniZahtev[0] == null) return;
            String tekst = odgovorArea.getText().trim();
            if (!ZahtevKontroler.validirajOdgovor(tekst)) { JOptionPane.showMessageDialog(dialog, "Unesite odgovor.", "Upozorenje", JOptionPane.WARNING_MESSAGE); return; }

            int iznos = 0;

            // Ako je Plan ishrane — tražimo iznos
            if ("Plan ishrane".equals(selektovaniZahtev[0].getTip())) {
                JPanel iznosPanel = new JPanel();
                iznosPanel.setLayout(new BoxLayout(iznosPanel, BoxLayout.Y_AXIS));
                iznosPanel.setBackground(CARD_BG);

                JLabel iznosLbl = new JLabel("Unesite iznos za plan ishrane (RSD):");
                iznosLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                iznosLbl.setForeground(HEADER_COLOR);
                iznosLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

                JTextField iznosField = new JTextField();
                iznosField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                iznosField.setPreferredSize(new Dimension(250, 36));
                iznosField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                iznosField.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(NUTR_BORDER, 1, true), new EmptyBorder(4, 8, 4, 8)));
                iznosField.setAlignmentX(Component.LEFT_ALIGNMENT);

                iznosPanel.add(iznosLbl);
                iznosPanel.add(Box.createVerticalStrut(8));
                iznosPanel.add(iznosField);

                int rezultat = JOptionPane.showConfirmDialog(
                    dialog, iznosPanel, "Iznos za plan ishrane",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (rezultat != JOptionPane.OK_OPTION) return;

                String iznosTekst = iznosField.getText().trim();
                try {
                    iznos = Integer.parseInt(iznosTekst);
                    if (iznos <= 0) throw new NumberFormatException();
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(dialog, "Unesite validan iznos (ceo broj veći od 0).", "Neispravni podaci", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try {
                dao.dodajOdgovorNaZahtev(selektovaniZahtev[0].getIdZahteva(), tekst, iznos);
                JOptionPane.showMessageDialog(dialog, "Odgovor je uspešno poslat!", "Uspeh", JOptionPane.INFORMATION_MESSAGE);
                odgovorPanel.setVisible(false);
                odgovorArea.setText("");
                selektovaniZahtev[0] = null;
                // Osveži chat — simuliraj ponovni klik na istog korisnika
                int idx = korisnikList.getSelectedIndex();
                korisnikList.clearSelection();
                korisnikList.setSelectedIndex(idx);
            } catch (Exception ex) {
                prikaziGresku("Greška pri slanju odgovora: " + ex.getMessage());
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, desnaStrana);
        split.setDividerLocation(200); split.setDividerSize(1); split.setBorder(null);
        dialog.add(split, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); bottom.setBackground(BG);
        JButton zatvoriBtn = styleBtn("Zatvori", new Color(200, 60, 60)); zatvoriBtn.addActionListener(e -> dialog.dispose());
        bottom.add(zatvoriBtn);
        dialog.add(bottom, BorderLayout.SOUTH); dialog.setVisible(true);
    }

    // ═══════════════════════════════════════════════════════════
    // POMOCNE METODE
    // ═══════════════════════════════════════════════════════════
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13)); table.setRowHeight(32); table.setGridColor(NUTR_BORDER);
        table.setSelectionBackground(new Color(210, 225, 245)); table.setSelectionForeground(HEADER_COLOR);
        table.setShowVerticalLines(false); table.setIntercellSpacing(new Dimension(0, 1)); table.setFillsViewportHeight(true);
        JTableHeader th = table.getTableHeader(); th.setFont(new Font("Segoe UI", Font.BOLD, 13)); th.setBackground(TABLE_HEADER_C); th.setForeground(HEADER_COLOR);
        th.setBorder(new MatteBorder(0, 0, 2, 0, ACCENT)); th.setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? TABLE_ROW1 : TABLE_ROW2);
                setBorder(new EmptyBorder(0, 10, 0, 10)); return this;
            }
        });
    }

    private JButton createBigButton(String text) {
        JButton btn = new JButton(text); btn.setFont(new Font("Segoe UI", Font.BOLD, 20)); btn.setForeground(Color.WHITE); btn.setBackground(PRIMARY_START);
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); btn.setBorder(new EmptyBorder(30, 20, 30, 20));
        btn.addMouseListener(new MouseAdapter() { public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_END); } public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY_START); } });
        return btn;
    }

    private JButton styleBtn(String text, Color bg) {
        JButton btn = new JButton(text); btn.setFont(new Font("Segoe UI", Font.BOLD, 13)); btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setOpaque(true); btn.setBorderPainted(false); btn.setBorder(new EmptyBorder(8, 20, 8, 20)); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}