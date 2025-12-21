# Scrum poročilo - Analitika produktivnosti (To-Do aplikacija)

## 1. Uporabniška zgodba
**Kot uporabnik želim videti analizo svojega produktivnega časa** (npr. povprečno trajanje nalog, odstotek dokončanih nalog v določenem časovnem obdobju), da lahko bolje upravljam svoje delo.

---

## 2. Razdelitev uporabniške zgodbe na naloge (Scrum backlog)

Uporabniško zgodbo smo razdelili na manjše, jasno opredeljene naloge, ki jih je mogoče izvesti v kratkem času:

- Analiza zahtev in določitev metrik produktivnosti  
- Implementacija analitike v backendu (izračuni na podlagi obstoječih opravil)  
- Dodajanje REST endpointa za pridobivanje analitike  
- Prikaz analitičnih podatkov v uporabniškem vmesniku  
- Pisanje unit testov (pozitivni in negativni scenariji)  
- Priprava dokumentacije (Scrum poročilo)

---

## 3. Planning Poker (ocenjevanje nalog)

Pri ocenjevanju zahtevnosti nalog smo uporabili metodo **planning poker** s **story points**.  
Pri ocenjevanju smo upoštevali:
- zahtevnost implementacije,
- količino sprememb v obstoječi kodi,
- potrebo po testiranju in dokumentaciji.

---

## 4. Razdelitev dela v ekipi

- **Živa Šumak:** implementacija backend analitike (izračuni, DTO, REST endpoint)  
- **Ana Cvetko:** frontend prikaz analitičnih podatkov  
- **Ismar Mujezinović:** unit testi in dokumentacija (README + Scrum poročilo)

---

## 5. Scrum tabla (GitHub Projects)

Za spremljanje napredka smo uporabili **GitHub Project** z agilno tablo, ki je vsebovala stolpce:
- **ToDo**
- **InProgress**
- **Done**

---

## 6. Povzetek implementacije

Implementirana je bila funkcionalnost analitike produktivnosti, ki na podlagi obstoječih opravil izračuna:
- skupno število opravil,
- število dokončanih opravil,
- število nedokončanih opravil,
- število zapadlih opravil,
- odstotek dokončanih opravil.

Analitika se izračuna dinamično in se ne shranjuje v bazo, temveč se vedno izračuna iz trenutnega stanja opravil.

---

## 7. Testiranje

Za backend del smo dodali **unit teste**, ki pokrivajo:
- **pozitiven scenarij** (pravilni izračuni ob obstoječih opravilih),
- **negativen scenarij** (prazen seznam opravil).

Testi uporabljajo **JUnit 5** in **Mockito**, ter preverjajo pravilnost izračunov in obnašanje sistema v robnih primerih.

## 8. Zaključek

Scrum pristop se je izkazal za učinkovit, saj je omogočil:
- jasno razdelitev dela,
- sprotno spremljanje napredka,
- boljšo organizacijo nalog.

V prihodnje bi analitiko lahko razširili z:
- analizo po časovnih obdobjih (npr. po mesecih),
- grafičnimi prikazi (grafi, diagrami),
- primerjavo produktivnosti med različnimi obdobji.

---