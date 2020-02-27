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

import kosui.ppplocalui.EiTriggerable;
import processing.core.PApplet;
import processing.data.StringList;
import processing.video.Capture;

public class VcCamera {
  
  private static Capture cmCamera = null;
  private static final StringList O_CAM_DICT = new StringList();
  private static int cmIndex=0;
  private static String cmPresent="<undeteted>";
  
  public static final void ccDetect(){
    O_CAM_DICT.clear();
    String[] lpDesAvailable=null;
    try {
      lpDesAvailable = Capture.list();
    } catch (Exception e) {
      System.err.println("pppmain.VcCamera.ccDetect()");
      lpDesAvailable=null;
    }//..?
    if(lpDesAvailable==null){return;}
    if(lpDesAvailable.length<1){return;}
    for(String it : lpDesAvailable){O_CAM_DICT.append(it);}//..~
    ssRefreshPresent();
  }//+++
  
  public static final boolean ccHasAvailables(){
    return O_CAM_DICT.size()>0;
  }//+++
  
  public static final void ccShiftCameraIndex(int pxOffset){
    if(!ccHasAvailables()){return;}
    if(cmCamera!=null){return;}
    cmIndex += pxOffset;
    cmIndex = PApplet.constrain(cmIndex, 0, O_CAM_DICT.size()-1);
    ssRefreshPresent();
  }//+++
  
  public static final EiTriggerable O_CAMERA_OPENING = new EiTriggerable() {
    @Override public void ccTrigger() {
      if(!ccHasAvailables()){return;}
      if(cmCamera!=null){return;}
      String[] lpDesAvailable=Capture.list();
      if(lpDesAvailable==null){return;}
      if(lpDesAvailable.length<1){return;}
      cmCamera = new Capture(
        MainSketch.ccGetPApplet(),
        O_CAM_DICT.get(cmIndex)
      );
      cmCamera.start();
    }//+++
  };//***
  
  public static final EiTriggerable O_CAMERA_CLOSING = new EiTriggerable() {
    @Override public void ccTrigger() {
      if(cmCamera == null){return;}
      cmCamera.stop();
      cmCamera.dispose();
      cmCamera = null;
    }//+++
  };//***
  
  public static final EiTriggerable O_CAMERA_CAPTURING = new EiTriggerable() {
    @Override public void ccTrigger() {
      if(cmCamera == null){return;}
      if(cmCamera.available()){
        cmCamera.read();
        VcImage.ccReloadImage(cmCamera.get());
      }//..?
    }//+++
  };//***
  
  public static final EiTriggerable O_INDEX_DECRECING = new EiTriggerable() {
    @Override public void ccTrigger() {
      VcCamera.ccShiftCameraIndex(-1);
    }//+++
  };//***
  
  public static final EiTriggerable O_INDEX_INCRECING = new EiTriggerable() {
    @Override public void ccTrigger() {
      VcCamera.ccShiftCameraIndex(1);
    }//+++
  };//***
  
  private static void ssRefreshPresent(){
    if(ccHasAvailables()){
      cmPresent=String.format("[%d]%s", cmIndex, O_CAM_DICT.get(cmIndex));
    }else{
      cmPresent="[no_camera_available]";
    }//..?
  }//+++
  
  public static String ccGetPresent(){
    return cmPresent;
  }//+++
  
}//***
