package it.unive.dais.legodroid.lib.gioUtil;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class Floor {



    public  static class OnFloorPosition implements Comparable<OnFloorPosition>{
        private int i , j ;
        public OnFloorPosition(int i , int j){this.i=i; this.j = j;}
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
    }

    private static class Tile {
        private float width , height;
        private int onFloorRaw , onFloorCol;
        private OnFloorPosition position;
        private boolean mine;
        private boolean checked;


        public Tile ( float width , float height , int onFloorRaw , int onFloorCol){
            setHeight(height);
            setWidth(width);
            this.onFloorRaw = onFloorRaw;
            this.onFloorCol=onFloorCol;
            position = new OnFloorPosition(onFloorRaw, onFloorCol);
            checked=false;
        }

        public boolean getChecked(){return checked;}
        public void setChecked(boolean b){checked = b;}

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
    public TurnDirection chooseNextDirection(){
        if(actualPosition.getRow()==getWidth()-1 && actualPosition.getCol()<getHeight()-1){
            botDirection.setDirection(Direction.HORIZONTAL_UP);
            return TurnDirection.TURN_RIGHT;
        }
        else {
            if (actualPosition.getRow() == getWidth()-1 && actualPosition.getCol() == getHeight()-1) {
                botDirection.setDirection(Direction.VERTICAL_DOWN);
                return TurnDirection.TURN_RIGHT;
            }
            else {
                if(actualPosition.getRow() < getWidth()-1 && actualPosition.getCol() < getHeight()-1){
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
            for(int i=0;i<getHeight();i++)
                if(!field[i][col].checked)
                    return false;
            return true;
        }
        return true;
    }

    public int chooseNextRaw() {
        for(int i=0;i<getWidth();i++)
            if(!rawVisited(i)) return i;
        return -1;
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

    public OnFloorPosition chooseNextPosition(){
        if(!rawVisited(actualPosition.getRow())) {
            Log.e("FLOOR : ", "ROW CASE");
            return new OnFloorPosition(actualPosition.getRow(),Math.min(getHeight()-1-actualPosition.getCol(),getHeight()-1));

        }
        else {
            if(!colVisited((actualPosition.getCol()))){
                Log.e("FLOOR : ", "COL CASE");
                return new OnFloorPosition(Math.min(getWidth()-1-actualPosition.getRow(),getWidth()-1),actualPosition.getCol());
            }
            else{ //TODO
                int raw=chooseNextRaw();
                if(raw!=-1) {
                    if(actualPosition.getCol()==0 || actualPosition.getCol()==getHeight()-1)
                        return new OnFloorPosition(raw, actualPosition.getCol());
                    else {
                        if(getHeight()-1-actualPosition.getCol()>actualPosition.getCol())
                            return new OnFloorPosition(actualPosition.getRow(), 0);
                        else
                            return new OnFloorPosition((actualPosition).getRow(),getHeight()-1);

                    }
                }
            }
        }
        return null;
    }

    public Direction changeBotDirection(OnFloorPosition newPosition) {
        int rawDiff=newPosition.getRow()-actualPosition.getRow();
        int colDiff=newPosition.getCol()-actualPosition.getCol();
        if(rawDiff!=0){
            if(rawDiff<0){
                //botDirection.setDirection(Direction.VERTICAL_DOWN);
                return Direction.VERTICAL_DOWN;
            }
            else {
                //botDirection.setDirection(Direction.VERTICAL_UP);
                return Direction.VERTICAL_UP;
            }
        }
        else{
            if(colDiff<0){
                // botDirection.setDirection(Direction.HORIZONTAL_DOWN);
                return Direction.HORIZONTAL_DOWN;
            }
            else{
                // botDirection.setDirection(Direction.HORIZONTAL_UP);
                return Direction.HORIZONTAL_UP;
            }
        }

    }

    public TurnDirection turnDirection(Direction d){
        switch(botDirection.getDirection()){
            case HORIZONTAL_DOWN:
                switch(d){
                    case HORIZONTAL_DOWN:
                        return TurnDirection.NO_TURN;
                    case HORIZONTAL_UP:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.U_INVERSION;
                    case VERTICAL_UP:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.TURN_RIGHT;
                    case VERTICAL_DOWN:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.TURN_LEFT;
                }
            case HORIZONTAL_UP:
                switch(d){
                    case HORIZONTAL_DOWN:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.U_INVERSION;
                    case HORIZONTAL_UP:
                        return TurnDirection.NO_TURN;
                    case VERTICAL_UP:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.TURN_LEFT;
                    case VERTICAL_DOWN:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.TURN_RIGHT;
                }
            case VERTICAL_UP:
                switch(d){
                    case HORIZONTAL_DOWN:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.TURN_LEFT;
                    case HORIZONTAL_UP:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.TURN_RIGHT;
                    case VERTICAL_UP:
                        return TurnDirection.NO_TURN;
                    case VERTICAL_DOWN:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.U_INVERSION;
                }
            case VERTICAL_DOWN:
                switch(d){
                    case HORIZONTAL_DOWN:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.TURN_RIGHT;
                    case HORIZONTAL_UP:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.TURN_LEFT;
                    case VERTICAL_UP:
                        botDirection.setDirection(d);
                        updateNextPosition();
                        return TurnDirection.U_INVERSION;
                    case VERTICAL_DOWN:
                        return TurnDirection.NO_TURN;
                }
            default:return TurnDirection.NO_TURN;
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

