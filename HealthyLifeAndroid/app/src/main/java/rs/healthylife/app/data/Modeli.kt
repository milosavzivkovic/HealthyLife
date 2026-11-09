package rs.healthylife.app.data

import kotlinx.serialization.Serializable

/**
 * Proizvod sa Open Food Facts. Sve vrednosti su na 100 g/ml.
 *
 * Podatke na Open Food Facts unose korisnici, pa mnoga polja mogu da nedostaju:
 * null znaci "nepoznato" (ne "nula").
 */
data class Proizvod(
    val barKod: String,
    val naziv: String,
    val brend: String?,
    val kolicina: String?,
    val sastojci: String?,
    val energijaKj: Double?,
    val energijaKcal: Double?,
    val masti: Double?,
    val zasiceneMasti: Double?,
    val ugljeniHidrati: Double?,
    val seceri: Double?,
    val vlakna: Double?,
    val proteini: Double?,
    val so: Double?,
    /** "a".."e" ili null kada Nutri-Score nije izracunat. */
    val nutriScore: String?,
    /** 1..4 (stepen industrijske obrade) ili null. */
    val nova: Int?,
    val alergeni: List<Alergen>,
    /** Alergeni sa Open Food Facts koji nisu medju 14 standardnih (retko). */
    val ostaliAlergeni: List<String>,
    /** true = vegansko (posno), false = sadrzi namirnice zivotinjskog porekla, null = nepoznato. */
    val posno: Boolean?,
    /** true = ima halal oznaku, null = nema podatka. */
    val halal: Boolean?,
    /** true = sadrzi palmino ulje, false = ne sadrzi, null = nepoznato. */
    val palminoUlje: Boolean?,
    val slikaUrl: String?,
)

/** Jedan rezultat pretrage po nazivu (skraceni podaci). */
data class RezultatPretrage(
    val barKod: String,
    val naziv: String,
    val brend: String?,
    val kolicina: String?,
    val nutriScore: String?,
    val slikaUrl: String?,
    val alergeni: List<Alergen>,
)

/** Proizvod u istoriji ili omiljenima - cuva se na telefonu. */
@Serializable
data class IstorijaStavka(
    val barKod: String,
    val naziv: String,
    val brend: String? = null,
    val kolicina: String? = null,
    val nutriScore: String? = null,
    val slikaUrl: String? = null,
    /** Kada je proizvod pregledan (milisekunde od 1970). */
    val vreme: Long = 0,
)

fun Proizvod.uStavku(vreme: Long = System.currentTimeMillis()) =
    IstorijaStavka(barKod, naziv, brend, kolicina, nutriScore, slikaUrl, vreme)

/** 14 alergena koje propisi zahtevaju da budu oznaceni na pakovanju. */
enum class Alergen(val oznaka: String, val naziv: String) {
    GLUTEN("en:gluten", "Gluten"),
    MLEKO("en:milk", "Mleko"),
    JAJA("en:eggs", "Jaja"),
    ORASASTI_PLODOVI("en:nuts", "Orašasti plodovi"),
    KIKIRIKI("en:peanuts", "Kikiriki"),
    SOJA("en:soybeans", "Soja"),
    RIBA("en:fish", "Riba"),
    RAKOVI("en:crustaceans", "Rakovi"),
    MEKUSCI("en:molluscs", "Mekušci"),
    CELER("en:celery", "Celer"),
    SENF("en:mustard", "Senf"),
    SUSAM("en:sesame-seeds", "Susam"),
    SULFITI("en:sulphur-dioxide-and-sulphites", "Sulfiti"),
    LUPINA("en:lupin", "Lupina");

    companion object {
        fun izOznake(oznaka: String): Alergen? = entries.firstOrNull { it.oznaka == oznaka }
    }
}

/** Licna podesavanja (cuvaju se samo na telefonu). */
data class Podesavanja(
    val alergeni: Set<Alergen> = emptySet(),
    val postim: Boolean = false,
    val izbegavamPalmino: Boolean = false,
    val samoSrbija: Boolean = true,
)
