package rs.healthylife.app.ui.ekrani

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.healthylife.app.R
import rs.healthylife.app.data.IstorijaStavka
import rs.healthylife.app.data.Podesavanja
import rs.healthylife.app.ui.komponente.Ikona
import rs.healthylife.app.ui.komponente.PoljePretrage
import rs.healthylife.app.ui.komponente.ProizvodRed
import rs.healthylife.app.ui.komponente.opisProizvoda
import rs.healthylife.app.ui.theme.Boje

/** Ekran 1: skeniranje, pretraga po nazivu i nedavno pregledani proizvodi. */
@Composable
fun PocetnaEkran(
    podesavanja: Podesavanja,
    istorija: List<IstorijaStavka>,
    onSkeniraj: () -> Unit,
    onTrazi: (String) -> Unit,
    onSamoSrbija: (Boolean) -> Unit,
    onProizvod: (String) -> Unit,
    onIstorija: () -> Unit,
    onProfil: () -> Unit,
) {
    var upit by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Healthy Life", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Boje.Zelena)
                Text("Šta danas proveravaš?", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 32.sp)
            }
            Surface(
                onClick = onProfil,
                shape = CircleShape,
                color = Boje.ZelenaSvetla,
                modifier = Modifier.size(44.dp).semantics { contentDescription = "Moj profil i alergeni" },
            ) {
                Box(contentAlignment = Alignment.Center) { Ikona(R.drawable.ic_profil, Boje.ZelenaTamna, velicina = 22.dp) }
            }
        }

        // Veliko dugme za skeniranje
        Surface(onClick = onSkeniraj, shape = RoundedCornerShape(22.dp), color = Boje.Zelena) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Skeniraj bar-kod", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Usmeri kameru na bar-kod sa pakovanja", fontSize = 14.sp, lineHeight = 20.sp, color = Boje.ZelenaSvetla)
                }
                Box(
                    Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) { Ikona(R.drawable.ic_skeniraj, Color.White, velicina = 34.dp) }
            }
        }

        // Pretraga po nazivu
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Ili pretraži po nazivu", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Boje.TekstLabele)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                PoljePretrage(upit, { upit = it }, { onTrazi(upit) }, Modifier.weight(1f))
                Button(
                    onClick = { onTrazi(upit) },
                    enabled = upit.trim().length >= 2,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Boje.Tekst),
                    modifier = Modifier.height(56.dp),
                ) { Text("Traži", fontWeight = FontWeight.SemiBold) }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .toggleable(value = podesavanja.samoSrbija, role = Role.Checkbox, onValueChange = onSamoSrbija)
                    .padding(end = 8.dp),
            ) {
                Checkbox(
                    checked = podesavanja.samoSrbija,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(checkedColor = Boje.Zelena),
                    modifier = Modifier.padding(12.dp),
                )
                Text("Samo proizvodi iz Srbije", fontSize = 14.sp, color = Boje.TekstLabele)
            }
        }

        // Nedavno pregledano
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Nedavno pregledano", fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (istorija.isNotEmpty()) {
                    TextButton(onClick = onIstorija) { Text("Sve", color = Boje.Zelena, fontWeight = FontWeight.SemiBold) }
                }
            }
            if (istorija.isEmpty()) {
                Text(
                    "Proizvodi koje skeniraš ili otvoriš pojaviće se ovde.",
                    fontSize = 14.sp, color = Boje.TekstSporedni,
                )
            }
            istorija.take(3).forEach { s ->
                ProizvodRed(s.naziv, opisProizvoda(s.brend, s.kolicina), s.nutriScore, s.slikaUrl) { onProizvod(s.barKod) }
            }
        }
        Box(Modifier.height(8.dp))
    }
}

/** Da li je uneti tekst bar-kod (8-14 cifara) - tada odmah otvaramo proizvod umesto pretrage. */
fun jeBarKod(tekst: String): Boolean = tekst.filterNot { it == ' ' || it == '-' }.let { it.length in 8..14 && it.all(Char::isDigit) }
