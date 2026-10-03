# Lagermelder – Fachliche Beschreibung

Der Lagermelder ist die Online-Anmeldung und Organisationshilfe für das **Kreiszeltlager der Jugendfeuerwehr
Landkreis Karlsruhe**. Die Jugendfeuerwehren melden darüber ihre Teilnehmer an. Die Organisatoren erhalten daraus
Planungsunterlagen, vorausgefüllte Anmeldeunterlagen und während des Lagers einen Überblick, wer gerade da ist.

Technische Details: [`backend/ARCHITECTURE.md`](../backend/ARCHITECTURE.md), Arbeitsregeln: [`AGENTS.md`](../AGENTS.md).

## Rollen

| Rolle | Wer | Sieht / darf |
|---|---|---|
| `USER` | Jugendwart einer Feuerwehr | Nur die eigene Feuerwehr: **Teilnehmer** und **Anmeldeunterlagen**, eigenes Profil |
| `LK_KARLSRUHE` | Landkreis Karlsruhe | Zusätzlich alle Feuerwehren lesen, **Planung** und **Anwesend** |
| `SPECIALIZED_FIELD_DIRECTOR` | Fachgebietsleitung (Organisation) | Zusätzlich **Feuerwehren** anlegen/bearbeiten, **Einstellungen**, Anmeldeunterlagen aller Feuerwehren, Teilnehmer auch nach Anmeldeschluss bearbeiten |
| `ADMIN` | Administration | Alles, zusätzlich Mails versenden |

Die Rechte prüft das Backend in den Services (`AuthorityService`), die Navigation blendet die Seiten passend ein (`LmHeader.vue`).

## Ablauf eines Zeltlagers

1. **Vorbereitung**: Die Organisation legt in den Einstellungen Termine, Fristen und Veranstalterdaten fest, pflegt die
   T-Shirt-Größen und legt Feuerwehren mit Account an (einzeln in der App oder gesammelt mit `account-creator/`).
2. **Anmeldephase**: Jede Feuerwehr meldet ihre Teilnehmer an, gibt Zelte und Kontaktdaten an und trägt die Juleika-Daten
   ihrer Leiter ein. Die Organisation verschickt Erinnerungsmails.
3. **Nach Anmeldeschluss**: Ab dem eingestellten Datum stehen die vorausgefüllten **Anmeldeunterlagen** zum Download.
   Die Organisation erzeugt die **Planungsunterlagen** (Lagerausweise, T-Shirts, Essen, Zeltmarkierungen usw.).
4. **Anmeldung im Lager**: Die Feuerwehr bringt die vom Kommandanten unterschriebenen Anmeldeunterlagen mit.
5. **Während des Lagers**: Teilnehmer werden beim Betreten und Verlassen über den Code auf ihrem Lagerausweis erfasst. So ist jederzeit
   bekannt, wie viele Personen im Lager sind (auch je Evakuierungsgruppe).

## Feuerwehren (Departments)

- Eine Feuerwehr kann eine ganze Feuerwehr oder eine **Abteilung** sein. Sie kann eine **übergeordnete Feuerwehr**
  haben (`headDepartmentName`). Diese wird nur über die Konfiguration gesetzt (z. B. beim Anlegen mit `account-creator/`),
  nicht über die Oberfläche.
- Pro Feuerwehr wird festgelegt, **wen sie anmelden darf** (Features): Teilnehmer (Jugendgruppen), Kinder für den
  Kindergruppentag, Z-Kids, Helfer.
- Jede Feuerwehr gibt an:
  - **Zelte**: Anzahl je Zelttyp (SG 200, SG 20, SG 30, SG 40, SG 50)
  - **Kontaktnummer** für die Erreichbarkeit während des Lagers
  - Name und Telefonnummer des **Kommandanten**
- Die Organisation teilt Feuerwehren in **Evakuierungsgruppen** ein und vergibt **Zeltmarkierungen** für die angemeldeten Zelte.
- Seiten: `/feuerwehr` (Übersicht, anlegen), `/feuerwehr/:id` (bearbeiten, Rolle des Accounts, Mail mit Zugangsdaten).

## Teilnehmeranmeldung (`/teilnehmer`)

| Typ | Dabei | Erfasste Daten | Frist |
|---|---|---|---|
| Jugendliche | alle 5 Tage | Name, Geburtsdatum, T-Shirt, Essen, Kommentar | Registrierungsende Teilnehmer |
| Jugendleiter | alle 5 Tage | wie Jugendliche, zusätzlich Juleika-Nummer und -Ablaufdatum | Registrierungsende Teilnehmer |
| Kinder | 1 Tag (Kindergruppentag) | Name, Geburtsdatum, Essen, Kommentar (kein T-Shirt) | Registrierungsende Kindergruppen |
| Kindergruppenleiter | 1 Tag (Kindergruppentag) | wie Kinder, zusätzlich Juleika | Registrierungsende Kindergruppen |
| Z-Kids | alle 5 Tage | wie Jugendliche, zusätzlich „Teil von“ (Feuerwehr) | Registrierungsende Teilnehmer |
| Helfer | an ausgewählten Helfertagen | Name, T-Shirt, Essen, Helfertage, Kommentar (kein Geburtsdatum) | Registrierungsende Helfer |

- **Z-Kids** sind Kinder von Teilnehmern, die selbst zu keiner Jugendfeuerwehr gehören. Sie werden über „Teil von“ der
  Feuerwehr zugeordnet, mit der sie ins Lager kommen.
- **Helfer** kommen zusätzlich zu den Teilnehmern, deshalb werden weniger Daten erfasst.
- Vor- und Nachname müssen pro Feuerwehr eindeutig sein.
- Nach Ablauf der jeweiligen Frist können nur noch Fachgebietsleitung und Admin Teilnehmer ändern.
- Jeder Teilnehmer bekommt einen eindeutigen 8-stelligen **Code**. Er steht als Strichcode auf dem Lagerausweis und dient zum Ein- und Auschecken.

## Juleika

- Die **Juleika** (Jugendleitercard) der Jugendleiter muss abgegeben werden, weil es dafür **Zuschüsse** gibt.
- **Quote**: Pro angefangene 5 Jugendliche ist ein Jugendleiter mit gültiger Juleika nötig. Gültig heißt: Nummer
  angegeben und Ablaufdatum nach Beginn des Lagers. Die Anmeldeseite warnt, wenn die Quote nicht erfüllt ist.
- Feuerwehren, die die Quote nicht erfüllen, müssen **nachzahlen**. Dafür gibt es die Liste „Jugendfeuerwehren mit zu wenig Juleika“.
- Geplant: Anbindung an eine Juleika-API (siehe [Offene Punkte](#geplant-und-offen)).

## Anmeldeunterlagen (`/files`)

Die Anmeldeunterlagen sind **mit den angemeldeten Teilnehmern vorausgefüllt**. Die Feuerwehr bringt sie zur Anmeldung im
Lager mit, unterschrieben vom **Kommandanten**. Sie stehen erst ab dem eingestellten Download-Datum zur Verfügung, jeweils
für Teilnehmer und für Kindergruppen:

| Link | Inhalt | Zweck |
|---|---|---|
| Anmeldung | Kommunale Anmeldeliste mit Kommandant | Anmeldung, Unterschrift |
| Teilnehmerlisten | Landesjugendplan, Teilnehmer | Zuschuss |
| Betreuer | Landesjugendplan, Betreuer | Zuschuss |
| Teilnehmerliste | Liste für das Jugendamt (LRA Karlsruhe) | Zuschuss |

Für den Landesjugendplan verteilt der Lagermelder die Teilnehmer automatisch und möglichst günstig auf „Teilnehmer“ und
„pädagogische Betreuer“ (`AttendeeRoleHelper`). Die Seite zeigt diese Verteilung und eine Übersicht der Zuschüsse.
Die Fachgebietsleitung sieht die Unterlagen aller Feuerwehren.

**Normale Feuerwehren sehen nur „Teilnehmer“ und „Anmeldeunterlagen“.**

## Planungsunterlagen (`/planung`)

Für `LK_KARLSRUHE`, Fachgebietsleitung und Admin:

| Unterlage | Inhalt |
|---|---|
| Lagerausweise | Ein Ausweis pro Person mit Strichcode. Sortiert nach Feuerwehr oder nach Erstelldatum (für Nachmeldungen) |
| Essensübersicht | Wer bekommt welches Essen (Fleisch, vegetarisch, muslimisch, Sonderessen, nichts) |
| Anmerkungsübersicht | Alle Kommentare je Feuerwehr, z. B. Wünsche für Sonderessen |
| T-Shirt- und Armbandübersicht | Gesamtbestellmenge der T-Shirts je Größe und der Armbänder je Farbe |
| Feuerwehr T-Shirt- und Armbandverteilliste | Je Feuerwehr: wie viele T-Shirts und Armbänder, und wer welche Größe bzw. Farbe bekommt (die T-Shirts werden je Feuerwehr vorsortiert) |
| Kontaktliste | Alle Feuerwehren mit Jugendwart, Kontaktnummer, Kommandant und Teilnehmerzahlen |
| Zeltmarkierungen | Ein Schild je Zelt mit Zeltname, Feuerwehr und Evakuierungsgruppe |
| Jugendfeuerwehren mit zu wenig Juleika | Feuerwehren unter der Juleika-Quote, mit Soll/Ist und den betroffenen Leitern |
| Zelte und Schichten (CSV) | Je Feuerwehr: Teilnehmerzahl, Zelte je Typ, Zelte gesamt und die zugeteilten Lagerdienste (Schichten) |

- **Armbänder** zeigen das Alter zu Beginn des Lagers: **Rot** unter 16, **Gelb** 16–17, **Grün** ab 18.
- Die **Lagerdienste** (Anzahl in den Einstellungen) werden anteilig zur Teilnehmerzahl auf die Feuerwehren verteilt und
  fortlaufend nummeriert.
- Die Seite zeigt außerdem die Summe der angemeldeten Zelte und eine filterbare Teilnehmerliste.

## Einstellungen (`/einstellungen`)

Nur für Fachgebietsleitung und Admin:

- **Anmeldung**: Registrierungsende für Teilnehmer, Kindergruppen und Helfer, Startdatum für den Download der Anmeldeunterlagen
- **Veranstaltung**: Beginn, Ende, Name, Ort und Adresse
- **Organisator**: Name und Adresse (erscheinen in den Anmeldeunterlagen)
- **Zuschuss**: Betrag pro Betreuer
- **Schichten**: Anzahl der Lagerdienste
- **Events**: Orte bzw. Events zum Ein- und Auschecken anlegen, dazu ein PDF mit einem QR-Code je Event
  („Bitte beim Kommen und Gehen scannen“). Der QR-Code öffnet die Erfassungsseite des Events. Die globalen Events „Betreten“ und
  „Verlassen“ des Lagers gibt es fest, sie können nicht gelöscht werden.
- **T-Shirt-Größen**: anlegen und löschen. Beim Löschen wird eine Ersatzgröße für bereits angemeldete Teilnehmer gewählt.
- **Mails**: Erinnerungsmail („noch X Tage offen“) und Mail zum Registrierungsende, jeweils an alle Feuerwehren, an
  Feuerwehren mit Teilnehmern oder an Feuerwehren ohne Teilnehmer. Versand nur durch Admin.

Weitere Mails versendet das System automatisch: Zugangsdaten für neue Accounts („Onlineanmeldung eröffnet“) und Passwort vergessen.

## Ein- und Auschecken und Anwesenheit

- **Events** (`/events/:eventCode`): Den Strichcode des Lagerausweises mit einem Scanner in das (automatisch fokussierte)
  Eingabefeld einlesen oder den Code von Hand eintippen. Ein Scan beim Event
  „Betreten“ bzw. „Verlassen“ setzt den Status des Teilnehmers auf anwesend bzw. abgereist. Bei anderen Events wird
  nur die Teilnahme erfasst.
- **Sammel-Check-in** (`/feuerwehr-betreten/:departmentId`): ganze Gruppen einer Feuerwehr auf einmal betreten oder
  verlassen lassen, mit Übersicht der Zuschüsse.
- **Anwesend** (`/anwesend`, Druckansicht `/anwesend-print`): Wie viele Personen gerade im Lager sind, gesamt, je Feuerwehr
  und nach Evakuierungsgruppen. Feuerwehren können pausiert werden, sie zählen dann nicht als anwesend.
- **Öffentliche Zählung**: `GET /api/public/present-by-executed-role` liefert ohne Login die Zahl der Anwesenden je Rolle
  (10 Minuten gecacht).

## Account

- `/account`: eigene Feuerwehr ansehen, Passwort ändern (mindestens 8 Zeichen)
- `/passwort-vergessen` und `/passwort-zuruecksetzen/:token`: Passwort über einen Mail-Link zurücksetzen

## Begleit-Tools

- `account-creator/` (Go): legt Feuerwehren mit Account gesammelt über die API an, inklusive Features und übergeordneter Feuerwehr.
- `page-to-pdf/` (Node, Playwright): meldet sich in der App an und speichert eine Seite als PDF, z. B. die Druckansicht der Anwesenden.

## Glossar

| Begriff | Bedeutung |
|---|---|
| Juleika | Jugendleitercard, Nachweis der Qualifikation als Jugendleiter. Voraussetzung für Zuschüsse |
| Landesjugendplan | Förderprogramm des Landes Baden-Württemberg. Zuschuss für Teilnehmer und pädagogische Betreuer |
| Kindergruppentag | Ein Tag des Lagers für die Kindergruppen der Feuerwehren |
| Z-Kids | Kinder von Teilnehmern, die selbst keiner Jugendfeuerwehr angehören |
| Helfer | Zusätzliche Unterstützer, nur an ausgewählten Helfertagen |
| SG 20 … SG 200 | Zelttypen (Größen siehe Anmeldeseite) |
| Evakuierungsgruppe | Gruppe von Feuerwehren bzw. Zelten, die bei einer Evakuierung gemeinsam gezählt wird |
| Zeltmarkierung | Schild bzw. Kennzeichnung eines Zelts einer Feuerwehr |
| Kommandant | Leiter der Feuerwehr, unterschreibt die Anmeldeunterlagen |
| Übergeordnete Feuerwehr | Gesamtwehr einer Abteilung (`headDepartmentName`) |
| Lagerausweis | Ausweis mit Code für jede Person im Lager |
| Nachmeldung | Anmeldung nach dem Druck der ersten Lagerausweise. Deshalb gibt es die Sortierung nach Erstelldatum |
| Lagerdienst / Schicht | Dienst, den Feuerwehren im Lager übernehmen, verteilt nach Teilnehmerzahl |

## Geplant und offen

- **Juleika-API**: Die Juleika-Daten sollen später über eine API geprüft bzw. übernommen werden.
- **Pausieren**: Was das Pausieren einer Feuerwehr fachlich bedeutet (z. B. vorübergehend außerhalb des Lagers), ist nicht
  dokumentiert. Technisch zählen pausierte Feuerwehren nicht als anwesend.
- Bekannte technische Probleme stehen in [`backend/ARCHITECTURE.md`](../backend/ARCHITECTURE.md#known-technical-debt).
