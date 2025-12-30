# Introductie

Dit programma haalt uit een Excel bestand informatie om markers op een kaart (Open StreetMap, OSM) te plaatsen.

# Installation
Het volgende Windows installatie kit is aanwezig:
- osmmp_vw.x.y.z_setup.exe

De kit heeft een JRE beschikbaar.

# Opening menu
When running the application (Windows excutable or Java jar-file) the following menu is shown:

![Main screen ing2ofx](./osmmappingMain.PNG)



# Setting menu

![Settings menu](./osmmapperSettings.PNG)

In the "settings" menu the following options are available:
- For debugging a _loglevel_ can be defined, default is level _INFO_.
- _Look and Feel_ of the GUI can be adjusted.
- _Create logfiles_ in the choosen directory a HTML- and a textfile with logging is created.



# Excel bestand

In het Excel bestand bevat in de eerste rij de kolom kop namen.



      Seq; AccountNr        ; Prefix
       1 ; NLIyyINGBxxxxxxx ; Home
       2 ; NLzzSNSBnnnnnnn  ; Business
       ......



"postcode"
"huisnummer"
"toevoeg"
"straat"
"plaats"
"voornaam"
"achternaam"
"telefoon"
"e-mail"
"project"

"long"
"lat"
"land"
