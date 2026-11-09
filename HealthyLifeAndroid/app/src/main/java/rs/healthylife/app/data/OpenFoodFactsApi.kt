package rs.healthylife.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * Klijent za Open Food Facts - besplatnu javnu bazu proizvoda (openfoodfacts.org).
 * Isti servisi kao u desktop verziji (HealtyLife/src/api/OpenFoodFacts.java):
 *   - world.openfoodfacts.org/api/v2/product/{bar-kod}  -> detalji proizvoda
 *   - search.openfoodfacts.org/search?q=...             -> pretraga po nazivu
 *
 * API kljuc nije potreban; Open Food Facts trazi da se aplikacija predstavi (User-Agent)
 * i da ne salje previse zahteva (~100 proizvoda i ~10 pretraga u minuti).
 */
class OpenFoodFactsApi(private val http: OkHttpClient) {

    /** Greska sa porukom koja moze direktno da se prikaze korisniku. */
    class ApiGreska(poruka: String, uzrok: Throwable? = null) : Exception(poruka, uzrok)

    private val json = Json { ignoreUnknownKeys = true }

    /** Vraca proizvod, ili null ako ga Open Food Facts nema. */
    suspend fun nadjiProizvod(barKod: String): Proizvod? = withContext(Dispatchers.IO) {
        val kod = barKod.filter { it.isDigit() }
        val telo = posalji("$PROIZVOD_URL$kod.json?fields=$POLJA_PROIZVODA") ?: return@withContext null
        val koren = parsiraj(telo)
        val p = koren["product"] as? JsonObject
        if (koren.broj("status") != 1.0 || p == null) null else napraviProizvod(kod, p)
    }

    /** Pretraga po nazivu ili brendu; samoSrbija = samo proizvodi koji se prodaju u Srbiji. */
    suspend fun pretrazi(upit: String, samoSrbija: Boolean): List<RezultatPretrage> = withContext(Dispatchers.IO) {
        val q = upit.trim() + if (samoSrbija) " countries_tags:\"en:serbia\"" else ""
        val url = PRETRAGA_URL.toHttpUrl().newBuilder()
            .addQueryParameter("q", q)
            .addQueryParameter("page_size", "30")
            .addQueryParameter("fields", "code,product_name,brands,quantity,nutriscore_grade,image_front_small_url,allergens_tags")
            .build()
        val telo = posalji(url.toString()) ?: return@withContext emptyList()
        val hits = parsiraj(telo)["hits"] as? JsonArray ?: return@withContext emptyList()
        hits.mapNotNull { h ->
            val o = h as? JsonObject ?: return@mapNotNull null
            val kod = o.tekst("code") ?: return@mapNotNull null
            RezultatPretrage(
                barKod = kod,
                naziv = o.tekst("product_name") ?: "(bez naziva)",
                // U servisu za pretragu "brands" je niz, a u podacima o proizvodu tekst
                brend = o.tekstovi("brands").firstOrNull() ?: o.tekst("brands"),
                kolicina = o.tekst("quantity"),
                nutriScore = ispravanNutriScore(o.tekst("nutriscore_grade")),
                slikaUrl = o.tekst("image_front_small_url"),
                alergeni = o.tekstovi("allergens_tags").mapNotNull { Alergen.izOznake(it) },
            )
        }
    }

    // ───────────────────────────────────────────────────────────

    private fun napraviProizvod(kod: String, p: JsonObject): Proizvod {
        val n = p["nutriments"] as? JsonObject ?: JsonObject(emptyMap())

        var kcal = n.broj("energy-kcal_100g")
        var kj = n.broj("energy-kj_100g") ?: n.broj("energy_100g") // "energy" je uvek u kJ
        if (kcal == null && kj != null) kcal = kj / 4.184
        if (kj == null && kcal != null) kj = kcal * 4.184

        val analiza = p.tekstovi("ingredients_analysis_tags")
        val oznakeAlergena = p.tekstovi("allergens_tags")

        return Proizvod(
            barKod = kod,
            // Naziv i sastojci: prvo na srpskom/hrvatskom/bosanskom, pa podrazumevani, pa engleski
            naziv = p.prvi("product_name_sr", "product_name_hr", "product_name_bs", "product_name", "product_name_en")
                ?: "(proizvod bez naziva)",
            brend = p.tekst("brands")?.substringBefore(',')?.trim(),
            kolicina = p.tekst("quantity"),
            sastojci = p.prvi("ingredients_text_sr", "ingredients_text_hr", "ingredients_text_bs", "ingredients_text", "ingredients_text_en"),
            energijaKj = kj,
            energijaKcal = kcal,
            masti = n.broj("fat_100g"),
            zasiceneMasti = n.broj("saturated-fat_100g"),
            ugljeniHidrati = n.broj("carbohydrates_100g"),
            seceri = n.broj("sugars_100g"),
            vlakna = n.broj("fiber_100g"),
            proteini = n.broj("proteins_100g"),
            so = n.broj("salt_100g"),
            nutriScore = ispravanNutriScore(p.tekst("nutriscore_grade")) ?: ispravanNutriScore(p.tekst("nutrition_grades")),
            nova = p.broj("nova_group")?.toInt()?.takeIf { it in 1..4 },
            alergeni = oznakeAlergena.mapNotNull { Alergen.izOznake(it) }.distinct(),
            ostaliAlergeni = oznakeAlergena.filter { Alergen.izOznake(it) == null }
                .map { it.substringAfter(':').replace('-', ' ') },
            posno = when {
                "en:vegan" in analiza -> true
                "en:non-vegan" in analiza -> false
                else -> null
            },
            halal = if ("en:halal" in p.tekstovi("labels_tags")) true else null,
            palminoUlje = when {
                "en:palm-oil" in analiza -> true
                "en:palm-oil-free" in analiza -> false
                else -> null
            },
            slikaUrl = p.tekst("image_front_url"),
        )
    }

    /** Salje GET zahtev; vraca telo odgovora, ili null ako proizvod ne postoji (404). */
    private fun posalji(url: String): String? {
        val zahtev = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "application/json")
            .build()
        try {
            http.newCall(zahtev).execute().use { odgovor ->
                when {
                    odgovor.code == 404 -> return null
                    odgovor.code == 429 -> throw ApiGreska("Previše zahteva ka Open Food Facts. Sačekaj minut pa pokušaj ponovo.")
                    odgovor.code >= 500 -> throw ApiGreska("Open Food Facts je trenutno nedostupan (greška ${odgovor.code}). Pokušaj kasnije.")
                    !odgovor.isSuccessful -> throw ApiGreska("Open Food Facts je vratio grešku ${odgovor.code}.")
                }
                return odgovor.body.string()
            }
        } catch (e: SocketTimeoutException) {
            throw ApiGreska("Open Food Facts ne odgovara. Pokušaj ponovo za nekoliko trenutaka.", e)
        } catch (e: IOException) {
            throw ApiGreska("Nema internet konekcije. Proveri Wi-Fi ili mobilne podatke.", e)
        }
    }

    private fun parsiraj(telo: String): JsonObject =
        try {
            json.parseToJsonElement(telo) as? JsonObject ?: throw ApiGreska("Neočekivan odgovor servera.")
        } catch (e: IllegalArgumentException) {
            throw ApiGreska("Open Food Facts je vratio neočekivan odgovor. Pokušaj kasnije.", e)
        }

    private companion object {
        const val PROIZVOD_URL = "https://world.openfoodfacts.org/api/v2/product/"
        const val PRETRAGA_URL = "https://search.openfoodfacts.org/search"
        const val USER_AGENT = "HealthyLifeAndroid/1.0 (studentski projekat)"
        val POLJA_PROIZVODA = listOf(
            "code", "product_name", "product_name_sr", "product_name_hr", "product_name_bs", "product_name_en",
            "brands", "quantity",
            "ingredients_text", "ingredients_text_sr", "ingredients_text_hr", "ingredients_text_bs", "ingredients_text_en",
            "nutriments", "nutriscore_grade", "nutrition_grades", "nova_group", "allergens_tags", "labels_tags",
            "ingredients_analysis_tags", "image_front_url",
        ).joinToString(",")

        fun ispravanNutriScore(s: String?): String? = s?.lowercase()?.takeIf { it.length == 1 && it[0] in 'a'..'e' }

        // ── Bezbedno citanje JSON polja (null kada polje ne postoji ili je drugog tipa) ──

        fun JsonObject.tekst(kljuc: String): String? =
            (this[kljuc] as? JsonPrimitive)?.takeIf { it.isString }?.content?.trim()?.takeIf { it.isNotEmpty() }

        fun JsonObject.broj(kljuc: String): Double? {
            val v = this[kljuc] as? JsonPrimitive ?: return null
            return v.doubleOrNull ?: v.content.replace(',', '.').toDoubleOrNull()
        }

        fun JsonObject.tekstovi(kljuc: String): List<String> =
            (this[kljuc] as? JsonArray)?.mapNotNull { e: JsonElement ->
                (e as? JsonPrimitive)?.takeIf { it.isString }?.content
            } ?: emptyList()

        fun JsonObject.prvi(vararg kljucevi: String): String? = kljucevi.firstNotNullOfOrNull { tekst(it) }
    }
}
