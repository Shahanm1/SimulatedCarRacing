package hg.scr.tools;

import scr.Controller;

public class ScrOptions {
	
	private String controlerName;
	private Controller controler;
	private String[] parms;

	public String getControlerName() {
		return controlerName;
	}

	public void setControlerName(String controlerName) {
		this.controlerName = controlerName;
	}

	public String[] getParms() {
		return parms;
	}

	public void setParms(String[] parms) {
		this.parms = parms;
	}

	public Controller getControler() {
		return controler;
	}

	public void setControler(Controller controler) {
		this.controler = controler;
	}
	
	
	
	

}
