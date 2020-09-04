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
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import kosui.pppmodel.McConst;
import kosui.pppswingui.ScConst;
import kosui.pppswingui.ScFactory;
import kosui.pppswingui.ScTitledWindow;
import kosui.ppputil.VcSwingCoordinator;
import kosui.ppputil.VcTranslator;
import processing.core.PApplet;

public final class MainWindow {
  
  private static final MainWindow SELF = new MainWindow();
  public static final MainWindow ccRefer(){return SELF;}//++>
  private MainWindow(){}//++!
  
  //===
  
  public static final ScTitledWindow O_WINDOW
   = new ScTitledWindow();
  
  public static final JButton O_QUIT_SW
   = ScFactory.ccCreateCommandButton("Quit");
  
  public static final JButton O_IMPORT_SW
   = ScFactory.ccCreateCommandButton("Import");
  
  public static final JToggleButton O_CAMERA_SS
   = ScFactory.ccCreateCommandToggler("Camera");
  
  public static final JButton O_CAPTURE_SW
   = ScFactory.ccCreateCommandButton("Capture");
  
  public static final JButton O_SOFTER_SW
   = ScFactory.ccCreateCommandButton("[-]");
  
  public static final JTextField O_THRESHOLD_TB
    = ScFactory.ccCreateValueBox(" 00 % ",48,31);
  
  public static final JButton O_HARDER_SW
   = ScFactory.ccCreateCommandButton("[+]");
  
  public static final JToggleButton O_PAPERWHITE_SS
   = ScFactory.ccCreateCommandToggler("Paperwhite");
  
  public static final JButton O_COLOR_SW
   = ScFactory.ccCreateCommandButton("*");
  
  public static final JButton O_EXPORT_SW
   = ScFactory.ccCreateCommandButton("Export");
  
  private static volatile int vmLocationX = 100;
  
  private static volatile int vmLocationY = 100;
  
  public static volatile float vmThresholdDirect = 0f;
  
  public static volatile String vmExportLocation = "";
  
  //===
  
  public static final Runnable O_SETUP = new Runnable() {
    @Override public void run() {
      
      //-- config ** mode
      O_PAPERWHITE_SS.setSelected(true);
      
      //-- config ** color switch
      O_COLOR_SW.setBackground(Color.BLACK);
      O_COLOR_SW.setToolTipText("choose filtered color");
      
      //-- config ** box
      O_THRESHOLD_TB.setBackground(ScConst.C_LIT_GRAY);
      O_THRESHOLD_TB.setDisabledTextColor(ScConst.C_DIM_BLUE);
      
      //-- adjust pane
      JPanel lpAdjustJPanel = ScFactory.ccCreateFlowPanel(1, false);
      lpAdjustJPanel.add(O_SOFTER_SW);
      lpAdjustJPanel.add(O_THRESHOLD_TB);
      lpAdjustJPanel.add(O_HARDER_SW);
      
      //-- center
      JPanel lpButtonPane = ScFactory.ccCreateGridPanel(10, 1);
      lpButtonPane.add(O_IMPORT_SW);
      lpButtonPane.add(new JSeparator(SwingConstants.HORIZONTAL));
      lpButtonPane.add(O_CAMERA_SS);
      lpButtonPane.add(O_CAPTURE_SW);
      lpButtonPane.add(new JSeparator(SwingConstants.HORIZONTAL));
      lpButtonPane.add(O_PAPERWHITE_SS);
      lpButtonPane.add(O_COLOR_SW);
      lpButtonPane.add(lpAdjustJPanel);
      lpButtonPane.add(new JSeparator(SwingConstants.HORIZONTAL));
      lpButtonPane.add(O_EXPORT_SW);
      
      //-- pack
      O_WINDOW.ccInit("[+]Operate", ScConst.C_DARK_GRAY);
      O_WINDOW.ccAddCenter(lpButtonPane);
      O_WINDOW.ccAddPageEnd(O_QUIT_SW);
      O_WINDOW.ccFinish(false,vmLocationX,vmLocationY);
      
      //-- sytle
      ScConst.ccApplyLookAndFeel(3, false);
      
      //-- bind
      VcSwingCoordinator.ccRegisterAction
        (O_QUIT_SW, MainActionManager.ccRefer().cmQuitting);
      VcSwingCoordinator.ccRegisterAction
        (O_IMPORT_SW, MainActionManager.ccRefer().cmLetImporting);
      VcSwingCoordinator.ccRegisterAction
        (O_EXPORT_SW, MainActionManager.ccRefer().cmLetExporting);
      VcSwingCoordinator.ccRegisterAction
        (O_PAPERWHITE_SS, MainActionManager.ccRefer().cmLetModeSwitching);
      VcSwingCoordinator.ccRegisterAction
        (O_CAMERA_SS, MainActionManager.ccRefer().cmLetCameraSwitching);
      VcSwingCoordinator.ccRegisterAction
        (O_SOFTER_SW, MainActionManager.ccRefer().cmLetSoftening);
      VcSwingCoordinator.ccRegisterAction
        (O_HARDER_SW, MainActionManager.ccRefer().cmLetHardening);
      VcSwingCoordinator.ccRegisterPressing
        (O_THRESHOLD_TB, MainActionManager.ccRefer().cmLetThresholdInputting);
      VcSwingCoordinator.ccRegisterAction
        (O_COLOR_SW, MainActionManager.ccRefer().cmColorChooseRunning);
      VcSwingCoordinator.ccRegisterAction
        (O_CAPTURE_SW, MainActionManager.ccRefer().cmLetCameraCapturing);
      
      //-- post
      O_THRESHOLD_REFRESH.run();
      VcTranslator.ccGetInstance().ccInit();
      VcTranslator.ccGetInstance().ccRegisterEnglishWord(
        McConst.C_KEY_OVERWRITE_COMFIRMATION,
        "overwrite?"
      );
      VcTranslator.ccGetInstance().ccSetMode('e');
      
    }//+++
  };//***
  
  public static final Runnable O_THRESHOLD_REFRESH = new Runnable() {
    @Override public void run() {
      MainWindow.O_THRESHOLD_TB.setText(String.format(
        " %2d %%", 
        PApplet.ceil(VcImage.ccGetThresholdFactor()*100f)
      ));
    }//+++
  };//***
  
  public static final void ccSetLocation(int pxX, int pxY){
    vmLocationX = pxX & 0xFFFF;
    vmLocationY = pxY & 0xFFFF;
  }//+++
  
  public static final Runnable O_RELOCATION = new Runnable() {
    @Override public void run() {
      O_WINDOW.setLocation(vmLocationX, vmLocationY);
      O_WINDOW.ccSetIsVisible();
    }//+++
  };//***
  
}//***eof
