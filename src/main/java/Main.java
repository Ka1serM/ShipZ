public class Main {
    public static void main(String[] args) {
        App.main(args);
    }
}
        
        /*
        // Create the spielfeld and settings
        Settings settings = new Settings();
        Spielfeld player1Board = new Spielfeld(10, 10);
        Spielfeld player2Board = new Spielfeld(10, 10);

        // Collect game state data
        GameMove move = collectGameState(player1Board, player2Board);

        // Add move to game state
        List<GameMove> gameMoves = new ArrayList<>();
        gameMoves.add(move);
        GameState gameState = new GameState(gameMoves);

        // Save game state
        saveGameState(gameState);

        // Load game state
        GameState loadedGameState = loadGameState();
        if (loadedGameState != null) {
            for (GameMove loadedMove : loadedGameState.gameMoves) {
                System.out.println("Player 1 Points: " + loadedMove.player1Points);
                System.out.println("Player 2 Points: " + loadedMove.player2Points);
                // Add additional print statements for other instance variables if needed
            }
        }

        // Highscore handling
        List<HighscoreItem> players = new ArrayList<>();

        // Add some players with their highscores to the list
        players.add(new HighscoreItem("Lina", 1500));
        players.add(new HighscoreItem("Marcel", 2000));
        players.add(new HighscoreItem("Sara", 1800));
        players.add(new HighscoreItem("Lina", 800));

        // Sort the list by scores
        Collections.sort(players);

        // Print the sorted list
        System.out.println("List of Highscores:");
        for (HighscoreItem player : players) {
            System.out.println(player);
        }

        // Example code for stack operations
        Stack<String> moveStack = new Stack<>();    // Stack for performed moves
        Stack<String> redoStack = new Stack<>();    // Stack for undone moves

        // Add some example moves
        addMove(moveStack, "Player1: Move1");
        addMove(moveStack, "Player2: Move2");

        // Undo the last move
        undoMove(moveStack, redoStack);

        // Print the moves
        System.out.println("Current moves:");
        printMoves(moveStack);

        // Redo the last undone move
        redoMove(moveStack, redoStack);

        // Print the moves again
        System.out.println("Current moves after redo:");
        printMoves(moveStack);

        // Save the current state
        saveGameStateStack(moveStack);

        // Load the saved state
        Stack<String> loadedMoveStack = loadGameStateStack();
        if (loadedMoveStack != null) {
            System.out.println("Loaded moves:");
            printMoves(loadedMoveStack);
        }
    }

    // Method to collect game state from the boards
    public static GameMove collectGameState(Spielfeld player1Board, Spielfeld player2Board) {
        int player1Points = calculatePoints(player1Board);
        int player2Points = calculatePoints(player2Board);
        int player1Ships = player1Board.getShips().size();
        int player2Ships = player2Board.getShips().size();

        char[][] player1BoardState = player1Board.getBoardState();
        char[][] player2BoardState = player2Board.getBoardState();

        return new GameMove(player1Points, player2Points, player1Ships, player2Ships, player1BoardState, player2BoardState);
    }

    // Method to calculate points based on sunk ships
    public static int calculatePoints(Spielfeld spielfeld) {
        int points = 0;
        for (Schiff ship : spielfeld.getShips()) {
            if (ship.isSunk()) {
                points += ship.getType().getSize();
            }
        }
        return points;
    }

    // Method to save game state
    public static void saveGameState(GameState gameState) {
        GameStorage storage = new GameStorage(SAVE_FILE);
        Gson gson = new Gson();
        String gameStateJson = gson.toJson(gameState);
        storage.saveGameState(gameStateJson);
    }

    // Method to load game state
    public static GameState loadGameState() {
        GameStorage storage = new GameStorage(SAVE_FILE);
        String gameStateJson = storage.loadGameState();
        Gson gson = new Gson();
        return gson.fromJson(gameStateJson, GameState.class);
    }

    // Method to add a move to the stack
    public static void addMove(Stack<String> moveStack, String move) {
        moveStack.push(move);   // Adds the move to the moveStack
    }

    // Method to undo the last move
    public static void undoMove(Stack<String> moveStack, Stack<String> redoStack) {
        if (!moveStack.isEmpty()) {
            String lastMove = moveStack.pop();    // Removes the last move from the moveStack
            redoStack.push(lastMove);     // Moves the last move to the redoStack
        }
    }

    // Method to redo the last undone move
    public static void redoMove(Stack<String> moveStack, Stack<String> redoStack) {
        if (!redoStack.isEmpty()) {
            // Remove the last undone move from the redoStack and store it in lastUndoneMove
            String lastUndoneMove = redoStack.pop();
            // Add the last undone move to the moveStack to restore it
            moveStack.push(lastUndoneMove);
        }
    }

    // Method to print the current moves
    public static void printMoves(Stack<String> moveStack) {
        // Iterate over each move in the moveStack
        for (String move : moveStack) {
            System.out.println(move);
        }
    }

    // Method to save the move stack
    public static void saveGameStateStack(Stack<String> moveStack) {
        GameStorage storage = new GameStorage(SAVE_FILE);
        Gson gson = new Gson();
        String moveStackJson = gson.toJson(moveStack);
        storage.saveGameState(moveStackJson);
    }

    // Method to load the move stack
    public static Stack<String> loadGameStateStack() {
        GameStorage storage = new GameStorage(SAVE_FILE);
        String loadedGameStateJson = storage.loadGameState();
        Gson gson = new Gson();
        Type type = new TypeToken<Stack<String>>() {}.getType();
        return gson.fromJson(loadedGameStateJson, type);
    }
   
    */