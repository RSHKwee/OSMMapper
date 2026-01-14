Introductie

Het doel van deze (java) application is om markers op een (OSM-) kaart te tonen.
De marker informatie is afkomstig uit een Excel-sheet.

De eerste tab van een Excel Werkboek wordt ingelezen.
De eerste rij bevat kolom koppen.

De volgende kop (deel) teksten worden herkend:
"postcode" , "huisnummer", "toevoeg", "straat", "plaats", "voornaam", "achternaam", "telefoon", "e-mail", "project", "long", "lat", "land"

Het programma genereerd Geo information aan de hand van: "straat", "huisnummer", "toevoeg", "plaats" and optioneel "land" om de Latitude en longitude te bepalen.
De kolommen: "long", "lat" en "land" worden gecreëerd of overschreven.

Per adres kunnen foto's worden toegevoegd.
De foto's dienen te worden opgeslagen in de volgende structuur:

Fotomap
|- 1234AA78
|      |- Foto1.jpg
|      |- ...
|
|- 5678BB91
|      |- Foto1.jpg
|      |- ...

