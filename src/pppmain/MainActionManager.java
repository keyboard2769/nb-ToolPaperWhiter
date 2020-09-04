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

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;
import kosui.ppplocalui.EiTriggerable;
import kosui.pppmodel.McConst;
import kosui.pppswingui.ScConst;
import kosui.ppputil.VcConst;
import kosui.ppputil.VcLocalCoordinator;
import kosui.ppputil.VcNumericUtility;
import processing.core.PApplet;

public final class MainActionManager {
  
  private static final MainActionManager SELF = new MainActionManager();
  public static final MainActionManager ccRefer(){return SELF;}//++>
  private MainActionManager(){}//++!
  
  //===
  
  public final WindowAdapter cmClosing = new WindowAdapter() {
    @Override public void windowClosing(WindowEvent we) {
      
      //-- flush
      VcCamera.O_CAMERA_CLOSING.ccTrigger();
      
      //-- exit
      VcConst.ccPrintln(".cmQuitting $ call PApplet::exit");
      MainSketch.ccGetPApplet().exit();
      
    }//+++
  };//***
  
  public final EiTriggerable cmQuitting = new EiTriggerable() {
    @Override public void ccTrigger() {
      cmClosing.windowClosing(null);
    }//+++
  };//***
  
  public final EiTriggerable cmImageReloading = new EiTriggerable() {
    @Override public void ccTrigger() {
      VcImage.ccReloadImage();
    }//+++
  };//***
  
  public final EiTriggerable cmLetImporting = new EiTriggerable() {
    @Override public void ccTrigger() {
      File lpFile = ScConst.ccGetFileByFileChooser('f');
      if(lpFile==null){return;}
      boolean lpVerified = McConst.ccVerifyFileForLoading(lpFile, "png");
      if(!lpVerified){
        ScConst.ccErrorBox("failed to open file");
        return;
      }//..?
      VcImage.ccSetFileLocation(lpFile.getAbsolutePath());
      VcLocalCoordinator.ccInvokeLater(cmImageReloading);
    }//+++
  };//***
  
  public final EiTriggerable cmLetModeSwitching = new EiTriggerable() {
    @Override public void ccTrigger() {
      boolean lpMode = MainWindow.O_PAPERWHITE_SS.isSelected();
      VcImage.ccSetPaperWhiteModeApplied(lpMode);
    }//+++
  };//***
  
  public final EiTriggerable cmSoftening = new EiTriggerable() {
    @Override public void ccTrigger() {
      VcImage.ccShiftThresholdFactor(-0.05f);
      SwingUtilities.invokeLater(MainWindow.O_THRESHOLD_REFRESH);
    }//+++
  };//***
  
  public final EiTriggerable cmLetSoftening = new EiTriggerable() {
    @Override public void ccTrigger(){
      VcLocalCoordinator.ccInvokeLater(cmSoftening);
    }//+++
  };//***
  
  public final EiTriggerable cmHardening = new EiTriggerable() {
    @Override public void ccTrigger() {
      VcImage.ccShiftThresholdFactor(0.05f);
      SwingUtilities.invokeLater(MainWindow.O_THRESHOLD_REFRESH);
    }//+++
  };//***
  
  public final EiTriggerable cmLetHardening = new EiTriggerable() {
    @Override public void ccTrigger(){
      VcLocalCoordinator.ccInvokeLater(cmHardening);
    }//+++
  };//***
  
  public final EiTriggerable cmThresholdDirectRefreshing = new EiTriggerable(){
    @Override public void ccTrigger() {
      VcImage.ccSetThresholdFactor(MainWindow.vmThresholdDirect);
      SwingUtilities.invokeLater(MainWindow.O_THRESHOLD_REFRESH);
    }//+++
  };//***
  
  public final EiTriggerable cmLetThresholdInputting = new EiTriggerable() {
    @Override public void ccTrigger() {
      String lpInputted = ScConst.ccGetStringByInputBox(
        "input directly (0.01 ~ 0.99)",
        VcNumericUtility.ccFormatPointTwoFloat(VcImage.ccGetThresholdFactor())
      );
      MainWindow.vmThresholdDirect
       = VcNumericUtility.ccParseFloatString(lpInputted);
      MainWindow.vmThresholdDirect
       = PApplet.constrain(MainWindow.vmThresholdDirect, 0.01f, 0.99f);
      VcLocalCoordinator.ccInvokeLater(cmThresholdDirectRefreshing);
    }//+++
  };//***
  
  public final EiTriggerable cmExporting = new EiTriggerable() {
    @Override public void ccTrigger() {
      VcImage.ccExportImage(MainWindow.vmExportLocation);
    }//+++
  };//***
  
  public final EiTriggerable cmLetExporting = new EiTriggerable() {
    @Override public void ccTrigger() {
      ScConst.ccSetFileChooserButtonText("Save");
      File lpFile = ScConst.ccGetFileByFileChooser('f');
      if(lpFile==null){return;}
      boolean lpToSave=McConst.ccVerifyFileForSaving(lpFile);
      if(!lpToSave){return;}
      MainWindow.vmExportLocation = lpFile.getAbsolutePath();
      VcLocalCoordinator.ccInvokeLater(cmExporting);
    }//+++
  };//***
  
  public final EiTriggerable cmLetCameraSwitching = new EiTriggerable() {
    @Override public void ccTrigger() {
      if(MainWindow.O_CAMERA_SS.isSelected()){
        VcLocalCoordinator.ccInvokeLater(VcCamera.O_CAMERA_OPENING);
      }else{
        VcLocalCoordinator.ccInvokeLater(VcCamera.O_CAMERA_CLOSING);
      }//..?
    }//+++
  };//***
  
  public final EiTriggerable cmLetCameraCapturing = new EiTriggerable() {
    @Override public void ccTrigger() {
      VcLocalCoordinator.ccInvokeLater(VcCamera.O_CAMERA_CAPTURING);
    }//+++
  };//***
  
  public final Runnable cmColorChoosing = new Runnable() {
    @Override public void run() {
      Color lpColor = JColorChooser.showDialog(
        MainSketch.ccGetPApplet().frame,
        "Filtered Color",
        Color.BLACK
      );
      if(lpColor==null){return;}
      MainWindow.O_COLOR_SW.setBackground(lpColor);
      int lpColorCode = lpColor.getRGB();
      VcImage.ccSetFilteredColor(lpColorCode);
    }//+++
  };//***
  
  public final EiTriggerable cmColorChooseRunning = new EiTriggerable() {
    @Override public void ccTrigger() {
      SwingUtilities.invokeLater(cmColorChoosing);
    }//+++
  };//***
  
}//***
