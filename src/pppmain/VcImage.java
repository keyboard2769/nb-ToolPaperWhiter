/*
 * Copyright (C) 2020 Key Parker from K.I.C
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package pppmain;

import kosui.ppputil.VcConst;
import kosui.ppputil.VcStringUtility;
import processing.core.PApplet;
import processing.core.PImage;

public final class VcImage {
  
  static private String cmFileLocation = "";
  static private int cmX = 0;
  static private int cmY = 0;
  static private boolean cmApplied = true;
  static private float cmSizeFactor = 1.0f;
  static private float cmThresholdFactor = 0.66f;
  
  static private PImage cmOringin  = null;
  static private PImage cmFiltered = null;
  static private int cmFilteredColor = 0xFF000000;
  
  //===
  
  public static void ccUpdate(){
    if(cmFiltered==null || cmOringin==null){
      MainSketch.ccGetSketch().pushStyle();{
        MainSketch.ccGetSketch().textAlign(PApplet.CENTER);
        MainSketch.ccGetSketch().fill(0xFF);
        MainSketch.ccGetSketch().text(
          MainSketch.C_MESSAGE,
          MainSketch.ccGetSketch().width/2,
          MainSketch.ccGetSketch().height/2
        );
      }MainSketch.ccGetSketch().popStyle();
    }else{
      MainSketch.ccGetSketch().image(
        cmApplied?cmFiltered:cmOringin,
        (float)cmX,(float)cmY,
        ((float)(cmOringin.width )) * cmSizeFactor,
        ((float)(cmOringin.height)) * cmSizeFactor
      );
    }//..?
  }//+++
  
  //===
  
  public static final void ccReloadImage(PImage pxTarget){
    if(pxTarget==null){return;}
    cmOringin=null;
    cmFiltered=null;
    /* [log]
    System.out.println(String.format(
      "%d (%d x %d) %d", 
      pxTarget.format,pxTarget.width,pxTarget.height,pxTarget.loaded?1:0
    ));
    */
    cmOringin=pxTarget.get();
    cmFiltered=cmOringin.get();
    cmFiltered.filter(PApplet.THRESHOLD,cmThresholdFactor);
  }//+++
  
  public static final void ccReloadImage(){
    cmOringin=null;
    cmFiltered=null;
    cmOringin=MainSketch.ccGetSketch().loadImage(cmFileLocation);
    cmFiltered=cmOringin.get();
    cmFiltered.filter(PApplet.THRESHOLD,cmThresholdFactor);
  }//+++
  
  public static final void ccRenewFiltered(){
    if(cmOringin==null){return;}
    cmFiltered=null;
    cmFiltered=cmOringin.get();
    cmFiltered.filter(PApplet.THRESHOLD,cmThresholdFactor);
    cmFiltered.loadPixels();
    for(int i=0, s=cmFiltered.pixels.length;i<s;i++){
      int lpBuf = cmFiltered.pixels[i];
      cmFiltered.pixels[i]=(lpBuf < 0xFF777777)?
        (cmFilteredColor)
        :
        (lpBuf);
    }//..~
    cmFiltered.updatePixels();
  }//+++
  
  public static final void ccExportImage(String pxLocation){
    if(!VcConst.ccIsValidString(pxLocation)){return;}
    if(cmApplied){
      cmFiltered.save(pxLocation);
    }else{
      cmOringin.save(pxLocation);
    }//..?
  }//+++
  
  public static final void ccExportFiltered(String pxLocation){
    if(!VcConst.ccIsValidString(pxLocation)){return;}
  }//+++
  
  //===
  
  public static final void ccSetLocation(int pxX, int pxY){
    cmX = pxX;
    cmY = pxY;
  }//+++
  
  public static final void ccSetFilteredColor(int pxColor){
    cmFilteredColor=pxColor;
  }//+++
  
  public static final void ccShiftLocation(int pxOffsetX, int pxOffsetY){
    cmX += pxOffsetX;
    cmY += pxOffsetY;
  }//+++
  
  public static final void ccShiftSizeFactor(float pxStep){
    cmSizeFactor+=pxStep;
    cmSizeFactor=PApplet.constrain(cmSizeFactor, 0.25f, 2.5f);
  }//+++
  
  synchronized static final void ccSetFileLocation(String pxLocation){
    cmFileLocation=VcStringUtility.ccNulloutString(pxLocation);
  }//+++
  
  synchronized static final void ccSetPaperWhiteModeApplied(boolean pxStatus){
    cmApplied=pxStatus;
  }//+++
  
  synchronized static final void ccSetThresholdFactor(float pxValue){
    cmThresholdFactor = PApplet.constrain(pxValue, 0.01f, 0.99f);
    ccRenewFiltered();
  }//+++
  
  synchronized static final void ccShiftThresholdFactor(float pxOffset){
    cmThresholdFactor += pxOffset;
    cmThresholdFactor = PApplet.constrain(cmThresholdFactor, 0.01f, 0.99f);
    ccRenewFiltered();
  }//+++
  
  //===
  
  public static final int ccGetX(){return cmX;}
  
  public static final int ccGetY(){return cmY;}
  
  public static final float ccGetThresholdFactor(){return cmThresholdFactor;}
  
}//***eof
