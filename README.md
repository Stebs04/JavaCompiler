# 🎓 Compiler Project - Fondamenti di Linguaggi e Traduttori
![Java](https://img.shields.io/badge/Language-Java-orange) ![JUnit](https://img.shields.io/badge/Test-JUnit%205-green) ![Target](https://img.shields.io/badge/Target-dc-lightgrey) ![Pattern](https://img.shields.io/badge/Pattern-Visitor-blue) ![IDE](https://img.shields.io/badge/IDE-IntelliJ-purple)

**Autore:** Stefano Bellan  
**Matricola:** 20054330  
**Anno Accademico:** 2025/2026  
**Linguaggio:** Java 

---

## 📖 Descrizione
Questo progetto consiste nello sviluppo di un **compilatore completo** per un linguaggio imperativo personalizzato ("acro-language"). Il compilatore traduce il codice sorgente in istruzioni per **`dc` (desk calculator)**, una calcolatrice a stack a notazione polacca inversa (RPN) disponibile sui sistemi Unix/Linux.

Il sistema copre l'intera pipeline di compilazione:
1.  **Analisi Lessicale (Scanner):** Tokenizzazione dell'input.
2.  **Analisi Sintattica (Parser):** Costruzione dell'Abstract Syntax Tree (AST) tramite discesa ricorsiva.
3.  **Analisi Semantica (Type Checker):** Controllo dei tipi, gestione degli scope e **Casting Implicito** (promozione automatica da `int` a `float`).
4.  **Generazione Codice (Backend):** Traduzione dell'AST nel linguaggio target `dc`.

---

## 🚀 Funzionalità del Linguaggio

### Tipi di Dato Supportati
* **`int`**: Numeri interi (es. `5`, `-10`).
* **`float`**: Numeri in virgola mobile (es. `3.14`, `0.5`).
    * *Nota:* Precisione fissata a 5 cifre decimali nello Scanner e 20 cifre nel codice `dc` generato.

### Caratteristiche Principali
* **Dichiarazione Variabili**: `int a;` o `float b;`.
* **Inizializzazione**: `int a = 10;`.
* **Assegnamento**: `a = 5 + 2;`.
* **Espressioni Matematiche**: Supporto per `+`, `-`, `*`, `/` con gestione della precedenza operatori e parentesi `(...)`.
* **Type System**:
    * Strong typing (non si possono assegnare float a int).
    * **Coercizione Implicita**: Un `int` viene convertito automaticamente in `float` se usato in operazioni miste (es. `float x = 5 + 2.5;` → `5` diventa `5.0`).
* **Output**: Istruzione `print variable;` per stampare il valore a video.

---

## 🏗️ Architettura del Software

Il progetto segue rigorosamente i principi dell'ingegneria del software, utilizzando il **Visitor Pattern** per separare la logica (controllo tipi, generazione codice) dalla struttura dati (nodi dell'AST).

### Struttura dei Package
* `it.unipmn.compilatore.scanner`: Gestisce la lettura del file e la creazione dei `Token`.
* `it.unipmn.compilatore.parser`: Implementa la grammatica e costruisce l'albero `NodeProgram`.
* `it.unipmn.compilatore.ast`: Definisce i nodi dell'albero (es. `NodeBinOp`, `NodeDecl`, `NodeId`).
* `it.unipmn.compilatore.symboltable`: Gestisce gli scope e la memorizzazione dei simboli (Tipo e Registro `dc`).
* `it.unipmn.compilatore.visitor`: Contiene la logica operativa:
    * `TypeCheckVisitor`: Valida i tipi e inietta nodi `NodeConvert` nell'AST per i cast.
    * `CodeGeneratorVisitor`: Traduce l'AST in comandi `dc` (es. `sa`, `la`, `p`).
    * `PrintASTVisitor`: Utility per visualizzare la struttura dell'albero a fini di debug.

---

## 💻 Esempio di Sintassi e Compilazione

### Codice Sorgente (`programma.txt`)
```java
int a = 10;
float b = 2.5;
float result;

// Esempio di espressione mista con conversione implicita
result = a + b * 2.0;

print result;
```
### Codice Target Generato (out.dc)
Il compilatore mappa le variabili sui registri di dc (es. a -> registro a, b -> registro b).
```bash
20 k           # Imposta precisione a 20 cifre
10 sa          # Store '10' in registro 'a'
2.5 sb         # Store '2.5' in registro 'b'
la lb 2.0 * +  # Carica a, Carica b, Moltiplica per 2.0, Somma
s3             # Salva risultato nel registro temporaneo (es. 'c')
l3 p si        # Carica 'c', Stampa (p), Pulisci stack (si)
```
---
## 🛠️ Istruzioni per l'Uso
Prerequisiti
Java JDK 11+ installato.

dc: Interprete desk calculator (preinstallato su Linux/macOS, disponibile su Windows via WSL o Git Bash).

1. Compilazione del Progetto
Dalla cartella radice del progetto, eseguire:
```bash
# Crea la cartella per i file compilati
mkdir -p bin

# Compila tutti i sorgenti Java
javac -d bin -sourcepath "Java compiler/src" "Java compiler/src/it/unipmn/compilatore/Compiler.java"
```

2. Esecuzione del Compilatore
Per compilare un file di testo (es. programma.txt):
```bash
java -cp bin it.unipmn.compilatore.Compiler programma.txt
```

Se non viene specificato alcun file, il compilatore cercherà di default programma.txt.

Se la compilazione ha successo, verrà generato il file out.dc.

3. Esecuzione del Programma Compilato
Per eseguire il codice generato usando l'interprete dc:
```bash
dc -f out.dc
```
---

## ✅ Testing (JUnit)
Il progetto include una suite completa di Unit Test e Integration Test per garantire la robustezza di ogni componente.

I test coprono:

* **ScannerTest: Riconoscimento token validi, gestione errori lessicali.**

* **ParserTest: Validazione grammatica, precedenza operatori, errori sintattici.**

* **TypeCheckTest: Verifica compatibilità tipi, cast impliciti, variabili non dichiarate.**

* **CodeGeneratorTest: Correttezza istruzioni dc, gestione numeri negativi (es. -5 -> _5).**

* **CompilerTest: Test End-to-End (Sorgente -> Output finale).**

Per eseguire i test (richiede junit-platform-console-standalone.jar o un IDE come IntelliJ/Eclipse):

* **Si consiglia di aprire il progetto come Progetto Maven/Gradle o importarlo in IntelliJ IDEA ed eseguire la cartella test.**
---

## 📂 Struttura File
```
20054330Compiler/
├── programma.txt               # Sorgente di esempio
├── out.dc                      # Output compilato
├── Java compiler/
│   └── src/it/unipmn/compilatore/
│       ├── Compiler.java       # Main Class
│       ├── scanner/            # Analisi Lessicale
│       ├── parser/             # Analisi Sintattica
│       ├── ast/                # Definizioni Nodi AST
│       ├── visitor/            # Logica (TypeCheck, CodeGen)
│       ├── symboltable/        # Gestione Variabili
│       ├── token/              # Definizioni Token
│       ├── exceptions/         # Errori custom
│       └── test/               # JUnit Tests
└── README.md                   # Questo file
```

