package fi.utu.tech.gui.javafx;

import java.util.ArrayList;

public class Player {

    private String name;
    private ArrayList<Ship> ships;
    private GameBoard gameBoard;
    private Boolean turn;

    public Player(String name) {
        this.name = name;
        this.ships = new ArrayList<>();
    }
    
    public String getName() {
    	return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addShip(Ship ship) {
        this.ships.add(ship);
    }
    
    public void addShips(ArrayList<Ship> ships) {
        this.ships.addAll(ships);
    }
    
    public void removeShip(Ship ship) {
    	this.ships.remove(ship);
    }

    public void removeShips() {
        this.gameBoard.removeShipsFromGameBoard();
        this.ships.clear();
    }
    
    public ArrayList<Ship> getShips() {
    	return ships;
    }
    
    public void setTurn(Boolean b) {
    	this.turn = b;
    }
    
    public Boolean getTurn() {
    	return this.turn;
    }

    public void setGameBoard(GameBoard gameboard) {
    	this.gameBoard = gameboard;
    }
    
    public GameBoard getGameBoard() {
    	return gameBoard;
    }

    public boolean hasShipsLeft() {
        for (Ship s : ships) {
            if (!s.isSunk()) {
                return true;
            }
        }
        return false;
    }
}
