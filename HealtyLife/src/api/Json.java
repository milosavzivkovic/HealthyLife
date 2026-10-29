package api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mali citac JSON-a (Java nema ugradjen, a ne zelimo dodatne biblioteke).
 *
 * Pretvara JSON tekst u obicne Java objekte:
 *   objekat -> Map<String, Object>,  niz -> List<Object>,
 *   tekst -> String,  broj -> Double,  true/false -> Boolean,  null -> null
 *
 * Pomocne metode (tekst, broj, objekat, niz) bezbedno citaju vrednosti
 * i vracaju null ako polje ne postoji ili je drugog tipa.
 */
public final class Json {

	private final String s;
	private int i;

	private Json(String s) {
		this.s = s;
	}

	/** Parsira ceo JSON tekst. Baca IllegalArgumentException ako JSON nije ispravan. */
	public static Object parsiraj(String tekst) {
		Json p = new Json(tekst);
		p.razmaci();
		Object v = p.vrednost();
		p.razmaci();
		if (p.i != p.s.length()) throw p.greska("visak teksta posle JSON-a");
		return v;
	}

	// ═══════════════════════════════════════════════════════════
	// BEZBEDNO CITANJE VREDNOSTI
	// ═══════════════════════════════════════════════════════════

	@SuppressWarnings("unchecked")
	public static Map<String, Object> objekat(Object o, String kljuc) {
		Object v = o instanceof Map<?, ?> m ? m.get(kljuc) : null;
		return v instanceof Map<?, ?> ? (Map<String, Object>) v : null;
	}

	@SuppressWarnings("unchecked")
	public static List<Object> niz(Object o, String kljuc) {
		Object v = o instanceof Map<?, ?> m ? m.get(kljuc) : null;
		return v instanceof List<?> ? (List<Object>) v : List.of();
	}

	/** Tekst polja; null ako ne postoji ili je prazan. */
	public static String tekst(Object o, String kljuc) {
		Object v = o instanceof Map<?, ?> m ? m.get(kljuc) : null;
		if (v instanceof String str) return str.isBlank() ? null : str.trim();
		if (v instanceof Double d) return d == Math.rint(d) ? String.valueOf(d.longValue()) : d.toString();
		return null;
	}

	/** Broj polja; null ako ne postoji. Prihvata i broj zapisan kao tekst ("12.5"). */
	public static Double broj(Object o, String kljuc) {
		Object v = o instanceof Map<?, ?> m ? m.get(kljuc) : null;
		if (v instanceof Double d) return d;
		if (v instanceof String str) {
			try {
				return Double.parseDouble(str.trim().replace(',', '.'));
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	/** Lista tekstova iz niza (npr. ["en:milk", "en:gluten"]). */
	public static List<String> tekstovi(Object o, String kljuc) {
		List<String> r = new ArrayList<>();
		for (Object v : niz(o, kljuc)) if (v instanceof String str) r.add(str);
		return r;
	}

	// ═══════════════════════════════════════════════════════════
	// PARSER
	// ═══════════════════════════════════════════════════════════

	private Object vrednost() {
		if (i >= s.length()) throw greska("neocekivan kraj");
		char c = s.charAt(i);
		return switch (c) {
		case '{' -> objekat();
		case '[' -> niz();
		case '"' -> tekst();
		case 't' -> rec("true", Boolean.TRUE);
		case 'f' -> rec("false", Boolean.FALSE);
		case 'n' -> rec("null", null);
		default -> {
			if (c == '-' || Character.isDigit(c)) yield broj();
			throw greska("neocekivan znak '" + c + "'");
		}
		};
	}

	private Map<String, Object> objekat() {
		Map<String, Object> m = new LinkedHashMap<>();
		i++; // {
		razmaci();
		if (sledeci('}')) return m;
		while (true) {
			razmaci();
			if (i >= s.length() || s.charAt(i) != '"') throw greska("ocekivan kljuc");
			String k = tekst();
			razmaci();
			ocekuj(':');
			razmaci();
			m.put(k, vrednost());
			razmaci();
			if (sledeci('}')) return m;
			ocekuj(',');
		}
	}

	private List<Object> niz() {
		List<Object> l = new ArrayList<>();
		i++; // [
		razmaci();
		if (sledeci(']')) return l;
		while (true) {
			razmaci();
			l.add(vrednost());
			razmaci();
			if (sledeci(']')) return l;
			ocekuj(',');
		}
	}

	private String tekst() {
		i++; // "
		StringBuilder sb = new StringBuilder();
		while (i < s.length()) {
			char c = s.charAt(i++);
			if (c == '"') return sb.toString();
			if (c != '\\') {
				sb.append(c);
				continue;
			}
			if (i >= s.length()) break;
			char e = s.charAt(i++);
			switch (e) {
			case '"', '\\', '/' -> sb.append(e);
			case 'b' -> sb.append('\b');
			case 'f' -> sb.append('\f');
			case 'n' -> sb.append('\n');
			case 'r' -> sb.append('\r');
			case 't' -> sb.append('\t');
			case 'u' -> {
				if (i + 4 > s.length()) throw greska("neispravan \\u");
				sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
				i += 4;
			}
			default -> throw greska("neispravan escape \\" + e);
			}
		}
		throw greska("nezavrsen tekst");
	}

	private Double broj() {
		int start = i;
		while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) i++;
		try {
			return Double.parseDouble(s.substring(start, i));
		} catch (NumberFormatException e) {
			throw greska("neispravan broj");
		}
	}

	private Object rec(String rec, Object vrednost) {
		if (!s.startsWith(rec, i)) throw greska("ocekivano " + rec);
		i += rec.length();
		return vrednost;
	}

	private void razmaci() {
		while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
	}

	private boolean sledeci(char c) {
		if (i < s.length() && s.charAt(i) == c) {
			i++;
			return true;
		}
		return false;
	}

	private void ocekuj(char c) {
		if (!sledeci(c)) throw greska("ocekivano '" + c + "'");
	}

	private IllegalArgumentException greska(String poruka) {
		return new IllegalArgumentException("Neispravan JSON (pozicija " + i + "): " + poruka);
	}
}
