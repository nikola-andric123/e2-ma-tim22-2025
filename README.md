# 📱 Kako pokrenuti projekat

Da biste uspešno pokrenuli i testirali projekat, potrebno je da podesite lokalno okruženje i Firebase.

## 🔧 Preduslovi
- [Android Studio](https://developer.android.com/studio) (preporučeno najnovija verzija)  
- [Git](https://git-scm.com/)  
- Google nalog za [Firebase](https://console.firebase.google.com/)  

## ⚡ Koraci za podešavanje

### 1. Kloniranje Repozitorijuma
```bash
git clone [URL_VAŠEG_REPOZITORIJUMA]
cd [NAZIV_FOLDERA]
```

### 2. Podešavanje Firebase Projekta
- Idite na [Firebase Konzolu](https://console.firebase.google.com/) i kreirajte novi projekat.  
- Dodajte Android aplikaciju:  
  - *Package Name*: `com.example.rpggame` (ili ime paketa vašeg projekta).  
  - *SHA-1 ključ*: pratite uputstva na ekranu i dodajte ga.  
- Preuzmite `google-services.json` i ubacite ga u `app/` folder unutar Android Studio projekta.  
- Aktivirajte servise:  
  - **Authentication** → *Sign-in method* → omogućite *Email/Password*.  
  - **Firestore Database** → kreirajte bazu (*test mode*).  
  - **Realtime Database** → kreirajte bazu (*test mode*).  

### 3. Pokretanje Aplikacije
1. Otvorite projekat u Android Studio.  
2. Sačekajte da se Gradle sinhronizacija završi. Ako se pojavi greška: *File → Invalidate Caches / Restart...*  
3. Izaberite emulator (API 30+) ili povežite fizički uređaj.  
4. Kliknite na **Run ▶** (zelena Play ikonica).  

## 🚀 Nakon pokretanja
Aplikacija će se startovati i prikazati ekran za **registraciju**. Najpre kreirajte nalog da biste mogli dalje da koristite aplikaciju.  
