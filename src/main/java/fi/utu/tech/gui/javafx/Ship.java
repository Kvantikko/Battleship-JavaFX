package fi.utu.tech.gui.javafx;

import javafx.geometry.BoundingBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Ship extends Rectangle {

	private final String type;
    private final int size;
    private double resetX;
    private double resetY;
    private int hitPoints;
    private boolean sunk;
	private BoundingBox protectiveBoundX;
	private BoundingBox protectiveBoundY;
	private ImagePattern defaultPattern;
	private ImagePattern collisionPattern;
	private ImagePattern transParentPattern;
	
	public Ship(int size, String type,ImagePattern imgPattern, ImagePattern destroyed, ImagePattern transParent) {
		this.type = type;
		super.setHeight(40);
		super.setWidth(40 * size);
		this.size = size;
		this.setFill(Color.GRAY);
		this.setStroke(Color.RED);
		this.setStrokeWidth(0);
		this.hitPoints = size;
		this.sunk = false;
		this.setFill(imgPattern);
		this.defaultPattern = imgPattern;
		this.collisionPattern = destroyed;
		this.transParentPattern = transParent;
		this.setFill(this.defaultPattern);
    }

    public int getSize() {
        return size;
    }

    //Asettaa laivan aloitus koordinaatit scenessä, johon se palautetaan reset napilla.
    public void setReset(double x, double y) {
    	this.resetX = x;
    	this.resetY = y;
    }
    public double getResetX() {
    	return this.resetX;
    }

    public double getResetY() {
    	return this.resetY;
    }

    //Hit pointsit määrää kuinka monta osumaa laiva ottaa ennen kuin se uppoaa.
    public int getHitPoints() {
		return hitPoints;
	}

    //Palauttaa true jos laiva on uponnut.
	public boolean isSunk() {
		return sunk;
	}

	public void setSunk() {
		this.sunk = true;
		this.setFill(this.collisionPattern);
	}

	public void hit() {
		this.hitPoints = hitPoints-1;
	}

	// Asettaa laivalle "suojaavat boundsit", joiden avulla lasketaan laivan vieressä olevat blokatut ruudut, eli siis
	// mahdollistetaan vain laivojen koskeminen toisistaan nurkista.
	public void setProtectiveBounds() {
		if (this.getRotate() == 0) {
			protectiveBoundX = new BoundingBox(
			this.getX()-40, this.getY(), this.getWidth()+80, this.getHeight()
			);
			protectiveBoundY = new BoundingBox(
				this.getX(), this.getY()-40, this.getWidth(), this.getHeight()+80
			);
		} else {
			protectiveBoundX = new BoundingBox(
			this.getX()+(-60+size*20), this.getY()-(size*20-20), this.getHeight()+80, this.getWidth()
			);
			protectiveBoundY = new BoundingBox(
			this.getX()+(size*20-20), this.getY()-(20+size*20), this.getHeight(), this.getWidth()+80
			);
		}
	}

	public BoundingBox getProtectiveBoundX() {
		return protectiveBoundX;
	}

	public BoundingBox getProtectiveBoundY() {
		return protectiveBoundY;
	}

	// Palauttaa laivan sisäiset bounsit, joiden avulla lasketaan onko laiva jonkin pelilaudan ruudun sisällä.
	public BoundingBox getInnerBounds() {
		if (this.getRotate() == 0) {
			return new BoundingBox(
				this.getX() + 10,
				this.getY() + 10,
				this.getSize()*40 - 20,
				40 - 20
			);
		} else {
			return new BoundingBox(
				this.getX() + 20*this.getSize() - 10,
				this.getY() - 20*this.getSize() + 30 ,
				40 - 20,
				this.getSize()*40 - 20
			);
		}
	}

	// Boundseja käytetään pelilaudan ruutujen värittämiseen
	public BoundingBox getBoundsForBoardColoringX() {
		setProtectiveBounds();
		var protecX = getProtectiveBoundX();
		return new BoundingBox(
				protecX.getMinX()+20,
				protecX.getMinY()+10,
				protecX.getWidth()-40,
				protecX.getHeight()-20
		);
	}

	// Boundseja käytetään pelilaudan ruutujen värittämiseen
	public BoundingBox getBoundsForBoardColoringY() {
		setProtectiveBounds();
		var protecY = getProtectiveBoundY();
		return new BoundingBox(
				protecY.getMinX()+20,
				protecY.getMinY()+20,
				protecY.getWidth()-40,
				protecY.getHeight()-40
		);
	}

	public String getType() {
		return type;
	}

	public ImagePattern getDefaultPattern() {
		return defaultPattern;
	}

	public ImagePattern getCollisionPattern() {
		return collisionPattern;
	}

	public ImagePattern getTransParentPattern() {
		return transParentPattern;
	}
}
