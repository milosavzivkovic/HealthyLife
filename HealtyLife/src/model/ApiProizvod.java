package model;

import java.util.List;

/**
 * Proizvod preuzet sa Open Food Facts.
 *
 * Za razliku od proizvoda iz nase baze, ovde podaci mogu da NEDOSTAJU
 * (Open Food Facts popunjavaju korisnici sirom sveta), pa su nutritivne
 * vrednosti Double (null = nepoznato), a posno/halal Boolean (null = nepoznato).
 *
 * Sve vrednosti su na 100 g/ml.
 */
public record ApiProizvod(
		String barKod,
		String naziv,
		String brend,
		String kolicina,
		String sastojci,
		Double energijaKj,
		Double energijaKcal,
		Double masti,
		Double zasiceneMasti,
		Double ugljeniHidrati,
		Double seceri,
		Double vlakna,
		Double proteini,
		Double so,
		String nutriScore,     // "a".."e" ili null
		Integer nova,          // 1..4 ili null (stepen industrijske obrade)
		List<String> alergeni, // vec prevedeni nazivi, npr. "Mleko", "Gluten"
		Boolean posno,         // true = vegansko, false = sadrzi namirnice zivotinjskog porekla, null = nepoznato
		Boolean halal,         // true = ima halal oznaku, null = nema podatka
		String slikaUrl) {
}
