Kako pokrenuti projekat
Da biste uspešno pokrenuli i testirali projekat, potrebno je da podesite i lokalno okruženje i Firebase.

Preduslovi
Android Studio (preporučeno najnovija verzija)

Git

Google nalog za Firebase

Koraci za podešavanje
1. Kloniranje Repozitorijuma
Bash

git clone [URL_VAŠEG_REPOZITORIJUMA]
cd [NAZIV_FOLDERA]
2. Podešavanje Firebase Projekta
Ovaj korak je OBAVEZAN jer se aplikacija oslanja na Firebase za rad.

Kreiranje projekta: Idite na Firebase Konzolu i kreirajte novi projekat.

Dodavanje Android aplikacije: Unutar projekta, dodajte novu Android aplikaciju.

Package Name: com.example.rpggame (ili koje god je ime paketa u vašem projektu).

SHA-1 ključ: Pratite uputstva na ekranu da generišete i dodate SHA-1 ključ (potrebno za Google prijavu i druge servise).

Preuzimanje google-services.json: Nakon dodavanja aplikacije, Firebase će vam ponuditi da preuzmete google-services.json fajl. Preuzmite ga.

Postavljanje fajla: Preuzeti google-services.json fajl iskopirajte u app/ folder unutar vašeg Android Studio projekta.

Aktivacija servisa: U Firebase konzoli, u meniju sa leve strane, aktivirajte sledeće servise:

Authentication: Idite na tab "Sign-in method" i omogućite "Email/Password".

Firestore Database: Kreirajte novu bazu. Možete početi u "test mode" za lakši razvoj.

Realtime Database: Kreirajte novu bazu. Takođe možete početi u "test mode".

3. Pokretanje Aplikacije
Otvorite projekat u Android Studiju.

Sačekajte da se Gradle sinhronizacija završi. Ako se pojavi greška, probajte File -> Invalidate Caches / Restart....

Izaberite emulator (preporučeno API 30+) ili povežite fizički Android uređaj.

Kliknite na "Run" (zelena Play ikonica) u gornjoj traci.

Nakon ovoga, aplikacija bi trebalo da se pokrene. Prvo ćete morati da prođete kroz ekran za registraciju da biste kreirali nalog.
