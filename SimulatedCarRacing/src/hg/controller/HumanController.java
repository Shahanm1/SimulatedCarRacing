/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hg.controller;

import scr.Action;
import scr.Controller;
import scr.SensorModel;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 
 * @author Jan Quadflieg
 */
public class HumanController extends Controller implements
KeyListener {

//	public KeyBoardInput keyBoardInput;

	double acc = 0.1;

	public HumanController() {
//		this.keyBoardInput = new KeyBoardInput();
	}

	public Action control(SensorModel m) {
		Action action = new Action();

		// if(keyBoardInput.getKey() == KeyEvent.VK_UP){
		// action.accelerate = 0.1;
		// }
		//
		// if(keyBoardInput.getKey() == KeyEvent.VK_DOWN){
		// action.accelerate = 0;
		// }
		action.accelerate = acc;

		return action;

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}



	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("sss");
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		
			KeyEvent keyEvent = (KeyEvent) event;
			int keyid = keyEvent.getKeyCode();
			System.out.println(keyid);
			if (keyid == KeyEvent.VK_UP) {
				acc = 0.1;
			}

			if (keyid == KeyEvent.VK_DOWN) {
				acc = 0;
			}
		
	}
}
