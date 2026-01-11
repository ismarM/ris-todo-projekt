# Poročilo o Kanban razvoju – sinhronizacija nalog s koledarjem

## 1. Uporabniška zgodba
**Kot uporabnik želim sinhronizirati svoje naloge z zunanjo aplikacijo koledarja, da vidim svoje obveznosti v vseh orodjih.**
Med razvojem je bila zahteva razširjena tako, da se za vsako nalogo prikazuje tudi status sinhronizacije (v teku, uspešno, napaka).

---

## 2. Seznam nalog

- analiza zahteve in obsega sinhronizacije

- definicija CalendarEventDTO

- backend logika za sinhronizacijo naloge

- shranjevanje statusa sinhronizacije v nalogo

- REST endpoint za sinhronizacijo

- prikaz statusa sinhronizacije v UI (badge)

- unit testi za sinhronizacijo (pozitiven + negativni scenariji)

---

## 3. Razdelitev dela
- backend: sinhronizacija, statusi, unit testi
- frontend: prikaz statusov in gumb za sinhronizacijo
- product owner: usklajevanje zahtev in komunikacija z asistentoma

---

## 4. Potek po Kanbanu

Razvoj je potekal po Kanban metodi z omejitvijo WIP. Naloge so se sproti premikale med stolpci To Do, In Progress, In Review in Done. Med razvojem smo prilagodili funkcionalnost na podlagi povratnih informacij (dodani statusi sinhronizacije).