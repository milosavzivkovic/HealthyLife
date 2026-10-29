package api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import model.ApiProizvod;
import model.RezultatPretrage;

/**
 * Klijent za Open Food Facts - besplatnu javnu bazu proizvoda (openfoodfacts.org).
 *
 * Koristi dva servisa:
 *   - world.openfoodfacts.org/api/v2/product/{bar-kod}  -> detalji jednog proizvoda
 *   - search.openfoodfacts.org/search?q=...             -> pretraga po nazivu
 *
 * Kljuc (API key) nije potreban, ali Open Food Facts trazi da se aplikacija
 * predstavi (User-Agent) i da ne salje previse zahteva: ~100 proizvoda i
 * ~10 pretraga u minuti.
 *
 * Sve metode rade preko interneta i mogu da potraju - ne pozivati ih iz
 * Swing niti (EDT), vec u pozadini (SwingWorker).
 */
public final class OpenFoodFacts {

	private static final String PROIZVOD_URL = "https://world.openfoodfacts.org/api/v2/product/";
	private static final String PRETRAGA_URL = "https://search.openfoodfacts.org/search";
	private static final String USER_AGENT = "HealthyLife/0.2 (studentski projekat, FTN)";

	/** Polja koja trazimo - manje polja = brzi odgovor. */
	private static final String POLJA_PROIZVODA = String.join(",",
			"code", "product_name", "product_name_sr", "product_name_hr", "product_name_bs", "product_name_en",
			"brands", "quantity",
			"ingredients_text", "ingredients_text_sr", "ingredients_text_hr", "ingredients_text_bs", "ingredients_text_en",
			"nutriments", "nutriscore_grade", "nutrition_grades", "nova_group", "allergens_tags", "labels_tags",
			"ingredients_analysis_tags", "image_front_url");

	private final HttpClient http = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(8))
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();

	/** Greska pri radu sa API-jem, sa porukom koja moze da se prikaze korisniku. */
	public static class ApiGreska extends Exception {
		private static final long serialVersionUID = 1L;

		public ApiGreska(String poruka, Throwable uzrok) {
			super(poruka, uzrok);
		}
	}

	// ═══════════════════════════════════════════════════════════
	// PROIZVOD PO BAR-KODU
	// ═══════════════════════════════════════════════════════════

	/** Vraca proizvod, ili null ako ga Open Food Facts nema. */
	public ApiProizvod nadjiProizvod(String barKod) throws ApiGreska {
		String kod = barKod.replaceAll("[^0-9]", "");
		HttpResponse<String> odgovor = posalji(PROIZVOD_URL + kod + ".json?fields=" + POLJA_PROIZVODA);
		if (odgovor.statusCode() == 404) return null;
		proveriStatus(odgovor);

		Object json = parsiraj(odgovor.body());
		Map<String, Object> p = Json.objekat(json, "product");
		Double status = Json.broj(json, "status");
		if (p == null || status == null || status != 1) return null;
		return napraviProizvod(kod, p);
	}

	private static ApiProizvod napraviProizvod(String kod, Map<String, Object> p) {
		Map<String, Object> n = Json.objekat(p, "nutriments");

		// Naziv i sastojci: prvo na srpskom/hrvatskom/bosanskom, pa podrazumevani, pa engleski
		String naziv = prvi(p, "product_name_sr", "product_name_hr", "product_name_bs", "product_name", "product_name_en");
		String sastojci = prvi(p, "ingredients_text_sr", "ingredients_text_hr", "ingredients_text_bs", "ingredients_text",
				"ingredients_text_en");

		Double kcal = Json.broj(n, "energy-kcal_100g");
		Double kj = Json.broj(n, "energy-kj_100g");
		if (kj == null) kj = Json.broj(n, "energy_100g"); // "energy" je uvek u kJ
		if (kcal == null && kj != null) kcal = kj / 4.184;
		if (kj == null && kcal != null) kj = kcal * 4.184;

		String ns = Json.tekst(p, "nutriscore_grade");
		if (ns == null || !ns.matches("[a-eA-E]")) ns = Json.tekst(p, "nutrition_grades"); // stariji naziv istog polja
		if (ns != null && !ns.matches("[a-eA-E]")) ns = null; // "unknown", "not-applicable"...

		Double nova = Json.broj(p, "nova_group");

		List<String> analiza = Json.tekstovi(p, "ingredients_analysis_tags");
		Boolean posno = analiza.contains("en:vegan") ? Boolean.TRUE
				: analiza.contains("en:non-vegan") ? Boolean.FALSE : null;
		Boolean halal = Json.tekstovi(p, "labels_tags").contains("en:halal") ? Boolean.TRUE : null;

		List<String> alergeni = new ArrayList<>();
		for (String a : Json.tekstovi(p, "allergens_tags")) alergeni.add(prevediAlergen(a));

		return new ApiProizvod(kod, naziv != null ? naziv : "(proizvod bez naziva)", prviBrend(Json.tekst(p, "brands")),
				Json.tekst(p, "quantity"), sastojci,
				kj, kcal, Json.broj(n, "fat_100g"), Json.broj(n, "saturated-fat_100g"),
				Json.broj(n, "carbohydrates_100g"), Json.broj(n, "sugars_100g"), Json.broj(n, "fiber_100g"),
				Json.broj(n, "proteins_100g"), Json.broj(n, "salt_100g"),
				ns == null ? null : ns.toLowerCase(), nova == null ? null : nova.intValue(),
				alergeni, posno, halal, Json.tekst(p, "image_front_url"));
	}

	// ═══════════════════════════════════════════════════════════
	// PRETRAGA PO NAZIVU
	// ═══════════════════════════════════════════════════════════

	/**
	 * Trazi proizvode po nazivu ili brendu.
	 * @param samoSrbija ako je true, vraca samo proizvode koji se prodaju u Srbiji
	 */
	public List<RezultatPretrage> pretrazi(String upit, boolean samoSrbija) throws ApiGreska {
		String q = upit.trim() + (samoSrbija ? " countries_tags:\"en:serbia\"" : "");
		String url = PRETRAGA_URL + "?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8)
				+ "&page_size=30&fields=code,product_name,brands,quantity,nutriscore_grade";
		HttpResponse<String> odgovor = posalji(url);
		proveriStatus(odgovor);

		List<RezultatPretrage> rezultati = new ArrayList<>();
		for (Object hit : Json.niz(parsiraj(odgovor.body()), "hits")) {
			String kod = Json.tekst(hit, "code");
			if (kod == null) continue;
			// U servisu za pretragu "brands" je niz, a ne tekst
			List<String> brendovi = Json.tekstovi(hit, "brands");
			String ns = Json.tekst(hit, "nutriscore_grade");
			rezultati.add(new RezultatPretrage(kod,
					Json.tekst(hit, "product_name") != null ? Json.tekst(hit, "product_name") : "(bez naziva)",
					brendovi.isEmpty() ? null : brendovi.get(0),
					Json.tekst(hit, "quantity"),
					ns != null && ns.matches("[a-e]") ? ns : null));
		}
		return rezultati;
	}

	// ═══════════════════════════════════════════════════════════
	// SLIKA PROIZVODA
	// ═══════════════════════════════════════════════════════════

	/** Preuzima sliku proizvoda; null ako ne uspe (slika nije obavezna). */
	public java.awt.image.BufferedImage slika(String url) {
		if (url == null) return null;
		try {
			HttpRequest zahtev = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(10))
					.header("User-Agent", USER_AGENT).GET().build();
			HttpResponse<byte[]> odgovor = http.send(zahtev, HttpResponse.BodyHandlers.ofByteArray());
			if (odgovor.statusCode() != 200) return null;
			return ImageIO.read(new ByteArrayInputStream(odgovor.body()));
		} catch (IOException | RuntimeException e) {
			return null;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		}
	}

	// ═══════════════════════════════════════════════════════════
	// POMOCNO
	// ═══════════════════════════════════════════════════════════

	private HttpResponse<String> posalji(String url) throws ApiGreska {
		HttpRequest zahtev = HttpRequest.newBuilder(URI.create(url))
				.timeout(Duration.ofSeconds(15))
				.header("User-Agent", USER_AGENT)
				.header("Accept", "application/json")
				.GET().build();
		try {
			return http.send(zahtev, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
		} catch (java.net.http.HttpTimeoutException e) {
			throw new ApiGreska("Open Food Facts ne odgovara (isteklo vreme). Pokušajte ponovo za par trenutaka.", e);
		} catch (IOException e) {
			throw new ApiGreska("Nije moguće povezati se sa Open Food Facts.\nProverite internet konekciju.", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ApiGreska("Zahtev je prekinut.", e);
		}
	}

	private static void proveriStatus(HttpResponse<String> odgovor) throws ApiGreska {
		int s = odgovor.statusCode();
		if (s == 200) return;
		if (s == 429) throw new ApiGreska("Previše zahteva ka Open Food Facts. Sačekajte minut pa pokušajte ponovo.", null);
		if (s >= 500) throw new ApiGreska("Open Food Facts je trenutno nedostupan (greška " + s + "). Pokušajte kasnije.", null);
		throw new ApiGreska("Open Food Facts je vratio grešku " + s + ".", null);
	}

	private static Object parsiraj(String telo) throws ApiGreska {
		try {
			return Json.parsiraj(telo);
		} catch (IllegalArgumentException e) {
			throw new ApiGreska("Open Food Facts je vratio neočekivan odgovor. Pokušajte kasnije.", e);
		}
	}

	private static String prvi(Map<String, Object> p, String... kljucevi) {
		for (String k : kljucevi) {
			String v = Json.tekst(p, k);
			if (v != null) return v;
		}
		return null;
	}

	/** "Bambi, Plazma" -> "Bambi" */
	private static String prviBrend(String brendovi) {
		if (brendovi == null) return null;
		return brendovi.split(",")[0].trim();
	}

	/** "en:milk" -> "Mleko" (14 alergena koje propisi zahtevaju da se oznace). */
	static String prevediAlergen(String oznaka) {
		String a = oznaka.contains(":") ? oznaka.substring(oznaka.indexOf(':') + 1) : oznaka;
		return switch (a) {
		case "gluten" -> "Gluten";
		case "milk" -> "Mleko";
		case "eggs" -> "Jaja";
		case "nuts" -> "Orašasti plodovi";
		case "peanuts" -> "Kikiriki";
		case "soybeans" -> "Soja";
		case "fish" -> "Riba";
		case "crustaceans" -> "Rakovi";
		case "molluscs" -> "Mekušci";
		case "celery" -> "Celer";
		case "mustard" -> "Senf";
		case "sesame-seeds" -> "Susam";
		case "sulphur-dioxide-and-sulphites" -> "Sulfiti";
		case "lupin" -> "Lupina";
		default -> a.replace('-', ' ');
		};
	}
}
