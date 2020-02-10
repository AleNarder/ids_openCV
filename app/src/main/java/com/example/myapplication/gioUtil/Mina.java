package com.example.myapplication.gioUtil;

public class Mina {
    public Floor.OnFloorPosition position;
    public String color;

    public Mina(Floor.OnFloorPosition pos , String col){
        position=pos;
        color=col;
    }
    public Floor.OnFloorPosition getPosition(){return position;}
    public String getColor(){return color;}

}