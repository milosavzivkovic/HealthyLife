package rs.healthylife.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Boje iz dizajna (iste kao u desktop verziji). */
object Boje {
    val Zelena = Color(0xFF15803D)
    val ZelenaTamna = Color(0xFF14532D)
    val ZelenaSvetla = Color(0xFFDCFCE7)
    val Pozadina = Color(0xFFF8FAFC)
    val Povrsina = Color(0xFFFFFFFF)
    val Siva = Color(0xFFF1F5F9)
    val Ivica = Color(0xFFE2E8F0)
    val IvicaPolja = Color(0xFFCBD5E1)
    val Tekst = Color(0xFF0F172A)
    val TekstLabele = Color(0xFF334155)
    val TekstSporedni = Color(0xFF64748B)
    val Crvena = Color(0xFFB91C1C)

    // Upozorenja i alergeni (narandzasto)
    val UpozorenjePozadina = Color(0xFFFFF7ED)
    val UpozorenjeIvica = Color(0xFFFED7AA)
    val UpozorenjeTekst = Color(0xFF7C2D12)
    val UpozorenjeIkona = Color(0xFFC2410C)
    val AlergenPozadina = Color(0xFFFFEDD5)
    val AlergenTekst = Color(0xFF9A3412)

    // Nivoi nutritivnih vrednosti (pozadina, tekst)
    val NiskoPozadina = Color(0xFFDCFCE7); val NiskoTekst = Color(0xFF14532D)
    val SrednjePozadina = Color(0xFFFEF3C7); val SrednjeTekst = Color(0xFF78350F)
    val VisokoPozadina = Color(0xFFFEE2E2); val VisokoTekst = Color(0xFF991B1B)

    /** Nutri-Score A..E: boja oznake i boja slova (citljiva na toj podlozi). */
    val NutriScore = listOf(
        Color(0xFF038141) to Color.White,
        Color(0xFF85BB2F) to Color(0xFF1A2E05),
        Color(0xFFFECB02) to Color(0xFF3F2D00),
        Color(0xFFEE8100) to Color(0xFF3B1F00),
        Color(0xFFC93A10) to Color.White,
    )

    /** NOVA 1..4: boja oznake. */
    val Nova = listOf(Color(0xFF15803D), Color(0xFFA16207), Color(0xFFC2410C), Color(0xFFB91C1C))
}

private val sema = lightColorScheme(
    primary = Boje.Zelena,
    onPrimary = Color.White,
    primaryContainer = Boje.ZelenaSvetla,
    onPrimaryContainer = Boje.ZelenaTamna,
    background = Boje.Pozadina,
    onBackground = Boje.Tekst,
    surface = Boje.Povrsina,
    onSurface = Boje.Tekst,
    surfaceVariant = Boje.Siva,
    onSurfaceVariant = Boje.TekstSporedni,
    outline = Boje.IvicaPolja,
    outlineVariant = Boje.Ivica,
    error = Boje.Crvena,
)

/** Tema aplikacije (za sada samo svetla, kao u dizajnu). */
@Composable
fun HealthyLifeTema(sadrzaj: @Composable () -> Unit) {
    MaterialTheme(colorScheme = sema, content = sadrzaj)
}
