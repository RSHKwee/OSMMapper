Introduction

The intent of this (java) application  is to place markers on a (OSM-)map.
The marker information is defined in an Excel-sheet.

The first sheet in an Excel workbook is read.
The first row contains collomn headings.

The following headings are recognized:
"postcode" , "huisnummer", "toevoeg", "straat", "plaats", "voornaam", "achternaam", "telefoon", "e-mail", "project", "long", "lat", "land"

The program can generate Geo information, it uses "straat", "huisnummer", "toevoeg", "plaats" and optional "land" for determine the Latitude and longitude.
It creates or overwrites the colomns: "long", "lat" and "land".

