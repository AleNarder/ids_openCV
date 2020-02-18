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

    public Floor.OnFloorPosition chooseNextPosition (Floor.OnFloorPosition destination){
        if(floor.getActualPosition().compareTo(destination)!=0){
            if(floor.getActualPosition().getRow()!=destination.getRow()){
                if(freeRowRoadaux(floor.getActualPosition(),destination)){  //TODO per riga e colonna
                    Log.e("FLOOR MASTER : ", "free row");
                    return new Floor.OnFloorPosition(destination.getRow(),floor.getActualPosition().getCol());
                }
            }

            if(floor.getActualPosition().getCol()!=destination.getCol()){
                    if(freeColRoadaux(floor.getActualPosition(),destination)){
                        Log.e("FLOOR MASTER : ", "free col");

                        return new Floor.OnFloorPosition(floor.getActualPosition().getRow(),destination.getCol());
                    }
            }
            Log.e("FLOOR MASTER : ", "null");

            return null;
        }
        else
            return destination;
    }

    private boolean freeRowRoad(Floor.OnFloorPosition source, Floor.OnFloorPosition destination) {
        if(source.compareTo(destination)==0) {
            Log.e("FLOOR MASTER :"," RESSSSSS:"+destination.getRow()+" "+destination.getCol());
            return true;
        }
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getRow()<destination.getRow()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
            if(!floor.isSafe(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getRow()!=finalPosition.getRow()){
                actualPosition.setOnFloorPosition(actualPosition.getRow()+1,actualPosition.getCol());
                if(!floor.isSafe(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return freeColRoad(actualPosition,finalPosition);

        }
        else{
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition=new Floor.OnFloorPosition(destination);
            if(!floor.isSafe(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getRow()!=finalPosition.getRow()){
                actualPosition.setOnFloorPosition(actualPosition.getRow()-1,actualPosition.getCol());
                if(!floor.isSafe(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return freeColRoad(actualPosition,finalPosition);
        }

    }

    private boolean freeColRoad(Floor.OnFloorPosition source, Floor.OnFloorPosition destination) {
        if(source.compareTo(destination)==0) {
            Log.e("FLOOR MASTER :"," RESSSSSS:"+destination.getRow()+" "+destination.getCol());

            return true;
        }
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getCol()<destination.getCol()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
            if(!floor.isSafe(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getCol()!=finalPosition.getCol()){
                actualPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol()+1);
                if(!floor.isSafe(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return freeRowRoad(actualPosition,finalPosition);
        }
        else{
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition=new Floor.OnFloorPosition(destination);
            if(!floor.isSafe(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getCol()!=finalPosition.getCol()){
                actualPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol()-1);
                if(!floor.isSafe(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return freeRowRoad(actualPosition,finalPosition);
        }

    }

    private boolean freeRowRoadaux(Floor.OnFloorPosition source, Floor.OnFloorPosition destination) {
        if(source.compareTo(destination)==0) {
            Log.e("FLOOR MASTER :"," RESSSSSS:"+destination.getRow()+" "+destination.getCol());
            return true;
        }
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getRow()<destination.getRow()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
            if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getRow()!=finalPosition.getRow()){
                actualPosition.setOnFloorPosition(actualPosition.getRow()+1,actualPosition.getCol());
                if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return freeColRoad(actualPosition,finalPosition);

        }
        else{
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition=new Floor.OnFloorPosition(destination);
            if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getRow()!=finalPosition.getRow()){
                actualPosition.setOnFloorPosition(actualPosition.getRow()-1,actualPosition.getCol());
                if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return freeColRoad(actualPosition,finalPosition);
        }

    }

    private boolean freeColRoadaux(Floor.OnFloorPosition source, Floor.OnFloorPosition destination) {
        if(source.compareTo(destination)==0) {
            Log.e("FLOOR MASTER :"," RESSSSSS:"+destination.getRow()+" "+destination.getCol());

            return true;
        }
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getCol()<destination.getCol()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
            if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getCol()!=finalPosition.getCol()){
                actualPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol()+1);
                if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return freeRowRoad(actualPosition,finalPosition);
        }
        else{
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition=new Floor.OnFloorPosition(destination);
            if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getCol()!=finalPosition.getCol()){
                actualPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol()-1);
                if(floor.getMine(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return freeRowRoad(actualPosition,finalPosition);
        }

    }

    public void destinationMeth(Floor.OnFloorPosition s , Floor.OnFloorPosition d , Floor.OnFloorPosition res){
        Floor.OnFloorPosition source = new Floor.OnFloorPosition(s);
        Floor.OnFloorPosition destination = new Floor.OnFloorPosition(d);
        floor.setChecked(source,true);
        floor.setChecked(destination,true);
        Floor.OnFloorPosition temp;

        if(freeRowRoad(source,destination)){
            res.setOnFloorPosition(destination.getRow(),destination.getCol());

        }
        else{
            if(freeColRoad(source,destination)){
                res.setOnFloorPosition(destination.getRow(),destination.getCol());
            }
            else{
                temp = new Floor.OnFloorPosition(destination.getRow()+1,destination.getCol());
                if(floor.isSafe(temp) && temp.compareTo(source)!=0 && res.compareTo(new Floor.OnFloorPosition(-1,-1))==0 && !floor.getChecked(temp)) {
                    Log.e("FLOOR MASTER :"," PROVO POS :"+temp.getRow()+" "+temp.getCol());
                    destinationMeth(source, temp, res);
                }

                temp=new Floor.OnFloorPosition(destination.getRow()-1,destination.getCol());
                if(floor.isSafe(temp) && temp.compareTo(source)!=0 && res.compareTo(new Floor.OnFloorPosition(-1,-1))==0 && !floor.getChecked(temp)) {
                    Log.e("FLOOR MASTER :"," PROVO POS :"+temp.getRow()+" "+temp.getCol());

                    destinationMeth(source, temp, res);
                }

                temp = new Floor.OnFloorPosition(destination.getRow(),destination.getCol()+1);
                if(floor.isSafe(temp) && temp.compareTo(source)!=0 && res.compareTo(new Floor.OnFloorPosition(-1,-1))==0 && !floor.getChecked(temp)) {
                    Log.e("FLOOR MASTER :", " PROVO POS :" + temp.getRow() + " " + temp.getCol());

                    destinationMeth(source, temp, res);
                }

                temp = new Floor.OnFloorPosition(destination.getRow(),destination.getCol()-1);
                if(floor.isSafe(temp) && temp.compareTo(source)!=0 && res.compareTo(new Floor.OnFloorPosition(-1,-1))==0 && !floor.getChecked(temp)) {
                    Log.e("FLOOR MASTER :"," PROVO POS :"+temp.getRow()+" "+temp.getCol());
                    destinationMeth(source, temp, res);
                }

                else return;
            }
        }
    }

    private Floor.Direction bestDirection(Floor.OnFloorPosition source , Floor.OnFloorPosition destination){
        int rowDiff = source.getRow()-destination.getRow();
        if(rowDiff>0)
            return Floor.Direction.VERTICAL_DOWN;
        if(rowDiff<0)
            return Floor.Direction.VERTICAL_UP;
        int colDiff = source.getCol()-destination.getCol();
        if(colDiff>0)
            return Floor.Direction.HORIZONTAL_DOWN;
        if(colDiff<0)
            return Floor.Direction.HORIZONTAL_UP;
        else
            return null;
    }
    private boolean notThatWay(Floor.OnFloorPosition pos){
        Floor.OnFloorPosition temp=new Floor.OnFloorPosition(pos.getRow(),pos.getCol());
        //temp.setOnFloorPosition(temp.getRow()+bt.getY(),temp.getCol()+bt.getY());

        if((temp.getRow()<0 || temp.getRow()>=floor.getWidth()) || (temp.getCol()<0 || temp.getCol()>= floor.getHeight()))
            return true;
        return false;
    }

    public Floor.TurnDirection chooseNextDirection(){ //TODO : eliminare? non credo sera
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


    public Floor.OnFloorPosition chooseNextPosition() throws AllPositionVisited {
        return chooseNextPosition2();
    }

   public Floor.OnFloorPosition chooseNextPosition1() throws AllPositionVisited{
        if(!floor.rawVisited(floor.getActualPosition().getRow())) {
            Log.e("FLOOR : ", "ROW CASE");
            int col;
            if(floor.getActualPosition().getCol()==0)
                col=floor.getHeight()-1;
            else
                if(floor.getActualPosition().getCol()==floor.getHeight()-1)
                    col=0;
                else
                    col=0;
            return new Floor.OnFloorPosition(floor.getActualPosition().getRow(),col);
        }
        else {
            if(!floor.colVisited((floor.getActualPosition().getCol()))){
                Log.e("FLOOR : ", "COL CASE");
                int row;
                if(floor.getActualPosition().getRow()==0)
                    row=floor.getWidth()-1;
                else
                    if(floor.getActualPosition().getRow()==floor.getWidth()-1)
                        row=0;
                    else
                        row=0;
                return new Floor.OnFloorPosition(row,floor.getActualPosition().getCol());
            }
            else{
                int row=chooseNextRaw();
                if(row!=-1) {
                    Log.e("FLOORMASTER:"," riga scelta = "+row);
                    /*if(floor.getActualPosition().getCol()==0 || floor.getActualPosition().getCol()==floor.getHeight()-1)
                        return new Floor.OnFloorPosition(row, floor.getActualPosition().getCol());
                    else {
                        if(floor.getHeight()-1-floor.getActualPosition().getCol()>floor.getActualPosition().getCol())
                            return new Floor.OnFloorPosition(row, 0);
                        else
                            return new Floor.OnFloorPosition(row,floor.getHeight()-1);

                    }*/
                    return new Floor.OnFloorPosition(row,floor.getActualPosition().getCol());
                }
            }
        }
        throw new AllPositionVisited();
    }

    public Floor.OnFloorPosition chooseNextPosition2() throws AllPositionVisited{
        if(!floor.rawVisited(floor.getActualPosition().getRow())) {
            Log.e("FLOOR : ", "ROW CASE");
            int col;
            if(floor.getActualPosition().getCol()==0)
                col=floor.getHeight()-1;
            else {
                if (floor.getActualPosition().getCol() == floor.getHeight() - 1)
                    col = 0;
                else
                    col = floor.getHeight() - 1;
            }
            return new Floor.OnFloorPosition(floor.getActualPosition().getRow(),col);
        }
        else {
                int row=chooseNextRaw();
                if(row!=-1) {
                    Log.e("FLOORMASTER:"," riga scelta = "+row);
                    return new Floor.OnFloorPosition(row,floor.getActualPosition().getCol());
                }
            }
        throw new AllPositionVisited();
    }

    public int chooseNextRaw() {
        for(int i=0;i<floor.getWidth();i++)
            if(!floor.rawVisited(i))
                return i;
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

    public Floor.TurnDirection turnDirectionDispatch(Floor.Direction d){
        if(floor.getAxisSystem()== Floor.Coordinate_System.CARTESIAN)
            return turnDirection(d);
        else
            return turnDirectionMatrix(d);
    }

    public Floor.TurnDirection turnDirectionMatrix(Floor.Direction d){
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
                        return Floor.TurnDirection.TURN_LEFT;
                    case VERTICAL_DOWN:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_RIGHT;
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
                        return Floor.TurnDirection.TURN_RIGHT;
                    case VERTICAL_DOWN:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_LEFT;
                }
            case VERTICAL_UP:
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
                        return Floor.TurnDirection.TURN_LEFT;
                    case HORIZONTAL_UP:
                        floor.getBot().setDirection(d);
                        floor.updateNextPosition();
                        return Floor.TurnDirection.TURN_RIGHT;
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
            if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getRow()!=finalPosition.getRow()){
                actualPosition.setOnFloorPosition(actualPosition.getRow()+1,actualPosition.getCol());
                if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return colVisitedInv(actualPosition,finalPosition);

        }
        else{
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition=new Floor.OnFloorPosition(destination);
            if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getRow()!=finalPosition.getRow()){
                actualPosition.setOnFloorPosition(actualPosition.getRow()-1,actualPosition.getCol());
                if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return colVisitedInv(actualPosition,finalPosition);
        }

    }

    private boolean colVisitedInv(Floor.OnFloorPosition source, Floor.OnFloorPosition destination) {
        if(source.compareTo(destination)==0)
            return true;
        Floor.OnFloorPosition actualPosition , finalPosition;
        if(source.getCol()<destination.getCol()){
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition= new Floor.OnFloorPosition(destination);
            if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getCol()!=finalPosition.getCol()){
                actualPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol()+1);
                if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return rowVisitedInv(actualPosition,finalPosition);
        }
        else{
            actualPosition=new Floor.OnFloorPosition(source);
            finalPosition=new Floor.OnFloorPosition(destination);
            if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                return false;
            while(actualPosition.getCol()!=finalPosition.getCol()){
                actualPosition.setOnFloorPosition(actualPosition.getRow(),actualPosition.getCol()-1);
                if(!floor.getChecked(actualPosition) && actualPosition.compareTo(finalPosition)!=0)
                    return false;
            }
            return rowVisitedInv(actualPosition,finalPosition);
        }

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

    public Floor.Direction oppDir(Floor.Direction dir){
        switch(dir){
            case HORIZONTAL_UP:
                return Floor.Direction.HORIZONTAL_DOWN;
            case HORIZONTAL_DOWN:
                return Floor.Direction.HORIZONTAL_UP;
            case VERTICAL_UP:
                return Floor.Direction.VERTICAL_DOWN;
            case VERTICAL_DOWN:
                return Floor.Direction.VERTICAL_UP;
            default: return null;
        }
    }

    public static class AllPositionVisited extends Exception{} //TODO : printstack
}
