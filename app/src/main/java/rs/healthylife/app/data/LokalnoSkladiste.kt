package rs.healthylife.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "healthylife")

/**
 * Sve sto aplikacija pamti na telefonu: moji alergeni, podesavanja, istorija i omiljeni.
 * Koristi DataStore (zamena za stari SharedPreferences). Nalog i server nisu potrebni.
 */
class LokalnoSkladiste(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private val listaStavki = ListSerializer(IstorijaStavka.serializer())

    private object Kljuc {
        val ALERGENI = stringSetPreferencesKey("alergeni")
        val POSTIM = booleanPreferencesKey("postim")
        val PALMINO = booleanPreferencesKey("izbegavam_palmino")
        val SAMO_SRBIJA = booleanPreferencesKey("samo_srbija")
        val ISTORIJA = stringPreferencesKey("istorija")
        val OMILJENI = stringPreferencesKey("omiljeni")
    }

    val podesavanja: Flow<Podesavanja> = context.dataStore.data.map { p ->
        Podesavanja(
            alergeni = p[Kljuc.ALERGENI].orEmpty()
                .mapNotNull { ime -> Alergen.entries.firstOrNull { it.name == ime } }.toSet(),
            postim = p[Kljuc.POSTIM] ?: false,
            izbegavamPalmino = p[Kljuc.PALMINO] ?: false,
            samoSrbija = p[Kljuc.SAMO_SRBIJA] ?: true,
        )
    }

    val istorija: Flow<List<IstorijaStavka>> = context.dataStore.data.map { procitaj(it[Kljuc.ISTORIJA]) }
    val omiljeni: Flow<List<IstorijaStavka>> = context.dataStore.data.map { procitaj(it[Kljuc.OMILJENI]) }

    suspend fun promeniAlergen(alergen: Alergen) {
        context.dataStore.edit { p ->
            val skup = p[Kljuc.ALERGENI].orEmpty().toMutableSet()
            if (!skup.add(alergen.name)) skup.remove(alergen.name)
            p[Kljuc.ALERGENI] = skup
        }
    }

    suspend fun postaviPostim(da: Boolean) = context.dataStore.edit { it[Kljuc.POSTIM] = da }
    suspend fun postaviPalmino(da: Boolean) = context.dataStore.edit { it[Kljuc.PALMINO] = da }
    suspend fun postaviSamoSrbija(da: Boolean) = context.dataStore.edit { it[Kljuc.SAMO_SRBIJA] = da }

    /** Dodaje proizvod na vrh istorije (bez duplikata, najvise 100 stavki). */
    suspend fun dodajUIstoriju(stavka: IstorijaStavka) {
        context.dataStore.edit { p ->
            val stara = procitaj(p[Kljuc.ISTORIJA]).filterNot { it.barKod == stavka.barKod }
            p[Kljuc.ISTORIJA] = json.encodeToString(listaStavki, (listOf(stavka) + stara).take(100))
        }
    }

    suspend fun obrisiIstoriju() = context.dataStore.edit { it.remove(Kljuc.ISTORIJA) }

    /** Dodaje proizvod u omiljene, ili ga uklanja ako je vec tu. */
    suspend fun promeniOmiljeni(stavka: IstorijaStavka) {
        context.dataStore.edit { p ->
            val lista = procitaj(p[Kljuc.OMILJENI])
            val nova = if (lista.any { it.barKod == stavka.barKod }) lista.filterNot { it.barKod == stavka.barKod }
            else listOf(stavka) + lista
            p[Kljuc.OMILJENI] = json.encodeToString(listaStavki, nova)
        }
    }

    private fun procitaj(tekst: String?): List<IstorijaStavka> =
        if (tekst == null) emptyList()
        else runCatching { json.decodeFromString(listaStavki, tekst) }.getOrDefault(emptyList())
}
