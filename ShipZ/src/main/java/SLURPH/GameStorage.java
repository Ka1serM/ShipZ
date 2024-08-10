package SLURPH;

import java.io.*;

public class GameStorage {

        private String fileName;

        public GameStorage(String fileName) {
            this.fileName = fileName;
        }

        public void saveGameState(String gameState) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(this.fileName))) {
                writer.println(gameState);
                System.out.println("Game state saved successfully.");
            } catch (IOException e) {
                System.err.println("Error saving game state: " + e.getMessage());
            }
        }

        public String loadGameState() {
            StringBuilder gameState = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(this.fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    gameState.append(line).append("\n");
                }
            } catch (IOException e) {
                System.err.println("Error loading game state: " + e.getMessage());
            }
            return gameState.toString();
        }
    }


