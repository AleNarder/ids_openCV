package com.example.myapplication.gioUtil;

import java.util.ArrayList;
import java.util.List;


public class Floor {



    public  static class OnFloorPosition implements Comparable<OnFloorPosition>{
        private int i , j ;
        public OnFloorPosition(int i , int j){this.i=i; this.j = j;}
        public OnFloorPosition(OnFloorPosition pos){this.i = pos.getRow(); this.j=pos.getCol();}
        public int getRow(){return i;}
        public int getCol(){return j;}
        public void setOnFloorPosition(int i , int j){this.i =i ; this.j=j;}

        @Override
        public int compareTo(OnFloorPosition o) {
            if(this.getCol()==o.getCol() && this.getRow()==o.getRow())
                return 0;
            else
                return 1; //TODO
        }

        @Override
        public String toString() {
            return "OnFloorPosition{" +
                    "i=" + i +
                    ", j=" + j +
                    '}';
        }
    }

    private static class Tile {
        private float width , height;
        private int onFloorRaw , onFloorCol;
        private OnFloorPosition position;
        private boolean mine; //TODO BISOGNA METTERE IL COLORE __> FARE CLASSE MINE
        private boolean checked;


        public Tile ( float width , float height , int onFloorRaw , int onFloorCol){
            setHeight(height);
            setWidth(width);
            this.onFloorRaw = onFloorRaw;
            this.onFloorCol=onFloorCol;
            position = new OnFloorPosition(onFloorRaw, onFloorCol);
            checked=false;
            mine=false;
        }

        public boolean getChecked(){return checked;}
        public void setChecked(boolean b){checked = b;}

        public void setMine(boolean b){this.mine=b;}

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public OnFloorPosition getOnFloorPosition(){
            return position;
        }

        public float getMeanWidth(){return width/2;}

        public boolean getMine(){return mine;}

    }

    /**Floor instance variable **/
    private OnFloorPosition startPosition , actualPosition , nextPosition , prevPosition;



    private int height;

    private Tile[][] field;

    private BotDirection botDirection;

    private List<Tile> uncheckedTileList;

    /************************/

    public Floor(int width , int height , float tileWidth , float tileHeight){
        uncheckedTileList = new ArrayList<>();

        field = new Tile[width][height];

        this.width = width;
        this.height = height;

        for(int i = 0 ; i<width ; i++)
            for(int j=0;j<height ; j++) {
                field[i][j] = new Tile(tileWidth, tileHeight, i, j);
                uncheckedTileList.add(field[i][j]);
            }


        /**TODO**/
        startPosition = new OnFloorPosition(0,0);
        field[startPosition.getRow()][startPosition.getCol()].setChecked(true);
        actualPosition = new OnFloorPosition(0,0);
        prevPosition=new OnFloorPosition(-1,-1);
        botDirection = BotDirection.getInstance(Direction.VERTICAL_UP);
        nextPosition = new OnFloorPosition(actualPosition.getRow()+botDirection.getY(),actualPosition.getCol()+botDirection.getX());


    }

    public Floor(int width , int height , float tileWidth , float tileHeight, int posX , int posY , Direction dir){
        uncheckedTileList = new ArrayList<>();

        field = new Tile[width][height];

        this.width = width;
        this.height = height;

        for(int i = 0 ; i<width ; i++)
            for(int j=0;j<height ; j++) {
                field[i][j] = new Tile(tileWidth, tileHeight, i, j);
                uncheckedTileList.add(field[i][j]);
            }


        /**TODO**/
        startPosition = new OnFloorPosition(posX,posY);
        field[startPosition.getRow()][startPosition.getCol()].setChecked(true);
        actualPosition = new OnFloorPosition(startPosition.getRow(),startPosition.getCol());
        prevPosition=new OnFloorPosition(-1,-1);
        botDirection = BotDirection.getInstance(dir);
        nextPosition = new OnFloorPosition(actualPosition.getRow()+botDirection.getY(),actualPosition.getCol()+botDirection.getX());


    }

    public Floor(int width , int height , float tileWidth , float tileHeight, int posX , int posY , Direction dir , List<OnFloorPosition> l){
        uncheckedTileList = new ArrayList<>();

        field = new Tile[width][height];

        this.width = width;
        this.height = height;

        for(int i = 0 ; i<width ; i++)
            for(int j=0;j<height ; j++) {
                field[i][j] = new Tile(tileWidth, tileHeight, i, j);
                uncheckedTileList.add(field[i][j]);
            }


        /**TODO**/
        startPosition = new OnFloorPosition(posX,posY);
        field[startPosition.getRow()][startPosition.getCol()].setChecked(true);
        actualPosition = new OnFloorPosition(startPosition.getRow(),startPosition.getCol());
        prevPosition=new OnFloorPosition(-1,-1);
        botDirection = BotDirection.getInstance(dir);
        nextPosition = new OnFloorPosition(actualPosition.getRow()+botDirection.getY(),actualPosition.getCol()+botDirection.getX());

        for(int i=0;i<l.size();i++){
            int row , col ;
            row=l.get(i).getRow();
            col=l.get(i).getCol();
            field[row][col].setMine(true);
        }


    }

    public Direction safeDirection(OnFloorPosition pos){
        Direction[] allDirections = Direction.class.getEnumConstants();
        BotDirection tempDirection = new BotDirection(Direction.VERTICAL_UP);
        for(int i=0;i<allDirections.length;i++){
            tempDirection.setDirection(allDirections[i]);
            if((pos.getRow()+tempDirection.getY())<0 || (pos.getRow()+tempDirection.getY())>=width)
                return allDirections[i];
            if((pos.getCol()+tempDirection.getX())<0 || (pos.getCol()+tempDirection.getX())>=height)
                return allDirections[i];
        }
        return null;
    }
    public BotDirection getBot(){return botDirection;}
    public OnFloorPosition getActualPosition() {
        return actualPosition;
    }
    public OnFloorPosition getNextPosition(){
        return nextPosition;
    }
    public OnFloorPosition getPrevPosition(){
        return prevPosition;
    }
    public OnFloorPosition getStartPosition(){return startPosition;}
    public float getTileWidth(){return field[0][0].getWidth();}
    public float getTileHeight(){return field[0][0].getHeight();}

    public int getWidth() {
        return width;
    }

    private int width;

    public int getHeight() {
        return height;
    }

    public boolean getMine(OnFloorPosition pos){return field[pos.getRow()][pos.getCol()].getMine();}
    public void setMine(OnFloorPosition pos , boolean b){field[pos.getRow()][pos.getCol()].setMine(b);}
    public boolean getChecked(OnFloorPosition pos){return field[pos.getRow()][pos.getCol()].getChecked();}

    public Direction getBotDirection(){return botDirection.getDirection();}

    public void updateBotPosition(){
        /** startPosition.setOnFloorPositio(); TODO**/
        prevPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol());
        actualPosition.setOnFloorPosition(nextPosition.getRow(),nextPosition.getCol());
        field[actualPosition.getRow()][actualPosition.getCol()].setChecked(true);
        nextPosition.setOnFloorPosition((actualPosition.getRow()+botDirection.getY()),actualPosition.getCol()+botDirection.getX());
    }

    public void updateNextPosition(){
        nextPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol());
        nextPosition.setOnFloorPosition((actualPosition.getRow()+botDirection.getY()),actualPosition.getCol()+botDirection.getX());
    }


    /** SECONDO ME NON DEVE STARE QUI**/


    public TurnDirection chooseNDPrimaProva(){
        if(actualPosition.getRow()==getWidth()-1 && botDirection.getDirection()==Direction.VERTICAL_UP){
            botDirection.setDirection(Direction.HORIZONTAL_UP);
            return TurnDirection.TURN_RIGHT;
        }
        else{
            if(actualPosition.getRow()==0 && botDirection.getDirection()==Direction.VERTICAL_DOWN){
                botDirection.setDirection(Direction.HORIZONTAL_UP);
                return TurnDirection.TURN_LEFT;
            }
            else{
                if(actualPosition.getRow()==getWidth()-1 && botDirection.getDirection()==Direction.HORIZONTAL_UP){
                    botDirection.setDirection(Direction.VERTICAL_DOWN);
                    return TurnDirection.TURN_RIGHT;
                }
                else{
                    botDirection.setDirection(Direction.VERTICAL_UP);  /**actualPosition.getRaw()==0 && botDirection.getDirection()==Direction.HORIZONTAL_UP**/
                    return TurnDirection.TURN_LEFT;
                }
            }
        }
    }

    public boolean rawVisited(int raw){
        if(raw<getWidth() && raw>=0){
            for(int i=0;i<getHeight();i++)
                if(!field[raw][i].checked)
                    return false;
            return true;
        }
        return true;
    }


    public boolean colVisited(int col){
        if(col<getHeight() && col>=0){
            for(int i=0;i<getWidth();i++)
                if(!field[i][col].checked)
                    return false;
            return true;
        }
        return true;
    }

    public int chooseNextCol(){
        for(int j=0;j<getHeight();j++)
            if(!colVisited(j))return j;
        return -1;
    }


    public TurnDirection TurnDirectionForVerticalMov(Direction d  , int raw){
        switch (d){
            case VERTICAL_UP:
                if(raw>=0)
                    return TurnDirection.NO_TURN;
                else
                    return TurnDirection.U_INVERSION;
            case VERTICAL_DOWN:
                if(raw<=0)
                    return TurnDirection.NO_TURN;
                else
                    return TurnDirection.U_INVERSION;
            case HORIZONTAL_DOWN:
                if(raw>0)
                    return TurnDirection.TURN_RIGHT;
                else
                if(raw<0)
                    return TurnDirection.TURN_LEFT;
                else return TurnDirection.NO_TURN;
            case HORIZONTAL_UP:
                if(raw>0)
                    return TurnDirection.TURN_LEFT;
                else
                if(raw<0)
                    return TurnDirection.TURN_RIGHT;
                else return TurnDirection.NO_TURN;
            default : return TurnDirection.NO_TURN;
        }
    }
    public TurnDirection TurnDirectionForHoriziontalMov(Direction d  , int col){
        switch (d){
            case VERTICAL_UP:
                if(col>0)
                    return TurnDirection.TURN_RIGHT;
                else
                if(col<0)
                    return TurnDirection.TURN_LEFT;
                else
                    return TurnDirection.NO_TURN;
            case VERTICAL_DOWN:
                if(col>0)
                    return TurnDirection.TURN_LEFT;
                else
                if(col<0)
                    return TurnDirection.TURN_RIGHT;
                else
                    return TurnDirection.NO_TURN;
            case HORIZONTAL_DOWN:
                if(col>0)
                    return TurnDirection.U_INVERSION;
                else
                    return TurnDirection.NO_TURN;
            case HORIZONTAL_UP:
                if(col>=0)
                    return TurnDirection.NO_TURN;
                else
                    return TurnDirection.U_INVERSION;
            default : return TurnDirection.NO_TURN;
        }
    }







    /**ALGORITMI SECONDA PROVA**/





    /**crea un percorso virtuale da un punto A (la mia posizione) a un punto B.
     * questa lista servirà per controllare se ci sono mine indesiderate lungo il percorso e quindi cambiarlo**/


    public static class BotDirection{
        private int y = 0, x = 0; Direction direction;
        public BotDirection(Direction d){
            if(y==0 && x==0){
                changeDirection(d);
            }

        }
        public static BotDirection getInstance(Direction d){
            return  new BotDirection(d);
        }

        public void setDirection(Direction d ){changeDirection(d);}

        private void changeDirection(Direction d){
            switch(d){
                case HORIZONTAL_UP:y=0;x=1; break;
                case VERTICAL_UP:y=1; x=0; break;
                case HORIZONTAL_DOWN:y=0;x=-1;break;
                case VERTICAL_DOWN:y=-1;x=0;break;
            }
            direction = d;
        }
        public Direction getDirection(){return direction;}

        public int getY(){return this.y;}
        public int getX(){return this.x;}
    }



    public enum TurnDirection{
        /** 90 gradi**/
        TURN_LEFT,
        TURN_RIGHT,
        /** 180 gradi**/
        U_INVERSION,
        NO_TURN
    }
    public enum Direction{
        HORIZONTAL_UP, /*moving straight change column value*/
        VERTICAL_UP, /*moving straight change raw value*/
        HORIZONTAL_DOWN,
        VERTICAL_DOWN


    }


}

