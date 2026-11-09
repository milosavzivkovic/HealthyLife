package rs.healthylife.app.data

import kotlin.math.floor
import kotlin.math.roundToLong

/** Nivo hranljive materije na 100 g (kao "semafor" na pakovanjima u Evropi). */
enum class Nivo(val naziv: String) { NISKO("nisko"), SREDNJE("srednje"), VISOKO("visoko") }

/**
 * Granice na 100 g hrane po preporukama za oznacavanje hrane (UK FSA "traffic light"):
 * masti 3 / 17,5 g, zasicene masti 1,5 / 5 g, secer 5 / 22,5 g, so 0,3 / 1,5 g.
 */
object Granice {
    fun masti(g: Double?) = nivo(g, 3.0, 17.5)
    fun zasiceneMasti(g: Double?) = nivo(g, 1.5, 5.0)
    fun seceri(g: Double?) = nivo(g, 5.0, 22.5)
    fun so(g: Double?) = nivo(g, 0.3, 1.5)

    private fun nivo(g: Double?, nisko: Double, visoko: Double): Nivo? = when {
        g == null -> null
        g <= nisko -> Nivo.NISKO
        g > visoko -> Nivo.VISOKO
        else -> Nivo.SREDNJE
    }
}

/** Licno upozorenje na ekranu proizvoda, na osnovu podesavanja iz profila. */
data class Upozorenje(val naslov: String, val opis: String)

fun licnaUpozorenja(p: Proizvod, s: Podesavanja): List<Upozorenje> = buildList {
    val moji = p.alergeni.filter { it in s.alergeni }
    if (moji.isNotEmpty()) {
        add(Upozorenje(
            naslov = "Sadrži " + spoji(moji.map { it.naziv.lowercase() }),
            opis = if (moji.size == 1) "Označen je u tvom profilu kao alergen koji izbegavaš."
            else "Označeni su u tvom profilu kao alergeni koje izbegavaš.",
        ))
    }
    if (s.postim && p.posno == false) {
        add(Upozorenje("Nije posno", "Sadrži sastojke životinjskog porekla, a u profilu je uključeno da postiš."))
    }
    if (s.izbegavamPalmino && p.palminoUlje == true) {
        add(Upozorenje("Sadrži palmino ulje", "U profilu je označeno da izbegavaš palmino ulje."))
    }
}

/** ["gluten", "mleko", "soja"] -> "gluten, mleko i soja" */
private fun spoji(reci: List<String>): String =
    if (reci.size <= 1) reci.joinToString() else reci.dropLast(1).joinToString(", ") + " i " + reci.last()

/** 12.0 -> "12", 5.7 -> "5,7", 0.963 -> "0,96" */
fun broj(d: Double): String {
    val r = (d * 100).roundToLong() / 100.0
    return if (r == floor(r)) r.toLong().toString() else r.toString().replace('.', ',')
}
