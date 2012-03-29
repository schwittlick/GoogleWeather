package googleweather.threads;

import java.util.Date;

import processing.core.PApplet;
import processing.xml.XMLElement;

/**
 * Java thread for loading googles geocode data concurrently.
 * 
 * @author Marcel Schwittlick
 * @date 27.03.2012
 * 
 */
public class GeocodeXMLThread extends Thread {

	private int updateIntervallMilis;
	private boolean running;
	private PApplet parentApplet;
	private String googleGeocodeLink;
	private Date lastUpdate;

	XMLElement mainXML;

	public GeocodeXMLThread(int updateIntervallSeconds, PApplet parent,
			String googleGeocodeLink) {
		this.updateIntervallMilis = updateIntervallSeconds * 1000;
		this.parentApplet = parent;
		this.googleGeocodeLink = googleGeocodeLink;
		running = false;
	}

	public void start() {
		running = true;
		PApplet.println("Geocode updated every " + updateIntervallMilis / 1000
				+ "s.");
		setMainXML();
		super.start();
	}

	public void run() {
		while (running) {
			setMainXML();
			try {
				sleep(updateIntervallMilis);
			} catch (Exception e) {
				PApplet.println(e);
			}
		}
	}

	public void quit() {
		PApplet.println("Thread quit.");
		running = false;
		interrupt();
	}

	/**
	 * @return mainXML the main xml file from google
	 */
	public XMLElement getMainXML() {
		return mainXML;
	}

	/**
	 * 
	 * @param googleWeatherLink
	 *            link to xml file
	 */
	public void setGoogleGeocodeLink(String googleGeocodeLink) {
		this.googleGeocodeLink = googleGeocodeLink;
		setMainXML();
	}

	/**
	 * 
	 * @param intervall
	 */
	public void setUpdateIntervallInSeconds(int intervall) {
		updateIntervallMilis = intervall * 1000;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * downloads the main xml file
	 */
	private void setMainXML() throws NullPointerException {
		try {
			mainXML = new XMLElement(parentApplet, googleGeocodeLink);
		} catch (NullPointerException n) {
			PApplet.println(n + " not available");
		}
		lastUpdate = new Date();
	}
}
