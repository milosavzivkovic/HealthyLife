package rs.healthylife.app.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import rs.healthylife.app.ui.ekrani.IstorijaEkran
import rs.healthylife.app.ui.ekrani.PocetnaEkran
import rs.healthylife.app.ui.ekrani.ProfilEkran
import rs.healthylife.app.ui.ekrani.ProizvodEkran
import rs.healthylife.app.ui.ekrani.RezultatiEkran
import rs.healthylife.app.ui.ekrani.jeBarKod
import rs.healthylife.app.ui.komponente.DonjiMeni

/** Nazivi ekrana za navigaciju. */
private object Ekran {
    const val POCETNA = "pocetna"
    const val REZULTATI = "rezultati"
    const val PROIZVOD = "proizvod"
    const val ISTORIJA = "istorija"
    const val PROFIL = "profil"
}

/** Cela aplikacija: ekrani, prelazi izmedju njih i donji meni. */
@Composable
fun HealthyLifeApp(vm: HealthyLifeViewModel = viewModel()) {
    val nav = rememberNavController()
    val context = LocalContext.current
    val podesavanja by vm.podesavanja.collectAsStateWithLifecycle()
    val istorija by vm.istorija.collectAsStateWithLifecycle()
    val omiljeni by vm.omiljeni.collectAsStateWithLifecycle()
    val trenutni = nav.currentBackStackEntryAsState().value?.destination?.route

    val otvoriProizvod: (String) -> Unit = { barKod ->
        vm.otvoriProizvod(barKod)
        nav.navigate(Ekran.PROIZVOD) { launchSingleTop = true }
    }
    val skeniraj = { skenirajBarKod(context, otvoriProizvod) }
    val trazi: (String) -> Unit = { upit ->
        // Ako je uneta samo cifra po cifra (bar-kod), odmah otvaramo proizvod
        if (jeBarKod(upit)) {
            otvoriProizvod(upit.filter(Char::isDigit))
        } else if (upit.trim().length >= 2) {
            vm.pretrazi(upit)
            nav.navigate(Ekran.REZULTATI) { launchSingleTop = true }
        }
    }

    Scaffold(
        bottomBar = {
            if (trenutni == Ekran.POCETNA || trenutni == Ekran.ISTORIJA) {
                DonjiMeni(
                    naPocetnoj = trenutni == Ekran.POCETNA,
                    onPocetna = { nav.idiNaGlavni(Ekran.POCETNA) },
                    onSkeniraj = skeniraj,
                    onIstorija = { nav.idiNaGlavni(Ekran.ISTORIJA) },
                )
            }
        },
    ) { razmak ->
        // Ekrani sami vode racuna o statusnoj traci; ovde dodajemo samo mesto za donji meni.
        Box(Modifier.fillMaxSize().padding(bottom = razmak.calculateBottomPadding())) {
            NavHost(nav, startDestination = Ekran.POCETNA) {
                composable(Ekran.POCETNA) {
                    PocetnaEkran(
                        podesavanja = podesavanja,
                        istorija = istorija,
                        onSkeniraj = skeniraj,
                        onTrazi = trazi,
                        onSamoSrbija = { vm.postaviSamoSrbija(it) },
                        onProizvod = otvoriProizvod,
                        onIstorija = { nav.idiNaGlavni(Ekran.ISTORIJA) },
                        onProfil = { nav.navigate(Ekran.PROFIL) },
                    )
                }
                composable(Ekran.REZULTATI) {
                    RezultatiEkran(
                        stanje = vm.pretraga,
                        podesavanja = podesavanja,
                        onNazad = { nav.popBackStack() },
                        onTrazi = trazi,
                        onSamoSrbija = { vm.postaviSamoSrbija(it, ponoviPretragu = true) },
                        onProizvod = otvoriProizvod,
                    )
                }
                composable(Ekran.PROIZVOD) {
                    ProizvodEkran(
                        stanje = vm.proizvod,
                        podesavanja = podesavanja,
                        omiljeni = omiljeni,
                        onNazad = { nav.popBackStack() },
                        onPonovo = { vm.otvoriProizvod(it) },
                        onOmiljeni = { vm.promeniOmiljeni(it) },
                        onProfil = { nav.navigate(Ekran.PROFIL) },
                    )
                }
                composable(Ekran.ISTORIJA) {
                    IstorijaEkran(
                        istorija = istorija,
                        omiljeni = omiljeni,
                        onProizvod = otvoriProizvod,
                        onObrisiIstoriju = { vm.obrisiIstoriju() },
                    )
                }
                composable(Ekran.PROFIL) {
                    ProfilEkran(
                        podesavanja = podesavanja,
                        onNazad = { nav.popBackStack() },
                        onAlergen = { vm.promeniAlergen(it) },
                        onPostim = { vm.postaviPostim(it) },
                        onPalmino = { vm.postaviPalmino(it) },
                        onSamoSrbija = { vm.postaviSamoSrbija(it) },
                    )
                }
            }
        }
    }
}

/** Prelaz izmedju Pocetne i Istorije iz donjeg menija (bez gomilanja ekrana na "nazad"). */
private fun NavHostController.idiNaGlavni(ruta: String) = navigate(ruta) {
    popUpTo(Ekran.POCETNA) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

/**
 * Googlov gotov skener bar-koda (ML Kit). Otvara se preko celog ekrana, radi i bez interneta
 * i ne trazi od nase aplikacije dozvolu za kameru - za to se brinu Google Play usluge.
 */
private fun skenirajBarKod(context: Context, onBarKod: (String) -> Unit) {
    val opcije = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
        .enableAutoZoom()
        .build()
    GmsBarcodeScanning.getClient(context, opcije)
        .startScan()
        .addOnSuccessListener { barKod -> barKod.rawValue?.let(onBarKod) }
        .addOnFailureListener { e ->
            // Korisnik je zatvorio skener - nije greska
            if (e is MlKitException && e.errorCode == MlKitException.CODE_SCANNER_CANCELLED) return@addOnFailureListener
            val poruka = if (e is MlKitException && e.errorCode == MlKitException.UNAVAILABLE) {
                "Skener se još preuzima uz Google Play usluge. Pokušaj ponovo za nekoliko trenutaka."
            } else {
                "Skeniranje nije uspelo: ${e.localizedMessage}"
            }
            Toast.makeText(context, poruka, Toast.LENGTH_LONG).show()
        }
}
