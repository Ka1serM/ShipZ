package Networking;

import Administration.Enums.EAction;
import Administration.Event;
import Administration.EventListener;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Netzwerkklasse für IP1 ShipZ - Schiffe versenken.
 * Stellt Server und Client bereit
 * Ein Ping-Pong zwischen Server und Client findet statt.
 * Anleitung: 1. Netzwerk Objekt(e) erstellen.
 * 			  2. Neues Thread Objekt mit dem erstellten Netzwerk-Objekt als Parameter erstellen
 * 			  3. start() Methode des Thread-Objekts aufrufen
 */
public class Netzwerk implements Runnable {

	private EventListener listener; // EventListener für Netzwerkereignisse
	private ServerSocket serverSocket; // Server-Socket für serverseitige Operationen
	private Socket socket; // Socket für die Client-Server-Kommunikation
	private int port; // Portnummer für die Verbindung
	private String hostIP; // Host-IP-Adresse für Clientseitige Operatioen
	private boolean connectionEstablished = false;
	private boolean timeoutCalled = false; // Um zu prüfen, ob handleTimeout() schonmal aufgerufen wurde
	private BufferedReader in; // Reader für Eingaben
	private PrintWriter out; // Writer für Ausgaben
	private Timer timer; // Für den Ping-Pong
	private boolean isServer;
	private long lastPingTime; // Zeitstempel des letzten empfangenen Pings
	private long pingPongInterval; // Intervall zwischen Pings 4 Sek
	private long timeout; // Wenn 12 Sekunden erreicht, Timeout

	/**
	 * Konstruktor, zum Erstellen einer Server-Instanz
	 * @param listener Der EventListener für die Kommunikation
	 *                 zwischen Netzwerk und Verwaltung.
	 * @param port Der Port, der geöffnet werden soll
	 * @param interval Intervall, für das Ping-Pong (4 Sekunden sind ideal)
	 * @param timeout Timeout für das Ping-Pong (12 Sekunden sind ideal)
	 */
	public Netzwerk(EventListener listener, int port, long interval, long timeout) {
		this.listener = listener;
		this.timer = new Timer();
		this.port = port;
		this.isServer = true;
		this.pingPongInterval = interval;
		this.timeout = timeout;
	}

	/**
	 * Konstruktor, zum Erstellen einer Client-Instanz
	 * @param listener Der EventListener für die Kommunikation
	 *                 zwischen Netzwerk und Verwaltung
	 * @param host Die IP-Adresse, auf dem der Server läuft
	 * @param port Der Port
	 * @param interval Intervall, für das Ping-Pong (4 Sekunden sind ideal)
	 * @param timeout Timeout für das Ping-Pong (12 Sekunden sind ideal)
	 */
	public Netzwerk(EventListener listener, String host, int port, long interval, long timeout) {
		this.listener = listener;
		this.timer = new Timer();
		this.hostIP = host;
		this.port = port;
		this.isServer = false;
		this.pingPongInterval = interval;
		this.timeout = timeout;
	}

	/**
	 * Hauptmethode des Netwerks zum Starten des
	 * Server- bzw. Client-Threads.
	 * Initialisiert die Verbindung, startet den Heartbeat
	 * und hört auf eingehende Nachrichten.
	 */
	@Override
	public void run() {
		this.initialize();
		if(this.connectionEstablished) {
			this.startHeartbeat();
			this.processIncoming();
		}
	}

	/**
	 * Initialisiert die Netzwerkverbindung als Server bzw. Client
	 * Der Server wartet 60 Sekunden lang auf eingehende Verbindungen.
	 * Falls keine Verbindung zustande kommt, wird abgebrochen.
	 */
	private void initialize() {
		try {
			if (this.isServer) { // Wenn die Instanz ein Server ist,
				this.serverSocket = new ServerSocket(port); // dann einen Port öffnen
				this.serverSocket.setSoTimeout(60000); // "With this option set to a positive timeout value, a call to accept() for this ServerSocket will block for only this amount of time. If the timeout expires, a java.net.SocketTimeoutException is raised"
				this.socket = serverSocket.accept();
			}
			else { // Wenn die Instanz ein Client ist, dann mit dem Server unter der gegebenen IP-Adresse und Port verbinden
				boolean connected = false;
				long startTime = System.currentTimeMillis();
				// Alle 1000 Millisekunden versuchen eine Verbindung herzustellen, bis 60 Sekunden vergangen sind.
				while(!connected && (System.currentTimeMillis() - startTime) < 60000) {
					try {
						this.socket = new Socket(this.hostIP, this.port); // Versuche zuerst Verbindung zum Server herzustellen.
						connected = true; // Wenn eine Verbindung hergestellt werden konnte, connected auf true und somit while beenden
					} catch(IOException e1) {
						try {
							Thread.sleep(1000); // Wenn die Verbindung zum Server nicht hergestellt werden konnte, 1 Sekunde warten
						} catch (InterruptedException e2) {
							e2.printStackTrace();
						}
					}
				}
				if(!connected) { // Wenn nach den 60 Sekunden immer noch keine Verbindung hergestellt werden konnte:
					System.out.println("Timeout von 1 Minute erreicht. Verbindung zum Server konnte nicht hergestellt werden.");
					this.handleTimeout();
				}
			}
			// I/O-Streams initialisieren
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // BufferedReader zum Lesen von Eingaben vom Socket
			this.out = new PrintWriter(socket.getOutputStream(), true /*Flusht den PrintWriter automatisch nach jeder Ausgabe*/); // PrintWriter zum Senden von Ausgaben über den Socket
			this.connectionEstablished = true;
			this.notifyListener(EAction.NETZWERK_VERBUNDEN, "");
			System.out.println("Die Verbindung wurde hergestellt.");
		} catch(IOException e) {
			this.handleTimeout();
		}
	}

	/**
	 * Stoppt die Netzwerkverbindung und schließt alle Ressourcen
	 */
	public void stop() {
		try {
			this.connectionEstablished = false; // Nicht mehr verbunden
			if (this.timer != null) {
				this.timer.cancel(); // Timer beenden
				this.timer = null; // und wieder auf null
			}
			if (this.serverSocket != null) {
				this.serverSocket.close(); // ServerSocket schließen
				this.serverSocket = null; // und wieder auf null
			}
			if (this.socket != null) {
				this.socket.close(); // Socket schließen
				this.socket = null; // und wieder auf null
			}
			if (this.out != null) {
				this.out.close(); // out-Stream schließen
				this.out = null; // und wieder auf null
			}
			if (this.in != null) {
				this.in.close(); // in-Reader schließen
				this.in = null; // und wieder auf null
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Startet den Ping-Pong, wenn aktuelle Instanz Server ist.
	 * Sendet alle "pingPongInterval" Millisekunden einen "Ping"
	 */
	public void startHeartbeat() {
		if(this.isServer) {
			this.timer.scheduleAtFixedRate(new TimerTask() { // Neuer TimerTask
				@Override
				public void run() {
					Netzwerk.this.sendString("Ping"); // Client konstant anpingen.
				}
			}, 0/*Keine Verzögerung*/, pingPongInterval/*Alle pingPongInterval Millisekunden*/);
		}
	}

	/**
	 * Hört auf eingehende Nachrichten und gibt sie auf die Konsole aus
	 * Wenn eingehende Nachricht vom Server ein "Ping" ist, wird mit "Pong" geantwortet
	 */
	private void processIncoming() {
		String message;
		try {
			while (connectionEstablished) { 
				if((message = this.in.readLine()) != null) {
					if (message.equals("Ping")) { // Wenn ankommende Nachricht ein Ping = Client
						System.out.println("Ping received on Client");
						this.lastPingTime = System.currentTimeMillis(); // Variable auf Systemzeit setzen
						this.sendString("Pong"); // schicke an Server Pong als Antwort
					} else if(message.equals("Pong")) { // Wenn ankommende Nachricht ein Pong = Server
						System.out.println("Pong received on Server");
						this.lastPingTime = System.currentTimeMillis(); // Variable auf Systemzeit setzen
					} else {
						//TODO: listener recieve message
						this.recieveActionEvent(message);
					}
				}
				this.checkConnectionTimeout(); // Besteht die Verbindung noch?
			}
		} catch (IOException e) {
			//this.handleTimeout();
		}
	}

	/**
	 * Überprüft, ob die Verbindung das Timeout (12s) überschritten hat
	 */
	private void checkConnectionTimeout() {
		if (this.lastPingTime != 0 && (System.currentTimeMillis() - this.lastPingTime) > timeout)
			this.handleTimeout();
	}

	/**
	 * Wird aufgerufen, falls ein Timeout oder eine IOException auftritt.
	 * Falls der Timeout zum ersten Mal passiert,
	 * wird die derzeitige Verbindung beendet,
	 * dann wird eine erneute Verbindung zwischen Server u. Client auf Port++ versucht.
	 * Falls nicht zum ersten Mal, wird die Verwaltung informiert.
	 */
	private void handleTimeout() {
		// Wenn noch nie ein Timeout war, dann auf port++ versuchen
		if(!this.timeoutCalled) {
			this.stop();
			this.timeoutCalled = true;
			System.out.println("Timeout, versuche auf Port++");
			this.port++;
			this.initialize();
			System.out.println("Neue Verbindung auf Port "+this.port+" "+this.connectionEstablished);
			if(this.connectionEstablished) {
				this.timer = new Timer(); // Neuer Timer für den Heartbeat
				this.startHeartbeat();
				this.processIncoming();
			}
		}
		else
			// sonst Verwaltung informieren
			notifyListener(EAction.NETZWERK_TIMEOUT, "");
	}

	/**
	 * Sendet eine Nachricht über die aktive Verbindung.
	 * @param message Die zu sendende Nachricht.
	 */
	public void sendString(String message) {
		if(this.connectionEstablished)
			this.out.println(message);
	}
	
	/**
	 * Sendet ein Action-Event mit dem angegebenen Spielbefehl und einer Nachricht 
	 * über das Netzwerk, indem das Event in ein JSON-Format konvertiert wird.
	 * @param gameAction Der auszuführende Spielbefehl, der durch das Enum EAction definiert ist.
	 * @param message Die Nachricht, die mit dem Befehl gesendet werden soll.
	 */
	public void sendActionEvent(EAction gameAction, String message) {
		// Erstellt ein neues Event-Objekt mit dem Namen der Klasse, dem ordinalen Wert des Spielbefehls und der übergebenen Nachricht
		Event event = new Event(Netzwerk.class.getSimpleName(), gameAction.ordinal(), message);
		// Wandelt das Event-Objekt mithilfe der Gson-Bibliothek in ein JSON-Format um. Entfernt alle Zeilenumbrüche aus dem JSON-String.
		String actionJson = new Gson().toJson(event).replace("\n", "");
		this.sendString(actionJson);
	}
	
	/**
	 * Empfängt eine Nachricht, die ein Action-Event enthält, und ruft den zugehörigen 
	 * Listener auf, um die entsprechende Aktion auszuführen.
	 * @param message Die empfangene Nachricht im JSON-Format, die in ein Event-Objekt 
	 * umgewandelt wird.
	 */
	private void recieveActionEvent(String message) {
		// Wandelt die empfangene Nachricht aus dem JSON-Format zurück in ein Event-Objekt.
		Event action = new Gson().fromJson(message, Event.class);
		// Informiert den Listener über das empfangene Event und führt die entsprechende Aktion aus.
		this.listener.actionPerformed(action);
	}

	/**
	 * Benachrichtigt die Verwaltung dass ein Befehl ausgeführt wurde.
	 * @param gameAction Der Spielbefehl
	 * @param message Die Nachricht
	 */
	private void notifyListener(EAction gameAction, String message) {
		this.listener.actionPerformed(new Event(Netzwerk.class.getSimpleName(), gameAction.ordinal(), message));
	}
}
