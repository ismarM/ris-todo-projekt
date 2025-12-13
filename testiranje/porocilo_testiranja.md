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

#### 1. Test: 

---

#### 2. Test: 

---

#### Analiza uspešnosti testiranja

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
