package googleweather;

import googleweather.threads.GeocodeXMLThread;
import googleweather.threads.WeatherXMLThread;

import java.util.Date;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.xml.XMLElement;

/**
 * Processing library for the Google Weather API
 * 
 * @author: Marcel Schwittlick
 * @email: marzzzel@gmail.com
 * @date: 28.03.2012
 */
public class GoogleWeather {
	private XMLElement weatherXML;
	private XMLElement geocodeXML;
	private PApplet parentApplet;
	private String googleWeatherLink;
	private String googleGeocodeLink;
	@SuppressWarnings("unused")
	private int updateIntervallInSeconds;

	private WeatherXMLThread weatherThread;
	private GeocodeXMLThread geocodeThread;
	private XMLElement temp;

	public String[] generalWeathers = { "Clear", "Cloudy", "Fog", "Haze",
			"Light rain", "Mostly Cloudy", "Overcast", "Partly Cloudy", "Rain",
			"Rain Showers", "Showers", "Thunderstorm", "Chance of Showers",
			"Chance of Snow", "Chance of Storm", "Chance of Rain",
			"Mostly Sunny", "Partly Sunny", "Scattered Showers", "Sunny",
			"Snow Showers", "Light snow", "Snow" };

	/**
	 * If your internet connection went down, or googles service is inconsistent
	 * the methods will either return 999 or »N/A« depending on if the return
	 * value of the method is a int/float or a String. For further information
	 * check the JavaDoc, the source code or the example inside the library
	 * package!
	 * 
	 * @param p
	 *            processings PApplet
	 * @param cityName
	 *            String containing the name of the city you want the forecast
	 *            for
	 * @param updateIntervallInMinutes
	 *            selfexplanatory
	 */
	public GoogleWeather(PApplet p, String cityName,
			int updateIntervallInSeconds) {
		this.parentApplet = p;
		this.googleWeatherLink = "http://www.google.com/ig/api?weather="
				+ cityName.replaceAll("\\s+", "%20");
		this.googleGeocodeLink = "http://maps.googleapis.com/maps/api/geocode/xml?address="
				+ cityName.replaceAll("\\s+", "%20") + "&sensor=true";
		this.updateIntervallInSeconds = updateIntervallInSeconds;
		weatherThread = new WeatherXMLThread(updateIntervallInSeconds,
				parentApplet, googleWeatherLink);
		geocodeThread = new GeocodeXMLThread(updateIntervallInSeconds,
				parentApplet, googleGeocodeLink);

		geocodeThread.start();
		weatherThread.start();

		update(); // init
		// this call is limited to 2500 times per day (don't overdo it)
		geocodeXML = geocodeThread.getMainXML();
	}

	/**
	 * updates the forecast
	 */
	public void update() {
		weatherXML = weatherThread.getMainXML();
	}

	/**
	 * sets the cityname
	 * 
	 * @param cityName
	 *            cityname
	 */
	public void setCityName(String cityName) {

		this.googleWeatherLink = "http://www.google.com/ig/api?weather="
				+ cityName.replaceAll("\\s+", "%20");
		this.googleGeocodeLink = "http://maps.googleapis.com/maps/api/geocode/xml?address="
				+ cityName.replaceAll("\\s+", "%20") + "&sensor=true";
		weatherThread.setGoogleWeatherLink(googleWeatherLink);
		geocodeThread.setGoogleGeocodeLink(googleGeocodeLink);

		// this call is limited to 2500 times per day (don't overdo it)
		geocodeXML = geocodeThread.getMainXML();
		update();
	}

	/**
	 * @para intervall updateintervall in minutes
	 */
	public void setUpdateIntervallInSeconds(int intervall) {
		updateIntervallInSeconds = intervall;
	}

	/**
	 * Prints all possible weathers to the console. Please message me if you
	 * found more. Having a list of all possible weather makes it possible to
	 * replace the original google pictures with custom icons.
	 */
	public void printAllPossibleWeathers() {
		for (String a : generalWeathers) {
			PApplet.println(a);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// The following methods provide you the most current weather information //
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return Date of the last time the weather was updated
	 */
	public Date getLastUpdated() {
		return weatherThread.getLastUpdate();
	}

	/**
	 * @return temperature in celsius as integer
	 */
	public int getTemperatureInCelsius() throws ArrayIndexOutOfBoundsException {
		int temperatureInCelsius;
		try {
			temp = weatherXML.getChild("weather/current_conditions/temp_c");
			temperatureInCelsius = temp.getInt("data");

		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Temperature in celsius unavailable.");
			temperatureInCelsius = 999;
		}
		return temperatureInCelsius;
	}

	/**
	 * @return temperature in fahrenheit as integer
	 */
	public int getTemperatureInFahrenheit()
			throws ArrayIndexOutOfBoundsException {
		int temperatureInFahrenheit;
		try {
			temp = weatherXML.getChild("weather/current_conditions/temp_f");
			temperatureInFahrenheit = temp.getInt("data");

		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Temperature in fahrenheit unavailable.");
			temperatureInFahrenheit = 999;
		}
		return temperatureInFahrenheit;
	}

	/**
	 * @return weathreInGeneral weather in general right now
	 */
	public String getWeatherInGeneral() throws ArrayIndexOutOfBoundsException {
		String weathreInGeneral;
		try {
			temp = weatherXML.getChild("weather/current_conditions/condition");
			weathreInGeneral = temp.getString("data");
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Temperature in fahrenheit unavailable.");
			weathreInGeneral = new String("N/A");
		}
		return weathreInGeneral;
	}

	/**
	 * @return humidity in percent as integer
	 */
	public int getHumidityInPercent() throws ArrayIndexOutOfBoundsException {
		int humidityInPercent;
		try {
			temp = weatherXML.getChild("weather/current_conditions/humidity");
			String tempString = temp.getString("data");
			StringBuffer sb = new StringBuffer(2);
			tempString = sb.append(tempString.charAt(tempString.length() - 3))
					.append(tempString.charAt(tempString.length() - 2))
					.toString();
			tempString = PApplet.trim(tempString);
			humidityInPercent = Integer.parseInt(tempString);

		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Humidity unavailable.");
			humidityInPercent = 999;
		}
		return humidityInPercent;
	}

	/**
	 * @return windspeed in kilometers per hour as integer
	 */
	public float getWindSpeedInKMH() throws ArrayIndexOutOfBoundsException {
		float windSpeedInKMH;
		try {
			windSpeedInKMH = (float) (getWindSpeedInMPH() * 1.609344); // mph->km/h
		} catch (ArrayIndexOutOfBoundsException ae) {
			PApplet.println("Windspeed in KMH unavailable.");
			windSpeedInKMH = 999;
		}
		return windSpeedInKMH;
	}

	/**
	 * @return windspeed in miles per hour as integer
	 */
	public int getWindSpeedInMPH() throws ArrayIndexOutOfBoundsException {
		int windSpeedInMPH;
		try {
			temp = weatherXML
					.getChild("weather/current_conditions/wind_condition");
			String tempString = temp.getString("data");
			StringBuffer sb = new StringBuffer(2);
			tempString = sb.append(tempString.charAt(tempString.length() - 6))
					.append(tempString.charAt(tempString.length() - 5))
					.toString();
			tempString = PApplet.trim(tempString);
			windSpeedInMPH = Integer.parseInt(tempString);
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Windspeed in MPH unavailable.");
			windSpeedInMPH = 999;
		}
		return windSpeedInMPH;
	}

	/**
	 * 
	 * @return winddirection as String (S, SE, E, NE, N, NW, W, SW)
	 */
	public String getWindDirectionString()
			throws ArrayIndexOutOfBoundsException {
		String windDirection;
		try {
			temp = weatherXML
					.getChild("weather/current_conditions/wind_condition");
			windDirection = temp.getString("data");
			StringBuffer sb = new StringBuffer(2);
			windDirection = sb.append(windDirection.charAt(6))
					.append(windDirection.charAt(7)).toString();
			windDirection = PApplet.trim(windDirection);
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Winddirection as string unavailable");
			return "N/A";
		}
		return windDirection;
	}

	/**
	 * winddirection in degree. (East = 0°; NE = 45°; N = 90° etc.)
	 * 
	 * @return winddirection in degree
	 */
	public int getWindDirectionDegree() throws ArrayIndexOutOfBoundsException {
		int windDirectionInDegree = 0;
		try {
			String windDirectionToday = getWindDirectionString();
			if (windDirectionToday.equals("E")) {
			} else if (windDirectionToday.equals("NE")) {
				windDirectionInDegree = 45;
			} else if (windDirectionToday.equals("N")) {
				windDirectionInDegree = 90;
			} else if (windDirectionToday.equals("NW")) {
				windDirectionInDegree = 135;
			} else if (windDirectionToday.equals("W")) {
				windDirectionInDegree = 180;
			} else if (windDirectionToday.equals("SW")) {
				windDirectionInDegree = 225;
			} else if (windDirectionToday.equals("S")) {
				windDirectionInDegree = 270;
			} else if (windDirectionToday.equals("SE")) {
				windDirectionInDegree = 315;
			}
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Winddirection in degre unavailable");
			windDirectionInDegree = 999;
		}
		return windDirectionInDegree;
	}

	/**
	 * the wind direction as normalized vector
	 * 
	 * @return normalized PVector
	 */
	public PVector getWindDirectionNormalizedVector()
			throws ArrayIndexOutOfBoundsException {
		PVector windDirectionNormalized = new PVector(0, 0);
		try {
			int windDirectionDegree = getWindDirectionDegree();
			if (windDirectionDegree == 0) {
				windDirectionNormalized = new PVector(1, 0);
			} else if (windDirectionDegree == 45) {
				windDirectionNormalized = new PVector(1, -1);
			} else if (windDirectionDegree == 90) {
				windDirectionNormalized = new PVector(0, -1);
			} else if (windDirectionDegree == 135) {
				windDirectionNormalized = new PVector(-1, -1);
			} else if (windDirectionDegree == 180) {
				windDirectionNormalized = new PVector(-1, 0);
			} else if (windDirectionDegree == 225) {
				windDirectionNormalized = new PVector(-1, 1);
			} else if (windDirectionDegree == 270) {
				windDirectionNormalized = new PVector(0, 1);
			} else if (windDirectionDegree == 315) {
				windDirectionNormalized = new PVector(1, 1);
			}
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Winddirection as normalized vector unavailable");
			windDirectionNormalized = new PVector(0, 0);
		}
		return windDirectionNormalized;
	}

	// //////////////////////////////////////////////////////////////
	// the following methods provide you with forecast information //
	// //////////////////////////////////////////////////////////////

	/**
	 * @param daysFromToday
	 *            max 3
	 * 
	 * @return weekdayshortname as String (Mon, Tue, Wed, Thu, Fri, Sat, Sun)
	 */
	public String getWeekdayInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		String weekdayToday;
		try {
			XMLElement lower = weatherXML.getChild(0);
			temp = lower.getChild(2 + daysFromToday);
			XMLElement tmp = temp.getChild(0);
			weekdayToday = tmp.getString("data");
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("Weekday in " + daysFromToday + " days unavailable");
			weekdayToday = new String("N/A");
		}
		return weekdayToday;
	}

	/**
	 * @param daysFromToday
	 *            max 3
	 * 
	 * @return general weather impression as string
	 * 
	 * @see generalWeathers
	 */
	public String getWeatherInGeneralInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		String weatherInGeneral;
		try {
			XMLElement tmp;
			XMLElement lower = weatherXML.getChild(0);
			if (daysFromToday == 0) {
				temp = lower.getChild(1);
				tmp = temp.getChild(0);
			} else {
				temp = lower.getChild(2 + daysFromToday);
				tmp = temp.getChild(4);
			}
			weatherInGeneral = tmp.getString("data");
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("weather in general in " + daysFromToday
					+ " days unavailable");
			weatherInGeneral = new String("N/A");
		}
		return weatherInGeneral;
	}

	/**
	 * @param daysFromToday
	 *            max 3
	 * 
	 * @return minimum temperature in fahrenheit as integer
	 */
	public int getMinTemperatureInFahrenheitInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		int minTemperatureinFahrenheit;
		try {
			XMLElement lower = weatherXML.getChild(0);
			temp = lower.getChild(2 + daysFromToday);
			XMLElement tmp = temp.getChild(1);
			minTemperatureinFahrenheit = tmp.getInt("data");
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("minimum temperature in fahrenheit in"
					+ daysFromToday + " days unavailable");
			minTemperatureinFahrenheit = 999;
		}
		return minTemperatureinFahrenheit;
	}

	/**
	 * @param daysFromToday
	 *            max 3
	 * 
	 * @return maximum temperature in fahrenheit as integer
	 */
	public int getMaxTemperatureInFahrenheitInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		int maxTemperatureinFahrenheit;
		try {

			XMLElement lower = weatherXML.getChild(0);
			temp = lower.getChild(2 + daysFromToday);
			XMLElement tmp = temp.getChild(2);
			maxTemperatureinFahrenheit = tmp.getInt("data");

		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("maximum temperature in fahrenheit in"
					+ daysFromToday + " days unavailable");
			maxTemperatureinFahrenheit = 999;
		}
		return maxTemperatureinFahrenheit;
	}

	/**
	 * http://www.usa-reisen-giese.de/umrech1.htm
	 * 
	 * @param daysFromToday
	 *            max 3
	 * @return minimum temperature in celsius as integer
	 */
	public float getMinTemperatureInCelsiusInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		try {
			return (getMinTemperatureInFahrenheitInXDays(daysFromToday) - 32) * 5 / 9;
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("minimum temperature in celsius in" + daysFromToday
					+ " days unavailable");
			return 999;
		}
	}

	/**
	 * http://www.usa-reisen-giese.de/umrech1.htm
	 * 
	 * @param daysFromToday
	 *            max 3
	 * 
	 * @return maximum temperature in celsius as integer
	 */
	public float getMaxTemperatureInCelsiusInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		try {
			return (getMaxTemperatureInFahrenheitInXDays(daysFromToday) - 32) * 5 / 9;
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("maximum temperature in celsius in" + daysFromToday
					+ " days unavailable");
			return 999;
		}
	}

	/**
	 * @param daysFromToday
	 *            max 3
	 * 
	 * @return average temperature in fahrenheit as float
	 */
	public int getAverageTemperatureInFahrenheitInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		int averageTemperatureInFahrenheitInXDays;
		try {
			averageTemperatureInFahrenheitInXDays = (getMinTemperatureInFahrenheitInXDays(daysFromToday) + getMaxTemperatureInFahrenheitInXDays(daysFromToday)) / 2;
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("average temperature in fahrenheit in"
					+ daysFromToday + " days unavailable");
			averageTemperatureInFahrenheitInXDays = 999;
		}
		return averageTemperatureInFahrenheitInXDays;
	}

	/**
	 * @param daysFromToday
	 *            max 3
	 * 
	 * @return average temperature in celsius as float
	 */
	public float getAverageTemperatureInCelsiusInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		float averageTemperatureInCelsiusInXDays;
		try {
			averageTemperatureInCelsiusInXDays = (getAverageTemperatureInFahrenheitInXDays(daysFromToday) - 32) * 5 / 9;
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("average temperature in celsius in" + daysFromToday
					+ " days unavailable");
			averageTemperatureInCelsiusInXDays = 999;
		}
		return averageTemperatureInCelsiusInXDays;
	}

	/**
	 * @param daysFromToday
	 *            max 3 return weaterimage as PImage
	 */
	public PImage getWeatherImageInXDays(int daysFromToday)
			throws ArrayIndexOutOfBoundsException {
		try {
			XMLElement tmp;
			PImage weatherImageToday;
			XMLElement lower = weatherXML.getChild(0);
			if (daysFromToday == 0) {
				temp = lower.getChild(1);
				tmp = temp.getChild(4);
			} else {
				temp = lower.getChild(2 + daysFromToday);
				tmp = temp.getChild(3);
			}
			weatherImageToday = parentApplet.loadImage("http://www.google.com/"
					+ tmp.getString("data"));
			return weatherImageToday;
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println("weatherimage in" + daysFromToday
					+ " days unavailable");
			return new PImage(80, 80);
		}
	}

	/**
	 * gives you the longitude value for your selected city (google geocode api)
	 * 
	 * @return
	 * @throws
	 */
	public float getLongitude() throws ArrayIndexOutOfBoundsException {
		float longitude = 0.0f;
		try {
			XMLElement status = geocodeXML.getChild(0);
			if (status.getContent().equals("OK")) {
				XMLElement tmp = geocodeXML
						.getChild("result/geometry/location/lng");
				longitude = Float.parseFloat(tmp.getContent());
			} else {
				throw new ArrayIndexOutOfBoundsException(status.getContent());
			}
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println(a
					+ " check https://developers.google.com/maps/documentation/geocoding/ under topic 'Status Codes'");
		}
		return longitude;
	}

	/**
	 * gives you the latitude value for your selected city (google geocode api)
	 * 
	 * @return
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public float getLatitude() throws ArrayIndexOutOfBoundsException {
		float latitude = 0.0f;
		try {
			XMLElement status = geocodeXML.getChild(0);
			if (status.getContent().equals("OK")) {
				XMLElement tmp = geocodeXML
						.getChild("result/geometry/location/lat");
				latitude = Float.parseFloat(tmp.getContent());
			} else {
				throw new ArrayIndexOutOfBoundsException(status.getContent());
			}
		} catch (ArrayIndexOutOfBoundsException a) {
			PApplet.println(a
					+ " check https://developers.google.com/maps/documentation/geocoding/ under topic 'Status Codes'");
		}
		return latitude;
	}

}