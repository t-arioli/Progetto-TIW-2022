# Progetto d'esame di Tecnologie Informatiche per il Web
                  
La versione pure HTML è presente nella directory ``TIW-2022-pureHtml``.                                                                                             
La versione con JavaScript è presente nella directory ``TIW-2022-RIA``.

## Descrizione

La traccia del progetto è presente nel file "TIW-progetti-2021-22-v2_traccia-2.pdf".
L'interfaccia del progetto è in italiano, la documentazione in inglese.

## Esecuzione

Le due cartelle sono due progetti distinti. Per eseguire ciascuno di essi:
- Configurare l'ambiente di sviluppo ``Eclipse IDE for Java Enterprise and Web Developers`` (versione 2021-12 o successive) con ``Java JDK 17`` (o versioni successive)  
- Configurare all'interno di Eclipse il server ``Apache Tomcat 9`` per l'esecuzione del protocollo HTTP sulla porta 8080
- Configurare ``MySQL Server`` (versione 8 o successive) e ``MySQL Workbench``; procedere a creare una connessione all'interno di Workbench e caricare lo script ``db_tiw_2022.sql``
- In Eclipse: ``File > Open Project from File System `` e selezionare la cartella per caricare nell'ambiente il progetto
- Modificare il file ``src/main/webapp/WEB-INF/web.xml`` alla sezione ``<!-- JDBC -->`` inserendo: 
- - al parametro ``dbUrl`` il nome della connessione al database locale
- - al parametro ``dbUser`` il nome dell`amministratore del database locale
- - al parametro ``dbPassword`` la sua password
- Cliccare con il tasto destro sul nome del progetto nel Project Explorer, ``Run As > Run on Server > Tomcat v9.0 Server at localhost`` : il progetto sarà disponibile all'indirizzo ``http://localhost:8080/TIW-2022-pureHtml`` o ``http://localhost:8080/TIW-2022-RIA`` 

