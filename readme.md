# Introductie  
Dit programma haalt uit een Excel bestand informatie om markers op een kaart (Open StreetMap, OSM) te plaatsen.

# Installatie  
Het volgende Windows installatie kit is aanwezig:
- osmmp_vw.x.y.z_setup.exe

De kit heeft een JRE beschikbaar.

Er is ook een ZIP beschikbaar om de applicatie (zonder installatie) te draaien.

# Startmenu  
Als het programma voor het eerst wordt gestart (Windows executable of Java jar-file) wordt het volgende menu getoond:

![Main screen OSM Mapper](./StartScherm.jpg)

# Setting menu  
![Settings menu](./Instellingen.jpg)

In het Instellingen menu zijn de volgende opties aanwezig:
- _Taal instellen_ Instellen van de taal van de GUI (alleen NL is volledig ondersteund)
- _Loglevel_ Voor debugging kan een "_loglevel_" worden ingesteld, default is level _INFO_.
- _Uiterlijk_ van de GUI kan worden ingesteld._Look and Feel_ of the GUI can be adjusted.
- _Logfiles aanmaken_ in de gekozen directory wordt een HTML- en een textfile met de logging aangemaakt.

# Excel bestand   
In het Excel bestand bevat in de eerste rij de kolom kop namen.  
In de koppen wordt gezocht naar de volgende (deel)teksten (niet case-sensitive):
- "postcode"
- "huisnummer"
- "toevoeg"
- "straat"
- "plaats"
- "voornaam"
- "achternaam"
- "telefoon"
- "e-mail"
- "project"
- "kleur"

Indien niet aanwezig dan worden de volgende kolommen aangemaakt en van inhoud voorzien bij genereren Geo info:
- "long"
- "lat"
- "land"

# Marker foto's   
In een fotomap kunnen foto's per adres worden opgenomen.
De foto's dienen te worden opgeslagen in de volgende structuur:
~~~~~~~~~~~~~~~~~~~~~
Fotomap
|- 1234AA78
|      |- Foto1.jpg
|      |- ...
|
|- 5678BB91
|      |- Foto1.jpg
|      |- ...
~~~~~~~~~~~~~~~~~~~~~

# Mail voorbereiden  
Uit een dropdown wordt de tab gekozen waarvoor de mails moeten worden voorbereidt.  
De marker informatie wordt doorgegeven naar de mail verwerking.  

*Let op:*  
In de EML-tab wordt de opslagdirectory van de te genereren email bestanden vastgelegd.

Een scherm met de volgende tabs wordt getoond:  
- Configuratie
- Ontvabgers
- Bericht
- Bijlagen
- EML Opslag
- Log

En de knoppen:   
- Verzenden (nog niet functioneel)  
De berichten worden voor alle ontvangers gegeneerd en verzonden.
- Opslaan EML  
De berichten voor alle ontvangers worden gegeneerd met het berichttemplate en opgeslagen in de Eml-directory.
- Testen (nog niet functioneel)  
Testen van de verbinding met emailserver.
- Help  
- Afsluiten  
Afsluiten van de mailverwerking, "x" kan ook worden gebruikt.

**Configuratie**  
In deze tab worden de e-mail server instellingen opgeven. (Nog niet volledig functioneel.)  
De opgegeven gebruikersnaam wordt gebruikt om het "Van" adres van de mail in te vullen.

**Ontvangers**  
In deze tab worden de ontvangers gedefinieerd, de e-mail adressen waar de mail naar toe wordt gestuurd.  
Deze tab wordt gevuld vanuit de marker informatie van de gekozen kaart.

**Bericht**  
In deze tab wordt het template voor het te versturen bericht ingevooerd.  
Bij het opstellen van het onderwerp en de berichttekst kunnen zogenaamde tags worden gebruikt:  

- {naam}  
Naam van de ontvanger, geextraheerd uit het e-mail adres.
- {voornaam}  
Voornaam van de ontvanger.
- {achternaam}  
Achternaam van de ontvanger.
- {straat_nr}  
Adrses, straat en huisnummer van de ontvanger.
- {postcode}  
Postcode adres van ontvanger.
- {plaats}  
Woonplaats van ontvanger.
- {email}  
e-mail adres ontvanger.
- {datum}  
Tijdstip Nu.  
   
  
Voorbeeld van een berichttekst:
```bash
Beste {voornaam} {achternaam},

Hierbij ontvangt u uw warmtescan foto.

Adres: 
{straat_nr} 
{postcode} {plaats}

Met vriendelijke groet,
Het Buurkracht team Hoevelaken Duurzaam
```

**Bijlagen**  
In deze tab worden de bijlagen van een bericht beheerd:  
- Algemen Bijlagen  
Deze bijlagen worden bij ieder bericht bijgevoegd.
- Persoonlijke Bijlagen  
Deze bijlagen worden per persoon bijgevoegd.  
Vanuit een marker worden de foto's van een persoon bijgevoegd.

**EML opslag**  
In deze tab wordt de opslagdirectory van de te genereren email bestanden gedefieerd.  

**Log**   
Meldingen van de applicatie worden hier getoond.