# 🎓 Progetto Compilatore - Fondamenti di Linguaggi e Traduttori

**Autore:** 👤 Bellan Stefano  
**Matricola:** 🆔 20054330  
**Corso:** Fondamenti di Linguaggi e Traduttori  
**Anno Accademico:** 2025/2026

---

## 📄 Descrizione del Progetto

Questo progetto implementa un compilatore completo in **Java** per un linguaggio imperativo personalizzato. Il compilatore analizza il codice sorgente attraverso una pipeline classica e traduce le istruzioni nel linguaggio per la calcolatrice a pila **`dc`** (desk calculator).

### 🚀 Funzionalità Principali
* **Tipi di Dato:** Supporto per interi (`int`) e virgola mobile (`float`).
* **Variabili:** Dichiarazione, inizializzazione e assegnamento (mappate sui registri `a`-`z` di `dc`).
* **Operazioni:** Aritmetica di base (`+`, `-`, `*`, `/`) con gestione della precedenza.
* **Type System:**
    * Controllo dei tipi (Type Checking).
    * **Casting Implicito:** Conversione automatica da `int` a `float` nelle espressioni miste.
* **IO:** Comando `print` per stampare i risultati.

---

## 📂 Struttura del Progetto

Di seguito è riportato l'albero completo dei file sorgente con una breve descrizione del loro ruolo nel compilatore.

```text
20054330Compiler/
├── 📄 programma.txt                # File sorgente di esempio per testare il compilatore
├── 📄 integration_test.txt         # File usato per i test di integrazione
├── ⚙️ out.dc                       # File di output generato (codice per dc)
│
└── ☕ Java compiler/src/it/unipmn/compilatore/
    │
    ├── 🚀 Compiler.java            # Main Class: orchestra Scanner, Parser, TypeChecker e CodeGenerator
    │
    ├── 📦 scanner/                 # ANALISI LESSICALE
    │   └── Scanner.java            # Tokenizza l'input gestendo numeri, ID, keyword e commenti
    │
    ├── 📦 token/                   # DEFINIZIONE TOKEN
    │   ├── Token.java              # Rappresenta l'unità lessicale (tipo, valore, riga)
    │   └── TokenType.java          # Enum dei tipi di token (INT, FLOAT, PRINT, PLUS, ecc.)
    │
    ├── 📦 parser/                  # ANALISI SINTATTICA
    │   └── Parser.java             # Parser a discesa ricorsiva, costruisce l'AST
    │
    ├── 📦 ast/                     # ABSTRACT SYNTAX TREE (Nodi dell'albero)
    │   ├── NodeAST.java            # Classe astratta base per tutti i nodi
    │   ├── NodeProgram.java        # Nodo radice: contiene la lista di istruzioni
    │   ├── NodeDecl.java           # Dichiarazione variabili (es. int a = 5;)
    │   ├── NodeAssign.java         # Assegnamento (es. a = 10;)
    │   ├── NodeBinOp.java          # Operazioni binarie (es. a + b)
    │   ├── NodeCost.java           # Costanti numeriche (Interi o Float)
    │   ├── NodeId.java             # Identificatori di variabile
    │   ├── NodePrint.java          # Istruzione di stampa (print x;)
    │   ├── NodeConvert.java        # Nodo speciale iniettato per il casting (Int -> Float)
    │   ├── LangType.java           # Enum tipi primitivi (INT, FLOAT)
    │   └── LangOper.java           # Enum operatori (+, -, *, /)
    │
    ├── 📦 visitor/                 # PATTERN VISITOR (Attraversamento AST)
    │   ├── IVisitor.java           # Interfaccia comune per i visitor
    │   ├── TypeCheckVisitor.java   # Analisi Semantica: controlla tipi e inietta conversioni
    │   ├── CodeGeneratorVisitor.java # Genera il codice target per 'dc'
    │   └── PrintASTVisitor.java    # Utility per stampare l'AST (debug)
    │
    ├── 📦 symboltable/             # TABELLA DEI SIMBOLI
    │   ├── SymbolTable.java        # Gestisce gli scope (stack di hashmap)
    │   └── Symbol.java             # Info variabile (Tipo e Registro fisico)
    │
    ├── 📦 exceptions/              # GESTIONE ERRORI
    │   ├── LexicalException.java   # Errori dello Scanner (caratteri invalidi)
    │   └── SyntacticException.java # Errori del Parser o Semantici
    │
    └── 🧪 test/                    # UNIT TESTING (JUnit)
        ├── ScannerTest.java        # Test tokenizzazione
        ├── ParserTest.java         # Test grammatica e precedenza
        ├── ASTTest.java            # Test costruzione nodi
        ├── TypeCheckTest.java      # Test controlli semantici e casting
        ├── CodeGeneratorTest.java  # Test generazione istruzioni dc
        ├── SymbolTableTest.java    # Test visibilità variabili
        ├── TokenTest.java          # Test struttura token
        └── CompilerTest.java       # Test End-to-End
```
---
## 🛠️ Istruzioni per Compilazione ed Esecuzione
Assicurarsi di avere Java JDK installato. I comandi vanno eseguiti dalla cartella radice del progetto.

1. Compilazione
Compila tutti i file sorgente e posiziona i .class in una cartella bin.

```Bash
mkdir -p bin
javac -d bin -sourcepath "Java compiler/src" "Java compiler/src/it/unipmn/compilatore/Compiler.java"
```
2. Esecuzione del Compilatore
Esegui il compilatore passando il file sorgente come argomento (default: programma.txt).

```Bash
java -cp bin it.unipmn.compilatore.Compiler programma.txt
```
Se la compilazione ha successo, verrà creato il file out.dc.

3. Esecuzione del Codice Generato (dc)
Per eseguire il programma compilato è necessario l'interprete dc (standard su Linux/macOS, disponibile su Windows via WSL o Git Bash).

```Bash
dc -f out.dc
```
---
## 💻 Esempio di Utilizzo
Input (programma.txt)
```Java
int a = 10;
float b = 2.5;
int c;

c = a * 2 + 5;
print c;

float d;
d = b + 1.5;
print d;

int x = 100;
x = x / 2;
print x;
```
Outuput generato (out.dc)
```Bash
20 k        (Imposta precisione a 20 cifre)
10 sa       (Salva 10 nel registro 'a')
2.5 sb      (Salva 2.5 nel registro 'b')
lb la + sb  (Carica b, Carica a, Somma, Salva in b)
lb p si     (Carica b, Stampa, Pulisci stack)
```
Output console (dc)
```Bash
25
4.0
50.00000000000000000000
```
---
## ✅ Testing
Il progetto include una suite di test completa. Per compilare ed eseguire i test (richiede JUnit 5 nel classpath):
```Bash
# Esempio generico (classpath da adattare in base al sistema)
javac -cp "lib/junit-platform-console-standalone.jar;bin" ...
```
Nota: Si consiglia di eseguire i test tramite un IDE come IntelliJ IDEA o Eclipse importando il progetto.


