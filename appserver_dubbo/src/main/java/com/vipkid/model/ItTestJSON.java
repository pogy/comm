package com.vipkid.model;


public class ItTestJSON extends Base {
	
	private static final long serialVersionUID = 1L;
	
	// 操作系统信息
	private ItTestInfo system;
	
	// 浏览器信息
	private ItTestInfo browser;
	
	// flash版本
	private ItTestInfo flash;
	
	// 网络连接信息
	private ItTestInfo connect;
	
	// 网络延迟信息
	private ItTestInfo delay;
	
	// 带宽延迟
	private ItTestInfo bandWidth;
	
	// 音频播放信息
	private ItTestInfo sound;
	
	// 麦克风信息
	private ItTestInfo mic;
	
	// 摄像头信息
	private ItTestInfo camera;

	public ItTestInfo getSystem() {
		return system;
	}

	public void setSystem(ItTestInfo system) {
		this.system = system;
	}

	public ItTestInfo getBrowser() {
		return browser;
	}

	public void setBrowser(ItTestInfo browser) {
		this.browser = browser;
	}

	public ItTestInfo getFlash() {
		return flash;
	}

	public void setFlash(ItTestInfo flash) {
		this.flash = flash;
	}

	public ItTestInfo getConnect() {
		return connect;
	}

	public void setConnect(ItTestInfo connect) {
		this.connect = connect;
	}

	public ItTestInfo getDelay() {
		return delay;
	}

	public void setDelay(ItTestInfo delay) {
		this.delay = delay;
	}

	public ItTestInfo getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(ItTestInfo bandWidth) {
		this.bandWidth = bandWidth;
	}

	public ItTestInfo getSound() {
		return sound;
	}

	public void setSound(ItTestInfo sound) {
		this.sound = sound;
	}

	public ItTestInfo getMic() {
		return mic;
	}

	public void setMic(ItTestInfo mic) {
		this.mic = mic;
	}

	public ItTestInfo getCamera() {
		return camera;
	}

	public void setCamera(ItTestInfo camera) {
		this.camera = camera;
	}

}
