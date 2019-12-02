package com.example.myapplication;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class McLovinFinder {

    private String[] colorNames = {"red", "blue", "yellow"};
    private int[] red = {0, 18,145, 184};
    private int[] blue = {25, 32,105, 120};
    private int[] yellow = {20, 30, 20, 30};
    private int[][] colors = {red, blue, yellow};
    private Mat frame;
    private boolean debug;
    private boolean ready;
    private int clock;
    private List<Integer> queue;
    private Iterator<Integer> it;

    public McLovinFinder(){
        this.frame = frame;
        this.debug = false;
        this.queue = new LinkedList<>();
        for(int i=0; i<3; i++){
            this.queue.add(i);
        }
        this.it = queue.iterator();
        this.clock = this.it.next().intValue();
        this.ready = false;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    private void updateClock(){
        if(!this.it.hasNext()){
            it = queue.iterator();
        }
        this.clock = it.next().intValue();
    }

    public void reduceQueue(){
        this.it.remove();
    }

    public void setColorValues(String color, int[] values){
        int i = 0;
        color = color.toLowerCase();
        while (! this.colorNames[i].equals(color)){
            i++;
        }
        if(values.length==4){
            this.colors[i] = values;
        }
    }


    public ArrayList<Ball> FindBalls(Mat frame){
        Mat hsv = new Mat();
        Mat lower = new Mat();
        Mat upper = new Mat();
        Mat circles = new Mat();
        ArrayList<Ball> balls = new ArrayList<>();
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_BGR2HSV);
        Core.inRange(hsv, new Scalar(this.colors[this.clock][0], 100, 100), new Scalar(this.colors[this.clock][1], 255, 255), lower);
        Core.inRange(hsv, new Scalar(this.colors[this.clock][2], 100, 100), new Scalar(this.colors[this.clock][3], 255, 255), upper);
        Core.addWeighted(lower, 1.0, upper, 1.0, 0.0, frame);
        Imgproc.GaussianBlur(frame, frame,  new Size(7,7),3);
        Imgproc.HoughCircles(frame, circles, Imgproc.CV_HOUGH_GRADIENT, 1, frame.rows()/8, 100, 20, 15,50);
        if(circles.cols()==0){
            this.updateClock();
        }else{
            for(int i=0; i<circles.cols(); i++){
                double[] parameters = circles.get(0,i);
                double x = parameters[0];
                double y = parameters[1];
                Point center = new Point(x,y);
                int r = (int)parameters[2];
                balls.add(new Ball(center, r, this.colorNames[this.clock]));
                if(this.debug){
                    Imgproc.circle(frame ,center,r, new Scalar(255,0,0),3);
                }
            }
        }
        return balls;
    }
}



