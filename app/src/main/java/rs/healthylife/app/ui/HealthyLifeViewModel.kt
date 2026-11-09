package rs.healthylife.app.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import rs.healthylife.app.data.Alergen
import rs.healthylife.app.data.IstorijaStavka
import rs.healthylife.app.data.LokalnoSkladiste
import rs.healthylife.app.data.OpenFoodFactsApi
import rs.healthylife.app.data.Podesavanja
import rs.healthylife.app.data.Proizvod
import rs.healthylife.app.data.RezultatPretrage
import rs.healthylife.app.data.uStavku
import java.util.concurrent.TimeUnit

/** Sta se trenutno prikazuje na ekranu proizvoda. */
sealed interface StanjeProizvoda {
    data object Prazno : StanjeProizvoda
    data object Ucitavanje : StanjeProizvoda
    data class Ucitan(val proizvod: Proizvod) : StanjeProizvoda
    data class NijePronadjen(val barKod: String) : StanjeProizvoda
    data class Greska(val poruka: String, val barKod: String) : StanjeProizvoda
}

/** Sta se trenutno prikazuje na ekranu rezultata pretrage. */
sealed interface StanjePretrage {
    data object Prazno : StanjePretrage
    data class Ucitavanje(val upit: String) : StanjePretrage
    data class Rezultati(val upit: String, val lista: List<RezultatPretrage>) : StanjePretrage
    data class Greska(val upit: String, val poruka: String) : StanjePretrage
}

/**
 * Stanje cele aplikacije. Ekrani samo prikazuju ovo stanje i pozivaju metode;
 * rad sa internetom i telefonom je ovde i u paketu data.
 */
class HealthyLifeViewModel(app: Application) : AndroidViewModel(app) {

    private val http = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
    private val api = OpenFoodFactsApi(http)
    private val skladiste = LokalnoSkladiste(app)

    val podesavanja: StateFlow<Podesavanja> =
        skladiste.podesavanja.stateIn(viewModelScope, SharingStarted.Eagerly, Podesavanja())
    val istorija: StateFlow<List<IstorijaStavka>> =
        skladiste.istorija.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val omiljeni: StateFlow<List<IstorijaStavka>> =
        skladiste.omiljeni.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var proizvod by mutableStateOf<StanjeProizvoda>(StanjeProizvoda.Prazno)
        private set
    var pretraga by mutableStateOf<StanjePretrage>(StanjePretrage.Prazno)
        private set

    private var posaoProizvoda: Job? = null
    private var posaoPretrage: Job? = null

    /** Ucitava proizvod po bar-kodu (posle skeniranja, iz pretrage ili istorije). */
    fun otvoriProizvod(barKod: String) {
        posaoProizvoda?.cancel()
        proizvod = StanjeProizvoda.Ucitavanje
        posaoProizvoda = viewModelScope.launch {
            proizvod = try {
                val p = api.nadjiProizvod(barKod)
                if (p == null) {
                    StanjeProizvoda.NijePronadjen(barKod)
                } else {
                    skladiste.dodajUIstoriju(p.uStavku())
                    StanjeProizvoda.Ucitan(p)
                }
            } catch (e: OpenFoodFactsApi.ApiGreska) {
                StanjeProizvoda.Greska(e.message ?: "Greška pri učitavanju.", barKod)
            }
        }
    }

    /** Pretraga po nazivu; filter "samo Srbija" se uzima iz podesavanja. */
    fun pretrazi(upit: String, samoSrbija: Boolean = podesavanja.value.samoSrbija) {
        val u = upit.trim()
        if (u.length < 2) return
        posaoPretrage?.cancel()
        pretraga = StanjePretrage.Ucitavanje(u)
        posaoPretrage = viewModelScope.launch {
            pretraga = try {
                StanjePretrage.Rezultati(u, api.pretrazi(u, samoSrbija))
            } catch (e: OpenFoodFactsApi.ApiGreska) {
                StanjePretrage.Greska(u, e.message ?: "Greška pri pretrazi.")
            }
        }
    }

    fun promeniAlergen(a: Alergen) = viewModelScope.launch { skladiste.promeniAlergen(a) }
    fun postaviPostim(da: Boolean) = viewModelScope.launch { skladiste.postaviPostim(da) }
    fun postaviPalmino(da: Boolean) = viewModelScope.launch { skladiste.postaviPalmino(da) }

    fun postaviSamoSrbija(da: Boolean, ponoviPretragu: Boolean = false) = viewModelScope.launch {
        skladiste.postaviSamoSrbija(da)
        val trenutna = pretraga
        if (ponoviPretragu && trenutna is StanjePretrage.Rezultati) pretrazi(trenutna.upit, da)
    }

    fun promeniOmiljeni(p: Proizvod) = viewModelScope.launch { skladiste.promeniOmiljeni(p.uStavku()) }
    fun obrisiIstoriju() = viewModelScope.launch { skladiste.obrisiIstoriju() }
}
