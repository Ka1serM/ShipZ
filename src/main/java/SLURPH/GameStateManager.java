package SLURPH;


import Administration.Nachrichten.SpielfeldKonfiguration;
import Administration.Spieler;
import Administration.Spielfeld;
import com.google.gson.Gson;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;



public class GameStateManager { //TODO Klasse umbenennen in Slurph, Package auch umbenennen, darf nicht gleich sein
    private final String spielstandOrdnerPfad;
    private String highscoreFilePath;     // Pfad zur Highscore-Datei
    //TODO access modifier
    ArrayList<HighscoreItem> highscoreList;
    String appData;
    // Stacks für Undo und Redo
    Stack<GameMove> moveStack;
    Stack<GameMove> redoStack;


    public GameStateManager( ) {
        this.highscoreList = new ArrayList<>();
        this.appData = new File(System.getenv("APPDATA"), "ShipZ").toString();
        
        File spielstandOrdner = new File ("./Spielstand/");
        if (!spielstandOrdner.exists()) { //TODO code duplikation
            spielstandOrdner.mkdir();
        }
        this.spielstandOrdnerPfad = spielstandOrdner.getPath();
        this.highscoreFilePath = new File (this.spielstandOrdnerPfad + "highscores.txt").getPath();  // Highscore-Dateipfad
        
        this.loadHighscores();
        this.moveStack = new Stack<>();
        this.redoStack = new Stack<>();
        
        
 
        this.highscoreList = new ArrayList<>();


        File ordner = new File(spielstandOrdnerPfad);
        if(!ordner.exists()){
            if(ordner.getParentFile() != null){
                ordner.getParentFile().mkdirs();
            }
            ordner.mkdirs();
        }
        loadHighscores();  // Lade die Highscores bei der Initialisierung

    }

    //TODO Es gibt natuerlich nur eine SpielfeldKonfiguration!
    // Spielstand speichern
    public void saveGame(Spielfeld player1Board, Spielfeld player2Board, Spieler player1Points, Spieler player2Points, SpielfeldKonfiguration player1Konfig, SpielfeldKonfiguration player2Konfig) {
        // Zeitstempel für den Dateinamen generieren
        long timestamp = System.currentTimeMillis();
        String filePath = spielstandOrdnerPfad + "/" + timestamp + ".txt";  // Pfad mit "/" am Ende

        // Erstelle ein GameMove-Objekt inklusive der SpielfeldKonfiguration
        GameMove gameMove = new GameMove(player1Points, player2Points, player1Board, player2Board, player1Konfig, player2Konfig);

        // Konvertiere das GameMove-Objekt in JSON-Format
        String gameMoveJson = new Gson().toJson(gameMove).replace("\n", "");  // Entferne Zeilenumbrüche

        // Speichern des Spielstands in einer separaten Datei
        try (FileWriter file = new FileWriter(filePath)) {  // Datei mit Zeitstempel speichern
            file.write(gameMoveJson);
            System.out.println("Spielstand erfolgreich gespeichert: " + filePath);
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern des Spiels: " + e.getMessage());
        }
    }

    public void saveAtEnd(Spieler player1Points, Spieler player2Points, Spielfeld player1Board, Spielfeld player2Board,SpielfeldKonfiguration player1Konfig, SpielfeldKonfiguration player2Konfig) {

        GameMove gameMove = new GameMove(player1Points, player2Points, player1Board, player2Board,player1Konfig, player2Konfig);

        String gameMoveJson = new Gson().toJson(gameMove).replace("\n", "");

        // 3. Anhängen am Ende der Datei
        try (FileWriter file = new FileWriter(this.appData+ "/savedGame.json", true)) {  // true -> Append-Modus
            file.write(gameMoveJson + "\n");  // Jede Zeile ein Spielzug
            System.out.println("Spielzug erfolgreich am Ende der Datei gespeichert.");
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern des Spiels: " + e.getMessage());
        }
    }


    // Spielstand laden
    //TODO input einen long (der Dateiname der Datei, die geladen werden soll). Nur diese Datei laden und den GameState zurueckgeben!
    public GameState loadGame() {
        File folder = new File(spielstandOrdnerPfad);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));  // Lade alle gespeicherten Spielstände (Textdateien)

        if (files == null || files.length == 0) {
            System.out.println("Kein gespeicherter Spielstand gefunden.");
            return null;
        }

        // Die Datei mit dem neuesten Änderungsdatum finden
        File lastFile = null;
        long latestModified = Long.MIN_VALUE;  // Korrektur: latest statt lastest

        for (File file : files) {
            if (file.lastModified() > latestModified) {
                latestModified = file.lastModified();
                lastFile = file;
            }
        }

        if (lastFile == null) {
            System.out.println("Kein gespeicherter Spielstand gefunden.");
            return null;
        }

        // Den letzten Spielzug aus der Datei laden
        String lastLine = "";
        try (BufferedReader br = new BufferedReader(new FileReader(lastFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lastLine = line;  // Speichere die letzte Zeile (den letzten Spielzug)
            }

            if (!lastLine.isEmpty()) {
                // Konvertiere die letzte Zeile in ein GameMove-Objekt
                GameMove lastMove = new Gson().fromJson(lastLine, GameMove.class);
                // Erstelle ein GameState-Objekt mit diesem letzten Spielzug
                return new GameState(List.of(lastMove));  // Erstelle eine Liste mit einem einzigen GameMove
            } else {
                System.out.println("Kein gespeicherter Spielstand gefunden.");
                return null;  // Oder hier einen neuen, leeren GameState erstellen
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Laden des Spiels: " + e.getMessage());
            return null;
        }

    }



    // Highscore speichern
    public void saveHighscore(String name, int score) {
        HighscoreItem highscore= new HighscoreItem(name, score);
        this.highscoreList.add(highscore);

        String highscoreJson = new Gson().toJson(highscore).replace("\n", "");

        try (FileWriter file = new FileWriter(highscoreFilePath, true)) {

            file.write(highscoreJson + "\n");
            System.out.println("Highscore erfolgreich gespeichert.");
        } catch (IOException e) {
            System.out.println("Fehler beim Speichern des Highscores: " + e.getMessage());
        }
    }

    public ArrayList<HighscoreItem> getHighscoreList() {
       this.highscoreList.sort(HighscoreItem::compareTo);
       return this.highscoreList;
    }

    private void loadHighscores() {
        
        File highscoreFile = new File(highscoreFilePath);
        if (!highscoreFile.exists()) {
            System.out.println("Keine Highscore-Datei gefunden.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(highscoreFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                HighscoreItem highscoreItem = new Gson().fromJson(line, HighscoreItem.class);
                this.highscoreList.add(highscoreItem);
            }
        } catch (IOException e) {
            System.out.println("Fehler beim Laden des Spiels: " + e.getMessage());

        }
    }

    public void bestenListeLeeren() {
        // 1. Leere die Highscore-Liste im Speicher
        this.highscoreList.clear();

        // 2. Datei leeren
        try (FileWriter file = new FileWriter(highscoreFilePath)) {
            file.write("");  // Schreibe einen leeren String in die Datei, um sie zu leeren
            System.out.println("Highscore-Liste und Datei erfolgreich geleert.");
        } catch (IOException e) {
            System.out.println("Fehler beim Leeren der Highscore-Datei: " + e.getMessage());
        }
    }

    // Methode, um den letzten Zug rückgängig zu machen (undo)
    public GameMove undoMove() {
        if (!moveStack.isEmpty()) {
            GameMove lastMove = moveStack.pop();  // Entfernt den letzten Zug vom Stack
            redoStack.push(lastMove);  // Legt den Zug auf den Redo-Stack
            System.out.println("Letzter Zug wurde rückgängig gemacht.");
            return lastMove;
        } else {
            System.out.println("Kein Zug zum Rückgängig machen vorhanden.");
            return null;
        }
    }


    // Methode, um den letzten rückgängig gemachten Zug wiederherzustellen (redo)
    public GameMove redoMove() {
        if (!redoStack.isEmpty()) {
            GameMove lastUndoneMove = redoStack.pop();  // Nimmt den letzten rückgängig gemachten Zug vom Redo-Stack
            moveStack.push(lastUndoneMove);  // Legt den Zug zurück auf den Move-Stack
            System.out.println("Letzter rückgängig gemachter Zug wurde wiederhergestellt.");
            return lastUndoneMove;
        } else {
            System.out.println("Kein rückgängig gemachter Zug zum Wiederherstellen vorhanden.");
            return null;
        }
    }



}//
