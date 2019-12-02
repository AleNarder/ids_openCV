package it.unive.dais.legodroid.lib.gioUtil;

import android.util.Log;

public class Floor {

    public  static class OnFloorPosition {
        private int i , j ;
        public OnFloorPosition(int i , int j){this.i=i; this.j = j;}
        public int getRaw(){return i;}
        public int getCol(){return j;}
        public void setOnFloorPosition(int i , int j){this.i =i ; this.j=j;}
    }

    private static class Tile {
        private float width , height;
        private int onFloorRaw , onFloorCol;
        private OnFloorPosition position;
        private boolean mine;


        public Tile ( float width , float height , int onFloorRaw , int onFloorCol){
            setHeight(height);
            setWidth(width);
            this.onFloorRaw = onFloorRaw;
            this.onFloorCol=onFloorCol;
            position = new OnFloorPosition(onFloorRaw, onFloorCol);
        }

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

    }

    /**Floor instance variable **/
    private OnFloorPosition startPosition , actualPosition , nextPosition , prevPosition;



    private int height;

    private Tile[][] field;

    private BotDirection botDirection;

    /************************/

    public Floor(int width , int height , float tileWidth , float tileHeight){
        field = new Tile[width][height];

        this.width = width;
        this.height = height;

        for(int i = 0 ; i<width ; i++)
            for(int j=0;j<height ; j++)
                field[i][j]=new Tile(tileWidth , tileHeight , i , j);


        /**TODO**/
        startPosition = field[0][0].getOnFloorPosition();
        actualPosition = field[0][0].getOnFloorPosition();
        prevPosition=new OnFloorPosition(-1,-1);
        botDirection = BotDirection.getInstance(Direction.VERTICAL_UP);
        nextPosition = new OnFloorPosition(actualPosition.getRaw()+botDirection.getY(),actualPosition.getCol()+botDirection.getX());


    }

    public OnFloorPosition getActualPosition() {
        return actualPosition;
    }
    public OnFloorPosition getNextPosition(){
        return nextPosition;
    }
    public OnFloorPosition getPrevPosition(){
        return prevPosition;
    }
    public int getWidth() {
        return width;
    }

    private int width;

    public int getHeight() {
        return height;
    }

    public Direction getBotDirection(){return botDirection.getDirection();}

    public void updateBotPosition(){
        /** startPosition.setOnFloorPositio(); TODO**/
        prevPosition.setOnFloorPosition(actualPosition.getRaw(),actualPosition.getCol());
        actualPosition.setOnFloorPosition(nextPosition.getRaw(),nextPosition.getCol());
        nextPosition.setOnFloorPosition((actualPosition.getRaw()+botDirection.getY()),actualPosition.getCol()+botDirection.getX());
    }

    public void updateNextPosition(){
        nextPosition.setOnFloorPosition(actualPosition.getRaw(),actualPosition.getCol());
        nextPosition.setOnFloorPosition((actualPosition.getRaw()+botDirection.getY()),actualPosition.getCol()+botDirection.getX());
    }


    /** SECONDO ME NON DEVE STARE QUI**/
    public TurnDirection chooseNextDirection(){
        if(actualPosition.getRaw()==getWidth()-1 && actualPosition.getCol()<getHeight()-1){
            botDirection.setDirection(Direction.HORIZONTAL_UP);
            return TurnDirection.TURN_RIGHT;
        }
        else {
            if (actualPosition.getRaw() == getWidth()-1 && actualPosition.getCol() == getHeight()-1) {
                botDirection.setDirection(Direction.VERTICAL_DOWN);
                return TurnDirection.TURN_RIGHT;
            }
            else {
                if(actualPosition.getRaw() < getWidth()-1 && actualPosition.getCol() < getHeight()-1){
                    botDirection.setDirection(Direction.HORIZONTAL_DOWN);
                    return TurnDirection.TURN_RIGHT;
                }
                else {
                    botDirection.setDirection(Direction.VERTICAL_UP);
                    return TurnDirection.TURN_RIGHT;
                }
            }
        }
    }

    public TurnDirection chooseNDPrimaProva(){
        if(actualPosition.getRaw()==getWidth()-1 && botDirection.getDirection()==Direction.VERTICAL_UP){
            botDirection.setDirection(Direction.HORIZONTAL_UP);
            return TurnDirection.TURN_RIGHT;
        }
        else{
            if(actualPosition.getRaw()==0 && botDirection.getDirection()==Direction.VERTICAL_DOWN){
                botDirection.setDirection(Direction.HORIZONTAL_UP);
                return TurnDirection.TURN_LEFT;
            }
            else{
                if(actualPosition.getRaw()==getWidth()-1 && botDirection.getDirection()==Direction.HORIZONTAL_UP){
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


    private static class BotDirection{
        private int y = 0, x = 0; Direction direction;
        private BotDirection(Direction d){
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
        U_INVERSION
    }
    public enum Direction{
        HORIZONTAL_UP, /*moving straight change column value*/
        VERTICAL_UP, /*moving straight change raw value*/
        HORIZONTAL_DOWN,
        VERTICAL_DOWN


    }


}