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

import java.awt.Point;
import javax.swing.SwingUtilities;
import kosui.ppplocalui.EcConst;
import kosui.ppplocalui.EcElement;
import kosui.ppputil.VcConst;
import kosui.ppputil.VcLocalCoordinator;
import kosui.ppputil.VcLocalTagger;
import processing.core.PApplet;

public class MainSketch extends PApplet{
  
  static private MainSketch self = null;
  
  static public volatile int pbRoller;
  
  static public final String C_MESSAGE
    = "press right mouse button to bring menu up."+VcConst.C_V_NEWLINE
    + "[w/s] to select camera if there are availables."+VcConst.C_V_NEWLINE
    + "[r] or 'capture' button to take a capture."+VcConst.C_V_NEWLINE
    + "[q] to quit."+VcConst.C_V_NEWLINE
    + "have fun"+VcConst.C_V_NEWLINE;

  //=== overridden
  
  private int pbDragAnchorX = 0;
  private int pbDragAnchorY = 0;
  
  @Override public void setup() {
    
    //-- pre
    size(800,600);
    noSmooth();
    imageMode(CENTER);
    frame.setTitle("Paper Whiter");
    frame.addWindowListener(MainActionManager.ccRefer().cmClosing);
    EcConst.ccSetupSketch(this);
    EcElement.ccSetOwner(this);
    VcLocalCoordinator.ccGetInstance().ccInit(this);
    VcLocalTagger.ccGetInstance().ccInit(this);
    SwingUtilities.invokeLater(MainWindow.O_SETUP);
    self = this;
    
    //-- init
    VcCamera.ccDetect();
    
    //-- binding
    VcLocalCoordinator.ccRegisterKeyTrigger
      (java.awt.event.KeyEvent.VK_Q, MainActionManager.ccRefer().cmQuitting);
    VcLocalCoordinator.ccRegisterKeyTrigger
      (java.awt.event.KeyEvent.VK_R, VcCamera.O_CAMERA_CAPTURING);
    VcLocalCoordinator.ccRegisterKeyTrigger
      (java.awt.event.KeyEvent.VK_W, VcCamera.O_INDEX_INCRECING);
    VcLocalCoordinator.ccRegisterKeyTrigger
      (java.awt.event.KeyEvent.VK_S, VcCamera.O_INDEX_DECRECING);
    
    //-- post
    VcImage.ccSetLocation(width/2, height/2);
    println(".setup $ end");
    
  }//+++

  @Override public void draw() {
    
    //-- pre
    background(0);
    pbRoller++;pbRoller&=0x0F;
    
    //-- logic
    VcLocalCoordinator.ccUpdate();
    
    //-- update
    VcImage.ccUpdate();
    
    //-- tag
    VcLocalTagger.ccTag("camera", VcCamera.ccGetPresent());
    VcLocalTagger.ccTag("roll", pbRoller);
    VcLocalTagger.ccStabilize();
    
  }//+++

  @Override public void keyPressed() {
    VcLocalCoordinator.ccKeyPressed(keyCode);
  }//+++

  @Override public void mouseClicked() {
    if(mouseButton != RIGHT){return;}
    Point lpLeftTop = frame.getLocationOnScreen();
    MainWindow.ccSetLocation(lpLeftTop.x+width+16, lpLeftTop.y);
    SwingUtilities.invokeLater(MainWindow.O_RELOCATION);
  }//+++
  
  @Override public void mouseReleased() {
    if(mouseButton != LEFT){return;}
    pbDragAnchorX = 0;
    pbDragAnchorY = 0;
  }//+++
  
  @Override public void mouseDragged() {
    if(mouseButton != LEFT){return;}
    if(pbDragAnchorX==0 && pbDragAnchorY==0){
      pbDragAnchorX=mouseX-VcImage.ccGetX();
      pbDragAnchorY=mouseY-VcImage.ccGetY();
    }//..?
    int lpRelativeX = mouseX - pbDragAnchorX;
    int lpRelativeY = mouseY - pbDragAnchorY;
    VcImage.ccSetLocation(lpRelativeX, lpRelativeY);
  }//+++

  @Override public void mouseWheel(processing.event.MouseEvent me) {
    int lpCount = -1 * me .getCount();
    VcImage.ccShiftSizeFactor(((float)(lpCount))/10f);
  }//+++
  
  //=== entry
  
  static public final MainSketch ccGetSketch(){return self;}//+++
  
  static public final PApplet ccGetPApplet(){return self;}//+++
  
  static public final String ccGetLastStamp(){return "_2002271042";}//+++
  
  static public void main(String[] passedArgs) {
    PApplet.main(MainSketch.class.getCanonicalName());
  }//!!!

}//***eof
