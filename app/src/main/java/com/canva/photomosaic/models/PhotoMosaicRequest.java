package com.canva.photomosaic.models;

public class PhotoMosaicRequest {

    private int averageColor;
    private int tileXCoordinate;
    private int tileYCoordinate;
    private int tileWidth;
    private int tileHeight;

    public int getAverageColor() {
        return averageColor;
    }

    public void setAverageColor(final int averageColor) {
        this.averageColor = averageColor;
    }

    public int getTileXCoordinate() {
        return tileXCoordinate;
    }

    public void setTileXCoordinate(final int tileXCoordinate) {
        this.tileXCoordinate = tileXCoordinate;
    }

    public int getTileYCoordinate() {
        return tileYCoordinate;
    }

    public void setTileYCoordinate(final int tileYCoordinate) {
        this.tileYCoordinate = tileYCoordinate;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(final int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(final int tileHeight) {
        this.tileHeight = tileHeight;
    }


    @Override
    public String toString() {
        return "PhotoMosaicRequest : " +
                " AverageColor=" + averageColor + "/n" +
                " TileXPosition=" + tileXCoordinate +  "/n" +
                " TileYPosition=" + tileYCoordinate +  "/n" +
                " TileWidth=" + tileWidth +  "/n" +
                " TileHeight=" + tileHeight;
    }
}
