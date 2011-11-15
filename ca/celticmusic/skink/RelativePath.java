//
//  RelativePath.java
//  5 Line Skink
//
//  Created by wil macaulay on Mon Aug 11 2003.
//  Copyright (c) 2003 Wil Macaulay All rights reserved.
//

/**
 * wrap a GeneralPath to support relative drawing ops as per PostScript
 *
 * @author Wil Macaulay
 * @version 1.0f1 11 Aug 2003
 * @see G2DRenderer
 * @since Aug 2003
 *
 */
 
package ca.celticmusic.skink;
import java.awt.geom.GeneralPath;

public class RelativePath {
    private GeneralPath _gp;
    private float _xstart,_ystart,_xinit,_yinit;
    private float _xscale,_yscale;
    
void init(float x, float y, GeneralPath gp){ 
    _gp = gp;
    _xinit = x;
    _yinit = -y;
    rOrigin();

    }
    
void init(double x, double y, GeneralPath gp){
    init((float)x,(float)y,gp)  ;
    }  

/**
 * equivalent to dx0 dy0 dx1 dy1 dx2 dy2 rcurveto
 */
                
void rcurveTo(
    float dx0, float dy0, 
    float dx1, float dy1,
    float dx2, float dy2
    )  
    // because PS has y increasing up, reverse the sign of dy
    {
    _gp.curveTo(_xstart+(dx0*_xscale),_ystart-(dy0*_yscale),
               _xstart+(dx1*_xscale),_ystart-(dy1*_yscale),
               _xstart+(dx2*_xscale),_ystart-(dy2*_yscale));
    _xstart += (dx2*_xscale);
    _ystart -= (dy2*_yscale);           
    }            

// interface with doubles	
void rcurveTo(
    double dx0, double dy0,
    double dx1, double dy1,
    double dx2, double dy2){
    rcurveTo((float)dx0,(float)dy0,(float)dx1,(float)dy1,(float)dx2,(float)dy2);
    }
// relative move
void rmoveTo( double dx0, double dy0){
    rmoveTo((float)dx0,(float)dy0);
    }

void rmoveTo( float dx0, float dy0) {
    _gp.moveTo(_xstart+dx0*_xscale,_ystart-dy0*_yscale);
    _xstart += dx0*_xscale;
    _ystart -= dy0*_yscale;  
    }  
// relative line
void rlineTo( float dx0, float dy0) {
    _gp.lineTo(_xstart+dx0*_xscale,_ystart-dy0*_yscale);
    _xstart += dx0*_xscale;
    _ystart -= dy0*_yscale;  
    }  
void rlineTo( double dx0, double dy0){
    rlineTo((float)dx0,(float)dy0);
    }
void rscale(float xscale,float yscale) {
	_xscale = xscale;
	_yscale = yscale;
}
void rOrigin(){
	_xstart = _xinit;
	_ystart = _yinit;
	_xscale = _yscale = 1.0f;
    _gp.moveTo(_xstart,_ystart);
}

}
