# Poročilo o testiranju

V okviru naloge smo izvedli enotno (unit) testiranje zalednega dela aplikacije To-Do.
Cilj testiranja je bil preveriti pravilnost delovanja ključnih funkcionalnosti aplikacije,
predvsem CRUD operacij, filtriranje in nove funkcionalnosti e-poštnih opomnikov.

Testi so bili napisani z uporabo JUnit 5 in Mockito ter se izvajajo kot samostojni unit testi
brez dejanske povezave z bazo ali SMTP strežnikom.

---

## Razdelitev dela v ekipi
| Član ekipe | Odgovornost |
|------------|------------|
| **Ismar Mujezinović** | Posodabljanje opravila (UPDATE) + pošiljanje e-poštnih opomnikov |
| **Živa Šumak** | Filtriranje opravil + Dodajanje (CREATE) |
| **Ana Cvetko** | Branje (READ) + Brisanje opravil (DELETE) |

## Opis ustvarjenih testov

### Ismar - Unit testi (UPDATE + REMINDER)

#### 1. Test: Posodabljanje opravila (UPDATE)

**Razred**: `TaskServiceUpdateTest`

- **Pozitiven scenarij**
    **Metoda**: `update_shouldUpdateFieldsAndSave` preveri ali `update()` pravilno:
    - prebere obstoječe opravilo iz repozitorija,
    - posodobi vsa polja (naslov, opis, datum, težavnost, e-pošta, opomnik),
    - shrani posodabljeno opravilo v bazo.
  
    Test zagotavlja, da UPDATE operacija deluje pravilno in da se noben podatek ne izgubi ali napačno prepiše.

- **Negativen scenarij**
    **Metoda**: `update_whenTaskNotFound_shouldThrowAndNotSave` preveri obnašanje sistema, kadar opravilo z določenim ID-jem ne obstaja. V tem primeru mora metoda:
    - vreči izjemo,
    - **ne** klicati metode `save()` na repozitoriju.

    S tem zagotovim, da sistem ne spreminja ali ustvarja neobstoječih zapisov.

---

#### 2. Test: Pošiljanje e-poštnih opomnikov (REMINDER)

**Razred:** `TaskServiceReminderTest`

Drugi sklop testov preverja novo funkcionalnost e-poštnih opomnikov, ki se izvajajo preko schedulerja.

- **Pozitiven scenarij**  
  **Metoda:** `sendTomorrowReminders_shouldSendEmailAndMarkReminderSent` preveri, da sistem:
  - najde opravilo z rokom jutri,
  - preveri, da je opomnik vklopljen in še ni bil poslan,
  - pošlje e-poštno sporočilo,
  - nastavi zastavico `reminderSent = true`,
  - shrani spremembe v repozitorij.

  S tem zagotovim, da se opomnik pošlje pravilno in samo enkrat.

- **Negativen scenarij**  
  **MEtoda:** `sendTomorrowReminders_whenEmailMissing_shouldSkipSendingAndNotSave` preveri, da se v primeru manjkajočega ali praznega e-poštnega naslova:
  - e-poštno sporočilo ne pošlje,
  - opravilo ne shrani,
  - stanje opomnika ostane nespremenjeno.

  Ta test preprečuje pošiljanje neveljavnih ali napačnih e-poštnih sporočil.

---

#### Analiza uspešnosti testiranja

Med pisanjem testov sem naletel na težavo `NullPointerException`, ki je nastala zaradi nepravilne inicializacije mock objetkov. Težavo sem uspešno odpravil z uporabo anotacije `@ExtendWith(MockitoExtension.class)`.

Vsi testi uspešno izvajajo, kar potrjujejo:
- pravilno delovanje UPDATE operacije,
- pravilno delovanje e-poštnih opomnikov,
- ustrezno obravnano napak.

---

### Živa - Unit testi (FILTER + CREATE)

#### 1. Test: Dodajanje novega opravila (CREATE)

**Razred:** `TaskServiceCreateTest`

- **Pozitiven scenarij**  
  Testna metoda `create_shouldSaveTask` preverja, ali metoda `create()` v razredu `TaskService` pravilno shrani novo opravilo v repozitorij in vrne shranjen objekt.

  Test potrdi, da:
  - se metoda `save()` na repozitoriju pokliče natanko enkrat,
  - metoda `create()` vrne shranjen objekt opravila,
  - se osnovni podatki opravila (npr. naslov) pravilno ohranijo.

- **Negativen scenarij**
  Negativni scenarij za to funkcionalnost v servisnem sloju ni smiseln, saj metoda `create()` ne vsebuje dodatne validacijske ali pogojne logike, temveč zgolj posreduje klic repozitoriju.

  Test je pomemben, ker potrjuje osnovno funkcionalnost dodajanja opravil, ki predstavlja temelj celotne aplikacije.

---

#### 2. Test: Filtriranje opravil po datumu (FILTER)

**Razred:** `TaskServiceFilterTest`

- **Pozitiven scenarij**  
  Test `findAll_withDate_shouldFilterByDueDate` preverja pravilno delovanje filtriranja opravil po datumu roka.

  Test zagotavlja, da:
  - se ob podanem datumu uporabi metoda `findByDueDate(dueDate)`,
  - sistem vrne samo opravila z izbranim datumom,
  - filtriranje deluje pravilno glede na vhodne parametre.

- **Negativen scenarij**  
  Test `findAll_withoutDate_shouldReturnAllTasks` preverja obnašanje sistema, kadar datum ni podan (`null`).

  V tem primeru test preveri, da:
  - se uporabi metoda `findAll()`,
  - aplikacija vrne celoten seznam opravil,
  - sistem deluje pravilno tudi brez aktivnega filtra.

  S tem je zagotovljeno pravilno delovanje aplikacije v pogostih realnih primerih uporabe.

---

#### Analiza uspešnosti testiranja

Vsi testi so bili uspešno izvedeni z ukazom `./mvnw test`. Med pisanjem in izvajanjem testov nisem zaznala napak v obstoječi implementaciji.

Unit testi potrjujejo pravilno delovanje funkcionalnosti dodajanja opravil in filtriranja po datumu ter omogočajo varnejše nadaljnje spremembe kode brez tveganja za nenamerne napake.

---

### Ana - Unit testi (READ + DELETE)

#### 1. Test: 

---

#### 2. Test:

---

#### Analiza uspešnosti testiranja

---

## Zaključek

Z izvedenimi unit testi smo zagotovili:
- zanesljivo delovanje ključnih funkcionalnosti,
- varno obnašanje v primeru napak,
- dobro osnovo za nadaljnji razvoj in razširitve aplikacije.
