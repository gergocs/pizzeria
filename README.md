Név: Csizi Gergő Lajos

Pizzéria

Adatbázisok kötelező feladat

\2021. 10. 16.

A program egy pizzéria adatbázisával való kommunikációt valósít meg. A program segítségével van lehetőség rendelést leadni. Fiókot létrehozni. Korábban leadott rendelések lekérdezése. Új pizzák hozzáadása (Ha meg van hozzá a megfelelő jogosultság), korábban felvett pizzák módosítása, törlése. Felhasználó törlése.

|**Egyed-kapcsolat modell**|
| - |

|**Relációs adatbázisséma**|
| - |
Ügyfelek (Felhasználónév, Telefonszám, Szállítási cím, jelszó, Jogosultság);

Feltétek (feltetID, Név);

Pizzák (pizzaID, Név, Ár, *Feltétek*);

Rendelések (Felhasználónév, Mikor, Mit, Ár);

|**Normálforma**|
| - |
Az adatbázissémák megfelelnek a 2 illetve a 3 NF-nek

|**Megvalósítási környezet**|
| - |
Java nyelven lett megvalósítva JDBC driver segítségével. A GUI JavaFX Frameworkkel lett elkészítve.

|**A program szolgáltatásai**|
| - |
1. Felhasználó
   1. Létrehozása
   1. Törlése
   1. Információk módosítása
1. Rendelés
   1. Új rendelés létrehozása
   1. Korábbi rendelések listázása -Felhasználó által szabott feltételek alapján (Növekvő-csökkenő sorrend)
1. Pizzák
   1. Elérhető pizzák megtekintése
   1. Pizzákon lévő feltétek megtekintése

|**Három nemtriviális lekérdezés**|
| - |
1. Valami nélküli pizza listázása
   1. Felhasználótól függően kilistázza azokat a pizzákat, amiken nincs az adott dolog amennyibe nincs ilyen akkor hibát ad vissza
1. Top 5 legtöbbet rendelt pizza kilistázása
   1. Az első 5 pizza kilistázása, amiből a legtöbbet rendelték
1. Legtöbbet rendelő felhasználók kilistázása
   1. Akik a legtöbb(darab) pizzát rendelte

|**Felhasználói útmutató**|
| - |
1. Bejelentkezési felület:
   1. Felhasználónév és jelszó megadása után, ha mindkét adat érvényes akkor belép az alkalmazás és a kezdő lapra navigál.
   1. Érvénytelen felhasználónév vagy jelszó alapján hibát ír ki.
   1. Regisztrálás gomb megnyomása után a regisztrációs oldalra navigál
1. Regisztrációs felület:
   1. Felhasználónév: Olyan karaktersorozat, amely még nem szerepel az adatbázisban.
   1. Jelszó: Olyan karaktersorozat, amely az alábbi feltételeknek megfelel:
      1. Minimum 8 maximum 20 karakter hosszú
      1. Tartalmaz számot
      1. Tartalmaz kisbetűt
      1. Tartalmaz nagybetűt
      1. Tartalmaz speciáliskaraktert (!@#&()–{}:;',?\\*~$^+=<>)
   1. Jelszó megint: A jelszó karaktersorozat megismétlése
   1. Telefonszám: Nem kötelező mező kitöltése ajánlott
   1. Cím: megadása kötelező
   1. Sikeres regisztráció után a bejelentkezési felületre navigál.
1. Kezdő lap (Felhasználó):
   1. Középen az elérhető pizzák a rajtuk lévő feltétek és az áruk „+” megnyomása után a kosárba kerülnek
   1. Jobb oldalt szűrési lehetőségek láthatók.
      1. Top 5:
         1. Az első 5 legkelendőbb pizza kilistázása
      1. Szűrés:
         1. A megadott feltételek alapján kilistázza a pizzákat
      1. Szűrő törlése:
         1. A szűrőket kitörli
      1. Feltétek:
         1. 1 kattintás legyen rajta
         1. 2 kattintás NE legyen rajta
         1. 3 kattintás alaphelyzet
   1. Bal oldalt, illetve felül navigációs gombok találhatóak.
1. Felhasználó kezelő felület:
   1. Bal oldalt ha egy mezőbe beírunk adatot akkor az az adat fog frissülni az adatbázisban a már korábban említett feltételek alapján (Regisztrációs felület)
   1. Regisztráció törlése:
      1. Törli a regisztrációt és a rendeléseknél törölt felhasználó fog megjelenni,
   1. Jobb oldalt a korábbi rendelések jelennek meg idő szerint csökkenő/növekvő vagy ár szerint csökkenő sorrendben.
1. Kosár (Felhasználó):
   1. Ha a kosárba nincs semmi akkor hiba íródik ki a képernyőre.
   1. Ha van a kosárba elem akkor a piros „X”-el lehet törölni a kosárból és a zöld fizetés gombbal lehet a rendelést véglegesíteni
1. Kezdő lap (Admin):
   1. Középen az elérhető pizzák a rajtuk lévő feltétek és az áruk „X” megnyomása után véglegesen kitörlődnek.
   1. Jobb oldalt szűrési lehetőségek láthatók.
      1. Top 5:
         1. Az első 5 legkelendőbb pizza kilistázása
      1. Szűrés:
         1. A megadott feltételek alapján kilistázza a pizzákat
      1. Szűrő törlése:
         1. A szűrőket kitörli
      1. Feltétek:
         1. 1 kattintás legyen rajta
         1. 2 kattintás NE legyen rajta
         1. 3 kattintás alaphelyzet
   1. Bal oldalt, illetve felül navigációs gombok találhatóak.
1. Pizza készítő felület (Admin):
   1. Feltét létrehozása:
      1. A gomb megnyomása után az EpicToppingCreatorTM Segítségével lehet új feltétet létrehozni.
         1. Név (Ami még nincs az adatbázisban) megadása után egyből létrejön az adatbázisban az új feltét
   1. Pizza létrehozása:
      1. A megfelelő feltétek kiválasztása után megnyílik a PizzaCreator420TM
         1. Itt név (Ami még nincs az adatbázisban) és ár megadása után létrejön az új pizza az adatbázisban.
1. Statisztika lekérdező felület (Admin):
   1. 1. kör diagram:
      1. Mely pizzákból lett eladva és mennyi százalékos formában megjelenítve.
   1. 2. kör diagram:
      1. A legtöbb pizzát rendelő felhasználók megjelenítése.
PAGE   \\* MERGEFORMAT4
