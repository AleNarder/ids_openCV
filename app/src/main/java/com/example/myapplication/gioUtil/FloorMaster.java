package com.example.myapplication.gioUtil;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FloorMaster {
    Floor floor;
    public FloorMaster(Floor floor){
        this.floor=floor;
    }


    public Floor getFloor(){return this.floor;}

    public Floor.OnFloorPosition chooseNextPosition (Floor.OnFloorPosition destination) throws Exception {
        if(floor.getActualPosition().compareTo(destination)!=0){
            if(floor.getActualPosition().getRow()!=destination.getRow()){
                if(freeRowRoad(floor.getActualPosition(),destination)){  //TODO per riga e colonna
                    return new Floor.OnFloorPosition(destination.getRow(),floor.getActualPosition().getCol());
                }
            }
            else{
                if(floor.getActualPosition().getCol()!=destination.getCol()){
                    if(freeColRoad(floor.getActualPosition(),destination)){
                        return new Floor.OnFloorPosition(floor.getActualPosition().getRow(),destination.getCol());
                    }
                }
            }
            return chooseOneTileMove(floor.getActualPosition());
        }
        else
            return destination;
    }

    private boolean freeRowRoad(Floor.OnFloorPosition source, Floor.OnFloorPosition destination) {
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getRow()<destination.getRow()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
        }
        else{
            actualPosition=new Floor.OnFloorPosition(destination);
            finalPosition=new Floor.OnFloorPosition(source);
        }

        while(actualPosition.getRow()<finalPosition.getRow()){
            if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            actualPosition.setOnFloorPosition(actualPosition.getRow()+1,actualPosition.getCol());
        }
        return true;
    }

    private boolean freeColRoad(Floor.OnFloorPosition source, Floor.OnFloorPosition destination) {
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getCol()<destination.getCol()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
        }
        else{
            actualPosition=new Floor.OnFloorPosition(destination);
            finalPosition=new Floor.OnFloorPosition(source);
        }
        while(actualPosition.getCol()<finalPosition.getCol()){
            if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            actualPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol()+1);
        }
        return true;

    }

    public Floor.OnFloorPosition chooseOneTileMove(Floor.OnFloorPosition source ) throws Exception {
        List<Floor.Direction> dir = new ArrayList<>();
        Floor.OnFloorPosition temp = new Floor.OnFloorPosition(-1,-1);
        Floor.BotDirection bt = new Floor.BotDirection(floor.getBotDirection());
        dir.add(Floor.Direction.HORIZONTAL_DOWN); dir.add(Floor.Direction.HORIZONTAL_UP); dir.add(Floor.Direction.VERTICAL_DOWN); dir.add(Floor.Direction.VERTICAL_UP);
        for(int i=0;i<dir.size();i++){
            bt.setDirection(dir.get(i));
            temp.setOnFloorPosition(source.getRow()+bt.getY(),source.getCol()+bt.getX());
            if(!notThatWay(temp,bt) && !floor.getMine(temp))
                return temp;
        }
        throw new Exception(); //TODO
    }

    private boolean notThatWay(Floor.OnFloorPosition pos , Floor.BotDirection bt){
        Floor.OnFloorPosition temp=new Floor.OnFloorPosition(pos.getRow(),pos.getCol());
        temp.setOnFloorPosition(temp.getRow()+bt.getY(),temp.getCol()+bt.getY());

        if((temp.getRow()<0 || temp.getRow()>=floor.getWidth()) || (temp.getCol()<0 || temp.getCol()>= floor.getHeight()))
            return true;
        return false;
    }

    public Floor.TurnDirection chooseNextDirection(){
        if(floor.getActualPosition().getRow()==floor.getWidth()-1 && floor.getActualPosition().getCol()<floor.getHeight()-1){
            floor.getBot().setDirection(Floor.Direction.HORIZONTAL_UP);
            return Floor.TurnDirection.TURN_RIGHT;
        }
        else {
            if (floor.getActualPosition().getRow() == floor.getWidth()-1 && floor.getActualPosition().getCol() == floor.getHeight()-1) {
                floor.getBot().setDirection(Floor.Direction.VERTICAL_DOWN);
                return Floor.TurnDirection.TURN_RIGHT;
            }
            else {
                if(floor.getActualPosition().getRow() < floor.getWidth()-1 && floor.getActualPosition().getCol() < floor.getHeight()-1){
                    floor.getBot().setDirection(Floor.Direction.HORIZONTAL_DOWN);
                    return Floor.TurnDirection.TURN_RIGHT;
                }
                else {
                    floor.getBot().setDirection(Floor.Direction.VERTICAL_UP);
                    return Floor.TurnDirection.TURN_RIGHT;
                }
            }
        }
    }


    public Floor.OnFloorPosition chooseNextPosition() throws AllPositionVisited{
        if(!floor.rawVisited(floor.getActualPosition().getRow())) {
            Log.e("FLOOR : ", "ROW CASE");
            return new Floor.OnFloorPosition(floor.getActualPosition().getRow(),Math.min(floor.getHeight()-1-floor.getActualPosition().getCol(),floor.getHeight()-1));

        }
        else {
            if(!floor.colVisited((floor.getActualPosition().getCol()))){
                Log.e("FLOOR : ", "COL CASE");
                return new Floor.OnFloorPosition(Math.min(floor.getWidth()-1-floor.getActualPosition().getRow(),floor.getWidth()-1),floor.getActualPosition().getCol());
            }
            else{ //TODO
                int raw=chooseNextRaw();
                if(raw!=-1) {
                    if(floor.getActualPosition().getCol()==0 || floor.getActualPosition().getCol()==floor.getHeight()-1)
                        return new Floor.OnFloorPosition(raw, floor.getActualPosition().getCol());
                    else {
                        if(floor.getHeight()-1-floor.getActualPosition().getCol()>floor.getActualPosition().getCol())
                            return new Floor.OnFloorPosition(floor.getActualPosition().getRow(), 0);
                        else
                            return new Floor.OnFloorPosition((floor.getActualPosition()).getRow(),floor.getHeight()-1);

                    }
                }
            }
        }
        throw new AllPositionVisited();
    }

    public int chooseNextRaw() {
        for(int i=0;i<floor.getWidth();i++)
            if(!floor.rawVisited(i)) return i;
        return -1;
    }

    public Floor.Direction changeBotDirection(Floor.OnFloorPosition newPosition) {
        int rawDiff=newPosition.getRow()-floor.getActualPosition().getRow();
        int colDiff=newPosition.getCol()-floor.getActualPosition().getCol();
        if(rawDiff!=0){
            if(rawDiff<0){
                //botDirection.setDirection(Direction.VERTICAL_DOWN);
                return Floor.Direction.VERTICAL_DOWN;
            }
            else {
                //botDirection.setDirection(Direction.VERTICAL_UP);
                return Floor.Direction.VERTICAL_UP;
            }
        }
        else{
            if(colDiff<0){
                // botDirection.setDirection(Direction.HORIZONTAL_DOWN);
                return Floor.Direction.HORIZONTAL_DOWN;
            }
            else{
                // botDirection.setDirection(Direction.HORIZONTAL_UP);
                return Floor.Direction.HORIZONTAL_UP;
            }
        }

    }

    public Floor.TurnDirection turnDirection(Floor.Direction d){
        switch(floor.getBot().getDirection()){
            case HORIZONTAL_DOWN:
                switch(d){
                    case HORIZONTAL_DOWN:
                        return Floor.TurnDirection.NO_TURN;
                    case HORIZONTAL_UP:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.U_INVERSION;
                    case VERTICAL_UP:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_RIGHT;
                    case VERTICAL_DOWN:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_LEFT;
                }
            case HORIZONTAL_UP:
                switch(d){
                    case HORIZONTAL_DOWN:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.U_INVERSION;
                    case HORIZONTAL_UP:
                        return Floor.TurnDirection.NO_TURN;
                    case VERTICAL_UP:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_LEFT;
                    case VERTICAL_DOWN:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_RIGHT;
                }
            case VERTICAL_UP:
                switch(d){
                    case HORIZONTAL_DOWN:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_LEFT;
                    case HORIZONTAL_UP:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_RIGHT;
                    case VERTICAL_UP:
                        return Floor.TurnDirection.NO_TURN;
                    case VERTICAL_DOWN:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.U_INVERSION;
                }
            case VERTICAL_DOWN:
                switch(d){
                    case HORIZONTAL_DOWN:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_RIGHT;
                    case HORIZONTAL_UP:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_LEFT;
                    case VERTICAL_UP:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.U_INVERSION;
                    case VERTICAL_DOWN:
                        return Floor.TurnDirection.NO_TURN;
                }
            default:return Floor.TurnDirection.NO_TURN;
        }
    }

    public void updateBotPosition(){floor.updateBotPosition();}
    public void updateNextPosition(){floor.updateNextPosition();}

    public boolean rowVisitedInv(Floor.OnFloorPosition source , Floor.OnFloorPosition destination ){
        if(source.compareTo(destination)==0)
            return true;
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getRow()<destination.getRow()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
        }
        else{
            actualPosition=new Floor.OnFloorPosition(destination);
            finalPosition=new Floor.OnFloorPosition(source);
        }

        while(actualPosition.getRow()<finalPosition.getRow()){
            if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            actualPosition.setOnFloorPosition(actualPosition.getRow()+1,actualPosition.getCol());
        }
        return colVisitedInv(actualPosition,finalPosition);
    }

    private boolean colVisitedInv(Floor.OnFloorPosition source, Floor.OnFloorPosition destination) {
        if(source.compareTo(destination)==0)
            return true;
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getCol()<destination.getCol()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
        }
        else{
            actualPosition=new Floor.OnFloorPosition(destination);
            finalPosition=new Floor.OnFloorPosition(source);
        }
        while(actualPosition.getCol()<finalPosition.getCol()){
            if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            actualPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol()+1);
        }
        return rowVisitedInv(actualPosition,finalPosition);
    }

    public Floor.OnFloorPosition chooseNextPositionInv (Floor.OnFloorPosition destination)  {
        if(floor.getActualPosition().compareTo(destination)!=0){
            if(floor.getActualPosition().getRow()!=destination.getRow()){
                if(rowVisitedInv(floor.getActualPosition(),destination)){  //TODO per riga e colonna
                    return new Floor.OnFloorPosition(destination.getRow(),floor.getActualPosition().getCol());
                }
            }
            else{
                if(floor.getActualPosition().getCol()!=destination.getCol()){
                    if(colVisitedInv(floor.getActualPosition(),destination)){
                        return new Floor.OnFloorPosition(floor.getActualPosition().getRow(),destination.getCol());
                    }
                }
            }
            return null;
        }
        else
            return destination;
    }

    public static class AllPositionVisited extends Exception{} //TODO : printstack
}
