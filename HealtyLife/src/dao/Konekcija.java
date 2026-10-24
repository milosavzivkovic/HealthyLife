package dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Jedno mesto za podesavanja baze i otvaranje konekcija.
 *
 * Podesavanja se citaju redom (kasnije prepisuje ranije):
 *   1. podrazumevane vrednosti (lokalni XAMPP, root bez lozinke)
 *   2. db.properties iz src foldera (deo projekta)
 *   3. db.local.properties u folderu iz kog se aplikacija pokrece (licno, nije u Git-u)
 */
public final class Konekcija {

	private static final String URL;
	private static final String KORISNIK;
	private static final String LOZINKA;

	static {
		Properties p = new Properties();
		p.setProperty("db.url", "jdbc:mysql://localhost/healthylifebaza1");
		p.setProperty("db.korisnik", "root");
		p.setProperty("db.lozinka", "");

		try (InputStream in = Konekcija.class.getResourceAsStream("/db.properties")) {
			if (in != null) p.load(in);
		} catch (IOException e) {
			System.err.println("Ne mogu da procitam db.properties: " + e.getMessage());
		}

		Path lokalni = Path.of("db.local.properties");
		if (Files.exists(lokalni)) {
			try (InputStream in = Files.newInputStream(lokalni)) {
				p.load(in);
			} catch (IOException e) {
				System.err.println("Ne mogu da procitam db.local.properties: " + e.getMessage());
			}
		}

		URL = p.getProperty("db.url");
		KORISNIK = p.getProperty("db.korisnik");
		LOZINKA = p.getProperty("db.lozinka");

		// Drajver se ucitava samo jednom, a ne pri svakom upitu kao ranije.
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("MySQL drajver nije pronadjen: " + e.getMessage());
		}
	}

	private Konekcija() {
	}

	public static Connection otvori() throws SQLException {
		return DriverManager.getConnection(URL, KORISNIK, LOZINKA);
	}
}
